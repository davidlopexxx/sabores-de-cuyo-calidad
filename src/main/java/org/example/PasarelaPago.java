package org.example;

public interface PasarelaPago {

    RespuestaPago cobrar(double monto, String tokenTarjeta);
}
