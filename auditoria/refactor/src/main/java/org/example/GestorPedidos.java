package org.example;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GestorPedidos {

    private static final Logger LOG = LogManager.getLogger(GestorPedidos.class);

    private static final double ENVIO_REDUCIDO_DESDE = 25000;
    private static final double ENVIO_SIN_PROVINCIA = 4500;
    private static final double RECARGO_TEMPORADA_ALTA = 1.2;

    private static final TarifaEnvio TARIFA_GENERAL = new TarifaEnvio(4500, 3000);
    private static final Map<String, TarifaEnvio> TARIFAS =
            Map.of(
                    "MENDOZA", new TarifaEnvio(1500, 0),
                    "SAN JUAN", new TarifaEnvio(2000, 500),
                    "SAN LUIS", new TarifaEnvio(2000, 500),
                    "BUENOS AIRES", new TarifaEnvio(3500, 2000),
                    "CORDOBA", new TarifaEnvio(3500, 2000));

    private static final Map<String, Double> UMBRAL_ALERTA =
            Map.of("MINORISTA", 100000.0, "MAYORISTA", 500000.0, "VIP", 250000.0);

    public record DatosPedido(
            String tipoCliente,
            String provincia,
            String medioPago,
            double subtotal,
            int cantidadItems,
            String cupon,
            boolean temporadaAlta) {}

    public double calcularPrecioFinal(DatosPedido pedido) {
        double total =
                aplicarDescuentoCliente(
                        pedido.tipoCliente(), pedido.cantidadItems(), pedido.subtotal());
        total = aplicarCupon(pedido.cupon(), pedido.temporadaAlta(), total);
        double envio = calcularEnvio(pedido.provincia(), total, pedido.temporadaAlta());
        double recargo = calcularRecargoPago(pedido.medioPago(), total);
        double resultado = Math.max(0, total + envio + recargo);
        LOG.info("Precio final: " + resultado);
        return resultado;
    }

    private double aplicarDescuentoCliente(String tipoCliente, int items, double total) {
        if (tipoCliente == null) {
            return total;
        }
        return switch (tipoCliente) {
            case "MINORISTA" -> total * factorMinorista(items);
            case "MAYORISTA" -> total * factorMayorista(items);
            case "VIP" -> descuentoVip(items, total);
            default -> {
                LOG.warn("Tipo de cliente desconocido: " + tipoCliente);
                yield total;
            }
        };
    }

    private double factorMinorista(int items) {
        if (items >= 10) {
            return 0.95;
        }
        return items >= 5 ? 0.98 : 1.0;
    }

    private double factorMayorista(int items) {
        if (items >= 50) {
            return 0.80;
        }
        if (items >= 20) {
            return 0.85;
        }
        return items >= 10 ? 0.90 : 0.95;
    }

    private double descuentoVip(int items, double total) {
        double conDescuento = total * 0.85;
        return items >= 10 ? conDescuento * 0.97 : conDescuento;
    }

    private double aplicarCupon(String cupon, boolean temporadaAlta, double total) {
        if (cupon == null) {
            return total;
        }
        return switch (cupon) {
            case "BIENVENIDA", "VINO10" -> total * 0.90;
            case "FIESTA20" -> total * (temporadaAlta ? 0.90 : 0.80);
            default -> {
                LOG.warn("Cupon invalido: " + cupon);
                yield total;
            }
        };
    }

    private double calcularEnvio(String provincia, double total, boolean temporadaAlta) {
        double envio =
                provincia == null
                        ? ENVIO_SIN_PROVINCIA
                        : TARIFAS.getOrDefault(provincia, TARIFA_GENERAL).costo(total);
        return temporadaAlta ? envio * RECARGO_TEMPORADA_ALTA : envio;
    }

    private double calcularRecargoPago(String medioPago, double total) {
        if (medioPago == null) {
            return 0;
        }
        return switch (medioPago) {
            case "EFECTIVO" -> -total * 0.05;
            case "DEBITO" -> 0;
            case "CREDITO_1" -> total * 0.03;
            case "CREDITO_3" -> total * 0.08;
            case "CREDITO_6" -> total * 0.15;
            default -> {
                LOG.warn("Medio de pago desconocido: " + medioPago);
                yield total * 0.20;
            }
        };
    }

    public String generarResumen(String tipoCliente, String cliente, double total, int items) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== RESUMEN DE PEDIDO =====\n");
        sb.append("Cliente: ").append(cliente).append("\n");
        sb.append("Tipo: ").append(tipoCliente).append("\n");
        sb.append("Items: ").append(items).append("\n");
        sb.append("Total: $").append(String.format("%.2f", total)).append("\n");
        if (total > UMBRAL_ALERTA.getOrDefault(tipoCliente, Double.MAX_VALUE)) {
            sb.append("Atencion: pedido de alto valor, requiere revision\n");
        }
        sb.append("=============================\n");
        return sb.toString();
    }

    private record TarifaEnvio(double bajo, double alto) {
        double costo(double total) {
            return total < ENVIO_REDUCIDO_DESDE ? bajo : alto;
        }
    }
}
