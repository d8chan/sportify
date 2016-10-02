package gspot.com.sportify.utils;

import android.app.Activity;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

import gspot.com.sportify.Model.Gathering;
import gspot.com.sportify.Model.SportTypes;

/**
 * Created by yunfanyang on 5/2/16.
 */
public class App {
    public static Firebase dbref = new Firebase(Constants.FIREBASE_URL);

    /*All gatherings from data base*/
    public static List<Gathering> mGatherings = new ArrayList<>();

    /*Gathering's based on user's filters*/
    public static List<Gathering> mFilteredGatherings = null;

    /* current gathering selected*/
    public static Gathering mCurrentGathering = null;

    /*filter: show based on skill level*/
    public static boolean [] mCurrentSkillLevels = { true, true, true };
    /*Match my availability*/
    public static boolean mMatch_My_Availability = false;
    /*filter: show closed events*/
    public static boolean mIsPrivateEvent = false;
    /*contains the names of the sports chosen in filter*/
    public static List<String> mChosenSports;

    public static void deleteConfigFiles(Activity activity) {
        activity.deleteFile(Constants.SPORTS_FILTER_FILE);
        activity.deleteFile(Constants.SKILL_LEVEL_FILE);
    }
}