package com.hospital.billing.controller;

import com.hospital.billing.repository.InvoiceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InvoiceController {

    private final InvoiceRepository invoiceRepository;

    public InvoiceController(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping("/facturas")
    public String listarFacturas(@RequestParam(required = false) String estado, Model model) {
        if (estado != null && !estado.isBlank()) {
            model.addAttribute("invoices", invoiceRepository.findByEstado(estado.toUpperCase()));
            model.addAttribute("filtroEstado", estado.toUpperCase());
        } else {
            model.addAttribute("invoices", invoiceRepository.findAll());
            model.addAttribute("filtroEstado", "");
        }
        return "invoices";
    }
}
