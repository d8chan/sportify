
package gspot.com.sportify.Model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.UUID;

import gspot.com.sportify.utils.App;
import gspot.com.sportify.utils.Constants;

/**
 * Authors Amir Assad, massoudmaher on 5/1/16.
 * Class that represents a single gathering
 * Purpose is to instantiate one of these for each created event and push to firebase
 */
public class Gathering{


    public static enum SkillLevel {
        BEGINNER("Beginner"),
        INTERMEDIATE("Intermediate"),
        ADVANCED("Advanced");

        private final String skillLevel;

        SkillLevel(String skillLevel) {
            this.skillLevel = skillLevel;
        }

        public String getSkillLevel() {
            return skillLevel;
        }

        @Override
        public String toString() {
            return this.skillLevel;
        }
    }

    private String mSport;
    private SkillLevel mSkillLevel;
    private String mGatheringTitle;
    private String mLocation;
    private String mDescription;
    private String mHostID;
    private String mTime;
    private String mSID;
    public String mID;
    private boolean mIsPrivate;
    private HashMap mPendings;
    private HashMap mAttendees;
    private int mTimeOfDay;
    private int mDayOfWeek;
    private String mDate;
    private int attendeeSize, pendingSize;

    public Gathering() {
        mIsPrivate = false;
        mAttendees = new HashMap();
        mPendings = new HashMap();
        mSkillLevel = SkillLevel.BEGINNER;
        mDayOfWeek = 0;
        attendeeSize = 1;
        pendingSize = 0;
        mSport = "AEROBATICS";
    }

    /** Getter and Setters*/
    public String getSport() {return mSport;}
    public void setSport(String mSport) {this.mSport = mSport;}


    public void setDayOfWeek(int mDayOfWeek) {
        this.mDayOfWeek = mDayOfWeek;
    }
    public int getDayOfWeek() {
        return mDayOfWeek;
    }


    public void setGatheringTitle (String title) { this.mGatheringTitle = title.toUpperCase(); }
    public String getGatheringTitle () { return mGatheringTitle; }

    public void setLocation (String location) { this.mLocation = location; }
    public String getLocation () { return mLocation; }

    public void setDescription (String description) { this.mDescription = description; }
    public String getDescription () { return mDescription; }

    public void setHostID (String hostID) { this.mHostID = hostID; }
    public String getHostID () { return mHostID; }

    public void setTime (String time) { this.mTime = time; }
    public String getTime () { return mTime; }

    public void setSID (String SID) { this.mSID = SID; }
    public String getSID () { return mSID; }

    public void setID (String ID) { this.mID = ID; }
    public String getID () { return mID; }

    public String getmDate() {
        return mDate;
    }
    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public void setIsPrivate (boolean isPrivate) { this.mIsPrivate = isPrivate; }
    public boolean getIsPrivate () { return mIsPrivate; }

    public void setAttendees(HashMap attendees) { this.mAttendees = attendees; }
    public HashMap getAttendees () { return mAttendees; }

    public void setPendings(HashMap pendings) {this.mPendings = pendings;}
    public HashMap getPendings() {return mPendings;}

    public void setTimeOfDay (int timeofDay) { this.mTimeOfDay = timeofDay; }
    public int getTimeOfDay () { return mTimeOfDay; }

    public void setDate (String date) { this.mDate = date; }
    public String getDate () { return mDate; }

    public void setSkillLevel (SkillLevel skillLevel) { this.mSkillLevel = skillLevel; }
    public static SkillLevel toSkillLevel (String skillLevel) {
        if (skillLevel.equals("Intermediate")) {
            return SkillLevel.INTERMEDIATE;
        }
        else if (skillLevel.equals("Advanced")) {
            return SkillLevel.ADVANCED;
        }
        else {
            return SkillLevel.BEGINNER;
        }
    }
    public SkillLevel getSkillLevel () { return mSkillLevel; }

    public void delete()
    {
        App.dbref.child("Gatherings").child(mID).removeValue();
    }

    public void addAttendee(String userUID) {mAttendees.put(userUID, userUID);}

    public void addPending(String userUID) {mPendings.put(userUID, userUID);}

    public void addPendingToAttending(String userUID)
    {
        mPendings.remove(userUID);
        addAttendee(userUID);
    }

    public void removeAttendee(String userUID) {mAttendees.remove(userUID);}

    public void removePending(String userUID) {mPendings.remove(userUID);}

    //public void setAttendeeSize() {attendeeSize = mAttendees.size();}
    public int getAttendeeSize(){ return mAttendees.size();}

    public int getPendingSize(){ return mPendings.size();}

    public int getStatus(String userUID){
        boolean attending = false;
        boolean pending = false;
        boolean fresh = false;

        if(userUID.equals(mHostID)) return 1; //host

        if (mAttendees.get(userUID) != null) {
            attending = true;
        }

        if(mPendings.get(userUID) != null) {
            pending = true;
        }
        if(attending && !pending) return 2;
        if(pending && !attending) return 3;

        return 0; // for new
    }


    public void updateAttendees(final Context context) {

        Firebase profileRef = new Firebase(Constants.FIREBASE_URL_GATHERINGS).child(mID);


        profileRef.setValue(this, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Toast.makeText(context, "Save Successful", Toast.LENGTH_SHORT);
            }
        });
    }
    public void updatePending(final Context context) {

        Firebase profileRef = new Firebase(Constants.FIREBASE_URL_GATHERINGS).child(mID);


        profileRef.setValue(this, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Toast.makeText(context, "Save Successful", Toast.LENGTH_SHORT);
            }
        });
    }

    public void updateGathering() {
        Firebase profileRef = new Firebase(Constants.FIREBASE_URL_GATHERINGS).child(mID);
        profileRef.setValue(this, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
            }
        });
    }




}

