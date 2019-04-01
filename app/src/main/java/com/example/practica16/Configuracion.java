package com.example.practica16;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Configuracion extends AppCompatActivity {

    private EditText etUsuarioConfig, etIpConfig;
    private String userName, direccion;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        etUsuarioConfig = (EditText) findViewById(R.id.etUsuarioConfig);
        etIpConfig = (EditText) findViewById(R.id.etIpConfig);

        bundle = getIntent().getExtras();

        userName = bundle.getString("nombreUsuario");
        direccion = bundle.getString("direccion");

        etUsuarioConfig.setText(userName);
        etIpConfig.setText(direccion);
    }

    public void guardarConfig(View v) {
        switch(v.getId()) {
            case R.id.btnGuardar:
                userName = etUsuarioConfig.getText().toString();
                direccion = etIpConfig.getText().toString();

                if(userName.length() == 0 && direccion.length() == 0) {
                    Toast.makeText(this, "Favor de rellenar todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences preferences = getSharedPreferences("User", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("nombreUsuario", etUsuarioConfig.getText().toString());
                editor.putString("direccion", etIpConfig.getText().toString());
                editor.commit();

                Intent intent = new Intent(this, Contactos.class);
                intent.putExtra("nombreUsuario", userName);
                intent.putExtra("direccion", direccion);
                startActivity(intent);
                finish();
                break;
        }
    }
}
