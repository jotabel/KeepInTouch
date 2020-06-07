package es.iesalandalus.chat;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import es.iesalandalus.chat.adapters.ChatsAdapter;
import es.iesalandalus.chat.models.Chat;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String numeroTelefono;

    FirebaseDatabase miBase;
    DatabaseReference miReferencia;
    FirebaseAuth firebase;

    private DrawerLayout drawerLayout;
    NavigationView navigationView;

    String imagen="https://firebasestorage.googleapis.com/v0/b/proyectochat-d3ed4.appspot.com/o/img_comprimidas%2Fgenerica.jpg?alt=media&token=2bb98c5d-c677-4fc7-8cdc-b074c056106c";

    private String sdescripcion, snombre;

    ImageView verImagen;
    TextView nombre,descripcion;

    private ChatsAdapter eAdapter;
    private RecyclerView eRecyclerView;
    private ArrayList<Chat> arrayChat=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciarFirebase();

        cargarViewsChats();

        arrayChat=new ArrayList<>();

        obtenerPerfilFirebase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        numeroTelefono = getIntent().getStringExtra("numeroTelefono");


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        iniciarNavigation();


    }

    public void cargarViewsChats(){

        eRecyclerView=findViewById(R.id.rvChats);

        eRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }



    public void obtenerPerfilFirebase(){

        miReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("perfiles/").child(firebase.getCurrentUser().getUid()).child("chats").hasChildren()) {
                    //vacio mi arraylist
                    arrayChat.clear();

                    for (DataSnapshot ds : dataSnapshot.child("perfiles").child(firebase.getCurrentUser().getUid()).child("chats").getChildren()) {

                        if (ds.exists()) {

                            for (DataSnapshot data : dataSnapshot.child("perfiles").getChildren()){

                                if(data.child("numero").getValue().equals(ds.getValue())){

                                    for(DataSnapshot losChatsGuardados : dataSnapshot.child("chats").getChildren()){

                                        Chat c = new Chat(data.child("nombre").getValue().toString(),losChatsGuardados.child(losChatsGuardados.getKey()).child("fecha").getValue().toString(),
                                                data.child("imagen").getValue().toString(),losChatsGuardados.child(losChatsGuardados.getKey()).child("mensaje").getValue().toString());

                                        arrayChat.add(c);

                                    }


                                }

                            }

                        }

                    }

                    eAdapter = new ChatsAdapter(R.layout.mostrar_chats, arrayChat);

                    eRecyclerView.setAdapter(eAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void iniciarFirebase(){
        FirebaseApp.initializeApp(this);
        miBase=FirebaseDatabase.getInstance();//esto es como una conexion a la DB
        miReferencia=miBase.getReference();
        if(miReferencia==null) {
            miBase.setPersistenceEnabled(true);
            miReferencia = miBase.getReference();
        }
        firebase = FirebaseAuth.getInstance();
        if (firebase.getCurrentUser() == null) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }

    public void iniciarNavigation(){
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        nombre = navigationView.getHeaderView(0).findViewById(R.id.nav_header_nombre);
        descripcion = navigationView.getHeaderView(0).findViewById(R.id.nav_header_descripcion);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();


        verImagen=navigationView.getHeaderView(0).findViewById(R.id.imag);
        miReferencia.child("perfiles/"+firebase.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    imagen=dataSnapshot.child("imagen").getValue().toString();
                    numeroTelefono=dataSnapshot.child("numero").getValue().toString();
                    sdescripcion=dataSnapshot.child("descripcion").getValue().toString();
                    snombre=dataSnapshot.child("nombre").getValue().toString();
                }else{
                    imagen="https://firebasestorage.googleapis.com/v0/b/proyectochat-d3ed4.appspot.com/o/img_comprimidas%2Fgenerica.jpg?alt=media&token=2bb98c5d-c677-4fc7-8cdc-b074c056106c";
                }
                Picasso.get().load(imagen).into(verImagen);
                nombre.setText(snombre);
                descripcion.setText(sdescripcion);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.ajustes:
                abrirAjustes();
                break;
            case R.id.contactos:
                abrirContactos();
                break;
        }

        return false;

    }

    public void abrirAjustes(){

        Intent i = new Intent(MainActivity.this,AjustesActivity.class);
        i.putExtra("numeroTelefono",numeroTelefono);
        i.putExtra("nombreUsuario",nombre.getText().toString());
        i.putExtra("descripcion",descripcion.getText().toString());
        i.putExtra("imagen",imagen);
        startActivity(i);

    }

    public void abrirContactos(){
        Intent i = new Intent(MainActivity.this,ContactosActivity.class);
        startActivity(i);
    }

}
