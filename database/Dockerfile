# Usa a imagem oficial do MySQL
FROM mysql

# Define a senha do root do MySQL
ENV MYSQL_ROOT_PASSWORD=root

# Copia o script SQL para a pasta de inicialização do MySQL
COPY init.sql /docker-entrypoint-initdb.d/

# Define a porta que será exposta para acesso ao MySQL
EXPOSE 3306

# Inicia o MySQL automaticamente quando o container subir
CMD ["mysqld"]
