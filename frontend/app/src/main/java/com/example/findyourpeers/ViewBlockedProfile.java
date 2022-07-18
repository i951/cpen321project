package com.example.findyourpeers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewBlockedProfile extends AppCompatActivity {
    final String TAG = "ViewBlockedProfile";

    private TextView otherDisplayNameTV, otherCoopTV, otherYearTV;
    private String otherDisplayName;
    private int isBlocked = 0;
    String userID, currentUserID, currentUserDisplayName;
    ArrayList<String> blockedUsers;
    HashMap<String, String> blockedUserNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blocked_profile);

        Intent intentProfileOther = getIntent();
        userID = intentProfileOther.getExtras().getString("userID");
        currentUserID = intentProfileOther.getExtras().getString("currentUserID");
        currentUserDisplayName = intentProfileOther.getExtras()
                .getString("currentUserDisplayName");

        blockedUsers = new ArrayList<>();
        blockedUserNames = new HashMap<String, String>();

        otherDisplayNameTV = findViewById(R.id.other_display_name);
        otherCoopTV = findViewById(R.id.other_coop_status);
        otherYearTV = findViewById(R.id.other_year_standing);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String urlOther = "http://10.0.2.2:3010/getuserprofile/" + userID;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlOther,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject student = response.getJSONObject(0);

                            otherDisplayName = student.getString("displayName");
                            String othercoopstatus = student.getString("coopStatus");
                            String otheryearstanding = student.getString("yearStanding");
                            JSONArray blockedUsersJSONArray = student.getJSONArray("blockedUsers");
                            // check if this other user has blocked the current user
                            for (int i = 0; i < blockedUsersJSONArray.length(); i++) {
                                if (blockedUsersJSONArray.getString(i).equals(currentUserID)) {
                                    isBlocked = 1; // true
                                }
                            }

                            otherDisplayNameTV.setText(otherDisplayName);

                            if (othercoopstatus.equals("Yes")) {
                                otherCoopTV.setText("I am in Co-op");
                            } else {
                                otherCoopTV.setText("I am not in Co-op, studying only");
                            }
                            otherYearTV.setText("I am in year " + otheryearstanding);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ViewBlockedProfile.this,
                                "Something went wrong in getting data", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);

        Button messageButton = findViewById(R.id.button_message);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privateChatIntent = new Intent(ViewBlockedProfile.this,
                        PrivateChatActivity.class);
                Log.d(TAG, "currentUserDisplayName: " + currentUserDisplayName);
                Log.d(TAG, "otherDisplayName: " + otherDisplayName);
                if (currentUserDisplayName.equals(otherDisplayName)) {
                    Toast.makeText(ViewBlockedProfile.this, "You cannot message yourself",
                            Toast.LENGTH_SHORT).show();
                } else {
                    privateChatIntent.putExtra("senderName", currentUserDisplayName);
                    privateChatIntent.putExtra("receiverName", otherDisplayName);
                    privateChatIntent.putExtra("isBlocked", isBlocked);
                    privateChatIntent.putExtra("currentUserID", currentUserID);
                    privateChatIntent.putExtra("userID", userID);

                    startActivity(privateChatIntent);
                }
            }
        });

        Button unblockButton = findViewById(R.id.button_unblock);
        unblockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://10.0.2.2:3010/unblock/" + currentUserID + "/" + userID;

                StringRequest unblockRequest = new StringRequest(Request.Method.DELETE, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(ViewBlockedProfile.this,
                                        "User unblocked", Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ViewBlockedProfile.this,
                                        "Unable to unblock user", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                requestQueue.add(unblockRequest);
            }
        });
    }
}