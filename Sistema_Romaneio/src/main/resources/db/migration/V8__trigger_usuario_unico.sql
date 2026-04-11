create or replace function verificar_usuario_unico()
       returns trigger as $$
       begin
       if exists (select 1 from usuario where usuario = new.usuario) then
          raise exception 'Usuario ja existente, escolha outro nome de usuario!';
          end if;
          return new;
end;
$$ language plpgsql;

   create  trigger trg_usuario_unico
before insert on usuario
    for each row execute function verificar_usuario_unico();