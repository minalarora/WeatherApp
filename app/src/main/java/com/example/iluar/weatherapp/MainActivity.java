package com.example.iluar.weatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class MyWeather
{
    private String name;
    private String weather;
    private Double max;
    private Double min;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }
}
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText e;
    TextView name,max,min,weather;
    Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        e=findViewById(R.id.edit);
        name=findViewById(R.id.name);
        max=findViewById(R.id.max);
        min=findViewById(R.id.min);
        weather=findViewById(R.id.weather);
        b=findViewById(R.id.button);
        if(checkInternet())
        {
            b.setOnClickListener(this);
        }
        else
        {
            Toast.makeText(this,"Please connect to the Internet!",Toast.LENGTH_LONG).show();
        }
    }
    public boolean checkInternet()
    {
        ConnectivityManager cm= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni=cm.getActiveNetworkInfo();
        if(ni==null)
        {
            return false;
        }
        return ni.isConnected();
    }
    class WeatherNetwork extends AsyncTask<String,Void,MyWeather>
    {

        @Override
        protected void onPreExecute() {
            name.setText("City:");
            max.setText("Max Temp:");
            min.setText("Min Temp:");
            weather.setText("Weather:");
        }

        @Override
        protected MyWeather doInBackground(String... strings) {

            HttpURLConnection huc=null;
            MyWeather myWeather=new MyWeather();
            try
            {
                URL url=new URL("http://api.openweathermap.org/data/2.5/weather?q="+strings[0]+"&APPID=c259e3bb12c2fe9bf99169e66e5e6b38");
                huc=(HttpURLConnection) url.openConnection();
                StringBuffer stringBuffer=new StringBuffer();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(huc.getInputStream()));
                String line=null;
                while((line=bufferedReader.readLine())!=null)
                {
                       stringBuffer.append(line+"\r\n");
                }
                String json=stringBuffer.toString();
                JSONObject alldata=new JSONObject(json);
                myWeather.setName(alldata.getString("name"));
                JSONArray weather=alldata.getJSONArray("weather");
                JSONObject forDetail=weather.getJSONObject(0);
                myWeather.setWeather(forDetail.getString("description"));
                JSONObject temp=alldata.getJSONObject("main");
                myWeather.setMax(temp.getDouble("temp_max")-273.15);
                myWeather.setMin(temp.getDouble("temp_min")-273.15);

            }
            catch (Exception e)
            {
              return null;
            }
            finally {
                huc.disconnect();
                return myWeather;
            }

        }

        @Override
        protected void onPostExecute(MyWeather myWeather) {
            if(myWeather==null)
            {
                Toast.makeText(MainActivity.this,"Unable to find data!",Toast.LENGTH_LONG).show();
                return;
            }

            name.setText(name.getText().toString()+" "+myWeather.getName());
            max.setText(max.getText().toString()+" "+myWeather.getMax()+"C");
            min.setText(min.getText().toString()+" "+myWeather.getMin()+"C");
            weather.setText(weather.getText().toString()+" "+myWeather.getWeather());
        }
    }

    @Override
    public void onClick(View view) {
        if(e.getText().toString().isEmpty())
        {
            Toast.makeText(this,"Please enter city name!",Toast.LENGTH_LONG).show();
            return;
        }
        new WeatherNetwork().execute(e.getText().toString());

    }
}
