package es.iesalandalus.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
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
import java.sql.SQLOutput;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import es.iesalandalus.chat.adapters.MensajesAdapter;
import es.iesalandalus.chat.models.Mensajes;
import id.zelory.compressor.Compressor;

public class VerChatActivity extends AppCompatActivity {

    private CircleImageView civFotoPerfil;
    private TextView tvNombre;
    private RecyclerView rvMensajes;
    private EditText etMensaje;
    private ImageButton btnEnviar;
    private ImageButton btnEnviarFoto;

    private String numeroTelfChat,nombre,imagen,otroNumero,main="",otroNombre,otroUid;
    private String snombre,simagen,lafotoenviada,nombreImagen;

    private String idConversacion;

    private static final int PHOTO_SEND = 1;


    FirebaseDatabase miBase;
    DatabaseReference miReferencia;
    FirebaseAuth firebase;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    Bitmap thumb_bitmap = null;

    private MensajesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_chat);

        cargarViews();
        cargarBundles();
        iniciarFirebase();
        encontarPerfil();
        cargarReferencias();

        Date fecha = new Date();
        nombreImagen=fecha.toString()+"comprimido.jpg";

        storage = FirebaseStorage.getInstance();

        adapter=new MensajesAdapter(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etMensaje.getText().length()>0) {
                    if (idConversacion.length() > 1) {

                        miReferencia.child("chats").child(idConversacion).push().setValue(new MensajeEnviar(
                                numeroTelfChat, etMensaje.getText().toString(), "1", nombre, imagen, ServerValue.TIMESTAMP));
                    } else {
                        idConversacion = UUID.randomUUID().toString();

                        System.out.println("numerotlf: " + numeroTelfChat + " mensaje: " + etMensaje.getText().toString() + " Hora: " + ServerValue.TIMESTAMP.toString());

                        miReferencia.child("chats").child(idConversacion).push().setValue(new MensajeEnviar(
                                numeroTelfChat, etMensaje.getText().toString(), "1", nombre, imagen, ServerValue.TIMESTAMP));
                        miReferencia.child("perfiles").child(firebase.getCurrentUser().getUid()).child("chats").child(idConversacion).setValue(otroNumero);
                        miReferencia.child("perfiles").child(otroUid).child("chats").child(idConversacion).setValue(otroNumero);
                    }
                }
                etMensaje.setText("");
            }
        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

        btnEnviarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),1);
            }
        });

        miReferencia.child("chats").child(idConversacion).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){

                    System.out.println(idConversacion);

                    if(main.equals("viene")) {
                        MensajeRecibir m = dataSnapshot.getValue(MensajeRecibir.class);
                        System.out.println();
                        adapter.addMensaje(m);
                    }else{
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            MensajeRecibir m = ds.getValue(MensajeRecibir.class);
                            adapter.addMensaje(m);
                        }
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void setScrollbar(){
        rvMensajes.scrollToPosition(adapter.getItemCount()-1);
    }


    public void cargarViews(){

        civFotoPerfil=findViewById(R.id.ivVerChat);
        tvNombre=findViewById(R.id.tvVerChatNombre);
        rvMensajes=findViewById(R.id.message_view);
        etMensaje=findViewById(R.id.etVerChatMensaje);
        btnEnviar=findViewById(R.id.btnVerChatEnviar);
        btnEnviarFoto=findViewById(R.id.ibGaleria);

    }

    public void cargarBundles(){

        numeroTelfChat=getIntent().getStringExtra("numero");
        idConversacion=getIntent().getStringExtra("idconversacion");
        nombre=getIntent().getStringExtra("nombre");
        imagen=getIntent().getStringExtra("imagen");
        otroNumero=getIntent().getStringExtra("otroNumero");
        main=getIntent().getStringExtra("main");
        otroNombre=getIntent().getStringExtra("otroNombre");

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
            Intent i = new Intent(VerChatActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }



    public void encontarPerfil(){

        miReferencia.child("perfiles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    for(DataSnapshot ds : dataSnapshot.getChildren()){

                        if(ds.child("numero").getValue().equals(otroNumero) || ds.child("nombre").getValue().equals(otroNombre)){

                            simagen=ds.child("imagen").getValue().toString();
                            snombre=ds.child("nombre").getValue().toString();
                            otroUid=ds.child("uid").getValue().toString();
                        }
                        if(ds.child("uid").getValue().equals(firebase.getCurrentUser().getUid())){

                            nombre = ds.child("nombre").getValue().toString();
                            imagen = ds.child("imagen").getValue().toString();
                            numeroTelfChat = ds.child("numero").getValue().toString();
                        }

                    }

                    Picasso.get().load(simagen).into(civFotoPerfil);
                    tvNombre.setText(snombre);

                }else{
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void cargarReferencias(){

        if(getIntent().getStringExtra("idconversacion").length()>0)
            storageReference= FirebaseStorage.getInstance().getReference().child("imagenes_chats").child(idConversacion);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== PHOTO_SEND && resultCode == Activity.RESULT_OK){
            Uri imageuri=CropImage.getPickImageResultUri(this,data);

            //Recortar imagen
            CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON)
                    .setRequestedSize(640,640)
                    .setAspectRatio(2,1).start(VerChatActivity.this);

        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                Uri resultUri = result.getUri();
                File url=new File(resultUri.getPath());

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

                final StorageReference ref=storageReference.child(nombreImagen);
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

                        lafotoenviada=downloaduri.toString();

                        MensajeEnviar m = new MensajeEnviar(numeroTelfChat,lafotoenviada,"Foto","2",nombre,imagen,ServerValue.TIMESTAMP);
                        miReferencia.child("chats").child(idConversacion).push().setValue(m);


                    }
                });

            }
        }

    }
}
