package com.example.metservice_radar;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
            ImageView img = (ImageView) findViewById(R.id.imageViewRadar);
            img.setImageBitmap(result);
            img.setAdjustViewBounds(true);
            img.setMinimumHeight(600);
            img.setMinimumWidth(600);
        }
    }

    public class GetJsonDataTask extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray array = null;
            try {
                String response = makeGetRequest(Constants.URL + Constants.PUBLIC_DATA_RADAR);
                /*[{
                longDateTime: "9:20pm Sunday 5 Oct 2014"
                shortDateTime: "9:20pm Sun"
                url: "/IcePics/ob/249a-148df64d380-148df68267c.RadarAUCKLAND.jpeg"
                validFrom: "9:20pm Sunday 5 Oct 2014"
                validToex: "9:20pm Sunday 5 Oct 2014"
                }]*/
                array = new JSONArray(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return array;
        }

        protected void onPostExecute(JSONArray result) {
            try {
                if (result != null && result.length() > 0) {
                    new DownloadImageTask().execute(Constants.URL + result.getJSONObject(0).get("url"));
                    TextView textDate = (TextView) findViewById(R.id.textViewDateTime);
                    textDate.setText((String)result.getJSONObject(0).get("longDateTime"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String makeGetRequest(String path) throws Exception {
            //instantiates httpclient to make request
            DefaultHttpClient httpclient = new DefaultHttpClient();
            //url with the post data
            HttpGet httpGet = new HttpGet(path);
            //sets a request header so the page receiving the request will know what to do with it
            httpGet.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
            //Handles what is returned from the page
            ResponseHandler responseHandler = new BasicResponseHandler();
            return (String) httpclient.execute(httpGet, responseHandler);
        }
    }
}
