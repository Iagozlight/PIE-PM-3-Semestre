CREATE OR REPLACE FUNCTION verificar_placa()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.placa !~ '^[A-Z]{3}[0-9][A-Z][0-9]{2}$' THEN
        RAISE EXCEPTION 'A placa deve seguir o formato Mercosul (ex: ABC1D23)';
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_placa_veiculos
    BEFORE INSERT ON veiculos
    FOR EACH ROW EXECUTE FUNCTION verificar_placa();