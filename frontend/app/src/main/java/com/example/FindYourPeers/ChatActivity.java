package com.example.FindYourPeers;

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
    private String Nickname ;

    public RecyclerView myRecylerView ;
    public List<Message> MessageList ;
    public ChatBoxAdapter chatBoxAdapter;
    public EditText messagetxt ;
    public Button send ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

//https://medium.com/@mohamedaymen.ourabi11/creating-a-realtime-chat-app-with-android-nodejs-and-socket-io-1050bc20c70

        messagetxt = (EditText) findViewById(R.id.message) ;
        send = (Button)findViewById(R.id.send);

        MessageList = new ArrayList<>();
        myRecylerView = (RecyclerView) findViewById(R.id.messagelist);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());
        myRecylerView.setAdapter(chatBoxAdapter);

        // actual name to be gotten from users module
        Nickname = CreateProfileActivity.strNicknameToSave;
        //(String)getIntent().getExtras().getString(CreateProfileActivity.nameField);

        // actual groupID to be gotten from course module
        // contains all "test msg"
//        String groupID = "d7dc21aa72ee41c9a8ed247e4c5d915e";

        // contains custom msgs
        String groupID = "testGroupID";

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://172.30.179.102:3010/getConversationByRoomID/" + groupID;

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

                                String nickname = msg.getString("postedByUser");
                                String message = msg.getString("message");
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
                        myRecylerView.setAdapter(chatBoxAdapter);

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
            // if you are using a phone device you should connect to same local network as
            // your laptop and disable your pubic firewall as well

            socket = IO.socket("http://172.30.179.102:3010");
            socket.connect();

            // emit the event join along side with the nickname
            socket.emit("joinChat", groupID, Nickname);
            Log.d("ChatActivity", "Joining group chat: " + groupID);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d("ChatActivity", "Error connect to socket");
        }

        // send message action
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messagetxt.getText().toString().isEmpty()){
                    Toast.makeText(ChatActivity.this,
                            "Cannot send empty message", Toast.LENGTH_SHORT).show();
                } else {
                    // retrieve the nickname and the message content and fire the event
                    socket.emit("message",
                            groupID,
                            Nickname,
                            messagetxt.getText().toString());

                    messagetxt.setText("");
//                socket.emit("message",
//                        groupID,
//                        Nickname,
//                        "test msg");

                    Log.d("ChatActivity", "Message emitted to server");
                }
            }
        });

        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            // extract data from fired event
                            String nickname = data.getString("senderNickname");
                            String message = data.getString("message");

                            // make instance of message
                            Message m = new Message(nickname, message);
                            // add the message to the messageList
                            MessageList.add(m);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // add the new updated list to the adapter
                        // notify the adapter to update the recycler view
                        // set the adapter for the recycler view
                        chatBoxAdapter = new ChatBoxAdapter(MessageList);
                        chatBoxAdapter.notifyDataSetChanged();
                        myRecylerView.setAdapter(chatBoxAdapter);
                    }
                });
            }
        });
    }
}