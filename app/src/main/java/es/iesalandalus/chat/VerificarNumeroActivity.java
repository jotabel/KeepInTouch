package es.iesalandalus.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

public class VerificarNumeroActivity extends AppCompatActivity {

    private String verificationId;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private EditText etCodigo;
    private String numeroTelefono,number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificar_numero);

        auth=FirebaseAuth.getInstance();

        progressBar=findViewById(R.id.progressbar);
        etCodigo=findViewById(R.id.etCodigo);

        numeroTelefono = getIntent().getStringExtra("numeroTelefono");
        number=getIntent().getStringExtra("number");

        sendVerificationCode(numeroTelefono);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                String code = etCodigo.getText().toString().trim();
                if (code.isEmpty() || code.length()<6){

                    etCodigo.setText("Enter code...");
                    etCodigo.requestFocus();
                    return;

                }
                verifyCode(code);
            }
        });

    }

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential){
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(VerificarNumeroActivity.this, PerfilActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("numeroTelefono",numeroTelefono);
                    startActivity(intent);
                }else{
                    Toast.makeText(VerificarNumeroActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendVerificationCode(String number){
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationId=s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            String code=phoneAuthCredential.getSmsCode();
            if(code != null){
                etCodigo.setText(code);
                verifyCode(code);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

            Toast.makeText(VerificarNumeroActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

}
