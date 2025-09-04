package com.mycompany.sistemaexpertomdi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiagnosticoResponse {
    private String nombre;
    private String descripcion;
    
    @JsonProperty("nivel_gravedad")
    private String nivelGravedad;
    
    private String recomendaciones;
    
    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getNivelGravedad() { return nivelGravedad; }
    public void setNivelGravedad(String nivelGravedad) { this.nivelGravedad = nivelGravedad; }
    
    public String getRecomendaciones() { return recomendaciones; }
    public void setRecomendaciones(String recomendaciones) { this.recomendaciones = recomendaciones; }
}
