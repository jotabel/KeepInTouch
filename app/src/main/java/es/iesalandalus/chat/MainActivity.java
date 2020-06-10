package es.iesalandalus.chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import es.iesalandalus.chat.adapters.ChatsAdapter;
import es.iesalandalus.chat.models.Chat;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String numeroTelefono;

    FirebaseDatabase miBase;
    DatabaseReference miReferencia;
    FirebaseAuth firebase;

    private String loschats;

    private String codigochat;

    private DrawerLayout drawerLayout;
    NavigationView navigationView;

    String imagen="https://firebasestorage.googleapis.com/v0/b/proyectochat-d3ed4.appspot.com/o/img_comprimidas%2Fgenerica.jpg?alt=media&token=2bb98c5d-c677-4fc7-8cdc-b074c056106c";

    private String sdescripcion, snombre;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    ImageView verImagen;
    TextView nombre,descripcion;

    private ChatsAdapter eAdapter;
    private RecyclerView eRecyclerView;
    private ArrayList<Chat> arrayChat=new ArrayList<>();
    private String fecha, mensaje;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciarFirebase();

        controlarPerimisosContactos();

        cargarViewsChats();

        arrayChat=new ArrayList<>();

        obtenerPerfilFirebase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        numeroTelefono = getIntent().getStringExtra("numeroTelefono");


        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        iniciarNavigation();


    }

    public void controlarPerimisosContactos(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
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

                                System.out.println("Esto que se?"+ ds.getKey());
                                System.out.println("y esto? "+data.child("chats").getKey());

                                if(data.child("chats").child(ds.getKey()).exists()) {
                                        codigochat = ds.getKey();
                                        if (data.child("numero").getValue().equals(ds.getValue()) && ds.getKey().equals(codigochat)) {

                                            for (DataSnapshot losChatsGuardados : dataSnapshot.child("chats").getChildren()) {


                                                for (DataSnapshot snap : losChatsGuardados.getChildren()) {

                                                    if (snap.exists()) {
                                                        mensaje = snap.child("mensaje").getValue().toString();
                                                        Long codigoHora = Long.parseLong(snap.child("hora").getValue().toString());
                                                        Date d = new Date(codigoHora);
                                                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                                                        fecha = sdf.format(d);
                                                        loschats = losChatsGuardados.getKey();
                                                    }
                                                }


                                            }


                                            Chat c = new Chat(data.child("nombre").getValue().toString(), fecha,
                                                    data.child("imagen").getValue().toString(), mensaje, loschats);
                                            arrayChat.add(c);



                                    }
                                }
                            }

                        }

                    }

                    eAdapter = new ChatsAdapter(R.layout.mostrar_chats, arrayChat);

                    eAdapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Aquí se implenta que cuando se pulse una contacto pues se pueda ver la información del mismo

                            Intent i = new Intent(MainActivity.this, VerChatActivity.class);
                            i.putExtra("idconversacion",arrayChat.get(eRecyclerView.getChildAdapterPosition(v)).getUid());
                            i.putExtra("numero",numeroTelefono);
                            i.putExtra("nombre",snombre);
                            i.putExtra("imagen",arrayChat.get(eRecyclerView.getChildAdapterPosition(v)).getImagen());
                            i.putExtra("main","viene");
                            i.putExtra("otroNombre",arrayChat.get(eRecyclerView.getChildAdapterPosition(v)).getNombre());
                            startActivity(i);
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
        /*if (id == R.id.action_settings) {
            return true;
        }*/

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
