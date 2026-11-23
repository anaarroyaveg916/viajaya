package com.dayana.viajaya.services.impl;

import com.dayana.viajaya.services.IBloqueoCupoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BloqueoCupoScheduler {

    private final IBloqueoCupoService bloqueoCupoService;

    @Scheduled(fixedDelay = 30000)
    public void revisarBloqueosVencidos() {
        try {
            bloqueoCupoService.liberarBloqueosVencidos();
        } catch (Exception e) {
            log.error("Error al liberar los bloqueos de cupos vencidos", e);
        }
    }
}