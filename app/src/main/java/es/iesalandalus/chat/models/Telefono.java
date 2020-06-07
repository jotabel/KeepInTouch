package es.iesalandalus.chat.models;

public class Telefono {

    String uid;
    String numero;

    public Telefono(String uid, String numero) {
        this.uid = uid;
        this.numero = numero;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
