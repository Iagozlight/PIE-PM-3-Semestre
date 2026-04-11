alter table motorista
drop constraint fk_motorista_usuario;

alter table motorista
add constraint fk_motorista_usuario
foreign key (usuario_id) references usuario(id) on delete cascade;