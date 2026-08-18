package com.module3.ccafe.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class QrCodeService {

    @Value("${app.qr-code.base-url:http://localhost:8080}")
    private String baseUrl;

    public String generateQrCode(Integer tableId) {
        try {
            String qrContent = baseUrl + "/menu?table=" + tableId;
            
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    qrContent,
                    BarcodeFormat.QR_CODE,
                    300,
                    300
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            return "data:image/png;base64," + base64Image;

        } catch (Exception e) {
            log.error("Error generating QR code for table: " + tableId, e);
            throw new RuntimeException("Không thể tạo mã QR");
        }
    }
}
