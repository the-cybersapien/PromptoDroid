package xyz.cybersapien.promptodroid.data;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import xyz.cybersapien.promptodroid.utils.NetUtils;

/**
 * Created by ogcybersapien on 6/1/18.
 */

public class GetVersionTask extends AsyncTask<Void, Void, String> {

    private OnFinishListener finishListener;

    public GetVersionTask(@NonNull OnFinishListener finishListener) {
        this.finishListener = finishListener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return NetUtils.getVersion();
    }

    @Override
    protected void onPostExecute(String s) {
        finishListener.onVersionFinish(s);
    }

    public interface OnFinishListener {
        void onVersionFinish(String version);
    }
}