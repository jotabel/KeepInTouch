package es.iesalandalus.chat;

import es.iesalandalus.chat.models.Mensajes;

public class MensajeRecibir extends Mensajes {

    private Long hora;

    public MensajeRecibir() {
    }

    public MensajeRecibir(Long hora) {
        this.hora = hora;
    }

    public MensajeRecibir(String enviadopor, String urlFoto, String mensaje, String type_mensaje, String nombre, String fotoPerfil, Long hora) {
        super(enviadopor, urlFoto, mensaje, type_mensaje, nombre, fotoPerfil);
        this.hora = hora;
    }

    public Long getHora() {
        return hora;
    }

    public void setHora(Long hora) {
        this.hora = hora;
    }

    @Override
    public String toString() {
        return super.toString()+" MensajeRecibir{" +
                "hora=" + hora +
                '}';
    }
}
