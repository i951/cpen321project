package com.example.findyourpeers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class BlockedUsersPage extends AppCompatActivity {

    final String TAG = "BlockedUsersPage";
    public LinearLayout layoutStudentButton;
    String currentUserDisplayName;
    String currentUserID;
    ArrayList<String> blockedUserIDs;
    HashMap<String, String> blockedUserNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_users_page);

        Intent thisIntent = getIntent();
        currentUserID = thisIntent.getExtras().getString("userID");
        currentUserDisplayName = thisIntent.getExtras().getString("displayName");
        blockedUserIDs = new ArrayList<>();
        blockedUserNames = new HashMap<>();

        layoutStudentButton = findViewById(R.id.layout_student_list);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String urltest = "http://10.0.2.2:3010/getuserprofile/" + currentUserID;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urltest,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject student = response.getJSONObject(0);
                            JSONArray blockedUsersJSONArray =
                                    student.getJSONArray("blockedUsers");
                            RequestQueue requestQueue2 =
                                    Volley.newRequestQueue(BlockedUsersPage.this);

                            for (int i = 0; i < blockedUsersJSONArray.length(); i++) {
                                String nextBlockedUserID = blockedUsersJSONArray.getString(i);
                                Log.d(TAG, "nextBlockedUserID: " + nextBlockedUserID);
                                blockedUserIDs.add(nextBlockedUserID);
                                makeDisplayNameGetRequest(requestQueue2, nextBlockedUserID);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BlockedUsersPage.this,
                                "Something went wrong in getting data", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void addStudentButton(String displayName, String userID) {
        final View studentButtonView = getLayoutInflater()
                .inflate(R.layout.studentname_button_layout, null, false);

        TextView studentName = (TextView) studentButtonView.findViewById(R.id.text_username);
        studentName.setText(displayName);
        studentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewBlockedProfileIntent = new Intent(BlockedUsersPage.this,
                        ViewBlockedProfile.class);
                viewBlockedProfileIntent.putExtra("currentUserID", currentUserID);
                viewBlockedProfileIntent.putExtra("userID", userID);
                viewBlockedProfileIntent.putExtra("currentUserDisplayName", currentUserDisplayName);
                startActivity(viewBlockedProfileIntent);
            }
        });
        layoutStudentButton.addView(studentButtonView);
    }

    private void makeDisplayNameGetRequest(RequestQueue requestQueue, String thisUserID) {
        String requestUrl = "http://10.0.2.2:3010/getDisplayNameByUserID/" + thisUserID;
        JsonObjectRequest displayNameRequest = new JsonObjectRequest(Request.Method.GET, requestUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String retrievedDisplayName = null;
                try {
                    retrievedDisplayName = response.getString("retrievedDisplayName");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Retrieved display name: " + retrievedDisplayName);
                blockedUserNames.put(thisUserID, retrievedDisplayName);
                addStudentButton(retrievedDisplayName, thisUserID);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    Log.d(TAG, "Error: " + body);
                }
            }
        }
        );
        requestQueue.add(displayNameRequest);
    }

}