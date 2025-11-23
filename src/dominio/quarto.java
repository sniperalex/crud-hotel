package dominio;

public class Quarto extends EntidadeDominio {
    private int numero;
    private String tipo; // Ex: Standard, Luxo, Suíte
    private int capAdultos;
    private int capCriancas;
    private double precoBase;

    public Quarto() {}

    public Quarto(int numero, String tipo, int capAdultos, int capCriancas, double precoBase) {
        this.numero = numero;
        this.tipo = tipo;
        this.capAdultos = capAdultos;
        this.capCriancas = capCriancas;
        this.precoBase = precoBase;
    }

    // Getters e Setters
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getCapAdultos() { return capAdultos; }
    public void setCapAdultos(int capAdultos) { this.capAdultos = capAdultos; }

    public int getCapCriancas() { return capCriancas; }
    public void setCapCriancas(int capCriancas) { this.capCriancas = capCriancas; }

    public double getPrecoBase() { return precoBase; }
    public void setPrecoBase(double precoBase) { this.precoBase = precoBase; }
}