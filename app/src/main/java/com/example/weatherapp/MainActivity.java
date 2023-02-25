package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button main_bnt;
    private EditText user_field;
    private TextView result_info;

    private void onClickButton(View view) {
        String city = user_field.getText().toString().trim();
        System.out.println(city);
        System.out.println(user_field.getText());
        if (city.equals("")) {
            Toast.makeText(this, R.string.no_user_inpur, Toast.LENGTH_LONG).show();
        } else {
            String token = "e908101910eeec256c9532fa097952d5";
            String units = "metric";
            String lang = "ru";

            String url = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=%s&lang=%s",
                    city, token, units, lang);

            new GetUrlData().execute(url);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_bnt = findViewById(R.id.main_bnt);
        user_field = findViewById(R.id.user_field);
        result_info = findViewById(R.id.result_info);

        main_bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickButton(view);
            }
        });

    }

    private class GetUrlData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText(R.string.waiting_for_result);
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer stringBuffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line);
                    stringBuffer.append('\n');
                }

                return stringBuffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                StringBuilder stringBuilder = new StringBuilder();

                JSONObject main = jsonObject.getJSONObject("main");

                Double temp = main.getDouble("temp");
                stringBuilder.append("Температура: ");
                stringBuilder.append(temp);
                stringBuilder.append("\n");

                Double feelsLike = main.getDouble("feels_like");
                stringBuilder.append("Ощущается как: ");
                stringBuilder.append(feelsLike);

                result_info.setText(stringBuilder.toString());

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}