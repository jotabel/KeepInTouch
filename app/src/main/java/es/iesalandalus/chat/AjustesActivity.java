package es.iesalandalus.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class AjustesActivity extends AppCompatActivity {


    private String numeroTelefono,snombre,simagen,sdescripcion;
    private TextView nombre,numero,usuario;
    private ImageView imgvi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        cargarBundles();

        cargarViews();

        inicializarDatos();

    }

    public void cargarBundles(){

        numeroTelefono=getIntent().getStringExtra("numeroTelefono");
        snombre=getIntent().getStringExtra("nombreUsuario");
        sdescripcion=getIntent().getStringExtra("descripcion");
        simagen=getIntent().getStringExtra("imagen");

    }

    public void cargarViews(){

        nombre=findViewById(R.id.ajusNomUsu);
        usuario=findViewById(R.id.ajusNomUsuMod);
        numero=findViewById(R.id.ajusNumTelf);
        imgvi=findViewById(R.id.imag);

    }

    public void inicializarDatos(){

        System.out.println(snombre);
        System.out.println(numeroTelefono);

        nombre.setText(snombre);
        usuario.setText(snombre);
        numero.setText(numeroTelefono);
        Picasso.get().load(simagen).into(imgvi);

    }


    public void modificarNombre(View view){
        Intent i = new Intent(AjustesActivity.this,ModificarNombreActivity.class);
        i.putExtra("numeroTelefono",numeroTelefono);
        i.putExtra("nombreUsuario",nombre.getText().toString());
        i.putExtra("descripcion",sdescripcion);
        i.putExtra("imagen",simagen);
        startActivity(i);
        finish();
    }

    public void modificarDescripcion(View view){

        Intent i = new Intent(AjustesActivity.this,ModificarDescripcionActivity.class);
        i.putExtra("numeroTelefono",numeroTelefono);
        i.putExtra("nombreUsuario",nombre.getText().toString());
        i.putExtra("descripcion",sdescripcion);
        i.putExtra("imagen",simagen);
        startActivity(i);
        finish();

    }

    public void modificarImagen(View view){

        Intent i = new Intent(AjustesActivity.this,ModificarImagenActivity.class);
        i.putExtra("numeroTelefono",numeroTelefono);
        i.putExtra("nombreUsuario",nombre.getText().toString());
        i.putExtra("descripcion",sdescripcion);
        i.putExtra("imagen",simagen);
        startActivity(i);
        finish();

    }


}
