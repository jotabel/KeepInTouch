package es.iesalandalus.chat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.iesalandalus.chat.R;
import es.iesalandalus.chat.models.Chat;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> implements View.OnClickListener {

    private int resource;
    private ArrayList<Chat> listaChats;

    private View.OnClickListener listener;

    public ChatsAdapter(int resource, ArrayList<Chat> listaChats) {
        this.resource = resource;
        this.listaChats = listaChats;
    }

    @Override
    public void onClick(View v) {

        if(listener!=null){
            listener.onClick(v);
        }

    }
    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @NonNull
    @Override
    public ChatsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        view.setOnClickListener(this);

        return new ChatsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsAdapter.ViewHolder holder, int position) {

        Chat chat=listaChats.get(position);

        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference imagen = storageRef.child("img_comprimidas/"+chat.getImagen());

        final long ONE_MEGABYTE = 1024 * 1024;
        imagen.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                holder.mProgressBar.setVisibility(View.GONE);
                holder.ivImagen.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                holder.mProgressBar.setVisibility(View.GONE);
                holder.ivImagen.setVisibility(View.VISIBLE);
                //holder.ivImagen.setImageResource(R.drawable.ic_error_carga);
            }
        });

        Picasso.get().load(chat.getImagen()).into(holder.ivImagen);

        holder.tvNombre.setText(chat.getNombre());
        holder.tvMensaje.setText(chat.getMensaje());
        holder.tvHora.setText(chat.getHora());

    }

    @Override
    public int getItemCount() {
        return listaChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView tvNombre,tvHora,tvMensaje;
        private ImageView ivImagen;
        private ProgressBar mProgressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view=itemView;

            this.tvNombre=itemView.findViewById(R.id.tvChatsNombre);
            this.tvHora=itemView.findViewById(R.id.tvChatsHora);
            this.tvMensaje=itemView.findViewById(R.id.tvChatsMensaje);
            this.ivImagen=itemView.findViewById(R.id.ivChats);
            this.mProgressBar=itemView.findViewById(R.id.progress);

        }
    }
}
