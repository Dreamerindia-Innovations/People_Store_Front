package com.Dreamerindia.People_Store_Front;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
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
 * Created by user on 10-03-2015.
 */
public class Monthly_Stock extends Activity {
    private static final String URL = "http://www.EmbeddedCollege.org/psfwebservices/select.php";
    private static final String D_URL = "http://www.EmbeddedCollege.org/psfwebservices/dselect.php";
    TextView findDist, branchArea, cardID, cardName, eMonth, eSugar, eWheat, eRice, cSugar, cWheat, cRice;
    private static final String TAG_PROFILE = "user";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_SUGAR = "sugar";
    private static final String TAG_RICE = "rice";
    private static final String TAG_WHEAT = "wheat";
    private static final String TAG_AREA = "area";
    private static final String TAG_U_NAME = "uname";
    private static final String TAG_DISTRIBUTOR = "distrib";
    private static final String TAG_BRANCH = "area";
    private static final String TAG_MONTH = "month";
    JSONObject hay;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthly_status);
        cardName = (TextView) findViewById(R.id.ecardName);
        cardID = (TextView) findViewById(R.id.ecardID);
        cSugar = (TextView) findViewById(R.id.ecardSugar);
        cWheat = (TextView) findViewById(R.id.ecardWheat);
        cRice = (TextView) findViewById(R.id.ecardRice);
        branchArea = (TextView) findViewById(R.id.branch);
        findDist = (TextView) findViewById(R.id.findDist);

        eMonth = (TextView) findViewById(R.id.tMonth);
        eSugar = (TextView) findViewById(R.id.ebSugar);
        eWheat = (TextView) findViewById(R.id.ebWheat);
        eRice = (TextView) findViewById(R.id.ebRice);
        Intent intent = getIntent();
        String t = intent.getExtras().getString("month");
        if (t.equals("March")) {
            new LoadProfile().execute();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    new LoadStock().execute();
                }
            }, 7000);
        } else {
            Toast.makeText(getApplicationContext(), "No Records avaialable for this month : " + t, Toast.LENGTH_LONG).show();
        }
        eMonth.append(" " + t);

    }

    class LoadProfile extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String json = null;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Monthly_Stock.this);
            String post_username = sp.getString(TAG_USERNAME, "anon");
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_USERNAME, post_username));
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URL);
                httppost.setEntity(new UrlEncodedFormEntity(params));

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
                JSONArray user = hay.getJSONArray(TAG_PROFILE);
                JSONObject jb = user.getJSONObject(0);
                String userID = jb.getString(TAG_PROFILE);
                String uname = jb.getString(TAG_U_NAME);
                String branch = jb.getString(TAG_AREA);
                String sugar = jb.getString(TAG_SUGAR);
                String rice = jb.getString(TAG_RICE);
                String wheat = jb.getString(TAG_WHEAT);
                String dist = jb.getString(TAG_DISTRIBUTOR);

                // displaying all data in textview
                branchArea.append(" " + branch);
                cardID.append(" " + userID);
                cardName.append(" " + uname);
                cSugar.append(" " + sugar);
                cWheat.append(" " + wheat);
                cRice.append(" " + rice);
                findDist.append(dist);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class LoadStock extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String json = null;
            String temp = findDist.getText().toString();
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_USERNAME, temp));

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(D_URL);
                httppost.setEntity(new UrlEncodedFormEntity(params));

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
            try {
                hay = new JSONObject(json);
                JSONArray user = hay.getJSONArray(TAG_PROFILE);
                JSONObject jb = user.getJSONObject(0);
                String use = jb.getString(TAG_PROFILE);
                String branch = jb.getString(TAG_BRANCH);
                String month = jb.getString(TAG_MONTH);
                String sugar = jb.getString(TAG_SUGAR);
                String rice = jb.getString(TAG_RICE);
                String wheat = jb.getString(TAG_WHEAT);

                // displaying all data in textview
//                branchArea.append(" " + branch);

                eSugar.append(sugar);
                eWheat.append(wheat);
                eRice.append(rice);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
