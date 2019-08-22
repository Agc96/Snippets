## LocationService

```Java
package pe.edu.pucp.a20190000.rebajatuscuentas.features.inmovable.create.presenter;

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

public class InmovableCreateLocationService extends IntentService {

    private final static String TAG = "RTC_INM_COMIDA";
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

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(LOCATION_SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
        }
    }
}
```

## InmovableCreateLocationFragment

``` Java
Log.d(TAG, "Activando GPS...");
LocationRequest locationRequest = new LocationRequest()
        .setInterval(LOCATION_INTERVAL)
        .setFastestInterval(LOCATION_FAST_INTERVAL)
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest);

final Activity activity = getActivity();
if (activity != null) {
    SettingsClient client = LocationServices.getSettingsClient(activity);
    Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
    task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
        @Override
        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
        }
    });
    task.addOnFailureListener(activity, new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        }
    });
}
```

## InmovableCreatePhotoFragment

```Java
package pe.edu.pucp.a20190000.rebajatuscuentas.features.inmovable.create;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

import pe.edu.pucp.a20190000.rebajatuscuentas.R;
import pe.edu.pucp.a20190000.rebajatuscuentas.utils.Constants;
import pe.edu.pucp.a20190000.rebajatuscuentas.utils.Image;
import pe.edu.pucp.a20190000.rebajatuscuentas.utils.Permissions;
import pe.edu.pucp.a20190000.rebajatuscuentas.utils.Utilities;

public class InmovableCreatePhotoFragment extends Fragment {
    private final static String TAG = "RTC_INM_NEW_PHOTO_FRG";
    private IInmovableCreateView mView;
    private ImageView mPhotoView;
    private String mPhotoPath;
    private Button mAddButton;
    private Button mRemoveButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IInmovableCreateView) {
            mView = (IInmovableCreateView) context;
        } else {
            throw new RuntimeException("El Activity debe implementar la interfaz IInmovableCreateView.");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout de este Fragment
        View view = inflater.inflate(R.layout.fragment_inmovable_create_photo, container, false);
        // Obtener los componentes
        mAddButton = view.findViewById(R.id.inm_create_photo_btn_add);
        mRemoveButton = view.findViewById(R.id.inm_create_photo_btn_remove);
        mPhotoView = view.findViewById(R.id.inm_create_photo_img_main);
        // Inicializar los componentes
        initializeComponents(savedInstanceState);
        return view;
    }

    public void initializeComponents(Bundle savedInstanceState) {
        // Configurar el botón de añadir una foto
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForTakePhoto();
            }
        });
        // Configurar el botón de quitar una foto
        mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePhoto();
            }
        });
        // Configurar la visibilidad del botón de remover
    }

    private void askForTakePhoto() {
        // Verificar que el dispositivo móvil cuenta con cámara
        PackageManager manager = mView.getContext().getPackageManager();
        if (!manager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Utilities.showMessage(mView.getContext(), R.string.camera_msg_unavailable);
            return;
        }
        // Verificar que se tengan los permisos necesarios para tomar la foto
        String[] permissions = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (Permissions.checkFromFragment(this, Constants.REQ_CODE_CAMERA_PERMISSIONS, permissions)) {
            takePhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Verificar si se aceptaron los permisos para el uso de la cámara y el almacenamiento externo
        if (requestCode == Constants.REQ_CODE_CAMERA_PERMISSIONS) {
            if (Permissions.checkFromResults(permissions, grantResults)) {
                takePhoto();
            } else {
                Utilities.showMessage(mView.getContext(), R.string.camera_msg_permissions);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void takePhoto() {
        PackageManager manager = mView.getContext().getPackageManager();
        // Solicitar a la cámara del celular que tome una foto
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(manager) != null) {
            File photoFile = Image.createImage(mView.getContext());
            if (photoFile != null) {
                // Guardar ubicación del archivo de la foto
                mPhotoPath = photoFile.getAbsolutePath();
                // Iniciar el Activity que maneja la cámara
                Uri photoUri = FileProvider.getUriForFile(mView.getContext(),
                        "pe.edu.pucp.a20190000.rebajatuscuentas.fileprovider", photoFile);
                Log.d(TAG, String.format("photoUri: %s", photoUri));
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            }
            startActivityForResult(takePhotoIntent, Constants.REQ_CODE_CAMERA_INTENT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQ_CODE_CAMERA_INTENT) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    processImage(data);
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d(TAG, "El usuario canceló la toma de fotos.");
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processImage(Intent data) {
        if (data == null) {
            Log.d(TAG, "Los datos del intent son nulos...");
            return;
        }
        Bundle extras = data.getExtras();
        if (extras != null) {
            // Obtener la imagen
            Bitmap photo = (Bitmap) extras.get("data");
            Log.d(TAG, String.format("URI: %s", data.getData()));
            if (photo != null) {
                // Colocar la imagen en el ImageView
                // photo = Image.rotateIfNeeded((Bitmap) extras.get("data"), mPhotoPath);
                mPhotoView.setImageBitmap(photo);
                // Activar el botón de borrar la foto
                mRemoveButton.setEnabled(true);
            }
        }
    }

    private void removePhoto() {
        // Borra la imagen del ImageView y del Fragment
        mPhotoView.setImageBitmap(null);
        mPhotoPath = null;
        // Desactiva el botón de remover foto
        mRemoveButton.setEnabled(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mView = null;
    }
}
```

## Image

```Java
/**
 * Crea una imagen temporal en el directorio reservado para uso de la aplicación.
 * @param context Contexto de la aplicación, generalmente un Activity
 * @return Un archivo temporal creado para guardar una foto, o NULL si es que no se pudo crear.
 */
public static File createImage(Context context) {
    // Formulamos el formato del archivo (sin la extensión)
    SimpleDateFormat now = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
    String filename = "RTC_PHOTO_" + now.format(new Date());
    // Obtenemos un directorio reservado para uso de la aplicación
    File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    // Creamos una imagen temporal en el directorio especificado.
    try {
        return File.createTempFile(filename, ".jpg", directory);
    } catch (IOException ex) {
        Log.e(TAG, "Error al crear la imagen", ex);
        return null;
    }
}
```
