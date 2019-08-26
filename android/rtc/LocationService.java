package pe.edu.pucp.a20190000.rebajatuscuentas.utils;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pe.edu.pucp.a20190000.rebajatuscuentas.R;
import pe.edu.pucp.a20190000.rebajatuscuentas.utils.Utilities;

public class LocationService extends IntentService {
    private final static String TAG = "RTC_LOCATION_SERVICE";
    public static final int LOCATION_SUCCESS_RESULT = 0;
    public static final int LOCATION_FAILURE_RESULT = 1;
    public static final String LOCATION_DATA_EXTRA = "";

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) return;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(LOCATION_DATA_EXTRA);

        // ...

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException ex) {
            Log.e(TAG, "Servicio no disponible, puede ser un error en la conexión a Internet", ex);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, String.format("Datos de geolocalización no válidos. Lat: %.2f, Long: %.2f.",
                    location.getLatitude(), location.getLongitude()), ex);
        }

        // Handle case where no address was found.
        if (Utilities.isEmptyList(addresses)) {
            Log.e(TAG, "No se encontraron direcciones cercanas.");
            deliverResultToReceiver(LOCATION_FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine, join them, and send them to the thread.
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(LOCATION_SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
        }
    }
}
