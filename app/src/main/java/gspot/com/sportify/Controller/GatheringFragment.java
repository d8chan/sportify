package gspot.com.sportify.Controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gspot.com.sportify.Model.Gathering;
import gspot.com.sportify.Model.Profile;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.App;
import gspot.com.sportify.utils.Constants;

/**
 * Authors amir assad, Aaron, Yunfan Yang on 4/17/16
 * This class uses the fragment_gathering.xml
 * This class is the detailed view of a gathering
 * it will be editable if the user is a host else
 * all editable widgets will be disabled and only viewable
 */
public class GatheringFragment extends Fragment {

    /*use for logging*/
    private static final String TAG = GatheringFragment.class.getSimpleName();

    /*the Gathering profile*/
    private Gathering mGathering;
    private Profile mProfile;

    private String hostID, hostName, gatheringUID, mCurrentUser;
    private int sizeofAttendees;

    ValueEventListener m_lis;
    Firebase gathering;

    @Bind(R.id.gathering_title) TextView mTitleField;
    @Bind(R.id.description_variable) TextView mDescriptionField;
    @Bind(R.id.location_variable) TextView mLocationField;
    @Bind(R.id.date_variable) TextView mDate;
    @Bind(R.id.time_variable) TextView mTimeField;
    @Bind(R.id.host_variable) TextView mHost;
    @Bind(R.id.public_or_private) TextView mPublicOrPrivate;
    @Bind(R.id.sport_variable) TextView mSportName;
    @Bind(R.id.skill_level_variable) TextView mSkillLevel;
    @Bind(R.id.request_join_leave_delete_button) Button mDelete;
    @Bind(R.id.edit_gathering_button) Button mEdit;
    @Bind(R.id.view_pending_button) Button mPendingDisplay;


    @OnClick(R.id.request_join_leave_delete_button)
    void onClick(Button button){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /*Writes to myGathering list */
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        int status = mGathering.getStatus(mCurrentUser);
        App.mCurrentGathering = mGathering;

        if(status == 1){
            deleteGathering();
        } //host
        else if (status == 2) {
            leaveAttending();
        } //attendee
        else if (status == 3) {
            leavePending();
        } // leave gatherig
        else {
            if (mGathering.getIsPrivate()) {
                requestGathering();
            }
            else {
                joinGathering(); }
        }

    }

    @OnClick(R.id.edit_gathering_button)
    void onClickEdit (Button button) {
        Intent intent = new Intent(getActivity(), GatheringActivity.class);
        intent.putExtra("Edit", true);
        App.mCurrentGathering = mGathering;
        startActivity(intent);
    }

    @OnClick (R.id.view_players_button)
    void onClickAttending()
    {
        Intent intent = new Intent(getActivity(), ViewAttendingPendingActivity.class);
        App.mCurrentGathering = mGathering;
        intent.putExtra("gatheringUID", mGathering.getID());
        intent.putExtra("cameFrom", "attending");
        intent.putExtra("gatheringSport", mGathering.getSport());
        getActivity().startActivity(intent);
    }

    @OnClick(R.id.view_pending_button)
    void onClickPending()
    {
        if(mGathering.getPendingSize() <= 0){
            Toast.makeText(this.getContext(), "There are no current requests", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity(), ViewAttendingPendingActivity.class);
        App.mCurrentGathering = mGathering;
        intent.putExtra("gatheringUID", mGathering.getID());
        intent.putExtra("cameFrom", "pending");
        intent.putExtra("gatheringSport", mGathering.getSport());
        getActivity().startActivity(intent);
    }

    @OnClick (R.id.host_variable)
    void onClickHost()
    {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        App.mCurrentGathering = mGathering;
        intent.putExtra("viewingUser", mGathering.getHostID());
        intent.putExtra("cameFrom", "viewing");
        Log.d(TAG, "HOSTID" + mGathering.getHostID());
        getActivity().startActivity(intent);
    }

    /* will be called when a new SportFragment needs to be created
     * This method Creates a fragment instance and bundles up &
     * sets its arguments,
     * attaching arguments to a fragment must be done after the fragment is created
     * but before it is added to an activity.*/
    public static GatheringFragment newInstance(String sportId) {
        Log.d(TAG, "newInstance() " + sportId);
        Bundle args = new Bundle();
        args.putString(Constants.ARG_SPORT_ID, sportId);    /*store the sportId for later retreival*/
        GatheringFragment fragment = new GatheringFragment();   /*create a new instance of the fragment*/
        fragment.setArguments(args);                    /*bundle up the arguments*/
        return fragment;
    } //end newInstance

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "On Create");

        Log.i(TAG, "onCreate");

        gatheringUID = getArguments().getString(Constants.ARG_SPORT_ID);



    }//end onCreate

    /**inflate the layout for the fragments view and return the view to the host*/
    @Override                //*inflate the layout   *from the activities   *layout recreate from a saved state
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {/**gets called when its the 1st time drawing its UI*/
        /**the 3rd param: whether the inflated layout should be attached to the 2nd param during inflation*/
        Log.d(TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.view_gathering2, parent, false);
        ButterKnife.bind(this, view);

        /*Read the Gathering with the unique gatheringID*/
        /*Retrieve text information from the database*/
        gathering = new Firebase(Constants.FIREBASE_URL_GATHERINGS).child(gatheringUID);
        /*Populate page with gathering*/
        m_lis = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    /*Create a gathering object from data in database*/
                    mGathering = dataSnapshot.getValue(Gathering.class);

                    /*Retrieve text information from the database*/
                    mTitleField.setText(mGathering.getGatheringTitle());
                    mDescriptionField.setText(mGathering.getDescription());
                    mTimeField.setText(mGathering.getTime());
                    mDate.setText(mGathering.getDate());
                    mLocationField.setText(mGathering.getLocation());
                    mSportName.setText(mGathering.getSID());
                    mSkillLevel.setText(mGathering.getSkillLevel().toString());
                    if(mGathering.getIsPrivate() == true) {
                        mPublicOrPrivate.setText("Closed");
                    } else {
                        mPublicOrPrivate.setText("Public");
                    }

                    hostID = mGathering.getHostID();
                    getHostname(hostID);

                    if (mGathering == null) {
                        mDelete.setText("Join");
                    }
                    else {
                        setButton();
                    }
                }catch(Exception e) {}

                /*the ValueEventListener will be called evertime the database has changed in real time
                * if the current gathering we are trying to get is no longer availble then
                * mGathering will be null*/
                if(mGathering == null){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getActivity(), "Refreshing data.", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "Fire    BaseError " + firebaseError.getMessage());
            }
        };

        gathering.addValueEventListener(m_lis);

        /**root of the fragments layout, return null if no layout.*/
        return view;
    }//end onCreateView


    @Override
    public void onDestroyView()
    {//Called when the view hierarchy associated with the fragment is being removed.
        super.onDestroyView();
        Log.i(TAG, "onDestroyView()");
        ButterKnife.unbind(this);
        gathering.removeEventListener(m_lis);
    }

    void getHostname(String hostID) {
        Firebase profileRef = new Firebase(Constants.FIREBASE_URL_PROFILES).child(hostID).child("mName");

        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mHost.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
    void deleteGathering() {

        removeAllAttendings();
        if(gathering != null && m_lis != null)
            gathering.removeEventListener(m_lis);
        App.mCurrentGathering.delete();
        App.mGatherings.remove(App.mCurrentGathering);
        App.mCurrentGathering = null;

        getActivity().finish();
    }

    void removeAllAttendings()
    {
        HashMap currentAttendees = App.mCurrentGathering.getAttendees();
        List<String> list = new ArrayList<String>(currentAttendees.values());
        Firebase myGatheringRef;

        for(String s : list)
        {
            myGatheringRef = App.dbref.child("MyGatherings").child(s).child("myGatherings").child(mGathering.getID());
            myGatheringRef.removeValue();
        }

    }

    void leaveAttending () {
        Log.i(TAG, "LEAVE ATTENDING");
        /*Gets user's UID*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /*Writes to myGathering list */
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        mGathering.removeAttendee(mCurrentUser);
        mGathering.updateAttendees(getActivity().getApplicationContext());

        Firebase myGatheringsID = new Firebase(Constants.FIREBASE_URL_MY_GATHERINGS).child(mCurrentUser).child("myGatherings").child(mGathering.getID());
        myGatheringsID.removeValue();
    }


    void leavePending () {
        Log.i(TAG, "LEAVE PENDING");
        /*Gets user's UID*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /*Writes to myGathering list */
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        mGathering.removePending(mCurrentUser);
        mGathering.updatePending(getActivity().getApplicationContext());


    }
    void joinGathering () {
        Log.i(TAG, "JOIN");
      /*Gets user's UID*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /*Writes to myGathering list */
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        mGathering.addAttendee(mCurrentUser);
        mGathering.updateAttendees(getActivity().getApplicationContext());

        Firebase myGatheringsID = new Firebase(Constants.FIREBASE_URL_MY_GATHERINGS).child(mCurrentUser).child("myGatherings");
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put(mGathering.getID(), mGathering.getID());
        myGatheringsID.updateChildren(updates);
    }

    void requestGathering() {
        Log.i(TAG, "Reqeust");
      /*Gets user's UID*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /*Writes to myGathering list */
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        mGathering.addPending(mCurrentUser);
        mGathering.updateAttendees(getActivity().getApplicationContext());
    }

    void setButton() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        /*Writes to myGathering list */
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        int status = mGathering.getStatus(mCurrentUser);

        Log.d(TAG, "STATUS" + status);
        if(status == 1) {
            mPendingDisplay.setVisibility(View.VISIBLE);
            mDelete.setText("Delete");
        } //host
        else if (status == 2) {
            mPendingDisplay.setVisibility(View.GONE);
            mEdit.setVisibility(View.GONE);
            mDelete.setText("Leave");
        } //attendee
        else if (status == 3) {
            mEdit.setVisibility(View.GONE);
            mPendingDisplay.setVisibility(View.GONE);
            mDelete.setText("Remove Request");
        } // leave gatherig
        else {
            mEdit.setVisibility(View.GONE);
            mPendingDisplay.setVisibility(View.GONE);
            if (mGathering.getIsPrivate()) { mDelete.setText("Request"); }
            else { mDelete.setText("Join"); }
        }
    }
}