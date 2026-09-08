# Etapa 1: build do jar
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# baixa as dependências antes de copiar o código -> melhor cache de camadas
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# Etapa 2: imagem final, só com o jar e os dados necessários
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Aplicação é um CLI (Scanner via stdin) que lê/grava CSVs em
# caminhos relativos: dados/ (entrada) e relatorios/ (saída).
COPY dados ./dados
COPY relatorios ./relatorios

# Sem EXPOSE: não é uma aplicação web, é interativa via terminal.
# Rodar com: docker run -it <imagem>
ENTRYPOINT ["java", "-jar", "app.jar"]
