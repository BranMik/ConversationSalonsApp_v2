package com.brankomikusic.conversationsalonsapp.backgroundtasks;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.brankomikusic.conversationsalonsapp.MainActivity;
import com.brankomikusic.conversationsalonsapp.networking.RetrofitHtmHandler;
import com.brankomikusic.conversationsalonsapp.room_sqlite.Article;
import com.brankomikusic.conversationsalonsapp.room_sqlite.ArticlesDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.brankomikusic.conversationsalonsapp.MainActivity.LOG_BR_INFO;

/**
 * Class that is performing scraping action at the start to fetch articles/blogs
 * that are not already stored in application database, and then store them in database.
 *
 * @author Branko Mikusic
 */
public class UpdateArticlesInDbFromBlog {

    private static UpdateArticlesInDbFromBlog instance;
    private Context context;
    private List<Article> listOfArticlesNotInDb;
    private ExecutorService innerExecutor;
    private ArticlesDatabase articlesDatabase;
    private AtomicBoolean finishedScrapingLinks;
    private AtomicInteger articlesBeingProcessed;
    private Object linksLock;
    private Object articlesLock;
    public static boolean articles_reinitialize_flag;

    /**
     * Constructor initializes the class variables.
     *
     * @param ctx Context reference, needed for accessing local database.
     */
    private UpdateArticlesInDbFromBlog(Context ctx) {
        context = ctx;
        listOfArticlesNotInDb = new ArrayList<>();
        innerExecutor = Executors.newFixedThreadPool(5);

        articlesDatabase = ArticlesDatabase.getAppDatabase(context);
        finishedScrapingLinks = new AtomicBoolean(false);
        articlesBeingProcessed = new AtomicInteger(0);
        linksLock = new Object();
        articlesLock = new Object();
    }

    /**
     * First point of entrance to the scraping sequence.
     * @param context
     */
    public static void initiateArticleUpdateFromBlogToLocalDb(Context context){
        final ExecutorService outer = Executors.newSingleThreadExecutor();

        // Threading is nested so that blocking action of article-fetching thread (inner) is done inside another
        // thread (outer) and does not block UI thread.
        articles_reinitialize_flag = false;
        outer.execute(()->{
            try {
                /* If there are some articles stored already in database, articles list that is source for ArticlesRecyclerList is being
                populated before update so existing articles are immediately visible to the user.*/
                if(ArticlesDatabase.getAppDatabase(context).articleDao().countArticles() > 0)
                    Article.initArticleList(context);

                /* Instance is created through which scraping job will be performed in separate threads. Outer thread is neccesary here so
                the blocking of the scraping job will not occur on main thread, also initArticleList method fetches data from database so should
                not block main thread. The sequence here will not continue until this startScraping method will finish.*/
                instance = new UpdateArticlesInDbFromBlog(context);
                instance.mainScrapingJob(); //instance of the class is created end then start method called

                /* Only one RecyclerView source update, either before scraping if db was not empty (some scraping done on previous
                   app starts), or here after scraping if db was empty before. RecyclerView didn't like its source data invalidated
                   in the middle of interaction. Tried to resolve it with notify, with no success.*/
                ////Log.d(LOG_BR_INFO,"AT THE SECOND ARTICLES LIST POINT !!!");
                if(Article.ITEMS.size() == 0)
                    Article.initArticleList(context);
                else{
                    /* Flag signifies that ITEMS list which contains articles have to be reinitialized from the database, but only when
                    ConversationsFragment is being open so update of a source for ArticlesRecyclerViewAdapter does not happen while
                    ArticlesFragment is shown and there is no data invalidation resulting in crash.
                     */
                    articles_reinitialize_flag = true;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        outer.shutdown(); // Job has ended so outer ExecutorService can also be shutdown.
    }

    /**
     * Method that runs scraping jobs.
     *
     * @throws InterruptedException
     */
    public void mainScrapingJob() throws InterruptedException {
        ////Log.d(LOG_BR_INFO, "Update started");

         /*Scraping article links from the blog index page. Article object is created for each article that does
           not exist in the database and link field is filled, rest of the fields will be populated later.*/
        RetrofitHtmHandler.scrapeLinks("http://conversationsalons.ie/blog/");

        // This lock waits for the links scraping to end.
        synchronized(linksLock) {
            while (!finishedScrapingLinks.get())
                linksLock.wait();

            //After all links for articles not present in database are collected, articles are scraped and stored in db.
            scrapeArticlesAndInsertIntoDb();
        }

        // After all the requests are made we are waiting for the last five that are still being processed
        ////Log.d(LOG_BR_INFO,"NOW WAIT FOR ALL ARTICLES TO BE PROCESSED");
        synchronized (articlesLock) {
            while(articlesBeingProcessed.get()>0)
                articlesLock.wait();
            //////Log.d(LOG_BR_INFO, "#"+counter+" innerExecutor SHUTDOWN: ");
            instance.innerExecutor.shutdown(); // Last request finished so instance ExecutorService can be shut down.
        }
        ////Log.d(LOG_BR_INFO,"SHUTDOWN");
    }


    /**
     * Method that scrapes contents of each article in the list of articles that are not in db already.
     *
     * @throws InterruptedException
     */
    private void scrapeArticlesAndInsertIntoDb() throws InterruptedException {
        // Populating each Article object with scraped data (page for each article now).
        int counter = 0;
        for (Article article : listOfArticlesNotInDb){
            /* Retrofit requests are asynchronous so after each request was started loop would immediately
               continue which would overcrowd Retrofit with calls and that ended with crashes or it stoping working.
               So calls are limited to 5 at the same time and lock is released to proceed to the next loop only when one of the
               previous 5 requests notifies it ended.*/
            synchronized (articlesLock){
                while(articlesBeingProcessed.get()>5){
                    articlesLock.wait();
                }
                ////Log.d(LOG_BR_INFO, "#"+counter+" Iterating article link : " + article.getLink());
                counter++;
                articlesBeingProcessed.set(articlesBeingProcessed.get()+1);
                RetrofitHtmHandler.scrapeArticles(article);
            }
        }
    }

    public static Object getArticleLock(){
        return (instance != null)?instance.articlesLock:null;
    }

    /**
     * Method that decrease the counter of articles being currently scraped. It is called from Retrofit class.
     */
    public static void decreaseArticlesBeingProcessed(){
        instance.articlesBeingProcessed.set(instance.articlesBeingProcessed.get()-1);
        ////Log.d(LOG_BR_INFO,"ArticlesBeingProcessed = "+instance.articlesBeingProcessed.get());
    }

    /**
     * Method that initiates parsing of the content of article links page. It is called from onResponse of Retrofit
     * scrapeLinks method.
     * @param response Response string sent from Retrofit onResponse method
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void scrapeLinksOnResponseCallback(String response) throws ExecutionException, InterruptedException {
        ////Log.d(LOG_BR_INFO," LINKS CALLBACK ");

        Future<String> nextPageFut = instance.innerExecutor.submit(
                ()->instance.processLinks(response)); // Response string is send to processLinks method that parses link pages

        String nextPage = null;
        nextPage = nextPageFut.get(); // Thread is blocked until the page is parsed.

        if(nextPage != null)
            RetrofitHtmHandler.scrapeLinks(nextPage); // Scraping next page of scraping links if there is one.

        else { // No next page link on the currently parsed page so no more index pages with links.
            ////Log.d(LOG_BR_INFO,"Null is returned, links scraping ends now releasing lock");
            synchronized (instance.linksLock) { // linksLock object is notified that link scraping has ended so it can be released.
                instance.finishedScrapingLinks.set(true);
                instance.linksLock.notifyAll();
            }
        }
    }

    /**
     * Method that initiates parsing of the content of the article page. It is called from onResponse of Retrofit
     * scrapeArticles method.
     * @param response
     * @param article
     */
    public static void scrapeArticlesOnResponseCallback(String response, Article article){
        ////Log.d(LOG_BR_INFO," ARTICLE CALLBACK ");
        // Article returned from Retrofit onResponse will now be parsed
        Future<Boolean> nextPageFut = instance.innerExecutor.submit(() -> instance.processArticle(response, article));
        try {
            nextPageFut.get(); // Blocking until the parsing is over
        } catch (Exception e) {
            e.printStackTrace();
        }

        ////Log.d(LOG_BR_INFO,"INSERTING INTO DB ARTICLE  link:"+article.getLink() +"   body:"+ article.getBody());
        // As the article is parsed it will now be inserted into database.
        instance.innerExecutor.execute(()->{
            instance.insertTheArticle(article);
        });

    }

    /**
     * Method that inserts scraped Article object into database.
     *
     * @param article Article object to scrape
     */
    private void insertTheArticle(Article article){

        synchronized (articlesLock) {
            // Article is inserted into database
            articlesDatabase.articleDao().insertAll(article);
            // articlesLock is notified that this article is finalized, so lock in scrapeArticlesAndInsertIntoDb will be released to check if any more articles are still being
            articlesLock.notifyAll();
        }
    }

    /**
     * Method that parses the html page with article links
     * @param stringToProcess String representing html of the page
     * @return String that contains link to the next index page with article links, or null if there are no more links.
     */
    private String processLinks(String stringToProcess) {
        ////Log.d(LOG_BR_INFO, "Process links "+stringToProcess.substring(0,10));

        // Looping through the document extracting blog links
        int nextLinkPos = stringToProcess.indexOf("gt-image\">");
        while(nextLinkPos>-1) {
            int from = stringToProcess.indexOf("http", nextLinkPos);
            int to = stringToProcess.indexOf("\"", from);

            // Link is parsed from string
            String linkToAnArticle = stringToProcess.substring(from,to);

            // Check in database does an Article with that link exist.
            String retVal = articlesDatabase.articleDao().findByLink(linkToAnArticle);
            boolean linkExists = retVal !=null && retVal.length()>0;

            if(linkExists){ // Not storing the link in list for scraping the articles
                ////Log.d(LOG_BR_INFO, "Link exist, skip it: "+linkToAnArticle);
                nextLinkPos = stringToProcess.indexOf("category tag", to);
            }else if(linkToAnArticle.contains("ie/category/")) { // Matched pattern is not valid article link
                nextLinkPos = stringToProcess.indexOf("category tag", to);
            }else{ // Link is valid, does not exist in database so Article object will be created and stored for later article scraping
                ////Log.d(LOG_BR_INFO, "Link for scraping : "+linkToAnArticle);
                final int indxAfterFirstSlash = 10;
                String title = linkToAnArticle.substring(linkToAnArticle.indexOf("/",indxAfterFirstSlash) + 1,linkToAnArticle.length()-1);
                title = Character.toUpperCase(title.charAt(0)) + title.substring(1);
                title = title.replaceAll("-"," ");
                String imgLink = extractImgLink(from, stringToProcess);

                Article article = new Article();
                article.setTitle(title);
                article.setLink(linkToAnArticle);
                article.setImg_path(imgLink);
                listOfArticlesNotInDb.add(article);

                nextLinkPos = stringToProcess.indexOf("gt-image\">", to);
            }
        }// While ends as there are no more "category tag"-s so no more blog links on this page

        String nextPageLink = null;
        nextLinkPos = stringToProcess.indexOf("<link rel=\"next\""); // Finding link to the next page of blog links
        if(nextLinkPos > -1){ // Next page with links was found so now adress is being extracted
            int from = stringToProcess.indexOf("http", nextLinkPos );
            int to = stringToProcess.indexOf("\"", from);

            nextPageLink = stringToProcess.substring(from, to);
        }
        ////Log.d(LOG_BR_INFO, "Returning from processLinks with nextPageLink = "+nextPageLink);
        ////Log.d(LOG_BR_INFO,"");
        return nextPageLink;
    }
    private String extractImgLink(int from, String source){
        int offset = 10;
        int startIndx = source.indexOf("data-src=\"",from) + offset;
        int endIndx = source.indexOf("\"",startIndx);
        ////Log.d(LOG_BR_INFO, "IMAGE from : " + from + " , startIndx : " + startIndx + " , " + source.substring(startIndx,endIndx));
        return source.substring(startIndx,endIndx);
    }

    private String[] allAuthors = {"Heather Bourke", "Conor McCormick", "Gillian Arigho", "Jyoti Chauhan",
            "Kate Carty", "Stacey Malaniff", "Emma Carville", "Anavi Ruiz", "Marty McGhee"};

    /**
     * Method that parses the html page of a complete article
     * @param stringToProcess String representing html of the page
     * @param article Article object representing this article page, was created during the scrapeLinks part
     * @return true
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private Boolean processArticle (String stringToProcess, Article article) throws ExecutionException, InterruptedException {
        ////Log.d(LOG_BR_INFO,"\n\nprocessArticle START\n");
        ////Log.d(LOG_BR_INFO,"ARTICLE "+article.getLink());

        // Eliminating comments and post at the end
        int commentsIndx = stringToProcess.indexOf("gt-post-comments");
        if(commentsIndx != -1) stringToProcess = stringToProcess.substring(0,commentsIndx);

        // Extracting author by comparing with the allAuthorsArray
        for(String author : allAuthors){
            if(stringToProcess.contains(author)){
                article.setAuthor(author);
                stringToProcess = stringToProcess.replaceAll("[Bb]y "+author," ");
                break;
            }
        }

        StringBuilder sb = new StringBuilder();

        // Collect all text inside paragraph tags.
        String pattern = "(?s)(?<=<p>)(.*?)(?=</p>)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(stringToProcess);
        while(m.find()){
            sb.append(m.group());
        }

        String body = sb.toString();

        // Clear up collected text
        body = cleanUp(body);
        body = cleanUp(body);
        body = removeFirstCharIfNotLetter(body);
        body = removeFirstCharIfNotLetter(body);

        article.setBody(body);
        ////Log.d(LOG_BR_INFO,"processArticle END");
        return true;
    }

    /**
     * Helper method for processArticle method
     * @param str String representing parsed content that is sent here for further cleaning.
     * @return
     */
    private String cleanUp(String str){
        int cookiesStart = str.indexOf("Necessary cookies");
        int cookiesEnd = str.indexOf("your website.");
        if(cookiesStart > -1 && cookiesEnd > cookiesStart) {
            String toRemove = str.substring(cookiesStart);
            str = str.replace(toRemove," ");
        }

        str = str.replaceAll("&nbsp;"," ")
                .replaceAll("(?s)(?<=<)(.*?)(?=>)","")
                .replaceAll("(?s)(?<=&)(.*?)(?=;)","")
                .replaceAll("(?s)(?<=</)(.*?)(?=>)","")
                .replaceAll("<>"," ").replaceAll("</>"," ").replaceAll("&;"," ");
        return str;
    }

    /**
     * Helper method for processArticle method, makes specific operations on a string passed in.
     * @param str String representing already parsed content that needs additional cleaning.
     * @return
     */
    private String removeFirstCharIfNotLetter(String str){
        int firstAscii = str.charAt(0);
        if(firstAscii <65 || firstAscii > 90){
            str = str.substring(1);
        }

        return str;
    }
}
