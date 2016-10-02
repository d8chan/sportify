package gspot.com.sportify.Controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import gspot.com.sportify.Model.Gathering;
import gspot.com.sportify.Model.SportType;
import gspot.com.sportify.Model.SportTypes;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.App;
import gspot.com.sportify.utils.Constants;
import gspot.com.sportify.utils.DatePickerFragment;
import gspot.com.sportify.utils.TimePickerFragment;


/**
 * Created by DannyChan on 5/8/16.
 */
public class GatheringActivity extends BaseNavBarActivity implements OnItemSelectedListener, Observer {

    private static final String TAG = GatheringActivity.class.getSimpleName();
    private static final int REQUEST_DATE = 0;

    private Gathering mgathering;
    private String m_hostID, mCurrentUser;
    private boolean toEdit;
    private int mDayOfWeek;
    private String mDateString = "";
    private String mTimeString = "";
    private Spinner sportTypeSpinner;
    private Spinner skillLevelSpinner;

    /*List of sports from database*/
    //private List<String> mSportTypes;

    private SportTypes mDataBaseSports;

    @Bind(R.id.sport_title)
    EditText mTitleField;
    @Bind(R.id.sport_description)
    EditText mDescriptionField;
    @Bind(R.id.sport_location)
    EditText mLocationField;
    @Bind(R.id.datepicker)
    Button dateButton;
    @Bind(R.id.timepicker)
    Button timeButton;

    @OnCheckedChanged(R.id.sport_status)
    void onCheckChanged(boolean isChecked) {
        if (toEdit) {
            App.mCurrentGathering.setIsPrivate(isChecked);
        }
        else {
            mgathering.setIsPrivate(isChecked);
        }
    }

    @OnClick(R.id.sport_submit)
    void onClick(Button button) {
        if (toEdit) {
            updateGathering();
        } else {
            submitGathering();
        }
    }

    /**
     * Prompts user to select a date and modifies the mDate field of mgathering
     * which will be pushed to firebase once submit is clicked
     *
     */
    @OnClick(R.id.datepicker)
    void inputDate(Button dateButton) {
        DatePickerFragment newFragment = new DatePickerFragment();

        // display calendar dialog for picking date
        newFragment.show(getFragmentManager(), "datepickerFragment");
    }

    /**
     * Prompts user to select a time and modifies the mTime field of mgathering
     * which will be pushed to firebase once submit is clicked
     */
    @OnClick(R.id.timepicker)
    void inputTime() {
        // Create timepicker dialog and show it
        TimePickerFragment timeFragment = new TimePickerFragment();
        timeFragment.show(getFragmentManager(), "timepickerFragment");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gathering);

        setContentView(R.layout.fragment_gathering_update);
        ButterKnife.bind(this);

        skillLevelSpinner = (Spinner) findViewById(R.id.skill_lv_spinner);
        sportTypeSpinner = (Spinner) findViewById(R.id.sport_type_spinner);


        ButterKnife.bind(this);

        /*intialize the observer*/
        mDataBaseSports = new SportTypes();
        mDataBaseSports.addObserver(this);

        /*get the sport types from database and call update when done*/
        mDataBaseSports.readSportTypes();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.sport_type_spinner:
                String sport = parent.getItemAtPosition(position).toString();
                Log.e(TAG, sport);
                if (toEdit) {
                    App.mCurrentGathering.setSID(sport);
                }
                else {
                    mgathering.setSID(sport);
                }
                break;

            case R.id.skill_lv_spinner:
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();
                if (toEdit) {
                    App.mCurrentGathering.setSkillLevel(App.mCurrentGathering.toSkillLevel(item));
                }
                else {
                    mgathering.setSkillLevel(mgathering.toSkillLevel(item));
                }
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.e(TAG, "onDestroy()");
        ButterKnife.unbind(this);
    }

    private void updateGathering() {
        if (!validateInputs()) {return;}
        App.mCurrentGathering.setDate(dateButton.getText().toString());
        App.mCurrentGathering.setGatheringTitle(mTitleField.getText().toString());
        App.mCurrentGathering.setDescription(mDescriptionField.getText().toString());
        App.mCurrentGathering.setLocation(mLocationField.getText().toString());
        App.mCurrentGathering.setTime(timeButton.getText().toString());
        App.mCurrentGathering.setSport(sportTypeSpinner.getSelectedItem().toString());
        App.mCurrentGathering.setSkillLevel(Gathering.toSkillLevel(skillLevelSpinner.getSelectedItem().toString()));
        App.mCurrentGathering.updateGathering();
        finish();
    }

    /**
     * Checks all inputs in the create a gathering form and lets the user know
     * if any inputs need to be filled out or changed.
     * @return true if all inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        boolean validInput = true; // Flag to indicate whether or not all inputs are valid
        String date = dateButton.getText().toString();
        String time =  timeButton.getText().toString();

        /* If date and time have not been selected display a toast */
        if (date.equals("DATE")
                || time.equals("TIME")) {
            Toast.makeText(this, "Please select Date and Time", Toast.LENGTH_SHORT).show();
            validInput = false;
        }

        /*
         * If the user selected date and time are before the current datetime,
         * notify the user
         */
        else {
            try {
                // Create a format with the user's timezone to parse the dates
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yy hh:mm");
                sdf.setTimeZone(TimeZone.getDefault());

                // Parse the user entered datetime
                Date userDate = sdf.parse(date.concat(" ").concat(time));

                // Get the current datetime
                Date currentDate = new Date();

                Log.v(TAG, userDate.toString());
                Log.v(TAG, currentDate.toString());

                // Notify the user
                if (userDate.before(currentDate)) {
                    Toast.makeText(this, "Select a valid date and time", Toast.LENGTH_SHORT).show();
                    validInput = false;
                }
            }
            catch(ParseException ex){
                Log.e(TAG, "COULD NOT PARSE");
            }
        }

        /* Checks for empty title and too long title */
        if(mTitleField.getText().length() == 0) {
            mTitleField.setError("Please fill out title");
            validInput = false;
        }
        else if(mTitleField.getText().length() > 50) {
            mTitleField.setError("Title must be less than 50 characters");
            validInput = false;
        }

        /* Checks for no description and too long description */
        if(mDescriptionField.getText().length() == 0) {
            mDescriptionField.setError("Please fill out description");
            validInput = false;
        }
        else if(mDescriptionField.getText().length() > 300) {
            mDescriptionField.setError("Description must be less than 300 characters");
            validInput = false;
        }

        /* Checks for no location and too long location */
        if(mLocationField.getText().length() == 0) {
            mLocationField.setError("Please fill out location");
            validInput = false;
        }
        else if(mLocationField.getText().length() > 100) {
            mLocationField.setError("Location must be lass than 100 characters");
            validInput = false;
        }

        return validInput;
    }

    private void submitGathering() {

        if (!validateInputs()) { return;}
        Firebase postID = new Firebase(Constants.FIREBASE_URL).child("Gatherings");

        /*Gets user's UID*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        /*Writes to myGathering list */
        mCurrentUser = prefs.getString(Constants.KEY_UID, "");
        Firebase sportRef = postID.push();
        Firebase myGatheringsID = new Firebase(Constants.FIREBASE_URL_MY_GATHERINGS).child(mCurrentUser).child("myGatherings");
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put(sportRef.getKey(), sportRef.getKey());
        myGatheringsID.updateChildren(updates);

        /*Writes the gathering to databse*/
        mgathering.setID(sportRef.getKey());
        mgathering.setGatheringTitle(mTitleField.getText().toString());
        mgathering.setDescription(mDescriptionField.getText().toString());
        mgathering.setLocation(mLocationField.getText().toString());
        mgathering.addAttendee(mCurrentUser);
        //mgathering.addPending(mCurrentUser);
        mgathering.setDate(mDateString);
        mgathering.setTime(mTimeString);
        mgathering.setDayOfWeek(mDayOfWeek);

        mgathering.setSport(sportTypeSpinner.getSelectedItem().toString());
        mgathering.setSkillLevel(Gathering.toSkillLevel(skillLevelSpinner.getSelectedItem().toString()));
        sportRef.setValue(mgathering);
        finish();
    }

    /**
     * Set date, to be called from DatePickerDialog
     * @param newDate
     */
    public void setDateString(String newDate, int dayOfWeek) {
        mDateString = newDate;
        mDayOfWeek = dayOfWeek;
        Log.d(TAG, "date set to: " + mDateString);
    }

    /**
     * Set date, to be called from TimePickerDialog
     * @param mTimeString
     */
    public void setmTimeString(String mTimeString) {
        this.mTimeString = mTimeString;
    }

    @Override
    public void update(Observable observable, Object data) {

        skillLevelSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> skillLevelAdapter = ArrayAdapter.createFromResource(this.getApplicationContext(), R.array.skill_lv_array, R.layout.spinner_style);
        skillLevelAdapter.setDropDownViewResource(R.layout.spinner_style);
        skillLevelSpinner.setAdapter(skillLevelAdapter);


        sportTypeSpinner.setOnItemSelectedListener(this);
        ArrayList<String> sport_types = new ArrayList<String>();
        for (SportType sport_type : mDataBaseSports.sportTypes) {
            sport_types.add(sport_type.getName());
        }

        Collections.sort(sport_types);

        ArrayAdapter<String> sportTypeListAdapter = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.spinner_style, sport_types);
        Log.e(TAG, "Sportype Size " + sport_types.size());
        sportTypeListAdapter.setDropDownViewResource(R.layout.spinner_style);
        sportTypeSpinner.setAdapter(sportTypeListAdapter);

        Intent intent = getIntent();
        toEdit = intent.getBooleanExtra("Edit", false);
        if (toEdit) {
            setTitle("Edit Event");
            mTitleField.setText(App.mCurrentGathering.getGatheringTitle());
            mDescriptionField.setText(App.mCurrentGathering.getDescription());
            mLocationField.setText(App.mCurrentGathering.getLocation());
            //Set date and time box

            dateButton = (Button) findViewById(R.id.datepicker);
            dateButton.setText(App.mCurrentGathering.getmDate());

            timeButton = (Button) findViewById(R.id.timepicker);
            timeButton.setText((App.mCurrentGathering.getTime()));

            int sportspinnerPosition = sportTypeListAdapter.getPosition(App.mCurrentGathering.getSID());
            sportTypeSpinner.setSelection(sportspinnerPosition);

            int skillLevelPosition = skillLevelAdapter.getPosition(App.mCurrentGathering.getSkillLevel().toString());
            skillLevelSpinner.setSelection(skillLevelPosition);

        } else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            m_hostID = prefs.getString(Constants.KEY_UID, "");
            mgathering = new Gathering();
            mgathering.setHostID(m_hostID);
        }

    }
}
