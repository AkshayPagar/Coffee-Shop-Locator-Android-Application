package com.example.student.coffeeapp;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.student.coffeeapp.GetNearbyPlaces;
import com.example.student.coffeeapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    public static final int MY_REQUEST_INT = 177;
    private GoogleMap mMap;
    Location location;
    public static double userLat=0, userLong=0;
    private EditText editText;
    private Button searchButton;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        // Inflate the layout for this fragment

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        Log.v("1","Inside onCreate maps");
        editText =(EditText) view.findViewById(R.id.searchBar);
        searchButton = (Button) view.findViewById(R.id.searchButton);

        init();
        LocationManager manager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.map2), "GPS is not enabled. Do you want to enable GPS?", Snackbar.LENGTH_LONG);
            View v= snackbar.getView();
            v.setClickable(true);
            v.setFocusable(true);
            CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)v.getLayoutParams();
            params.gravity = Gravity.TOP;
            v.setLayoutParams(params);
            snackbar.show();
        }
        mapFragment.getMapAsync(this);

    }

    private void init()
    {
        Log.d("OP", "Init initializing");
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocate();
            }
        });
    }

    private void geoLocate() {

        String searchStr= editText.getText().toString();
        Log.d("OP", searchStr);
        Geocoder geocoder = new Geocoder(getActivity());
         List<Address> list = new ArrayList<>();
        try{
            list= geocoder.getFromLocationName(searchStr,1);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        if(list.size()>0)
        {
            Address address = list.get(0);
            Log.d("OP", " "+address.getLatitude()+ " "+ address.getLongitude());
            findLoc(address.getLatitude(), address.getLongitude());
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.map2),"Please allow location permission", Snackbar.LENGTH_LONG);
            snackbar.show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{ACCESS_COARSE_LOCATION,
                        ACCESS_FINE_LOCATION}, MY_REQUEST_INT);
            }
            return;

        }
        else{
            mMap.setMyLocationEnabled(true);

        }

        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if(location != null)
        {
            userLat=location.getLatitude();
            userLong=location.getLongitude();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
            View locationButton = ((View) getActivity().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
            findLoc(location.getLatitude(), location.getLongitude());
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults)
    {

        if(requestCode== MY_REQUEST_INT){
           onMapReady(mMap);
        }
    }





    public void findLoc(double lat, double longi) {

        LatLng l = new LatLng(lat, longi);
        // mMap.addMarker(new MarkerOptions().position(l).title("Foundry Commons"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 15));
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+lat+","+longi);
        stringBuilder.append("&radius="+1500);
        stringBuilder.append("&type="+"cafe");
        stringBuilder.append("&key=" + "AIzaSyAhZSWY8MxriIkVvPPuwyPsN3ZeSy0uSN8");
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.map2),"Showing Cafes Nearby "+editText.getText().toString(), Snackbar.LENGTH_LONG);
        snackbar.show();

        String url = stringBuilder.toString();


        Object datatransfer[] = new Object[3];
        datatransfer[0]=mMap;
        datatransfer[1]= url;
        datatransfer[2] = getActivity().findViewById(R.id.map2);
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        getNearbyPlaces.execute(datatransfer);

    }
}
