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
import org.w3c.dom.Text;

import java.util.ArrayList;

public class ProfilePage extends AppCompatActivity {

    private TextView displaynameTV, coopTV, yearTV;
    private Button findCourseBtn;
    private String displayname, yearstanding, coopstatus;
    public LinearLayout layoutCourseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        Intent intentProfile = getIntent();
        String userID = intentProfile.getExtras().getString("userID");
        layoutCourseButton = findViewById(R.id.layout_button_list);

        displaynameTV = findViewById(R.id.textView_displayname);
        coopTV = findViewById(R.id.textview_coop);
        yearTV = findViewById(R.id.textView_yearstanding);


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String urltest = "http://10.0.2.2:8081/getuserprofile/"+userID;

        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urltest,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Do something with response
                        //mTextView.setText(response.toString());

                        // Process the JSON
                        try{
                                // Get current json object
                                JSONObject student = response.getJSONObject(0);

                                // Get the current student (json object) data
                                displayname = student.getString("displayName");
                                coopstatus = student.getString("coopStatus");
                                yearstanding = student.getString("yearStanding");
                                JSONArray coursesJSONArray= student.getJSONArray("courselist");
                                //ArrayList<Object> courseArrayList = new ArrayList<Object>();

                                if (coursesJSONArray != null) {
                                    for (int i=0;i<coursesJSONArray.length();i++){
                                    //courseArrayList.add(coursesJSONArray.getString(i));
                                        String courseNameSingle = coursesJSONArray.getString(i);
                                        addCourseButton(courseNameSingle, userID);
                                    }
                                }

                            // Display the formatted json data in text view
                                displaynameTV.setText("Display name: " + displayname);

                                if(coopstatus.equals("Yes")){
                                    coopTV.setText("I am in Co-op");
                                }else{
                                    coopTV.setText("I am not in Co-op, studying only");
                                }
                                yearTV.setText("I am in year " + yearstanding);


                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Toast.makeText(ProfilePage.this, "Something went wrong in getting data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);

        findCourseBtn = (Button) findViewById(R.id.find_course_button);
        findCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browseCourseIntent = new Intent(ProfilePage.this, BrowseCourse.class);
                browseCourseIntent.putExtra("userID", userID);
                browseCourseIntent.putExtra("displayName", displayname);
                startActivity(browseCourseIntent);
            }
        });
    }

    private void addCourseButton(String courseNameSingle, String userID) {
        final View courseButtonView = getLayoutInflater().inflate(R.layout.coursename_buttons_layout,null,false);

        Button courseBtn = (Button)courseButtonView.findViewById(R.id.coursename_button);
        courseBtn.setText(courseNameSingle);
        courseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewStudentIntent = new Intent(ProfilePage.this, StudentListPage.class);
                viewStudentIntent.putExtra("coursename", courseNameSingle);
                startActivity(viewStudentIntent);
            }
        });

        ImageView delCourseBtn = (ImageView)courseButtonView.findViewById(R.id.delete_course_button);
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
        String urlUserToCourse = "http://10.0.2.2:8081/deleteuserfromcourse"+"/"+userID+"/"+coursenameNoSpace;

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, urlUserToCourse,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(ProfilePage.this, "user deleted from course", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
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
            courseDelete.put("coursename",courseNameSingle);
            courseDelete.put("userID",userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "http://10.0.2.2:8081/deletecoursefromuser";
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
}