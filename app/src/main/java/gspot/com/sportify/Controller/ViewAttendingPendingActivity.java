package gspot.com.sportify.Controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import butterknife.ButterKnife;
import gspot.com.sportify.Model.MySport;
import gspot.com.sportify.Model.Profile;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.App;
import gspot.com.sportify.utils.Constants;
import gspot.com.sportify.utils.UserPicture;

/**
 * This file views the attending List of Pending List
 * Created by Aaron and Danny on 5/21/2016.
 */
public class ViewAttendingPendingActivity extends BaseNavBarActivity {
    private static final String TAG = ViewAttendingPendingActivity.class.getSimpleName();


    private Context context;
    //Holds the UserUID
    private ArrayList<String> userUID = new ArrayList();
    //Holds the profile of all the players
    private ArrayList<Profile> mPlayersList = new ArrayList<>();
    private String gatheringUID, cameFrom, mSport;
    private PendingAttendingAdapter mAdapter;
    /*the View to hold our list of Sports*/
    private RecyclerView mAttendingPendingRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pending_attending);
        ButterKnife.bind(this);
        context = getApplicationContext();

        mAttendingPendingRecyclerView = (RecyclerView) findViewById(R.id.pending_attending_recycler_view);
        /*recycler view delegates the positioning to the layout manager*/
        mAttendingPendingRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Intent intent = getIntent();
        gatheringUID = intent.getStringExtra("gatheringUID");
        cameFrom = intent.getStringExtra("cameFrom");
        mSport = intent.getStringExtra("gatheringSport");

        if(cameFrom.equals("attending")) {
            setTitle("View Players");
        }

        else if(cameFrom.equals("pending"))
        {
            setTitle("Requests");
        }

        //gets the users ids and then their profiles
        loadUserUID(gatheringUID);


        Log.d(TAG, "popularList size:" + userUID.size());

        Log.d(TAG, "popularList size after register call back:" + userUID.size());

    } //end onCreate()

    /*utility function to update the UI for new data*/
    public void updateUI(){
        /*there are currently no gatherings listed*/
        if(mAdapter == null) {
            mAdapter = new PendingAttendingAdapter(mPlayersList);

            /*set the data behind the list view*/
            mAttendingPendingRecyclerView.setAdapter(mAdapter);
        }/*end if*/

        /*only one sport will change at a time*/
        else {
            Log.i(TAG, "notify");
            mAdapter.setSports(mPlayersList);
            mAdapter.notifyDataSetChanged();
        }/*end else*/
    }

    void getPlayerName(String playerID) {
        Firebase profileRef = Profile.profileRef(playerID);

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("onDataChange", "loadUserId jkhsbfhjwebfhjbwefhjbew");
                Profile profile =  dataSnapshot.getValue(Profile.class);
                if (profile == null ) { profile = new Profile(); }
                mPlayersList.add(profile);
                Log.d(TAG, "Profile " + profile.getmName());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
    private void loadUserUID (String gatheringUID) {
        userUID.clear();

        Firebase attendingRef;

        if(cameFrom.equals("pending")) {
            attendingRef = App.dbref.child("Gatherings").child(gatheringUID).child("pendings");
        }
        else {
            attendingRef = App.dbref.child("Gatherings").child(gatheringUID).child("attendees");
        }
        attendingRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*refresh the list for the next lookup*/
                mPlayersList = new ArrayList<Profile>();
                userUID.clear();
                Log.d(TAG, userUID.size() + " is the size of the list after none iteration");
                for (DataSnapshot attendeeSnapshot: dataSnapshot.getChildren()) {
                    String participant = attendeeSnapshot.getValue (String.class);
                    Log.d(TAG,"Adding " + participant);
                    userUID.add(participant);
                    getPlayerName(participant);
                    Log.d(TAG, userUID.size() + " is the size of the list after iteration");
                }
                updateUI();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }

        });

    }

    /*Provide a reference to the views for each data item
      Complex data items may need more than one view per item, and
      you provide access to all the views for a data item in a view holder
      Implements the onClickerListener so everytime a sport is clicked
      an action will happen
     */
    private class PlayerHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        /*use for logging*/
        private final String TAG = PlayerHolder.class.getSimpleName();

        private Profile mPlayer;
        private TextView mName;
        private ImageView mPicture;
        private TextView mSkill;
        private Button mAccept;


        public PlayerHolder(View itemView) {
            super(itemView);

            Log.i(TAG, "SportHolder()");

            /*link member with the widget*/
            mName = (TextView)itemView.findViewById(R.id.player_name);
            mPicture = (ImageView) itemView.findViewById(R.id.player_picture);
            mSkill= (TextView) itemView.findViewById(R.id.player_skill_lv);
            mAccept = (Button) itemView.findViewById(R.id.accept_request);

            /*when the player is clicked in the list*/
            itemView.setOnClickListener(this);
        }//end Sport()

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick()" + mPlayer.getmOwner());


            Intent intent = new Intent(context, ProfileActivity.class);
            String UID = mPlayer.getmOwner();
            intent.putExtra("viewingUser", UID);
            intent.putExtra("cameFrom", "list");
            Log.i(TAG, "UID" + UID);
            startActivity(intent);
            //or create other intents here

            Log.i(TAG, "END OF FUNCTION");

        } //end onClick()


        /*function to display the data names on screen
         *the RecyclerView will call onCreateViewHolder to
         *update the screen*/
        public void bindPlayer(Profile player) {

            Log.i(TAG, "bindSport()");
            mPlayer = player;
            mName.setText(mPlayer.getmName());
            mPicture.setImageBitmap(UserPicture.StringToBitMap(mPlayer.getmProfilePic()));
            if (cameFrom.equals("attending")) {
                mAccept.setVisibility(View.GONE);
            }
            int indexOfSport = -1;


            //set the skill of the player only if they have that sport in their profile
            List<MySport> mySports = mPlayer.getmMySports();
            //finds the index of the users sport profile
            indexOfSport = mPlayer.getIndexOfSport(mSport);


            if (indexOfSport == -1) {
                mSkill.setVisibility(View.GONE);
            } else {
                mSkill.setVisibility(View.VISIBLE);
                mSkill.setText(mySports.get(indexOfSport).getmSkillLevel().toString());
            }




            mAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Firebase myGatheringsID = new Firebase(Constants.FIREBASE_URL_MY_GATHERINGS).child(mPlayer.getmOwner()).child("myGatherings");
                    String UID = mPlayer.getmOwner();
                    App.mCurrentGathering.addPendingToAttending(UID);
                    App.mCurrentGathering.updatePending(getApplicationContext());
                    mPlayersList.remove(mPlayer);

                    Map<String, Object> updates = new HashMap<String, Object>();
                    updates.put(App.mCurrentGathering.getID(), App.mCurrentGathering.getID());
                    Log.i(TAG, "UID IS" + mPlayer.getmOwner());
                    Log.i(TAG, "mCurrentGathering ID" + App.mCurrentGathering.getID());
                    myGatheringsID.updateChildren(updates);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(view.getContext() ,"You have successfully accepted " + mPlayer.getmName() , Toast.LENGTH_SHORT).show();

                }

            });
        }/*end bindSport*/
    }/*end SportHolder*/

    public class PendingAttendingAdapter extends RecyclerView.Adapter<PlayerHolder>{

        /*use for logging*/
        private final String TAG = PendingAttendingAdapter.class.getSimpleName();

        /*list of sports*/
        private List<Profile> mPlayers;

        PendingAttendingAdapter(List<Profile> players){ this.mPlayers = players; }


        @Override
        public int getItemCount() {
            return mPlayers.size();
        }

        @Override
        public PlayerHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.the_user, viewGroup, false);
            PlayerHolder pvh = new PlayerHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(PlayerHolder playerHolder, int position) {
            Profile player = mPlayers.get(position);
            playerHolder.bindPlayer(player);  /*give the proper description to the widets*/
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public void setSports(List<Profile> players) { mPlayers = players; }

    }

    /*inflates a custom drop down menu and hides certain members
    * The implementation of each field in the main menu will be done
    * by the super class (BaseNavBarActivity) implicitly*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.home).setVisible(false);
        /*dont enable the user to log out from this page*/
        menu.findItem(R.id.log_out).setVisible(false);
        menu.findItem(R.id.profile).setVisible(false);
        menu.findItem(R.id.active).setVisible(false);
        return true;
    } //end onCreateOptionsMenu
}