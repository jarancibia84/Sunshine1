package com.example.tizos.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment  {


    private ArrayAdapter<String> mforecastAdapter;
    private ListView listView;
    private Integer numDays = 7;
    private String LOCATION = "santiago,chile";

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute(LOCATION);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String[] ListaForecast = { "Today - Sunny - 30/11",
                "Monday - Sunny - 33/10",
                "Thuesday - Sunny - 32/8",
                "Wednesday - Sunny - 30/11",

                "Thursday - Sunny - 29/9",
                "Friday - Cloudy - 29/10",
                "Saturday - Foggy - 27/6"

        };

        ArrayList<String> list = new ArrayList<String>();
        Collections.addAll(list, ListaForecast);

        mforecastAdapter =
                new ArrayAdapter<String>(
                        //Context
                        getActivity(),
                        //List Item Layout
                        R.layout.list_item_forecast,
                        // List View
                        R.id.list_item_forecast_textview,
                        //Data
                        list);
        listView = (ListView) rootView.findViewById(R.id.ListViewForecast);

        //Pasamos Adaptador
        listView.setAdapter(mforecastAdapter);
       // return rootView;

        //Evento para escuchar click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Parametros Mensaje
                Context contextShow = getActivity().getApplicationContext();
                String textShow = mforecastAdapter.getItem(position);
                Integer shortShow = Toast.LENGTH_SHORT;

                Intent showIntent = new Intent(contextShow,DetailActivity.class);
                showIntent.putExtra("Datos", textShow);
                startActivity(showIntent);



                //Mostrar mensaje
                //Toast toast = Toast.makeText(contextShow, textShow, shortShow);
                //toast.show();

            }
        });



        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        @Override
        protected String[] doInBackground(String... strings) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String postCode = strings[0];

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String[] forecastResult = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                //URI for http

                Uri.Builder newUrl = new Uri.Builder();
                newUrl.scheme("http")
                    .authority("api.openweathermap.org")
                    .appendPath("data")
                    .appendPath("2.5")
                    .appendPath("forecast")
                    .appendPath("daily")
                    .appendQueryParameter("q",strings[0])
                    .appendQueryParameter("mode","json")
                    .appendQueryParameter("units","metric")
                    .appendQueryParameter("cnt",numDays.toString());

                String myURL = newUrl.build().toString();

                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
                URL url = new URL(myURL);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
                /*
                String WEATHER_DATA_FREMONT_JUN_4 = "{\"cod\":\"200\",\"message\":2.5405,\"city\":{\"id\":\"5350734\",\"name\":\"Fremont\",\"coord\":{\"lon\":-121.982,\"lat\":37.5509},\"country\":\"United States of America\",\"population\":0},\"cnt\":7,\"list\":[{\"dt\":1401912000,\"temp\":{\"day\":28.12,\"min\":12.74,\"max\":28.12,\"night\":12.74,\"eve\":23.73,\"morn\":28.12},\"pressure\":1004.41,\"humidity\":52,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":2.41,\"deg\":263,\"clouds\":0},{\"dt\":1401998400,\"temp\":{\"day\":28.58,\"min\":10.3,\"max\":28.58,\"night\":10.3,\"eve\":22.76,\"morn\":16.08},\"pressure\":1002.75,\"humidity\":47,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":3.13,\"deg\":261,\"clouds\":0},{\"dt\":1402084800,\"temp\":{\"day\":26.73,\"min\":11.2,\"max\":27.55,\"night\":11.2,\"eve\":22.94,\"morn\":12.53},\"pressure\":1001.79,\"humidity\":50,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":2.01,\"deg\":267,\"clouds\":0},{\"dt\":1402171200,\"temp\":{\"day\":30.67,\"min\":11.81,\"max\":31.25,\"night\":11.81,\"eve\":25.92,\"morn\":15.5},\"pressure\":1001.95,\"humidity\":48,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":1.91,\"deg\":271,\"clouds\":0},{\"dt\":1402257600,\"temp\":{\"day\":16.6,\"min\":10.32,\"max\":17.52,\"night\":12.62,\"eve\":17.52,\"morn\":10.32},\"pressure\":1003.29,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":2.84,\"deg\":292,\"clouds\":0},{\"dt\":1402344000,\"temp\":{\"day\":14.82,\"min\":10.66,\"max\":16.14,\"night\":11.97,\"eve\":16.14,\"morn\":10.66},\"pressure\":1009.02,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":3.72,\"deg\":305,\"clouds\":12},{\"dt\":1402430400,\"temp\":{\"day\":15.26,\"min\":9.84,\"max\":16.75,\"night\":12.76,\"eve\":16.75,\"morn\":9.84},\"pressure\":1009.9,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":5.87,\"deg\":325,\"clouds\":0}]}";

                try {
                    JSONObject weather = new JSONObject(WEATHER_DATA_FREMONT_JUN_4);
                    JSONArray days = weather.getJSONArray("list");
                    JSONObject dayInfo = days.getJSONObject(6);
                    JSONObject tempInfo = dayInfo.getJSONObject("temp");
                    Double temperaturaMaxima = tempInfo.getDouble("max");
                    Log.v(LOG_TAG, "Lista " + temperaturaMaxima);


                } catch (JSONException e) {


                    e.printStackTrace();
                }

                Log.v(LOG_TAG, "Forecast JSON String " + forecastJsonStr);
                */



            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                forecastResult = getWeatherDataFromJson(forecastJsonStr,numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(),e);
                e.printStackTrace();
            }


            return forecastResult;
        }

        protected void onPostExecute(String[] result) {

                if(result != null) {

                    //Limpiar Adaptador
                    mforecastAdapter.clear();

                    for (String dayForecastString : result) {
                        mforecastAdapter.add(dayForecastString);

                    }
                }
        }
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
         inflater.inflate(R.menu.forecastfragment, menu);
    }

    public boolean onOptionsItemSelected (MenuItem item)
    {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            //String[] postalCode = {"94043"} ;

            /*

            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("94043");
*/
            // Alternativa mas rapida
            // new FetchWeatherTask().execute();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* The date/time conversion code is going to be moved outside the asynctask later,
 * so for convenience we're breaking it out into its own method now.
 */
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DATETIME = "dt";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        String[] resultStrs = new String[numDays];
        for(int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime = dayForecast.getLong(OWM_DATETIME);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        return resultStrs;
    }



}
