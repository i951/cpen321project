package com.example.findyourpeers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.ArrayList;

public class BlockedUsersPage extends AppCompatActivity {

    final String TAG = "BlockedUsersPage";
    String currentUserDisplayName;
    String currentUserID;
    public LinearLayout layoutStudentButton;
    ArrayList<String> blockedUsers;
    ArrayList<String> blockedUserNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_users_page);

        Intent thisIntent = getIntent();
        blockedUsers = (ArrayList<String>) thisIntent.getSerializableExtra("blockedUsers");
        blockedUserNames = (ArrayList<String>) thisIntent.
                getSerializableExtra("blockedUserNames");

        layoutStudentButton = findViewById(R.id.layout_student_list);

        for (int i = 0; i < blockedUserNames.size(); i++) {
            String nextBlockedUserID = blockedUsers.get(i);
            String nextBlockedUserName = blockedUserNames.get(i);
            Log.d(TAG, "nextBlockedUserName: " + nextBlockedUserName);

            addStudentButton(nextBlockedUserName, nextBlockedUserID);
        }
    }

    private void addStudentButton(String displayName, String userID) {
        final View studentButtonView = getLayoutInflater()
                .inflate(R.layout.studentname_button_layout, null, false);

        TextView studentName = (TextView) studentButtonView.findViewById(R.id.text_username);
        studentName.setText(displayName);
        studentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewStudentIntent = new Intent(BlockedUsersPage.this,
                        ViewOtherProfile.class);
                viewStudentIntent.putExtra("currentUserID", currentUserID);
                viewStudentIntent.putExtra("userID", userID);
                viewStudentIntent.putExtra("currentUserDisplayName", currentUserDisplayName);
                startActivity(viewStudentIntent);
            }
        });
        layoutStudentButton.addView(studentButtonView);
    }

}