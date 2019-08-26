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

public class PhotoFragment extends Fragment {
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
