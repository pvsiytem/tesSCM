package com.test.service;

import com.test.model.PurchaseOrderDetail;
import com.test.model.PurchaseOrderHeader;
import com.test.repository.DetailRepository;
import com.test.repository.HeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class XMLExporter {

    @Autowired private HeaderRepository headerRepo;
    @Autowired private DetailRepository detailRepo;

    public void exportToXML(String outboxDir) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        Element root = doc.createElement("HEADERS");
        doc.appendChild(root);

        for (PurchaseOrderHeader h : headerRepo.findAll()) {
            Element he = doc.createElement("HEADER");
            addTextElement(doc, he, "PO_NUMBER", h.getPoNumber());
            addTextElement(doc, he, "PO_DATE", new SimpleDateFormat("yyyy-MM-dd").format(h.getPoDate()));
            addTextElement(doc, he, "BUYER_NAME", h.getBuyerName());
            addTextElement(doc, he, "BUYER_ADDR", h.getBuyerAddress());
            Element details = doc.createElement("DETAILS");
            he.appendChild(details);

            List<PurchaseOrderDetail> list = detailRepo.findByPurchaseOrderHeader_PoNumber(h.getPoNumber());
            for (var d : list) {
                Element de = doc.createElement("DETAIL");
                addTextElement(doc, de, "PART_NO", d.getPartNo());
                addTextElement(doc, de, "PART_NAME", d.getPartName());
                addTextElement(doc, de, "QTY", d.getQty().toString());
                addTextElement(doc, de, "UNIT", d.getUnit());
                addTextElement(doc, de, "PRICE", d.getPrice().toString());
                details.appendChild(de);
            }
            root.appendChild(he);
        }

        File outputFile = new File(outboxDir, "output.xml");
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.transform(new DOMSource(doc), new StreamResult(outputFile));
    }

    private void addTextElement(Document doc, Element parent, String name, String txt) {
        Element el = doc.createElement(name);
        el.appendChild(doc.createTextNode(txt));
        parent.appendChild(el);
    }
}
