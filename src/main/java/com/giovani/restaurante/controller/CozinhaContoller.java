package com.giovani.restaurante.controller;

import com.giovani.restaurante.dto.CozinhaItemResponse;
import com.giovani.restaurante.service.CozinhaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cozinha")
public class CozinhaContoller {

    private final CozinhaService cozinhaService;

    public CozinhaContoller(CozinhaService cozinhaService) {
        this.cozinhaService = cozinhaService;
    }

    @GetMapping("/itens-pendentes")
    public List<CozinhaItemResponse> listarItensPendentes(){
        return cozinhaService.listarItensPendentes();
    }

    @GetMapping("/itens-em-preparo")
    public List<CozinhaItemResponse> listarItensEmPreparo(){
        return cozinhaService.listarItensEmPreparo();
    }

    @PatchMapping("/itens/{itemId}/iniciar-preparo")
    public CozinhaItemResponse inicarPreparo(@PathVariable Long itemId){
        return cozinhaService.inicarPreparo(itemId);
    }

    @PatchMapping("/itens/{itemId}/marcar-pronto")
    public CozinhaItemResponse marcarComoPronto(@PathVariable Long itemId){
        return cozinhaService.marcarComoPronto(itemId);
    }

    @PatchMapping("/itens/{itemId}/entregar")
    public CozinhaItemResponse entregarItem(@PathVariable Long itemId){
        return cozinhaService.entregarItem(itemId);
    }
}
