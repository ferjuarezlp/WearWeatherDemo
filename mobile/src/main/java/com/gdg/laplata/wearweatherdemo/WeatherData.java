package com.gdg.laplata.wearweatherdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ferjuarez on 04/02/15.
 */
public class WeatherData {
    public String temp;
    public String temp_min;
    public String temp_max;
    public String pressure;
    public String humidity;
    public String name;
    public String description;
    public String icon;


    public WeatherData(JSONObject jsonWeather){

        try {
            JSONObject jsonObjectMain = jsonWeather.getJSONObject("main");
            this.temp = jsonObjectMain.getString("temp");
            this.temp_min = jsonObjectMain.getString("temp_min");
            this.temp_max = jsonObjectMain.getString("temp_max");
            this.pressure = jsonObjectMain.getString("pressure");
            this.humidity = jsonObjectMain.getString("humidity");
            this.name = jsonWeather.getString("name");
            JSONArray jsonArrayWeather = jsonWeather.getJSONArray("weather");
            this.description = ((JSONObject) jsonArrayWeather.get(0)).getString("description");

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
