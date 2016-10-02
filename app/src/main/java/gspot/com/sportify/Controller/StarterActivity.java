package gspot.com.sportify.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import gspot.com.sportify.utils.App;
import gspot.com.sportify.utils.Constants;

/**
 * Created by amir, assad, yunfan on 4/17/16.
 * This class is the first class that will be called everytime the application starts
 * The Job of this class is to determine where the user has currently logged in to the app
 * if they have then they will be redirected to the Gathering list else they will
 * be redirected to the login page.
 */
public class StarterActivity extends Activity {

    private static final String TAG = StarterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate()");

        /*the value stored here will remain on device until the app is deleted*/
        SharedPreferences settings = getSharedPreferences(Constants.STARTER_ID, Context.MODE_PRIVATE);

        //activity to start
        Intent intent;

        /*see if the flag exists if not return the default value*/
        boolean logged_in = settings.getBoolean("logged_in",false);

        //if running for first time or not logged in*/
        if(logged_in){
            Log.i(TAG, "Logged in");
            /*start the home activity*/

            intent = new Intent(StarterActivity.this, GatheringListActivity.class); //Default Activity

        } //end if

        /*already logged in*/
        else {
            Log.i(TAG, "Not logged in yet");

            /*start fresh and delete the file that is stored on user device*/
            Log.i(TAG, "Deleteting file " + Constants.SPORTS_FILTER_FILE + " from device");
            App.deleteConfigFiles(this);

            /*start the login activity*/
            intent = new Intent(StarterActivity.this, LoginActivity.class);

        } //end else

        /*start the correct activity*/
        startActivity(intent);
        /*terminate current activity*/
        finish();

    }//end onCreate

} //end StarterActivity
