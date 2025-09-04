package com.mycompany.sistemaexpertomdi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsuarioResponse {
    private int edad;
    private String sexo;
    private String nombre;
    private String apellido;
    private String identificacion;
    
    // Getters y setters
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }
}