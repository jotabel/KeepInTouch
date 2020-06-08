package es.iesalandalus.chat;

import java.util.Map;

import es.iesalandalus.chat.models.Mensajes;

public class MensajeEnviar extends Mensajes {

    private Map hora;

    public MensajeEnviar() {
    }

    public MensajeEnviar(String enviadopor, String mensaje, String type_mensaje, String nombre, String fotoPerfil, Map hora) {
        super(enviadopor, mensaje, type_mensaje, nombre, fotoPerfil);
        this.hora = hora;
    }

    public MensajeEnviar(String enviadopor, String urlFoto, String mensaje, String type_mensaje, String nombre, String fotoPerfil, Map hora) {
        super(enviadopor, urlFoto, mensaje, type_mensaje, nombre, fotoPerfil);
        this.hora = hora;
    }

    public Map getHora() {
        return hora;
    }

    public void setHora(Map hora) {
        this.hora = hora;
    }
}
