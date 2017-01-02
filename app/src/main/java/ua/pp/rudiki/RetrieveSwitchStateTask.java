package ua.pp.rudiki;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class RetrieveSwitchStateTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = RetrieveSwitchStateTask.class.getSimpleName();

    private SwitchStateListener listener;

    RetrieveSwitchStateTask(SwitchStateListener listener) {
        this.listener = listener;
    }

    protected void onPreExecute() {
    }

    protected String doInBackground(Void... urls) {

        try {
            URL url = new URL("https://rudiki.pp.ua/api/getSwitchState");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(String response) {
        Log.i(TAG, "Response received: "+response);
        listener.onTaskCompleted(response);
    }
}