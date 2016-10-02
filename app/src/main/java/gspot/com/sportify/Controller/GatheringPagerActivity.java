package gspot.com.sportify.Controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import gspot.com.sportify.Model.Gathering;
import gspot.com.sportify.Model.SportLab;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.App;

/**
 * Authors amir assad, on 4/17/16
 * This class enables the swiping functionality between single gathering's
 * after it has loaded a ViewPager onto the screen it will invoke
 * GatheringFragment by retrieving the gathering's id and passing it to GatheringFragment
 * to load the correct information on to the page.
 */
public class GatheringPagerActivity extends BaseNavBarActivity {

    private static final String EXTRA_SPORT_ID = "sport_id";
    private static final String TAG = GatheringPagerActivity.class.getSimpleName();

    /*load the pager*/
    private ViewPager mViewPager;

    /*list of all mGatherings*/
    private List<Gathering> mGatherings;

    /*
     * Function to Create a new intent and save the sport Id for when the
     * Activity gets started
     * Forces the client of this class to pass in the id of the sport before
     * the object is instantiated*/
    public static Intent newIntent(Context packageContext, String sportId){
        Log.i(TAG, "newIntent()");
        Intent intent = new Intent(packageContext, GatheringPagerActivity.class);
        intent.putExtra(EXTRA_SPORT_ID, sportId);
        return intent;
    }/*end newIntent*/


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gathering_pager);

        /*get the id of the GatheringFragment that was passed into newIntent*/
        String sportId = getIntent().getStringExtra(EXTRA_SPORT_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_sport_pager_view_pager);

        /*get all the gatherings*/
        mGatherings = App.mFilteredGatherings;

        FragmentManager fragmentManager = getSupportFragmentManager();

        /*manage the conversation between the ViewPager*/
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            /* Fetch the Gathering instance for the given position in the dataset.
            * it uses the Gathering id to create and return a properly configured
            * Gathering, it will also load a fragment before and after this position for
            * fast loading*/
            @Override
            public Fragment getItem(int position) {
                Log.d(TAG, "getItem()::FSPA " + position + " " + mViewPager.getCurrentItem());

                /*get the gathering that is being loaded*/
                Gathering gathering = mGatherings.get(position);

                return GatheringFragment.newInstance(gathering.getID());
            }

            /*return the number of items in the array list*/
            @Override
            public int getCount() {
                Log.d(TAG, "getCount()::FSPA");
                return mGatherings.size();
            }
        });

        /* by default the ViewPager shows the 1st Item in its
         * PagerAdapter which is the first sport that was clicked
         * to show the sport that was selected you can find the
         * id that will match the sportID
         */
        for(int i = 0; i < mGatherings.size(); i++){
            if(mGatherings.get(i).getID().equals(sportId)){
                mViewPager.setCurrentItem(i);
                break;
            }/*end if*/
        }/*end for*/
    }/*end onCreate*/

    /*inflates a custom drop down menu and hides certain members
    * The implementation of each field in the main menu will be done
    * by the super class (BaseNavBarActivity) implicitly*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        /*dont show this filed in this screen*/
        menu.findItem(R.id.home).setVisible(false);

        menu.findItem(R.id.active).setVisible(false);
        /*dont enable the user to log out from this page*/
        menu.findItem(R.id.log_out).setVisible(false);
        menu.findItem(R.id.profile).setVisible(false);
        menu.findItem(R.id.active).setVisible(false);
        return true;
    } //end onCreateOptionsMenu

}