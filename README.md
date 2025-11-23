# CRUD HOTEL

Aplicação de exemplo para gestão de hóspedes e quartos (Java + HTTP server + SQLite).

Estrutura:
- `src/` - código-fonte Java e frontend (`index.html`)
- `lib/` - libs externas (se houver)

Executando localmente (sem Maven):
1. Compile os arquivos Java:

```powershell
cd 'C:\Users\maq\Desktop\CRUD HOTEL\src'
javac main\Main.java
```

2. Execute a aplicação:

```powershell
java main.Main
```

Acesse: `http://localhost:8080`

Observações:
- Se for publicar no GitHub, crie um repositório e depois rode os comandos de git abaixo para enviar o código.
- Se preferir eu posso criar um `pom.xml` e migrar para Maven/Gradle.

Como subir no GitHub (resumo):
1. Crie um novo repositório no GitHub (não inicialize com README nem .gitignore se quiser usar este local).
2. No terminal, dentro da pasta do projeto, rode:

```powershell
cd 'C:\Users\maq\Desktop\CRUD HOTEL'
git init
git add .
git commit -m "Initial commit"
# Substitua <URL-DO-REPO> pela URL do repositório remoto
git remote add origin <URL-DO-REPO>
git branch -M main
git push -u origin main
```

Se quiser, eu mesmo gero o `.github/workflows` para CI (build Java) depois.
