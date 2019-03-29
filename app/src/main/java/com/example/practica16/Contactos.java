package com.example.practica16;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Contactos extends AppCompatActivity {

    private Bundle bundle;
    private String userName, mySocketId;
    private Socket mSocket;
    private Spinner spnUsuarios;
    private ContactsListAdapter listAdapter;
    ArrayList<Usuario> usuarios;

    {
        try {
            mSocket = IO.socket("http://192.168.0.3:90");
        }catch(URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        spnUsuarios = (Spinner) findViewById(R.id.spnUsuarios);

        usuarios = new ArrayList<>();
        bundle = getIntent().getExtras();
        userName = bundle.getString("nombreUsuario");

        mSocket.connect();

        mSocket.on("myId", setSocketId);

        mSocket.on("usersUpdated", getUsersConnected);
    }

    private Emitter.Listener setSocketId = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mySocketId = args[0].toString();
                    Log.d("Socket id", mySocketId);

                    JSONObject nombre = new JSONObject();
                    try {
                        nombre.put("nombreUsuario", userName);
                        nombre.put("sessionId", mySocketId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mSocket.emit("newUser", nombre.toString());
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
                    try {
                        users = new JSONArray(args[0].toString());
                        for (int i = 0; i < users.length(); i++) {
                            Usuario usuario = gson.fromJson(users.getJSONObject(i).toString(), Usuario.class);
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

}
