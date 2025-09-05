package com.mycompany.sistemaexpertomdi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.util.Date;
import javax.swing.table.TableCellRenderer;

public class ConsultaUnificadaFrame extends InternalFrame {
    
    // Componentes para la sección de CONSULTA (arriba)
    private JTextField txtIdentificacion;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JSpinner spnEdad;
    private JComboBox<String> cmbSexo;
    private JCheckBox[] checksSintomas;
    private String[] preguntas = {
        "¿Siente dolor en el pecho, presión o ardor?",
        "¿Experimenta falta de aire o dificultad para respirar?",
        "¿Tiene hinchazón en los pies, tobillos o piernas?",
        "¿Ha notado palpitaciones, latidos irregulares o muy rápidos?",
        "¿Ha sentido mareos o desmayos con frecuencia?"
    };
    
    // Componentes para la sección de HISTORIAL (abajo)
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    private JTextField txtBusqueda;
    
    public ConsultaUnificadaFrame(String title, int width, int height) {
        super(title, width, height);
        initComponents();
        
        // Configuración adicional para mejor visualización
        this.setResizable(true);
        this.setMaximizable(true);
        this.setIconifiable(true);
        this.setClosable(true);
    }
    
    private void initComponents() {
        // Panel principal con división vertical
        JPanel panelMain = new JPanel(new BorderLayout(15, 15));
        panelMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // 1. PANEL SUPERIOR (FORMULARIO DE CONSULTA)
        JPanel panelConsulta = crearPanelConsulta();
        
        // 2. PANEL INFERIOR (HISTORIAL DE RESULTADOS)
        JPanel panelHistorial = crearPanelHistorial();
        
        // Divisor para redimensionar las secciones
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelConsulta, panelHistorial);
        splitPane.setResizeWeight(0.5); // Mitad y mitad
        splitPane.setDividerLocation(400); // Altura inicial del panel superior
        
        panelMain.add(splitPane, BorderLayout.CENTER);
        this.add(panelMain);
    }
    
    private JPanel crearPanelConsulta() {
        JPanel panelConsulta = new JPanel(new BorderLayout(10, 10));
        panelConsulta.setBorder(BorderFactory.createTitledBorder("Nuevo Diagnóstico"));
        
        // Panel datos paciente
        JPanel panelDatos = new JPanel(new GridLayout(5, 2, 10, 10));
        panelDatos.setBorder(BorderFactory.createTitledBorder("Datos del Paciente"));
       
        panelDatos.add(new JLabel("Identificación:"));
        txtIdentificacion = new JTextField();
        panelDatos.add(txtIdentificacion);
        
        panelDatos.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelDatos.add(txtNombre);
        
        panelDatos.add(new JLabel("Apellido:"));
        txtApellido = new JTextField();
        panelDatos.add(txtApellido);
        
        panelDatos.add(new JLabel("Edad:"));
        spnEdad = new JSpinner(new SpinnerNumberModel(30, 1, 120, 1));
        panelDatos.add(spnEdad);
        
        panelDatos.add(new JLabel("Sexo:"));
        cmbSexo = new JComboBox<>(new String[]{"M", "F", "Otro"});
        panelDatos.add(cmbSexo);
        
        // Panel síntomas
        JPanel panelSintomas = new JPanel(new GridLayout(5, 1, 5, 5));
        panelSintomas.setBorder(BorderFactory.createTitledBorder("Síntomas Cardiovasculares"));
        checksSintomas = new JCheckBox[preguntas.length];
        
        for (int i = 0; i < preguntas.length; i++) {
            checksSintomas[i] = new JCheckBox((i + 1) + ". " + preguntas[i]);
            panelSintomas.add(checksSintomas[i]);
        }
        
        // Panel botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEnviar = new JButton("consultar Diagnostico");
        btnEnviar.addActionListener(this::enviarDiagnostico);
        
        JButton btnLimpiar = new JButton("Limpiar Formulario");
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnEnviar);
        
        // Ensamblar panel de consulta
        panelConsulta.add(panelDatos, BorderLayout.NORTH);
        panelConsulta.add(new JScrollPane(panelSintomas), BorderLayout.CENTER);
        panelConsulta.add(panelBotones, BorderLayout.SOUTH);
        
        return panelConsulta;
    }
    
    private JPanel crearPanelHistorial() {
        JPanel panelHistorial = new JPanel(new BorderLayout(10, 10));
        panelHistorial.setBorder(BorderFactory.createTitledBorder("Historial de Diagnósticos"));
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Buscar por ID o Nombre:"));
        txtBusqueda = new JTextField(20);
        panelBusqueda.add(txtBusqueda);
        
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(this::buscarRegistros);
        panelBusqueda.add(btnBuscar);
        
        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        btnMostrarTodos.addActionListener(e -> cargarTodosRegistros());
        panelBusqueda.add(btnMostrarTodos);
        
        JButton btnLimpiarBusqueda = new JButton("Limpiar");
        btnLimpiarBusqueda.addActionListener(e -> {
            txtBusqueda.setText("");
            cargarTodosRegistros();
        });
        panelBusqueda.add(btnLimpiarBusqueda);
        
        // Tabla de resultados
        String[] columnas = {"ID", "Nombre", "Apellido", "Edad", "Sexo", "Fecha", "Diagnóstico"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaResultados = new JTable(modeloTabla);
        tablaResultados.setAutoCreateRowSorter(true);
        
        // Ajustar anchos de columnas
        /*tablaResultados.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaResultados.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaResultados.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaResultados.getColumnModel().getColumn(3).setPreferredWidth(50);
        tablaResultados.getColumnModel().getColumn(4).setPreferredWidth(50);
        tablaResultados.getColumnModel().getColumn(5).setPreferredWidth(100);
        tablaResultados.getColumnModel().getColumn(6).setPreferredWidth(200);*/
        
        tablaResultados.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        tablaResultados.getColumnModel().getColumn(1).setPreferredWidth(100); // Nombre
        tablaResultados.getColumnModel().getColumn(2).setPreferredWidth(100); // Apellido
        tablaResultados.getColumnModel().getColumn(3).setPreferredWidth(50);  // Edad
        tablaResultados.getColumnModel().getColumn(4).setPreferredWidth(50);  // Sexo
        tablaResultados.getColumnModel().getColumn(5).setPreferredWidth(120); // Fecha
        tablaResultados.getColumnModel().getColumn(6).setPreferredWidth(400); // Diagnóstico (MÁS ANCHA)
        
        // 🔥 AGREGAR RENDERER MULTILÍNEA PARA LA COLUMNA DE DIAGNÓSTICO
        tablaResultados.getColumnModel().getColumn(6).setCellRenderer(new MultiLineCellRenderer());
        
        // Ajustar altura de las filas para que se vea el texto multilínea
        tablaResultados.setRowHeight(80); // Altura suficiente para 3-4 líneas
        
        JScrollPane scrollPane = new JScrollPane(tablaResultados);
        
        // Ensamblar panel de historial
        panelHistorial.add(panelBusqueda, BorderLayout.NORTH);
        panelHistorial.add(scrollPane, BorderLayout.CENTER);
        
        return panelHistorial;
    }
    
    class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
        public MultiLineCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            setFont(new Font("SansSerif", Font.PLAIN, 11));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }

            setText((value == null) ? "" : value.toString());
            adjustRowHeight(table, row);

            return this;
        }

        private void adjustRowHeight(JTable table, int row) {
            int preferredHeight = getPreferredSize().height + 12;
            preferredHeight = Math.min(preferredHeight, 400); // Máximo 400px
            preferredHeight = Math.max(preferredHeight, 100); // Mínimo 100px

            if (table.getRowHeight(row) != preferredHeight) {
                table.setRowHeight(row, preferredHeight);
            }
        }
    }
    
    
    // ==================== MÉTODOS PARA CONSULTA ====================
    
    private void enviarDiagnostico(ActionEvent e) {
        if (validarCampos()) {
            try {
                // 1. Crear JSON directamente con los datos del formulario
                String jsonPayload = crearJsonParaEnvio();

                // 2. Enviar a la API
                String respuestaApi = enviarDatosAApi(jsonPayload);

                // 3. Parsear la respuesta de la API
                DiagnosticoCompletoResponse apiResponse = parsearRespuestaApi(respuestaApi);

                if (apiResponse != null && apiResponse.getMessage() != null) {
                    // 4. LIMPIAR LA TABLA y mostrar SOLO este diagnóstico
                    modeloTabla.setRowCount(0); // ← Limpiar tabla antes de agregar
                    mostrarResultadoEnTabla(apiResponse);

                    // 5. Limpiar formulario para nuevo ingreso
                    limpiarFormulario();

                    // 6. Mostrar mensaje de éxito
                    JOptionPane.showMessageDialog(this, 
                        "Diagnóstico procesado exitosamente\n" +
                        "Ahora mostrando solo este diagnóstico en la tabla",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al enviar datos: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    // Método para mostrar el resultado directamente en la tabla
    private void mostrarResultadoEnTabla(DiagnosticoCompletoResponse apiResponse) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String fechaStr = sdf.format(new Date());

            // Crear mensaje con el MISMO formato que agregarFilaATabla
            StringBuilder diagnosticoBuilder = new StringBuilder();

            // 1. INFORMACIÓN DEL DIAGNÓSTICO
            diagnosticoBuilder.append("✅ ").append(apiResponse.getMessage()).append("\n\n");

            if (apiResponse.getDiagnostico() != null) {
                DiagnosticoResponse diag = apiResponse.getDiagnostico();

                diagnosticoBuilder.append("🎯 DIAGNÓSTICO\n")
                                .append("● Nombre: ").append(diag.getNombre()).append("\n")
                                .append("● Gravedad: ").append(diag.getNivelGravedad()).append("\n\n");

                if (diag.getDescripcion() != null && !diag.getDescripcion().isEmpty()) {
                    diagnosticoBuilder.append("📋 DESCRIPCIÓN\n")
                                    .append(formatearTexto(diag.getDescripcion(), 60)).append("\n\n");
                }

                if (diag.getRecomendaciones() != null && !diag.getRecomendaciones().isEmpty()) {
                    diagnosticoBuilder.append("💡 RECOMENDACIONES\n")
                                    .append(formatearTexto(diag.getRecomendaciones(), 60)).append("\n\n");
                }
            }

            // 2. PREGUNTAS Y RESPUESTAS
            if (apiResponse.getPreguntasRespuestas() != null && 
                !apiResponse.getPreguntasRespuestas().isEmpty()) {

                diagnosticoBuilder.append("❓ PREGUNTAS Y RESPUESTAS\n");
                for (PreguntaRespuestaResponse pr : apiResponse.getPreguntasRespuestas()) {
                    String respuesta = pr.getRespuesta() == 1 ? "✅ Sí" : "❌ No";
                    diagnosticoBuilder.append("● ").append(pr.getPregunta())
                                    .append(": ").append(respuesta).append("\n");
                }
                diagnosticoBuilder.append("\n");
            }

            // 3. METADATOS
            diagnosticoBuilder.append("📊 METADATOS\n")
                            .append("● ID Historial: ").append(apiResponse.getHistorialId()).append("\n")
                            .append("● ID Diagnóstico: ").append(apiResponse.getDiagnosticoId()).append("\n")
                            .append("● Fecha: ").append(fechaStr);

            // Agregar a la tabla
            modeloTabla.addRow(new Object[]{
                txtIdentificacion.getText(),
                txtNombre.getText().trim(),
                txtApellido.getText().trim(),
                spnEdad.getValue(),
                cmbSexo.getSelectedItem(),
                fechaStr,
                diagnosticoBuilder.toString()
            });

            // Ajustar altura de la fila
            tablaResultados.setRowHeight(modeloTabla.getRowCount() - 1, 350);

        } catch (Exception ex) {
            System.out.println("Error al mostrar resultado en tabla: " + ex.getMessage());
        }
    }
    
    // Método para crear el JSON de envío
    private String crearJsonParaEnvio() {
        try {
            // Crear objeto para Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonNode = objectMapper.createObjectNode();

            // Datos básicos del paciente
            jsonNode.put("nombre", txtNombre.getText());
            jsonNode.put("apellido", txtApellido.getText());
            jsonNode.put("identificacion", txtIdentificacion.getText());
            jsonNode.put("edad", (Integer) spnEdad.getValue());
            jsonNode.put("sexo", (String) cmbSexo.getSelectedItem());

            // Array de respuestas
            ArrayNode respuestasArray = objectMapper.createArrayNode();
            boolean[] respuestas = obtenerRespuestas();

            for (int i = 0; i < respuestas.length; i++) {
                ObjectNode respuestaNode = objectMapper.createObjectNode();
                respuestaNode.put("id_pregunta", i + 1);
                respuestaNode.put("respuesta_valor", respuestas[i]);
                respuestasArray.add(respuestaNode);
            }

            jsonNode.set("respuestas", respuestasArray);

            return objectMapper.writeValueAsString(jsonNode);

        } catch (Exception ex) {
            throw new RuntimeException("Error al crear JSON: " + ex.getMessage());
        }
    }
    
    private boolean[] obtenerRespuestas() {
        boolean[] respuestas = new boolean[checksSintomas.length];
        for (int i = 0; i < checksSintomas.length; i++) {
            respuestas[i] = checksSintomas[i].isSelected();
        }
        return respuestas;
    }
    
    private boolean validarCampos() {
        if (txtIdentificacion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese la identificación del paciente", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            txtIdentificacion.requestFocus();
            return false;
        }
        
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese el nombre del paciente", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }
        
        if (txtApellido.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese el apellido del paciente", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            txtApellido.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void limpiarFormulario() {
        txtIdentificacion.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        spnEdad.setValue(30);
        cmbSexo.setSelectedIndex(0);
        for (JCheckBox check : checksSintomas) {
            check.setSelected(false);
        }
    }
    
    // ==================== MÉTODOS PARA HISTORIAL ====================
    
    private void buscarRegistros(ActionEvent e) {
    String criterio = txtBusqueda.getText().trim();
    
        if (criterio.isEmpty()) {
            cargarTodosRegistros();
        } else {
            try {
                // Buscar por ID específico
                buscarPorId(criterio);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error en la búsqueda: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cargarTodosRegistros() {
        try {
            // 1. Hacer HTTP GET al endpoint de todos los historiales
            String endpoint = "http://localhost:3000/historial/get";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();

            // 2. Enviar la solicitud y obtener respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 3. IMPRIMIR LA RESPUESTA EN CONSOLA PARA DEBUGGING
            System.out.println("=== RESPUESTA CRUDA DE LA API ===");
            System.out.println("Código de estado: " + response.statusCode());
            System.out.println("Respuesta JSON: " + response.body());
            System.out.println("================================");

            // 4. Verificar el código de respuesta
            if (response.statusCode() == 200) {
                // 5. Parsear la respuesta JSON y llenar la tabla
                llenarTablaConRespuesta(response.body());
            } else {
                // Manejar errores de la API
                manejarErrorAPI(response.statusCode(), response.body());
            }

        } catch (IOException ex) {
            if (ex.getMessage().contains("ECONNRESET") || ex.getMessage().contains("Connection reset")) {
                JOptionPane.showMessageDialog(this,
                    "Error de conexión con la base de datos.\n" +
                    "La API reportó: ECONNRESET\n\n" +
                    "Esto usually significa que la base de datos está sobrecargada o\n" +
                    "hay problemas de conexión. Intenta nuevamente en unos momentos.",
                    "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error de conexión: " + ex.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            JOptionPane.showMessageDialog(this,
                "Operación interrumpida: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar datos: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarPorId(String id) {
        try {
            // 1. Hacer HTTP GET al endpoint específico por ID
            String endpoint = "http://localhost:3000/historial/get/" + id;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .GET()
                .timeout(Duration.ofSeconds(30)) // ← Agregar timeout
                .build();

            // 2. Enviar la solicitud y obtener respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 3. Verificar el código de respuesta
            if (response.statusCode() == 200) {
                // 4. Parsear la respuesta JSON y llenar la tabla
                llenarTablaConRespuesta(response.body());
            } else if (response.statusCode() == 404) {
                JOptionPane.showMessageDialog(this,
                    "No se encontró ningún diagnóstico con ID: " + id,
                    "Búsqueda sin resultados",
                    JOptionPane.INFORMATION_MESSAGE);
                modeloTabla.setRowCount(0); // Limpiar tabla
            } else {
                // Manejar otros errores de la API
                manejarErrorAPI(response.statusCode(), response.body());
            }

        } catch (IOException ex) {
            if (ex.getMessage().contains("ECONNRESET") || ex.getMessage().contains("Connection reset")) {
                JOptionPane.showMessageDialog(this,
                    "Error de conexión con la base de datos.\n" +
                    "La API reportó: ECONNRESET\n\n" +
                    "Esto usually significa que la base de datos está sobrecargada o\n" +
                    "hay problemas de conexión. Intenta nuevamente en unos momentos.",
                    "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error de conexión: " + ex.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            JOptionPane.showMessageDialog(this,
                "Operación interrumpida: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error en la búsqueda: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void llenarTablaConRespuesta(String jsonResponse) {
        try {
            modeloTabla.setRowCount(0); // Limpiar tabla primero

            ObjectMapper objectMapper = new ObjectMapper();

            // Parsear la respuesta completa
            ApiResponse apiResponse = objectMapper.readValue(jsonResponse, ApiResponse.class);

            System.out.println("Mensaje de la API: " + apiResponse.getMessage());

            if (apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
                for (HistorialCompletoResponse historial : apiResponse.getData()) {
                    agregarFilaATabla(historial);
                }

                JOptionPane.showMessageDialog(this,
                    "Datos cargados exitosamente: " + apiResponse.getData().size() + " registros",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No hay datos en el historial",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            System.out.println("Error al parsear JSON: " + ex.getMessage());
            ex.printStackTrace();

            JOptionPane.showMessageDialog(this,
                "Error al procesar la respuesta de la API: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void agregarFilaATabla(HistorialCompletoResponse historial) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String fechaStr = historial.getFecha() != null ? 
                             sdf.format(historial.getFecha()) : "N/A";

            // Obtener datos del usuario
            String identificacion = historial.getUsuario() != null ? 
                                   historial.getUsuario().getIdentificacion() : "N/A";
            String nombre = historial.getUsuario() != null ? 
                           historial.getUsuario().getNombre() : "N/A";
            String apellido = historial.getUsuario() != null ? 
                             historial.getUsuario().getApellido() : "N/A";
            int edad = historial.getUsuario() != null ? 
                      historial.getUsuario().getEdad() : 0;
            String sexo = historial.getUsuario() != null ? 
                         historial.getUsuario().getSexo() : "N/A";

            // Crear diagnóstico con formato claro y separado
            StringBuilder diagnosticoBuilder = new StringBuilder();

            // 1. INFORMACIÓN DEL DIAGNÓSTICO (si está disponible)
            if (historial.getDiagnostico() != null) {
                DiagnosticoResponse diag = historial.getDiagnostico();

                diagnosticoBuilder.append("🎯 DIAGNÓSTICO\n")
                                .append("● Nombre: ").append(diag.getNombre()).append("\n")
                                .append("● Gravedad: ").append(diag.getNivelGravedad()).append("\n\n");

                // Descripción con formato
                if (diag.getDescripcion() != null && !diag.getDescripcion().isEmpty()) {
                    diagnosticoBuilder.append("📋 DESCRIPCIÓN\n")
                                    .append(formatearTexto(diag.getDescripcion(), 60)).append("\n\n");
                }

                // Recomendaciones con formato
                if (diag.getRecomendaciones() != null && !diag.getRecomendaciones().isEmpty()) {
                    diagnosticoBuilder.append("💡 RECOMENDACIONES\n")
                                    .append(formatearTexto(diag.getRecomendaciones(), 60)).append("\n\n");
                }
            }

            // 2. PREGUNTAS Y RESPUESTAS (separadas claramente)
            if (historial.getPreguntasRespuestas() != null && 
                !historial.getPreguntasRespuestas().isEmpty()) {

                diagnosticoBuilder.append("❓ PREGUNTAS Y RESPUESTAS\n");
                for (PreguntaRespuestaResponse pr : historial.getPreguntasRespuestas()) {
                    String respuesta = pr.getRespuesta() == 1 ? "✅ Sí" : "❌ No";
                    diagnosticoBuilder.append("● ").append(pr.getPregunta())
                                    .append(": ").append(respuesta).append("\n");
                }
            }

            // 3. separacion de diagnosticos
            diagnosticoBuilder.append("\n------------------------------------------------------------------\n");
     

            modeloTabla.addRow(new Object[]{
                identificacion,
                nombre != null ? nombre.trim() : "N/A",
                apellido != null ? apellido.trim() : "N/A",
                edad,
                sexo,
                fechaStr,
                diagnosticoBuilder.toString()
            });

            // Ajustar altura de la fila
            int lastRow = modeloTabla.getRowCount() - 1;
            tablaResultados.setRowHeight(lastRow, 350); // Altura suficiente

        } catch (Exception ex) {
            System.out.println("Error al agregar fila: " + ex.getMessage());
        }
    }

// Método auxiliar para formatear texto con saltos de línea
    private String formatearTexto(String texto, int maxLineLength) {
        if (texto == null || texto.isEmpty()) return "N/A";

        StringBuilder formatted = new StringBuilder();
        int currentLength = 0;

        String[] words = texto.split(" ");
        for (String word : words) {
            if (currentLength + word.length() + 1 > maxLineLength) {
                formatted.append("\n");
                currentLength = 0;
            }
            if (currentLength > 0) {
                formatted.append(" ");
                currentLength++;
            }
            formatted.append(word);
            currentLength += word.length();
        }

        return formatted.toString();
    }

    
    // ==================== COMUNICACIÓN CON API ====================

    // Método actualizado para enviar datos
    private String enviarDatosAApi(String jsonPayload) {
        try {
            System.out.println("JSON enviado a API: " + jsonPayload);

            String endpoint = "http://localhost:3000/respuesta/add";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .timeout(Duration.ofSeconds(30))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return response.body();
            } else {
                throw new RuntimeException("Error en la API. Código: " + response.statusCode() + 
                                          "\nMensaje: " + response.body());
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error al conectar con la API: " + ex.getMessage());
        }
    }
    
    // Método parsearRespuestaApi actualizado:
    private DiagnosticoCompletoResponse parsearRespuestaApi(String jsonResponse) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return objectMapper.readValue(jsonResponse, DiagnosticoCompletoResponse.class);
        } catch (Exception e) {
            throw new Exception("Error al parsear respuesta: " + e.getMessage());
        }
    }


    // Método auxiliar para extraer valores simples del JSON
    private String extraerValorJson(String json, String clave) {
        try {
            String searchPattern = "\"" + clave + "\":\"";
            int startIndex = json.indexOf(searchPattern);
            if (startIndex == -1) {
                searchPattern = "\"" + clave + "\":";
                startIndex = json.indexOf(searchPattern);
                if (startIndex == -1) return "";
                startIndex += searchPattern.length();
                int endIndex = json.indexOf(",", startIndex);
                if (endIndex == -1) endIndex = json.indexOf("}", startIndex);
                return json.substring(startIndex, endIndex).replace("\"", "").trim();
            }

            startIndex += searchPattern.length();
            int endIndex = json.indexOf("\"", startIndex);
            return json.substring(startIndex, endIndex);
        } catch (Exception ex) {
            return "";
        }
    }
    
    // Método para manejar errores específicos de la API
    private void manejarErrorAPI(int statusCode, String responseBody) {
        switch (statusCode) {
            case 500:
                JOptionPane.showMessageDialog(this,
                    "Error interno del servidor (500).\n\n" +
                    "El servidor reportó: ECONNRESET\n\n" +
                    "Esto significa que hay problemas de conexión con la base de datos.\n" +
                    "Por favor, informa al administrador del sistema.",
                    "Error del Servidor",
                    JOptionPane.ERROR_MESSAGE);
                break;
            case 503:
                JOptionPane.showMessageDialog(this,
                    "Servicio no disponible (503).\n" +
                    "La base de datos puede estar sobrecargada.\n" +
                    "Intenta nuevamente en unos minutos.",
                    "Servicio No Disponible",
                    JOptionPane.WARNING_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(this,
                    "Error en la API. Código: " + statusCode + "\n" +
                    "Respuesta: " + responseBody,
                    "Error del Servidor",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
