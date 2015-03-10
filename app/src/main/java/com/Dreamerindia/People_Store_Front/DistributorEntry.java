package com.Dreamerindia.People_Store_Front;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
 * Created by user on 08-03-2015.
 */
public class DistributorEntry extends Activity {
    TextView eMonth, eSugar, eWheat, eRice, eCardName, branchArea, ts, tw, tr, di;
    EditText cNumber, cSugar, cWheat, cRice, t, m;
    JSONArray user;
    JSONObject hay;
    JSONParser jsonParser = new JSONParser();
    private static final String URL = "http://www.EmbeddedCollege.org/psfwebservices/dselect.php";
    private static final String N_URL = "http://www.EmbeddedCollege.org/psfwebservices/dnamepull.php";
    private static final String U_URL = "http://www.EmbeddedCollege.org/psfwebservices/dupdate.php";
    private static final String D_URL = "http://www.EmbeddedCollege.org/psfwebservices/dsupdate.php";
    private static final String TAG_PROFILE = "user";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_U_NAME = "uname";
    private static final String TAG_BRANCH = "area";
    private static final String TAG_MONTH = "month";
    private static final String TAG_SUGAR = "sugar";
    private static final String TAG_RICE = "rice";
    private static final String TAG_WHEAT = "wheat";
    private static final String TAG_DISTRI = "distrib";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distributor_entry);
        branchArea = (TextView) findViewById(R.id.branch);
        eMonth = (TextView) findViewById(R.id.eMonth);
        eSugar = (TextView) findViewById(R.id.ebSugar);
        eWheat = (TextView) findViewById(R.id.ebWheat);
        eRice = (TextView) findViewById(R.id.ebRice);
        ts = (TextView) findViewById(R.id.ts);
        tw = (TextView) findViewById(R.id.tw);
        tr = (TextView) findViewById(R.id.tr);
        di = (TextView) findViewById(R.id.di);

        cNumber = (EditText) findViewById(R.id.eddcardID);
        eCardName = (TextView) findViewById(R.id.cardName);
        cSugar = (EditText) findViewById(R.id.eddcardSugar);
        cWheat = (EditText) findViewById(R.id.eddcardWheat);
        cRice = (EditText) findViewById(R.id.eddcardRice);
        // Loading Profile in Background Thread
        new LoadStock().execute();
        eCardName.requestFocus();

    }

    public void dFeedbackSubmit(View v) {
        hideSoftKeyboard(v);
        t = (EditText) findViewById(R.id.title);
        m = (EditText) findViewById(R.id.message);
        String tit = t.getText().toString();
        String mes = m.getText().toString();
        Intent i = new Intent(this, DAddComment.class);
        i.putExtra("title", tit);
        i.putExtra("msg", mes);
        startActivity(i);
        t.setText("");
        m.setText("");
    }

    public void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void lookUp(View v) {
        hideSoftKeyboard(v);
        new LoadName().execute();
    }

    public void update(View v) {
        hideSoftKeyboard(v);
        new LoadStock().execute();
        String sug = cSugar.getText().toString();
        String whe = cWheat.getText().toString();
        String ric = cRice.getText().toString();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(DistributorEntry.this);
        String post_username = sp.getString(TAG_USERNAME, "anon");
        String check = di.getText().toString();
        if (post_username.equals(check)) {

            if (sug.equals("") || whe.equals("") || ric.equals("")) {
                Toast.makeText(getApplicationContext(), "Please fill all the details", Toast.LENGTH_SHORT).show();
            } else {
                new UpdateStock().execute();
                new DUpdateStock().execute();
                eCardName.setText(getResources().getString(R.string.name));

            }
        } else {
            Toast.makeText(getApplicationContext(), "Card Number you have entered is not belongs to this Area", Toast.LENGTH_SHORT).show();
            eCardName.setText(getResources().getString(R.string.name));
        }
    }

    class LoadStock extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String json = null;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(DistributorEntry.this);
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
            try {
                hay = new JSONObject(json);
                JSONArray user = hay.getJSONArray(TAG_PROFILE);
                JSONObject jb = user.getJSONObject(0);
                String branch = jb.getString(TAG_BRANCH);
                String month = jb.getString(TAG_MONTH);
                String sugar = jb.getString(TAG_SUGAR);
                String rice = jb.getString(TAG_RICE);
                String wheat = jb.getString(TAG_WHEAT);

                // displaying all data in textview
                branchArea.append(" " + branch);
                eMonth.append(month);
                eSugar.append(sugar);
                eWheat.append(wheat);
                eRice.append(rice);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class LoadName extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... args) {
            String name = cNumber.getText().toString();
            String json = null;
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_USERNAME, name));

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(N_URL);
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
                String userN = jb.getString(TAG_U_NAME);
                String sug = jb.getString(TAG_SUGAR);
                String whe = jb.getString(TAG_WHEAT);
                String ric = jb.getString(TAG_RICE);
                String dis = jb.getString(TAG_DISTRI);
                eCardName.append(" " + userN);
                ts.append(sug);
                tw.append(whe);
                tr.append(ric);
                di.setText(dis);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class UpdateStock extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            int success;
            String name = cNumber.getText().toString();
            String sug = cSugar.getText().toString();
            String whe = cWheat.getText().toString();
            String ric = cRice.getText().toString();
            String esug = ts.getText().toString();
            String ewhe = tw.getText().toString();
            String eric = tr.getText().toString();
            int cs = Integer.parseInt(sug);
            int cw = Integer.parseInt(whe);
            int cr = Integer.parseInt(ric);
            int s = Integer.parseInt(esug);
            int w = Integer.parseInt(ewhe);
            int r = Integer.parseInt(eric);
            int sugar = s + cs;
            int wheat = w + cw;
            int rice = r + cr;

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_PROFILE, name));
                params.add(new BasicNameValuePair(TAG_SUGAR, String.valueOf(sugar)));
                params.add(new BasicNameValuePair(TAG_WHEAT, String.valueOf(wheat)));
                params.add(new BasicNameValuePair(TAG_RICE, String.valueOf(rice)));

                hay = jsonParser.makeHttpRequest(
                        U_URL, "POST", params);

                // full json response
                Log.d("Post Comment attempt", hay.toString());

                // json success element
                success = hay.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("updated in CH Log!", hay.toString());
                    return hay.getString(TAG_MESSAGE);
                } else {
                    Log.d("Failed to update in CH Log", hay.getString(TAG_MESSAGE));
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
                Toast.makeText(DistributorEntry.this, "Purchase updated successfully!", Toast.LENGTH_LONG).show();
            }
        }
    }

    class DUpdateStock extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            int success;
            String sug = cSugar.getText().toString();
            String whe = cWheat.getText().toString();
            String ric = cRice.getText().toString();
            String dsug = eSugar.getText().toString();
            String dwhe = eWheat.getText().toString();
            String dric = eRice.getText().toString();
            int cs = Integer.parseInt(sug);
            int cw = Integer.parseInt(whe);
            int cr = Integer.parseInt(ric);
            int ds = Integer.parseInt(dsug);
            int dw = Integer.parseInt(dwhe);
            int dr = Integer.parseInt(dric);
            int dsugar = ds - cs;
            int dwheat = dw - cw;
            int drice = dr - cr;


            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(DistributorEntry.this);
            String post_username = sp.getString(TAG_USERNAME, "anon");
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(TAG_PROFILE, post_username));
                params.add(new BasicNameValuePair(TAG_SUGAR, String.valueOf(dsugar)));
                params.add(new BasicNameValuePair(TAG_WHEAT, String.valueOf(dwheat)));
                params.add(new BasicNameValuePair(TAG_RICE, String.valueOf(drice)));

                hay = jsonParser.makeHttpRequest(
                        D_URL, "POST", params);

                // full json response
                Log.d("Post Comment attempt", hay.toString());

                // json success element
                success = hay.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("updated in D Log!", hay.toString());
                    return hay.getString(TAG_MESSAGE);
                } else {
                    Log.d("Failed to update in D Log!", hay.getString(TAG_MESSAGE));
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
                Toast.makeText(DistributorEntry.this, "Purchase updated successfully!", Toast.LENGTH_LONG).show();
                cSugar.setText("");
                cWheat.setText("");
                cRice.setText("");
            }
        }
    }
}