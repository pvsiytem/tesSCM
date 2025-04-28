package com.test.controller;

import com.test.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api")
public class PurchaseOrderController {

    private static final String DATASET_PATH = "E:\\SCM\\INBOX";
    private static final String DATASET_FILE = "DATASET.xlsx";
    private static final String OUTBOX_PATH = "E:\\SCM\\OUTBOX";

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("API is up");
    }

    @GetMapping(value = "/import/excel", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> importExcel() {
        File f = new File(DATASET_PATH, DATASET_FILE);
        if (!f.exists()) {
            return ResponseEntity.status(404).body("Excel not found: " + f.getAbsolutePath());
        }
        try {
            purchaseOrderService.importDataFromExcel(f);
            return ResponseEntity.ok("Imported from: " + f.getAbsolutePath());
        } catch (Exception ex) {
            return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN)
                    .body("Import error: " + ex.getMessage());
        }
    }

    @GetMapping(value = "/export/xml", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> exportXml() {
        try {
            purchaseOrderService.exportDataToXML(OUTBOX_PATH);
            return ResponseEntity.ok("XML written to: " + OUTBOX_PATH + "\\output.xml");
        } catch (Exception ex) {
            return ResponseEntity.status(500).contentType(MediaType.TEXT_PLAIN)
                    .body("Export error: " + ex.getMessage());
        }
    }
}
