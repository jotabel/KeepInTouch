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

import static es.iesalandalus.chat.LoginActivity.pregMatch;

public class ModificarDescripcionActivity extends AppCompatActivity {

    private EditText descripcion;
    private String sdescripcion, inombre,idescripcion,inumero,iimagen;

    FirebaseDatabase miBase;
    DatabaseReference miReferencia;
    FirebaseAuth firebase;

    final String patterDescripcion = "[a-zA-Z0-9\\s]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_descripcion);

        inombre=getIntent().getStringExtra("nombreUsuario");
        inumero=getIntent().getStringExtra("numeroTelefono");
        idescripcion=getIntent().getStringExtra("descripcion");
        iimagen=getIntent().getStringExtra("imagen");

        descripcion=findViewById(R.id.etModDescripcion);

        if(!idescripcion.isEmpty()){
            descripcion.setText(idescripcion);
        }

        iniciarFirebase();

    }

    public void cambiarDescripcion(View view){

        sdescripcion = descripcion.getText().toString();

        if(!pregMatch(patterDescripcion,sdescripcion) || sdescripcion.length()>100){
            descripcion.setError("Escribe una descripción con letras y números de 0 a 100 caracteres.");
        }
        else {
            if (sdescripcion != idescripcion) {

                miReferencia.child("perfiles/" + firebase.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            DatabaseReference referencia = miReferencia.child("perfiles/" + firebase.getCurrentUser().getUid());
                            referencia.child("descripcion").setValue(sdescripcion);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            Intent i = new Intent(ModificarDescripcionActivity.this, AjustesActivity.class);
            i.putExtra("numeroTelefono", inumero);
            i.putExtra("nombreUsuario", inombre);
            i.putExtra("descripcion", sdescripcion);
            i.putExtra("imagen", iimagen);
            startActivity(i);
            finish();
        }
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
            Intent i = new Intent(ModificarDescripcionActivity.this, AjustesActivity.class);
            startActivity(i);
            finish();
        }
    }



}
