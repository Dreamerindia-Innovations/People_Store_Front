package com.Dreamerindia.People_Store_Front;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 23-02-2015.
 */
public class CardHolderInfo extends Activity {
    TextView branchArea, cardID, cardName, cardSex, cardMembers;
    CheckBox time, response, measure;
    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;
    JSONArray user;
    JSONObject hay;
    private static final String POST_COMMENT_URL = "http://www.EmbeddedCollege.org/psfwebservices/addcomment.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String URL = "http://www.EmbeddedCollege.org/psfwebservices/select.php";
    private static final String TAG_PROFILE = "user";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_SUGAR = "sugar";
    private static final String TAG_RICE = "rice";
    private static final String TAG_WHEAT = "wheat";
    private static final String TAG_NO_MEMBER = "nomember";
    private static final String TAG_SEX = "sex";
    private static final String TAG_AREA = "area";
    private static final String TAG_U_NAME = "uname";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardholderinfo);

        cardName = (TextView) findViewById(R.id.cardName);
        cardSex = (TextView) findViewById(R.id.cardSex);
        cardMembers = (TextView) findViewById(R.id.cardMembers);
        cardID = (TextView) findViewById(R.id.cardID);
        branchArea = (TextView) findViewById(R.id.branch);
        // Loading Profile in Background Thread
        new LoadProfile().execute();
    }

    public void storeComment(View v) {
        time = (CheckBox) findViewById(R.id.time);
        response = (CheckBox) findViewById(R.id.response);
        measure = (CheckBox) findViewById(R.id.measure);
        String S_TIME = null, S_RESPONSE = null, S_MEASURE = null;
        if (time.isChecked()) {
            S_TIME = getResources().getString(R.string.Time);
        }else{
            S_TIME = "";
        }
        if (response.isChecked()) {
            S_RESPONSE = getResources().getString(R.string.response);
        }else{
            S_RESPONSE ="";
        }
        if (measure.isChecked()) {
            S_MEASURE = getResources().getString(R.string.Measure);
        }else{
            S_MEASURE ="";
        }
        Toast.makeText(getApplicationContext(), "Please enter the detailed Feedback", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this,AddComment.class);
        i.putExtra("time",S_TIME);
        i.putExtra("response",S_RESPONSE);
        i.putExtra("measure",S_MEASURE);
        startActivity(i);
    }

    class LoadProfile extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(CardHolderInfo.this);
//            pDialog.setMessage("Loading profile ...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
        }

        /**
         * getting Profile JSON
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            String json = null;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CardHolderInfo.this);
            String post_username = sp.getString("username", "anon");
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", post_username));
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URL);
                httppost.setEntity(new UrlEncodedFormEntity(params));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                json = EntityUtils.toString(resEntity);

                Log.i("Profile JSON: ", json.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            // dismiss the dialog after getting all products
//            pDialog.dismiss();
            try {
                hay = new JSONObject(json);
                JSONArray user = hay.getJSONArray("user");
                JSONObject jb = user.getJSONObject(0);
                String userID = jb.getString(TAG_PROFILE);
                String uname = jb.getString(TAG_U_NAME);
                String branch = jb.getString(TAG_AREA);
                String sex = jb.getString(TAG_SEX);
                String noMember = jb.getString(TAG_NO_MEMBER);
                String sugar = jb.getString(TAG_SUGAR);
                String rice = jb.getString(TAG_RICE);
                String wheat = jb.getString(TAG_WHEAT);

                // displaying all data in textview
                branchArea.append(" " + branch);
                cardID.append(" " + userID);
                cardName.append(" " + uname);
                cardSex.append(" " + sex);
                cardMembers.append(" " + noMember);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
