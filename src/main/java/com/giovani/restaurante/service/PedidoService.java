package com.giovani.restaurante.service;

import com.giovani.restaurante.domain.entity.Mesa;
import com.giovani.restaurante.domain.entity.Pedido;
import com.giovani.restaurante.domain.entity.PedidoItem;
import com.giovani.restaurante.domain.entity.Produto;
import com.giovani.restaurante.domain.enums.StatusItemPedido;
import com.giovani.restaurante.domain.enums.StatusMesa;
import com.giovani.restaurante.domain.enums.StatusPedido;
import com.giovani.restaurante.dto.PedidoItemRequest;
import com.giovani.restaurante.dto.PedidoItemResponse;
import com.giovani.restaurante.dto.PedidoRequest;
import com.giovani.restaurante.dto.PedidoResponse;
import com.giovani.restaurante.exception.RegraNegocioException;
import com.giovani.restaurante.repository.MesaRepository;
import com.giovani.restaurante.repository.PedidoItemRepository;
import com.giovani.restaurante.repository.PedidoRepository;
import com.giovani.restaurante.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoItemRepository pedidoItemRepository;

    public PedidoService(PedidoRepository pedidoRepository, MesaRepository mesaRepository,  ProdutoRepository produtoRepository, PedidoItemRepository pedidoItemRepository) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
    }

    public PedidoResponse abrirPedido(PedidoRequest pedidoRequest) {
        Mesa mesa = mesaRepository.findById(pedidoRequest.mesaId())
                .orElseThrow(() -> new RegraNegocioException("Mesa inexistente"));

        if (mesa.getStatus() != StatusMesa.LIVRE){
            throw new RegraNegocioException("Mesa não disponível");
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
        Pedido pedido = buscarPedidoPorId(id);
        return PedidoResponse.fromEntity(pedido);
    }

    public PedidoItemResponse adicionarItem(Long pedidoId, PedidoItemRequest request){
        Pedido pedido = buscarPedidoPorId(pedidoId);
        if (pedido.getStatus() != StatusPedido.ABERTO){
            throw new RegraNegocioException("Não é possível adicionar item em pedidos não aberto");
        }

        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado"));

        if (!produto.getDisponivel()){
            throw new RegraNegocioException("Produto indisponível");
        }

        if (request.quantidade() == null || request.quantidade() <=0){
            throw new RegraNegocioException("Quantidade deve ser maior que 0");
        }

        PedidoItem pedidoItem = new PedidoItem();
        pedidoItem.setPedido(pedido);
        pedidoItem.setProduto(produto);
        pedidoItem.setQuantidade(request.quantidade());
        pedidoItem.setPrecoUnitario(produto.getPreco());
        pedidoItem.setObservacao(request.observacao());
        pedidoItem.setStatus(StatusItemPedido.PENDENTE);
        PedidoItem itemSalvo = pedidoItemRepository.save(pedidoItem);
        return PedidoItemResponse.fromEntity(itemSalvo);
    }

    public List<PedidoItemResponse> listarItens(Long pedidoId) {
        buscarPedidoPorId(pedidoId);

        return pedidoItemRepository.findByPedidoId(pedidoId).stream().map(PedidoItemResponse::fromEntity).collect(Collectors.toList());
    }

    private Pedido buscarPedidoPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado"));
    }
}
