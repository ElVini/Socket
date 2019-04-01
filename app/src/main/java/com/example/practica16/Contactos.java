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

        mSocket.on("usersUpdated", getUsersConnected);

        mSocket.on("newMsg", handleNewMessage);

        mSocket.on("disconnectedClient", handleDisconnection);
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

    private Emitter.Listener handleDisconnection = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    JSONArray users = null;
                    usuarios.clear();
                    usuarios.add(new Usuario("Todos", "x"));
                    try {
                        users = new JSONArray(args[0].toString());
                        for (int i = 0; i < users.length(); i++) {
                            Usuario usuario = gson.fromJson(users.getJSONObject(i).toString(), Usuario.class);
                            if(usuario.sessionId != mySocketId)
                                usuarios.add(usuario);
                        }
                        listAdapter = new ContactsListAdapter(Contactos.this, R.layout.list_adapter, usuarios);
                        spnUsuarios.setAdapter(listAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

                    Log.d("JSON", args[0].toString());

                    msg = gson.fromJson(args[0].toString(), Mensaje.class);

                    mensajes.add(msg);

                    messagesAdapter = new MessagesListAdapter(Contactos.this, R.layout.lista_mensajes, mensajes);

                    lstMensajes.setAdapter(messagesAdapter);

                    etMensaje.setText("");
                }
            });
        }
    };

    private Emitter.Listener setSocketId = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mySocketId = args[0].toString();
                    Log.d("Socket id", mySocketId);
                    Gson gson = new Gson();
                    Usuario user = new Usuario(userName, mySocketId);

                    mSocket.emit("newUser", gson.toJson(user));
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
                    Log.d("Respuesta", args[0].toString());
                    Gson gson = new Gson();
                    JSONArray users = null;
                    usuarios.clear();
                    usuarios.add(new Usuario("Todos", "x"));
                    try {
                        users = new JSONArray(args[0].toString());
                        Log.d("JSON_string", users.toString());
                        for (int i = 0; i < users.length(); i++) {
                            Log.d("JSON_user", users.getJSONObject(i).toString() + "Indice: " + i);
                            Usuario usuario = gson.fromJson(users.getJSONObject(i).toString(), Usuario.class);
                            if(usuario.sessionId != mySocketId)
                                usuarios.add(usuario);
                        }
                        listAdapter = new ContactsListAdapter(Contactos.this, R.layout.list_adapter, usuarios);
                        spnUsuarios.setAdapter(listAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    public void enviarMensaje(View view) {
        switch (view.getId()) {
            case R.id.btnEnviar:
                Mensaje msg = new Mensaje();
                msg.mensaje = etMensaje.getText().toString();

                if(msg.mensaje.length() == 0) {
                    Toast.makeText(this, "Escriba un mensaje", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(userSelected.equals("Todos")) {
                    msg.nombre = "Todos";
                    msg.clienteId = "null";

                    mensajes.add(msg);

                    messagesAdapter = new MessagesListAdapter(this, R.layout.lista_mensajes, mensajes);

                    lstMensajes.setAdapter(messagesAdapter);

                    Gson gson = new Gson();
                    mSocket.emit("todos", gson.toJson(msg));
                }
                else {
                    msg.nombre = userSelected;
                    msg.clienteId = selectedSocket;

                    mensajes.add(msg);

                    messagesAdapter = new MessagesListAdapter(this, R.layout.lista_mensajes, mensajes);

                    lstMensajes.setAdapter(messagesAdapter);

                    Gson gson = new Gson();
                    mSocket.emit("cliente", gson.toJson(msg));
                }

                etMensaje.setText("");
                break;
        }
    }

}
