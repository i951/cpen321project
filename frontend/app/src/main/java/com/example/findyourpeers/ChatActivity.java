package com.example.findyourpeers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ChatActivity extends AppCompatActivity {
    private Socket socket;
    private String Nickname;
    private String groupID;

    public RecyclerView myRecyclerView;
    public List<Message> MessageList;
    public ChatBoxAdapter chatBoxAdapter;
    public EditText messageTxt;
    public Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Nickname = getIntent().getExtras().getString("displayname");
        groupID = getIntent().getExtras().getString("coursename");
        String userID = getIntent().getExtras().getString("userID");

        //https://medium.com/@mohamedaymen.ourabi11/creating-a-realtime-chat-app-with-android-nodejs-and-socket-io-1050bc20c70

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

        // https://stackoverflow.com/questions/34102741/recyclerview-not-scrolling-to-end-when-keyboard-opens
//        if (Build.VERSION.SDK_INT >= 11) {
//            myRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                @Override
//                public void onLayoutChange(View v,
//                                           int left, int top, int right, int bottom,
//                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                    if (bottom < oldBottom) {
//                        myRecyclerView.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (myRecyclerView.getAdapter().getItemCount() > 1)
//                                myRecyclerView.smoothScrollToPosition(
//                                        myRecyclerView.getAdapter().getItemCount() - 1);
//                            }
//                        }, 100);
//                    }
//                }
//            });
//        }

        String serverIP = "10.0.2.2";

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + serverIP + ":3010/getConversationByGroupID/" + groupID;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
//                        Log.d("ChatActivity", "Response: " + response);
                                JSONArray msgsArray = new JSONArray();
                                try {
                                    msgsArray = response.getJSONArray("retrievedMsgs");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                for (int i = 0; i < msgsArray.length(); i++) {
                                    try {
                                        JSONObject msg = msgsArray.getJSONObject(i);
//                                Log.d("ChatActivity", "msg: " + msg);

                                        String nickname = msg.getString("senderName");
                                        String message = msg.getString("messageContent");
//                                Log.d("ChatActivity", "nickname: " + nickname);
                                        Log.d("ChatActivity", "message: " + message);

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
                        Log.d("ChatActivity", "Volley request error");
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

        // connect socket client to the server
        try {
            socket = IO.socket("http://" + serverIP + ":3010");

            socket.connect();

//            socket.emit("joinGroupChat", groupID, Nickname);
            socket.emit("joinGroupChat", groupID, userID);
            Log.d("ChatActivity", "Joining group chat: " + groupID);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d("ChatActivity", "Error connect to socket");
        }

        // send message action
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageTxt.getText().toString().isEmpty()){
                    Toast.makeText(ChatActivity.this,
                            "Cannot send empty message", Toast.LENGTH_SHORT).show();
                } else {
                    socket.emit("groupMessage",
                            groupID,
                            Nickname,
                            messageTxt.getText().toString());

                    messageTxt.setText("");

                    Log.d("ChatActivity", "Message emitted to server");
                }
            }
        });

        socket.on("groupMessage", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            String nickname = data.getString("senderNickname");
                            String message = data.getString("message");

                            Message m = new Message(nickname, message);
                            MessageList.add(m);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // add the new updated list to the adapter
                        // notify the adapter to update the recycler view
                        // set the adapter for the recycler view
                        chatBoxAdapter = new ChatBoxAdapter(MessageList);
                        chatBoxAdapter.notifyDataSetChanged();
                        myRecyclerView.setAdapter(chatBoxAdapter);
                    }
                });
            }
        });
    }
}