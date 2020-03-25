package com.skteam.thesquad.loginregisteration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.skteam.thesquad.MainActivity;
import com.skteam.thesquad.R;
import com.skteam.thesquad.common.Common;
import com.skteam.thesquad.retrofit.TheSquadApi;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "OTPACTIVTYTEST";
    private ImageView go_back;
    private TextView number_tv;
    private FirebaseAuth firebaseAuth;
    private PinView otpCodeBox;
    private String verificationId;
    private TheSquadApi mService;
    private Button submit;
    private PhoneAuthProvider.ForceResendingToken token;
    private ProgressDialog pd;
    private int user_wallet;

    private String user_number, user_email, user_password, user_ccp, user_num, refercode_owner_email;
    private int refercode_owner_wallet , updatedRefercode_owner_wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        go_back = findViewById(R.id.go_back);
        number_tv = findViewById(R.id.number_tv);
        mService = Common.getAPI();
//otp
        firebaseAuth = FirebaseAuth.getInstance();
        submit = findViewById(R.id.button);
        otpCodeBox = findViewById(R.id.pinView);
        pd = new ProgressDialog(this);
        pd.setTitle("Otp Sent");
        pd.setMessage("please wait for automatic detect");
        pd.show();
        //getting user info from register activty
        try {
            Bundle bundle = getIntent().getExtras();
            user_email = bundle.getString("email");
            user_ccp = bundle.getString("country_code");
            user_number = bundle.getString("number");
            user_num = bundle.getString("num");
            user_password = bundle.getString("password");
            refercode_owner_email = bundle.getString("refercode_owner_email");
            refercode_owner_wallet = bundle.getInt("refercode_owner_wallet");

            requestOTP(user_number);
            number_tv.setText(user_ccp + "-" + user_num);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        go_back.setOnClickListener(this);
        submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go_back:
                finish();
                break;
            case R.id.button:
                String otp = otpCodeBox.getText().toString();
                if (!TextUtils.isEmpty(otp) && otp.length() == 6) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                    verifiyOtp(credential);
                } else {
                    pd.dismiss();
                    Toast.makeText(OtpActivity.this, "please enter valid otp", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }


    private void requestOTP(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                token = forceResendingToken;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(OtpActivity.this, "Otp Expired, re-request otp", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                verifiyOtp(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(OtpActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }


    private void verifiyOtp(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    pd.dismiss();
                    final ProgressDialog progressDialog = new ProgressDialog(OtpActivity.this);
//                    progressDialog.setTitle("Registerarion Done");
                    progressDialog.setMessage("please wait");
                    progressDialog.show();
                    Toast.makeText(OtpActivity.this, "Otp Verified", Toast.LENGTH_SHORT).show();
//|| Common.isNewPhoneLogin
                    if (Common.isNewPhoneLogin || !Common.isPhoneLogin) {
                     String genratedUserReferalCode=   RandomString.getAlphaNumericString(5) + user_num.substring(user_num.length()-5);


                     if (refercode_owner_email != null){
                          user_wallet = 150;
                     }else {
                         user_wallet = 0;
                     }
                        Log.d(TAG, "onResponse: " + refercode_owner_wallet + " " + refercode_owner_email + " " + user_email+ " " +user_wallet+ " " +user_number + " " +user_password);
                        mService.registerNewUser(user_number, user_email, user_password,genratedUserReferalCode,user_wallet).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.isSuccessful()) {
                                    if (TextUtils.isEmpty(response.body().getError_msg())) {
                                        progressDialog.dismiss();
                                        final ProgressDialog progressDialog1 = new ProgressDialog(OtpActivity.this);
                                        progressDialog1.setMessage("please wait..");
                                        progressDialog1.show();

                                        if (refercode_owner_email != null){

                                            updatedRefercode_owner_wallet = refercode_owner_wallet + 100;
                                            mService.creditReferalAmount(refercode_owner_email,updatedRefercode_owner_wallet).enqueue(new Callback<User>() {
                                                @Override
                                                public void onResponse(Call<User> call, Response<User> response) {
                                                    if (!response.body().isError()){
                                                        Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                    }else {
                                                        Toast.makeText(OtpActivity.this, "Failed ", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<User> call, Throwable t) {
                                                    progressDialog1.dismiss();
                                                    Toast.makeText(OtpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                        }else {
                                            Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }





                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(OtpActivity.this, response.body().getError_msg(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                pd.dismiss();

                            }
                        });
                    } else {
                        Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                        startActivity(intent);
                    }


//                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            storeUserData();
//                            ProgressDialog pd = new ProgressDialog(OtpActivity.this);
//                            pd.setMessage("please wait");
//                            pd.show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(OtpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });


                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(OtpActivity.this, "failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
