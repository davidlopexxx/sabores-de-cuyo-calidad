package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PedidoServiceTest {

    // STUB: devuelve siempre una respuesta fija (inyecta datos indirectos).
    static class PasarelaStub implements PasarelaPago {
        private final RespuestaPago respuesta;

        PasarelaStub(RespuestaPago respuesta) {
            this.respuesta = respuesta;
        }

        @Override
        public RespuestaPago cobrar(double monto, String tokenTarjeta) {
            return respuesta;
        }
    }

    // MOCK: sabe qué llamada espera y después verifica que haya ocurrido.
    static class PasarelaMock implements PasarelaPago {
        private final double montoEsperado;
        private final String tokenEsperado;
        private int llamadas = 0;

        PasarelaMock(double montoEsperado, String tokenEsperado) {
            this.montoEsperado = montoEsperado;
            this.tokenEsperado = tokenEsperado;
        }

        @Override
        public RespuestaPago cobrar(double monto, String tokenTarjeta) {
            llamadas++;
            if (Math.abs(monto - montoEsperado) > 0.01 || !tokenEsperado.equals(tokenTarjeta)) {
                throw new AssertionError(
                        "Llamada inesperada a cobrar(" + monto + ", " + tokenTarjeta + ")");
            }
            return RespuestaPago.APROBADO;
        }

        void verificarLlamadas(int esperadas) {
            if (llamadas != esperadas) {
                throw new AssertionError(
                        "Se esperaban " + esperadas + " cobro(s) y hubo " + llamadas);
            }
        }
    }

    // ---------- Pruebas con STUB (se verifica el ESTADO) ----------

    @Test
    void pagoAprobado_marcaElPedidoComoPagado() {
        // Arrange
        PasarelaStub pasarela = new PasarelaStub(RespuestaPago.APROBADO);
        PedidoService servicio = new PedidoService(pasarela);
        Pedido pedido = new Pedido(18000);

        // Act
        servicio.pagar(pedido, "tok_123");

        // Assert
        assertEquals(EstadoPedido.PAGADO, pedido.getEstado());
    }

    @Test
    void pagoRechazado_marcaElPedidoComoRechazado() {
        // Arrange
        PasarelaStub pasarela = new PasarelaStub(RespuestaPago.RECHAZADO);
        PedidoService servicio = new PedidoService(pasarela);
        Pedido pedido = new Pedido(18000);

        // Act
        servicio.pagar(pedido, "tok_123");

        // Assert
        assertEquals(EstadoPedido.PAGO_RECHAZADO, pedido.getEstado());
    }

    // ---------- Pruebas con MOCK (se verifica el COMPORTAMIENTO) ----------

    @Test
    void pagar_cobraUnaSolaVezConElMontoYElTokenCorrectos() {
        // Arrange
        PasarelaMock pasarela = new PasarelaMock(18000, "tok_123");
        PedidoService servicio = new PedidoService(pasarela);
        Pedido pedido = new Pedido(18000);

        // Act
        servicio.pagar(pedido, "tok_123");

        // Assert
        pasarela.verificarLlamadas(1);
    }

    @Test
    void pedidoYaPagado_noVuelveACobrar() {
        // Arrange
        PasarelaMock pasarela = new PasarelaMock(18000, "tok_123");
        PedidoService servicio = new PedidoService(pasarela);
        Pedido pedido = new Pedido(18000);
        pedido.setEstado(EstadoPedido.PAGADO);

        // Act
        servicio.pagar(pedido, "tok_123");

        // Assert
        pasarela.verificarLlamadas(0);
    }
}
