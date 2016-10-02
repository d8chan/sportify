package gspot.com.sportify.Controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
 * This class defines the implementation and functionality of the forgot
 * password page. The class uses a Firebase instance to use Firebase's built-in
 * forgot password functionality in which it takes the entered email and, after
 * ensuring the email is in the database, emails the user with a temporary
 * login password.
 * This is normally called using an Intent.
 */
public class ForgotPasswordActivity extends AppCompatActivity{
    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    /* Code for when the User requests forgot password */
    //private static final int REQUEST_CHANGE = 0;

    /* A reference to the Firebase */
    private Firebase mFirebaseRef;

    /*link to the widgets*/
    @Bind(R.id.input_email_fpwd)
    EditText mEmailText;
    @Bind(R.id.btn_forgot_password_email)
    Button mForgotPwdButton;
    @Bind(R.id.link_remembered_password)
    TextView mSigninText;

    /* Will hold the email from the form */
    String mEmail;

    /* onClick()
     * Annotation listener for the signup button
     * Once the button is clicked the signup() is called
     * to create an account for the User
     * */
    @OnClick(R.id.btn_forgot_password_email)
    void onClick(Button button) { sendEmail(); }

    /* onClick()
    * Annotation listener for the return to login link
    * closes the activity and goes back to the
    * signin page
    * */
    @OnClick(R.id.link_remembered_password)
    void onClick() { finish(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate()");

        setContentView(R.layout.activity_forgot_password);

        /*link the widgets to the members*/
        ButterKnife.bind(this);

    } //end onCreate

    /* sendEmail()
     * utility function to send an email to the user with a randomly created
     * password that has replaced theirs
     * */
    private void sendEmail() {
        Log.i(TAG, "in sendEmail()");
        // Get the Firebase reference
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        // Disable the button
        mForgotPwdButton.setEnabled(false);

        // Get user provided email
        mEmail = mEmailText.getText().toString();

        // Ensure the email is of a valid format
        if (mEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            mEmailText.setError("Enter a valid email address");
            mForgotPwdButton.setEnabled(true);
        } //end if
        else {
            mEmailText.setError(null);
            // Call Firebase's resetPassword method
            mFirebaseRef.resetPassword(mEmail, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    // Let the user know that the email was sent
                    Toast.makeText(getApplicationContext(),
                            "Password reset email sent",
                            Toast.LENGTH_LONG).show();
                    finish();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    //Log.v(TAG, String.valueOf(FirebaseError.NETWORK_ERROR));

                    /* Check for errors */
                    switch(firebaseError.getCode()){
                        /*
                         * Add more errors here as the become evident through
                         * testing
                         */
                        case FirebaseError.USER_DOES_NOT_EXIST:
                            mEmailText.setError("No user is associated with this address");
                            break;
                        case FirebaseError.NETWORK_ERROR:
                            Toast.makeText(getApplicationContext(),
                                    "Cannot connect to internet",
                                    Toast.LENGTH_LONG).show();
                            break;
                    }

                    // Re-enable the button
                    mForgotPwdButton.setEnabled(true);
                }
            });
        } //end else

    }
}
