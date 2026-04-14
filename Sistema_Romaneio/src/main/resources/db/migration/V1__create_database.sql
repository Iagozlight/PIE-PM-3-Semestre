CREATE TABLE permissoes (
                            id SERIAL PRIMARY KEY,
                            nome VARCHAR(50) UNIQUE NOT NULL
);




CREATE TABLE usuarios (
                          id SERIAL PRIMARY KEY,
                          usuario VARCHAR(50) NOT NULL,
                          senha VARCHAR(50) NOT NULL
);

CREATE TABLE usuarios_permissoes (
                                     usuario_id BIGINT,
                                     permissao_id BIGINT,
                                     PRIMARY KEY (usuario_id, permissao_id),
                                     FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
                                     FOREIGN KEY (permissao_id) REFERENCES permissoes(id) ON DELETE CASCADE
);

CREATE TABLE veiculos (
                          id SERIAL PRIMARY KEY,
                          nome VARCHAR(50) NOT NULL,
                          placa VARCHAR(50) NOT NULL,
                          disponibilidade BOOLEAN NOT NULL
);

CREATE TABLE motoristas (
                            id BIGSERIAL PRIMARY KEY,
                            nome VARCHAR(50) NOT NULL,
                            usuario_id BIGINT,
                            data_nascimento DATE NOT NULL DEFAULT '2000-01-01',
                            FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE romaneios (
                           id SERIAL PRIMARY KEY,
                           data DATE NOT NULL,
                           veiculo_id BIGINT,
                           motorista_id BIGINT,
                           FOREIGN KEY (veiculo_id) REFERENCES veiculos(id),
                           FOREIGN KEY (motorista_id) REFERENCES motoristas(id)
);

CREATE TABLE clientes_romaneio (
                                   id SERIAL PRIMARY KEY,
                                   nome_cliente VARCHAR(50) NOT NULL,
                                   cpf VARCHAR(50) NOT NULL,
                                   cep VARCHAR(50) NOT NULL,
                                   rua VARCHAR(50) NOT NULL,
                                   numero VARCHAR(50) NOT NULL,
                                   bairro VARCHAR(50) NOT NULL,
                                   complemento VARCHAR(50),
                                   referencia VARCHAR(50),
                                   romaneios_id BIGINT,
                                   FOREIGN KEY (romaneios_id) REFERENCES romaneios(id)
);

CREATE TABLE pedidos (
                         id SERIAL PRIMARY KEY,
                         nome_produto VARCHAR(50),
                         quantidade VARCHAR(50),
                         clientesRomaneio_id BIGINT NOT NULL,
                         FOREIGN KEY (clientesRomaneio_id) REFERENCES clientes_romaneio(id)
);


