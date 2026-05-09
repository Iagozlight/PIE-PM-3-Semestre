INSERT INTO permissoes (nome)
SELECT 'ADMIN'
    WHERE NOT EXISTS (
    SELECT 1 FROM permissoes WHERE nome = 'ADMIN'
);

INSERT INTO usuarios (usuario, senha)
SELECT 'admin', 'admin123'
    WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE usuario = 'admin'
);

INSERT INTO usuarios_permissoes (usuario_id, permissao_id)
SELECT u.id, p.id
FROM usuarios u, permissoes p
WHERE u.usuario = 'admin'
  AND p.nome = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM usuarios_permissoes up
    WHERE up.usuario_id = u.id
      AND up.permissao_id = p.id
);