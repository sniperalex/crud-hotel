package controle;

import core.IDAO;
import core.IStrategy;
import dao.HospedeDAO;
import dao.QuartoDAO;
import dominio.EntidadeDominio;
import dominio.Hospede;
import dominio.Quarto;
import negocio.ValidadorCPF;
import negocio.ValidadorDadosQuarto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fachada {

    private Map<String, IDAO> daos;
    private Map<String, List<IStrategy>> regrasNegocio;

    public Fachada() {
        daos = new HashMap<>();
        regrasNegocio = new HashMap<>();

        // --- Configuração HÓSPEDES ---
        daos.put(Hospede.class.getName(), new HospedeDAO());
        List<IStrategy> regrasHospede = new ArrayList<>();
        regrasHospede.add(new ValidadorCPF());
        regrasNegocio.put(Hospede.class.getName(), regrasHospede);

        // --- Configuração QUARTOS (NOVO) ---
        daos.put(Quarto.class.getName(), new QuartoDAO());
        List<IStrategy> regrasQuarto = new ArrayList<>();
        regrasQuarto.add(new ValidadorDadosQuarto());
        regrasNegocio.put(Quarto.class.getName(), regrasQuarto);
    }

    public void salvar(EntidadeDominio entidade) {
        String nmClasse = entidade.getClass().getName();
        
        List<IStrategy> regras = regrasNegocio.get(nmClasse);
        if (regras != null) {
            for (IStrategy s : regras) s.processar(entidade);
        }

        IDAO dao = daos.get(nmClasse);
        if (dao != null) {
            dao.salvar(entidade);
        } else {
            throw new RuntimeException("DAO não configurado para " + nmClasse);
        }
    }

    public List<EntidadeDominio> consultar(EntidadeDominio entidade) {
        IDAO dao = daos.get(entidade.getClass().getName());
        if (dao != null) return dao.consultar(entidade);
        return null;
    }
}