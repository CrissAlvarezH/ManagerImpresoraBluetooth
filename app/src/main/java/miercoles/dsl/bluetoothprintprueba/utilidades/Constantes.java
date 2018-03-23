package miercoles.dsl.bluetoothprintprueba.utilidades;

import android.os.Environment;

import java.io.File;

/**
 * Created by usuario on 22/03/2018.
 */

public class Constantes {

    public static void crearRutaCarpetaImg(){
        File dir = new File(Environment.getExternalStorageDirectory().toString()+"/Pictures/ImagenesImprimir");

        if(!dir.exists()){
            dir.mkdir();
        }
    }

    public static File getRutaDestinoImg(String nombreImg){
        return new File(Environment.getExternalStorageDirectory().toString()+"/Pictures/ImagenesImprimir/"+nombreImg+".png");
    }
}
