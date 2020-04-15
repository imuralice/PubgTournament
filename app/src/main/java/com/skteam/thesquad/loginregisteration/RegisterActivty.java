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
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.skteam.thesquad.MainActivity;
import com.skteam.thesquad.R;
import com.skteam.thesquad.common.Common;
import com.skteam.thesquad.home.HomeActivity;
import com.skteam.thesquad.retrofit.TheSquadApi;
import com.skteam.thesquad.startscreen.ImageSliderActivity;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivty extends AppCompatActivity {
    private RelativeLayout relativeLayout;
private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private LoginButton loginButton;
    private FirebaseAuth mAuth;
    private TextView login_tv;
    private CardView google_sign_in;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1;
    private ProgressDialog progressDialog;
    private RelativeLayout signup_layout;
    private TheSquadApi mService;
    private String TAG = "REGISTERSCREENTEST";
    private CountryCodePicker ccp;
    private FirebaseUser user;
    private ProgressDialog pd;
    private EditText number , email, password , signup_code;
    private TextView apply;
    private String refercode_owner_email;
    private int refercode_owner_wallet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activty);
initViews();
        initActions();


    }

    //--------------------------- All views ids initialize here =================//
    private void initViews(){
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        login_tv = findViewById(R.id.login_tv);
        google_sign_in = findViewById(R.id.google_sign_in);
        signup_layout = findViewById(R.id.signup_layout);
        ccp = findViewById(R.id.ccp);
        number = findViewById(R.id.number);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signup_code = findViewById(R.id.signup_code);
        relativeLayout = findViewById(R.id.relative_layout);
        apply = findViewById(R.id.apply);

        mService = Common.getAPI();
        progressDialog = new ProgressDialog(this);
        pd = new ProgressDialog(RegisterActivty.this);
        pd.setMessage("please wait...");
        loginButton =  findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);

    }


    //========================= Actions on all views =====================//
    private void initActions(){
//Google sign in
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
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
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String check_signup_code = signup_code.getText().toString().trim();
                checkPromoCode(check_signup_code,signup_code,apply);
            }
        });

        ////////

//Facebook Login /////

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
        login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivty.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signup_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        findViewById(R.id.login_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });
    }


    //============================ Checking user applied promo code for validation======================//
    private void checkPromoCode(String str_signup_code, EditText signup_code, final TextView apply) {
    if (TextUtils.isEmpty(str_signup_code) || str_signup_code.length() != 10){
        signup_code.setError("Invalid code");
        signup_code.requestFocus();
    }else {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("validating sign up code");
        progressDialog.show();
        mService.checkExistsPromoCode(str_signup_code).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (TextUtils.isEmpty(response.body().getError_msg())){
                    apply.setText("applied");
                    apply.setEnabled(false);

                    refercode_owner_email = response.body().getEmail();
                    refercode_owner_wallet =  response.body().getWallet();
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivty.this, "Sign up code applied", Toast.LENGTH_SHORT).show();

                }else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivty.this, response.body().getError_msg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivty.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    }


    //============================== Register User=========================//
    private void registerUser() {
    final String str_number = "+" + ccp.getSelectedCountryCode() + number.getText().toString().trim();
    final String str_num = number.getText().toString().trim();
       final String str_ccp = "+" + ccp.getSelectedCountryCode();
    final String str_email = email.getText().toString().trim();
        final String str_password = password.getText().toString().trim();
        String str_signup_code = signup_code.getText().toString().trim();

        if (TextUtils.isEmpty(str_num)){
            number.setError("number required");
            number.requestFocus();
        } else  if (str_num.length() != 10){
            number.setError("enter valid 10 digit number");
            number.requestFocus();
        } else
        if (TextUtils.isEmpty(str_email)){
            email.setError("email required");
            email.requestFocus();
        } else
        if (TextUtils.isEmpty(str_password)){
            password.setError("password required");
            password.requestFocus();
        } else{
            Log.d(TAG, "registerUser: str_number " +str_number);
            Log.d(TAG, "registerUser: str_email " +str_email);
            Log.d(TAG, "registerUser: str_password " +str_password);
            progressDialog.setTitle("Registering you");
            progressDialog.setMessage("please wait");
            progressDialog.show();

            mService.checkUserExists(str_number,str_email).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()){
                        User user = response.body();
                        if (TextUtils.isEmpty(user.getError_msg())){
                            Intent intent = new Intent(RegisterActivty.this,OtpActivity.class);
                            intent.putExtra("country_code",str_ccp);
                            intent.putExtra("num",str_num);
                            intent.putExtra("number",str_number);
                            intent.putExtra("email",str_email);
                            intent.putExtra("password",str_password);
                            intent.putExtra("refercode_owner_email",refercode_owner_email);
                            intent.putExtra("refercode_owner_wallet",refercode_owner_wallet);
                            Common.isPhoneLogin = false;
                            progressDialog.dismiss();
                            startActivity(intent);
                        }else {
                            progressDialog.dismiss();
                            Snackbar
                                    .make(relativeLayout, user.getError_msg(), Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
progressDialog.dismiss();
                    Log.d(TAG, "onFailure: "+t.getMessage());
                }
            });
//            mService.registerNewUser(str_number,str_email,str_password).enqueue(new Callback<User>() {
//                @Override
//                public void onResponse(Call<User> call, Response<User> response) {
//                    if (response.isSuccessful()){
//                        if (TextUtils.isEmpty(response.body().getError_msg())){
//                            progressDialog.dismiss();
//                            User user = response.body();
//                            Intent intent = new Intent(RegisterActivty.this,OtpActivity.class);
//                            intent.putExtra("number",user.getNumber());
//                            intent.putExtra("email",user.getEmail());
//                            intent.putExtra("password",user.getPassword());
//                            startActivity(intent);
//
//                            Log.d(TAG, "onResponse: " +response.body().getEmail());
//
//                        }else {
//                            progressDialog.dismiss();
//                            Toast.makeText(RegisterActivty.this, response.body().getError_msg(), Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<User> call, Throwable t) {
//                    progressDialog.dismiss();
//                    Log.d(TAG, "onFailure: "+t.getMessage());
//                }
//            });


        }



    }


    //====================== Handle Facebook After login======================//
    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    loginUSingPhoneIfUserNotExist(task.getResult().getUser().getEmail());
                }else {
                    Toast.makeText(RegisterActivty.this, "Failed : -" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }




    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
       if (account!= null){
           Log.d("REGISTERACTIVTYYYY", "onStart: user already signed in with google");
       }else {
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

                // ...
            }

            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("REGISTERACTIVTYYY", "handleSignInResult: "+account.getDisplayName() + "_EMail "+account.getEmail());

            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("REGISTERACTIVTYYY", "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }


    //===================== Firebase Google signin ======================//
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

                        }


                    }
                });
    }



// ========================= Open Registeration bottom sheet if google or fb email doesnt exist==================//
    private void loginUSingPhoneIfUserNotExist(final String user_email) {
        mService.checkUserExists("randomnumber1125455125", user_email).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!TextUtils.isEmpty(response.body().getError_msg())) {
                    Intent intent = new Intent(RegisterActivty.this, HomeActivity.class);
                    startActivity(intent);
                } else {

                    try {

                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(RegisterActivty.this, R.style.BottomSheetDialogTheme);
                        View bottomSheetView = LayoutInflater.from(RegisterActivty.this).inflate(R.layout.bottom_sheet_phone_layout, (LinearLayout) findViewById(R.id.bottomSheetContainer));
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
                                } else    if (phone.getText().toString().length() != 10) {
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
                                                Toast.makeText(RegisterActivty.this, user1.getError_msg(), Toast.LENGTH_SHORT).show();
                                            } else {

                                                Intent intent = new Intent(RegisterActivty.this, OtpActivity.class);
                                                intent.putExtra("country_code", str_ccp);
                                                intent.putExtra("num", str_num);
                                                intent.putExtra("number", str_number);
                                                intent.putExtra("email", user_email);
                                                intent.putExtra("password", "defaultpasswordgenrator123");
                                                intent.putExtra("refercode_owner_email",refercode_owner_email);
                                                intent.putExtra("refercode_owner_wallet",refercode_owner_wallet);
                                                Log.d(TAG, "onResponse: " + refercode_owner_wallet + refercode_owner_email);
                                                Common.isNewPhoneLogin = true;
                                                pd.dismiss();
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<User> call, Throwable t) {
                                            pd.dismiss();
                                            Toast.makeText(RegisterActivty.this, "Failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                                String check_signup_code = signup_code.getText().toString().trim();
                                checkPromoCode(check_signup_code,signup_code,apply_bottomsheet);
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
                Toast.makeText(RegisterActivty.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
