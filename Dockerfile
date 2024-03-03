# Use a imagem base do OpenJDK 21
FROM openjdk

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo JAR da aplicação Quarkus para o contêiner
COPY . .

# Exponha a porta em que a aplicação Quarkus está sendo executada (ajuste conforme necessário)
EXPOSE 8080

# Comando para iniciar a aplicação quando o contêiner for iniciado
CMD ["java", "-jar", ".\target\quarkus-app\quarkus-run.jar"]