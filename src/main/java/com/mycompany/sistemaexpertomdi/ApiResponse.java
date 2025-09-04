package com.mycompany.sistemaexpertomdi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ApiResponse {
    private String message;
    private List<HistorialCompletoResponse> data;
    
    // Getters y setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public List<HistorialCompletoResponse> getData() { return data; }
    public void setData(List<HistorialCompletoResponse> data) { this.data = data; }
}
