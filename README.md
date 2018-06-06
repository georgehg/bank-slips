# API REST para geração de boletos

Esta é uma API REST desenvolvida em Java 8 utilizando Spring Boot 2.0. Como ferramenta de Build foi utilizado o Maven.

A aplicação contempla os Endpoints abaixo:

● Criar boleto
● Listar boletos
● Ver detalhes
● Pagar um boleto
● Cancelar um boleto

	
## Instalação

Após baixar o código fonte da aplicação executar o comando abaixo para realizar o build da aplicação com o Maven e executar os testes unitários:

```js
mvn clean package
```


## Executando

Após compilar o código com sucesso, pode-se subir a aplicação utilizando o comando abaixo:

```js
mvn spring-boot:run
```

A API da aplicação pode ser acessada a partir do endereço de HOST e número de Porta abaixo:

```js
http://localhost:8080/rest/bankslips/
```