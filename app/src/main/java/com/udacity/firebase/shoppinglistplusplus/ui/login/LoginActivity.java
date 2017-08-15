package com.udacity.firebase.shoppinglistplusplus.ui.login;

/**
 * Created by Elias Myronidis on 13/8/17.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.udacity.firebase.shoppinglistplusplus.R;
import com.udacity.firebase.shoppinglistplusplus.ui.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.udacity.firebase.shoppinglistplusplus.ui.MainActivity;

/**
 * Represents Sign in screen and functionality of the app
 */
public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;
    private EditText mEditTextEmailInput, mEditTextPasswordInput;

    /**
     * Variables related to Google Login
     */
    /* A flag indicating that a PendingIntent is in progress and prevents us from starting further intents. */
    private boolean mGoogleIntentInProgress;

    /* Request code used to invoke sign in user interactions for Google+ */
    public static final int RC_SIGN_IN = 1;

    /* A Google account object that is populated if the user signs in with Google */
    GoogleSignInAccount mGoogleAccount;

    GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         * Link layout elements from XML and setup progress dialog
         */
        initializeScreen();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = mAuth.getCurrentUser();

                if (mUser != null) {
                    Log.d(TAG, "onAuthStateChanged: Signed in" + mUser.getUid());

                    Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mIntent);
                    finish();

                } else {
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        /**
         * Call signInPassword() when user taps "Done" keyboard action
         */
        mEditTextPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    signInWithEmailAndPassword(mEditTextEmailInput.getText().toString(), mEditTextPasswordInput.getText().toString());
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    /**
     * Override onCreateOptionsMenu to inflate nothing
     *
     * @param menu The menu with which nothing will happen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    /**
     * Sign in with Password provider when user clicks sign in button
     */
    public void onSignInPressed(View view) {
        String email = mEditTextEmailInput.getText().toString();
        String password = mEditTextPasswordInput.getText().toString();

        if (isEmailValid(email) && isPasswordValid(password))
            signInWithEmailAndPassword(email, password);
    }

    /**
     * Open CreateAccountActivity when user taps on "Sign up" TextView
     */
    public void onSignUpPressed(View view) {
        Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
        startActivity(intent);
    }

    /**
     * Link layout elements from XML and setup the progress dialog
     */
    public void initializeScreen() {
        mEditTextEmailInput = (EditText) findViewById(R.id.edit_text_email);
        mEditTextPasswordInput = (EditText) findViewById(R.id.edit_text_password);
        LinearLayout linearLayoutLoginActivity = (LinearLayout) findViewById(R.id.linear_layout_login_activity);
        initializeBackground(linearLayoutLoginActivity);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_authenticating_with_firebase));
        mAuthProgressDialog.setCancelable(false);

        /* Setup Google Sign In */
        setupGoogleSignIn();
    }

    /**
     * Sign in with Email and Password provider (used when user taps "Done" action on keyboard)
     */
    public void signInWithEmailAndPassword(String email, String password) {

        mAuthProgressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Sign in successful.");
                            mAuthProgressDialog.dismiss();
                            mUser = mAuth.getCurrentUser();

                            // Go to MainActivity
                            Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mIntent);
                            finish();
                        } else {
                            mAuthProgressDialog.dismiss();

                            try {
                                throw task.getException();
                            } catch (FirebaseAuthException e) {
                                if (e.getErrorCode().equals("ERROR_USER_NOT_FOUND")) {
                                    showErrorToast("There is no user corresponding to the given email.");
                                } else if (e.getErrorCode().equals("ERROR_WRONG_PASSWORD")) {
                                    showErrorToast("The password is not correct.");
                                }
                                Log.d(TAG, "onComplete: " + e.getErrorCode());
                            } catch (FirebaseNetworkException e) {
                                showErrorToast("Please check your internet connection.");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private boolean isEmailValid(String email) {

        if (email.equals("")) {
            mEditTextEmailInput.setError("The email can't be empty.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEditTextEmailInput.setError("The email format is not valid.");
            return false;
        }

        return true;
    }

    private boolean isPasswordValid(String password) {
        if (!password.trim().isEmpty()) {
            return true;
        }

        mEditTextPasswordInput.setError("The password should not be empty.");
        return false;
    }


    /**
     * Show error toast to users
     */
    private void showErrorToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }


    /**
     * GOOGLE SIGN IN CODE
     * <p>
     * This code is mostly boiler plate from
     * https://developers.google.com/identity/sign-in/android/start-integrating
     * and
     * https://github.com/googlesamples/google-services/blob/master/android/signin/app/src/main/java/com/google/samples/quickstart/signin/SignInActivity.java
     * <p>
     * The big picture steps are:
     * 1. User clicks the sign in with Google button
     * 2. An intent is started for sign in.
     * - If the connection fails it is caught in the onConnectionFailed callback
     * - If it finishes, onActivityResult is called with the correct request code.
     * 3. If the sign in was successful, set the mGoogleAccount to the current account and
     * then call get GoogleOAuthTokenAndLogin
     * 4. getGoogleOAuthTokenAndLogin launches an AsyncTask to get an OAuth2 token from Google.
     * 5. Once this token is retrieved it is available to you in the onPostExecute method of
     * the AsyncTask. **This is the token required by Firebase**
     */


    /* Sets up the Google Sign In Button : https://developers.google.com/android/reference/com/google/android/gms/common/SignInButton */
    private void setupGoogleSignIn() {
        SignInButton signInButton = (SignInButton) findViewById(R.id.login_with_google);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInGooglePressed(v);
            }
        });
    }

    /**
     * Sign in with Google plus when user clicks "Sign in with Google" textView (button)
     */
    public void onSignInGooglePressed(View view) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        mAuthProgressDialog.show();
    }

    /**
     * This callback is triggered when any startActivityForResult finishes. The requestCode maps to
     * the value passed into startActivityForResult.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...); */
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                mGoogleAccount = result.getSignInAccount();
                firebaseAuthWithGoogle(mGoogleAccount);
                mAuthProgressDialog.dismiss();
            } else {
                if (result.getStatus().getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    showErrorToast("The sign in was cancelled. Make sure you're connected to the internet and try again.");
                } else {
                    showErrorToast("Error handling the sign in: " + result.getStatus().getStatusMessage());
                }
                mAuthProgressDialog.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuthProgressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent mIntent = new Intent(LoginActivity.this, MainActivity.class);
                            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mIntent);
                            finish();
                        } else {
                            mAuthProgressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        /**
         * An unresolvable error has occurred and Google APIs (including Sign-In) will not
         * be available.
         */
        mAuthProgressDialog.dismiss();
        showErrorToast(result.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}