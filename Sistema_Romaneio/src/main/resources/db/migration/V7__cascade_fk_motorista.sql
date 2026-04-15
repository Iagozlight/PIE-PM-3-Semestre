alter table motoristas
drop constraint fk_motorista_usuario;

alter table motoristas
add constraint fk_motorista_usuario
foreign key (usuario_id) references usuarios(id) on delete cascade;