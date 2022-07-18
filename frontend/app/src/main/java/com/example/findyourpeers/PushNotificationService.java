package com.example.findyourpeers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.findyourpeers.CreateProfile;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;



public class PushNotificationService extends FirebaseMessagingService {

    final static String TAG = "uuidThingy";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        //make the api call to update the token for the user
        String uuidString = CreateProfile.usernameStr.toString();
//        Log.d(TAG, "phad lo token " + token + " " + uuidString);
        postDatausingVolley(uuidString, token);


    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

    }

    private void postDatausingVolley(String uuidString, String token) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        PushNotificationService.krleTest("lauda");

        JSONObject newToken = new JSONObject();
        try {
            //input your API parameters
            newToken.put("userID", uuidString);
            newToken.put("registrationToken", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = "http://10.0.2.2:3010/newRegistrationToken";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, newToken,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Toast.makeText(CreateProfile.this, "Profile created", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "successfully updated token for firebase");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(CreateProfile.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "unsuccessfully updated token for firebase");
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

}



