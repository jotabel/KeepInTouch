package es.iesalandalus.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import es.iesalandalus.chat.adapters.ContactosAdapter;
import es.iesalandalus.chat.models.Contactos;

public class ContactosActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    FirebaseDatabase miBase;
    DatabaseReference miReferencia;
    FirebaseAuth firebase;

    //variables para listar
    private ContactosAdapter eAdapter;
    private RecyclerView eRecyclerView;
    private ArrayList<Contactos> arrayContactos=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        cargarViews();

        arrayContactos=new ArrayList<>();


        iniciarFirebase();

        ObtenerDatos();


    }

    public void cargarViews(){

        eRecyclerView=findViewById(R.id.rvEntradas);

        eRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void controlarPerimisosContactos(){
        if (ContextCompat.checkSelfPermission(ContactosActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ContactosActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(ContactosActivity.this,
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

    public void ObtenerDatos(){

        controlarPerimisosContactos();

        String[] projeccion = new String[] { ContactsContract.Data._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE };
        String selectionClause = ContactsContract.Data.MIMETYPE + "='" +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND "
                + ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";
        String sortOrder = ContactsContract.Data.DISPLAY_NAME + " ASC";

        Cursor c = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                projeccion,
                selectionClause,
                null,
                sortOrder);




        while(c.moveToNext()){
            //textView.append(" Nombre: " + c.getString(1) + " Número: " + c.getString(2)+"\n");
            comprobarDatos(c.getString(2));
        }
        c.close();

        /*contactsCursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,   // URI de contenido para los contactos
                projection,                        // Columnas a seleccionar
                selectionClause                    // Condición del WHERE
                selectionArgs,                     // Valores de la condición
                sortOrder);                        // ORDER BY columna [ASC|DESC]*/

    }

    public boolean comprobarDatos(final String curs){

        miReferencia.child("Telefonos/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getValue().equals(curs)){
                        //Iguales

                        buscarDatosPersona(curs);

                    }else{

                        //No iguales

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return false;
    }

    public void buscarDatosPersona(final String numero){

        miReferencia.child("perfiles/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                //vacio mi arraylist
                    arrayContactos.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("numero").getValue().equals(numero)) {
                            //Iguales. AQUÍ ES DONDE SE VAN A IR INTRODUCIENDO LOS LA GENTE QUE SE
                            //PARA HACER EL RECLYCLERVIEW Y MOSTRAR A TODOS LOS CONTACTOS QUE USEN
                            //LA APLICACIÓN.

                            Contactos cont=new Contactos(ds.child("uid").getValue().toString(),ds.child("numero").getValue().toString(),ds.child("nombre").getValue().toString(),ds.child("descripcion").getValue().toString(),ds.child("imagen").getValue().toString());

                            arrayContactos.add(cont);

                        } else {

                            //No iguales

                        }
                    }
                    eAdapter = new ContactosAdapter(arrayContactos, R.layout.mostrar_conctactos);

                    eAdapter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Aquí se implenta que cuando se pulse una contacto pues se pueda ver la información del mismo

                            Intent i = new Intent(ContactosActivity.this, VerContactoActivity.class);
                            i.putExtra("uid",arrayContactos.get(eRecyclerView.getChildAdapterPosition(v)).getUid());
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
            Intent i = new Intent(ContactosActivity.this, MainActivity.class);
            startActivity(i);
        }
    }

}
