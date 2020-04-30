package com.skteam.thesquad.loginregisteration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.skteam.thesquad.R;
import com.skteam.thesquad.common.Common;
import com.skteam.thesquad.home.HomeActivity;
import com.skteam.thesquad.retrofit.TheSquadApi;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private RelativeLayout relative_layout;
    private TextView loginusing_tv, register_tv;
    private RelativeLayout relative_phone_layout, login_relative_layout;
    private LinearLayout email_passwrd_layout;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private LoginButton loginButton;
    private FirebaseAuth mAuth;
    private EditText email, password;
    private ProgressDialog progressDialog;
    private TheSquadApi mService;
    private EditText phone_edittext;
    private CountryCodePicker ccp;
    private CardView google_sign_in;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1;
    private boolean isPhoneLayoutVisible = true;
    private boolean isEmailIdLayoutVisible = false;
    private FirebaseUser user;
    private ProgressDialog pd;
    private String refercode_owner_email;
    private int refercode_owner_wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initActions();

    }


    //========================All view ids initialize======================//
    private void initViews(){
        loginusing_tv = findViewById(R.id.loginusing_tv);
        relative_phone_layout = findViewById(R.id.relative_phone_layout);
        email_passwrd_layout = findViewById(R.id.email_paswrd_layout);
        register_tv = findViewById(R.id.register_tv);
        relative_layout = findViewById(R.id.relative_layout);
        phone_edittext = findViewById(R.id.phone_edittext);
        ccp = findViewById(R.id.ccp);
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        google_sign_in = findViewById(R.id.google_sign_in);
        progressDialog = new ProgressDialog(this);
        pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("please wait...");
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("please wait..");
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login_relative_layout = findViewById(R.id.login_relative_layout);
        mService = Common.getAPI();
        loginButton = findViewById(R.id.login_button);
    }
    //========================All view ids initialize======================//


    //===========================Actions on All Views======================//
    private void initActions(){
        loginusing_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPhoneLayoutVisible) {
                    loginusing_tv.setText("Login using mobile number");
                    relative_phone_layout.setVisibility(View.GONE);
                    email_passwrd_layout.setVisibility(View.VISIBLE);
                    isEmailIdLayoutVisible = true;
                    isPhoneLayoutVisible = false;
                } else if (isEmailIdLayoutVisible) {
                    loginusing_tv.setText("Login using email id");
                    relative_phone_layout.setVisibility(View.VISIBLE);
                    email_passwrd_layout.setVisibility(View.GONE);
                    isPhoneLayoutVisible = true;
                    isEmailIdLayoutVisible = false;
                }
            }
        });

        register_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivty.class);
                startActivity(intent);
                finish();
            }
        });

        login_relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmailIdLayoutVisible) {
                    loginUserUsingEmailPassword();

                } else if (isPhoneLayoutVisible) {
                    loginUsingNumber();

                }

            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        google_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        ////////

//Facebook Login /////

        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);


        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                pd.show();
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code

            }

            @Override
            public void onError(FacebookException exception) {
                // App code

            }
        });

        findViewById(R.id.login_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });
    }
    //===========================Actions on All Views======================//



    //============================Login using phone number firebase otp verifcation================//
    private void loginUsingNumber() {

        final String str_number = "+" + ccp.getSelectedCountryCode() + phone_edittext.getText().toString().trim();
        final String str_ccp = "+" + ccp.getSelectedCountryCode();
        final String str_num = phone_edittext.getText().toString().trim();

        if (TextUtils.isEmpty(str_num)) {
            phone_edittext.setError("Number required");
            phone_edittext.requestFocus();
        } else if (str_num.length() != 10) {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            mService.checkUserExists(str_number, "randomnonexistemail").enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();

                    assert user != null;
                    if (TextUtils.isEmpty(user.getError_msg())) {
                        progressDialog.dismiss();
                        Snackbar.make(relative_layout, "User not found", Snackbar.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
                        Common.isPhoneLogin = true;
                        intent.putExtra("number", str_number);
                        intent.putExtra("country_code", str_ccp);
                        intent.putExtra("num", str_num);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }
    //============================Login using phone number================//



    //========================== Log in via email and password mysql===========================//
    private void loginUserUsingEmailPassword() {
        String str_email = email.getText().toString().trim();
        String str_password = password.getText().toString().trim();

        if (TextUtils.isEmpty(str_email)) {
            email.setError("email required");
            email.requestFocus();
        } else if (TextUtils.isEmpty(str_password)) {
            password.setError("password required");
            password.requestFocus();
        } else {

            progressDialog.setTitle("Logging in");
            progressDialog.setMessage("please wait");
            progressDialog.show();

            mService.userLogin(str_email, str_password).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.body().isError()) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();

                        Snackbar
                                .make(relative_layout, "email and password mismatch please try again", Snackbar.LENGTH_LONG)
                                .show();


                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.d("LOGINTEST", "onFailure: " + t.getMessage());

                }
            });
        }


    }
    //========================== Log in via email and password mysql===========================//



    //=====================Handle Facebook login checking if fb email exist in databse or not=================//
    private void handleFacebookToken(final AccessToken accessToken) {
        final AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    loginUSingPhoneIfUserNotExist(task.getResult().getUser().getEmail());

                } else {
                    Toast.makeText(LoginActivity.this, "Failed : -" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
    //=====================Handle Facebook login checkif fb email exist in databse or not=================//


    //Facebook Login /////


    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Log.d("REGISTERACTIVTYYYY", "onStart: user already signed in with google");
        } else {
            Log.d("REGISTERACTIVTYYYY", "onStart: user not signed in with google");
        }
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                pd.show();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("REGISTERACTIVITYY", "Google sign in failed", e);
                // ...
            }

            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("REGISTERACTIVTYYY", "handleSignInResult: " + account.getDisplayName() + "_EMail " + account.getEmail());

            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("REGISTERACTIVTYYY", "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }


    //======================= Firebase Google Signin ================//
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loginUSingPhoneIfUserNotExist(acct.getEmail());

                        } else {
                            // If sign in fails, display a message to the user.

                        }


                    }
                });
    }
    //======================= Firebase Google Signin ================//



//==================If login email not exist open Registeration bottom sheeet==============//
    private void loginUSingPhoneIfUserNotExist(final String user_email) {
        mService.checkUserExists("randomnumber1125455125", user_email).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!TextUtils.isEmpty(response.body().getError_msg())) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {

                    try {

                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(LoginActivity.this, R.style.BottomSheetDialogTheme);
                        View bottomSheetView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.bottom_sheet_phone_layout, (LinearLayout) findViewById(R.id.bottomSheetContainer));
                        final CountryCodePicker ccp = bottomSheetView.findViewById(R.id.ccp);
                        final EditText phone = bottomSheetView.findViewById(R.id.phone_edittext);
                        final EditText signup_code = bottomSheetView.findViewById(R.id.signup_code);
                        final TextView apply_bottomsheet = bottomSheetView.findViewById(R.id.apply);


                        bottomSheetView.findViewById(R.id.signup_layout).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String str_number = "+" + ccp.getSelectedCountryCode() + phone.getText().toString().trim();
                                final String str_ccp = ccp.getSelectedCountryCode();
                                final String str_num = phone.getText().toString();
                                if (TextUtils.isEmpty(phone.getText().toString())) {
                                    phone.setError("Mobile number required");
                                    phone.requestFocus();
                                } else if (phone.getText().toString().length() != 10) {
                                    phone.setError("please enter 10 digit valid number");
                                    phone.requestFocus();
                                } else {
                                    pd.show();

                                    mService.checkUserExists(str_number, "randomnonexistemail").enqueue(new Callback<User>() {
                                        @Override
                                        public void onResponse(Call<User> call, Response<User> response) {
                                            User user1 = response.body();
                                            if (!TextUtils.isEmpty(user1.getError_msg())) {
                                                pd.dismiss();
                                                Toast.makeText(LoginActivity.this, user1.getError_msg(), Toast.LENGTH_SHORT).show();
                                            } else {

                                                Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
                                                intent.putExtra("country_code", str_ccp);
                                                intent.putExtra("num", str_num);
                                                intent.putExtra("number", str_number);
                                                intent.putExtra("email", user_email);
                                                intent.putExtra("password", "defaultpasswordgenrator123");
                                                intent.putExtra("refercode_owner_email", refercode_owner_email);
                                                intent.putExtra("refercode_owner_wallet", refercode_owner_wallet);
                                                Common.isNewPhoneLogin = true;
                                                pd.dismiss();
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<User> call, Throwable t) {
                                            pd.dismiss();
                                            Toast.makeText(LoginActivity.this, "Failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }

                            }
                        });
                        bottomSheetView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bottomSheetDialog.dismiss();
                            }
                        });

                        bottomSheetView.findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkPromoCode(signup_code, apply_bottomsheet);
                            }
                        });
                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.setCancelable(false);
                        pd.dismiss();
                        bottomSheetDialog.show();


                    } catch (WindowManager.BadTokenException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
//==================If login email not exist open Registeration bottom sheeet==============//



    //=================== user applied Promo code checking code============================//
    private void checkPromoCode(EditText signup_code, final TextView apply) {
        String str_signup_code = signup_code.getText().toString().trim();
        if (TextUtils.isEmpty(str_signup_code) || str_signup_code.length() != 10) {
            signup_code.setError("Invalid code");
            signup_code.requestFocus();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("validating sign up code");
            progressDialog.show();
            mService.checkExistsPromoCode(str_signup_code).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (TextUtils.isEmpty(response.body().getError_msg())) {
                        apply.setText("applied");
                        apply.setEnabled(false);

                        refercode_owner_email = response.body().getEmail();
                        refercode_owner_wallet = response.body().getWallet();
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Sign up code applied", Toast.LENGTH_SHORT).show();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, response.body().getError_msg(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }
    //=================== Promo code checking code============================//

}
