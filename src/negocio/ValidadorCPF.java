package negocio;

import core.IStrategy;
import dominio.EntidadeDominio;
import dominio.Hospede;

public class ValidadorCPF implements IStrategy {

    @Override
    public void processar(EntidadeDominio entidade) {
        if (entidade instanceof Hospede) {
            Hospede h = (Hospede) entidade;
            if (h.getCpf() == null || h.getCpf().length() != 11) {
                throw new RuntimeException("Erro de Validação: CPF deve ter 11 dígitos numéricos.");
            }
            // Aqui entraria o algoritmo de módulo 11 real
            System.out.println("Strategy: CPF validado com sucesso.");
        }
    }
}