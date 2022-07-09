package com.example.findyourpeers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class SignUp extends AppCompatActivity {

    private EditText emailSU, usernameSU, passwordSU;
    private String emailStr, usernameStr, passwordStr;
    private Button getCodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailSU = (EditText) findViewById(R.id.email_address_signup);
        usernameSU = (EditText) findViewById(R.id.username_signup);
        passwordSU = (EditText) findViewById(R.id.password_signup);

        getCodeBtn = findViewById(R.id.verify_email_button);
        getCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailStr = emailSU.getText().toString();
                usernameStr = usernameSU.getText().toString();
                passwordStr = passwordSU.getText().toString();

                if (emailStr.isEmpty() || usernameStr.isEmpty() || passwordStr.isEmpty()) {
                    Toast.makeText(SignUp.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                postSignUpData(emailStr, usernameStr, passwordStr);

            }
        });


    }

    private void postSignUpData(String emailStr, String usernameStr, String passwordStr) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject signUpData = new JSONObject();
        try {
            //input your API parameters
            signUpData.put("email",emailStr);
            signUpData.put("password",passwordStr);
            signUpData.put("username", usernameStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = "http://10.0.2.2:3010/signup";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, signUpData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(SignUp.this, "Credentials sent successfully", Toast.LENGTH_SHORT).show();
                        Intent enterCodeIntent = new Intent(SignUp.this, EnterCode.class);
                        enterCodeIntent.putExtra("email",emailStr);
                        enterCodeIntent.putExtra("password",passwordStr);
                        enterCodeIntent.putExtra("username", usernameStr);
                        startActivity(enterCodeIntent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignUp.this, "Sending credentials failed", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}