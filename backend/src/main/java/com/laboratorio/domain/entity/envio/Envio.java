package com.laboratorio.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la orden
    @Column(name = "orden_id", nullable = false)
    private Long ordenId;

    @Column(name = "orden_number", nullable = false)
    private String ordenNumber;

    // Tipo de envío: ENVIO, RETIRO, RETIRO_ENVIO
    @Column(name = "tipo", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoEnvio tipo;

    // Estado: PROGRAMADO, EN_TRANSITO, ENTREGADO, CANCELADO
    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoEnvio estado;

    // Datos del destinatario
    @Column(name = "cliente_nombre", nullable = false)
    private String clienteNombre;

    @Column(name = "direccion", nullable = false)
    private String direccion;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "codigo_postal")
    private String codigoPostal;

    @Column(name = "telefono")
    private String telefono;

    // Datos del transporte
    @Column(name = "transportista")
    private String transportista;

    @Column(name = "codigo_seguimiento")
    private String codigoSeguimiento;

    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio;

    // Fechas
    @Column(name = "fecha_retiro")
    private LocalDate fechaRetiro;

    @Column(name = "fecha_entrega_estimada")
    private LocalDate fechaEntregaEstimada;

    @Column(name = "fecha_entrega_real")
    private LocalDate fechaEntregaReal;

    // Notas
    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    // Auditoría
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum TipoEnvio {
        ENVIO,
        RETIRO,
        RETIRO_ENVIO
    }

    public enum EstadoEnvio {
        PROGRAMADO,
        EN_TRANSITO,
        ENTREGADO,
        CANCELADO
    }

    // Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoEnvio.PROGRAMADO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Envio() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public TipoEnvio getTipo() {
        return tipo;
    }

    public void setTipo(TipoEnvio tipo) {
        this.tipo = tipo;
    }

    public EstadoEnvio getEstado() {
        return estado;
    }

    public void setEstado(EstadoEnvio estado) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}