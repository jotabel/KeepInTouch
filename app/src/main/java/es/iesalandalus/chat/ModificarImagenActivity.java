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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class ModificarImagenActivity extends AppCompatActivity {

    private ImageView ivImagen;
    private String simagen, inombre,idescripcion,inumero,iimagen, nombre;
    private Button btnModImagen,btnModImagGuardar;

    FirebaseDatabase miBase;
    DatabaseReference miReferencia;
    FirebaseAuth firebase;

    Bitmap thumb_bitmap = null;
    StorageReference storageReference;
    ProgressDialog cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_imagen);

        inombre=getIntent().getStringExtra("nombreUsuario");
        inumero=getIntent().getStringExtra("numeroTelefono");
        idescripcion=getIntent().getStringExtra("descripcion");
        iimagen=getIntent().getStringExtra("imagen");

        ivImagen=findViewById(R.id.ivModImagen);
        btnModImagen=findViewById(R.id.btnModImagCambiarImagen);
        btnModImagGuardar=findViewById(R.id.btnModImagGuardar);

        Picasso.get().load(iimagen).into(ivImagen);

        iniciarFirebase();

        Date fecha = new Date();
        nombre=fecha.toString()+"comprimido.jpg";

        storageReference= FirebaseStorage.getInstance().getReference().child("img_comprimidas");

        cargando=new ProgressDialog(this);

        btnModImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(ModificarImagenActivity.this);
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
            Intent i = new Intent(ModificarImagenActivity.this, AjustesActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri imageuri=CropImage.getPickImageResultUri(this,data);

            //Recortar imagen
            CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON)
                    .setRequestedSize(640,640)
                    .setAspectRatio(2,1).start(ModificarImagenActivity.this);

        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                Uri resultUri = result.getUri();
                File url=new File(resultUri.getPath());

                Picasso.get().load(url).into(ivImagen);

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



                btnModImagGuardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cargando.setTitle("Subiendo foto");
                        cargando.setMessage("Espere Por favor");
                        cargando.show();

                        final StorageReference ref=storageReference.child(inumero);
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

                                Toast.makeText(ModificarImagenActivity.this,"Imagen subida con exito",Toast.LENGTH_SHORT).show();

                                guardar(simagen);

                            }
                        });

                    }
                });

            }
        }

    }

    public void guardar(final String simagen){

        if(simagen!=iimagen){

            miReferencia.child("perfiles/"+firebase.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){

                        DatabaseReference referencia = miReferencia.child("perfiles/"+firebase.getCurrentUser().getUid());
                        referencia.child("imagen").setValue(simagen);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        Intent i = new Intent(ModificarImagenActivity.this, AjustesActivity.class);
        i.putExtra("numeroTelefono",inumero);
        i.putExtra("nombreUsuario",inombre);
        i.putExtra("descripcion",idescripcion);
        i.putExtra("imagen",simagen);
        startActivity(i);
        finish();

    }


}
