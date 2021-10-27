package com.example.assignment3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button btnGetWeather, btnGetInfo, btnGetCityWeatherForecast, btnShowOnMaps;
    TextView tvResult;
    EditText etCity;
    RequestQueue requestQueue;
    String Icon="";

    private final String url = "https://api.openweathermap.org/data/2.5/weather?appid=e61ca53e504dc1a5b86f7e9e9b14f086&q=";
    private final String urlInfo = "https://countriesnow.space/api/v0.1/countries/population/cities";
    private final String API_KEY = "e61ca53e504dc1a5b86f7e9e9b14f086&";

    DecimalFormat df = new DecimalFormat("#.##");

    String cityNameFW = "";
    String strLat="", strLong="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetWeather = findViewById(R.id.btnGetWeather);
        btnGetInfo = findViewById(R.id.btnGetCityInfo);
        tvResult = findViewById(R.id.tvResult);
        etCity = findViewById(R.id.etCity);

        requestQueue = Volley.newRequestQueue(getApplicationContext());


        btnGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchWeatherInfo();
            }
        });
        btnGetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCityInfo();
                btnGetCityWeatherForecast.setVisibility(View.INVISIBLE);
            }
        });
        btnGetCityWeatherForecast = findViewById(R.id.btnGoToCityWeatherForecast);
        btnShowOnMaps = findViewById(R.id.btnShowOnMap);
        btnGetCityWeatherForecast.setBackgroundColor(Color.YELLOW);

        btnGetCityWeatherForecast.setVisibility(View.INVISIBLE);
        btnShowOnMaps.setBackgroundColor(Color.CYAN);

        btnShowOnMaps.setVisibility(View.INVISIBLE);

        btnGetCityWeatherForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cityNameFW.equals("")){
                    showForecast(cityNameFW);
                }
            }
        });
        btnShowOnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cityNameFW.equals("")){
                    showOnMap(cityNameFW);
                }
            }
        });


    }

    private void fetchCityInfo() {
        String tempURL = "";
        String city = etCity.getText().toString().trim();
        if (city.equals("")){
            etCity.setError("City field can not be empty!");
        }
        else{
            tempURL = urlInfo;
            cityNameFW = city;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String output = "";
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject jsonData = jsonResponse.getJSONObject("data");
                    String country = jsonData.getString("country");
                    JSONArray jsonPopulationData = jsonData.getJSONArray("populationCounts");
                    JSONObject jsonPopulation = jsonPopulationData.getJSONObject(0);
                    String year = jsonPopulation.getString("year");
                    String value = jsonPopulation.getString("value");

                    output += "City: "+city
                            + "\n Country: "+country
                            +"\n City population: "+ value
                            +"\n Last updated: "+ year;

                    tvResult.setText(output);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //imageView.setVisibility(View.INVISIBLE);
                btnShowOnMaps.setText("Show " + cityNameFW + " on a map");
                btnShowOnMaps.setVisibility(View.VISIBLE);
                btnGetCityWeatherForecast.setVisibility(View.INVISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Population data cannot be found for " + city, Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("city", city);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void fetchWeatherInfo() {
        String tempURL = "";
        String city = etCity.getText().toString().trim();
        if (city.equals("")){
            etCity.setError("City field can not be empty!");
        }
        else{
            tempURL = url + city;
            cityNameFW = city;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String output = "";
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    Icon = jsonObjectWeather.getString("icon");

                    String description = jsonObjectWeather.getString("description");
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    System.out.println("För att hitta lat och long: "+jsonObjectMain.toString());

                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    float pressure = jsonObjectMain.getInt("pressure");
                    int humidity = jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String wind = jsonObjectWind.getString("speed");
                    JSONObject getJsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = getJsonObjectClouds.getString("all");
                    JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                    String cityName = jsonResponse.getString("name");
                    tvResult.setTextColor(Color.rgb(68,134,139));
                    System.out.println("Cityname: "+cityName);
                    output += "Current weather of "+cityName
                            + "\n Temp: "+df.format(temp)+ " °C"
                            +"\n Feels like: "+df.format(feelsLike)
                            +"\n Humidity: "+humidity + "%"
                            +"\n Description: "+description
                            +"\n Wind speed: "+wind+"m/s"
                            +"\n Cloudiness: "+clouds+"%"
                            +"\n Pressure: "+pressure+"hPa";
                    tvResult.setText(output);


                    cityNameFW = cityName;
                    btnGetCityWeatherForecast.setText("Show forecast for " + cityNameFW);
                    btnGetCityWeatherForecast.setVisibility(View.VISIBLE);
                    btnShowOnMaps.setVisibility(View.INVISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, city+" could not be found. Try again!", Toast.LENGTH_SHORT).show();
                System.out.println(error.toString());
            }
        });
        requestQueue.add(stringRequest);
    }


    private void showForecast(String strCity){
        String tempURL = "";
        String city = strCity.trim();
        if (city.equals("")){
            Toast.makeText(this, "No city was entered", Toast.LENGTH_SHORT).show();
        }
        else{
            tempURL =  "https://api.openweathermap.org/geo/1.0/direct?q="+strCity+"&limit=1&appid="+API_KEY;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{

                            // Do something with the response
                            JSONArray jsonResponse = new JSONArray(response);
                            JSONObject jsonObject = jsonResponse.getJSONObject(0);
                            strLat = jsonObject.getString("lat");
                            strLong = jsonObject.getString("lon");

                            System.out.println("1.  Lat: "+strLat+", long: "+strLong);
                            if(strLat.equals("") && strLong.equals("")){
                                Toast.makeText(MainActivity.this, "City Lat and long is missing", Toast.LENGTH_SHORT).show();
                            }else {
                                // Get weather forecast
                                Intent intent = new Intent(getApplicationContext(), ForecastWeatherActivity.class);
                                intent.putExtra("city_name", cityNameFW);
                                intent.putExtra("city_icon", Icon);
                                intent.putExtra("city_lat", strLat);
                                intent.putExtra("city_long", strLong);
                                startActivity(intent);
                            }

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        System.out.println(error.toString());
                    }
                });


        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    private void showOnMap(String strCity){
        String tempURL = "";
        String city = strCity.trim();
        if (city.equals("")){
            Toast.makeText(this, "No city was entered", Toast.LENGTH_SHORT).show();
        }
        else{
            tempURL =  "https://api.openweathermap.org/geo/1.0/direct?q="+strCity+"&limit=1&appid="+API_KEY;

        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, tempURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            // Do something with the response
                            JSONArray jsonResponse = new JSONArray(response);
                            JSONObject jsonObject = jsonResponse.getJSONObject(0);
                            //String lat = jsonResponse.getString(3);
                            strLat = jsonObject.getString("lat");
                            strLong = jsonObject.getString("lon");

                            if(strLat.equals("") && strLong.equals("")){
                                Toast.makeText(MainActivity.this, "City Lat and long is missing", Toast.LENGTH_SHORT).show();
                            }else {
                                // Get weather forecast
                                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                intent.putExtra("city_name", cityNameFW);
                                intent.putExtra("city_lat", strLat);
                                intent.putExtra("city_long", strLong);
                                startActivity(intent);
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        System.out.println(error.toString());
                    }
                });


        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }
}