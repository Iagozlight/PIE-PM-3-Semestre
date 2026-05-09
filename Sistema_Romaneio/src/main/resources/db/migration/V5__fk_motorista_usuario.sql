ALTER TABLE motoristas
add constraint fk_motorista_usuario
foreign key (usuario_id) references usuarios(id);