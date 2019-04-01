package com.example.practica16;

public class Usuario {
    public String nombreUsuario;
    public String sessionId;

    public Usuario() {
        this.nombreUsuario = "";
        this.sessionId = "";
    }

    public Usuario(String nombreUsuario, String sessionId) {
        this.nombreUsuario = nombreUsuario;
        this.sessionId = sessionId;
    }
}
