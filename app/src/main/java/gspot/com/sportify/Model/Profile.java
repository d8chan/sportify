package gspot.com.sportify.Model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import gspot.com.sportify.utils.Constants;

/** Profile Class
 * Represents and holds all of a user's information.
 * Created by patrickhayes on 5/3/16.
 */

public class Profile {

    /* Set of information that each profile has */
    private String mOwner;
    private String mProfilePic;
    private String mName;
    private String mBio;
    private String mContactInfo;
    private GspotCalendar mCalendar;
    private List<MySport> mMySports;

    /* Create a profile, but don't initialize the data */
    public Profile() {
    }

    /* Create a profile with default data */
    public Profile(String mName, String mOwner) {
        this.mName = mName;
        this.mOwner = mOwner;
        this.mBio = "This is my description.";
        this.mContactInfo = "(555)555-5555, email@ucsd.edu";
        this.mCalendar = new GspotCalendar();
        this.mMySports = new ArrayList<MySport>();
        this.mProfilePic = Constants.DEFAULT_PROFILE_PIC;
    }

    /* Get the name of the owner */
    public String getmOwner() {
        return mOwner;
    }

    /* Get the name of the user */
    public String getmName() {
        return mName;
    }

    /* Get the bio information */
    public String getmBio() {
        return mBio;
    }

    /* Get the contact info information */
    public String getmContactInfo() {
        return mContactInfo;
    }

    /* Get the list of sports */
    public List<MySport> getmMySports() {
        return mMySports;
    }

    /* Get the user's calendar */
    public GspotCalendar getmCalendar() {
        return mCalendar;
    }

    /* Get the user's profile picture */
    public String getmProfilePic() {
        return mProfilePic;
    }

    /* Set the owner */
    public void setmOwner(String mOwner) {
        this.mOwner = mOwner;
    }

    /* Set the profile picture */
    public void setmProfilePic(String mProfilePic) {
        this.mProfilePic = mProfilePic;
    }

    /* Set the name of the user */
    public void setmName(String mName) {
        this.mName = mName;
    }

    /* Set the bio information for the user */
    public void setmBio(String mBio) {
        this.mBio = mBio;
    }

    /* Set the contact info */
    public void setmContactInfo(String mContactInfo) {
        this.mContactInfo = mContactInfo;
    }

    /* Set the calendar */
    public void setmCalendar(GspotCalendar mCalendar) {
        this.mCalendar = mCalendar;
    }

    /* Set the list of sports */
    public void setmMySports(List<MySport> mMySports) {
        this.mMySports = mMySports;
    }

    /** Toggle Time Method
     * Toggle the availability of the user at a specific time
     * of the day and a specific day of the week.
     * @param timeOfDay specified time of the day to toggle
     * @param dayOfWeek specified day of the week to toggle
     */
    public void toggleTime(int dayOfWeek, int timeOfDay) {
        mCalendar.toggleTime(dayOfWeek, timeOfDay);
    }

    /** Set Availability Method
     * Sets the a specified time to be set as
     * 'available' or 'busy'.
     * @param availability specified time of the day to set
     * @param timeOfDay specified time of the day to set
     * @param dayOfWeek specified day of the week to set
     */
    public void setAvailability(boolean availability, int dayOfWeek, int timeOfDay) {
        mCalendar.setAvailability(availability, dayOfWeek, timeOfDay);
    }

    /**
     * Save To Database Method
     * Saves all data inputted in edit mode and sends this
     * information the database.
     */
    public void updateProfile(final Context context) {

        Firebase profileRef = new Firebase(Constants.FIREBASE_URL_PROFILES).child(this.mOwner);


        profileRef.setValue(this, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Toast.makeText(context, "Save Successful", Toast.LENGTH_SHORT);
            }
        });
    }

    public static Firebase profileRef(String profileId) {
        return new Firebase(Constants.FIREBASE_URL_PROFILES).child(profileId);
    }

    public List<String> getMySportsAsString () {

        List<String> toReturn = new ArrayList<>();
        if (mMySports == null) { mMySports = new ArrayList<>();}
        for (MySport sport: mMySports) {
            toReturn.add(sport.getmSport());
        }

        return toReturn;
    }

    public int getIndexOfSport (String sport) {

        int indexOfSport = -1;
        //set the skill of the player only if they have that sport in their profile
        List<MySport> mySports = getmMySports();
        List<String> mySportsOnlyNames = getMySportsAsString();

        if (mySports == null) {
            indexOfSport = -1;
        } else {
            indexOfSport = mySportsOnlyNames.indexOf(sport);
        }
        return indexOfSport;
    }
}