package com.giovani.restaurante.controller;

import com.giovani.restaurante.dto.PedidoItemRequest;
import com.giovani.restaurante.dto.PedidoItemResponse;
import com.giovani.restaurante.dto.PedidoRequest;
import com.giovani.restaurante.dto.PedidoResponse;
import com.giovani.restaurante.service.PagamentoService;
import com.giovani.restaurante.service.PedidoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PagamentoService pagamentoService;

    public PedidoController(PedidoService pedidoService,  PagamentoService pagamentoService) {
        this.pedidoService = pedidoService;
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse abrirPedido(@RequestBody PedidoRequest pedidoRequest){
        return pedidoService.abrirPedido(pedidoRequest);
    }

    @GetMapping
    public Page<PedidoResponse> listar(Pageable pageable){
        return pedidoService.listar(pageable);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscarPorId(@PathVariable Long id){
        return pedidoService.buscarPorId(id);
    }

    @PostMapping("/{pedidoId}/itens")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoItemResponse adicionarItem(@PathVariable Long pedidoId, @RequestBody PedidoItemRequest request){
        return pedidoService.adicionarItem(pedidoId, request);
    }

    @GetMapping("/{pedidoId}/itens")
    public List<PedidoItemResponse> listarItens (@PathVariable Long pedidoId){
        return pedidoService.listarItens(pedidoId);
    }

    @PostMapping("/pedidos/{pedidoId}/pagar")
    public void pagar(@PathVariable Long pedidoId, @RequestParam String formaPagamento){
        pagamentoService.pagar(pedidoId, formaPagamento);
    }
}
