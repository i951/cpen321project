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

public class EnterCode extends AppCompatActivity {

    private EditText verifCode;
    private String verifCodeStr, emailStr, usernameStr;
    private Button verifCodeBtn, resendCodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);

        Intent intentCode = getIntent();
        usernameStr = intentCode.getExtras().getString("username");
        emailStr = intentCode.getExtras().getString("email");

        verifCode = (EditText) findViewById(R.id.verification_code);

        verifCodeBtn = findViewById(R.id.verify_code_button);
        verifCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifCodeStr = verifCode.getText().toString();
                if (verifCodeStr.isEmpty()) {
                    Toast.makeText(EnterCode.this, "Please enter verification code", Toast.LENGTH_SHORT).show();
                    return;
                }
                postVerifCodeData(verifCodeStr, usernameStr);
            }
        });

        resendCodeBtn = findViewById(R.id.resend_code_button);
        resendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendConfirmationCode(usernameStr);
            }
        });


    }

    private void resendConfirmationCode(String usernameStr) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject resendData = new JSONObject();
        try {
            //input your API parameters
            resendData.put("username", usernameStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = "http://10.0.2.2:3010/resendconfirmationcode";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, resendData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(EnterCode.this, "Verification code sent", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EnterCode.this, "Verification code false", Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void postVerifCodeData(String verifCodeStr, String usernameStr) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject verifData = new JSONObject();
        try {
            //input your API parameters
            verifData.put("username", usernameStr);
            verifData.put("confirmationCode",verifCodeStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = "http://10.0.2.2:3010/confirmsignup";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, verifData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(EnterCode.this, "Verification code sent", Toast.LENGTH_SHORT).show();
                        Intent enterCodeIntent = new Intent(EnterCode.this, CreateProfile.class);
                        enterCodeIntent.putExtra("email",emailStr);
                        enterCodeIntent.putExtra("username", usernameStr);
                        startActivity(enterCodeIntent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EnterCode.this, "Verification code false", Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonObjectRequest);

    }
}