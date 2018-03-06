package miercoles.dsl.bluetoothprintprueba.listas.dispositivosbluetooth;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.frosquivel.magicalcamera.MagicalCamera;
import com.frosquivel.magicalcamera.MagicalPermissions;
import com.kyanogen.signatureview.SignatureView;

import java.util.ArrayList;

import miercoles.dsl.bluetoothprintprueba.R;

public class DibujarActivity extends AppCompatActivity {

    private static final int COD_PERMISOS = 426;
    private LinearLayout layoutProgresoImagen;
    private SignatureView signatureView;
    private MagicalCamera magicalCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dibujar);

        layoutProgresoImagen = (LinearLayout) findViewById(R.id.layout_progreso_firma);
        signatureView = (SignatureView) findViewById(R.id.firma);

        if(getSupportActionBar() !=  null){
            getSupportActionBar().setTitle("Dibujar");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        String[] permissions = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        MagicalPermissions magicalPermissions = new MagicalPermissions(this, permissions);
        magicalCamera = new MagicalCamera(this, 40, magicalPermissions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_firma_dibujar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.item_guardar_dibujo:
                if(verificarPermisos()) {
                    layoutProgresoImagen.setVisibility(View.VISIBLE);// Hacemos visible el progreso

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            String nombreImg = "dibujo";

                            /*Guarda la foto en la memoria interna del dispositivo, si no tiene espacio, pasa a
                            * guardarla en la SD card, retorna la ruta en la cual almacenó la foto */
                            final String rutaImg = magicalCamera.savePhotoInMemoryDevice(
                                    signatureView.getSignatureBitmap(),// bitmap de la foto a guardar
                                    nombreImg,// nombre con el que se guardará la imgImagen
                                    "DibujosImprimir",// nombre de la carpeta donde se guardarán las fotos
                                    MagicalCamera.PNG,// formato de compresion
                                    false // true: le agrega la fecha al nombre de la foto para no replicarlo
                            );

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intentAtras = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("rutaImg", rutaImg);
                                    intentAtras.putExtras(bundle);
                                    setResult(Activity.RESULT_OK, intentAtras);

                                    finish();
                                    layoutProgresoImagen.setVisibility(View.GONE);// ocultamos el progreso
                                }
                            });

                        }
                    }).start();
                }
                return true;
            case R.id.item_limpiar_dibujo:
                signatureView.clearCanvas();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean verificarPermisos(){
        ArrayList<String> permisosFaltantes = new ArrayList<>();

        boolean permisoEscrituraSD = ( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);

        boolean permisoLecturaSD = ( ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);


        if(!permisoEscrituraSD){
            permisosFaltantes.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if(!permisoLecturaSD){
            permisosFaltantes.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if(permisosFaltantes.size() > 0){
            String[] permisos = new String[permisosFaltantes.size()];
            permisos = permisosFaltantes.toArray(permisos);

            ActivityCompat.requestPermissions(this, permisos, COD_PERMISOS);

            return false;
        }else{
            return true;
        }
    }
}
