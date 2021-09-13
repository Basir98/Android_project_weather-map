package com.example.assignment3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ForecastWeatherActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    TextView tvCityName;
    Button btnBackToMain;
    private final String API_KEY = "e61ca53e504dc1a5b86f7e9e9b14f086&";


    String[] dayTitles = {" In 1 Day","In 2 Days","In 3 Days","In 4 Days","In 5 Days","In 6 Days","In 7 Days","In 8 Days"};

    ArrayList<String> arrayDayTemp;
    ArrayList<String> arrayNightTemp;
    ArrayList<String> arrayMainAndDescription;
    ImageView imageView;


    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_weather);


        tvCityName = (TextView) findViewById(R.id.tv_city_name);
        imageView = findViewById(R.id.imageViewIcon);

        listView = (ListView) findViewById(R.id.forecast_listView);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        arrayDayTemp = new ArrayList<>();
        arrayNightTemp = new ArrayList<>();
        arrayMainAndDescription = new ArrayList<>();

        Intent getWeatherIntent = getIntent();
        String cityName="";
        String icon = "";
        String strCityLat ="", strCityLong="";
        try {
            cityName = getWeatherIntent.getStringExtra("city_name");
            strCityLat = getWeatherIntent.getStringExtra("city_lat");
            strCityLong = getWeatherIntent.getStringExtra("city_long");
            icon = getWeatherIntent.getStringExtra("city_icon");

        }catch (Exception e){ }

        tvCityName.setText(cityName);
        imageView.setVisibility(View.VISIBLE);
        Glide.with(ForecastWeatherActivity.this).load("https://openweathermap.org/img/w/"+icon+".png").into(imageView);

        // get the lat and long before this
        if(strCityLat.equals("")&&strCityLong.equals(""))
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        else
            fetchWeatherForecastInfo(strCityLat, strCityLong);


        btnBackToMain = (Button) findViewById(R.id.btnBack);
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void fetchWeatherForecastInfo(String strLat, String strLong) {
        String tempURL = "";
        if (strLat.equals("")&& strLong.equals("")){
            Toast.makeText(this, "No city was entered", Toast.LENGTH_SHORT).show();
        }
        else{
               tempURL = "https://api.openweathermap.org/data/2.5/onecall?lat="+ strLat+"&lon="+strLong+"&units=metric&exclude=minutely,hourly&appid="+API_KEY;

            //System.out.println("2. Lat: "+strLat +" long: "+strLong);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    arrayDayTemp.clear();
                    arrayNightTemp.clear();
                    arrayMainAndDescription.clear();

                    JSONObject jsonResponse = new JSONObject(response);

                    JSONArray jsonArray = jsonResponse.getJSONArray("daily");

                    for(int i=0; i< jsonArray.length(); i++){
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(i);

                        JSONObject dayTemp = jsonObjectWeather.getJSONObject("temp");

                        String day = dayTemp.getString("day")+" °C";
                        String night = dayTemp.getString("night")+ " °C";

                        JSONArray dayWeather = jsonObjectWeather.getJSONArray("weather");
                        System.out.println("Day weather: "+dayWeather.toString());

                        JSONObject jsonObjectMain = dayWeather.getJSONObject(0);

                        String main = jsonObjectMain.getString("main");
                        String weatherDes = jsonObjectMain.getString("description");
                        String icon = jsonObjectMain.getString("icon");

                        arrayDayTemp.add(day);
                        arrayNightTemp.add(night);
                        arrayMainAndDescription.add(main+", "+weatherDes);


                        //System.out.println("Day: "+day+" night: "+night+" main: "+main+" des: "+weatherDes);

                    }

                    String[] strArrayDayTemp = GetStringArray(arrayDayTemp);
                    String[] strArrayNightTemp = GetStringArray(arrayNightTemp);
                    String[] strArrayDayMain = GetStringArray(arrayMainAndDescription);





                    ForecastAdapter forecastAdapter = new ForecastAdapter(dayTitles, strArrayDayTemp, strArrayNightTemp, strArrayDayMain);
                    listView.setAdapter(forecastAdapter);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ForecastWeatherActivity.this, "This city could not be found. Try again!", Toast.LENGTH_SHORT).show();
                System.out.println(error.toString());
            }
        });
        requestQueue.add(stringRequest);
    }




    class ForecastAdapter extends BaseAdapter {

        String[] titles;
        String [] dayTemp;
        String[] nightTemp;
        String[] mainAndDescription;
        //int[] images;

        public ForecastAdapter(String[] titles, String[] dayTemp, String[] nightTemp, String[] mainAndDescription) {
            this.titles = titles;
            this.dayTemp = dayTemp;
            this.nightTemp = nightTemp;
            this.mainAndDescription = mainAndDescription;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.forecastlist_activity, null);

            //ImageView mImageView = (ImageView) view.findViewById(R.id.weather_image_show);
            TextView mTextTitle = (TextView) view.findViewById(R.id.listView_dayTitle);
            TextView mTextDayTemp = (TextView) view.findViewById(R.id.listView_dayTemp);
            TextView mTextNightTemp = (TextView) view.findViewById(R.id.listView_nightTemp);
            TextView mTextMainAndDescription = (TextView) view.findViewById(R.id.weather_mainAndDescription);


            //mImageView.setImageResource(images[position]);
            mTextTitle.setText(titles[position]);
            mTextDayTemp.setText(dayTemp[position]);
            mTextNightTemp.setText(nightTemp[position]);
            mTextMainAndDescription.setText(mainAndDescription[position]);

            return view;
        }
    }

    // convert arrayList to normal array
    public static String[] GetStringArray(ArrayList<String> arr)
    {

        // declaration and initialise String Array
        String str[] = new String[arr.size()];

        // ArrayList to Array Conversion
        for (int j = 0; j < arr.size(); j++) {

            // Assign each value to String array
            str[j] = arr.get(j);
        }
        return str;
    }


}