package es.iesalandalus.chat.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import es.iesalandalus.chat.R;

public class HolderMensaje extends RecyclerView.ViewHolder {

    private TextView mensaje;
    private TextView hora;

    public HolderMensaje(@NonNull View itemView) {
        super(itemView);

        mensaje=itemView.findViewById(R.id.etVerChatMensaje);
        hora=itemView.findViewById(R.id.tvChatsHora);

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
