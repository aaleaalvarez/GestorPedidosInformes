package com.example.gestorpedidoshibernate;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Esta clase proporciona servicios para generar e imprimir informes utilizando JasperReports.
 * Se conecta a la base de datos mediante la clase MySQLConnection y genera un informe
 *
 * @author Alejandro Alvarez Merida
 * @version 28-01-2024
 */
public class ReportService {
    /**
     * Genera un informe para el código de pedido dado, lo muestra en una ventana y lo exporta a un archivo PDF.
     *
     * @param codigoPedido El código de pedido para el cual se genera el informe.
     * @throws SQLException Si hay un error al acceder a la base de datos.
     * @throws JRException Si hay un error al generar o manipular el informe Jasper.
     */
    public void generateReport(String codigoPedido) throws SQLException, JRException {
        System.out.println("Generando informe para el código de pedido: " + codigoPedido);

        Connection c = MySQLConnection.getConnection();

        if (c == null) {
            System.out.println("Error: No se pudo establecer la conexión a la base de datos.");
            return;
        } else {
            System.out.println("Conexión establecida correctamente.");
        }

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("codigo", codigoPedido);

        var jasperPrint = JasperFillManager.fillReport("gestor.jasper", parameters, c);

        System.out.println("Informe generado correctamente.");

        var viewer = new JRViewer(jasperPrint);

        JFrame frame = new JFrame("Pedido");
        frame.getContentPane().add(viewer);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);

        System.out.println("Visualización del informe completada.");

        JRPdfExporter exp = new JRPdfExporter();
        exp.setExporterInput(new SimpleExporterInput(jasperPrint));
        exp.setExporterOutput(new SimpleOutputStreamExporterOutput("pedido.pdf"));
        exp.setConfiguration(new SimplePdfExporterConfiguration());
        exp.exportReport();

        System.out.println("Exportación del informe a PDF completada.");
    }
}
