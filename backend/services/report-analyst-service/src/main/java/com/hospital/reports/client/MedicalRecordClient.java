package com.hospital.reports.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Cliente Feign hacia medical-record-service, resuelto via Eureka con
 * 'lb://medical-record-service' (el 'name' debe coincidir con el
 * spring.application.name registrado por ese servicio en Eureka).
 *
 * SUPOSICION: el endpoint GET /api/medical-records/count no existe todavia en
 * medical-record-service; agregalo alla (un simple repository.count()) o cambia
 * la ruta aqui para que apunte a uno equivalente.
 */
@FeignClient(name = "medical-record-service")
public interface MedicalRecordClient {

    @GetMapping("/api/medical-records/count")
    Long countMedicalRecords();
}
