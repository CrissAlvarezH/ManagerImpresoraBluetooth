package miercoles.dsl.bluetoothprintprueba.listas.dispositivosbluetooth;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import miercoles.dsl.bluetoothprintprueba.R;

/**
 * Created by usuario on 3/03/2018.
 */

public class DispositivosAdapter extends RecyclerView.Adapter<DispositivosAdapter.DispositivosViewHolder>{
    private MiListenerClick miListenerClick;
    private ArrayList<ItemDispositivo> dispositivos;

    public DispositivosAdapter(MiListenerClick miListenerClick, ArrayList<ItemDispositivo> dispositivos) {
        this.miListenerClick = miListenerClick;
        this.dispositivos = dispositivos;
    }

    public interface MiListenerClick {
        void clickItem(View itemView, int posicion);
    }

    public class DispositivosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView txtNombre, txtDireccion;

        public DispositivosViewHolder(View itemView) {
            super(itemView);

            txtNombre = (TextView) itemView.findViewById(R.id.txt_nombre_item);
            txtDireccion = (TextView) itemView.findViewById(R.id.txt_direccion_item);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.item_dispositivo:
                    miListenerClick.clickItem(view, getAdapterPosition());
                    break;
            }
        }
    }

    @Override
    public DispositivosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View vistaItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dispositivo_bluetooth, parent, false);


        return new DispositivosViewHolder(vistaItem);
    }

    @Override
    public int getItemCount() {
        return dispositivos.size();
    }

    @Override
    public void onBindViewHolder(DispositivosViewHolder holder, int position) {
        holder.txtNombre.setText(dispositivos.get(position).getNombre());
        holder.txtDireccion.setText(dispositivos.get(position).getDireccion());
    }

    public void setDispositivos(ArrayList<ItemDispositivo> dispositivos) {
        this.dispositivos = dispositivos;
        notifyDataSetChanged();
    }

    public ArrayList<ItemDispositivo> getDispositivos() {
        return dispositivos;
    }
}
