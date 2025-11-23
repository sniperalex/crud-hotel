package core;

import dominio.EntidadeDominio;

public interface IStrategy {
    // O famoso método "processar" do seu diagrama
    void processar(EntidadeDominio entidade);
}