package com.example.findyourpeers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

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

public class ProfilePage extends AppCompatActivity {

    private TextView displaynameTV;
    private TextView coopTV;
    private TextView yearTV;
    private String displayname;
    public LinearLayout layoutCourseButton;
    ArrayList<String> blockedUserNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        Intent intentProfile = getIntent();
        String userID = intentProfile.getExtras().getString("userID");
        ArrayList<String> courseListAL = new ArrayList<>();
        ArrayList<String> blockedUsers = new ArrayList<>();
        blockedUserNames = new ArrayList<>();

        layoutCourseButton = findViewById(R.id.layout_button_list);

        displaynameTV = findViewById(R.id.textView_displayname);
        coopTV = findViewById(R.id.textview_coop);
        yearTV = findViewById(R.id.textView_yearstanding);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String urltest = "http://10.0.2.2:3010/getuserprofile/" + userID;

        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urltest,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Do something with response
                        //mTextView.setText(response.toString());

                        // Process the JSON
                        try {
                            // Get current json object
                            JSONObject student = response.getJSONObject(0);

                            // Get the current student (json object) data
                            displayname = student.getString("displayName");
                            String coopstatus = student.getString("coopStatus");
                            String yearstanding = student.getString("yearStanding");
                            JSONArray coursesJSONArray = student.getJSONArray("courselist");
                            JSONArray blockedUsersJSONArray = student.getJSONArray("blockedUsers");

                            //ArrayList<Object> courseArrayList = new ArrayList<Object>();

                            if (coursesJSONArray != null) {
                                for (int i = 0; i < coursesJSONArray.length(); i++) {
                                    //courseArrayList.add(coursesJSONArray.getString(i));
                                    String courseNameSingle = coursesJSONArray.getString(i);
                                    addCourseButton(courseNameSingle, userID);
                                    courseListAL.add(courseNameSingle);
                                }
                            }

                            for (int i = 0; i < blockedUsersJSONArray.length(); i++) {
                                String nextBlockedUserID = blockedUsersJSONArray.getString(i);
                                blockedUsers.add(nextBlockedUserID);
                                makeDisplayNameGetRequest(requestQueue, nextBlockedUserID);

                            }

                            // Display the formatted json data in text view
                            displaynameTV.setText("Display name: " + displayname);

                            if (coopstatus.equals("Yes")) {
                                coopTV.setText("I am in Co-op");
                            } else {
                                coopTV.setText("I am not in Co-op, studying only");
                            }
                            yearTV.setText("I am in year " + yearstanding);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something when error occurred
                        Toast.makeText(ProfilePage.this, "Something went wrong in getting data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);

        Button seeBlockedUsersBtn = (Button) findViewById(R.id.blocked_users_button);
        seeBlockedUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent seeBlockedUsersIntent = new Intent(ProfilePage.this, BlockedUsersPage.class);
                seeBlockedUsersIntent.putExtra("userID", userID);
                seeBlockedUsersIntent.putExtra("displayName", displayname);
                seeBlockedUsersIntent.putExtra("blockedUsers", blockedUsers);
                seeBlockedUsersIntent.putExtra("blockedUserNames", blockedUserNames);
                startActivity(seeBlockedUsersIntent);
            }
        });

        Button findCourseBtn = (Button) findViewById(R.id.find_course_button);
        findCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browseCourseIntent = new Intent(ProfilePage.this, BrowseCourse.class);
                browseCourseIntent.putExtra("userID", userID);
                browseCourseIntent.putExtra("displayName", displayname);
                browseCourseIntent.putExtra("courselist", courseListAL);
                startActivity(browseCourseIntent);
            }
        });
    }

    private void addCourseButton(String courseNameSingle, String userID) {
        final View courseButtonView = getLayoutInflater().inflate(R.layout.coursename_buttons_layout, null, false);

        Button courseBtn = (Button) courseButtonView.findViewById(R.id.coursename_button);
        courseBtn.setText(courseNameSingle);
        courseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewStudentIntent = new Intent(ProfilePage.this, StudentListPage.class);
                viewStudentIntent.putExtra("currentUserID", userID);
                viewStudentIntent.putExtra("coursename", courseNameSingle);
                viewStudentIntent.putExtra("displayname", displayname);
                startActivity(viewStudentIntent);
            }
        });

        ImageView chatBtn = (ImageView) courseButtonView.findViewById(R.id.group_chat_button);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(ProfilePage.this, ChatActivity.class);
                chatIntent.putExtra("coursename", courseNameSingle); //groupid
                chatIntent.putExtra("userID", userID); //userID
                chatIntent.putExtra("displayname", displayname); //nickname
                startActivity(chatIntent);

            }
        });

        ImageView delCourseBtn = (ImageView) courseButtonView.findViewById(R.id.delete_course_button);
        delCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCoursefromUser(courseNameSingle, userID);
                deleteUserfromCourse(courseNameSingle, userID);
                removeView(courseButtonView);
            }
        });

        layoutCourseButton.addView(courseButtonView);
    }

    private void removeView(View courseButtonView) {
        layoutCourseButton.removeView(courseButtonView);
    }

    private void deleteUserfromCourse(String courseNameSingle, String userID) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String coursenameNoSpace = courseNameSingle.replaceAll(" ", "");

        // Enter the correct url for your api service site
        String urlUserToCourse = "http://10.0.2.2:3010/deleteuserfromcourse" + "/" + userID + "/" + coursenameNoSpace;

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, urlUserToCourse,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(ProfilePage.this, "user deleted from course", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.
                        Toast.makeText(ProfilePage.this, "Unable to delete user from course", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(deleteRequest);
    }

    private void deleteCoursefromUser(String courseNameSingle, String userID) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject courseDelete = new JSONObject();
        try {
            //input your API parameters
            courseDelete.put("coursename", courseNameSingle);
            courseDelete.put("userID", userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "http://10.0.2.2:3010/deletecoursefromuser";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, courseDelete,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ProfilePage.this, "Course deleted from user", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfilePage.this, "Unable to delete course from user", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    private void makeDisplayNameGetRequest(RequestQueue requestQueue, String userID) {
        String requestUrl = "http://10.0.2.2:3010/getDisplayNameByUserID/" + userID;
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
                Log.d("ProfilePage", " Retrieved display name: " +
                        retrievedDisplayName);
                blockedUserNames.add(retrievedDisplayName);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(BrowseCourse.this, "Something went wrong in getting data", Toast.LENGTH_SHORT).show();
                //String body;
                //get status code here
                //String statusCode = String.valueOf(error.networkResponse.statusCode);
                //get response body and parse with appropriate encoding
                if (error.networkResponse != null) {
                    try {
                        String body = new String(error.networkResponse.data, "UTF-8");
                        Log.d("ProfilePage error: ", body);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        );
        requestQueue.add(displayNameRequest);
    }
}