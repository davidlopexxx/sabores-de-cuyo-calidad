package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class CalculadoraPedidoTest {

    private static final double DELTA = 0.01;

    @Test
    void compraSuperiorAlMinimo_tieneEnvioGratis() {
        // Arrange
        CalculadoraPedido calculadora = new CalculadoraPedido();
        double subtotal = 30000;

        // Act
        double total = calculadora.calcularTotal(subtotal, false, null, false);

        // Assert
        assertEquals(30000, total, DELTA);
    }

    @Test
    void compraInferiorAlMinimo_pagaEnvioBase() {
        // Arrange
        CalculadoraPedido calculadora = new CalculadoraPedido();
        double subtotal = 20000;

        // Act
        double total = calculadora.calcularTotal(subtotal, false, null, false);

        // Assert
        assertEquals(23500, total, DELTA); // 20000 + 3500 de envío
    }

    @Test
    void compraInferiorAlMinimo_enInterior_pagaRecargo() {
        // Arrange
        CalculadoraPedido calculadora = new CalculadoraPedido();
        double subtotal = 20000;

        // Act
        double total = calculadora.calcularTotal(subtotal, false, null, true);

        // Assert
        assertEquals(25500, total, DELTA); // 20000 + 3500 + 2000 de recargo
    }

    @Test
    void compraExactaEnElMinimo_pagaEnvio() {
        // Arrange: la política dice "superiores a $25.000", así que $25.000 justo no califica
        CalculadoraPedido calculadora = new CalculadoraPedido();
        double subtotal = 25000;

        // Act
        double total = calculadora.calcularTotal(subtotal, false, null, false);

        // Assert
        assertEquals(28500, total, DELTA);
    }

    @Test
    void primeraCompraConCodigo_aplicaDescuento() {
        // Arrange
        CalculadoraPedido calculadora = new CalculadoraPedido();
        double subtotal = 30000;

        // Act
        double total = calculadora.calcularTotal(subtotal, true, "BIENVENIDA", false);

        // Assert
        assertEquals(27000, total, DELTA); // 10% de descuento y sigue con envío gratis
    }

    @Test
    void descuentoQueBajaDelMinimo_cobraEnvioEnInterior() {
        // Arrange: con el descuento el total queda en 24300, por debajo de $25.000
        CalculadoraPedido calculadora = new CalculadoraPedido();
        double subtotal = 27000;

        // Act
        double total = calculadora.calcularTotal(subtotal, true, "BIENVENIDA", true);

        // Assert
        assertEquals(29800, total, DELTA); // 24300 + 3500 de envío + 2000 de recargo
    }

    @Test
    void primeraCompraConCodigoIncorrecto_noAplicaDescuento() {
        // Arrange
        CalculadoraPedido calculadora = new CalculadoraPedido();
        double subtotal = 20000;

        // Act
        double total = calculadora.calcularTotal(subtotal, true, "OTRO", false);

        // Assert
        assertEquals(23500, total, DELTA); // sin descuento: 20000 + 3500 de envío
    }

    @Test
    void codigoCorrectoSinSerPrimeraCompra_noAplicaDescuento() {
        // Arrange
        CalculadoraPedido calculadora = new CalculadoraPedido();
        double subtotal = 30000;

        // Act
        double total = calculadora.calcularTotal(subtotal, false, "BIENVENIDA", false);

        // Assert
        assertEquals(30000, total, DELTA);
    }

    @Test
    void subtotalCero_lanzaExcepcion() {
        // Arrange
        CalculadoraPedido calculadora = new CalculadoraPedido();
        Executable calculo = () -> calculadora.calcularTotal(0, false, null, false);

        // Act
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, calculo);

        // Assert
        assertEquals("El subtotal debe ser mayor a 0", error.getMessage());
    }
}
