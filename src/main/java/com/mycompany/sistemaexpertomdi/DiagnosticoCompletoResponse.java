package com.mycompany.sistemaexpertomdi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DiagnosticoCompletoResponse {
    private String message;
    private Long historialId;
    private Long diagnosticoId;
    private DiagnosticoResponse diagnostico; // ¡Usa la clase existente!
    
    // Getters y setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Long getHistorialId() { return historialId; }
    public void setHistorialId(Long historialId) { this.historialId = historialId; }
    
    public Long getDiagnosticoId() { return diagnosticoId; }
    public void setDiagnosticoId(Long diagnosticoId) { this.diagnosticoId = diagnosticoId; }
    
    public DiagnosticoResponse getDiagnostico() { return diagnostico; }
    public void setDiagnostico(DiagnosticoResponse diagnostico) { this.diagnostico = diagnostico; }
}