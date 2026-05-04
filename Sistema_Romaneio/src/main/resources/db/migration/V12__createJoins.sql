
SELECT
    r.id,
    r.data,
    v.nome AS veiculo,
    v.placa,
    m.nome AS motorista
FROM romaneios r
         INNER JOIN veiculos v ON r.veiculo_id = v.id
         INNER JOIN motoristas m ON r.motorista_id = m.id
ORDER BY r.data DESC;

SELECT
    r.id,
    r.data,
    v.nome AS veiculo,
    m.nome AS motorista,
    CASE
        WHEN r.veiculo_id IS NULL AND r.motorista_id IS NULL THEN 'Falta veículo e motorista'
        WHEN r.veiculo_id IS NULL THEN 'Falta veículo'
        WHEN r.motorista_id IS NULL THEN 'Falta motorista'
        END AS status
FROM romaneios r
         LEFT JOIN veiculos v ON r.veiculo_id = v.id
         LEFT JOIN motoristas m ON r.motorista_id = m.id
WHERE r.veiculo_id IS NULL OR r.motorista_id IS NULL
ORDER BY r.data DESC;



SELECT
    m.id,
    m.nome,
    m.data_nascimento,
    u.usuario
FROM motoristas m
         INNER JOIN usuarios u ON m.usuario_id = u.id
         LEFT JOIN romaneios r ON r.motorista_id = m.id
WHERE r.id IS NULL
ORDER BY m.nome;


SELECT
    r.id AS romaneio_id,
    r.data,
    c.nome_cliente,
    c.cpf,
    c.rua || ', ' || c.numero || ' - ' || c.bairro AS endereco,
    p.nome_produto,
    p.quantidade
FROM romaneios r
         INNER JOIN clientes_romaneio c ON c.romaneios_id = r.id
         LEFT JOIN pedidos p ON p.clientesromaneio_id = c.id
ORDER BY r.data DESC, c.nome_cliente;