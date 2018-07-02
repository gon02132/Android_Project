package com.study.googlemapsandroidapiexample.Main_Page;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//길찾기 함수 -> github에서 제공중
//한국의 경우 정부에서 Googlemap길찾기 기능을 거부해서 받지못함
//일본(Go japan)버튼을 누를시 polyline으로 길 경로가 나옴
//생성자에 필요한 초기화 작업외에는 github에서 소스를 가져옴

public class Directions_Functions extends FragmentActivity{

    private static final int    LOCATION_REQUEST = 500;
    private GoogleMap           main_Map, mini_Map;
    private ArrayList<LatLng>   japan_two_marker;
    private Get_set_package     get_set_package;
    public  Polyline            main_polyline, mini_polyline;

    //생성자
    public Directions_Functions(Get_set_package get_set_package) {

        this.get_set_package    = get_set_package;

        main_Map                = get_set_package.get_main_map();
        mini_Map                = get_set_package.get_mini_map();
    }

    //그리기 실행!
    public void call_Function(LatLng start_latLng, LatLng end_latLng){

        japan_two_marker = new ArrayList<LatLng>();
        japan_two_marker.add(start_latLng);
        japan_two_marker.add(end_latLng);

        //현재 위치와 가야할 자판기의 위치 사이에 길경로를 그린다.
        String url = getRequestUrl(japan_two_marker.get(0), japan_two_marker.get(1));
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        taskRequestDirections.execute(url);

    }

    private String getRequestUrl(LatLng origin, LatLng dest) {
        //Value of origin
        String str_org  = "origin=" + origin.latitude +","+origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude+","+dest.longitude;
        //Set value enable the sensor
        String sensor   = "sensor=false";
        //Mode for find direction
        String mode     = "mode=driving";
        //Build the full param
        String param    = str_org +"&" + str_dest + "&" +sensor+"&" +mode;
        //Output format
        String output   = "json";
        //Create url to request
        String url      = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString               = "";
        InputStream inputStream             = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream                         = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader       = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer   = new StringBuffer();
            String line                 = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    main_Map.setMyLocationEnabled(true);
                }
                break;
        }
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject                       = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject                          = new JSONObject(strings[0]);
                DirectionsParser directionsParser   = new DirectionsParser();
                routes                              = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points          = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {

                //기존의 폴리라인을 없앤다
                if(main_polyline != null)
                    main_polyline.remove();

                if(mini_polyline != null)
                    mini_polyline.remove();

                //폴리라인을 그린다
                main_polyline = main_Map.addPolyline(polylineOptions);
                mini_polyline = mini_Map.addPolyline(polylineOptions);

            } else {
                //Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
