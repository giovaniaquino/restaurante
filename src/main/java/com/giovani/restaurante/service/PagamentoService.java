package com.giovani.restaurante.service;

import com.giovani.restaurante.client.PagamentoClient;
import com.giovani.restaurante.domain.entity.FechamentoConta;
import com.giovani.restaurante.domain.entity.Mesa;
import com.giovani.restaurante.domain.entity.Pagamento;
import com.giovani.restaurante.domain.entity.Pedido;
import com.giovani.restaurante.domain.enums.FormaPagamento;
import com.giovani.restaurante.domain.enums.StatusMesa;
import com.giovani.restaurante.domain.enums.StatusPagamento;
import com.giovani.restaurante.domain.enums.StatusPedido;
import com.giovani.restaurante.dto.PagamentoRequest;
import com.giovani.restaurante.dto.PagamentoResponse;
import com.giovani.restaurante.exception.RegraNegocioException;
import com.giovani.restaurante.repository.FechamentoContaRepository;
import com.giovani.restaurante.repository.MesaRepository;
import com.giovani.restaurante.repository.PagamentoRepository;
import com.giovani.restaurante.repository.PedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    private final PagamentoClient pagamentoClient;
    private final FechamentoContaRepository fechamentoContaRepository;
    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final PagamentoRepository pagamentoRepository;

    public PagamentoService(PagamentoClient pagamentoClient, FechamentoContaRepository fechamentoContaRepository, PedidoRepository pedidoRepository, MesaRepository mesaRepository, PagamentoRepository pagamentoRepository) {
        this.pagamentoClient = pagamentoClient;
        this.fechamentoContaRepository = fechamentoContaRepository;
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    @Transactional
    public void pagar(Long pedidoId, String formaPagamento){
        FechamentoConta fechamento = fechamentoContaRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RegraNegocioException("Conta não encontrada"));

        PagamentoResponse response = pagamentoClient.processar(
                new PagamentoRequest(
                        fechamento.getTotal(),
                        formaPagamento
                )
        );

        if ("APROVADO".equals(response.status())){
            Pedido pedido = fechamento.getPedido();
            pedido.setStatus(StatusPedido.FECHADO);

            Mesa mesa = pedido.getMesa();
            mesa.setStatus(StatusMesa.LIVRE);

            Pagamento pagamento = new Pagamento();
            pagamento.setPedido(pedido);
            pagamento.setFormaPagamento(FormaPagamento.valueOf(formaPagamento));
            pagamento.setStatus(StatusPagamento.APROVADO);
            pagamento.setValor(fechamento.getTotal());
            pagamento.setDataPagamento(fechamento.getDataFechamento());

            pedidoRepository.save(pedido);
            mesaRepository.save(mesa);
            fechamentoContaRepository.save(fechamento);
        }
    }
}
