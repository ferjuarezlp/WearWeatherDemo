package com.gdg.laplata.wearweatherdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private static final String SHOW_MESSAGE_PATH = "/show-message-in-activity";
    private static final String TAG = "MainActivity";

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;

    TextView textName;
    TextView textTemp;
    Button sendToWear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textName = (TextView) findViewById(R.id.textName);
        textTemp = (TextView) findViewById(R.id.textTemp);
        sendToWear = (Button) findViewById(R.id.btnSendToWear);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        sendToWear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StartWearableActivityTask().execute(textName.getText().toString() + " " + textTemp.getText().toString() );
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Obtain current location and send city / lat,lng / postal code
        new WeatherDataTask().execute("La%20Plata");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mResolvingError) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Fail");
    }

    private class StartWearableActivityTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... args) {
            String message = args[0];
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node, message);
            }
            return null;
        }
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
    }

    private void sendStartActivityMessage(String node, String message) {
        Wearable.MessageApi.sendMessage(
                mGoogleApiClient, node, SHOW_MESSAGE_PATH, message.getBytes()).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        }
                    }
                }
        );
    }

    private void updateUI(WeatherData weatherData){
        if(weatherData != null){
            textName.setText(weatherData.name);
            textTemp.setText(weatherData.temp + " C");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class WeatherDataTask extends AsyncTask<String, Void, WeatherData> {
        protected WeatherData doInBackground(String... value) {
            return WeatherManager.getWeatherData(value[0]);
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(WeatherData weatherData) {
            updateUI(weatherData);
        }
    }
}
