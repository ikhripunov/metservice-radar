package com.example.metservice_radar;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

public class Metservice extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        new GetJsonDataTask().execute();
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            Bitmap image = null;
            try {
                image = BitmapFactory.decodeStream(new java.net.URL(urls[0]).openStream());
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return image;
        }

        protected void onPostExecute(Bitmap result) {
            ImageView img = (ImageView) findViewById(R.id.imageView1);
            img.setImageBitmap(result);
            img.setAdjustViewBounds(true);
            img.setMinimumHeight(600);
            img.setMinimumWidth(600);
        }
    }

    public class GetJsonDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String response = makeGetRequest(Constants.URL + Constants.PUBLIC_DATA_RADAR);
                JSONArray array = new JSONArray(response);
                new DownloadImageTask().execute(Constants.URL + array.getJSONObject(0).get("url"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public String makeGetRequest(String path) throws Exception {
            //instantiates httpclient to make request
            DefaultHttpClient httpclient = new DefaultHttpClient();

            //url with the post data
            HttpGet httpGet = new HttpGet(path);

            //sets a request header so the page receving the request
            //will know what to do with it
            httpGet.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");

            //Handles what is returned from the page
            ResponseHandler responseHandler = new BasicResponseHandler();
            return (String) httpclient.execute(httpGet, responseHandler);
        }
    }
}
