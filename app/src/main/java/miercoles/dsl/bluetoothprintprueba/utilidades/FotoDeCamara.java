package miercoles.dsl.bluetoothprintprueba.utilidades;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by usuario on 13/01/2018.
 */

/*
ESTA CLASE ES UNA MEJORA DE CameraPhoto DE LA LIBRERIA PhotoUtils PARA QUE FUNCIONE EN ANDROID N, POR CUESTION DE
LAS URI QUE SON DE TIPO FILE
 */
public class FotoDeCamara {
    final String TAG = this.getClass().getSimpleName();
    private String photoPath;
    private Context context;

    public String getPhotoPath() {
        return this.photoPath;
    }

    public FotoDeCamara(Context context) {
        this.context = context;
    }

    public Intent takePhotoIntent() throws IOException {
        Intent in = new Intent("android.media.action.IMAGE_CAPTURE");
        if(in.resolveActivity(this.context.getPackageManager()) != null) {
            File photoFile = this.createImageFile();
            if(photoFile != null) {
                Uri uri;

                // Si la version es inferior a lollipop
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                    uri = Uri.fromFile(photoFile);
                }else{// Si es mayor o igual a lollipop
                    uri = FileProvider.getUriForFile(context,
                            "miercoles.dsl.bluetoothprintprueba.provider",
                            photoFile);
                }

                in.putExtra("output", uri);
            }
        }

        return in;
    }

    private File createImageFile() throws IOException {
        String timeStamp = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        storageDir.mkdirs();
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        this.photoPath = image.getAbsolutePath();
        return image;
    }

    public void addToGallery() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(this.photoPath);
        //Uri contentUri = Uri.fromFile(f); // obsoleto en android N
        Uri contentUri;

        // Si la version es inferior a lollipop
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            contentUri = Uri.fromFile(f);
        }else{// Si es mayor o igual a lollipop
            contentUri = FileProvider.getUriForFile(context,
                    "miercoles.dsl.bluetoothprintprueba.provider",
                    f);
        }

        mediaScanIntent.setData(contentUri);
        this.context.sendBroadcast(mediaScanIntent);
    }
}
