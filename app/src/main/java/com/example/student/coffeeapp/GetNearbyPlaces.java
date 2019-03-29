package com.example.student.coffeeapp;

import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {
    GoogleMap mMap;
    String url;
    InputStream is;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    String data;
    public static List<String> cafelist;
    public View id;
    @Override
    protected String doInBackground(Object... params) {

        cafelist = new ArrayList();
        mMap=(GoogleMap)params[0];
        url= (String)params[1];
        id= (View)params[2];


        try{
            URL myurl= new URL(url);
            HttpURLConnection httpURLConnection= (HttpURLConnection)myurl.openConnection();
            httpURLConnection.connect();
            int code = httpURLConnection.getResponseCode();

            is = httpURLConnection.getInputStream();

            bufferedReader = new BufferedReader(new InputStreamReader(is));
            stringBuilder = new StringBuilder();

            String line ="";

            while((line= bufferedReader.readLine())!=null) {
                stringBuilder.append(line);
            }

            data = stringBuilder.toString();


        }
        catch (MalformedURLException e)
        {
           e.printStackTrace();

        } catch (IOException e) {
            Snackbar snackbar = Snackbar.make(id, "Please Check Your Network Connection!", Snackbar.LENGTH_LONG);
            snackbar.show();
            e.printStackTrace();
        }


        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        try{
            JSONObject parentObject = new JSONObject(s);
            JSONArray result = parentObject.getJSONArray("results");

            JSONArray sortedJsonArray = new JSONArray();

            List<JSONObject> jsonValues = new ArrayList<JSONObject>();
            for (int i = 0; i < result.length(); i++) {
                jsonValues.add(result.getJSONObject(i));
            }
            Collections.sort( jsonValues, new Comparator<JSONObject>() {
                //You can change "Name" with "ID" if you want to sort by ID
                private static final String KEY_NAME = "rating";

                @Override
                public int compare(JSONObject a, JSONObject b) {
                    String valA = new String();
                    String valB = new String();

                    try {
                        valA = (String) a.get(KEY_NAME).toString();
                        valB = (String) b.get(KEY_NAME).toString();
                    }
                    catch (JSONException e) {
                        //do something
                    }

                    return -valA.compareTo(valB);
                    //if you want to change the sort order, simply use the following:
                    //return -valA.compareTo(valB);
                }
            });

            for (int i = 0; i < result.length(); i++) {
                sortedJsonArray.put(jsonValues.get(i));
            }



            for(int i=0; i<result.length();i++)
            {
                JSONObject jsonObject = sortedJsonArray.getJSONObject(i);
                JSONObject locationObj= jsonObject.getJSONObject("geometry").getJSONObject("location");
                String latitude= locationObj.getString("lat");
                String longitude = locationObj.getString("lng");
                JSONObject nameObject = sortedJsonArray.getJSONObject(i);

                String name_restaurant = nameObject.getString("name");
                String vicinity=  nameObject.getString("vicinity");
                double rating=  nameObject.getDouble("rating");
                cafelist.add(name_restaurant+"\nRating: "+rating+"\n"+vicinity);
                LatLng latLng = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(name_restaurant);

                Location loc = new Location("");

                if(MapFragment.userLong!=0 && MapFragment.userLat!=0)
                {
                loc.setLongitude(MapFragment.userLong);
                loc.setLatitude(MapFragment.userLat);
                }

                Location loc1 = new Location("");
                loc1.setLatitude(Double.parseDouble(latitude));
                loc1.setLongitude(Double.parseDouble(longitude));

                double distanceInMeters = Math.floor(loc.distanceTo(loc1));

                markerOptions.snippet(vicinity+"\n"+distanceInMeters+" meters away");
                markerOptions.position(latLng);
                mMap.addMarker(markerOptions);

            }

        }catch (JSONException e)
        {
           Log.d("error","Kaitaru hotay");
           e.printStackTrace();
        }catch(NullPointerException e)
        {
            Log.d("error","Kaitaru null hotay");
            e.printStackTrace();

        }
    }
}
