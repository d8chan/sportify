package gspot.com.sportify.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

import butterknife.Bind;
import gspot.com.sportify.Model.Profile;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.App;
import gspot.com.sportify.utils.Constants;

/**
 * Authors: amir assad, yunfan yang on 4/24/16.
 *
 * This is the base class for the navigationBar
 * this class over writes the onOptionsItemSelected()
 * Which only implements the drop down menu options of
 * the Nav Bar. Other classes derived from this class can
 * implement their own onOptionsItemSelected() and if they
 * also need to use this classes function they can call
 * super.onOptionsItemSelected(item);
 */
public class BaseNavBarActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*the intent to start*/
        Intent intent = null;

        switch (id) {

            case R.id.log_out:
                SharedPreferences settings = getSharedPreferences(Constants.STARTER_ID, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();

                /*flag to search if app was run before*/
                editor.putBoolean("logged_in",false).commit();

                /*delete config files*/
                App.deleteConfigFiles(this);

                /*go back to the login activity*/
                intent = new Intent(this, LoginActivity.class);

                startActivity(intent);
                finish();
                break;

            case R.id.profile:
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String UID = prefs.getString(Constants.KEY_UID, "");
                intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("viewingUser", UID);
                intent.putExtra("cameFrom", "profile");
                startActivity(intent);
                break;
        }//end case

        return super.onOptionsItemSelected(item);
    } //end onOptionsItemSelected
} //end BaseNavBarActivity


