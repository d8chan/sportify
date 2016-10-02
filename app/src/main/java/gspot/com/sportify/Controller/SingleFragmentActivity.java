package gspot.com.sportify.Controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import gspot.com.sportify.R;

/**
 * Author amir assad on 4/17/16.
 */
public abstract class SingleFragmentActivity extends BaseNavBarActivity {

    /*for Other classes to say which fragment to use*/
    protected abstract Fragment createFragment();

    /*use for logging*/
    private static final String TAG = SingleFragmentActivity.class.getSimpleName();

    @Override
    public void onCreate (Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()::SingleFragmentActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        /*when an activity is destroyed--> fm saves out its list of fragments*/
        FragmentManager fm = getSupportFragmentManager();

        /*on create could have been re-created*/
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        /*first time the fragments is being created*/
        if(fragment == null){
            fragment = createFragment();
            /**where should the fragments view appear in activity*/
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        } //end if


    }//end onCreate
} //end SingleFragmentActivity