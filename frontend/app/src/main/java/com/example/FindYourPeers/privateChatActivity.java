package com.example.findyourpeers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class privateChatActivity extends AppCompatActivity {
    private Socket socket;
    // enter Nickname = senderr for testing
    private String senderName;

//    private String receiverName = "receiverr";

    // 1 = receiver has blocked sender
    private int isBlocked;// = 0;

    public RecyclerView myRecyclerView;
    public List<Message> MessageList;
    public ChatBoxAdapter chatBoxAdapter;
    public EditText messageTxt;
    public Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //https://medium.com/@mohamedaymen.ourabi11/creating-a-realtime-chat-app-with-android-nodejs-and-socket-io-1050bc20c70

        Intent privateChatIntent = getIntent();
        isBlocked = privateChatIntent.getExtras().getInt("isBlocked");

        String receiverName = privateChatIntent.getExtras().getString("receiverName");
        senderName = getIntent().getExtras().
                getString("senderName");
        Log.d("privateChatActivity", "senderName: " + senderName);

        Log.d("privateChatActivity", "receiverName: " + receiverName);


        messageTxt = (EditText) findViewById(R.id.message) ;
        send = (Button)findViewById(R.id.send);

        MessageList = new ArrayList<>();
        myRecyclerView = (RecyclerView) findViewById(R.id.messagelist);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        myRecyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager.setStackFromEnd(true);
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myRecyclerView.setAdapter(chatBoxAdapter);

        // actual name to be gotten from users module
        // sender name, change
//        senderName = CreateProfileActivity.strNicknameToSave;


        String groupID = "newGroupID";

        // wsl ip address
//        String serverIP = "172.30.179.102";
        // local ip address
//        String serverIP = "192.168.1.72";
        String serverIP = "10.0.2.2";

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
//        String url = "http://172.30.179.102:3010/getConversationByGroupID/" + groupID;
        String url = "http://" + serverIP + ":3010/getPrivateConversationByUserNames/"
                + senderName + "/" + receiverName;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                JSONArray msgsArray = new JSONArray();
                                try {
                                    msgsArray = response.getJSONArray("retrievedMsgs");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                for (int i = 0; i < msgsArray.length(); i++) {
                                    try {
                                        JSONObject msg = msgsArray.getJSONObject(i);

                                        String nickname = msg.getString("senderName");
                                        String message = msg.getString("messageContent");
                                        Log.d("privateChatActivity", "message: " + message);

                                        Message m = new Message(nickname, message);
                                        MessageList.add(m);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                // notify adapter dataset changed
                                chatBoxAdapter = new ChatBoxAdapter(MessageList);
                                chatBoxAdapter.notifyDataSetChanged();
                                myRecyclerView.setAdapter(chatBoxAdapter);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("privateChatActivity", "Volley request error");
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

        // connect socket client to the server
        try {
            socket = IO.socket("http://" + serverIP + ":3010");

            socket.connect();

            socket.emit("joinPrivateChat", senderName);
            Log.d("privateChatActivity", "Joining private chat: " + senderName);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d("privateChatActivity", "Error connect to socket");
        }

        // send message action
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageTxt.getText().toString().isEmpty()){
                    Toast.makeText(privateChatActivity.this,
                            "Cannot send empty message", Toast.LENGTH_SHORT).show();
                } else if (isBlocked == 1){
                    Toast.makeText(privateChatActivity.this,
                            "Blocked", Toast.LENGTH_SHORT).show();
                } else {
                    socket.emit("privateMessage",
                            senderName,
                            receiverName,
                            messageTxt.getText().toString(),
                            isBlocked);

                    String nickname = senderName;
                    String message = messageTxt.getText().toString();

                    Message m = new Message(nickname, message);
                    MessageList.add(m);

                    chatBoxAdapter = new ChatBoxAdapter(MessageList);
                    chatBoxAdapter.notifyDataSetChanged();
                    myRecyclerView.setAdapter(chatBoxAdapter);

                    messageTxt.setText("");

                    Log.d("privateChatActivity", "Message emitted to server");
                }
            }
        });

        socket.on("privateMessage", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            String nickname = data.getString("senderNickname");

                            // only show the message if we're currently talking to that person
                            if (nickname.equals(receiverName) ) {
                                String message = data.getString("message");

                                Message m = new Message(nickname, message);
                                MessageList.add(m);

                                // add the new updated list to the adapter
                                // notify the adapter to update the recycler view
                                // set the adapter for the recycler view
                                chatBoxAdapter = new ChatBoxAdapter(MessageList);
                                chatBoxAdapter.notifyDataSetChanged();
                                myRecyclerView.setAdapter(chatBoxAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}