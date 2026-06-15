package com.giovani.restaurante.service;

import com.giovani.restaurante.domain.entity.Mesa;
import com.giovani.restaurante.domain.entity.Pedido;
import com.giovani.restaurante.domain.enums.StatusMesa;
import com.giovani.restaurante.domain.enums.StatusPedido;
import com.giovani.restaurante.dto.PedidoRequest;
import com.giovani.restaurante.dto.PedidoResponse;
import com.giovani.restaurante.repository.MesaRepository;
import com.giovani.restaurante.repository.PedidoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;

    public PedidoService(PedidoRepository pedidoRepository, MesaRepository mesaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
    }

    public PedidoResponse abrirPedido(PedidoRequest pedidoRequest) {
        Mesa mesa = mesaRepository.findById(pedidoRequest.mesaId())
                .orElseThrow(() -> new RuntimeException("Mesa inexistente"));

        if (mesa.getStatus() != StatusMesa.LIVRE){
            throw new RuntimeException("Mesa não disponível");
        }

        Pedido pedido = new Pedido();
        pedido.setMesa(mesa);
        pedido.setStatus(StatusPedido.ABERTO);
        pedido.setObservacao(pedidoRequest.observacao());

        mesa.setStatus(StatusMesa.OCUPADA);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        mesaRepository.save(mesa);

        return PedidoResponse.fromEntity(pedidoSalvo);
    }

    public Page<PedidoResponse> listar(Pageable pageable) {
        return pedidoRepository.findAll(pageable).map(PedidoResponse::fromEntity);
    }

    public PedidoResponse buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido inexistente"));
        return PedidoResponse.fromEntity(pedido);
    }
}
