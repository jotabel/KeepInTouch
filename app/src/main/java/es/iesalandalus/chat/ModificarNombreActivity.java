package es.iesalandalus.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ModificarNombreActivity extends AppCompatActivity {

    private EditText nombre;
    private String snombre, inombre,idescripcion,inumero,iimagen;

    FirebaseDatabase miBase;
    DatabaseReference miReferencia;
    FirebaseAuth firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_nombre);

        inombre=getIntent().getStringExtra("nombreUsuario");
        inumero=getIntent().getStringExtra("numeroTelefono");
        idescripcion=getIntent().getStringExtra("descripcion");
        iimagen=getIntent().getStringExtra("imagen");

        nombre=findViewById(R.id.etModNombre);
        nombre.setText(inombre);

        iniciarFirebase();


    }

    public void cambiarNombre(View view){

        snombre = nombre.getText().toString();
        if(snombre!=inombre){

            miReferencia.child("perfiles/"+firebase.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){

                        DatabaseReference referencia = miReferencia.child("perfiles/"+firebase.getCurrentUser().getUid());
                        referencia.child("nombre").setValue(snombre);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        Intent i = new Intent(ModificarNombreActivity.this, AjustesActivity.class);
        i.putExtra("numeroTelefono",inumero);
        i.putExtra("nombreUsuario",snombre);
        i.putExtra("descripcion",idescripcion);
        i.putExtra("imagen",iimagen);
        startActivity(i);
        finish();

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
            Intent i = new Intent(ModificarNombreActivity.this, AjustesActivity.class);
            startActivity(i);
            finish();
        }
    }

}
