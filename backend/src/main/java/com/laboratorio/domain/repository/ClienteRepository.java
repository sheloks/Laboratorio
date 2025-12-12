package com.laboratorio.domain.repository;

import com.laboratorio.domain.entity.cliente.Cliente;
import com.laboratorio.domain.entity.cliente.TipoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Buscar cliente por email
     */
    Optional<Cliente> findByEmail(String email);

    /**
     * Verificar si existe un cliente con ese email
     */
    boolean existsByEmail(String email);

    /**
     * Buscar clientes por tipo
     */
    List<Cliente> findByTipoCliente(TipoCliente tipoCliente);

    /**
     * Buscar clientes activos
     */
    List<Cliente> findByActivo(Boolean activo);

    /**
     * Buscar por nombre o email (búsqueda)
     */
    List<Cliente> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

    /**
     * Contar clientes activos
     */
    long countByActivo(Boolean activo);

    /**
     * Contar clientes por tipo
     */
    long countByTipoCliente(TipoCliente tipoCliente);

    /**
     * Buscar clientes por nombre
     */
    List<Cliente> findByNameContainingIgnoreCase(String name);
}