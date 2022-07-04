package com.example.FindYourPeers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateProfileActivity extends AppCompatActivity implements android.widget.AdapterView.OnItemSelectedListener {
    private Button ConfirmCreateProfileButton;
    private Button PrivateMessageButton;

    public static String strNicknameToSave;

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        String selectedItem = parent.getItemAtPosition(pos).toString();
        Toast.makeText(getApplicationContext(), selectedItem, Toast.LENGTH_LONG).show();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Toast.makeText(getApplicationContext(), "Nothing selected", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);


//        EditText nameField = (EditText) findViewById(R.id.editTextTextPersonName);
//        String strNicknameToSave = nameField.getText().toString();
        //Toast.makeText(getApplicationContext(), "Name: strNicknameToSave", Toast.LENGTH_LONG).show();

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.year_standing_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.coop_standing_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        spinner2.setOnItemSelectedListener(this);


        ConfirmCreateProfileButton = findViewById(R.id.confirmCreateProfile_id);
        ConfirmCreateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CreateProfileActivity", "Confirm create profile");

//                Intent weatherIntent = new Intent(MainActivity.this, WeatherActivity.class);
//                startActivity(weatherIntent);

                // send the profile info to server for saving to database
                EditText nameField = (EditText) findViewById(R.id.editTextTextPersonName);
                strNicknameToSave = nameField.getText().toString();
                Toast.makeText(getApplicationContext(), "Name: " + strNicknameToSave, Toast.LENGTH_LONG).show();

                Intent chatIntent = new Intent(CreateProfileActivity.this, ChatActivity.class);
//                chatIntent.putExtra(NICKNAME,nameField.getText().toString());
                startActivity(chatIntent);

//                Intent privateChatIntent = new Intent(CreateProfileActivity.this, privateChatActivity.class);
////                chatIntent.putExtra(NICKNAME,nameField.getText().toString());
//                startActivity(privateChatIntent);
            }
        });

        PrivateMessageButton = findViewById(R.id.private_Message_Button_id);
        PrivateMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CreateProfileActivity", "go private message");

//                Intent weatherIntent = new Intent(MainActivity.this, WeatherActivity.class);
//                startActivity(weatherIntent);

                // send the profile info to server for saving to database
                EditText nameField = (EditText) findViewById(R.id.editTextTextPersonName);
                strNicknameToSave = nameField.getText().toString();
                Toast.makeText(getApplicationContext(), "Name: " + strNicknameToSave, Toast.LENGTH_SHORT).show();

                Intent chatIntent = new Intent(CreateProfileActivity.this, privateChatActivity.class);
//                chatIntent.putExtra(NICKNAME,nameField.getText().toString());
                startActivity(chatIntent);

//                Intent privateChatIntent = new Intent(CreateProfileActivity.this, privateChatActivity.class);
////                chatIntent.putExtra(NICKNAME,nameField.getText().toString());
//                startActivity(privateChatIntent);
            }
        });

    }



}
