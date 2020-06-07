package es.iesalandalus.chat.models;

public class chats {

    private String uid;
    private String numero;
    private String nombre;
    private String descripcion;
    private String imagen;
    private String chats;//se guardará un codigo de cada chat, las dos personas que crean el chat
                        //y lo comparten tendrán el mismo código, y cada persona tendrá su propia
                        //copia.

    public chats(){

    }

    public chats(String numero, String nombre, String descripcion, String imagen) {
        this.numero = numero;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    public chats(String uid, String numero, String nombre, String descripcion, String imagen) {
        this.uid = uid;
        this.numero=numero;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getChats() {
        return chats;
    }

    public void setChats(String chats) {
        this.chats = chats;
    }
}
