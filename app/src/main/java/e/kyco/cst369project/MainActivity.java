package e.kyco.cst369project;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";
    //error user will get if they dont have correct version of google play services
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_PERMISSION_CODE = 1;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Marker marker;
    private Location currentLocationSend;
    private double latit, longitu;
    Button btnCam;
    private ArrayList<Markers> markerList;
    public int count = 0;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    DatabaseReference myRefer = FirebaseDatabase.getInstance().getReference();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("markers");

    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }


        Intent intentFrom = getIntent();
        Bundle gettingExtras = intentFrom.getExtras();
        if (gettingExtras != null) {
            Log.d(TAG, "sending to marker method");


            Markers mein = new Markers();


            mein.setLatit(gettingExtras.getDouble("lat"));
            mein.setLongitu(gettingExtras.getDouble("long"));
            mein.setTitle(gettingExtras.getString("description"));


            markerList.add(mein);


            myRefer.push().setValue(mein);

            setMarker(markerList.get(count));

            count++;
        }


        myRefer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    Markers markerz = s.getValue(Markers.class);
                    markerList.add(markerz);
                    for (int i = 0; i < markerList.size(); i++) {
                        LatLng latLng = new LatLng((markerz.getLatit()), markerz.getLongitu());
                        if (mMap != null) {
                            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(markerz.getTitle()));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        markerList = new ArrayList<>();


        getLocationPermission();

        if (isServicesOK()) {
            initMap();
        }


        Button btnCam = (Button) findViewById(R.id.cameraButton);
        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
                Log.d(TAG, "camBtn: opening camera");

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });


    }


    private void initMap() {
        Log.d(TAG, "intMap: intitializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MainActivity.this);

    }


    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: Checking Google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //they have the version, user can use map
            Log.d(TAG, "isServicesOK: Google Play Services are working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //error occured but can be fixed, ex: version issue
            Log.d(TAG, "isServicesOK: Error occured but we can fix it.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Map request can't be made", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }

        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);

        }
    }


    protected void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the current location of device");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {

            if (mLocationPermissionsGranted) {

                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: location found");
                            Location currentLocation = (Location) task.getResult();
                            currentLocationSend = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "onComplete: location is null");
                            Toast.makeText(MainActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException | NullPointerException e) {
            Log.e(TAG, "getDeviceLocation: Security Exception" + e.getMessage());

        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionRequest: method called");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionRequest : permission denied");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //intilize map
                    initMap();
                }

            }
        }
    }


    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: Moving Camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

            longitu = currentLocationSend.getLongitude();
            latit = currentLocationSend.getLatitude();

            Intent intent = new Intent(MainActivity.this, PhotoActivity.class);

            intent.putExtra("image", thumbnail);
            intent.putExtra("lat", latit);
            intent.putExtra("long", longitu);

            startActivity(intent);

        }
    }


    public void setMarker(Markers markerListInside) {

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(markerListInside.getLatit(), markerListInside.getLongitu()))
                .title(markerListInside.getTitle()));


    }


    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream ByteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, ByteStream);
        byte[] b = ByteStream.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


}




