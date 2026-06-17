package com.giovani.restaurante.dto;

import com.giovani.restaurante.domain.entity.PedidoItem;
import com.giovani.restaurante.domain.enums.StatusItemPedido;

import java.math.BigDecimal;

public record PedidoItemResponse(
        Long id,
        Long pedidoId,
        Long produtoid,
        String produtoNome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal Total,
        String observacao,
        StatusItemPedido status
) {

    public static PedidoItemResponse fromEntity(PedidoItem item) {
        BigDecimal total = item.getPrecoUnitario()
                .multiply(BigDecimal.valueOf(item.getQuantidade()));

        return new PedidoItemResponse(
                item.getId(),
                item.getPedido().getId(),
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                total,
                item.getObservacao(),
                item.getStatus()
        );
    }
}
