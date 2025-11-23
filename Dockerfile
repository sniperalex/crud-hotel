FROM eclipse-temurin:17-jdk

WORKDIR /app

# Cria diretório para libs (se quiser adicionar jars em lib/ no futuro)
RUN mkdir -p lib
COPY src/ ./src/

# Compila todos os .java encontrados para a pasta out
RUN find src -name "*.java" > sources.txt \
 && javac -cp "lib/*" @sources.txt -d out

# Diretório para persistência (mapeie no Render como Persistent Disk)
VOLUME /data

# Caminho padrão do SQLite dentro do container
ENV SQLITE_PATH=/data/hotel_db.sqlite

# Porta usada pela aplicação
EXPOSE 8080

# Executa a aplicação
CMD ["java", "-cp", "out:lib/*", "main.Main"]
