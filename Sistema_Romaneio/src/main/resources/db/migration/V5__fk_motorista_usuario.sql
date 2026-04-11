alter table motorista
add constraint fk_motorista_usuario
foreign key (usuario_id) references usuario(id);