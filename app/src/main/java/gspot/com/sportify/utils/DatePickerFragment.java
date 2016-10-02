package gspot.com.sportify.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import gspot.com.sportify.Controller.GatheringActivity;
import gspot.com.sportify.R;

/**
 * Class for picking a date used in create gathering page
 * fragment_gathering.xml
 *
 * Created by massoudmaher on 5/15/16.
 */
public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

    /*use for logging*/
    private static final String TAG = DatePickerFragment.class.getSimpleName();

    /**
     *
     * @param savedInstanceState
     * @return DatePickerDialog with calendar set to current date
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // TODO set these to last set date and time

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     *  Updates instance vars when date is set and updates the mDateString field of
     *  the calling Activity, which is GatheringActivity.java
     *
     * @param view
     * @param year
     * @param month
     * @param day
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {

        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE, MMM d, yy");
        GregorianCalendar date = new GregorianCalendar(year, month, day);
        simpledateformat.setCalendar(date);

        String dateString = simpledateformat.format(date.getTime());
        int dayOfTheWeek = date.get(date.DAY_OF_WEEK) - 1;
        // Return dateString to calling activity (GatheringActivity.Java)
        ((GatheringActivity)getActivity()).setDateString(dateString, dayOfTheWeek);

        // Update button to display newString
        Button dateButton = (Button)getActivity().findViewById(R.id.datepicker);
        dateButton.setText(dateString);

        Log.e(TAG, "Date picked: " + year + "-" +  month + "-" + day + "-" + dayOfTheWeek);
    }



}
