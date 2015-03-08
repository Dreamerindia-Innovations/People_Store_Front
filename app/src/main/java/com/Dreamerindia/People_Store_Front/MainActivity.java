package com.Dreamerindia.People_Store_Front;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText user, pass;
    private Button mSubmit, mRegister;

    private RadioGroup radioGroupId;
    private RadioButton radioButton;
    TextView usName;
    Intent i;
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    //    private static final String URL = "http://www.EmbeddedCollege.org/psfwebservices/login.php";
    private String URL;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = (EditText) findViewById(R.id.Username);
        pass = (EditText) findViewById(R.id.textpassword);
        mSubmit = (Button) findViewById(R.id.signin);
        mRegister = (Button) findViewById(R.id.register);
        usName = (TextView) findViewById(R.id.username1);
        radioGroupId = (RadioGroup) findViewById(R.id.radioLoginGroup);
        radioGroupId.clearCheck();
        checkRadio();
        //register listeners
        mSubmit.setOnClickListener(this);
//        mRegister.setOnClickListener(this);
        if (!isNetworkConnected()) {
            Toast.makeText(getApplicationContext(), "Please Enable Internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkRadio() {

        radioGroupId.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (radioGroupId.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(MainActivity.this, "Select the Login mode", Toast.LENGTH_SHORT).show();
                } else if (radioGroupId.getCheckedRadioButtonId() == R.id.ration_cardHolder) {
                    usName.setText("Ration ID");
                    URL = "http://www.EmbeddedCollege.org/psfwebservices/login.php";
                } else if (radioGroupId.getCheckedRadioButtonId() == R.id.distributor) {
                    usName.setText("Distributor ID");
                    URL = "http://www.EmbeddedCollege.org/psfwebservices/logind.php";
                } else if (radioGroupId.getCheckedRadioButtonId() == R.id.Collector) {
                    usName.setText("Collector ID");
                    URL = "http://www.EmbeddedCollege.org/psfwebservices/loginc.php";
                }
            }
        });
//        radioGroupId.clearCheck();
    }

    @Override
    public void onClick(View v) {
        // determine which button was pressed:
        switch (v.getId()) {
            case R.id.signin:
                if (radioGroupId.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(MainActivity.this, "Select the Login mode", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isNetworkConnected()) {
                        Toast.makeText(getApplicationContext(), "Please Enable Internet connection", Toast.LENGTH_SHORT).show();
                    }else {
                        new AttemptLogin().execute();
                    }
                }
                break;
            case R.id.register:
                Intent i = new Intent(this, Register.class);
                startActivity(i);
                break;

            default:
                break;
        }
    }


    class AttemptLogin extends AsyncTask<String, String, String> {

        //three methods get called, first preExecute, then do in background, and once do
        //in back ground is completed, the onPost execute method will be called.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Attempting Login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {

            int success;
            String username = user.getText().toString();
            String password = pass.getText().toString();
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        URL, "POST", params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                //upon successful login, save username:
                // Async json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Login Successful!", json.toString());
                    // save user data
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("username", username);
                    edit.commit();

                    if (radioGroupId.getCheckedRadioButtonId() == R.id.ration_cardHolder) {
                        i = new Intent(MainActivity.this, CardHolderInfo.class);
                    } else if (radioGroupId.getCheckedRadioButtonId() == R.id.distributor) {
                        i = new Intent(MainActivity.this, DistributorEntry.class);
                    } else if (radioGroupId.getCheckedRadioButtonId() == R.id.Collector) {
                        i = new Intent(MainActivity.this, ReadComments.class);
                    }
                    startActivity(i);
//                    finish();
                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        protected void onPostExecute(String file_url) {
            if (file_url != null) {
                Toast.makeText(MainActivity.this, file_url, Toast.LENGTH_SHORT).show();
                pDialog.cancel();
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            Toast.makeText(getApplicationContext(), "Please Enable Internet connection", Toast.LENGTH_LONG).show();
            return false;
        } else
            return true;
    }
}
