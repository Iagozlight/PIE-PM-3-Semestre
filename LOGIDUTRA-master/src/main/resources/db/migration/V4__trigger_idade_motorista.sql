alter table motorista add column data_nascimento date; -- adicionei a colunda data de nascimento

create or replace function verificar_idade_motorista() -- o "or replace" vai criar a função, se a função ja existir ele substitui

       returns trigger as $$ -- esses "$$" sao delimitadores do corpo da função, define onde começa e onde termina

       begin --começa a logica

       if extract(year from age(new.data_nascimento)) <24 then --o extract serve pra extrair uma parte especifica da data, nesse caso o ano

          raise exception 'Motorista deve ter mais de 24 anos!!'; -- o raise exception lança um erro, cancela a operação e exibe a mensagem

          end if; --encerra o if

          return new; --retorna o registro novo e deixa a operação continuar

end; --termina a logica

$$ language plpgsql; -- essa é a linguagem que o banco usa pra interpretar o codigo, tipo um Java pro Bd

   create trigger trg_idade_motorista --quando ter um update, delete ou insert na tabela, vai criar a trigger e chama automaticamente

before insert on motorista --o "before" vai executa a trigger antes de inserir na tabela

for each row execute function verificar_idade_motorista(); --toda linha inserida vai executar a funçao

