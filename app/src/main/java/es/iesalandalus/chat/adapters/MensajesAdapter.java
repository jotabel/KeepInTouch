package es.iesalandalus.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.ClientInfoStatus;
import java.util.List;

import es.iesalandalus.chat.R;
import es.iesalandalus.chat.models.Mensajes;

public class MensajesAdapter extends RecyclerView.Adapter<HolderMensaje> {

    private List<Mensajes> listMensaje;


    private Context c;

    public MensajesAdapter(Context c){
        this.c=c;
    }

    public void addMensaje(Mensajes m){

        listMensaje.add(m);

        notifyItemInserted(listMensaje.size());

    }

    @NonNull
    @Override
    public HolderMensaje onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(c).inflate(R.layout.card_view_mensajes,parent,false);

        return new HolderMensaje(v);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderMensaje holder, int position) {

        holder.getHora().setText(listMensaje.get(position).getFecha());
        holder.getMensaje().setText(listMensaje.get(position).getMensaje());
    }

    @Override
    public int getItemCount() {
        return listMensaje.size();
    }
}
