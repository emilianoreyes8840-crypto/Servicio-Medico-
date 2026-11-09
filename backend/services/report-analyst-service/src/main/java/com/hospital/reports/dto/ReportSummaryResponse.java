package com.hospital.reports.dto;

import java.math.BigDecimal;
import java.util.Map;

public class ReportSummaryResponse {

    private long totalFacturas;
    private BigDecimal ingresosTotales;
    private BigDecimal ingresoPromedioPorFactura;

    private long totalCitas;
    private Map<String, Long> citasPorEstado;

    private long totalPacientesRegistrados;
    private long totalRecetasEmitidas;

    private Long totalExpedientesMedicos;

    public ReportSummaryResponse() {
    }

    public long getTotalFacturas() {
        return totalFacturas;
    }

    public void setTotalFacturas(long totalFacturas) {
        this.totalFacturas = totalFacturas;
    }

    public BigDecimal getIngresosTotales() {
        return ingresosTotales;
    }

    public void setIngresosTotales(BigDecimal ingresosTotales) {
        this.ingresosTotales = ingresosTotales;
    }

    public BigDecimal getIngresoPromedioPorFactura() {
        return ingresoPromedioPorFactura;
    }

    public void setIngresoPromedioPorFactura(BigDecimal ingresoPromedioPorFactura) {
        this.ingresoPromedioPorFactura = ingresoPromedioPorFactura;
    }

    public long getTotalCitas() {
        return totalCitas;
    }

    public void setTotalCitas(long totalCitas) {
        this.totalCitas = totalCitas;
    }

    public Map<String, Long> getCitasPorEstado() {
        return citasPorEstado;
    }

    public void setCitasPorEstado(Map<String, Long> citasPorEstado) {
        this.citasPorEstado = citasPorEstado;
    }

    public long getTotalPacientesRegistrados() {
        return totalPacientesRegistrados;
    }

    public void setTotalPacientesRegistrados(long totalPacientesRegistrados) {
        this.totalPacientesRegistrados = totalPacientesRegistrados;
    }

    public long getTotalRecetasEmitidas() {
        return totalRecetasEmitidas;
    }

    public void setTotalRecetasEmitidas(long totalRecetasEmitidas) {
        this.totalRecetasEmitidas = totalRecetasEmitidas;
    }

    public Long getTotalExpedientesMedicos() {
        return totalExpedientesMedicos;
    }

    public void setTotalExpedientesMedicos(Long totalExpedientesMedicos) {
        this.totalExpedientesMedicos = totalExpedientesMedicos;
    }
}
