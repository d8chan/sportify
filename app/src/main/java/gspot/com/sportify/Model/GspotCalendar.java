package gspot.com.sportify.Model;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import gspot.com.sportify.utils.Constants;

/** GSpot Calendar
 * Represents a calendar indicating a user's
 * times of availability.
 * Created by patrickhayes on 5/3/16.
 */
public class GspotCalendar extends Observable{

    /* Represents the times that the user is available */
    private List<List<Boolean>> calendarGrid;

    /* Magic Numbers */
    private static final int NUM_TIMES_OF_DAY = 4;
    private static final int NUM_DAYS_OF_WEEK = 7;

    /* Create GSpot Calendar with default values */
    public GspotCalendar() {
        calendarGrid = new ArrayList<List<Boolean>>(7);

        /* For all time cells, the user is available */
        for( int i= 0; i < NUM_DAYS_OF_WEEK; ++i) {
            calendarGrid.add(new ArrayList<Boolean>(4));
            for (int j=0; j < NUM_TIMES_OF_DAY; ++j) {
                calendarGrid.get(i).add(true);
            }
        }
    }

    /* Get the calendar data */
    public List<List<Boolean>> getCalendarGrid() {
        return calendarGrid;
    }

    /** Toggle Time Method
     * Toggles the time to available/busy at a specific time of
     * the day and a specific day of the week.
     * @param timeOfDay specified time of the day to toggle (0-3)
     * @param dayOfWeek specified day of the week to toggle (0-6)
     */
    public void toggleTime(int dayOfWeek, int timeOfDay) {

        /* See if user is available at this time and day */
        Boolean available = calendarGrid.get(dayOfWeek).get(timeOfDay);

        /* Toggle the availability at this time and day */
        calendarGrid.get(dayOfWeek).set(timeOfDay, !available);
    }

    /** Set Availability Method
     * Sets the a specified time to be set as
     * 'available' or 'busy'.
     * @param availability specified time of the day to set
     * @param timeOfDay specified time of the day to set (0-3)
     * @param dayOfWeek specified day of the week to toggle (0-6)
     */
    public void setAvailability(boolean availability, int dayOfWeek, int timeOfDay) {
        calendarGrid.get(dayOfWeek).set(timeOfDay, availability);
        /*To notify the observers we have changed*/
        setChanged();
        notifyObservers();
    }

    /** Get Availability Method
     * Gets the a specified time to be set as
     * 'available' or 'busy'.
     * @params dayOfWeek - the day of the week (0-6)
     * @params timeOfDay - the time of the day (0-3)
     */
    public boolean getAvailability(int dayOfWeek, int timeOfDay) {
        return calendarGrid.get(dayOfWeek).get(timeOfDay);
    }

    public void getCalendar(String UID) {
        Firebase profileRef = Profile.profileRef(UID);

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                GspotCalendar calendarTemp =  dataSnapshot.getValue(Profile.class).getmCalendar();
                calendarGrid = calendarTemp.getCalendarGrid();

                 /*To notify the observers we have changed*/
                setChanged();
                notifyObservers();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public boolean playerCanMakeGathering (Gathering gathering) {

        Log.e("Gathering", this.toString());

        int dayOfWeek = gathering.getDayOfWeek();
        int time = GspotCalendar.convertTimeStringToEncodedInt(gathering.getTime());

        return getAvailability(dayOfWeek, time);
    }

    private static int convertTimeStringToEncodedInt(String time) {
        if (time == null) { time = "00:00";}
        String hourString = time.split(":")[0];
        int hour =  Integer.parseInt(hourString);
        int encodedHour = -1;

        if (Constants.EARLY_MORNING <= hour && hour < Constants.NOON) {
            encodedHour = 0;
        } else if (Constants.NOON <= hour && hour < Constants.EVENING) {
            encodedHour = 1;
        } else if (Constants.EVENING <= hour && hour < Constants.NIGHT) {
            encodedHour = 2;
        } else {
            encodedHour = 3;
        }

        return encodedHour;
    }



}