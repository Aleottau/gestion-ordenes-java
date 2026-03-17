package com.tuempresa.gestion.gestion_ordenes_java.service;

import com.tuempresa.gestion.gestion_ordenes_java.model.Order;
import com.tuempresa.gestion.gestion_ordenes_java.model.OrderItem;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class OrderTicketPdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

    public byte[] generate(Order order) {
        order.getItems().size(); // asegura carga
        List<String> lines = buildLines(order);
        int height = Math.max(200, 80 + lines.size() * 14);
        String contentStream = buildContentStream(lines, height);
        String pdf = buildPdf(contentStream, height);
        return pdf.getBytes(StandardCharsets.US_ASCII);
    }

    private List<String> buildLines(Order order) {
        List<String> lines = new ArrayList<>();
        lines.add("GESTION DE ORDENES");
        lines.add("-------------------------");
        lines.add("ORDEN #" + String.format("%04d", order.getId()));
        lines.add("FECHA: " + DATE_FORMATTER.format(order.getOrderDate()));
        lines.add("CLIENTE: " + order.getCustomerName());
        lines.add("");
        for (OrderItem item : order.getItems()) {
            lines.add(item.getProductName().toUpperCase());
            lines.add(item.getQuantity() + " x " + formatCurrency(item.getUnitPrice().doubleValue())
                + "  " + formatCurrency(item.getLineTotal().doubleValue()));
            lines.add("");
        }
        lines.add("-------------------------");
        lines.add("TOTAL ITEMS: " + order.getTotalItems());
        lines.add("TOTAL: " + formatCurrency(order.getTotalAmount().doubleValue()));
        lines.add("");
        lines.add("Gracias por su compra");
        return lines;
    }

    private String formatCurrency(double value) {
        return CURRENCY_FORMAT.format(value);
    }

    private String buildContentStream(List<String> lines, int height) {
        StringBuilder stream = new StringBuilder();
        stream.append("BT\n/F1 10 Tf\n");
        double y = height - 25;

        for (String line : lines) {
            stream.append(String.format("1 0 0 1 10 %.2f Tm (%s) Tj\n", y, escape(line)));
            y -= 12;
        }

        stream.append("ET");
        return stream.toString();
    }

    private String buildPdf(String contentStream, int height) {
        List<String> objects = new ArrayList<>();
        objects.add("<< /Type /Catalog /Pages 2 0 R >>");
        objects.add("<< /Type /Pages /Kids [3 0 R] /Count 1 /MediaBox [0 0 200 " + height + "] >>");
        objects.add("<< /Type /Page /Parent 2 0 R /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R /MediaBox [0 0 200 " + height + "] >>");
        objects.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");
        objects.add("<< /Length " + contentStream.length() + " >>\nstream\n" + contentStream + "\nendstream");

        StringBuilder pdf = new StringBuilder("%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();

        for (int i = 0; i < objects.size(); i++) {
            offsets.add(pdf.length());
            pdf.append(i + 1).append(" 0 obj\n").append(objects.get(i)).append("\nendobj\n");
        }

        int startXref = pdf.length();
        int count = objects.size() + 1;
        pdf.append("xref\n0 ").append(count).append("\n0000000000 65535 f \n");
        for (Integer offset : offsets) {
            pdf.append(String.format("%010d 00000 n \n", offset));
        }
        pdf.append("trailer << /Size ").append(count).append(" /Root 1 0 R >>\nstartxref\n")
            .append(startXref).append("\n%%EOF");

        return pdf.toString();
    }

    private String escape(String text) {
        return text
            .replace("\\", "\\\\")
            .replace("(", "\\(")
            .replace(")", "\\)");
    }
}
