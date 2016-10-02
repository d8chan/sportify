package gspot.com.sportify.Controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import gspot.com.sportify.Model.MySport;
import gspot.com.sportify.Model.Profile;
import gspot.com.sportify.Model.SportType;
import gspot.com.sportify.Model.SportTypes;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.Constants;
import gspot.com.sportify.utils.GatheringTypeProvider;

/** Add Sport Fragment Class
 * Represents a list of sports for the user to
 * add a sport to their profiles. Once a user clicks on
 * a sport, this fragments adds the sport to their profile
 * with default values which the user can modify.
 */
public class AddSportFragment extends DialogFragment implements Observer{

    private Profile mProfile;
    /*contains the list of sports from the databse*/
    private SportTypes mDataBaseSports = new SportTypes();
    private ListView listView;

    private static final String TAG = AddSportFragment.class.getSimpleName();

    String mCurrentUser;

    /** On Create Method
     * Called when Add Button is Pressed. (To be continued)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Choose a Sport.");
        return dialog;
    }

    /** On Create View Method
     * Generates the view of the fragment. (Also to be continued)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");

        View v = inflater.inflate(R.layout.fragment_add_sport, container, false);
        ButterKnife.bind(this, v);

        listView = (ListView) v.findViewById(R.id.sport_type_list);

        /*create an observer*/
        mDataBaseSports.addObserver(this);
        /*Asynchronous tasks*/
        mDataBaseSports.readSportTypes();


        return v;
    }

    @Override
    public void update(Observable observable, Object data) {

        Log.i(TAG, "update");
        /*Filters chosen by the user that was saved on the device*/
        HashMap<Integer, boolean[]> filters;

        ArrayList<String> sport_types = new ArrayList<String>();
        for (SportType sport_type : mDataBaseSports.sportTypes) {
            sport_types.add(sport_type.getName());
        }

        Collections.sort(sport_types);
        ProfileActivity activity = (ProfileActivity) getActivity();
        List<String> currentSports = activity.getMySportList();
        mProfile = activity.getProfile();

        //I user shouldn't be able to have two basketball profiles, so hide all the sports they
        //already have a profile for
        if (currentSports != null) {
            sport_types.removeAll(currentSports);
        }

        ArrayAdapter<String> sportTypeListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_sport_type, sport_types);
        listView.setAdapter(sportTypeListAdapter);

        setSportListListeners(listView, sport_types);

    } //end update

    /** Set Sport Listeners Method
     * Creates listeners for each sport in the list.
     */
    private void setSportListListeners(ListView lv, final List<String> sport_types) {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i(TAG, "onItemClick()");

                if (mProfile.getmMySports() == null) {
                    mProfile.setmMySports(new ArrayList<MySport>());
                }
                //get the title of the sport from sport_type, then make a new MySport and add
                //it to the users profile
                mProfile.getmMySports().add(new MySport(sport_types.get(position)));
                ProfileActivity activity = (ProfileActivity) getActivity();
                Context context = activity.getApplicationContext();
                activity.setMySportsAdapter(context);
                activity.getFragManager().beginTransaction().remove(AddSportFragment.this).commit();
            }
        });
    }

}
