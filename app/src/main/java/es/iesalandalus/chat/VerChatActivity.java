package es.iesalandalus.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;
import es.iesalandalus.chat.adapters.MensajesAdapter;
import es.iesalandalus.chat.models.Mensajes;

public class VerChatActivity extends AppCompatActivity {

    private CircleImageView civFotoPerfil;
    private TextView tvNombre;
    private RecyclerView rvMensajes;
    private EditText etMensaje;
    private ImageButton btnEnviar;

    private String numeroTelfChat;
    private String snombre,simagen;


    FirebaseDatabase miBase;
    DatabaseReference miReferencia;
    FirebaseAuth firebase;

    private MensajesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_chat);

        cargarViews();
        cargarBundles();
        iniciarFirebase();
        encontarPerfil();

        adapter=new MensajesAdapter(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miReferencia.push().setValue(new Mensajes(numeroTelfChat,"00:00",etMensaje.getText().toString(),"1"));
            }
        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                setScrollbar();

            }
        });

        miReferencia.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Mensajes m = dataSnapshot.getValue(Mensajes.class);
                adapter.addMensaje(m);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void setScrollbar(){
        rvMensajes.scrollToPosition(adapter.getItemCount()-1);
    }


    public void cargarViews(){

        civFotoPerfil=findViewById(R.id.ivVerChat);
        tvNombre=findViewById(R.id.tvVerChatNombre);
        rvMensajes=findViewById(R.id.message_view);
        etMensaje=findViewById(R.id.etVerChatMensaje);
        btnEnviar=findViewById(R.id.btnVerChatEnviar);

    }

    public void cargarBundles(){

        numeroTelfChat=getIntent().getStringExtra("numeroTelfChat");

    }

    public void iniciarFirebase(){
        FirebaseApp.initializeApp(this);
        miBase= FirebaseDatabase.getInstance();//esto es como una conexion a la DB
        miReferencia=miBase.getReference();
        if(miReferencia==null) {
            miBase.setPersistenceEnabled(true);
            miReferencia = miBase.getReference();
        }
        firebase = FirebaseAuth.getInstance();
        if (firebase.getCurrentUser() == null) {
            Intent i = new Intent(VerChatActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }

    public void encontarPerfil(){

        miReferencia.child("perfiles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    for(DataSnapshot ds : dataSnapshot.getChildren()){

                        if(ds.child("numero").equals(numeroTelfChat)){

                            simagen=dataSnapshot.child("imagen").getValue().toString();
                            snombre=dataSnapshot.child("nombre").getValue().toString();

                        }

                    }

                    Picasso.get().load(simagen).into(civFotoPerfil);
                    tvNombre.setText(snombre);

                }else{
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
