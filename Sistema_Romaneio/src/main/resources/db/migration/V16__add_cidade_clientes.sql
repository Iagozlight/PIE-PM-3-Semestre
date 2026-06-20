ALTER TABLE clientes_romaneio
    ADD COLUMN IF NOT EXISTS cidade VARCHAR(80),
    ADD COLUMN IF NOT EXISTS cidades_atendidas VARCHAR(255);
