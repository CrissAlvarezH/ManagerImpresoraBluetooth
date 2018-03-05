package miercoles.dsl.bluetoothprintprueba;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_DISPOSITIVO = 425;
    private static final int LIMITE_CARACTERES_POR_LINEA = 32;
    private static final String TAG_DEBUG = "tag_debug";

    private TextView txtLabel;
    private EditText edtTexto;
    private Button btnImprimir, btnCerrarConexion;
    private Spinner spnFuente, spnNegrita, spnAncho, spnAlto;

    // Para la operaciones con dispositivos bluetooth
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice dispositivoBluetooth;
    private BluetoothSocket bluetoothSocket;
    // identificador unico default
    private UUID aplicacionUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Para el flujo de datos de entrada y salida del socket bluetooth
    private OutputStream outputStream;
    private InputStream inputStream;
    private Thread hiloComunicacion;

    // Para el manejo de la informacion en byte que fluye en los streams
    private byte[] bufferLectura;
    private int bufferLecturaPosicion;
    // volatile: no guarda una copia en chaché para cada hilo, si no que los sincroliza cuando cambien la variable
    // de esa manera todos manejaran el mismo valor de la variable y no una copia que puede estar con valor anterior
    private volatile boolean pararLectura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLabel = (TextView) findViewById(R.id.txt_label);
        edtTexto = (EditText) findViewById(R.id.edt_texto);
        btnImprimir = (Button) findViewById(R.id.btn_imprimir);
        spnNegrita = (Spinner) findViewById(R.id.spn_negrita);
        spnAlto = (Spinner) findViewById(R.id.spn_alto);
        spnFuente = (Spinner) findViewById(R.id.spn_fuente);
        spnAncho = (Spinner) findViewById(R.id.spn_ancho);
        btnCerrarConexion = (Button) findViewById(R.id.btn_cerrar_conexion);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnImprimir.setOnClickListener(this);
        btnCerrarConexion.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_imprimir:
                if(bluetoothSocket != null){
                    try {
                        String texto = edtTexto.getText().toString() + "\n";

                        /*// si el titulo no sobrepasa los caracteres por linea
                        if(texto.length() < LIMITE_CARACTERES_POR_LINEA) {
                            // Caculamos desde caracter ponerlo para que esté centrado
                            int posicion = 16 - ((int) texto.length() / 2 );

                            // relenamos con caracteres en blanco
                            for(int i=0; i< posicion; i++){
                                texto = " " + texto;
                            }

                            texto += "\n";
                        }*/


                        OutputStream osBluetooth = bluetoothSocket.getOutputStream();

                        int fuente = Integer.parseInt(spnFuente.getSelectedItem().toString());
                        int negrita = spnNegrita.getSelectedItem().toString().equals("Si") ? 1 : 0;
                        int ancho = Integer.parseInt(spnAncho.getSelectedItem().toString());
                        int alto = Integer.parseInt(spnAlto.getSelectedItem().toString());


                        osBluetooth.write(getByteString(texto, negrita, fuente, ancho, alto));


                    } catch (IOException e) {
                        Log.e(TAG_DEBUG, "Error al escribir en el socket");

                        Toast.makeText(this, "Error al interntar imprimir", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }else{
                    Log.e(TAG_DEBUG, "Socket nulo");

                    txtLabel.setText("Impresora no conectada");
                }

                break;
            case R.id.btn_cerrar_conexion:
                cerrarConexion();

                break;
        }
    }

    public void clickBuscarDispositivosSync(View btn){
        // Cerramos la conexion antes de establecer otra
        cerrarConexion();

        Intent intentLista = new Intent(this, ListaBluetoohtActivity.class);
        startActivityForResult(intentLista, REQUEST_DISPOSITIVO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_DISPOSITIVO:
                    txtLabel.setText("Cargando...");

                    final String direccionDispositivo = data.getExtras().getString("DireccionDispositivo");
                    final String nombreDispositivo = data.getExtras().getString("NombreDispositivo");

                    // Obtenemos el dispositivo con la direccion seleccionada en la lista
                    dispositivoBluetooth = bluetoothAdapter.getRemoteDevice(direccionDispositivo);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Conectamos los dispositivos

                                // Creamos un socket
                                bluetoothSocket = dispositivoBluetooth.createRfcommSocketToServiceRecord(aplicacionUUID);
                                bluetoothSocket.connect();// conectamos el socket
                                outputStream = bluetoothSocket.getOutputStream();
                                inputStream = bluetoothSocket.getInputStream();

                                //empezarEscucharDatos();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        txtLabel.setText(nombreDispositivo + " conectada");
                                        Toast.makeText(MainActivity.this, "Dispositivo Conectado", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        txtLabel.setText("");
                                        Toast.makeText(MainActivity.this, "No se pudo conectar el dispositivo", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Log.e(TAG_DEBUG, "Error al conectar el dispositivo bluetooth");

                                e.printStackTrace();
                            }
                        }
                    }).start();

                    break;
            }
        }
    }



    private void empezarEscucharDatos(){

        final byte saltoLinea = 10;

        // Inicializamos las variables para leer el inputStream
        pararLectura = false;
        bufferLecturaPosicion = 0;
        bufferLectura = new byte[1024];

        hiloComunicacion = new Thread(new Runnable() {
            @Override
            public void run() {
                // Mientras el hilo no sea interrumpido y la variable booleana esté en false
                while (!Thread.currentThread().isInterrupted() && !pararLectura){
                    try {
                        // Cantidad de bytes disponibles para leer al inputStream
                        int bytesDisponibles = inputStream.available();

                        if(bytesDisponibles > 0){
                            byte[] paqueteDeBytes = new byte[bytesDisponibles];// para guardar los bytes del inputStream
                            inputStream.read(paqueteDeBytes);// leemos los byte y colocamos en paqueteDeBytes

                            for(int i = 0; i < bytesDisponibles; i++){
                                byte b = paqueteDeBytes[i];// leemos los bytes uno a uno, lo guardamos en b

                                // Si es un salto de linea asumimos que es un renglon y lo pasamos a String
                                // Para ponerlo en el txtLabel, si no lo es guardamos en bufferLectura
                                // el byte leido hasta completar el renglon
                                if(b == saltoLinea){
                                    Log.v(TAG_DEBUG, "Encontramos salto de linea");

                                    // array de bytes para copiar el array bufferLectura y pasarlo a String
                                    byte[] bytesCopia = new byte[bufferLecturaPosicion];

                                    // Copiamos el array
                                    System.arraycopy(bufferLectura, 0, bytesCopia, 0, bytesCopia.length);

                                    // Codificamos el array de byten en caracteres tipo ASCII de estados unidos
                                    final String datosString = new String(bytesCopia, "US-ASCII");

                                    // Colocamos la posicion en cero para leer una nueva linea y guardarla en bufferLectura
                                    bufferLecturaPosicion = 0;

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // colocamos lo leido en el inputStream en el EditText
                                            txtLabel.setText(datosString);
                                        }
                                    });
                                }else{
                                    Log.v(TAG_DEBUG, "leemos un byte");

                                    // Si no es un salto de linea es otro caracter y por tanto lo guardamos
                                    bufferLectura[bufferLecturaPosicion++] = b;
                                }
                            }
                        }else{
                            Log.v(TAG_DEBUG, "no hay bytes disponibles para leer");
                        }


                    } catch (IOException e) {
                        pararLectura = true;

                        Log.e(TAG_DEBUG, "Error ecuchar datos");
                        e.printStackTrace();
                    }
                }
            }
        });

        hiloComunicacion.start();

    }

    private void cerrarConexion(){
        try {
            if(bluetoothSocket != null ) {
                if(outputStream != null) outputStream.close();
                pararLectura = true;
                if(inputStream != null) inputStream.close();
                bluetoothSocket.close();
                txtLabel.setText("Conexion terminada");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * (font:A font:B)
     * @param str
     * @param bold
     * @param font
     * @param widthsize
     * @param heigthsize
     * @return
     */
    public static byte[] getByteString(String str, int bold, int font, int widthsize, int heigthsize){

        if(str.length() == 0 | widthsize<0 | widthsize >3 | heigthsize<0 | heigthsize>3
                | font<0 | font>1)
            return null;

        byte[] strData = null;
        try
        {
            strData = str.getBytes("GBK");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return null;
        }

        byte[] command = new byte[strData.length + 9];

        byte[] intToWidth = { 0x00, 0x10, 0x20, 0x30 };//
        byte[] intToHeight = { 0x00, 0x01, 0x02, 0x03 };//

        command[0] = 27;
        command[1] = 69;
        command[2] = ((byte)bold);
        command[3] = 27;
        command[4] = 77;
        command[5] = ((byte)font);
        command[6] = 29;
        command[7] = 33;
        command[8] = (byte) (intToWidth[widthsize] + intToHeight[heigthsize]);

        System.arraycopy(strData, 0, command, 9, strData.length);
        return command;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cerrarConexion();
    }
}
