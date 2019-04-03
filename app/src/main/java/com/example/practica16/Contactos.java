package com.example.practica16;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class Contactos extends AppCompatActivity {

    private Bundle bundle;
    private String userName, mySocketId, direccion, userSelected, selectedSocket;
    private Socket mSocket;
    private Spinner spnUsuarios;
    private ContactsListAdapter listAdapter;
    private MessagesListAdapter messagesAdapter;
    private ListView lstMensajes;
    private EditText etMensaje;
    ArrayList<Usuario> usuarios;
    ArrayList<Mensaje> mensajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        etMensaje = (EditText) findViewById(R.id.etMensaje);
        lstMensajes = (ListView) findViewById(R.id.lstMensajes);

        bundle = getIntent().getExtras();
        userName = bundle.getString("nombreUsuario");
        direccion = bundle.getString("direccion");

        try {
            mSocket = IO.socket("http://" + direccion);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        usuarios = new ArrayList<>();
        mensajes = new ArrayList<>();

        spnUsuarios = (Spinner) findViewById(R.id.spnUsuarios);

        spnUsuarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userSelected = usuarios.get(i).nombreUsuario;
                selectedSocket = usuarios.get(i).sessionId;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        mSocket.connect();

        mSocket.on("myId", setSocketId);

        mSocket.on("usersConnected", getUsersConnected);

        mSocket.on("newMsg", handleNewMessage);
    }

    public void enviarMensaje(View v) {
        switch(v.getId()) {
            case R.id.btnEnviar:
                if(etMensaje.getText().toString().length() == 0) {
                    Toast.makeText(this, "Escribir un mensaje", Toast.LENGTH_SHORT).show();
                    return;
                }

                Mensaje mensaje = new Mensaje();
                Gson gson = new Gson();
                mensaje.mensaje = etMensaje.getText().toString();
                mensaje.nombre = userName;
                mensaje.clienteId = selectedSocket;

                mSocket.emit("aTodos", gson.toJson(mensaje));
                mensajes.add(mensaje);

                messagesAdapter = new MessagesListAdapter(this, R.layout.lista_mensajes, mensajes);
                lstMensajes.setAdapter(messagesAdapter);
                etMensaje.setText("");

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnConfig:
                Intent intent = new Intent(this, Configuracion.class);
                intent.putExtra("direccion", direccion);
                intent.putExtra("nombreUsuario", userName);
                startActivity(intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private Emitter.Listener setSocketId = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mySocketId = args[0].toString();
                    Toast.makeText(Contactos.this, "El id es" + mySocketId, Toast.LENGTH_SHORT).show();
                    Log.d("socket", mySocketId);

                    Gson gson = new Gson();
                    Usuario newUserConnected = new Usuario();
                    newUserConnected.nombreUsuario = userName;
                    newUserConnected.sessionId = mySocketId;

                    mSocket.emit("newUserConnected", gson.toJson(newUserConnected));
                }
            });
        }
    };

    private Emitter.Listener getUsersConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    Usuario[] usersConnected;
                    usersConnected = gson.fromJson(args[0].toString(), Usuario[].class);
                    usuarios.clear();

                    usuarios.add(new Usuario("Todos", "x"));

                    for (int i = 0; i < usersConnected.length; i++) {
                        Log.d("sessionId", usersConnected[i].sessionId);
                        if(!usersConnected[i].sessionId.equals(mySocketId))
                            usuarios.add(usersConnected[i]);
                    }

                    ContactsListAdapter adapter = new ContactsListAdapter(Contactos.this, R.layout.list_adapter, usuarios);
                    spnUsuarios.setAdapter(adapter);
                }
            });
        }
    };

    private Emitter.Listener handleNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Mensaje msg;
                    Gson gson = new Gson();

                    msg = gson.fromJson(args[0].toString(), Mensaje.class);

                    mensajes.add(msg);

                    messagesAdapter = new MessagesListAdapter(Contactos.this, R.layout.lista_mensajes, mensajes);

                    lstMensajes.setAdapter(messagesAdapter);
                }
            });
        }
    };
}
