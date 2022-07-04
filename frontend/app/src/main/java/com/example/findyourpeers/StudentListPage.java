package com.example.findyourpeers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StudentListPage extends AppCompatActivity {

    String displayName, userID;
    public LinearLayout layoutStudentButton;
    TextView titleCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list_page);

        Intent intentStudentList = getIntent();
        String coursename = intentStudentList.getExtras().getString("coursename");
        layoutStudentButton= findViewById(R.id.layout_student_list);
        String coursenameNoSpace = coursename.replaceAll(" ", "");
        titleCourse = findViewById(R.id.student_list_title);
        titleCourse.setText(coursename);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String urlStudentList = "http://10.0.2.2:8081/getstudentlist/"+coursenameNoSpace;

        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlStudentList,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Do something with response
                        //mTextView.setText(response.toString());

                        // Process the JSON
                        try{
                            // Get current json object

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject otherstudent = response.getJSONObject(i);

                                // Get the current student (json object) data
                                displayName = otherstudent.getString("displayName");
                                userID = otherstudent.getString("userID");
                                addStudentButton(displayName, userID);
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Toast.makeText(StudentListPage.this, "Something went wrong in getting data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);

    }

    private void addStudentButton(String displayName, String userID) {
        final View studentButtonView = getLayoutInflater().inflate(R.layout.studentname_button_layout,null,false);

        TextView studentName = (TextView) studentButtonView.findViewById(R.id.text_username);
        studentName.setText(displayName);
        studentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewStudentIntent = new Intent(StudentListPage.this, ViewOtherProfile.class);
                viewStudentIntent.putExtra("userID", userID);
                startActivity(viewStudentIntent);
            }
        });

        layoutStudentButton.addView(studentButtonView);

    }
}