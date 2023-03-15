# desafioilia

## Como rodar o projeto:
Este projeto está separado em duas pastas principais:
a própria pasta raiz e a pasta database, a pasta database contém as infos
necessárias para iniciar o container do banco.

Primeiro: entre na pasta 'database' e execute:
```
docker build -t mysql .;
docker run --name mysql -g -p 3306:3306 mysql;
```

Depois, execute, na pasta raíz do projeto:
```
mvn clean install
```
Ao final da construção do projeto, execute:
```
./mvwn spring-boot:run
```
---
### Se houver erro na subida da aplicação
Dependendo da sua configuração do docker, pode ser que o IP da interface
virtual docker esteja errado no arquivo 'application.properties', entao deve-se realizar o seguinte:
executar no terminal:
```
docker network inspect bridge
```
E procurar na saída do comando:
```
"Gateway": "172.17.42.1"
```
pegue o IP exibido nessa config e substitua na application.properties
```
spring.datasource.url=jdbc:mysql://<ip_do_gateway>:3306/folhaDePonto 
```
Tente subir a aplicação novamente