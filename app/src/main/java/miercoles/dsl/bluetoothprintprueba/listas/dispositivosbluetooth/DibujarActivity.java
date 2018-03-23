package miercoles.dsl.bluetoothprintprueba.listas.dispositivosbluetooth;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.kyanogen.signatureview.SignatureView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import miercoles.dsl.bluetoothprintprueba.R;
import miercoles.dsl.bluetoothprintprueba.utilidades.Constantes;

public class DibujarActivity extends AppCompatActivity {

    private static final int COD_PERMISOS = 426;
    private LinearLayout layoutProgresoImagen;
    private SignatureView signatureView;
    private String ruta;

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

                            FileOutputStream out = null;
                            try {
                                File fileImg = Constantes.getRutaDestinoImg("dibujo");
                                ruta = fileImg.getAbsolutePath();

                                out = new FileOutputStream(fileImg);

                                // Comprimimos el bitmap en el Stream
                                signatureView.getSignatureBitmap().compress(Bitmap.CompressFormat.PNG, 100, out);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (out != null) {
                                        out.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intentAtras = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("rutaImg", ruta);
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
