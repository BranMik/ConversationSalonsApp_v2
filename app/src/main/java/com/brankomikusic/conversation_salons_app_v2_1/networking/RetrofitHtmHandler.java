package com.brankomikusic.conversation_salons_app_v2_1.networking;

import android.util.Log;
import com.brankomikusic.conversation_salons_app_v2_1.MainActivity;
import com.brankomikusic.conversation_salons_app_v2_1.backgroundtasks.UpdateArticlesInDbFromBlog;
import com.brankomikusic.conversation_salons_app_v2_1.room_sqlite.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Modified Retrofit class used here to fetch HTML pages from web blog.
 *
 */
public class RetrofitHtmHandler {

    Dispatcher dispatcher;
    OkHttpClient okHttpClient;
    Retrofit retrofit;

    static RetrofitHtmHandler instance;

    private RetrofitHtmHandler(){
        dispatcher = new Dispatcher(Executors.newFixedThreadPool(20));
        dispatcher.setMaxRequests(20);
        dispatcher.setMaxRequestsPerHost(1);
        okHttpClient = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(100,30, TimeUnit.SECONDS))
                .build();
    }

    /**
     * Method to fetch the html of the page containing links.
     * @param url Web adress for html to be fetched
     */
    public static void scrapeLinks(String url){
        Call<ResponseBody> pageCall = prepare(url);
        pageCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    // Method in UpdateArticlesInDbFromBlog is called when the page is fetched.
                    UpdateArticlesInDbFromBlog.scrapeLinksOnResponseCallback(response.body().string());
                } catch (IOException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(MainActivity.LOG_BR_INFO,"FAILURE ERROR IN RETROFIT CALL !!");
            }
        });
    }

    /**
     * Method to fetch the html of the page containing some article content.
     * @param article Article object previously created, contains link to the page as well.
     */
    public static void scrapeArticles(Article article){
        Call<ResponseBody> pageCall = prepare(article.getLink());
        pageCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    // Method in UpdateArticlesInDbFromBlog is called when the page is fetched.
                    UpdateArticlesInDbFromBlog.scrapeArticlesOnResponseCallback(response.body().string(), article);
                    /* Counter in the UpdateArticlesInDbFromBlog class, of the articles being currently processed is decreased
                    immediately as it relates to Retrofit request not parsing of the page. So the lock in the Articles for loop
                    will be released to proceed. */
                    UpdateArticlesInDbFromBlog.decreaseArticlesBeingProcessed();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { // On fail lock is released as well
                Object lock = UpdateArticlesInDbFromBlog.getArticleLock();
                synchronized (lock) {
                    UpdateArticlesInDbFromBlog.decreaseArticlesBeingProcessed();
                    lock.notifyAll();
                }
            }
        });
    }

    private static Call<ResponseBody> prepare(String url){
        if(instance == null) instance = new RetrofitHtmHandler();
        instance.retrofit = new Retrofit.Builder()
                .baseUrl(HttpUrl.parse(url))
                .addConverterFactory(PageAdapter.FACTORY)
                .build();
        PageService requestAddress = instance.retrofit.create(PageService.class);
        return requestAddress.get(HttpUrl.parse(url));
    }

    static class Page {
        String content;

        Page(String content) {
            this.content = content;
        }
    }

    static final class PageAdapter implements Converter<ResponseBody, RetrofitHtmHandler.Page> {
        static final Converter.Factory FACTORY = new Converter.Factory() {
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                if (type == RetrofitHtmHandler.Page.class) return new RetrofitHtmHandler.PageAdapter();
                return null;
            }
        };

        @Override
        public RetrofitHtmHandler.Page convert(ResponseBody responseBody) throws IOException {
            Document document = Jsoup.parse(responseBody.string());
            Element value = document.select("script").get(1);
            String content = value.html();
            return new RetrofitHtmHandler.Page(content);
        }
    }

    interface PageService {
        @GET
        Call<ResponseBody> get(@Url HttpUrl url);
    }
}
