package com.example.practica16;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText etUsuario;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUsuario = (EditText) findViewById(R.id.etUsuario);

        SharedPreferences preferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        userName = preferences.getString("nombreUsuario", "");

        if(userName.length() > 0) {
            Intent intent = new Intent(this, Contactos.class);
            intent.putExtra("nombreUsuario", userName);
            startActivity(intent);
            finish();
        }
    }

    public void guardar(View v) {
        switch(v.getId()) {
            case R.id.btnGuardar:
                if(etUsuario.getText().toString().length() == 0) {
                    Toast.makeText(this, "El campo de usuario es obligatorio", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences preferences = getSharedPreferences("User", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("nombreUsuario", etUsuario.getText().toString());
                editor.commit();

                Intent intent = new Intent(this, Contactos.class);
                intent.putExtra("nombreUsuario", userName);
                startActivity(intent);
                finish();
                break;
        }
    }
}
