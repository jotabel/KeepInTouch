package es.iesalandalus.chat.models;

public class Chat {

    private String nombre,hora,imagen,mensaje,uid;

    public Chat(String nombre, String hora, String imagen, String mensaje,String uid) {
        this.nombre = nombre;
        this.hora = hora;
        this.imagen = imagen;
        this.mensaje = mensaje;
        this.uid=uid;
    }

    public Chat() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "nombre='" + nombre + '\'' +
                ", hora='" + hora + '\'' +
                ", imagen='" + imagen + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
