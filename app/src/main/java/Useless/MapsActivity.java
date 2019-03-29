package Useless;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final int MY_REQUEST_INT = 177;
    private GoogleMap mMap;
    Location location;
    public static double userLat=0, userLong=0;
    KeyEvent keyEvent;
    LocationManager locationmanager;
    private EditText editText;
    private Button searchButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Log.v("1","Inside onCreate maps");
        editText = (EditText)findViewById(R.id.searchBar);
        searchButton = (Button) findViewById(R.id.searchButton);
        init();
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
        Geocoder geocoder = new Geocoder(MapsActivity.this);
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This  is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_INT);
            }

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            onMapReady(googleMap);
            return;

        }
        else{
            mMap.setMyLocationEnabled(true);

        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

         location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if(location != null)
        {
            userLat=location.getLatitude();
            userLong=location.getLongitude();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        findLoc(location.getLatitude(), location.getLongitude());
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
        Toast.makeText(MapsActivity.this,"Showing Cafes Nearby...",Toast.LENGTH_LONG).show();
        String url = stringBuilder.toString();


        Object datatransfer[] = new Object[2];
        datatransfer[0]=mMap;
        datatransfer[1]= url;
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        getNearbyPlaces.execute(datatransfer);
    }


}
