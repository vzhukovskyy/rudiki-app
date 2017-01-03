package ua.pp.rudiki;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class SendSwitchStateTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = SendSwitchStateTask.class.getSimpleName();

    private SwitchStateListener listener;
    private String token;
    private int index, state;

    SendSwitchStateTask(int index, int state, String token, SwitchStateListener listener) {
        this.index = index;
        this.state = state;
        this.listener = listener;
        this.token = token;
    }

    protected void onPreExecute() {
        listener.onSwitchStateRequestIssued();
    }

    protected String doInBackground(Void... urls) {

        // Create data variable for sent values to server

        String text = "";
        BufferedReader reader = null;

        // Send data
        try {
            URL url = new URL("https://rudiki.pp.ua/api/setSwitchState");

            // Send POST data request

            HttpURLConnection  connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("Authorization", "OAuth "+token);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("switch", index);
            jsonParam.put("state", state);

            wr.writeBytes(jsonParam.toString());

            wr.flush();
            wr.close();

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)  {
                sb.append(line).append("\n");
            }

            text = sb.toString();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                reader.close();
            }
            catch(Exception ex) {
            }
        }

        return text;
    }

    protected void onPostExecute(String response) {
        Log.i(TAG, "Response received: "+response);
        listener.onSwitchStateReceived(response);
    }
}