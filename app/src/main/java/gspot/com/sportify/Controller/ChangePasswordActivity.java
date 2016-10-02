package gspot.com.sportify.Controller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gspot.com.sportify.R;
import gspot.com.sportify.utils.Constants;

/**
 * Created by Anshul and Armin on 5/8/2016.
 *
 * This class defines the implementation and functionality of the change
 * password page. The class uses a Firebase instance to use Firebase's built-in
 * change password functionality in which it takes the user's entered password
 * and saves it as the new password. There is a check to ensure the password is
 * a certain length and that the user correctly typed their new password twice.
 * This is normally called using an Intent.
 */
public class ChangePasswordActivity extends Activity {
    private static final String TAG = ChangePasswordActivity.class.getSimpleName();

    /* A reference to the Firebase */
    private Firebase mFirebaseRef;

    @Bind(R.id.newPassword)
    EditText mNewPwdText;
    @Bind(R.id.rePassword)
    EditText mRePwdText;
    @Bind(R.id.btn_change_password)
    Button mChangePwdButton;


    /* mEmail: will hold the email passed in with the intent
     * mTempPwd: will hold the temporary password from the form
     * mNewPwd: will hold the new password from the form
     * */
    String mEmail, mTempPwd, mNewPwd, mRePwd;

    /* onClick()
     * Annotation listener for the change password button
     * Once the button is clicked the changePassword() is called
     * to change the password associated with the user's email
     * */
    @OnClick(R.id.btn_change_password)
    void onClick(Button button) { changePassword(); }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate()");

        setContentView(R.layout.activity_change_password);

        /*link the widgets to the members*/
        ButterKnife.bind(this);

    } //end onCreate

    /* changePassword()
     * Utility method that handles the password switch from the temporary
     * password to a new, user-specified password. Uses Firebases's built-in
     * methods to do so.
     * */
    private void changePassword() {
        Log.i(TAG, "in changePassword()");

        // Get the Firebase reference
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        // Disable the button
        mChangePwdButton.setEnabled(false);

        // Get the email and temporary password passed by the intent
        Bundle bundle = getIntent().getExtras();
        mEmail = bundle.getString("Email");
        mTempPwd = bundle.getString("TempPwd");

        // Get the Temporary and New passwords
        mNewPwd = mNewPwdText.getText().toString();
        mRePwd = mRePwdText.getText().toString();

        /* Ensure the new password is the correct length */
        if (mNewPwd.isEmpty() || mNewPwd.length() < 4 || mNewPwd.length() > 15) {
            mNewPwdText.setError("Between 4 and 15 alphanumeric characters");
            mChangePwdButton.setEnabled(true);
        }//end if

        /* Ensure the entered passwords match */
        else if (!mNewPwd.equals(mRePwd)) {
            mRePwdText.setError("Passwords must match");
            mChangePwdButton.setEnabled(true);
        }//end else if

        /* If passwords are good, attempt to change them */
        else {
            mNewPwdText.setError(null);

            // Call Firebase's changePassword method
            mFirebaseRef.changePassword(mEmail, mTempPwd, mNewPwd,
                    new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    // Let the user know that the password was changed
                    Toast.makeText(getApplicationContext(),
                            "Password changed successfully!",
                            Toast.LENGTH_LONG).show();
                    finish();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    /* Check for errors */
                    switch(firebaseError.getCode()){
                        /*
                         * Add any more errors here as they become evident in
                         * testing
                         */
                        case FirebaseError.NETWORK_ERROR:
                            Toast.makeText(getApplicationContext(),
                                    "Cannot connect to internet",
                                    Toast.LENGTH_LONG).show();
                            break;
                    }

                    // Re-enable the button
                    mChangePwdButton.setEnabled(true);
                }
            });
        }//end else
    }



}
