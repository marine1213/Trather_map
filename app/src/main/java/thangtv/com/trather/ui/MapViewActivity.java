package thangtv.com.trather.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import de.hdodenhof.circleimageview.CircleImageView;
import thangtv.com.trather.R;
import thangtv.com.trather.adapters.MapPlaceAutocompleteAdapter;
import thangtv.com.trather.apis.Apis;
import thangtv.com.trather.commons.Const;
import thangtv.com.trather.controller.PlaceData;
import thangtv.com.trather.controller.RotaTask;
import thangtv.com.trather.ui.helper.ViewHelper;
import thangtv.com.trather.ui.route.ViewRouteActivity;

public class MapViewActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback {

    private final String TAG = "PathGoogleMapActivity";

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private TextView navName;
    private TextView navMail;
    private CircleImageView navAvatar;
    private NavigationView navigationView;

    GoogleMap googleMap;

    @Override
    protected void onResume() {
        super.onResume();

        //get user
        ParseUser user = ParseUser.getCurrentUser();

        //Set up navigation header
        navName.setText(user.getString("name"));
        navMail.setText(user.getEmail());
        ParseFile file = user.getParseFile("avatar");
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                navAvatar.setImageBitmap(bitmap);
            }
        });

        //Set checked item back to map
        navigationView.setCheckedItem(R.id.map);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        //set up toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header);
        navName = (TextView) header.findViewById(R.id.nav_user_name);
        navMail = (TextView) header.findViewById(R.id.nav_user_email);
        navAvatar = (CircleImageView) header.findViewById(R.id.nav_avatar);


        //set up navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.nav_open, R.string.nav_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //set up navigationview on item click listener
        navigationView.setNavigationItemSelectedListener(this);


        //Set up toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_reorder_black_18dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });
        }

        //Set up map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.view_route_map);
        mapFragment.getMapAsync(this);
        setupAutoComplete();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        new RotaTask(this, map, "Thanh Nhan", "Dai hoc Bach Khoa Ha Noi").execute();
    }

    public void bt_map_click(View v) {
        switch (v.getId()) {
            case R.id.bt_map_findRoute:
                Intent intent = new Intent(MapViewActivity.this, ViewRouteActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_map_myPlaces:
                Location location = PlaceData.getGpsData(this);
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    addMarker(latLng, "Your location", 500);
                }
                break;
        }
    }

    private MapPlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private ImageView imClear;

    private void setupAutoComplete() {

        mAutocompleteView = (AutoCompleteTextView)findViewById(R.id.autoTv_place);
        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteView.addTextChangedListener(mAutocompleteTextWatcher);
        mAutocompleteView.setOnEditorActionListener(mAutocompleteTextEditor);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new MapPlaceAutocompleteAdapter(this, Apis.mGoogleApiClient, Const.MAP_BOUNDS_NORTH_VIETNAM,
                null,imClear);
        mAutocompleteView.setAdapter(mAdapter);

        imClear = (ImageView)findViewById(R.id.bt_map_toolbar_clear);
        imClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteView.setText("");
                ViewHelper.hideKeyboard(MapViewActivity.this);
            }
        });
    }

    TextWatcher mAutocompleteTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length()==0){
                imClear.setImageResource(R.drawable.ic_search_black_24dp);
            }else {
                imClear.setImageResource(R.drawable.ic_clear_black_24dp);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    TextView.OnEditorActionListener mAutocompleteTextEditor = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.FLAG_EDITOR_ACTION) {
                ViewHelper.hideKeyboard(MapViewActivity.this);
            }
            return false;
        }
    };

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            PlaceData.getPlaceWithId(placeId,mUpdatePlaceDetailsCallback);
        }
    };


    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // move map view
            addMarker(place.getLatLng(),place.getName().toString(),2000);

            // Hide keyboard
            ViewHelper.hideKeyboard(MapViewActivity.this);

            Log.i(TAG, "Place details received: " + place.getName());
            places.release();
        }
    };

    Marker addMarker(LatLng latLng, String name, int timeMoveCam_ms) {
        //        LatLng sydney = new LatLng(-34, 151);
        Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(name)
//                .snippet("Kiel is cool")
//                .icon(BitmapDescriptorFactory
//                        .fromResource(R.drawable.photo))
        );
        if (timeMoveCam_ms>0){
//            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng,15,0,0)));
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng,16,0,0)), timeMoveCam_ms, null);
        }
        return marker;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        ParseUser user = ParseUser.getCurrentUser();
        switch (item.getItemId()) {
            case R.id.profile:
                Intent intent = new Intent(MapViewActivity.this, ProfileActivity.class);
                intent.putExtra("user", user.getUsername());
                startActivity(intent);
                break;
            case R.id.notifications:
                break;
            case R.id.map:
                break;
            case R.id.directions:
                break;
            case R.id.settings:
                break;
            case R.id.log_out:
                user.logOut();
                Intent intent1 = new Intent(MapViewActivity.this, MainActivity.class);
                startActivity(intent1);
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}