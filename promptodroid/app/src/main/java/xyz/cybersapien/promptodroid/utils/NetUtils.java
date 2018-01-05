package xyz.cybersapien.promptodroid.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by ogcybersapien on 6/1/18.
 * Code in the following file has been re-used by me from the utility
 * classes learnt while doing Android Basics Nanodegree(2016)
 *
 * @see <a href="https://github.com/the-cybersapien/NewsDroid/blob/master/app/src/main/java/xyz/cybersapien/newsdroid/network/NetUtils.java">My Github</a>
 */

public final class NetUtils {

    private static final String LOG_TAG = NetUtils.class.getSimpleName();

    @Nullable
    public static String getVersion() {
        URL ourUrl = getURL();
        if (ourUrl == null) {
            // Error in Url, no Data
            return null;
        }
        try {
            String jsonRequest = makeHttpRequest(ourUrl);
            if (TextUtils.isEmpty(jsonRequest)) {
                return null;
            }
            JSONObject versionObject = new JSONObject(jsonRequest);
            return versionObject.getString("version");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Return a parsed URL from the string.
     *
     * @return URL object after conversion from String.
     */
    @Nullable
    private static URL getURL() {
        try {
            // TODO: Add URL
            return new URL("https://us-central1-promptodroid.cloudfunctions.net/api/fetch/version");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Make an HTTP request from given URL and return a String as the response
     *
     * @param url URL to get the data from
     * @return returns JSON String from data from the server
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        /*If url is null no point conitnuing, so return early*/
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If request was successful, server will send a response code 200
            //Else it will send an error code. In case of the former, read the input stream.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with JSON result.");
        } finally {
            /*Close the urlConnection and input stream after we're done getting data*/
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader streamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(streamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
