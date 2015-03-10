package com.Dreamerindia.People_Store_Front;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 08-03-2015.
 */
public class CollectorEntry extends Activity {
    EditText ceMonth, ceSugar, ceWheat, ceRice, ceCardID;
    JSONObject hay;
    JSONParser jsonParser = new JSONParser();
    private static final String D_URL = "http://www.EmbeddedCollege.org/psfwebservices/dsupdate.php";
    private static final String TAG_PROFILE = "user";
    private static final String TAG_SUGAR = "sugar";
    private static final String TAG_RICE = "rice";
    private static final String TAG_WHEAT = "wheat";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collector_entry);

        ceSugar = (EditText) findViewById(R.id.ceSugar);
        ceWheat = (EditText) findViewById(R.id.ceWheat);
        ceRice = (EditText) findViewById(R.id.ceRice);
        ceCardID = (EditText) findViewById(R.id.cecardID);
        ceCardID.requestFocus();

    }

    public void readFeedback(View v) {
        hideSoftKeyboard(v);
        Intent i = new Intent(this, ReadComments.class);
        startActivity(i);
    }

    public void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    public void update(View v) {
        hideSoftKeyboard(v);

        String sug = ceSugar.getText().toString();
        String whe = ceWheat.getText().toString();
        String ric = ceRice.getText().toString();
        String dis = ceCardID.getText().toString();
        if (sug.equals("") || whe.equals("") || ric.equals("") || dis.equals("")) {
            Toast.makeText(getApplicationContext(), "Please fill all the details", Toast.LENGTH_SHORT).show();
        } else {
            new UpdateStoc().execute();
        }
    }

    private class UpdateStoc extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            int success, cs = 0, cw = 0, cr = 0;
            String su = ceSugar.getText().toString();
            String wh = ceWheat.getText().toString();
            String ri = ceRice.getText().toString();
            String di = ceCardID.getText().toString();

            try {
                cs = Integer.parseInt(su);
                cw = Integer.parseInt(wh);
                cr = Integer.parseInt(ri);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Please enter numbers only in measurement", Toast.LENGTH_SHORT).show();
            }
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_PROFILE, di));
                params.add(new BasicNameValuePair(TAG_SUGAR, String.valueOf(cs)));
                params.add(new BasicNameValuePair(TAG_WHEAT, String.valueOf(cw)));
                params.add(new BasicNameValuePair(TAG_RICE, String.valueOf(cr)));

                hay = jsonParser.makeHttpRequest(
                        D_URL, "POST", params);

                // full json response
                Log.d("Post Comment attempt", hay.toString());

                // json success element
                success = hay.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("updated in C Log!", hay.toString());
                    return hay.getString(TAG_MESSAGE);
                } else {
                    Log.d("Failed to update in C Log", hay.getString(TAG_MESSAGE));
                    return hay.getString(TAG_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (file_url != null) {
                Toast.makeText(CollectorEntry.this, "Goods assigned successfully!", Toast.LENGTH_LONG).show();
                ceSugar.setText("");
                ceWheat.setText("");
                ceRice.setText("");
                ceCardID.setText("");
            }
        }
    }
}