package org.example;

public class PedidoService {

    private final PasarelaPago pasarela;

    public PedidoService(PasarelaPago pasarela) {
        this.pasarela = pasarela;
    }

    public void pagar(Pedido pedido, String tokenTarjeta) {
        if (pedido.getEstado() == EstadoPedido.PAGADO) {
            return; // ya está pagado: no se vuelve a cobrar
        }

        RespuestaPago respuesta = pasarela.cobrar(pedido.getTotal(), tokenTarjeta);

        if (respuesta == RespuestaPago.APROBADO) {
            pedido.setEstado(EstadoPedido.PAGADO);
        } else {
            pedido.setEstado(EstadoPedido.PAGO_RECHAZADO);
        }
    }
}
