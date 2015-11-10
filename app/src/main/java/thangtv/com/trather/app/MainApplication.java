package thangtv.com.trather.app;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

import thangtv.com.trather.apis.Apis;
import thangtv.com.trather.ui.helper.LruBitmapCache;

/**
 * Created by Nguyen on 10/28/2015.
 */
public class MainApplication extends Application {

    private static String APPLICATION_ID = "AGvShiLrsaTQf93lhDDO2NmzT2dBvPk6oxpgLIYh";
    private static String CLIENT_KEY = "RqEzhoVUNXoXo50UE4lcDocqX0L6Uv3LhMpMW0uQ";

    private static final String TAG = MainApplication.class.getSimpleName();


    private static MainApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;


        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        Apis.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .build();

        Apis.mGoogleApiClient.connect();

        Apis.locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        initParseService();
    }

    //-----------------------------Parse----------------------------------

    void initParseService(){
        //enable local datastore
        Parse.enableLocalDatastore(this);

        //add initialization code
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);

        ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }

    //---------------------------map--------------------------------------
    public static synchronized MainApplication getInstance() {
        return mInstance;
    }

    //--------------------------image loader----------------------------------

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private LruBitmapCache mLruBitmapCache;

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
