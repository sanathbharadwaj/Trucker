package com.harsha.trucker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        TextView textl = (TextView) findViewById(R.id.log_acc);

        // Capture button clicks
        textl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start NewActivity.class
                Intent intent = new Intent(RegistrationActivity.this,
                        LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onSubmit(View view)
    {
        showToast("Verifying phone number");
        phoneAuthenticate();
    }


    void registerUser(){
        ParseUser user = new ParseUser();
        user.setUsername(getEditText(R.id.username).getText().toString());
        user.setPassword(getEditText(R.id.password).getText().toString());
        user.setEmail(getEditText(R.id.email_add).getText().toString());
        user.put("name",getEditText(R.id.name_field).getText().toString());
        user.put("phone", getEditText(R.id.phone_number).getText().toString());
        user.put("isDriver", false);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null)
                {
                    //TODO: Handling failures
                    showToast("Registration error");
                    return;
                }
                showToast("Registration successful");
                loadToMapsActivity();

            }
        });
    }

    void phoneAuthenticate()
    {
        String phoneNumber = getEditText(R.id.phone_number).getText().toString();
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                showToast("Verification successful");
                registerUser();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    showToast("Automatic verification failed");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    showToast("Automatic verification failed");
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later

                // ...

            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                20,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    void loadToMapsActivity()
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }


    EditText getEditText(int id)
    {
        return (EditText)findViewById(id);
    }

    void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
