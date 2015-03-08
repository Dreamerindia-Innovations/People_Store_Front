package com.Dreamerindia.People_Store_Front;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

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
 * Created by user on 08-03-2015.
 */
public class DistributorEntry extends Activity {
    TextView branchArea, cardID, cardName, cardSex, cardMembers;

    // Progress Dialog
    private ProgressDialog pDialog;

    // Profile json object
    JSONArray user;
    JSONObject hay;
    // Profile JSON url
    private static final String URL = "http://www.EmbeddedCollege.org/psfwebservices/select.php";

    // ALL JSON node names
    private static final String TAG_PROFILE = "user";
    // private static final String TAG_ID = "id";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_SUGAR = "sugar";
    private static final String TAG_RICE = "rice";
    private static final String TAG_WHEAT = "wheat";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distributor_entry);

        cardName = (TextView) findViewById(R.id.cardName);
        cardSex = (TextView) findViewById(R.id.cardSex);
        cardMembers = (TextView) findViewById(R.id.cardMembers);
        cardID = (TextView) findViewById(R.id.cardID);
        branchArea = (TextView) findViewById(R.id.branch);
        // Loading Profile in Background Thread
        new LoadProfile().execute();
    }

    class LoadProfile extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DistributorEntry.this);
            pDialog.setMessage("Loading profile ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Profile JSON
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            String json = null;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(DistributorEntry.this);
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
            pDialog.dismiss();
            try {
                hay = new JSONObject(json);
                JSONArray user = hay.getJSONArray("user");
                JSONObject jb = user.getJSONObject(0);
                String userID = jb.getString("user");
                String uname = jb.getString("uname");
                String branch = jb.getString("area");
                String sex = jb.getString("sex");
                String noMember = jb.getString("nomember");
                String sugar = jb.getString("sugar");
                String rice = jb.getString("rice");
                String wheat = jb.getString("wheat");

                // displaying all data in textview
                branchArea.append(branch);
                cardID.append(userID);
                cardName.append(uname);
                cardSex.append(sex);
                cardMembers.append(noMember);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}