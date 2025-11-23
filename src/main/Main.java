package main;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import controle.Fachada;
import dao.ConexaoFactory;
import dominio.EntidadeDominio;
import dominio.Hospede;
import dominio.Quarto;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class Main {

    private static final Fachada fachada = new Fachada();

    public static void main(String[] args) throws IOException {
        // 1. Inicializa o Banco
        ConexaoFactory.inicializarBanco();

        // 2. Cria servidor HTTP na porta 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // ==================================================================
        // AQUI ESTÃO OS HANDLERS (ROTAS)
        // ==================================================================
        server.createContext("/", new StaticHandler());           // Serve o HTML
        server.createContext("/api/hospedes", new HospedesHandler()); // API Hóspedes
        server.createContext("/api/quartos", new QuartosHandler());   // <--- ESSA É A LINHA NOVA QUE REGISTRA O HANDLER
        // ==================================================================

        server.setExecutor(null);
        System.out.println("Servidor rodando em: http://localhost:8080");
        server.start();
    }

    // -------------------------------------------------------------------
    // HANDLER 1: Serve o arquivo HTML (Frontend)
    // -------------------------------------------------------------------
    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Durante desenvolvimento: tenta servir o arquivo diretamente da pasta src/
            java.nio.file.Path devPath = java.nio.file.Paths.get("src", "index.html");
            byte[] fileBytes = null;
            if (java.nio.file.Files.exists(devPath)) {
                try {
                    fileBytes = java.nio.file.Files.readAllBytes(devPath);
                } catch (IOException e) {
                    fileBytes = null; // fallback abaixo
                }
            }

            if (fileBytes == null) {
                InputStream is = getClass().getClassLoader().getResourceAsStream("index.html");
                if (is == null) {
                    String response = "Erro: index.html nao encontrado na pasta src/ ou resources.";
                    byte[] respBytes = response.getBytes(StandardCharsets.UTF_8);
                    t.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
                    t.sendResponseHeaders(404, respBytes.length);
                    try (OutputStream os = t.getResponseBody()) { os.write(respBytes); }
                    return;
                }
                fileBytes = is.readAllBytes();
            }

            t.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
            t.sendResponseHeaders(200, fileBytes.length);
            try (OutputStream os = t.getResponseBody()) { os.write(fileBytes); }
        }
    }

    // -------------------------------------------------------------------
    // HANDLER 2: API de Hóspedes (Já tínhamos feito)
    // -------------------------------------------------------------------
    static class HospedesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();

            if ("POST".equals(method)) {
                InputStream is = t.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                String[] dados = body.split("\\|"); // Espera: nome|cpf|email|telefone
                
                if(dados.length >= 4) {
                    Hospede h = new Hospede(dados[0], dados[1], dados[2], dados[3]);
                    try {
                        fachada.salvar(h);
                        enviarResposta(t, 200, "Hóspede salvo com sucesso!");
                    } catch (Exception e) {
                        enviarResposta(t, 400, "Erro: " + e.getMessage());
                    }
                } else {
                    enviarResposta(t, 400, "Dados inválidos.");
                }

            } else if ("GET".equals(method)) {
                List<EntidadeDominio> lista = fachada.consultar(new Hospede());
                StringBuilder json = new StringBuilder("[");
                
                for (int i = 0; i < lista.size(); i++) {
                    Hospede h = (Hospede) lista.get(i);
                    json.append(String.format("{\"nome\":\"%s\", \"cpf\":\"%s\", \"email\":\"%s\"}", 
                        h.getNome(), h.getCpf(), h.getEmail()));
                    if (i < lista.size() - 1) json.append(",");
                }
                json.append("]");
                enviarJson(t, json.toString());
            }
        }
    }

    // -------------------------------------------------------------------
    // HANDLER 3: API de Quartos (ESSA É A CLASSE NOVA QUE FALTAVA)
    // -------------------------------------------------------------------
    static class QuartosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String method = t.getRequestMethod();

            if ("POST".equals(method)) {
                // RECEBE DADOS DO FRONTEND PARA SALVAR
                InputStream is = t.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                
                // O front manda: numero|tipo|capAdulto|capCrianca|preco
                String[] dados = body.split("\\|");
                
                if(dados.length >= 5) {
                    try {
                        Quarto q = new Quarto();
                        q.setNumero(Integer.parseInt(dados[0]));
                        q.setTipo(dados[1]);
                        q.setCapAdultos(Integer.parseInt(dados[2]));
                        q.setCapCriancas(Integer.parseInt(dados[3]));
                        q.setPrecoBase(Double.parseDouble(dados[4]));
                        
                        fachada.salvar(q);
                        enviarResposta(t, 200, "Quarto salvo com sucesso!");
                    } catch (Exception e) {
                        enviarResposta(t, 400, "Erro ao salvar quarto: " + e.getMessage());
                    }
                } else {
                    enviarResposta(t, 400, "Formato de dados inválido.");
                }

            } else if ("GET".equals(method)) {
                // ENVIA A LISTA DE QUARTOS PARA O FRONTEND
                List<EntidadeDominio> lista = fachada.consultar(new Quarto());
                StringBuilder json = new StringBuilder("[");
                
                for (int i = 0; i < lista.size(); i++) {
                    Quarto q = (Quarto) lista.get(i);
                    // Usamos Locale.US para garantir que o preço venha com ponto (99.90) e não vírgula
                    json.append(String.format(Locale.US, 
                        "{\"numero\":%d, \"tipo\":\"%s\", \"adultos\":%d, \"criancas\":%d, \"preco\":%.2f}", 
                        q.getNumero(), q.getTipo(), q.getCapAdultos(), q.getCapCriancas(), q.getPrecoBase()));
                        
                    if (i < lista.size() - 1) json.append(",");
                }
                json.append("]");
                enviarJson(t, json.toString());
            }
        }
    }

    // --- MÉTODOS AUXILIARES PARA EVITAR REPETIÇÃO DE CÓDIGO ---
    
    private static void enviarResposta(HttpExchange t, int status, String resposta) throws IOException {
        t.sendResponseHeaders(status, resposta.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(resposta.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void enviarJson(HttpExchange t, String json) throws IOException {
        t.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        t.sendResponseHeaders(200, json.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }
}