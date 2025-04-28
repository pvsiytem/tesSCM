package com.test.service;

import com.test.model.PurchaseOrderDetail;
import com.test.model.PurchaseOrderHeader;
import com.test.repository.DetailRepository;
import com.test.repository.HeaderRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@Service
@Transactional
public class PurchaseOrderService {

    @Autowired private HeaderRepository headerRepo;
    @Autowired private DetailRepository detailRepo;
    @Autowired private XMLExporter xmlExporter;

    public void importDataFromExcel(File file) throws Exception {
        try (Workbook wb = new XSSFWorkbook(new FileInputStream(file))) {
            importHeaders(wb.getSheet("HEADER"));
            importDetails(wb.getSheet("DETAIL"));
        }
    }

    private void importHeaders(Sheet sheet) {
        if (sheet == null) return;
        var iter = sheet.iterator();
        if (iter.hasNext()) iter.next(); // skip header row
        while (iter.hasNext()) {
            var row = iter.next();
            var h = new PurchaseOrderHeader();
            h.setPoNumber(getCellString(row.getCell(0)));
            var date = row.getCell(1).getDateCellValue();
            h.setPoDate(new java.sql.Date(date.getTime()));
            h.setBuyerName(getCellString(row.getCell(2)));
            h.setBuyerAddress(getCellString(row.getCell(3)));
            headerRepo.save(h);
        }
    }

    private void importDetails(Sheet sheet) {
        if (sheet == null) return;
        var iter = sheet.iterator();
        if (iter.hasNext()) iter.next(); // skip header row
        while (iter.hasNext()) {
            var row = iter.next();
            
            // Get the PO number from the first column
            String poNum = getCellString(row.getCell(0)); // PO_NUMBER is in the first column
    
            // Use findByPoNumberWithDetails to get the header with its details
            var header = headerRepo.findByPoNumberWithDetails(poNum); // Find header along with details
    
            if (header == null) continue; // Skip if header not found
    
            var d = new PurchaseOrderDetail();
            d.setPartNo(getCellString(row.getCell(1))); // PART_NO is in the second column
            d.setPartName(getCellString(row.getCell(2))); // PART_NAME is in the third column
            d.setQty((int) row.getCell(3).getNumericCellValue()); // QTY is in the fourth column
            d.setUnit(getCellString(row.getCell(4))); // UNIT is in the fifth column
            d.setPrice(row.getCell(5).getNumericCellValue()); // PRICE is in the sixth column
    
            // Link detail to header
            d.setPurchaseOrderHeader(header); // Linking detail to header
            detailRepo.save(d); // Save the detail
    
            // Optionally, you can also add this detail to the header's details collection if you want it to be linked in memory
            header.addDetail(d);
            headerRepo.save(header); // Save the updated header with the new detail
        }
    }
    

    private String getCellString(Cell c) {
        if (c == null) return "";
        return c.getCellType() == CellType.STRING
                ? c.getStringCellValue()
                : String.valueOf(c.getNumericCellValue());
    }

    public void exportDataToXML(String outboxDir) throws Exception {
        xmlExporter.exportToXML(outboxDir);
    }
}
