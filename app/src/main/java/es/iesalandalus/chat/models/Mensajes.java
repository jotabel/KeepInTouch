package es.iesalandalus.chat.models;

public class Mensajes {

    private String enviadopor;
    private String urlFoto;
    private String mensaje;
    private String type_mensaje;
    private String nombre;
    private String fotoPerfil;

    public Mensajes() {
    }

    public Mensajes(String enviadopor, String mensaje, String type_mensaje, String nombre, String fotoPerfil) {
        this.enviadopor = enviadopor;
        this.mensaje = mensaje;
        this.type_mensaje = type_mensaje;
        this.nombre = nombre;
        this.fotoPerfil = fotoPerfil;
    }

    public Mensajes(String enviadopor, String urlFoto, String mensaje, String type_mensaje, String nombre, String fotoPerfil) {
        this.enviadopor = enviadopor;
        this.urlFoto = urlFoto;
        this.mensaje = mensaje;
        this.type_mensaje = type_mensaje;
        this.nombre = nombre;
        this.fotoPerfil = fotoPerfil;
    }

    public String getEnviadopor() {
        return enviadopor;
    }

    public void setEnviadopor(String enviadopor) {
        this.enviadopor = enviadopor;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    @Override
    public String toString() {
        return "Mensajes{" +
                "enviadopor='" + enviadopor + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", type_mensaje='" + type_mensaje + '\'' +
                ", nombre='" + nombre + '\'' +
                ", fotoPerfil='" + fotoPerfil + '\'' +
                ", urlFoto='" + urlFoto + '\'' +
                '}';
    }
}
