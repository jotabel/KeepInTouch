package es.iesalandalus.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class VerContactoActivity extends AppCompatActivity {

    private ImageView ivImagen;
    private TextView tvNombre, tvDescripcion, tvNumero;

    private String sdescripcion, snombre,snumero,simagen;

    private String uid,idconversacion="";

    FirebaseDatabase miBase;
    DatabaseReference miReferencia;
    FirebaseAuth firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_contacto);

        cargarViews();

        cargarBundles();

        iniciarFirebase();

        cargarDatos();

        FloatingActionButton fab = findViewById(R.id.btnVerNuevo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Aquí hay que poner que se crea un chat nuevo
                Intent i = new Intent(VerContactoActivity.this, VerChatActivity.class);
                i.putExtra("idconversacion",idconversacion);
                i.putExtra("otroNumero",snumero);
                i.putExtra("main","");
                startActivity(i);
                finish();
            }
        });

    }

    public void cargarBundles(){

        uid=getIntent().getStringExtra("uid");

    }

    public void cargarViews(){

        ivImagen=findViewById(R.id.ivVerContacto);
        tvNombre=findViewById(R.id.tvVerNombre);
        tvNumero=findViewById(R.id.tvVerNumero);
        tvDescripcion=findViewById(R.id.tvVerDescripcion);

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
            Intent i = new Intent(VerContactoActivity.this, MainActivity.class);
            startActivity(i);
        }
    }

    public void cargarDatos(){
        miReferencia.child("perfiles/"+uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    simagen=dataSnapshot.child("imagen").getValue().toString();
                    snumero=dataSnapshot.child("numero").getValue().toString();
                    sdescripcion=dataSnapshot.child("descripcion").getValue().toString();
                    snombre=dataSnapshot.child("nombre").getValue().toString();

                    if(dataSnapshot.child("chats").exists()){

                        for (DataSnapshot ds : dataSnapshot.child("chats").getChildren()){

                            System.out.println("mi numero de telefono: "+firebase.getCurrentUser().getPhoneNumber());
                            System.out.println("la key: "+ds.getValue());

                            if (ds.getValue().toString().equals(firebase.getCurrentUser().getPhoneNumber())){

                                idconversacion=ds.getKey();

                                System.out.println(idconversacion);

                            }

                        }

                    }

                }else{
                    simagen="https://firebasestorage.googleapis.com/v0/b/proyectochat-d3ed4.appspot.com/o/img_comprimidas%2Fgenerica.jpg?alt=media&token=2bb98c5d-c677-4fc7-8cdc-b074c056106c";
                }
                Picasso.get().load(simagen).into(ivImagen);
                tvNombre.setText(snombre);
                tvDescripcion.setText(sdescripcion);
                tvNumero.setText(snumero);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
