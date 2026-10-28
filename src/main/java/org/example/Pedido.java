package org.example;

public class Pedido {

    private final double total;
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    public Pedido(double total) {
        this.total = total;
    }

    public double getTotal() {
        return total;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String toString() {
        return "Pedido{total=" + total + ", estado=" + estado + "}";
    }
}
