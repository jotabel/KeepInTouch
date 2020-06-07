package es.iesalandalus.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import es.iesalandalus.chat.models.Telefono;
import es.iesalandalus.chat.models.chats;
import id.zelory.compressor.Compressor;

public class PerfilActivity extends AppCompatActivity {

    private String numeroTelefono,number;
    private ImageView imagen;
    private EditText nombre, descripcion;
    private String snombre,sdescripcion,simagen;
    private Button siguiente,btnCambiarImagen;

    private String nombreImagen;

    Bitmap thumb_bitmap = null;
    StorageReference storageReference;
    ProgressDialog cargando;

    DatabaseReference imgref;

    private final String TABLA="perfiles";

    FirebaseDatabase miBase;
    DatabaseReference miReferencia;
    FirebaseAuth firebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        cargarViews();
        cargarReferencias();

        iniciarFirebase();

        Date fecha = new Date();
        nombreImagen=fecha.toString()+"comprimido.jpg";

        cargando=new ProgressDialog(this);

        btnCambiarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(PerfilActivity.this);
            }
        });

    }

    public void cargarReferencias(){
        imgref= FirebaseDatabase.getInstance().getReference().child("Fotos_subidas");
        storageReference= FirebaseStorage.getInstance().getReference().child("img_comprimidas");
    }

    public void cargarViews(){

        numeroTelefono = getIntent().getStringExtra("numeroTelefono");
        number=getIntent().getStringExtra("number");
        imagen = findViewById(R.id.ivImagenPerfil);
        nombre = findViewById(R.id.etNombre);
        descripcion = findViewById(R.id.etDesc);
        btnCambiarImagen = findViewById(R.id.btnCambiarImagen);

        siguiente = findViewById(R.id.btnSiguiente);

    }

    public void cargarDatos(){

        snombre = nombre.getText().toString();
        sdescripcion = descripcion.getText().toString();

    }


    public void cargarImagen(View view){
        cargandoImagen();
    }

    private void cargandoImagen(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("imagen/");
        startActivityForResult(intent.createChooser(intent,"Seleccione la aplicación"),10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri imageuri=CropImage.getPickImageResultUri(this,data);

            //Recortar imagen
            CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON)
                    .setRequestedSize(640,640)
                    .setAspectRatio(2,1).start(PerfilActivity.this);

        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                Uri resultUri = result.getUri();
                File url=new File(resultUri.getPath());

                Picasso.get().load(url).into(imagen);

                //Ahora vamos a comprimir la imagen.
                try{
                    thumb_bitmap=new Compressor(this).setMaxWidth(640).setMaxHeight(480)
                            .setQuality(90).compressToBitmap(url);
                }catch (IOException e){
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,90,byteArrayOutputStream);
                final byte [] thumb_byte = byteArrayOutputStream.toByteArray();

                //fin del compresor.



                siguiente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cargando.setTitle("Creando nuevo usuario");
                        cargando.setMessage("Espere Por favor");
                        cargando.show();

                        final StorageReference ref=storageReference.child(numeroTelefono);
                        UploadTask uploadTask=ref.putBytes(thumb_byte);

                        //subir imagen en storage
                        Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                if(!task.isSuccessful()){
                                    throw Objects.requireNonNull(task.getException());
                                }

                                return ref.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Uri downloaduri = task.getResult();
                                cargando.dismiss();

                                simagen=downloaduri.toString();

                                Toast.makeText(PerfilActivity.this,"Imagen subida con exito",Toast.LENGTH_SHORT).show();

                                guardar(simagen);

                            }
                        });

                    }
                });

            }
        }

    }

    private void iniciarFirebase() {
        miBase=FirebaseDatabase.getInstance();//esto es como una conexion a la DB
        miReferencia=miBase.getReference();
        if(miReferencia==null) {
            miBase.setPersistenceEnabled(true);
            miReferencia = miBase.getReference();
        }
        firebase = FirebaseAuth.getInstance();
    }

    public void guardar (String nomImagen){

        cargarDatos();

        chats e = new chats(firebase.getCurrentUser().getUid(),numeroTelefono,snombre,sdescripcion,nomImagen);
        miReferencia.child(TABLA).child(e.getUid()).setValue(e);

        Telefono t = new Telefono(firebase.getUid(),numeroTelefono);
        miReferencia.child("Telefonos").child(t.getUid()).setValue(t.getNumero());

        Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
        intent.putExtra("numeroTelefono",numeroTelefono);
        startActivity(intent);
        finish();
    }

    public void btnguardar(View v){

        if(simagen.isEmpty()){
            guardar("generica.jpg");
        }else{
            guardar(simagen);
        }


    }

}
