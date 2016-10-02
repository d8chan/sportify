package gspot.com.sportify.utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

import gspot.com.sportify.Controller.GatheringActivity;
import gspot.com.sportify.R;

/**
 * Class to pick a time when creating a gathering
 *
 * Created by massoudmaher on 5/22/16.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    /*use for logging*/
    private static final String TAG = DatePickerFragment.class.getSimpleName();

    // time of day
    private int mHour;
    private int mMinute;

    /**
     * Makes dialog default to current time when launched/displayed
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Initialize dialog with current time
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // true is saying we are using 24 hr format
        return new TimePickerDialog(getActivity(), this, mHour, mMinute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        String timeString = String.format("%02d:%02d", hourOfDay, minute);

        // Return string representation of time to calling Activity, GatheringActivity.java
        ((GatheringActivity)getActivity()).setmTimeString(timeString);

        // Set button text to reflect selected date
        Button timeButton = (Button)getActivity().findViewById(R.id.timepicker);
        timeButton.setText(timeString);

        Log.d(TAG, "Date picked: " + timeString);

    }
}
