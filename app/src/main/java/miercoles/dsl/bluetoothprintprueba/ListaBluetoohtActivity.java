package miercoles.dsl.bluetoothprintprueba;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import miercoles.dsl.bluetoothprintprueba.listas.dispositivosbluetooth.DispositivosAdapter;
import miercoles.dsl.bluetoothprintprueba.listas.dispositivosbluetooth.ItemDispositivo;

public class ListaBluetoohtActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private ArrayList<ItemDispositivo> dispositivos;
    private DispositivosAdapter adapterDispositivos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_bluetooht);

        recycler = (RecyclerView) findViewById(R.id.recycler_dispositivos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);

        dispositivos = new ArrayList<ItemDispositivo>();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter != null){
            if(!bluetoothAdapter.isEnabled()){// si no está activado
                // Mandamos a activarlo
                Intent habilitarBluIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(habilitarBluIntent, 243);
            }else {

                // Obtenemos la lista de dispositivos sincronizados
                Set<BluetoothDevice> dispositivosSync = bluetoothAdapter.getBondedDevices();

                // Si hay dispositivos sincronizados
                if(dispositivosSync.size() > 0){
                    // Llenamos el array de dispositivos para pasarlo al adapter
                    for(BluetoothDevice dispositivo : dispositivosSync){
                        dispositivos.add(new ItemDispositivo(dispositivo.getName(),  dispositivo.getAddress()));
                    }
                }
            }
        }else{
            Toast.makeText(this, "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
        }


        adapterDispositivos = new DispositivosAdapter(new EscuchadorClick(), dispositivos);

        recycler.setAdapter(adapterDispositivos);
    }

    private class EscuchadorClick implements DispositivosAdapter.MiListenerClick{

        @Override
        public void clickItem(View itemView, int posicion) {
            // Mandamos la direccion al onActivityResult de la actividad que lanzo esta
            Bundle bundle = new Bundle();
            bundle.putString("DireccionDispositivo", adapterDispositivos.getDispositivos().get(posicion).getDireccion());
            bundle.putString("NombreDispositivo", adapterDispositivos.getDispositivos().get(posicion).getNombre());
            Intent intentPaAtras = new Intent();
            intentPaAtras.putExtras(bundle);
            setResult(Activity.RESULT_OK, intentPaAtras);
            finish();
        }
    }
}
