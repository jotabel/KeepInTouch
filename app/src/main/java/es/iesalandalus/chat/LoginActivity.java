package es.iesalandalus.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity{

    private EditText etTelefono;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        spinner=findViewById(R.id.spPais);
        spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,PaisesClase.countryNames));

        etTelefono=findViewById(R.id.etTelefono);

        findViewById(R.id.btnCod).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String code = PaisesClase.countryAreaCodes[spinner.getSelectedItemPosition()];

                String number = etTelefono.getText().toString().trim();

                if(number.isEmpty() || number.length()<9){
                    etTelefono.setError("Number is required");
                    etTelefono.requestFocus();
                    return;
                }

                String numeroTelefono="+"+code+number;

                Intent intent = new Intent(LoginActivity.this, VerificarNumeroActivity.class);
                intent.putExtra("numeroTelefono", numeroTelefono);
                startActivity(intent);

            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }
}
