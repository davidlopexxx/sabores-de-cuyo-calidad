package org.example.legacy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GestorPedidosLegacy {

    private static final Logger LOG = LogManager.getLogger(GestorPedidosLegacy.class);

    // Calcula el precio final de un pedido
    public double calcularPrecioFinal(String tipoCliente, String provincia, String medioPago,
                                      double subtotal, int cantidadItems, boolean tieneCupon,
                                      String cupon, boolean esTemporadaAlta) {
        double total = subtotal;
        // descuento segun tipo de cliente
        if (tipoCliente != null) {
            if (tipoCliente.equals("MINORISTA")) {
                if (cantidadItems >= 10) {
                    total = total * 0.95;
                } else if (cantidadItems >= 5) {
                    total = total * 0.98;
                }
            } else if (tipoCliente.equals("MAYORISTA")) {
                if (cantidadItems >= 50) {
                    total = total * 0.80;
                } else if (cantidadItems >= 20) {
                    total = total * 0.85;
                } else if (cantidadItems >= 10) {
                    total = total * 0.90;
                } else {
                    total = total * 0.95;
                }
            } else if (tipoCliente.equals("VIP")) {
                total = total * 0.85;
                if (cantidadItems >= 10) {
                    total = total * 0.97;
                }
            } else {
                LOG.warn("Tipo de cliente desconocido: " + tipoCliente);
            }
        }
        // cupon de descuento
        if (tieneCupon) {
            if (cupon != null) {
                if (cupon.equals("BIENVENIDA")) {
                    total = total * 0.90;
                } else if (cupon.equals("VINO10")) {
                    total = total * 0.90;
                } else if (cupon.equals("FIESTA20")) {
                    if (esTemporadaAlta) {
                        total = total * 0.90;
                    } else {
                        total = total * 0.80;
                    }
                } else {
                    LOG.warn("Cupon invalido: " + cupon);
                }
            }
        }
        // costo de envio
        double envio = 0;
        if (provincia != null) {
            if (provincia.equals("MENDOZA")) {
                if (total < 25000) {
                    envio = 1500;
                } else {
                    envio = 0;
                }
            } else if (provincia.equals("SAN JUAN")) {
                if (total < 25000) {
                    envio = 2000;
                } else {
                    envio = 500;
                }
            } else if (provincia.equals("SAN LUIS")) {
                if (total < 25000) {
                    envio = 2000;
                } else {
                    envio = 500;
                }
            } else if (provincia.equals("BUENOS AIRES")) {
                if (total < 25000) {
                    envio = 3500;
                } else {
                    envio = 2000;
                }
            } else if (provincia.equals("CORDOBA")) {
                if (total < 25000) {
                    envio = 3500;
                } else {
                    envio = 2000;
                }
            } else {
                if (total < 25000) {
                    envio = 4500;
                } else {
                    envio = 3000;
                }
            }
        } else {
            envio = 4500;
        }
        if (esTemporadaAlta) {
            envio = envio * 1.2;
        }
        // recargo segun medio de pago
        double recargo = 0;
        if (medioPago != null) {
            if (medioPago.equals("EFECTIVO")) {
                recargo = -total * 0.05;
            } else if (medioPago.equals("DEBITO")) {
                recargo = 0;
            } else if (medioPago.equals("CREDITO_1")) {
                recargo = total * 0.03;
            } else if (medioPago.equals("CREDITO_3")) {
                recargo = total * 0.08;
            } else if (medioPago.equals("CREDITO_6")) {
                recargo = total * 0.15;
            } else {
                LOG.warn("Medio de pago desconocido: " + medioPago);
                recargo = total * 0.20;
            }
        }
        double resultado = total + envio + recargo;
        if (resultado < 0) {
            resultado = 0;
        }
        LOG.info("Precio final: " + resultado);
        return resultado;
    }

    public String generarResumenMinorista(String cliente, double total, int items) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== RESUMEN DE PEDIDO =====\n");
        sb.append("Cliente: ").append(cliente).append("\n");
        sb.append("Tipo: MINORISTA\n");
        sb.append("Items: ").append(items).append("\n");
        sb.append("Total: $").append(String.format("%.2f", total)).append("\n");
        if (total > 100000) {
            sb.append("Atencion: pedido de alto valor, requiere revision\n");
        }
        sb.append("=============================\n");
        return sb.toString();
    }

    public String generarResumenMayorista(String cliente, double total, int items) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== RESUMEN DE PEDIDO =====\n");
        sb.append("Cliente: ").append(cliente).append("\n");
        sb.append("Tipo: MAYORISTA\n");
        sb.append("Items: ").append(items).append("\n");
        sb.append("Total: $").append(String.format("%.2f", total)).append("\n");
        if (total > 500000) {
            sb.append("Atencion: pedido de alto valor, requiere revision\n");
        }
        sb.append("=============================\n");
        return sb.toString();
    }

    public String generarResumenVip(String cliente, double total, int items) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== RESUMEN DE PEDIDO =====\n");
        sb.append("Cliente: ").append(cliente).append("\n");
        sb.append("Tipo: VIP\n");
        sb.append("Items: ").append(items).append("\n");
        sb.append("Total: $").append(String.format("%.2f", total)).append("\n");
        if (total > 250000) {
            sb.append("Atencion: pedido de alto valor, requiere revision\n");
        }
        sb.append("=============================\n");
        return sb.toString();
    }
}
