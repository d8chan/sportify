package gspot.com.sportify.Controller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gspot.com.sportify.Model.MyGatherings;
import gspot.com.sportify.Model.Profile;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.Constants;

/**
 * The controller for the sign up page. Handles creating a new user and validating
 * the email. There will be a temporary password for the user to change the first
 * time they log in. If the user uses a valid email, it creates a default profile
 * for them and stores it in firebase. The activity returns the activity that
 * called it.
 */
public class SignupActivity extends AppCompatActivity {
    private static final String TAG = SignupActivity.class.getSimpleName();

    /* A reference to the Firebase */
    private Firebase mFirebaseRef;

    /* Data from the authenticated User */
    private AuthData mAuthData;

    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;

    /*link to the widgets*/
    @Bind(R.id.input_email) EditText mEmailText;
    //@Bind(R.id.input_password) EditText mPasswordText;
    @Bind(R.id.btn_signup) Button mSignupButton;
    @Bind(R.id.link_login) TextView mSigninText;
    @Bind(R.id.input_name) EditText mNameText;

    /*Holds user info*/
    private String name, email, password;

    /* onClick()
    * Annotation listener for the signup button
    * Once the button is clicked the signup() is called
    * to create an account for the User
    * */
    @OnClick(R.id.btn_signup)
    void onClick(View v) { signup(); }

    /* onClick()
    * Annotation listener for the login link
    * closes the activity and goes back to the
    * signin page
    * */
    @OnClick(R.id.link_login)
    void onClick() { finish(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate()");

        setContentView(R.layout.activity_signup);

        /*link the widgets to the members*/
        ButterKnife.bind(this);

    } //end onCreate


    /*
    * singup()
    * utility function to sing the User with the app and save their
    * information in our database*/
    private void signup() {
        Log.i(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        } //end if

        mSignupButton.setEnabled(false);  //disable the button

        /*Progress dialog*/
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        /* Store temporary password as password, will prompt user later */
        password = "T3mpP@ssword";

        // TODO: Implement your signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    } //end signup


    /* onSignupSuccess
     * If account creation is successful then we will return back to the caller activity.
     * If there was some issue creating the account, do not return back, let the
     * Handler dismiss the progress dialog.
     * */
    public void onSignupSuccess() {

        Log.e(TAG, "onSignupSuccess");
        mSignupButton.setEnabled(true);


        /*Create user in database*/
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);
        mFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            // Success for Create User
            @Override
            public void onSuccess(Map<String, Object> result) {

                //store the information in firebase web
                Profile profile = new Profile(name, (String) result.get("uid"));
                MyGatherings gatheringList = new MyGatherings((String)result.get("uid"));
                mFirebaseRef.child("profiles").child((String)result.get("uid")).setValue(profile);
                mFirebaseRef.child("MyGatherings").child((String)result.get("uid")).setValue(gatheringList);
                //mFirebaseRef.child("myEvents").child((String) result.get("uid"));

                /*store the users uid in shared preferences so we know who they are */
//                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SignupActivity.this);
//                SharedPreferences.Editor spe = sp.edit();
//

                /*
                 * Send email with temporary password, repurposing Firebase's
                 * resetPassword
                 */
                mFirebaseRef.resetPassword(email, new Firebase.ResultHandler() {
                    //Success for Reset Password
                    @Override
                    public void onSuccess() {
                        // Let the user know that the email was sent
                        Toast.makeText(getApplicationContext(),
                                "Sent Email with Password",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                    // Error for Reset Password
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Log.v(TAG, String.valueOf(FirebaseError.NETWORK_ERROR));
                    }
                });

                //this is how we pass data back to the function onActivityForResult
                //setResult(RESULT_OK, null);
                //terminate activity

                finish();
            }

            //Error for Create User
            @Override
            public void onError(FirebaseError firebaseError) {

                Log.e(TAG,"ERROR THROWN WHEN CREATING USER");
                /* Check for errors */
                switch(firebaseError.getCode()){
                    /*
                     * Add more errors here as the become evident through
                     * testing
                     */
                    case FirebaseError.EMAIL_TAKEN:
                        Toast.makeText(getApplicationContext(),
                                "That email is taken by another user",
                                Toast.LENGTH_LONG).show();
                        break;
                    case FirebaseError.NETWORK_ERROR:
                        Toast.makeText(getApplicationContext(),
                                "Cannot connect to internet",
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }//end onSignupSuccess

    /*
     * onSignupFailed()
     * notify the user their signup failed
     * */
    public void onSignupFailed() {
        Log.i(TAG, "onSignupFailed");
        Toast.makeText(getBaseContext(), "Sign Up failed", Toast.LENGTH_LONG).show();

        mSignupButton.setEnabled(true); //enable the button for input
    } //end onSignupFailed


    /*
    * validate()x
    * This utility function w   ill check and make sure that the
    * User has entered the correct email and password syntax
    * */
    private boolean validate() {
        Log.i(TAG, "validate");
        boolean valid = true;

        /* get the User input, and store into variables */
        name = mNameText.getText().toString();
        email = mEmailText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            mNameText.setError("at least 3 characters");
            valid = false;
        } //end if
        else {
            mNameText.setError(null);
        }//end else

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailText.setError("enter a valid email address");
            valid = false;
        } //end if
        else {
            mEmailText.setError(null);
        } //end else

        return valid;
    }//end validate()

    @Override
    public void onDestroy()
    {//Called when the view hierarchy associated with the Activity is being removed.
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        ButterKnife.unbind(this);
    }
}// end SignupActivity