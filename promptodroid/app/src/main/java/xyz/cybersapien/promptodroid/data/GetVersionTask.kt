package xyz.cybersapien.promptodroid.data

import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

/**
 * Created by ogcybersapien on 9/1/18.
 */

class GetVersionTask(
        private val finishedListener: OnFinishListener
) : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg params: Void?): String? {
        val ourUrl = getUrl() ?: return null

        val jsonResponse = makeHttpRequest(ourUrl)

        if (TextUtils.isEmpty(jsonResponse)) {
            return null
        }

        val versionObject = JSONObject(jsonResponse)
        return versionObject.getString("version")
    }

    private fun makeHttpRequest(url: URL): String? {
        var jsonResponse: String? = null

        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        var dataStream: InputStream? = null

        urlConnection.readTimeout = READ_TIMEOUT
        urlConnection.connectTimeout = CONNECT_TIMEOUT
        urlConnection.requestMethod = "GET"
        urlConnection.connect()

        if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
            dataStream = urlConnection.inputStream
            jsonResponse = readFromStream(dataStream)
        } else {
            Log.d(LOG_TAG, "Error Response code: " + urlConnection.responseCode)
            Log.d(LOG_TAG, "Error Message: " + readFromStream(urlConnection.errorStream))
        }

        urlConnection.disconnect()
        dataStream?.close()

        return jsonResponse
    }

    private fun readFromStream(inputStream: InputStream?): String {
        val output = StringBuilder()

        if (inputStream != null) {
            val streamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val reader = BufferedReader(streamReader)
            var line = reader.readLine()
            while (line != null) {
                output.append(line)
                line = reader.readLine()
            }
        }
        return output.toString()
    }

    override fun onPostExecute(result: String?) {
        finishedListener.onVersionTaskFinish(result)
    }

    private fun getUrl(): URL? {
        try {
            return URL("https://us-central1-promptodroid.cloudfunctions.net/api/fetch/version")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        val READ_TIMEOUT = 10000
        val CONNECT_TIMEOUT = 15000
        val LOG_TAG: String = GetVersionTask::class.java.simpleName
    }

    interface OnFinishListener {
        fun onVersionTaskFinish(version: String?)
    }
}