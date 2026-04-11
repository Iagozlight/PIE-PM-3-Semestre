create table Motoristas (
    id serial not null,
    nome varchar(100),
    senha varchar(50),
    usuario_id bigint,
    primary key (id)
)