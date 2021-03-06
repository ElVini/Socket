package com.example.practica16;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.UserManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText etUsuario, etIp;
    private String userName, direccion;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUsuario = (EditText) findViewById(R.id.etUsuario);
        etIp = (EditText) findViewById(R.id.etIp);

        SharedPreferences preferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        userName = preferences.getString("nombreUsuario", "");
        direccion = preferences.getString("direccion", "");

        if(userName.length() > 0 && direccion.length() > 0) {
            Intent intent = new Intent(this, Contactos.class);
            intent.putExtra("nombreUsuario", userName);
            intent.putExtra("direccion", direccion);
            startActivity(intent);
            finish();
        }
    }

    public void guardar(View v) {
        switch(v.getId()) {
            case R.id.btnGuardar:
                userName = etUsuario.getText().toString();
                direccion = etIp.getText().toString();

                if(userName.length() == 0 && direccion.length() == 0) {
                    Toast.makeText(this, "Favor de rellenar todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences preferences = getSharedPreferences("User", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("nombreUsuario", etUsuario.getText().toString());
                editor.putString("direccion", etIp.getText().toString());
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
