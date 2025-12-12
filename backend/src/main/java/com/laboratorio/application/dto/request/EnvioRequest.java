package com.laboratorio.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class EnvioRequest {

    @NotNull(message = "El ID de la orden es requerido")
    private Long ordenId;

    @NotBlank(message = "El numero de orden es requerido")
    private String ordenNumber;

    @NotBlank(message = "El tipo de envio es requerido")
    private String tipo; // ENVIO, RETIRO, RETIRO_ENVIO

    private String estado; // PROGRAMADO, EN_TRANSITO, ENTREGADO, CANCELADO

    @NotBlank(message = "El nombre del cliente es requerido")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String clienteNombre;

    @NotBlank(message = "La direccion es requerida")
    @Size(max = 500, message = "La direccion no puede exceder 500 caracteres")
    private String direccion;

    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String ciudad;

    @Size(max = 20, message = "El codigo postal no puede exceder 20 caracteres")
    private String codigoPostal;

    @Size(max = 50, message = "El telefono no puede exceder 50 caracteres")
    private String telefono;

    @Size(max = 100, message = "El transportista no puede exceder 100 caracteres")
    private String transportista;

    @Size(max = 100, message = "El codigo de seguimiento no puede exceder 100 caracteres")
    private String codigoSeguimiento;

    private BigDecimal costoEnvio;

    private LocalDate fechaRetiro;

    private LocalDate fechaEntregaEstimada;

    private LocalDate fechaEntregaReal;

    @Size(max = 1000, message = "Las notas no pueden exceder 1000 caracteres")
    private String notas;

    // Constructors
    public EnvioRequest() {}

    // Getters and Setters
    public Long getOrdenId() {
        return ordenId;
    }

    public void setOrdenId(Long ordenId) {
        this.ordenId = ordenId;
    }

    public String getOrdenNumber() {
        return ordenNumber;
    }

    public void setOrdenNumber(String ordenNumber) {
        this.ordenNumber = ordenNumber;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTransportista() {
        return transportista;
    }

    public void setTransportista(String transportista) {
        this.transportista = transportista;
    }

    public String getCodigoSeguimiento() {
        return codigoSeguimiento;
    }

    public void setCodigoSeguimiento(String codigoSeguimiento) {
        this.codigoSeguimiento = codigoSeguimiento;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public LocalDate getFechaRetiro() {
        return fechaRetiro;
    }

    public void setFechaRetiro(LocalDate fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }

    public LocalDate getFechaEntregaEstimada() {
        return fechaEntregaEstimada;
    }

    public void setFechaEntregaEstimada(LocalDate fechaEntregaEstimada) {
        this.fechaEntregaEstimada = fechaEntregaEstimada;
    }

    public LocalDate getFechaEntregaReal() {
        return fechaEntregaReal;
    }

    public void setFechaEntregaReal(LocalDate fechaEntregaReal) {
        this.fechaEntregaReal = fechaEntregaReal;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}