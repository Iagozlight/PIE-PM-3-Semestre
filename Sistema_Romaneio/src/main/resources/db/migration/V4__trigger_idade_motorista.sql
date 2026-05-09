create or replace function verificar_idade_motorista()
returns trigger as $$

begin

    if extract(year from age(new.data_nascimento)) < 24 then
        raise exception 'Motorista deve ter mais de 24 anos!';
end if;

return new;

end;

$$ language plpgsql;

create trigger trg_idade_motorista
    before insert on motoristas
    for each row execute function verificar_idade_motorista();