package thangtv.com.trather.apis;

import android.location.LocationManager;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Nguyen on 10/29/2015.
 */
public class Apis {

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    public static GoogleApiClient mGoogleApiClient;

    public static LocationManager locationManager;
}
