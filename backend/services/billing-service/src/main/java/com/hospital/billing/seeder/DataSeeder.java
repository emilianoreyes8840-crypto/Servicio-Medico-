package com.hospital.billing.seeder;

import com.hospital.billing.entity.Invoice;
import com.hospital.billing.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final InvoiceRepository invoiceRepository;

    public DataSeeder(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public void run(String... args) {
        if (invoiceRepository.count() > 0) {
            log.info("billing_db ya contiene datos, se omite el seeding.");
            return;
        }

        List<Invoice> seedData = List.of(
                new Invoice(1L, 101L, "Consulta General", new BigDecimal("450.00")),
                new Invoice(2L, 102L, "Consulta Pediatria", new BigDecimal("600.00")),
                new Invoice(3L, 103L, "Analisis de Laboratorio", new BigDecimal("980.50"))
        );

        invoiceRepository.saveAll(seedData);
        log.info("Se insertaron {} facturas de ejemplo en billing_db.", seedData.size());
    }
}
