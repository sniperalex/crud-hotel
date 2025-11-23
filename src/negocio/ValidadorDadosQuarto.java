package negocio;

import core.IStrategy;
import dominio.EntidadeDominio;
import dominio.Quarto;

public class ValidadorDadosQuarto implements IStrategy {

    @Override
    public void processar(EntidadeDominio entidade) {
        if(entidade instanceof Quarto) {
            Quarto q = (Quarto) entidade;
            
            if(q.getNumero() <= 0) {
                throw new RuntimeException("Número do quarto inválido.");
            }
            if(q.getPrecoBase() <= 0) {
                throw new RuntimeException("O preço base deve ser maior que zero.");
            }
            if(q.getCapAdultos() < 1) {
                throw new RuntimeException("O quarto deve comportar pelo menos 1 adulto.");
            }
            System.out.println("Strategy: Dados do quarto validados.");
        }
    }
}