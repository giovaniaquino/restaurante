package com.giovani.restaurante.service;

import com.giovani.restaurante.domain.entity.PedidoItem;
import com.giovani.restaurante.domain.enums.StatusItemPedido;
import com.giovani.restaurante.dto.CozinhaItemResponse;
import com.giovani.restaurante.exception.RegraNegocioException;
import com.giovani.restaurante.repository.PedidoItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CozinhaService {

    private final PedidoItemRepository pedidoItemRepository;

    public CozinhaService(PedidoItemRepository pedidoItemRepository) {
        this.pedidoItemRepository = pedidoItemRepository;
    }

    public List<CozinhaItemResponse> listarItensPendentes(){
        return pedidoItemRepository.findByStatusOrderByIdAsc(StatusItemPedido.PENDENTE)
                .stream()
                .map(CozinhaItemResponse::fromEntity)
                .toList();
    }

    public List<CozinhaItemResponse> listarItensEmPreparo(){
        return pedidoItemRepository.findByStatusOrderByIdAsc(StatusItemPedido.EM_PREPARO)
                .stream()
                .map(CozinhaItemResponse::fromEntity)
                .toList();
    }

    public CozinhaItemResponse inicarPreparo(Long itemId){
        PedidoItem item = buscarItemPorId(itemId);
        if(item.getStatus() != StatusItemPedido.PENDENTE){
            throw new RegraNegocioException("Somente itens pendentes podem iniciar preparo");
        }
        item.setStatus(StatusItemPedido.EM_PREPARO);
        item.setDataInicioPreparo(LocalDateTime.now());

        return CozinhaItemResponse.fromEntity(pedidoItemRepository.save(item));
    }

    public CozinhaItemResponse marcarComoPronto(Long itemId){
        PedidoItem item = buscarItemPorId(itemId);
        if(item.getStatus() != StatusItemPedido.EM_PREPARO){
            throw new RegraNegocioException("Somente itens em preparo podem marcar como pronto");
        }
        item.setStatus(StatusItemPedido.PRONTO);
        item.setDataPronto(LocalDateTime.now());

        return CozinhaItemResponse.fromEntity(pedidoItemRepository.save(item));
    }

    public CozinhaItemResponse entregarItem(Long itemId){
        PedidoItem item = buscarItemPorId(itemId);
        if(item.getStatus() != StatusItemPedido.PRONTO){
            throw new RegraNegocioException("Somente itens em prontos podem entregues");
        }
        item.setStatus(StatusItemPedido.ENTREGUE);
        item.setDataEntrega(LocalDateTime.now());

        return CozinhaItemResponse.fromEntity(pedidoItemRepository.save(item));
    }

    private PedidoItem buscarItemPorId(Long itemId){
        return pedidoItemRepository.findById(itemId)
                .orElseThrow(() -> new RegraNegocioException("Item do pedido não encontrado"));
    }
}
