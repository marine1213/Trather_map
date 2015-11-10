package thangtv.com.trather.commons;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Nguyen on 10/29/2015.
 */
public class Const {
    public static final int PLACE_PICKER_REQUEST = 1000;

    // The minimum distance to change Updates in meters
    public static final long PLACE_MIN_DISTANCE_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    public static final long PLACE_MIN_TIME_UPDATES = 1000 * 60 * 1; // 1 minute

    // Bound the suggested result in Viet Nam first
    public static final LatLngBounds MAP_BOUNDS_NORTH_VIETNAM = new LatLngBounds(
            new LatLng(20.814116, 103.706864), new LatLng(22.697388, 106.589234));
}
