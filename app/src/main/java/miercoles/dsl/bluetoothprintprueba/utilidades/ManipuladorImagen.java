package miercoles.dsl.bluetoothprintprueba.utilidades;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by CristianAlvarez on 14/10/2017.
 */

public class ManipuladorImagen {
    private static File rutaNuevaImg;

    public static long pesoKBytesFile(String rutaFile){
        Log.e("rutaFile", rutaFile+" ---");
        File file = new File(rutaFile+"");

        return (file.length() / 1024);
    }

    public static Bitmap redimencionar(Context contexto, Bitmap bitmapOriginal, int nuevoAncho, int nuevoAlto){
        int anchoOriginal = bitmapOriginal.getWidth();
        int altoOriginal = bitmapOriginal.getHeight();

        // calculamos el escalado de la imagen destino
        float anchoEscalado = ((float) nuevoAncho) / anchoOriginal;
        float altoEscalado = ((float) nuevoAlto) / altoOriginal;

        // Creamos una matrix para manipular la imagen
        Matrix matrix = new Matrix();
        // Redimencionamos el bitmap
        matrix.postScale(anchoEscalado, altoEscalado);

        // Volvemos a crear la imagen con los nuevos valores
        Bitmap bitmapEscalado = Bitmap.createBitmap(bitmapOriginal, 0, 0, anchoOriginal, altoOriginal,
                matrix, true);

        return bitmapEscalado;
    }

    public Bitmap readBitmap(Uri selectedImage, Context contexto) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;
        AssetFileDescriptor fileDescriptor =null;

        try {
            fileDescriptor = contexto.getContentResolver().openAssetFileDescriptor(selectedImage,"r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally{
            try {
                bm = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                fileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        return bm;
    }
}
