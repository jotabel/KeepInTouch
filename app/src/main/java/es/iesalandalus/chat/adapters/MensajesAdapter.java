package es.iesalandalus.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.sql.ClientInfoStatus;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.iesalandalus.chat.MensajeRecibir;
import es.iesalandalus.chat.R;
import es.iesalandalus.chat.models.Mensajes;

public class MensajesAdapter extends RecyclerView.Adapter<HolderMensaje> {

    private List<MensajeRecibir> listMensaje = new ArrayList<>();
    private Context c;

    public MensajesAdapter(Context c){
        this.c=c;
    }

    public void addMensaje(MensajeRecibir m){

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

        holder.getNombre().setText(listMensaje.get(position).getNombre());
        holder.getMensaje().setText(listMensaje.get(position).getMensaje());
        holder.getFotoMensaje().setVisibility(View.VISIBLE);
        Glide.with(c).load(listMensaje.get(position).getFotoPerfil()).into(holder.getFotoMensaje());
        System.out.println("la imagen es esta: "+listMensaje.get(position).toString());
        if(listMensaje.get(position).getType_mensaje().equals("2")){
            holder.getFotoMensajeEnviado().setVisibility(View.VISIBLE);
            System.out.println("la imagen es esta: "+listMensaje.get(position).toString());
            Picasso.get().load(listMensaje.get(position).getUrlFoto()).into(holder.getFotoMensajeEnviado());
        }else if(listMensaje.get(position).getType_mensaje().equals("1")){
            holder.getFotoMensajeEnviado().setVisibility(View.GONE);
        }

        Long codigoHora = listMensaje.get(position).getHora();
        Date d= new Date(codigoHora);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        holder.getHora().setText(sdf.format(d));

    }

    @Override
    public int getItemCount() {
        return listMensaje.size();
    }
}
