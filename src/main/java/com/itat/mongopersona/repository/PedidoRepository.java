package com.itat.mongopersona.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.itat.mongopersona.event.Pedido;

public interface PedidoRepository
        extends MongoRepository<Pedido, String> {

    int countByHora(String hora);
}