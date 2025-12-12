package com.laboratorio.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnvioStatsResponse {

    private long total;
    private long programados;
    private long enTransito;
    private long entregados;
    private long cancelados;
    private long enviosDelMes;
    private long vencidos;
}