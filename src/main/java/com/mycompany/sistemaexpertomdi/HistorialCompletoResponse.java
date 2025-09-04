package com.mycompany.sistemaexpertomdi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

public class HistorialCompletoResponse {
    private int id;
    
    @JsonProperty("id_usuario")
    private int idUsuario;
    
    @JsonProperty("id_diagnostico")
    private int idDiagnostico;
    
    private Date fecha;
    private UsuarioResponse usuario;
    private DiagnosticoResponse diagnostico;
    
    @JsonProperty("preguntas_respuestas")
    private List<PreguntaRespuestaResponse> preguntasRespuestas;
    
    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public int getIdDiagnostico() { return idDiagnostico; }
    public void setIdDiagnostico(int idDiagnostico) { this.idDiagnostico = idDiagnostico; }
    
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    
    public UsuarioResponse getUsuario() { return usuario; }
    public void setUsuario(UsuarioResponse usuario) { this.usuario = usuario; }
    
    public DiagnosticoResponse getDiagnostico() { return diagnostico; }
    public void setDiagnostico(DiagnosticoResponse diagnostico) { this.diagnostico = diagnostico; }
    
    public List<PreguntaRespuestaResponse> getPreguntasRespuestas() { return preguntasRespuestas; }
    public void setPreguntasRespuestas(List<PreguntaRespuestaResponse> preguntasRespuestas) { 
        this.preguntasRespuestas = preguntasRespuestas; 
    }
}
