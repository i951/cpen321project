package com.example.findyourpeers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Button;
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

public class CreateProfile extends AppCompatActivity {

    private EditText displayName;
    private Spinner coopSpinner, yearSpinner;
    private Button registerButton;
    public String displayNameStr, coopStatus, yearStanding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        displayName = (EditText) findViewById(R.id.display_name_input);
        
        coopSpinner = (Spinner) findViewById(R.id.spinner_coop);
        ArrayAdapter<CharSequence> coopAdapter = ArrayAdapter.createFromResource(this, R.array.coop_status, android.R.layout.simple_spinner_item);
        coopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coopSpinner.setAdapter(coopAdapter);
        coopSpinner.setOnItemSelectedListener(new CoopSpinnerClass());

        yearSpinner = (Spinner) findViewById(R.id.spinner_yearstanding);
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.year_standing, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setOnItemSelectedListener(new YearSpinnerClass());

        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNameStr = displayName.getText().toString();
                if (displayNameStr.isEmpty() && coopStatus.isEmpty() && yearStanding.isEmpty()) {
                    Toast.makeText(CreateProfile.this, "Please enter both the values", Toast.LENGTH_SHORT).show();
                    return;
                }
                // calling a method to post the data and passing our name and job.
                String userID = postDataUsingVolley(displayNameStr, coopStatus, yearStanding);
                Intent displayProfileIntent = new Intent(CreateProfile.this, ProfilePage.class);
                displayProfileIntent.putExtra("userID", userID);
                startActivity(displayProfileIntent);
            }
        });

    }

    private class CoopSpinnerClass implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            coopStatus = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class YearSpinnerClass implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            yearStanding = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private String postDataUsingVolley(String name, String coop, String yearstand) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        UUID uuid=UUID.randomUUID();
        String uuidstr = uuid.toString();
        JSONObject newprofile = new JSONObject();
        try {
            //input your API parameters
            newprofile.put("displayName",name);
            newprofile.put("userID",uuidstr);
            newprofile.put("coopStatus", coop);
            newprofile.put("yearStanding", yearstand);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = "http://10.0.2.2:8081/createprofile";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, newprofile,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(CreateProfile.this, "Profile created", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CreateProfile.this, "Sign up failed", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
        return uuidstr;
    }

}