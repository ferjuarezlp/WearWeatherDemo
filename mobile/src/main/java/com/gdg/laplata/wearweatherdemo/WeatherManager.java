package com.gdg.laplata.wearweatherdemo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by ferjuarez on 04/02/15.
 */
public class WeatherManager {
    public static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    public static String LANG = "sp";
    public static String UNITS = "metric";


    public static WeatherData getWeatherData(String city){
        String url = BASE_URL + city + "&units=" + UNITS + "&lang=" + LANG;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = client.execute(request);
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);

                try {
                    JSONObject jsonWeather = new JSONObject(json);
                    return new WeatherData(jsonWeather);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Show error message
                return null;
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
