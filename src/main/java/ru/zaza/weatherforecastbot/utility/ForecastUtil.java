package ru.zaza.weatherforecastbot.utility;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.zaza.weatherforecastbot.enums.MethodState;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class ForecastUtil {
    final String URL_FIRST_PART = "https://api.openweathermap.org/data/2.5/weather?q=";
    final String URL_FIRST_COORD_PART = "https://api.openweathermap.org/data/2.5/forecast?lat=";
    final String URL_SECOND_PART = "&appid=7f3807d97d32ab879f38caec10cb787a&units=metric";

    public String giveForecast(String city, MethodState state) {
        switch (state) {
            case CURRENTFORECAST -> {
                return giveCurrentForecast(city);
            }
            case TODAYSDETAILEDFORECAST -> {
                return giveDetailedForecast(city);
            }
            case TOMORROWSFORECAST -> {
                return giveTomorrowForecast(city);
            }
        }
        return null;
    }

    public String giveCurrentForecast(String city) {
        String output = getUrlContent(URL_FIRST_PART + city + URL_SECOND_PART);
        if(!output.isEmpty()) {
            JSONObject object = new JSONObject(output);

            return city.toUpperCase() + "\n\n" +
                    "Temperature: " + object.getJSONObject("main").getDouble("temp") + "°C\n" +
                    "Feels like: " + object.getJSONObject("main").getDouble("feels_like") + "°C\n" +
                    "Condition: " + object.getJSONArray("weather").getJSONObject(0).getString("description") + "\n" +
                    "Wind speed: " + object.getJSONObject("wind").getDouble("speed") + "m/s\n" +
                    "Humidity: " + object.getJSONObject("main").getInt("humidity") + "%\n\n";
        } else {
            return "City not found, try again";
        }
    }

    public String giveDetailedForecast(String city) {
        StringBuilder str = new StringBuilder();
        String forecast;
        boolean isToday = true;
        int i = 0;
        String output = getUrlContent(URL_FIRST_PART + city + URL_SECOND_PART);
        if(!output.isEmpty()) {
            JSONObject object = new JSONObject(output);
            forecast = getUrlContent(URL_FIRST_COORD_PART + object.getJSONObject("coord").getDouble("lat") +
                    "&lon=" + object.getJSONObject("coord").getDouble("lon") + URL_SECOND_PART);
        } else {
            return "City not found, try again";
        }

        JSONObject object = new JSONObject(forecast);
        JSONArray jsonArray = new JSONArray(object.getJSONArray("list"));
        str.append(city.toUpperCase() + "\n\n");

        while(isToday) {
            Date date = new Date(jsonArray.getJSONObject(i).getLong("dt") * 1000);
            if(date.getDay() == new Date().getDay()) {
                object = jsonArray.getJSONObject(i);

                str.append("Time: " + date.getHours() + " hours\n")
                        .append("Temperature: " + object.getJSONObject("main").getDouble("temp") + "°C\n")
                        .append("Feels like: " + object.getJSONObject("main").getDouble("feels_like") + "°C\n")
                        .append("Condition: " + object.getJSONArray("weather").getJSONObject(0).getString("description") + "\n")
                        .append("Wind speed: " + object.getJSONObject("wind").getDouble("speed") + "m/s\n")
                        .append("Humidity: " + object.getJSONObject("main").getInt("humidity") + "%\n\n");

                i++;
            } else {
                isToday = false;
            }
        }
        return str.toString();
    }

    public String giveTomorrowForecast(String city) {
        StringBuilder str = new StringBuilder();
        String forecast;
        boolean isTomorrow = false;
        int i = 0;
        String output = getUrlContent(URL_FIRST_PART + city + URL_SECOND_PART);
        if(!output.isEmpty()) {
            JSONObject object = new JSONObject(output);
            forecast = getUrlContent(URL_FIRST_COORD_PART + object.getJSONObject("coord").getDouble("lat") +
                    "&lon=" + object.getJSONObject("coord").getDouble("lon") + URL_SECOND_PART);
        } else {
            return "City not found, try again";
        }

        JSONObject object = new JSONObject(forecast);
        JSONArray jsonArray = new JSONArray(object.getJSONArray("list"));
        str.append(city.toUpperCase() + "\n\n");

        while(!isTomorrow) {
            Date date = new Date(jsonArray.getJSONObject(i).getLong("dt") * 1000L);
            if(date.getDay() == new Date().getDay() + 1 || date.getDay() == (new Date().getDay() - 6)) {
                isTomorrow = true;
                break;
            }
            i++;
        }

        while(isTomorrow) {
            Date date = new Date(jsonArray.getJSONObject(i).getLong("dt") * 1000L);
            if(date.getDay() == new Date().getDay() + 1 || date.getDay() == (new Date().getDay() - 6)) {
                object = jsonArray.getJSONObject(i);

                str.append("Date: " + date.getDate() + "." + (date.getMonth() + 1) + ", time: " + date.getHours() + " hours\n")
                        .append("Temperature: " + object.getJSONObject("main").getDouble("temp") + "°C\n")
                        .append("Feels like: " + object.getJSONObject("main").getDouble("feels_like") + "°C\n")
                        .append("Condition: " + object.getJSONArray("weather").getJSONObject(0).getString("description") + "\n")
                        .append("Wind speed: " + object.getJSONObject("wind").getDouble("speed") + "m/s\n")
                        .append("Humidity: " + object.getJSONObject("main").getInt("humidity") + "%\n\n");

                i++;
            } else {
                isTomorrow = false;
            }
        }


        return str.toString();
    }

    public String getUrlContent(String urlAdress) {
        StringBuffer content = new StringBuffer();

        try {
            URL url = new URL(urlAdress);
            URLConnection urlConnection = url.openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;

            while((line = bufferedReader.readLine()) != null) content.append(line + "\n");
            bufferedReader.close();

        } catch (Exception e) {
            return "";
        }
        return content.toString();
    }
}
