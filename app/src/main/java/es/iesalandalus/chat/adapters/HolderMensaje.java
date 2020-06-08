package es.iesalandalus.chat.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import es.iesalandalus.chat.R;

public class HolderMensaje extends RecyclerView.ViewHolder {

    private TextView nombre;
    private TextView mensaje;
    private TextView hora;
    private CircleImageView fotoMensaje;
    private ImageView fotoMensajeEnviado;

    public HolderMensaje(@NonNull View itemView) {
        super(itemView);

        nombre=itemView.findViewById(R.id.tvVerChatMensajeNombre);
        mensaje=itemView.findViewById(R.id.tvVerChatMensajeMensaje);
        hora=itemView.findViewById(R.id.tvVerChatMensajeHora);
        fotoMensaje=itemView.findViewById(R.id.ivVerChatMensaje);
        fotoMensajeEnviado=itemView.findViewById(R.id.mensajeFoto);
    }

    public ImageView getFotoMensajeEnviado() {
        return fotoMensajeEnviado;
    }

    public void setFotoMensajeEnviado(ImageView fotoMensajeEnviado) {
        this.fotoMensajeEnviado = fotoMensajeEnviado;
    }

    public TextView getNombre() {
        return nombre;
    }

    public void setNombre(TextView nombre) {
        this.nombre = nombre;
    }

    public CircleImageView getFotoMensaje() {
        return fotoMensaje;
    }

    public void setFotoMensaje(CircleImageView fotoMensaje) {
        this.fotoMensaje = fotoMensaje;
    }

    public TextView getMensaje() {
        return mensaje;
    }

    public void setMensaje(TextView mensaje) {
        this.mensaje = mensaje;
    }

    public TextView getHora() {
        return hora;
    }

    public void setHora(TextView hora) {
        this.hora = hora;
    }
}
