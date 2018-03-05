package miercoles.dsl.bluetoothprintprueba.listas.dispositivosbluetooth;

/**
 * Created by usuario on 3/03/2018.
 */

public class ItemDispositivo {
    private String nombre, direccion;

    public ItemDispositivo(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
