package com.example.tizos.sunshine.app;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment  {



    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        //Lista de pronosticos
        String[] ListaForecast = { "Today - Sunny - 30/11",
                                "Monday - Sunny - 33/10",
                                "Thuesday - Sunny - 32/8",
                                "Wednesday - Sunny - 30/11",

                                "Thursday - Sunny - 29/9",
                                "Friday - Cloudy - 29/10",
                                "Saturday - Foggy - 27/6"

    };
        //Agregamos la lista de pronosticos a un ArrayList de String
        ArrayList<String> list = new ArrayList<String>();
        Collections.addAll(list, ListaForecast);

        //Inicializamos un ArrayAdapter
        ArrayAdapter<String> forecastAdapter =
                new ArrayAdapter<String>(
                    //Context
                    getActivity(),
                    //List Item Layout
                    R.layout.list_item_forecast,
                    // List View
                    R.id.list_item_forecast_textview,
                    //Data
                    list);

        // Buscamos text View
        ListView listView = (ListView) rootView.findViewById(R.id.ListViewForecast);

        //Pasamos Adaptador
        listView.setAdapter(forecastAdapter);


        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<Integer, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        @Override
        protected Void doInBackground(Integer... integers) {
            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Integer postCode = integers[0];

// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

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
                    .appendQueryParameter("q","94043")
                    .appendQueryParameter("mode","json")
                    .appendQueryParameter("units","metric")
                    .appendQueryParameter("cnt","7");

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

                //Log para ver datos que llegan desde la consulta
               Log.v(LOG_TAG,"Forecast JSON String " + forecastJsonStr);


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

            return null;
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

            Integer[] array = {1} ;
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute(array);

            // Alternativa mas rapida
            // new FetchWeatherTask().execute();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
