package com.itat.mongopersona.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import com.itat.mongopersona.event.Pedido;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    // Lee app.base-url de application.properties
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    // Colores corporativos
    private static final DeviceRgb ROJO      = new DeviceRgb(231, 76,  60);
    private static final DeviceRgb ROJO_DARK = new DeviceRgb(192, 57,  43);
    private static final DeviceRgb GRIS_BG   = new DeviceRgb(245, 245, 245);
    private static final DeviceRgb GRIS_TEXT = new DeviceRgb( 80,  80,  80);

    /**
     * Genera el PDF del comprobante de pedido con QR de validación.
     *
     * @param pedido pedido ya guardado en MongoDB (con id asignado)
     * @return bytes del PDF listo para descargar o adjuntar
     */
    public byte[] generarComprobante(Pedido pedido) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter   writer   = new PdfWriter(baos);
        PdfDocument pdfDoc   = new PdfDocument(writer);
        Document    document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(36, 48, 36, 48);

        PdfFont bold    = PdfFontFactory.createFont("Helvetica-Bold");
        PdfFont regular = PdfFontFactory.createFont("Helvetica");

        // ── ENCABEZADO ────────────────────────────────────────────────────────
        Table header = new Table(UnitValue.createPercentArray(new float[]{1}))
                .useAllAvailableWidth();

        Cell headerCell = new Cell()
                .setBackgroundColor(ROJO)
                .setPadding(18)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER)
                .add(new Paragraph("Bona Pizza")
                        .setFont(bold).setFontSize(26).setFontColor(ColorConstants.WHITE))
                .add(new Paragraph("COMPROBANTE DE PEDIDO")
                        .setFont(regular).setFontSize(12).setFontColor(ColorConstants.WHITE));

        header.addCell(headerCell);
        document.add(header);
        document.add(new Paragraph("\n"));

        // ── DATOS DEL PEDIDO ─────────────────────────────────────────────────
        document.add(new Paragraph("Detalle del pedido")
                .setFont(bold).setFontSize(13).setFontColor(ROJO_DARK)
                .setMarginBottom(6));

        Table tabla = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        agregarFila(tabla, "Nº de pedido",     pedido.getId(),                 bold, regular, true);
        agregarFila(tabla, "Cliente",
                pedido.getNombre() + " " + pedido.getApellido(),               bold, regular, false);
        agregarFila(tabla, "Correo",           pedido.getCorreo(),              bold, regular, true);
        agregarFila(tabla, "Sabor",            pedido.getSabor(),               bold, regular, false);
        agregarFila(tabla, "Cantidad",         pedido.getCantidad() + " pizza(s)", bold, regular, true);
        agregarFila(tabla, "Hora de entrega",  pedido.getHora(),                bold, regular, false);
        agregarFila(tabla, "Dirección",        pedido.getDireccion(),           bold, regular, true);

        document.add(tabla);

        // ── SECCIÓN QR ────────────────────────────────────────────────────────
        String urlValidacion = baseUrl + "/confirmarRecibido/" + pedido.getId();

        document.add(new Paragraph("Código QR de validación")
                .setFont(bold).setFontSize(13).setFontColor(ROJO_DARK)
                .setMarginBottom(4));

        document.add(new Paragraph(
                "El repartidor escaneará este QR al momento de la entrega " +
                "para confirmar que el pedido fue recibido correctamente.")
                .setFont(regular).setFontSize(10).setFontColor(GRIS_TEXT)
                .setMarginBottom(10));

        // Generar QR en memoria (250x250 px)
        byte[] qrBytes = generarQR(urlValidacion, 250);

        Image qrImg = new Image(ImageDataFactory.create(qrBytes))
                .setWidth(160)
                .setHeight(160)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBorder(new SolidBorder(ROJO, 2))
                .setMarginBottom(6);

        document.add(qrImg);

        document.add(new Paragraph("Escanear al recibir el pedido")
                .setFont(bold).setFontSize(9).setFontColor(GRIS_TEXT)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(4));

        document.add(new Paragraph("URL: " + urlValidacion)
                .setFont(regular).setFontSize(7).setFontColor(GRIS_TEXT)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // ── AVISO LEGAL ───────────────────────────────────────────────────────
        Table aviso = new Table(UnitValue.createPercentArray(new float[]{1}))
                .useAllAvailableWidth();

        Cell avisoCell = new Cell()
                .setBackgroundColor(GRIS_BG)
                .setPadding(10)
                .setBorder(new SolidBorder(ROJO, 1))
                .add(new Paragraph(
                        "⚠  Conserva este documento. " +
                        "El QR es único para tu pedido y sólo puede usarse una vez.")
                        .setFont(regular).setFontSize(9).setFontColor(GRIS_TEXT));

        aviso.addCell(avisoCell);
        document.add(aviso);

        // ── PIE DE PÁGINA ─────────────────────────────────────────────────────
        document.add(new Paragraph("\nGracias por tu preferencia — Bona Pizza © 2026")
                .setFont(regular).setFontSize(8).setFontColor(GRIS_TEXT)
                .setTextAlignment(TextAlignment.CENTER));

        document.close();
        return baos.toByteArray();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Agrega una fila de dos columnas a la tabla de datos.
     */
    private void agregarFila(Table tabla, String etiqueta, String valor,
                              PdfFont bold, PdfFont regular, boolean sombreado) {

  DeviceRgb fondo = sombreado ? GRIS_BG : new DeviceRgb(255, 255, 255);

        tabla.addCell(new Cell()
                .setBackgroundColor(fondo)
                .setPadding(7)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .add(new Paragraph(etiqueta)
                        .setFont(bold).setFontSize(10).setFontColor(GRIS_TEXT)));

        tabla.addCell(new Cell()
                .setBackgroundColor(fondo)
                .setPadding(7)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .add(new Paragraph(valor != null ? valor : "—")
                        .setFont(regular).setFontSize(10)));
    }

    /**
     * Genera los bytes PNG de un código QR con ZXing.
     */
    private byte[] generarQR(String contenido, int size) throws Exception {
        QRCodeWriter writer    = new QRCodeWriter();
        BitMatrix    bitMatrix = writer.encode(contenido, BarcodeFormat.QR_CODE, size, size);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
        return baos.toByteArray();
    }
}