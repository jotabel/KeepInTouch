package es.iesalandalus.chat.models;

public class Mensajes {

    private String enviadopor;
    private String fecha;
    private String mensaje;
    private String type_mensaje;

    public Mensajes() {
    }

    public Mensajes(String enviadopor, String fecha, String mensaje, String type_mensaje) {
        this.enviadopor = enviadopor;
        this.fecha = fecha;
        this.mensaje = mensaje;
        this.type_mensaje = type_mensaje;
    }

    public String getEnviadopor() {
        return enviadopor;
    }

    public void setEnviadopor(String enviadopor) {
        this.enviadopor = enviadopor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getType_mensaje() {
        return type_mensaje;
    }

    public void setType_mensaje(String type_mensaje) {
        this.type_mensaje = type_mensaje;
    }
}
