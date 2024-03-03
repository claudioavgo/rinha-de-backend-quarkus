package com.claudioav;

import com.claudioav.dto.ClienteDTO;
import com.claudioav.models.Cliente;
import com.claudioav.models.Transacao;
import com.claudioav.records.CreateTransacaoResponse;
import com.claudioav.records.ExtratoResponse;
import com.claudioav.records.SaldoCliente;
import com.claudioav.records.TransacaoRequest;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.LocalDateTime;
import java.util.List;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Main {

    @GET
    @Path("/{id}/extrato")
    @Transactional
    @RunOnVirtualThread
    public RestResponse<ExtratoResponse> obterExtratoDoCliente(@PathParam("id") Long clienteId) {
        if (clienteId < 1 || clienteId > 5) return RestResponse.status(404);

        Cliente cliente = Cliente.findById(clienteId, LockModeType.PESSIMISTIC_READ);

        List<Transacao> transacoes = Transacao.findLast10(clienteId);

        SaldoCliente saldo = new SaldoCliente(cliente.saldo, LocalDateTime.now(), cliente.limite);

        return RestResponse.ok(new ExtratoResponse(saldo, transacoes));
    }

    @POST
    @Path("/{id}/transacoes")
    @RunOnVirtualThread
    @Transactional
    public RestResponse<CreateTransacaoResponse> realizarTransacao(@PathParam("id") Long clienteId, TransacaoRequest transacaoRequest) {
        if (clienteId < 1 || clienteId > 5) return RestResponse.status(404);

        if (!validarTransacao(transacaoRequest)) return RestResponse.status(422);

        Cliente cliente = Cliente.findById(clienteId, LockModeType.PESSIMISTIC_WRITE);

        int valor = transacaoRequest.valor().intValue();
        int limite = cliente.limite;
        int saldo = cliente.saldo;

        if (transacaoRequest.tipo() == 'c') {
            saldo += valor;
        } else if (transacaoRequest.tipo() == 'd') {
            if ((saldo - valor) < -limite) {
                return RestResponse.status(422);
            }
            saldo -= valor;
        } else {
            return RestResponse.status(422);
        }

        cliente.saldo = saldo;

        new Transacao(
                transacaoRequest.valor().intValue(),
                transacaoRequest.tipo(),
                transacaoRequest.descricao(),
                LocalDateTime.now(),
                clienteId
        ).persist();

        cliente.persist();

        return RestResponse.ok(new CreateTransacaoResponse(limite, saldo));
    }

    @POST
    @Path("/cadastrar")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Response cadastrar_novo_cliente(ClienteDTO clienteDTO) {

        new Cliente(clienteDTO.saldo, clienteDTO.limite).persist();

        return Response.status(201).build();
    }

    private boolean validarTransacao(TransacaoRequest transacao) {
        return transacao.descricao() != null
                && transacao.valor() != null
                && !transacao.descricao().isEmpty()
                && transacao.descricao().length() <= 10
                && validarTipo(transacao.tipo())
                && validarValor(transacao.valor()
        );
    }

    private boolean validarValor(double valor) {
        return valor % 1 == 0;
    }

    private boolean validarTipo(char type) {
        return type == 'c' || type == 'd';
    }
}
