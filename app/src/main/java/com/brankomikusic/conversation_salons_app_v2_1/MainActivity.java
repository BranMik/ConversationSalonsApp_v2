package com.brankomikusic.conversation_salons_app_v2_1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import com.brankomikusic.conversation_salons_app_v2_1.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Main Activity class of the application. It is host to the fragments representing different sections
 * of the application.
 *
 * @author Branko Mikusic
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static String LOG_BR_ERROR = "BR_ERROR";
    public static String LOG_BR_INFO = "BR_INFO";
    public static String SHARED_PREFERENCES_IDENTIFIER_NAME = "app_data";

    public static Toolbar toolbarRef ;

    private static SharedPreferences sharedPreferencesInstance;
    private ActivityMainBinding activityMainBinding;


    public static SharedPreferences getSharedPreferences(){
        return sharedPreferencesInstance;
    }

    public static void setSharedPreferencesInstance(SharedPreferences shPrefs){
        sharedPreferencesInstance = shPrefs;
    }

    /**
     * Toolbar is set up. Navigation Drawer is set up here as well. Action is initiated for checking user
     * notification subscriptions, if this is first time user default notification subscriptions are made
     * to the Firebase Messaging Service and they are stored in Shared Preferences.
     *
     * @param savedInstanceState  Bundle variable that may contain saved values/states (or could be null).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarRef = toolbar;
        setSupportActionBar(toolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_invitations, R.id.nav_articles,  R.id.nav_conversations, R.id.nav_recommendations,R.id.nav_recent)
                .setDrawerLayout(activityMainBinding.drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(activityMainBinding.navView, navController);

        sharedPreferencesInstance = getSharedPreferences(SHARED_PREFERENCES_IDENTIFIER_NAME, MODE_PRIVATE);

        UserObject.UserSettings.initUserSubscriptions();
    }

    /**
     * Method for initializing user data shown in the upper part of Navigation Drawer is started in onResume.
     */
    @Override
    protected void onResume(){
        super.onResume();
        initializeDrawerUserDataViews(MainActivity.this);
    }

    /**
     * Method for initializing user data shown in the upper part of Navigation Drawer .
     * @param context Context reference
     */
    private void initializeDrawerUserDataViews(Activity context){
        NavigationView navigationView = (NavigationView) context.findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView tv_fullnameInDrawer = (TextView)hView.findViewById(R.id.tv_drawer_username);
        ImageView imgv_profileImgInDrawer = (ImageView) hView.findViewById(R.id.imgv_drawer_profilePic);
        if(UserObject.getUserObjectInstance().getFullName() != null && UserObject.getUserObjectInstance().getFullName().length()>1)
            tv_fullnameInDrawer.setText(UserObject.getUserObjectInstance().getFullName());

        MyUtils.showImageFromCloudStorage(context,UserObject.getUserObjectInstance().getProfileImageLocationInCloudStorage(),
                imgv_profileImgInDrawer,R.drawable.image_empty);
    }

    /**
     * Options menu is inflated.
     * @param menu Menu reference
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Method that is called when user selects/click some menu item.
     * @param item MenuItem reference
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.bla) {
            Intent intent = new Intent(this,UserSettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        toolbarRef.setVisibility(View.VISIBLE);
    }
}