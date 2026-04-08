CREATE TABLE veiculos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(50) NOT NULL,
  placa VARCHAR(50) NOT NULL,
  disponibilidade BOOLEAN NOT NULL
);

CREATE TABLE motoristas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL
);

CREATE TABLE romaneios (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   data DATE NOT NULL,
   veiculo_id BIGINT,
   motorista_id BIGINT,
   FOREIGN KEY (veiculo_id) REFERENCES veiculos(id),
   FOREIGN KEY (motorista_id) REFERENCES motoristas(id)
);

CREATE TABLE clientes_romaneio (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   nome_cliente VARCHAR(50) NOT NULL,
   cpf VARCHAR(50) NOT NULL,
   cep VARCHAR(50) NOT NULL,
   rua VARCHAR(50) NOT NULL,
   numero VARCHAR(50) NOT NULL,
   bairro VARCHAR(50) NOT NULL,
   complemento VARCHAR(50),
   referencia VARCHAR(50),
   romaneios_id BIGINT NOT NULL,
   FOREIGN KEY (romaneios_id) REFERENCES romaneios(id)
);

CREATE TABLE pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_produto VARCHAR(50),
    valor DOUBLE,
    quantidade VARCHAR(50),
    clientesRomaneio_id BIGINT NOT NULL,
    FOREIGN KEY (clientesRomaneio_id) REFERENCES clientes_romaneio(id)
);