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
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddComment extends Activity implements OnClickListener {

    private EditText title, message;
    private Button mSubmit;
    String time, response, measure;
    String post_username;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static final String POST_COMMENT_URL = "http://www.EmbeddedCollege.org/psfwebservices/addcomment.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_comment);

        title = (EditText) findViewById(R.id.title);
        message = (EditText) findViewById(R.id.message);

        mSubmit = (Button) findViewById(R.id.submit);
        mSubmit.setOnClickListener(this);
        Intent intent = getIntent();
        time = intent.getExtras().getString("time");
        response = intent.getExtras().getString("response");
        measure = intent.getExtras().getString("measure");
//        Toast.makeText(getApplicationContext(), time+response+measure+other, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        hideSoftKeyboard(v);
        new PostComment().execute();
    }

    public void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    class PostComment extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddComment.this);
            pDialog.setMessage("Posting Comment...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String post_title = title.getText().toString();
            String post_message = message.getText().toString();


            //We need to change this:
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AddComment.this);
            post_username = sp.getString("username", "anon");

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("username", post_username));
                params.add(new BasicNameValuePair("title", post_title));
                params.add(new BasicNameValuePair("message", post_message));
                params.add(new BasicNameValuePair("time", time));
                params.add(new BasicNameValuePair("response", response));
                params.add(new BasicNameValuePair("measure", measure));
                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        POST_COMMENT_URL, "POST", params);

                // full json response
                Log.d("Post Comment attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Comment Added!", json.toString());
                    finish();
                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Comment Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(AddComment.this, "Feedback from " + post_username + " posted successfully!", Toast.LENGTH_LONG).show();
            }

        }

    }


}
