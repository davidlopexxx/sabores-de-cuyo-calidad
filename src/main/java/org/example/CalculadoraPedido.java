package org.example;

public class CalculadoraPedido {

    public static final double ENVIO_GRATIS_DESDE = 25000;
    public static final double DESCUENTO_BIENVENIDA = 0.10;
    public static final double ENVIO_BASE = 3500;
    public static final double RECARGO_INTERIOR = 2000;
    public static final String CODIGO_BIENVENIDA = "BIENVENIDA";

    public double calcularTotal(
            double subtotal, boolean esPrimeraCompra, String codigo, boolean esInterior) {
        if (subtotal <= 0) {
            throw new IllegalArgumentException("El subtotal debe ser mayor a 0");
        }

        double total = subtotal;
        if (esPrimeraCompra && CODIGO_BIENVENIDA.equals(codigo)) {
            total = total * (1 - DESCUENTO_BIENVENIDA);
        }

        double envio = 0;
        if (total <= ENVIO_GRATIS_DESDE) {
            envio = ENVIO_BASE;
            if (esInterior) {
                envio = envio + RECARGO_INTERIOR;
            }
        }

        return total + envio;
    }
}
