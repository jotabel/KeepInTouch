package es.iesalandalus.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.iesalandalus.chat.adapters.ChatsAdapter;
import es.iesalandalus.chat.adapters.ContactosAdapter;
import es.iesalandalus.chat.models.Chat;
import es.iesalandalus.chat.models.Contactos;

public class ChatsActivity extends AppCompatActivity {

    FirebaseDatabase miBase;
    DatabaseReference miReferencia;
    FirebaseAuth firebase;
    private ChatsAdapter eAdapter;
    private RecyclerView eRecyclerView;
    private ArrayList<Chat> arrayChat=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);


        cargarViewsChats();

        obtenerPerfilFirebase();

        if(!getIntent().getStringExtra("uid").isEmpty()){



        }

    }

    public void cargarViewsChats(){

        eRecyclerView=findViewById(R.id.rvChats);

        eRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }



    public void obtenerPerfilFirebase(){

        miReferencia.child("perfiles/"+firebase.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("chats").hasChildren()){

                    for (DataSnapshot ds : dataSnapshot.child("chats").getChildren()) {

                        if(ds.exists()){

                            System.out.println(ds.getValue());

                        }

                    }
                    eAdapter = new ChatsAdapter(R.layout.mostrar_chats, arrayChat);

                    eAdapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(ChatsActivity.this, VerContactoActivity.class);
                            startActivity(i);
                            finish();

                        }
                    });

                    eRecyclerView.setAdapter(eAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
