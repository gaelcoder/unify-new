-- =====================================================
-- POPULAÇÃO DE DADOS - SISTEMA UNIFY (SQLITE COMPATIBLE)
-- =====================================================

-- =====================================================
-- UNIVERSIDADES
-- =====================================================
INSERT INTO universidades (nome, cnpj, data_fundacao, sigla) VALUES
                                                                 ('Universidade Federal do Rio de Janeiro', '33.663.683/0001-16', '1920-09-07 00:00:00.000', 'UFRJ'),
                                                                 ('Universidade de São Paulo','63.025.530/0001-04','1934-01-25 00:00:00.000', 'USP'),
                                                                 ('Universidade Estadual de Campinas', '46.068.425/0001-33','1966-10-05 00:00:00.000','UNICAMP'),
                                                                 ('Universidade do Estado do Rio de Janeiro', '33.540.014/0001-57','1950-12-04 00:00:00.000', 'UERJ'),
                                                                 ('Universidade Federal Fluminense', '28.523.215/0001-06','1912-06-03 00:00:00.000','UFF'),
                                                                 ('Universidade Estadual Paulista', '48.031.918/0001-24','1976-01-30 00:00:00.000','UNESP');

-- =====================================================
-- CAMPUS DAS UNIVERSIDADES
-- =====================================================
INSERT INTO universidade_campus (universidade_id, campus_nome) VALUES
-- UFRJ
(1, 'Cidade Universitária'),
(1, 'Praia Vermelha'),
(1, 'Ilha do Fundão'),
-- USP
(2, 'Cidade Universitária'),
(2, 'Ribeirão Preto'),
(2, 'São Carlos'),
(2, 'Piracicaba'),
-- UNICAMP
(3, 'Cidade Universitária'),
(3, 'Limeira'),
(3, 'Piracicaba'),
-- UERJ
(4, 'Maracanã'),
(4, 'São Gonçalo'),
(4, 'Nova Friburgo'),
-- UFF
(5, 'Campus do Gragoatá'),
(5, 'Campus do Valonguinho'),
(5, 'Campus de Volta Redonda'),
-- UNESP
(6, 'Campus de Araraquara'),
(6, 'Campus de Bauru'),
(6, 'Campus de São José do Rio Preto');

-- =====================================================
-- GRADUAÇÕES (40 graduações distribuídas entre as 6 universidades)
-- =====================================================
INSERT INTO graduacoes (titulo, universidade_id, semestres, codigo_curso) VALUES
-- UFRJ (7 graduações)
('Engenharia Civil', 1, 10, 'EC001'),
('Engenharia Elétrica', 1, 10, 'EE002'),
('Engenharia Mecânica', 1, 10, 'EM003'),
('Medicina', 1, 12, 'MD004'),
('Direito', 1, 10, 'DR005'),
('Administração', 1, 8, 'AD006'),
('Ciência da Computação', 1, 8, 'CC007'),
-- USP (8 graduações)
('Engenharia Civil', 2, 10, 'EC008'),
('Engenharia Elétrica', 2, 10, 'EE009'),
('Medicina', 2, 12, 'MD010'),
('Direito', 2, 10, 'DR011'),
('Administração', 2, 8, 'AD012'),
('Ciência da Computação', 2, 8, 'CC013'),
('Arquitetura e Urbanismo', 2, 10, 'AU014'),
('Psicologia', 2, 10, 'PS015'),
-- UNICAMP (7 graduações)
('Engenharia Civil', 3, 10, 'EC016'),
('Engenharia Elétrica', 3, 10, 'EE017'),
('Medicina', 3, 12, 'MD018'),
('Direito', 3, 10, 'DR019'),
('Administração', 3, 8, 'AD020'),
('Ciência da Computação', 3, 8, 'CC021'),
('Engenharia Química', 3, 10, 'EQ022'),
-- UERJ (6 graduações)
('Engenharia Civil', 4, 10, 'EC023'),
('Medicina', 4, 12, 'MD024'),
('Direito', 4, 10, 'DR025'),
('Administração', 4, 8, 'AD026'),
('Ciência da Computação', 4, 8, 'CC027'),
('Psicologia', 4, 10, 'PS028'),
-- UFF (6 graduações)
('Engenharia Civil', 5, 10, 'EC029'),
('Engenharia Elétrica', 5, 10, 'EE030'),
('Medicina', 5, 12, 'MD031'),
('Direito', 5, 10, 'DR032'),
('Administração', 5, 8, 'AD033'),
('Ciência da Computação', 5, 8, 'CC034'),
-- UNESP (6 graduações)
('Engenharia Civil', 6, 10, 'EC035'),
('Engenharia Elétrica', 6, 10, 'EE036'),
('Medicina', 6, 12, 'MD037'),
('Direito', 6, 10, 'DR038'),
('Administração', 6, 8, 'AD039'),
('Ciência da Computação', 6, 8, 'CC040');

-- =====================================================
-- GRADUAÇÃO CAMPUS DISPONÍVEIS
-- =====================================================
INSERT INTO graduacao_campus_disponiveis (graduacao_id, campus) VALUES
-- UFRJ - Cidade Universitária
(1, 'Cidade Universitária'),
(2, 'Cidade Universitária'),
(3, 'Cidade Universitária'),
(4, 'Cidade Universitária'),
(5, 'Cidade Universitária'),
(6, 'Cidade Universitária'),
(7, 'Cidade Universitária'),
-- UFRJ - Praia Vermelha
(4, 'Praia Vermelha'),
(5, 'Praia Vermelha'),
-- UFRJ - Ilha do Fundão
(1, 'Ilha do Fundão'),
(2, 'Ilha do Fundão'),
(3, 'Ilha do Fundão'),
-- USP - Cidade Universitária
(8, 'Cidade Universitária'),
(9, 'Cidade Universitária'),
(10, 'Cidade Universitária'),
(11, 'Cidade Universitária'),
(12, 'Cidade Universitária'),
(13, 'Cidade Universitária'),
(14, 'Cidade Universitária'),
(15, 'Cidade Universitária'),
-- USP - Ribeirão Preto
(10, 'Ribeirão Preto'),
(11, 'Ribeirão Preto'),
(12, 'Ribeirão Preto'),
-- USP - São Carlos
(8, 'São Carlos'),
(9, 'São Carlos'),
(13, 'São Carlos'),
-- USP - Piracicaba
(14, 'Piracicaba'),
(15, 'Piracicaba'),
-- UNICAMP - Cidade Universitária
(16, 'Cidade Universitária'),
(17, 'Cidade Universitária'),
(18, 'Cidade Universitária'),
(19, 'Cidade Universitária'),
(20, 'Cidade Universitária'),
(21, 'Cidade Universitária'),
(22, 'Cidade Universitária'),
-- UNICAMP - Limeira
(19, 'Limeira'),
(20, 'Limeira'),
(21, 'Limeira'),
-- UNICAMP - Piracicaba
(22, 'Piracicaba'),
-- UERJ - Maracanã
(23, 'Maracanã'),
(24, 'Maracanã'),
(25, 'Maracanã'),
(26, 'Maracanã'),
(27, 'Maracanã'),
(28, 'Maracanã'),
-- UERJ - São Gonçalo
(23, 'São Gonçalo'),
(26, 'São Gonçalo'),
(27, 'São Gonçalo'),
-- UERJ - Nova Friburgo
(24, 'Nova Friburgo'),
(25, 'Nova Friburgo'),
-- UFF - Campus do Gragoatá
(29, 'Campus do Gragoatá'),
(30, 'Campus do Gragoatá'),
(31, 'Campus do Gragoatá'),
(32, 'Campus do Gragoatá'),
(33, 'Campus do Gragoatá'),
(34, 'Campus do Gragoatá'),
-- UFF - Campus do Valonguinho
(29, 'Campus do Valonguinho'),
(30, 'Campus do Valonguinho'),
(32, 'Campus do Valonguinho'),
(33, 'Campus do Valonguinho'),
-- UFF - Campus de Volta Redonda
(31, 'Campus de Volta Redonda'),
(34, 'Campus de Volta Redonda'),
-- UNESP - Campus de Araraquara
(35, 'Campus de Araraquara'),
(36, 'Campus de Araraquara'),
(37, 'Campus de Araraquara'),
(38, 'Campus de Araraquara'),
(39, 'Campus de Araraquara'),
(40, 'Campus de Araraquara'),
-- UNESP - Campus de Bauru
(35, 'Campus de Bauru'),
(36, 'Campus de Bauru'),
(37, 'Campus de Bauru'),
-- UNESP - Campus de São José do Rio Preto
(38, 'Campus de São José do Rio Preto'),
(39, 'Campus de São José do Rio Preto'),
(40, 'Campus de São José do Rio Preto');


-- =====================================================
-- REPRESENTANTES ADICIONAIS
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Roberto Silva', 'roberto.silva@adm.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Cristina Santos', 'cristina.santos@adm.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Andre Oliveira', 'andre.oliveira@adm.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Lucia Costa', 'lucia.costa@adm.unify.usp.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Fernando Lima', 'fernando.lima@adm.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Monica Ferreira', 'monica.ferreira@adm.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (2, 2), (3, 2), (4, 2), (5, 2), (6, 2), (7, 2);

INSERT INTO representantes (cpf, data_nascimento, nome, sobrenome, email, telefone, usuario_id, cargo) VALUES
                                                                                                           ('88888888801', '1975-03-15 00:00:00.000', 'Roberto', 'Silva', 'roberto.silva@unify.edu.br', '21987654535', 2, 'Professor(a)'),
                                                                                                           ('88888888802', '1978-06-20 00:00:00.000', 'Cristina', 'Santos', 'cristina.santos@unify.edu.br', '11987654536', 3, 'Professor(a)'),
                                                                                                           ('88888888803', '1980-09-10 00:00:00.000', 'Andre', 'Oliveira', 'andre.oliveira@unify.edu.br', '19987654537', 4, 'Professor(a)'),
                                                                                                           ('88888888804', '1977-12-25 00:00:00.000', 'Lucia', 'Costa', 'lucia.costa@unify.edu.br', '21987654538', 5, 'Professor(a)'),
                                                                                                           ('88888888805', '1982-04-18 00:00:00.000', 'Fernando', 'Lima', 'fernando.lima@unify.edu.br', '21987654539', 6, 'Professor(a)'),
                                                                                                           ('88888888806', '1979-07-30 00:00:00.000', 'Monica', 'Ferreira', 'monica.ferreira@unify.edu.br', '11987654540', 7, 'Professor(a)');


-- =====================================================
-- PROFESSORES UFRJ (ROLE_PROFESSOR - perfil_id = 5)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Fernando Silva', 'fernando.silva@prof.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Patricia Santos', 'patricia.santos@prof.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Ricardo Oliveira', 'ricardo.oliveira@prof.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Carla Costa', 'carla.costa@prof.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Marcelo Lima', 'marcelo.lima@prof.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Juliana Ferreira', 'juliana.ferreira@prof.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Andre Almeida', 'andre.almeida@prof.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Monica Rocha', 'monica.rocha@prof.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Paulo Pereira', 'paulo.pereira@prof.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Lucia Carvalho', 'lucia.carvalho@prof.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (8, 5), (9, 5), (10, 5), (11, 5), (12, 5), (13, 5), (14, 5), (15, 5), (16, 5), (17, 5);

INSERT INTO professores (cpf, data_nascimento, nome, sobrenome, email, telefone, titulacao, universidade_id, usuario_id, salario) VALUES
                                                                                                                                      ('77777777771', '1975-03-15 00:00:00.000', 'Fernando', 'Silva', 'fernando.silva@ufrj.edu.br', '21987654327', 'Doutor', 1, 8, 14576.5),
                                                                                                                                      ('77777777772', '1980-05-20 00:00:00.000', 'Patricia', 'Santos', 'patricia.santos@ufrj.edu.br', '21987654328', 'Mestre', 1, 9, 14576.5),
                                                                                                                                      ('77777777773', '1978-07-10 00:00:00.000', 'Ricardo', 'Oliveira', 'ricardo.oliveira@ufrj.edu.br', '21987654329', 'Doutor', 1, 10, 14576.5),
                                                                                                                                      ('77777777774', '1982-09-25 00:00:00.000', 'Carla', 'Costa', 'carla.costa@ufrj.edu.br', '21987654330', 'Mestre', 1, 11, 14576.5),
                                                                                                                                      ('77777777775', '1976-11-30 00:00:00.000', 'Marcelo', 'Lima', 'marcelo.lima@ufrj.edu.br', '21987654331', 'Doutor', 1, 12, 14576.5),
                                                                                                                                      ('77777777776', '1981-01-05 00:00:00.000', 'Juliana', 'Ferreira', 'juliana.ferreira@ufrj.edu.br', '21987654332', 'Mestre', 1, 13, 14576.5),
                                                                                                                                      ('77777777777', '1979-03-15 00:00:00.000', 'Andre', 'Almeida', 'andre.almeida@ufrj.edu.br', '21987654333', 'Doutor', 1, 14, 14576.5),
                                                                                                                                      ('77777777778', '1983-05-28 00:00:00.000', 'Monica', 'Rocha', 'monica.rocha@ufrj.edu.br', '21987654334', 'Mestre', 1, 15, 14576.5),
                                                                                                                                      ('77777777779', '1977-07-12 00:00:00.000', 'Paulo', 'Pereira', 'paulo.pereira@ufrj.edu.br', '21987654335', 'Doutor', 1, 16, 14576.5),
                                                                                                                                      ('77777777780', '1984-09-18 00:00:00.000', 'Lucia', 'Carvalho', 'lucia.carvalho@ufrj.edu.br', '21987654336', 'Mestre', 1, 17, 14576.5);

-- =====================================================
-- PROFESSORES USP (ROLE_PROFESSOR - perfil_id = 5)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Roberto Silva', 'roberto.silva@prof.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Ana Santos', 'ana.santos@prof.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Carlos Oliveira', 'carlos.oliveira@prof.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Fernanda Costa', 'fernanda.costa@prof.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Joao Lima', 'joao.lima@prof.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Maria Ferreira', 'maria.ferreira@prof.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Pedro Almeida', 'pedro.almeida@prof.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Sonia Rocha', 'sonia.rocha@prof.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Antonio Pereira', 'antonio.pereira@prof.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Teresa Carvalho', 'teresa.carvalho@prof.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (18, 5), (19, 5), (20, 5), (21, 5), (22, 5), (23, 5), (24, 5), (25, 5), (26, 5), (27, 5);

INSERT INTO professores (cpf, data_nascimento, nome, sobrenome, email, telefone, titulacao, universidade_id, usuario_id, salario) VALUES
                                                                                                                                      ('88888888881', '1974-04-12 00:00:00.000', 'Roberto', 'Silva', 'roberto.silva@usp.edu.br', '11987654337', 'Doutor', 2, 18, 13576.5),
                                                                                                                                      ('88888888882', '1981-06-18 00:00:00.000', 'Ana', 'Santos', 'ana.santos@usp.edu.br', '11987654338', 'Mestre', 2, 19, 13576.5),
                                                                                                                                      ('88888888883', '1979-08-05 00:00:00.000', 'Carlos', 'Oliveira', 'carlos.oliveira@usp.edu.br', '11987654339', 'Doutor', 2, 20, 13576.5),
                                                                                                                                      ('88888888884', '1983-10-20 00:00:00.000', 'Fernanda', 'Costa', 'fernanda.costa@usp.edu.br', '11987654340', 'Mestre', 2, 21, 13576.5),
                                                                                                                                      ('88888888885', '1977-12-15 00:00:00.000', 'Joao', 'Lima', 'joao.lima@usp.edu.br', '11987654341', 'Doutor', 2, 22, 13576.5),
                                                                                                                                      ('88888888886', '1982-02-08 00:00:00.000', 'Maria', 'Ferreira', 'maria.ferreira@usp.edu.br', '11987654342', 'Mestre', 2, 23, 13576.5),
                                                                                                                                      ('88888888887', '1976-04-22 00:00:00.000', 'Pedro', 'Almeida', 'pedro.almeida@usp.edu.br', '11987654343', 'Doutor', 2, 24, 13576.5),
                                                                                                                                      ('88888888888', '1984-06-30 00:00:00.000', 'Sonia', 'Rocha', 'sonia.rocha@usp.edu.br', '11987654344', 'Mestre', 2, 25, 13576.5),
                                                                                                                                      ('88888888889', '1978-08-14 00:00:00.000', 'Antonio', 'Pereira', 'antonio.pereira@usp.edu.br', '11987654345', 'Doutor', 2, 26, 13576.5),
                                                                                                                                      ('88888888890', '1985-10-26 00:00:00.000', 'Teresa', 'Carvalho', 'teresa.carvalho@usp.edu.br', '11987654346', 'Mestre', 2, 27, 13576.5);

-- =====================================================
-- PROFESSORES UNICAMP (ROLE_PROFESSOR - perfil_id = 5)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Lucas Silva', 'lucas.silva@prof.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Amanda Santos', 'amanda.santos@prof.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Thiago Oliveira', 'thiago.oliveira@prof.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Juliana Costa', 'juliana.costa@prof.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Marcelo Lima', 'marcelo.lima@prof.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Vanessa Ferreira', 'vanessa.ferreira@prof.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Andre Almeida', 'andre.almeida@prof.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Camila Rocha', 'camila.rocha@prof.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Rafael Pereira', 'rafael.pereira@prof.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Isabela Carvalho', 'isabela.carvalho@prof.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (28, 5), (29, 5), (30, 5), (31, 5), (32, 5), (33, 5), (34, 5), (35, 5), (36, 5), (37, 5);

INSERT INTO professores (cpf, data_nascimento, nome, sobrenome, email, telefone, titulacao, universidade_id, usuario_id, salario) VALUES
                                                                                                                                      ('99999999991', '1973-05-10 00:00:00.000', 'Lucas', 'Silva', 'lucas.silva@unicamp.edu.br', '19987654347', 'Doutor', 3, 28, 17324.5),
                                                                                                                                      ('99999999992', '1980-07-15 00:00:00.000', 'Amanda', 'Santos', 'amanda.santos@unicamp.edu.br', '19987654348', 'Mestre', 3, 29, 17324.5),
                                                                                                                                      ('99999999993', '1978-09-08 00:00:00.000', 'Thiago', 'Oliveira', 'thiago.oliveira@unicamp.edu.br', '19987654349', 'Doutor', 3, 30, 17324.5),
                                                                                                                                      ('99999999994', '1982-11-22 00:00:00.000', 'Juliana', 'Costa', 'juliana.costa@unicamp.edu.br', '19987654350', 'Mestre', 3, 31, 17324.5),
                                                                                                                                      ('99999999995', '1976-01-30 00:00:00.000', 'Marcelo', 'Lima', 'marcelo.lima@unicamp.edu.br', '19987654351', 'Doutor', 3, 32, 17324.5),
                                                                                                                                      ('99999999996', '1981-03-12 00:00:00.000', 'Vanessa', 'Ferreira', 'vanessa.ferreira@unicamp.edu.br', '19987654352', 'Mestre', 3, 33, 17324.5),
                                                                                                                                      ('99999999997', '1979-05-25 00:00:00.000', 'Andre', 'Almeida', 'andre.almeida@unicamp.edu.br', '19987654353', 'Doutor', 3, 34, 17324.5),
                                                                                                                                      ('99999999998', '1983-07-08 00:00:00.000', 'Camila', 'Rocha', 'camila.rocha@unicamp.edu.br', '19987654354', 'Mestre', 3, 35, 17324.5),
                                                                                                                                      ('99999999999', '1977-09-20 00:00:00.000', 'Rafael', 'Pereira', 'rafael.pereira@unicamp.edu.br', '19987654355', 'Doutor', 3, 36, 17324.5),
                                                                                                                                      ('99999999900', '1984-11-05 00:00:00.000', 'Isabela', 'Carvalho', 'isabela.carvalho@unicamp.edu.br', '19987654356', 'Mestre', 3, 37, 17324.5);

-- =====================================================
-- PROFESSORES UERJ (ROLE_PROFESSOR - perfil_id = 5)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Bruno Silva', 'bruno.silva@prof.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Gabriela Santos', 'gabriela.santos@prof.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Diego Oliveira', 'diego.oliveira@prof.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Leticia Costa', 'leticia.costa@prof.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Paulo Lima', 'paulo.lima@prof.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Ana Ferreira', 'ana.ferreira@prof.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Carlos Almeida', 'carlos.almeida@prof.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Monica Rocha', 'monica.rocha@prof.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Joao Pereira', 'joao.pereira@prof.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Patricia Carvalho', 'patricia.carvalho@prof.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (38, 5), (39, 5), (40, 5), (41, 5), (42, 5), (43, 5), (44, 5), (45, 5), (46, 5);

INSERT INTO professores (cpf, data_nascimento, nome, sobrenome, email, telefone, titulacao, universidade_id, usuario_id, salario) VALUES
                                                                                                                                      ('00000000001', '1975-12-21 00:00:00.000', 'Bruno', 'Silva', 'bruno.silva@uerj.edu.br', '21987654357', 'Doutor', 4, 38, 17324.5),
                                                                                                                                      ('00000000002', '1981-02-16 00:00:00.000', 'Gabriela', 'Santos', 'gabriela.santos@uerj.edu.br', '21987654358', 'Mestre', 4, 39, 17324.5),
                                                                                                                                      ('00000000003', '1979-04-03 00:00:00.000', 'Diego', 'Oliveira', 'diego.oliveira@uerj.edu.br', '21987654359', 'Doutor', 4, 40, 17324.5),
                                                                                                                                      ('00000000004', '1983-06-30 00:00:00.000', 'Leticia', 'Costa', 'leticia.costa@uerj.edu.br', '21987654360', 'Mestre', 4, 41, 17324.5),
                                                                                                                                      ('00000000005', '1977-08-14 00:00:00.000', 'Paulo', 'Lima', 'paulo.lima@uerj.edu.br', '21987654361', 'Doutor', 4, 42, 17324.5),
                                                                                                                                      ('00000000006', '1982-10-26 00:00:00.000', 'Ana', 'Ferreira', 'ana.ferreira@uerj.edu.br', '21987654362', 'Mestre', 4, 43, 17324.5),
                                                                                                                                      ('00000000007', '1976-12-03 00:00:00.000', 'Carlos', 'Almeida', 'carlos.almeida@uerj.edu.br', '21987654363', 'Doutor', 4, 44, 17324.5),
                                                                                                                                      ('00000000008', '1984-02-16 00:00:00.000', 'Monica', 'Rocha', 'monica.rocha@uerj.edu.br', '21987654364', 'Mestre', 4, 45, 17324.5),
                                                                                                                                      ('00000000009', '1978-04-28 00:00:00.000', 'Joao', 'Pereira', 'joao.pereira@uerj.edu.br', '21987654365', 'Doutor', 4, 46, 17324.5),
                                                                                                                                      ('00000000010', '1985-06-10 00:00:00.000', 'Patricia', 'Carvalho', 'patricia.carvalho@uerj.edu.br', '21987654366', 'Mestre', 4, 47, 17324.5);

-- =====================================================
-- PROFESSORES UFF (ROLE_PROFESSOR - perfil_id = 5)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Joao Souza', 'joao.souza@prof.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Mariana Lima', 'mariana.lima@prof.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Felipe Almeida', 'felipe.almeida@prof.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Camila Rocha', 'camila.rocha@prof.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Rafael Pereira', 'rafael.pereira@prof.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Isabela Carvalho', 'isabela.carvalho@prof.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Bruno Martins', 'bruno.martins@prof.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Patricia Souza', 'patricia.souza@prof.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Gabriel Lima', 'gabriel.lima@prof.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Larissa Almeida', 'larissa.almeida@prof.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (49, 5), (50, 5), (51, 5), (52, 5), (53, 5), (54, 5), (55, 5), (56, 5), (57, 5);

INSERT INTO professores (cpf, data_nascimento, nome, sobrenome, email, telefone, titulacao, universidade_id, usuario_id, salario) VALUES
                                                                                                                                      ('11111111112', '1976-03-15 00:00:00.000', 'Joao', 'Souza', 'joao.souza@uff.edu.br', '21987654367', 'Doutor', 5, 48, 17324.5),
                                                                                                                                      ('11111111113', '1981-05-20 00:00:00.000', 'Mariana', 'Lima', 'mariana.lima@uff.edu.br', '21987654368', 'Mestre', 5, 49, 17324.5),
                                                                                                                                      ('11111111114', '1979-07-10 00:00:00.000', 'Felipe', 'Almeida', 'felipe.almeida@uff.edu.br', '21987654369', 'Doutor', 5, 50, 17324.5),
                                                                                                                                      ('11111111115', '1983-09-25 00:00:00.000', 'Camila', 'Rocha', 'camila.rocha@uff.edu.br', '21987654370', 'Mestre', 5, 51, 17324.5),
                                                                                                                                      ('11111111116', '1977-11-30 00:00:00.000', 'Rafael', 'Pereira', 'rafael.pereira@uff.edu.br', '21987654371', 'Doutor', 5, 52, 17324.5),
                                                                                                                                      ('11111111117', '1982-01-05 00:00:00.000', 'Isabela', 'Carvalho', 'isabela.carvalho@uff.edu.br', '21987654372', 'Mestre', 5, 53, 17324.5),
                                                                                                                                      ('11111111118', '1979-03-15 00:00:00.000', 'Bruno', 'Martins', 'bruno.martins@uff.edu.br', '21987654373', 'Doutor', 5, 54, 17324.5),
                                                                                                                                      ('11111111119', '1983-05-28 00:00:00.000', 'Patricia', 'Souza', 'patricia.souza@uff.edu.br', '21987654374', 'Mestre', 5, 55, 17324.5),
                                                                                                                                      ('11111111120', '1977-07-12 00:00:00.000', 'Gabriel', 'Lima', 'gabriel.lima@uff.edu.br', '21987654375', 'Doutor', 5, 56, 17324.5),
                                                                                                                                      ('11111111121', '1984-09-18 00:00:00.000', 'Larissa', 'Almeida', 'larissa.almeida@uff.edu.br', '21987654376', 'Mestre', 5, 57, 17324.5);

-- =====================================================
-- PROFESSORES UNESP (ROLE_PROFESSOR - perfil_id = 5)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Marcos Silva', 'marcos.silva@prof.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Renata Santos', 'renata.santos@prof.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Eduardo Oliveira', 'eduardo.oliveira@prof.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Luciana Costa', 'luciana.costa@prof.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Bruno Lima', 'bruno.lima@prof.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Patricia Ferreira', 'patricia.ferreira@prof.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Carlos Almeida', 'carlos.almeida@prof.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Fernanda Rocha', 'fernanda.rocha@prof.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Joao Pereira', 'joao.pereira@prof.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Larissa Carvalho', 'larissa.carvalho@prof.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (58, 5), (59, 5), (60, 5), (61, 5), (62, 5), (63, 5), (64, 5), (65, 5), (66, 5), (67, 5);

INSERT INTO professores (cpf, data_nascimento, nome, sobrenome, email, telefone, titulacao, universidade_id, usuario_id, salario) VALUES
                                                                                                                                      ('22222222212', '1976-03-15 00:00:00.000', 'Marcos', 'Silva', 'marcos.silva@unesp.edu.br', '11987654377', 'Doutor', 6, 58, 17324.5),
                                                                                                                                      ('22222222213', '1981-05-20 00:00:00.000', 'Renata', 'Santos', 'renata.santos@unesp.edu.br', '11987654378', 'Mestre', 6, 59, 17324.5),
                                                                                                                                      ('22222222214', '1979-07-10 00:00:00.000', 'Eduardo', 'Oliveira', 'eduardo.oliveira@unesp.edu.br', '11987654379', 'Doutor', 6, 60, 17324.5),
                                                                                                                                      ('22222222215', '1983-09-25 00:00:00.000', 'Luciana', 'Costa', 'luciana.costa@unesp.edu.br', '11987654380', 'Mestre', 6, 61, 17324.5),
                                                                                                                                      ('22222222216', '1977-11-30 00:00:00.000', 'Bruno', 'Lima', 'bruno.lima@unesp.edu.br', '11987654381', 'Doutor', 6, 62, 17324.5),
                                                                                                                                      ('22222222217', '1982-01-05 00:00:00.000', 'Patricia', 'Ferreira', 'patricia.ferreira@unesp.edu.br', '11987654382', 'Mestre', 6, 63, 17324.5),
                                                                                                                                      ('22222222218', '1979-03-15 00:00:00.000', 'Carlos', 'Almeida', 'carlos.almeida@unesp.edu.br', '11987654383', 'Doutor', 6, 64, 17324.5),
                                                                                                                                      ('22222222219', '1983-05-28 00:00:00.000', 'Fernanda', 'Rocha', 'fernanda.rocha@unesp.edu.br', '11987654384', 'Mestre', 6, 65, 17324.5),
                                                                                                                                      ('22222222220', '1977-07-12 00:00:00.000', 'Joao', 'Pereira', 'joao.pereira@unesp.edu.br', '11987654385', 'Doutor', 6, 66, 17324.5),
                                                                                                                                      ('22222222221', '1984-09-18 00:00:00.000', 'Larissa', 'Carvalho', 'larissa.carvalho@unesp.edu.br', '11987654386', 'Mestre', 6, 67, 17324.5);

-- =====================================================
-- ALUNOS UFRJ (ROLE_ALUNO - perfil_id = 6)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Ana Silva', 'ana.silva@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Joao Santos', 'joao.santos@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Maria Oliveira', 'maria.oliveira@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Pedro Costa', 'pedro.costa@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Carla Lima', 'carla.lima@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Lucas Ferreira', 'lucas.ferreira@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Beatriz Almeida', 'beatriz.almeida@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Gabriel Rocha', 'gabriel.rocha@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Julia Pereira', 'julia.pereira@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Rafael Carvalho', 'rafael.carvalho@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Isabela Gomes', 'isabela.gomes@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Matheus Martins', 'matheus.martins@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Sofia Souza', 'sofia.souza@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Thiago Barbosa', 'thiago.barbosa@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Larissa Cardoso', 'larissa.cardoso@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Bruno Cavalcanti', 'bruno.cavalcanti@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Camila Dias', 'camila.dias@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Diego Freitas', 'diego.freitas@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Amanda Henrique', 'amanda.henrique@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Felipe Jesus', 'felipe.jesus@al.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (68, 6), (69, 6), (70, 6), (71, 6), (72, 6), (73, 6), (74, 6), (75, 6), (76, 6),
                                                       (77, 6), (78, 6), (79, 6), (80, 6), (81, 6), (82, 6), (83, 6), (84, 6), (85, 6), (86, 6), (87,6);

INSERT INTO aluno (cpf, data_nascimento, nome, sobrenome, matricula, email, telefone, universidade_id, graduacao_id, campus, usuario_id) VALUES
                                                                                                                                             ('11111111121', '2000-03-15 00:00:00.000', 'Ana', 'Silva', '2024001', 'ana.silva@ufrj.edu.br', '21987654367', 1, 1, 'Cidade Universitária', 68),
                                                                                                                                             ('11111111122', '2001-05-20 00:00:00.000', 'Joao', 'Santos', '2024002', 'joao.santos@ufrj.edu.br', '21987654368', 1, 2, 'Cidade Universitária', 69),
                                                                                                                                             ('11111111123', '2002-07-10 00:00:00.000', 'Maria', 'Oliveira', '2024003', 'maria.oliveira@ufrj.edu.br', '21987654369', 1, 3, 'Cidade Universitária', 70),
                                                                                                                                             ('11111111124', '2000-09-25 00:00:00.000', 'Pedro', 'Costa', '2024004', 'pedro.costa@ufrj.edu.br', '21987654370', 1, 4, 'Cidade Universitária', 71),
                                                                                                                                             ('11111111125', '2001-11-30 00:00:00.000', 'Carla', 'Lima', '2024005', 'carla.lima@ufrj.edu.br', '21987654371', 1, 5, 'Cidade Universitária', 72),
                                                                                                                                             ('11111111126', '2002-01-05 00:00:00.000', 'Lucas', 'Ferreira', '2024006', 'lucas.ferreira@ufrj.edu.br', '21987654372', 1, 6, 'Cidade Universitária', 73),
                                                                                                                                             ('11111111127', '2000-03-15 00:00:00.000', 'Beatriz', 'Almeida', '2024007', 'beatriz.almeida@ufrj.edu.br', '21987654373', 1, 7, 'Cidade Universitária', 74),
                                                                                                                                             ('11111111128', '2001-05-28 00:00:00.000', 'Gabriel', 'Rocha', '2024008', 'gabriel.rocha@ufrj.edu.br', '21987654374', 1, 1, 'Ilha do Fundão', 75),
                                                                                                                                             ('11111111129', '2002-07-12 00:00:00.000', 'Julia', 'Pereira', '2024009', 'julia.pereira@ufrj.edu.br', '21987654375', 1, 2, 'Ilha do Fundão', 76),
                                                                                                                                             ('11111111130', '2000-09-18 00:00:00.000', 'Rafael', 'Carvalho', '2024010', 'rafael.carvalho@ufrj.edu.br', '21987654376', 1, 3, 'Ilha do Fundão', 77),
                                                                                                                                             ('11111111131', '2001-11-22 00:00:00.000', 'Isabela', 'Gomes', '2024011', 'isabela.gomes@ufrj.edu.br', '21987654377', 1, 4, 'Praia Vermelha', 78),
                                                                                                                                             ('11111111132', '2002-01-08 00:00:00.000', 'Matheus', 'Martins', '2024012', 'matheus.martins@ufrj.edu.br', '21987654378', 1, 5, 'Praia Vermelha', 79),
                                                                                                                                             ('11111111133', '2000-03-25 00:00:00.000', 'Sofia', 'Souza', '2024013', 'sofia.souza@ufrj.edu.br', '21987654379', 1, 1, 'Cidade Universitária', 80),
                                                                                                                                             ('11111111134', '2001-05-17 00:00:00.000', 'Thiago', 'Barbosa', '2024014', 'thiago.barbosa@ufrj.edu.br', '21987654380', 1, 2, 'Cidade Universitária', 81),
                                                                                                                                             ('11111111135', '2002-07-29 00:00:00.000', 'Larissa', 'Cardoso', '2024015', 'larissa.cardoso@ufrj.edu.br', '21987654381', 1, 3, 'Cidade Universitária', 82),
                                                                                                                                             ('11111111136', '2000-09-14 00:00:00.000', 'Bruno', 'Cavalcanti', '2024016', 'bruno.cavalcanti@ufrj.edu.br', '21987654382', 1, 4, 'Cidade Universitária', 83),
                                                                                                                                             ('11111111137', '2001-11-09 00:00:00.000', 'Camila', 'Dias', '2024017', 'camila.dias@ufrj.edu.br', '21987654383', 1, 5, 'Cidade Universitária', 84),
                                                                                                                                             ('11111111138', '2002-01-19 00:00:00.000', 'Diego', 'Freitas', '2024018', 'diego.freitas@ufrj.edu.br', '21987654384', 1, 6, 'Cidade Universitária', 85),
                                                                                                                                             ('11111111139', '2000-03-27 00:00:00.000', 'Amanda', 'Henrique', '2024019', 'amanda.henrique@ufrj.edu.br', '21987654385', 1, 7, 'Cidade Universitária', 86),
                                                                                                                                             ('11111111140', '2001-05-30 00:00:00.000', 'Felipe', 'Jesus', '2024020', 'felipe.jesus@ufrj.edu.br', '21987654386', 1, 1, 'Cidade Universitária', 87);

-- =====================================================
-- ALUNOS USP (ROLE_ALUNO - perfil_id = 6)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Mariana Silva', 'mariana.silva@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Carlos Santos', 'carlos.santos@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Fernanda Oliveira', 'fernanda.oliveira@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Ricardo Costa', 'ricardo.costa@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Patricia Lima', 'patricia.lima@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Andre Ferreira', 'andre.ferreira@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Cristina Almeida', 'cristina.almeida@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Paulo Rocha', 'paulo.rocha@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Lucia Pereira', 'lucia.pereira@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Roberto Carvalho', 'roberto.carvalho@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Monica Gomes', 'monica.gomes@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Jose Martins', 'jose.martins@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Sonia Souza', 'sonia.souza@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Antonio Barbosa', 'antonio.barbosa@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Teresa Cardoso', 'teresa.cardoso@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Manuel Cavalcanti', 'manuel.cavalcanti@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Isabel Dias', 'isabel.dias@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Francisco Freitas', 'francisco.freitas@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Helena Henrique', 'helena.henrique@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Miguel Jesus', 'miguel.jesus@al.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (88, 6), (89, 6), (90, 6), (91, 6), (92, 6), (93, 6), (94, 6), (95, 6), (96, 6),
                                                       (97, 6), (98, 6), (99, 6), (100, 6), (101, 6), (102, 6), (103, 6), (104, 6), (105, 6), (106, 6),
                                                       (107, 6);

INSERT INTO aluno (cpf, data_nascimento, nome, sobrenome, matricula, email, telefone, universidade_id, graduacao_id, campus, usuario_id) VALUES
                                                                                                                                             ('22222222221', '2000-04-12 00:00:00.000', 'Mariana', 'Silva', '2024001', 'mariana.silva@usp.edu.br', '11987654387', 2, 8, 'Cidade Universitária', 88),
                                                                                                                                             ('22222222222', '2001-06-18 00:00:00.000', 'Carlos', 'Santos', '2024002', 'carlos.santos@usp.edu.br', '11987654388', 2, 9, 'Cidade Universitária', 89),
                                                                                                                                             ('22222222223', '2002-08-05 00:00:00.000', 'Fernanda', 'Oliveira', '2024003', 'fernanda.oliveira@usp.edu.br', '11987654389', 2, 10, 'Cidade Universitária', 90),
                                                                                                                                             ('22222222224', '2000-10-20 00:00:00.000', 'Ricardo', 'Costa', '2024004', 'ricardo.costa@usp.edu.br', '11987654390', 2, 11, 'Cidade Universitária', 91),
                                                                                                                                             ('22222222225', '2001-12-15 00:00:00.000', 'Patricia', 'Lima', '2024005', 'patricia.lima@usp.edu.br', '11987654391', 2, 12, 'Cidade Universitária', 92),
                                                                                                                                             ('22222222226', '2002-02-08 00:00:00.000', 'Andre', 'Ferreira', '2024006', 'andre.ferreira@usp.edu.br', '11987654392', 2, 13, 'Cidade Universitária', 93),
                                                                                                                                             ('22222222227', '2000-04-22 00:00:00.000', 'Cristina', 'Almeida', '2024007', 'cristina.almeida@usp.edu.br', '11987654393', 2, 14, 'Cidade Universitária', 94),
                                                                                                                                             ('22222222228', '2001-06-30 00:00:00.000', 'Paulo', 'Rocha', '2024008', 'paulo.rocha@usp.edu.br', '11987654394', 2, 15, 'Cidade Universitária', 95),
                                                                                                                                             ('22222222229', '2002-08-14 00:00:00.000', 'Lucia', 'Pereira', '2024009', 'lucia.pereira@usp.edu.br', '11987654395', 2, 8, 'São Carlos', 96),
                                                                                                                                             ('22222222230', '2000-10-26 00:00:00.000', 'Roberto', 'Carvalho', '2024010', 'roberto.carvalho@usp.edu.br', '11987654396', 2, 9, 'São Carlos', 97),
                                                                                                                                             ('22222222231', '2001-12-03 00:00:00.000', 'Monica', 'Gomes', '2024011', 'monica.gomes@usp.edu.br', '11987654397', 2, 13, 'São Carlos', 98),
                                                                                                                                             ('22222222232', '2002-02-16 00:00:00.000', 'Jose', 'Martins', '2024012', 'jose.martins@usp.edu.br', '11987654398', 2, 10, 'Ribeirão Preto', 99),
                                                                                                                                             ('22222222233', '2000-04-28 00:00:00.000', 'Sonia', 'Souza', '2024013', 'sonia.souza@usp.edu.br', '11987654399', 2, 11, 'Ribeirão Preto', 100),
                                                                                                                                             ('22222222234', '2001-06-10 00:00:00.000', 'Antonio', 'Barbosa', '2024014', 'antonio.barbosa@usp.edu.br', '11987654400', 2, 12, 'Ribeirão Preto', 101),
                                                                                                                                             ('22222222235', '2002-08-24 00:00:00.000', 'Teresa', 'Cardoso', '2024015', 'teresa.cardoso@usp.edu.br', '11987654401', 2, 14, 'Piracicaba', 102),
                                                                                                                                             ('22222222236', '2000-10-06 00:00:00.000', 'Manuel', 'Cavalcanti', '2024016', 'manuel.cavalcanti@usp.edu.br', '11987654402', 2, 15, 'Piracicaba', 103),
                                                                                                                                             ('22222222237', '2001-12-19 00:00:00.000', 'Isabel', 'Dias', '2024017', 'isabel.dias@usp.edu.br', '11987654403', 2, 8, 'Cidade Universitária', 104),
                                                                                                                                             ('22222222238', '2002-02-01 00:00:00.000', 'Francisco', 'Freitas', '2024018', 'francisco.freitas@usp.edu.br', '11987654404', 2, 9, 'Cidade Universitária', 105),
                                                                                                                                             ('22222222239', '2000-04-13 00:00:00.000', 'Helena', 'Henrique', '2024019', 'helena.henrique@usp.edu.br', '11987654405', 2, 10, 'Cidade Universitária', 106),
                                                                                                                                             ('22222222240', '2001-06-25 00:00:00.000', 'Miguel', 'Jesus', '2024020', 'miguel.jesus@usp.edu.br', '11987654406', 2, 11, 'Cidade Universitária', 107);

-- =====================================================
-- ALUNOS UNICAMP (ROLE_ALUNO - perfil_id = 6)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Lucas Souza', 'lucas.souza@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Amanda Lima', 'amanda.lima@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Thiago Almeida', 'thiago.almeida@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Juliana Rocha', 'juliana.rocha@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Marcelo Pereira', 'marcelo.pereira@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Vanessa Carvalho', 'vanessa.carvalho@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Andre Martins', 'andre.martins@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Camila Dias', 'camila.dias@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Rafael Freitas', 'rafael.freitas@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Isabela Henrique', 'isabela.henrique@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Bruno Jesus', 'bruno.jesus@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Patricia Souza', 'patricia.souza@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Carlos Cardoso', 'carlos.cardoso@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Fernanda Barbosa', 'fernanda.barbosa@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Ricardo Dias', 'ricardo.dias@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Lucia Martins', 'lucia.martins@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Roberto Lima', 'roberto.lima@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Monica Santos', 'monica.santos@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Jose Oliveira', 'jose.oliveira@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Sonia Costa', 'sonia.costa@al.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (108, 6), (109, 6), (110, 6), (111, 6), (112, 6), (113, 6), (114, 6), (115, 6), (116, 6),
                                                       (117, 6), (118, 6), (119, 6), (120, 6), (121, 6), (122, 6), (123, 6), (124, 6), (125, 6), (126, 6),(127,6);

INSERT INTO aluno (cpf, data_nascimento, nome, sobrenome, matricula, email, telefone, universidade_id, graduacao_id, campus, usuario_id) VALUES
                                                                                                                                             ('33333333321', '2000-03-10 00:00:00.000', 'Lucas', 'Souza', '2024001', 'lucas.souza@unicamp.edu.br', '19987654407', 3, 16, 'Barão Geraldo', 108),
                                                                                                                                             ('33333333322', '2001-05-12 00:00:00.000', 'Amanda', 'Lima', '2024002', 'amanda.lima@unicamp.edu.br', '19987654408', 3, 17, 'Barão Geraldo', 109),
                                                                                                                                             ('33333333323', '2002-07-14 00:00:00.000', 'Thiago', 'Almeida', '2024003', 'thiago.almeida@unicamp.edu.br', '19987654409', 3, 18, 'Barão Geraldo', 110),
                                                                                                                                             ('33333333324', '2000-09-16 00:00:00.000', 'Juliana', 'Rocha', '2024004', 'juliana.rocha@unicamp.edu.br', '19987654410', 3, 19, 'Barão Geraldo', 111),
                                                                                                                                             ('33333333325', '2001-11-18 00:00:00.000', 'Marcelo', 'Pereira', '2024005', 'marcelo.pereira@unicamp.edu.br', '19987654411', 3, 20, 'Barão Geraldo', 112),
                                                                                                                                             ('33333333326', '2002-01-20 00:00:00.000', 'Vanessa', 'Carvalho', '2024006', 'vanessa.carvalho@unicamp.edu.br', '19987654412', 3, 16, 'Limeira', 113),
                                                                                                                                             ('33333333327', '2000-03-22 00:00:00.000', 'Andre', 'Martins', '2024007', 'andre.martins@unicamp.edu.br', '19987654413', 3, 17, 'Limeira', 114),
                                                                                                                                             ('33333333328', '2001-05-24 00:00:00.000', 'Camila', 'Dias', '2024008', 'camila.dias@unicamp.edu.br', '19987654414', 3, 18, 'Limeira', 115),
                                                                                                                                             ('33333333329', '2002-07-26 00:00:00.000', 'Rafael', 'Freitas', '2024009', 'rafael.freitas@unicamp.edu.br', '19987654415', 3, 19, 'Limeira', 116),
                                                                                                                                             ('33333333330', '2000-09-28 00:00:00.000', 'Isabela', 'Henrique', '2024010', 'isabela.henrique@unicamp.edu.br', '19987654416', 3, 20, 'Limeira', 117),
                                                                                                                                             ('33333333331', '2001-11-30 00:00:00.000', 'Bruno', 'Jesus', '2024011', 'bruno.jesus@unicamp.edu.br', '19987654417', 3, 16, 'Barão Geraldo', 118),
                                                                                                                                             ('33333333332', '2002-01-02 00:00:00.000', 'Patricia', 'Souza', '2024012', 'patricia.souza@unicamp.edu.br', '19987654418', 3, 17, 'Barão Geraldo', 119),
                                                                                                                                             ('33333333333', '2000-03-04 00:00:00.000', 'Carlos', 'Cardoso', '2024013', 'carlos.cardoso@unicamp.edu.br', '19987654419', 3, 18, 'Barão Geraldo', 120),
                                                                                                                                             ('33333333334', '2001-05-06 00:00:00.000', 'Fernanda', 'Barbosa', '2024014', 'fernanda.barbosa@unicamp.edu.br', '19987654420', 3, 19, 'Barão Geraldo', 121),
                                                                                                                                             ('33333333335', '2002-07-08 00:00:00.000', 'Ricardo', 'Dias', '2024015', 'ricardo.dias@unicamp.edu.br', '19987654421', 3, 20, 'Barão Geraldo', 122),
                                                                                                                                             ('33333333336', '2000-09-10 00:00:00.000', 'Lucia', 'Martins', '2024016', 'lucia.martins@unicamp.edu.br', '19987654422', 3, 16, 'Limeira', 123),
                                                                                                                                             ('33333333337', '2001-11-12 00:00:00.000', 'Roberto', 'Lima', '2024017', 'roberto.lima@unicamp.edu.br', '19987654423', 3, 17, 'Limeira', 124),
                                                                                                                                             ('33333333338', '2002-01-14 00:00:00.000', 'Monica', 'Santos', '2024018', 'monica.santos@unicamp.edu.br', '19987654424', 3, 18, 'Limeira', 125),
                                                                                                                                             ('33333333339', '2000-03-16 00:00:00.000', 'Jose', 'Oliveira', '2024019', 'jose.oliveira@unicamp.edu.br', '19987654425', 3, 19, 'Limeira', 126),
                                                                                                                                             ('33333333340', '2001-05-18 00:00:00.000', 'Sonia', 'Costa', '2024020', 'sonia.costa@unicamp.edu.br', '19987654426', 3, 20, 'Limeira', 127);

-- =====================================================
-- ALUNOS UERJ (ROLE_ALUNO - perfil_id = 6)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Bruna Silva', 'bruna.silva@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Gabriel Santos', 'gabriel.santos@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Diego Oliveira', 'diego.oliveira@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Leticia Costa', 'leticia.costa@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Paulo Lima', 'paulo.lima@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Ana Ferreira', 'ana.ferreira@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Carlos Almeida', 'carlos.almeida@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Monica Rocha', 'monica.rocha@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Joao Pereira', 'joao.pereira@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Patricia Carvalho', 'patricia.carvalho@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Isabela Gomes', 'isabela.gomes@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Matheus Martins', 'matheus.martins@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Sofia Souza', 'sofia.souza@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Thiago Barbosa', 'thiago.barbosa@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Larissa Cardoso', 'larissa.cardoso@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Bruno Cavalcanti', 'bruno.cavalcanti@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Camila Dias', 'camila.dias@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Diego Freitas', 'diego.freitas@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Amanda Henrique', 'amanda.henrique@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Felipe Jesus', 'felipe.jesus@al.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (128, 6), (129, 6), (130, 6), (131, 6), (132, 6), (133, 6), (134, 6), (135, 6), (136, 6),
                                                       (137, 6), (138, 6), (139, 6), (140, 6), (141, 6), (142, 6), (143, 6), (144, 6), (145, 6), (146, 6), (147,6);

INSERT INTO aluno (cpf, data_nascimento, nome, sobrenome, matricula, email, telefone, universidade_id, graduacao_id, campus, usuario_id) VALUES
                                                                                                                                             ('44444444421', '2000-03-15 00:00:00.000', 'Bruna', 'Silva', '2024001', 'bruna.silva@uerj.edu.br', '21987654427', 4, 21, 'Maracanã', 128),
                                                                                                                                             ('44444444422', '2001-05-20 00:00:00.000', 'Gabriel', 'Santos', '2024002', 'gabriel.santos@uerj.edu.br', '21987654428', 4, 22, 'Maracanã', 129),
                                                                                                                                             ('44444444423', '2002-07-10 00:00:00.000', 'Diego', 'Oliveira', '2024003', 'diego.oliveira@uerj.edu.br', '21987654429', 4, 23, 'Maracanã', 130),
                                                                                                                                             ('44444444424', '2000-09-25 00:00:00.000', 'Leticia', 'Costa', '2024004', 'leticia.costa@uerj.edu.br', '21987654430', 4, 24, 'Maracanã', 131),
                                                                                                                                             ('44444444425', '2001-11-30 00:00:00.000', 'Paulo', 'Lima', '2024005', 'paulo.lima@uerj.edu.br', '21987654431', 4, 25, 'Maracanã', 132),
                                                                                                                                             ('44444444426', '2002-01-05 00:00:00.000', 'Ana', 'Ferreira', '2024006', 'ana.ferreira@uerj.edu.br', '21987654432', 4, 26, 'Maracanã', 133),
                                                                                                                                             ('44444444427', '2000-03-15 00:00:00.000', 'Carlos', 'Almeida', '2024007', 'carlos.almeida@uerj.edu.br', '21987654433', 4, 27, 'Maracanã', 134),
                                                                                                                                             ('44444444428', '2001-05-28 00:00:00.000', 'Monica', 'Rocha', '2024008', 'monica.rocha@uerj.edu.br', '21987654434', 4, 28, 'Maracanã', 135),
                                                                                                                                             ('44444444429', '2002-07-12 00:00:00.000', 'Joao', 'Pereira', '2024009', 'joao.pereira@uerj.edu.br', '21987654435', 4, 29, 'Maracanã', 136),
                                                                                                                                             ('44444444430', '2000-09-18 00:00:00.000', 'Patricia', 'Carvalho', '2024010', 'patricia.carvalho@uerj.edu.br', '21987654436', 4, 30, 'Maracanã', 137),
                                                                                                                                             ('44444444431', '2001-11-22 00:00:00.000', 'Isabela', 'Gomes', '2024011', 'isabela.gomes@uerj.edu.br', '21987654437', 4, 21, 'São Gonçalo', 138),
                                                                                                                                             ('44444444432', '2002-01-08 00:00:00.000', 'Matheus', 'Martins', '2024012', 'matheus.martins@uerj.edu.br', '21987654438', 4, 22, 'São Gonçalo', 139),
                                                                                                                                             ('44444444433', '2000-03-25 00:00:00.000', 'Sofia', 'Souza', '2024013', 'sofia.souza@uerj.edu.br', '21987654439', 4, 23, 'São Gonçalo', 140),
                                                                                                                                             ('44444444434', '2001-05-17 00:00:00.000', 'Thiago', 'Barbosa', '2024014', 'thiago.barbosa@uerj.edu.br', '21987654440', 4, 24, 'São Gonçalo', 141),
                                                                                                                                             ('44444444435', '2002-07-29 00:00:00.000', 'Larissa', 'Cardoso', '2024015', 'larissa.cardoso@uerj.edu.br', '21987654441', 4, 25, 'São Gonçalo', 142),
                                                                                                                                             ('44444444436', '2000-09-14 00:00:00.000', 'Bruno', 'Cavalcanti', '2024016', 'bruno.cavalcanti@uerj.edu.br', '21987654442', 4, 26, 'São Gonçalo', 143),
                                                                                                                                             ('44444444437', '2001-11-09 00:00:00.000', 'Camila', 'Dias', '2024017', 'camila.dias@uerj.edu.br', '21987654443', 4, 27, 'São Gonçalo', 144),
                                                                                                                                             ('44444444438', '2002-01-19 00:00:00.000', 'Diego', 'Freitas', '2024018', 'diego.freitas@uerj.edu.br', '21987654444', 4, 28, 'São Gonçalo', 145),
                                                                                                                                             ('44444444439', '2000-03-27 00:00:00.000', 'Amanda', 'Henrique', '2024019', 'amanda.henrique@uerj.edu.br', '21987654445', 4, 29, 'São Gonçalo', 146),
                                                                                                                                             ('44444444440', '2001-05-30 00:00:00.000', 'Felipe', 'Jesus', '2024020', 'felipe.jesus@uerj.edu.br', '21987654446', 4, 30, 'São Gonçalo', 147);

-- =====================================================
-- ALUNOS UFF (ROLE_ALUNO - perfil_id = 6)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Joao Souza', 'joao.souza@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Mariana Lima', 'mariana.lima@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Felipe Almeida', 'felipe.almeida@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Camila Rocha', 'camila.rocha@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Rafael Pereira', 'rafael.pereira@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Isabela Carvalho', 'isabela.carvalho@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Bruno Martins', 'bruno.martins@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Patricia Souza', 'patricia.souza@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Gabriel Lima', 'gabriel.lima@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Larissa Almeida', 'larissa.almeida@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Bruna Silva', 'bruna.silva@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Gabriel Santos', 'gabriel.santos@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Diego Oliveira', 'diego.oliveira@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Leticia Costa', 'leticia.costa@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Paulo Lima', 'paulo.lima@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Ana Ferreira', 'ana.ferreira@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Carlos Almeida', 'carlos.almeida@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Monica Rocha', 'monica.rocha@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Joao Pereira', 'joao.pereira@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Patricia Carvalho', 'patricia.carvalho@al.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (148, 6), (149, 6), (150, 6), (151, 6), (152, 6), (153, 6), (154, 6), (155, 6), (156, 6),
                                                       (157, 6), (158, 6), (159, 6), (160, 6), (161, 6), (162, 6), (163, 6), (164, 6), (165, 6), (166, 6), (167,6);

INSERT INTO aluno (cpf, data_nascimento, nome, sobrenome, matricula, email, telefone, universidade_id, graduacao_id, campus, usuario_id) VALUES
                                                                                                                                             ('55555555521', '2000-03-15 00:00:00.000', 'Joao', 'Souza', '2024001', 'joao.souza@uff.edu.br', '21987654447', 5, 31, 'Gragoatá', 148),
                                                                                                                                             ('55555555522', '2001-05-20 00:00:00.000', 'Mariana', 'Lima', '2024002', 'mariana.lima@uff.edu.br', '21987654448', 5, 32, 'Gragoatá', 149),
                                                                                                                                             ('55555555523', '2002-07-10 00:00:00.000', 'Felipe', 'Almeida', '2024003', 'felipe.almeida@uff.edu.br', '21987654449', 5, 33, 'Gragoatá', 150),
                                                                                                                                             ('55555555524', '2000-09-25 00:00:00.000', 'Camila', 'Rocha', '2024004', 'camila.rocha@uff.edu.br', '21987654450', 5, 34, 'Gragoatá', 151),
                                                                                                                                             ('55555555525', '2001-11-30 00:00:00.000', 'Rafael', 'Pereira', '2024005', 'rafael.pereira@uff.edu.br', '21987654451', 5, 35, 'Gragoatá', 152),
                                                                                                                                             ('55555555526', '2002-01-05 00:00:00.000', 'Isabela', 'Carvalho', '2024006', 'isabela.carvalho@uff.edu.br', '21987654452', 5, 36, 'Gragoatá', 153),
                                                                                                                                             ('55555555527', '2000-03-15 00:00:00.000', 'Bruno', 'Martins', '2024007', 'bruno.martins@uff.edu.br', '21987654453', 5, 37, 'Gragoatá', 154),
                                                                                                                                             ('55555555528', '2001-05-28 00:00:00.000', 'Patricia', 'Souza', '2024008', 'patricia.souza@uff.edu.br', '21987654454', 5, 38, 'Gragoatá', 155),
                                                                                                                                             ('55555555529', '2002-07-12 00:00:00.000', 'Gabriel', 'Lima', '2024009', 'gabriel.lima@uff.edu.br', '21987654455', 5, 39, 'Gragoatá', 156),
                                                                                                                                             ('55555555530', '2000-09-18 00:00:00.000', 'Larissa', 'Almeida', '2024010', 'larissa.almeida@uff.edu.br', '21987654456', 5, 40, 'Gragoatá', 157),
                                                                                                                                             ('55555555531', '2001-11-22 00:00:00.000', 'Bruna', 'Silva', '2024011', 'bruna.silva@uff.edu.br', '21987654457', 5, 31, 'Santo Antônio', 158),
                                                                                                                                             ('55555555532', '2002-01-08 00:00:00.000', 'Gabriel', 'Santos', '2024012', 'gabriel.santos@uff.edu.br', '21987654458', 5, 32, 'Santo Antônio', 159),
                                                                                                                                             ('55555555533', '2000-03-25 00:00:00.000', 'Diego', 'Oliveira', '2024013', 'diego.oliveira@uff.edu.br', '21987654459', 5, 33, 'Santo Antônio', 160),
                                                                                                                                             ('55555555534', '2001-05-17 00:00:00.000', 'Leticia', 'Costa', '2024014', 'leticia.costa@uff.edu.br', '21987654460', 5, 34, 'Santo Antônio', 161),
                                                                                                                                             ('55555555535', '2002-07-29 00:00:00.000', 'Paulo', 'Lima', '2024015', 'paulo.lima@uff.edu.br', '21987654461', 5, 35, 'Santo Antônio', 162),
                                                                                                                                             ('55555555536', '2000-09-14 00:00:00.000', 'Ana', 'Ferreira', '2024016', 'ana.ferreira@uff.edu.br', '21987654462', 5, 36, 'Santo Antônio', 163),
                                                                                                                                             ('55555555537', '2001-11-09 00:00:00.000', 'Carlos', 'Almeida', '2024017', 'carlos.almeida@uff.edu.br', '21987654463', 5, 37, 'Santo Antônio', 164),
                                                                                                                                             ('55555555538', '2002-01-19 00:00:00.000', 'Monica', 'Rocha', '2024018', 'monica.rocha@uff.edu.br', '21987654464', 5, 38, 'Santo Antônio', 165),
                                                                                                                                             ('55555555539', '2000-03-27 00:00:00.000', 'Joao', 'Pereira', '2024019', 'joao.pereira@uff.edu.br', '21987654465', 5, 39, 'Santo Antônio', 166),
                                                                                                                                             ('55555555540', '2001-05-30 00:00:00.000', 'Patricia', 'Carvalho', '2024020', 'patricia.carvalho@uff.edu.br', '21987654466', 5, 40, 'Santo Antônio', 167);

-- =====================================================
-- ALUNOS UNESP (ROLE_ALUNO - perfil_id = 6)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Marcos Silva', 'marcos.silva@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Renata Santos', 'renata.santos@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Eduardo Oliveira', 'eduardo.oliveira@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Luciana Costa', 'luciana.costa@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Bruno Lima', 'bruno.lima@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Patricia Ferreira', 'patricia.ferreira@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Carlos Almeida', 'carlos.almeida@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Fernanda Rocha', 'fernanda.rocha@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Joao Pereira', 'joao.pereira@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Larissa Carvalho', 'larissa.carvalho@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Mariana Silva', 'mariana.silva@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Carlos Marconi', 'carlos.marconi@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Fernanda Torres', 'fernanda.torres@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Ricardo Costa', 'ricardo.costa@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Patricia Pilares', 'patricia.pilares@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Andre Ferreira', 'andre.ferreira@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Cristina Almeida', 'cristina.almeida@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Paulo Rocha', 'paulo.rocha@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Lucia Pereira', 'lucia.pereira@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Roberto Carvalho', 'roberto.carvalho@al.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (168, 6), (169, 6), (170, 6), (171, 6), (172, 6), (173, 6), (174, 6), (175, 6), (176, 6),
                                                       (177, 6), (178, 6), (179, 6), (180, 6), (181, 6), (182, 6), (183, 6), (184, 6), (185, 6), (186, 6), (187,6);

INSERT INTO aluno (cpf, data_nascimento, nome, sobrenome, matricula, email, telefone, universidade_id, graduacao_id, campus, usuario_id) VALUES
                                                                                                                                             ('66666666621', '2000-03-15 00:00:00.000', 'Marcos', 'Silva', '2024001', 'marcos.silva@unesp.edu.br', '11987654467', 6, 36, 'Botucatu', 168),
                                                                                                                                             ('66666666622', '2001-05-20 00:00:00.000', 'Renata', 'Santos', '2024002', 'renata.santos@unesp.edu.br', '11987654468', 6, 37, 'Botucatu', 169),
                                                                                                                                             ('66666666623', '2002-07-10 00:00:00.000', 'Eduardo', 'Oliveira', '2024003', 'eduardo.oliveira@unesp.edu.br', '11987654469', 6, 38, 'Botucatu', 170),
                                                                                                                                             ('66666666624', '2000-09-25 00:00:00.000', 'Luciana', 'Costa', '2024004', 'luciana.costa@unesp.edu.br', '11987654470', 6, 39, 'Botucatu', 171),
                                                                                                                                             ('66666666625', '2001-11-30 00:00:00.000', 'Bruno', 'Lima', '2024005', 'bruno.lima@unesp.edu.br', '11987654471', 6, 40, 'Botucatu', 172),
                                                                                                                                             ('66666666626', '2002-01-05 00:00:00.000', 'Patricia', 'Ferreira', '2024006', 'patricia.ferreira@unesp.edu.br', '11987654472', 6, 36, 'Araraquara', 173),
                                                                                                                                             ('66666666627', '2000-03-15 00:00:00.000', 'Carlos', 'Marconi', '2024007', 'carlos.almeida@unesp.edu.br', '11987654473', 6, 37, 'Araraquara', 174),
                                                                                                                                             ('66666666628', '2001-05-28 00:00:00.000', 'Fernanda', 'Rocha', '2024008', 'fernanda.rocha@unesp.edu.br', '11987654474', 6, 38, 'Araraquara', 175),
                                                                                                                                             ('66666666629', '2002-07-12 00:00:00.000', 'Joao', 'Pereira', '2024009', 'joao.pereira@unesp.edu.br', '11987654475', 6, 39, 'Araraquara', 176),
                                                                                                                                             ('66666666630', '2000-09-18 00:00:00.000', 'Larissa', 'Carvalho', '2024010', 'larissa.carvalho@unesp.edu.br', '11987654476', 6, 40, 'Araraquara', 177),
                                                                                                                                             ('66666666631', '2001-11-22 00:00:00.000', 'Mariana', 'Silva', '2024011', 'mariana.silva@unesp.edu.br', '11987654477', 6, 36, 'Bauru', 178),
                                                                                                                                             ('66666666632', '2002-01-08 00:00:00.000', 'Carlos', 'Santos', '2024012', 'carlos.marconi@unesp.edu.br', '11987654478', 6, 37, 'Bauru', 179),
                                                                                                                                             ('66666666633', '2000-03-25 00:00:00.000', 'Fernanda', 'Torres', '2024013', 'fernanda.torres@unesp.edu.br', '11987654479', 6, 38, 'Bauru', 180),
                                                                                                                                             ('66666666634', '2001-05-17 00:00:00.000', 'Ricardo', 'Costa', '2024014', 'ricardo.costa@unesp.edu.br', '11987654480', 6, 39, 'Bauru', 181),
                                                                                                                                             ('66666666635', '2002-07-29 00:00:00.000', 'Patricia', 'Pilares', '2024015', 'patricia.pilares@unesp.edu.br', '11987654481', 6, 40, 'Bauru', 182),
                                                                                                                                             ('66666666636', '2000-09-14 00:00:00.000', 'Andre', 'Ferreira', '2024016', 'andre.ferreira@unesp.edu.br', '11987654482', 6, 36, 'Botucatu', 183),
                                                                                                                                             ('66666666637', '2001-11-09 00:00:00.000', 'Cristina', 'Almeida', '2024017', 'cristina.almeida@unesp.edu.br', '11987654483', 6, 37, 'Botucatu', 184),
                                                                                                                                             ('66666666638', '2002-01-19 00:00:00.000', 'Paulo', 'Rocha', '2024018', 'paulo.rocha@unesp.edu.br', '11987654484', 6, 38, 'Botucatu', 185),
                                                                                                                                             ('66666666639', '2000-03-27 00:00:00.000', 'Lucia', 'Pereira', '2024019', 'lucia.pereira@unesp.edu.br', '11987654485', 6, 39, 'Botucatu', 186),
                                                                                                                                             ('66666666640', '2001-05-30 00:00:00.000', 'Roberto', 'Carvalho', '2024020', 'roberto.carvalho@unesp.edu.br', '11987654486', 6, 40, 'Botucatu', 187);

-- =====================================================
-- FUNCIONÁRIOS UFRJ (SECRETARIA E RH)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Maria Silva', 'maria.silva@sec.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Joao Santos', 'joao.santos@sec.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Ana Oliveira', 'ana.oliveira@sec.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Carlos Costa', 'carlos.costa@sec.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Patricia Lima', 'patricia.lima@sec.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Roberto Ferreira', 'roberto.ferreira@rh.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Lucia Almeida', 'lucia.almeida@rh.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Fernando Rocha', 'fernando.rocha@rh.ufrj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (188, 3), (189, 3), (190, 3), (191, 3), (192, 3),  -- Secretaria
                                                       (193, 4), (194, 4), (195, 4); -- RH

INSERT INTO funcionarios (cpf, data_nascimento, nome, sobrenome, email, telefone, universidade_id, salario, usuario_id, setor) VALUES
                                                                                                                                   ('77777777701', '1980-05-15 00:00:00.000', 'Maria', 'Silva', 'maria.silva@ufrj.edu.br', '21987654487', 1, 3500.00, 188, 'Secretaria'),
                                                                                                                                   ('77777777702', '1982-08-20 00:00:00.000', 'Joao', 'Santos', 'joao.santos@ufrj.edu.br', '21987654488', 1, 3500.00, 189, 'Secretaria'),
                                                                                                                                   ('77777777703', '1985-03-10 00:00:00.000', 'Ana', 'Oliveira', 'ana.oliveira@ufrj.edu.br', '21987654489', 1, 3500.00, 190, 'Secretaria'),
                                                                                                                                   ('77777777704', '1981-12-25 00:00:00.000', 'Carlos', 'Costa', 'carlos.costa@ufrj.edu.br', '21987654490', 1, 3500.00, 191, 'Secretaria'),
                                                                                                                                   ('77777777705', '1983-07-18 00:00:00.000', 'Patricia', 'Lima', 'patricia.lima@ufrj.edu.br', '21987654491', 1, 3500.00, 192, 'Secretaria'),
                                                                                                                                   ('77777777706', '1978-11-30 00:00:00.000', 'Roberto', 'Ferreira', 'roberto.ferreira@ufrj.edu.br', '21987654492', 1, 4200.00, 193, 'RH'),
                                                                                                                                   ('77777777707', '1980-04-12 00:00:00.000', 'Lucia', 'Almeida', 'lucia.almeida@ufrj.edu.br', '21987654493', 1, 4200.00, 194, 'RH'),
                                                                                                                                   ('77777777708', '1982-09-05 00:00:00.000', 'Fernando', 'Rocha', 'fernando.rocha@ufrj.edu.br', '21987654494', 1, 4200.00, 195, 'RH');

-- =====================================================
-- FUNCIONÁRIOS USP (SECRETARIA E RH)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Sonia Silva', 'sonia.silva@sec.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Paulo Santos', 'paulo.santos@sec.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Cristina Oliveira', 'cristina.oliveira@sec.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Ricardo Costa', 'ricardo.costa@sec.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Monica Lima', 'monica.lima@sec.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Andre Ferreira', 'andre.ferreira@rh.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Larissa Almeida', 'larissa.almeida@rh.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Bruno Rocha', 'bruno.rocha@rh.usp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (196, 3), (197, 3), (198, 3), (199, 3), -- Secretaria
                                                       (200, 3), (201, 4), (202, 4), (203, 4); -- RH

INSERT INTO funcionarios (cpf, data_nascimento, nome, sobrenome, email, telefone, universidade_id, salario, usuario_id, setor) VALUES
                                                                                                                                   ('77777777709', '1980-06-15 00:00:00.000', 'Sonia', 'Silva', 'sonia.silva@usp.edu.br', '11987654495', 2, 3800.00, 196, 'Secretaria'),
                                                                                                                                   ('77777777710', '1982-09-20 00:00:00.000', 'Paulo', 'Santos', 'paulo.santos@usp.edu.br', '11987654496', 2, 3800.00, 197, 'Secretaria'),
                                                                                                                                   ('77777777711', '1985-04-10 00:00:00.000', 'Cristina', 'Oliveira', 'cristina.oliveira@usp.edu.br', '11987654497', 2, 3800.00, 198, 'Secretaria'),
                                                                                                                                   ('77777777712', '1981-01-25 00:00:00.000', 'Ricardo', 'Costa', 'ricardo.costa@usp.edu.br', '11987654498', 2, 3800.00, 199, 'Secretaria'),
                                                                                                                                   ('77777777713', '1983-08-18 00:00:00.000', 'Monica', 'Lima', 'monica.lima@usp.edu.br', '11987654499', 2, 3800.00, 200, 'Secretaria'),
                                                                                                                                   ('77777777714', '1978-12-30 00:00:00.000', 'Andre', 'Ferreira', 'andre.ferreira@usp.edu.br', '11987654500', 2, 4500.00, 201, 'RH'),
                                                                                                                                   ('77777777715', '1980-05-12 00:00:00.000', 'Larissa', 'Almeida', 'larissa.almeida@usp.edu.br', '11987654501', 2, 4500.00, 202, 'RH'),
                                                                                                                                   ('77777777716', '1982-10-05 00:00:00.000', 'Bruno', 'Rocha', 'bruno.rocha@usp.edu.br', '11987654502', 2, 4500.00, 203, 'RH');

-- =====================================================
-- FUNCIONÁRIOS UNICAMP (SECRETARIA E RH)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Lucia Silva', 'lucia.silva@sec.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Marcos Santos', 'marcos.santos@sec.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Renata Oliveira', 'renata.oliveira@sec.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Eduardo Costa', 'eduardo.costa@sec.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Isabela Lima', 'isabela.lima@sec.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Thiago Ferreira', 'thiago.ferreira@rh.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Camila Almeida', 'camila.almeida@rh.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Rafael Rocha', 'rafael.rocha@rh.unicamp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (204, 3), (205, 3), (206, 3), (207, 3), -- Secretaria
                                                       (208, 3), (209, 4), (210, 4), (211, 4); -- RH

INSERT INTO funcionarios (cpf, data_nascimento, nome, sobrenome, email, telefone, universidade_id, salario, usuario_id, setor) VALUES
                                                                                                                                   ('77777777717', '1980-07-15 00:00:00.000', 'Lucia', 'Silva', 'lucia.silva@unicamp.edu.br', '19987654503', 3, 3600.00, 204, 'Secretaria'),
                                                                                                                                   ('77777777718', '1982-10-20 00:00:00.000', 'Marcos', 'Santos', 'marcos.santos@unicamp.edu.br', '19987654504', 3, 3600.00, 205, 'Secretaria'),
                                                                                                                                   ('77777777719', '1985-05-10 00:00:00.000', 'Renata', 'Oliveira', 'renata.oliveira@unicamp.edu.br', '19987654505', 3, 3600.00, 206, 'Secretaria'),
                                                                                                                                   ('77777777720', '1981-02-25 00:00:00.000', 'Eduardo', 'Costa', 'eduardo.costa@unicamp.edu.br', '19987654506', 3, 3600.00, 207, 'Secretaria'),
                                                                                                                                   ('77777777721', '1983-09-18 00:00:00.000', 'Isabela', 'Lima', 'isabela.lima@unicamp.edu.br', '19987654507', 3, 3600.00, 208, 'Secretaria'),
                                                                                                                                   ('77777777722', '1978-01-30 00:00:00.000', 'Thiago', 'Ferreira', 'thiago.ferreira@unicamp.edu.br', '19987654508', 3, 4300.00, 209, 'RH'),
                                                                                                                                   ('77777777723', '1980-06-12 00:00:00.000', 'Camila', 'Almeida', 'camila.almeida@unicamp.edu.br', '19987654509', 3, 4300.00, 210, 'RH'),
                                                                                                                                   ('77777777724', '1982-11-05 00:00:00.000', 'Rafael', 'Rocha', 'rafael.rocha@unicamp.edu.br', '19987654510', 3, 4300.00, 211, 'RH');

-- =====================================================
-- FUNCIONÁRIOS UERJ (SECRETARIA E RH)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Bruna Silva', 'bruna.silva@sec.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Gabriel Santos', 'gabriel.santos@sec.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Diego Oliveira', 'diego.oliveira@sec.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Leticia Costa', 'leticia.costa@sec.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Paulo Lima', 'paulo.lima@sec.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Ana Ferreira', 'ana.ferreira@rh.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Carlos Almeida', 'carlos.almeida@rh.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Monica Rocha', 'monica.rocha@rh.uerj.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (212, 3), (213, 3), (214, 3), (215, 3), -- Secretaria
                                                       (216, 3), (217, 4), (218, 4), (219, 4); -- RH

INSERT INTO funcionarios (cpf, data_nascimento, nome, sobrenome, email, telefone, universidade_id, salario, usuario_id, setor) VALUES
                                                                                                                                   ('77777777725', '1980-08-15 00:00:00.000', 'Bruna', 'Silva', 'bruna.silva@uerj.edu.br', '21987654511', 4, 3400.00, 212, 'Secretaria'),
                                                                                                                                   ('77777777726', '1982-11-20 00:00:00.000', 'Gabriel', 'Santos', 'gabriel.santos@uerj.edu.br', '21987654512', 4, 3400.00, 213, 'Secretaria'),
                                                                                                                                   ('77777777727', '1985-06-10 00:00:00.000', 'Diego', 'Oliveira', 'diego.oliveira@uerj.edu.br', '21987654513', 4, 3400.00, 214, 'Secretaria'),
                                                                                                                                   ('77777777728', '1981-03-25 00:00:00.000', 'Leticia', 'Costa', 'leticia.costa@uerj.edu.br', '21987654514', 4, 3400.00, 215, 'Secretaria'),
                                                                                                                                   ('77777777729', '1983-10-18 00:00:00.000', 'Paulo', 'Lima', 'paulo.lima@uerj.edu.br', '21987654515', 4, 3400.00, 216, 'Secretaria'),
                                                                                                                                   ('77777777730', '1978-02-30 00:00:00.000', 'Ana', 'Ferreira', 'ana.ferreira@uerj.edu.br', '21987654516', 4, 4100.00, 217, 'RH'),
                                                                                                                                   ('77777777731', '1980-07-12 00:00:00.000', 'Carlos', 'Almeida', 'carlos.almeida@uerj.edu.br', '21987654517', 4, 4100.00, 218, 'RH'),
                                                                                                                                   ('77777777732', '1982-12-05 00:00:00.000', 'Monica', 'Rocha', 'monica.rocha@uerj.edu.br', '21987654518', 4, 4100.00, 219, 'RH');

-- =====================================================
-- FUNCIONÁRIOS UFF (SECRETARIA E RH)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Joao Silva', 'joao.silva@sec.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Mariana Santos', 'mariana.santos@sec.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Felipe Oliveira', 'felipe.oliveira@sec.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Camila Costa', 'camila.costa@sec.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Rafael Lima', 'rafael.lima@sec.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Isabela Ferreira', 'isabela.ferreira@rh.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Bruno Almeida', 'bruno.almeida@rh.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Patricia Rocha', 'patricia.rocha@rh.uff.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (220, 3), (221, 3), (222, 3), (223, 3), -- Secretaria
                                                       (224, 3), (225, 4), (226, 4), (227, 4); -- RH

INSERT INTO funcionarios (cpf, data_nascimento, nome, sobrenome, email, telefone, universidade_id, salario, usuario_id, setor) VALUES
                                                                                                                                   ('77777777733', '1980-09-15 00:00:00.000', 'Joao', 'Silva', 'joao.silva@uff.edu.br', '21987654519', 5, 3300.00, 220, 'Secretaria'),
                                                                                                                                   ('77777777734', '1982-12-20 00:00:00.000', 'Mariana', 'Santos', 'mariana.santos@uff.edu.br', '21987654520', 5, 3300.00, 221, 'Secretaria'),
                                                                                                                                   ('77777777735', '1985-07-10 00:00:00.000', 'Felipe', 'Oliveira', 'felipe.oliveira@uff.edu.br', '21987654521', 5, 3300.00, 222, 'Secretaria'),
                                                                                                                                   ('77777777736', '1981-04-25 00:00:00.000', 'Camila', 'Costa', 'camila.costa@uff.edu.br', '21987654522', 5, 3300.00, 223, 'Secretaria'),
                                                                                                                                   ('77777777737', '1983-11-18 00:00:00.000', 'Rafael', 'Lima', 'rafael.lima@uff.edu.br', '21987654523', 5, 3300.00, 224, 'Secretaria'),
                                                                                                                                   ('77777777738', '1978-03-30 00:00:00.000', 'Isabela', 'Ferreira', 'isabela.ferreira@uff.edu.br', '21987654524', 5, 4000.00, 225, 'RH'),
                                                                                                                                   ('77777777739', '1980-08-12 00:00:00.000', 'Bruno', 'Almeida', 'bruno.almeida@uff.edu.br', '21987654525', 5, 4000.00, 226, 'RH'),
                                                                                                                                   ('77777777740', '1982-01-05 00:00:00.000', 'Patricia', 'Rocha', 'patricia.rocha@uff.edu.br', '21987654526', 5, 4000.00, 227, 'RH');

-- =====================================================
-- FUNCIONÁRIOS UNESP (SECRETARIA E RH)
-- =====================================================
INSERT INTO usuarios (nome, email, senha, primeiro_acesso, ativo) VALUES
                                                                      ('Marcos Silva', 'marcos.silva@sec.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Renata Santos', 'renata.santos@sec.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Eduardo Oliveira', 'eduardo.oliveira@sec.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Luciana Costa', 'luciana.costa@sec.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Bruno Lima', 'bruno.lima@sec.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Patricia Ferreira', 'patricia.ferreira@rh.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Carlos Almeida', 'carlos.almeida@rh.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true),
                                                                      ('Fernanda Rocha', 'fernanda.rocha@rh.unesp.unify.edu.com', '$2a$10$foIfg2GXynD2qjxr6XLJIO7dn7CIAlvVRAlLhJL1PEaeK31nYbp4m', false, true);

INSERT INTO usuario_perfis (usuario_id, perfil_id) VALUES
                                                       (228, 3), (229, 3), (230, 3), (231, 3), -- Secretaria
                                                       (232, 3), (233, 4), (234, 4), (235, 4); -- RH

INSERT INTO funcionarios (cpf, data_nascimento, nome, sobrenome, email, telefone, universidade_id, salario, usuario_id, setor) VALUES
                                                                                                                                   ('77777777741', '1980-10-15 00:00:00.000', 'Marcos', 'Silva', 'marcos.silva@unesp.edu.br', '11987654527', 6, 3200.00, 228, 'Secretaria'),
                                                                                                                                   ('77777777742', '1982-01-20 00:00:00.000', 'Renata', 'Santos', 'renata.santos@unesp.edu.br', '11987654528', 6, 3200.00, 229, 'Secretaria'),
                                                                                                                                   ('77777777743', '1985-08-10 00:00:00.000', 'Eduardo', 'Oliveira', 'eduardo.oliveira@unesp.edu.br', '11987654529', 6, 3200.00, 230, 'Secretaria'),
                                                                                                                                   ('77777777744', '1981-05-25 00:00:00.000', 'Luciana', 'Costa', 'luciana.costa@unesp.edu.br', '11987654530', 6, 3200.00, 231, 'Secretaria'),
                                                                                                                                   ('77777777745', '1983-12-18 00:00:00.000', 'Bruno', 'Lima', 'bruno.lima@unesp.edu.br', '11987654531', 6, 3200.00, 232, 'Secretaria'),
                                                                                                                                   ('77777777746', '1978-04-30 00:00:00.000', 'Patricia', 'Ferreira', 'patricia.ferreira@unesp.edu.br', '11987654532', 6, 3900.00, 233, 'RH'),
                                                                                                                                   ('77777777747', '1980-09-12 00:00:00.000', 'Carlos', 'Almeida', 'carlos.almeida@unesp.edu.br', '11987654533', 6, 3900.00, 234, 'RH'),
                                                                                                                                   ('77777777748', '1982-02-05 00:00:00.000', 'Fernanda', 'Rocha', 'fernanda.rocha@unesp.edu.br', '11987654534', 6, 3900.00, 235, 'RH');

UPDATE aluno SET cr = 0;

-- =====================================================
-- AJUSTE DE USUARIO_PERFIS COM BASE NO EMAIL
-- =====================================================

-- =====================================================
-- DISTRIBUIÇÃO DE UNIVERSIDADE_ID COM BASE NO EMAIL
-- =====================================================

-- UFRJ
UPDATE usuarios SET universidade_id = 1
WHERE email LIKE '%ufrj%' AND id != 1;

-- USP
UPDATE usuarios SET universidade_id = 2
WHERE email LIKE '%usp%' AND id != 1;

-- UNICAMP
UPDATE usuarios SET universidade_id = 3
WHERE email LIKE '%unicamp%' AND id != 1;

-- UERJ
UPDATE usuarios SET universidade_id = 4
WHERE email LIKE '%uerj%' AND id != 1;

-- UFF
UPDATE usuarios SET universidade_id = 5
WHERE email LIKE '%uff%' AND id != 1;

-- UNESP
UPDATE usuarios SET universidade_id = 6
WHERE email LIKE '%unesp%' AND id != 1;

-- =====================================================
-- MATÉRIAS POR UNIVERSIDADE
-- =====================================================

-- =====================================================
-- MATÉRIAS UFRJ (15 matérias)
-- =====================================================
INSERT INTO materias (carga_horaria, codigo, creditos, creditos_necessarios, titulo, universidade_id) VALUES
-- Engenharia Civil
(60, 'EC001', 4, 0, 'Cálculo I', 1),
(60, 'EC002', 4, 0, 'Física I', 1),
(60, 'EC003', 4, 0, 'Química Geral', 1),
(60, 'EC004', 4, 0, 'Desenho Técnico', 1),
(60, 'EC005', 4, 4, 'Cálculo II', 1),
(60, 'EC006', 4, 4, 'Física II', 1),
(60, 'EC007', 4, 4, 'Mecânica dos Solos', 1),
(60, 'EC008', 4, 8, 'Resistência dos Materiais', 1),
(60, 'EC009', 4, 8, 'Hidráulica', 1),
(60, 'EC010', 4, 8, 'Estruturas de Concreto', 1),
-- Medicina
(60, 'MD001', 4, 0, 'Anatomia Humana', 1),
(60, 'MD002', 4, 0, 'Fisiologia Humana', 1),
(60, 'MD003', 4, 0, 'Bioquímica', 1),
(60, 'MD004', 4, 4, 'Patologia Geral', 1),
(60, 'MD005', 4, 4, 'Farmacologia', 1);

-- =====================================================
-- MATÉRIAS USP (18 matérias)
-- =====================================================
INSERT INTO materias (carga_horaria, codigo, creditos, creditos_necessarios, titulo, universidade_id) VALUES
-- Engenharia Civil
(60, 'EC001', 4, 0, 'Cálculo I', 2),
(60, 'EC002', 4, 0, 'Física I', 2),
(60, 'EC003', 4, 0, 'Química Geral', 2),
(60, 'EC004', 4, 0, 'Desenho Técnico', 2),
(60, 'EC005', 4, 4, 'Cálculo II', 2),
(60, 'EC006', 4, 4, 'Física II', 2),
(60, 'EC007', 4, 4, 'Mecânica dos Solos', 2),
(60, 'EC008', 4, 8, 'Resistência dos Materiais', 2),
(60, 'EC009', 4, 8, 'Hidráulica', 2),
(60, 'EC010', 4, 8, 'Estruturas de Concreto', 2),
-- Medicina
(60, 'MD001', 4, 0, 'Anatomia Humana', 2),
(60, 'MD002', 4, 0, 'Fisiologia Humana', 2),
(60, 'MD003', 4, 0, 'Bioquímica', 2),
(60, 'MD004', 4, 4, 'Patologia Geral', 2),
(60, 'MD005', 4, 4, 'Farmacologia', 2),
-- Arquitetura
(60, 'AR001', 4, 0, 'História da Arquitetura', 2),
(60, 'AR002', 4, 0, 'Desenho Arquitetônico', 2),
(60, 'AR003', 4, 4, 'Projeto Arquitetônico', 2);

-- =====================================================
-- MATÉRIAS UNICAMP (16 matérias)
-- =====================================================
INSERT INTO materias (carga_horaria, codigo, creditos, creditos_necessarios, titulo, universidade_id) VALUES
-- Engenharia Civil
(60, 'EC001', 4, 0, 'Cálculo I', 3),
(60, 'EC002', 4, 0, 'Física I', 3),
(60, 'EC003', 4, 0, 'Química Geral', 3),
(60, 'EC004', 4, 0, 'Desenho Técnico', 3),
(60, 'EC005', 4, 4, 'Cálculo II', 3),
(60, 'EC006', 4, 4, 'Física II', 3),
(60, 'EC007', 4, 4, 'Mecânica dos Solos', 3),
(60, 'EC008', 4, 8, 'Resistência dos Materiais', 3),
(60, 'EC009', 4, 8, 'Hidráulica', 3),
(60, 'EC010', 4, 8, 'Estruturas de Concreto', 3),
-- Medicina
(60, 'MD001', 4, 0, 'Anatomia Humana', 3),
(60, 'MD002', 4, 0, 'Fisiologia Humana', 3),
(60, 'MD003', 4, 0, 'Bioquímica', 3),
(60, 'MD004', 4, 4, 'Patologia Geral', 3),
(60, 'MD005', 4, 4, 'Farmacologia', 3),
-- Engenharia Química
(60, 'EQ001', 4, 0, 'Termodinâmica', 3);

-- =====================================================
-- MATÉRIAS UERJ (15 matérias)
-- =====================================================
INSERT INTO materias (carga_horaria, codigo, creditos, creditos_necessarios, titulo, universidade_id) VALUES
-- Engenharia Civil
(60, 'EC001', 4, 0, 'Cálculo I', 4),
(60, 'EC002', 4, 0, 'Física I', 4),
(60, 'EC003', 4, 0, 'Química Geral', 4),
(60, 'EC004', 4, 0, 'Desenho Técnico', 4),
(60, 'EC005', 4, 4, 'Cálculo II', 4),
(60, 'EC006', 4, 4, 'Física II', 4),
(60, 'EC007', 4, 4, 'Mecânica dos Solos', 4),
(60, 'EC008', 4, 8, 'Resistência dos Materiais', 4),
(60, 'EC009', 4, 8, 'Hidráulica', 4),
(60, 'EC010', 4, 8, 'Estruturas de Concreto', 4),
-- Medicina
(60, 'MD001', 4, 0, 'Anatomia Humana', 4),
(60, 'MD002', 4, 0, 'Fisiologia Humana', 4),
(60, 'MD003', 4, 0, 'Bioquímica', 4),
(60, 'MD004', 4, 4, 'Patologia Geral', 4),
(60, 'MD005', 4, 4, 'Farmacologia', 4);

-- =====================================================
-- MATÉRIAS UFF (15 matérias)
-- =====================================================
INSERT INTO materias (carga_horaria, codigo, creditos, creditos_necessarios, titulo, universidade_id) VALUES
-- Engenharia Civil
(60, 'EC001', 4, 0, 'Cálculo I', 5),
(60, 'EC002', 4, 0, 'Física I', 5),
(60, 'EC003', 4, 0, 'Química Geral', 5),
(60, 'EC004', 4, 0, 'Desenho Técnico', 5),
(60, 'EC005', 4, 4, 'Cálculo II', 5),
(60, 'EC006', 4, 4, 'Física II', 5),
(60, 'EC007', 4, 4, 'Mecânica dos Solos', 5),
(60, 'EC008', 4, 8, 'Resistência dos Materiais', 5),
(60, 'EC009', 4, 8, 'Hidráulica', 5),
(60, 'EC010', 4, 8, 'Estruturas de Concreto', 5),
-- Medicina
(60, 'MD001', 4, 0, 'Anatomia Humana', 5),
(60, 'MD002', 4, 0, 'Fisiologia Humana', 5),
(60, 'MD003', 4, 0, 'Bioquímica', 5),
(60, 'MD004', 4, 4, 'Patologia Geral', 5),
(60, 'MD005', 4, 4, 'Farmacologia', 5);

-- =====================================================
-- MATÉRIAS UNESP (15 matérias)
-- =====================================================
INSERT INTO materias (carga_horaria, codigo, creditos, creditos_necessarios, titulo, universidade_id) VALUES
-- Engenharia Civil
(60, 'EC001', 4, 0, 'Cálculo I', 6),
(60, 'EC002', 4, 0, 'Física I', 6),
(60, 'EC003', 4, 0, 'Química Geral', 6),
(60, 'EC004', 4, 0, 'Desenho Técnico', 6),
(60, 'EC005', 4, 4, 'Cálculo II', 6),
(60, 'EC006', 4, 4, 'Física II', 6),
(60, 'EC007', 4, 4, 'Mecânica dos Solos', 6),
(60, 'EC008', 4, 8, 'Resistência dos Materiais', 6),
(60, 'EC009', 4, 8, 'Hidráulica', 6),
(60, 'EC010', 4, 8, 'Estruturas de Concreto', 6),
-- Medicina
(60, 'MD001', 4, 0, 'Anatomia Humana', 6),
(60, 'MD002', 4, 0, 'Fisiologia Humana', 6),
(60, 'MD003', 4, 0, 'Bioquímica', 6),
(60, 'MD004', 4, 4, 'Patologia Geral', 6),
(60, 'MD005', 4, 4, 'Farmacologia', 6);

-- =====================================================
-- RELAÇÕES MATÉRIA-GRADUAÇÃO
-- =====================================================

-- UFRJ - Engenharia Civil (ID 1)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1), (7, 1), (8, 1), (9, 1), (10, 1);

-- UFRJ - Engenharia Elétrica (ID 2)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (1, 2), (2, 2), (3, 2), (4, 2), (5, 2), (6, 2);

-- UFRJ - Engenharia Mecânica (ID 3)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (1, 3), (2, 3), (3, 3), (4, 3), (5, 3), (6, 3), (8, 3);

-- UFRJ - Medicina (ID 4)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (11, 4), (12, 4), (13, 4), (14, 4), (15, 4);

-- UFRJ - Direito (ID 5)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (1, 5), (2, 5), (3, 5);

-- UFRJ - Administração (ID 6)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (1, 6), (2, 6), (3, 6);

-- UFRJ - Ciência da Computação (ID 7)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (1, 7), (2, 7), (3, 7), (5, 7), (6, 7);

-- USP - Engenharia Civil (ID 8)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (16, 8), (17, 8), (18, 8), (19, 8), (20, 8), (21, 8), (22, 8), (23, 8), (24, 8), (25, 8);

-- USP - Engenharia Elétrica (ID 9)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (16, 9), (17, 9), (18, 9), (19, 9), (20, 9), (21, 9);

-- USP - Medicina (ID 10)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (26, 10), (27, 10), (28, 10), (29, 10), (30, 10);

-- USP - Direito (ID 11)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (16, 11), (17, 11), (18, 11);

-- USP - Administração (ID 12)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (16, 12), (17, 12), (18, 12);

-- USP - Ciência da Computação (ID 13)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (16, 13), (17, 13), (18, 13), (20, 13), (21, 13);

-- USP - Arquitetura e Urbanismo (ID 14)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (31, 14), (32, 14), (33, 14), (19, 14), (20, 14), (21, 14);

-- USP - Psicologia (ID 15)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (16, 15), (17, 15), (18, 15);

-- UNICAMP - Engenharia Civil (ID 16)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (34, 16), (35, 16), (36, 16), (37, 16), (38, 16), (39, 16), (40, 16), (41, 16), (42, 16), (43, 16);

-- UNICAMP - Engenharia Elétrica (ID 17)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (34, 17), (35, 17), (36, 17), (37, 17), (38, 17), (39, 17);

-- UNICAMP - Medicina (ID 18)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (44, 18), (45, 18), (46, 18), (47, 18), (48, 18);

-- UNICAMP - Direito (ID 19)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (34, 19), (35, 19), (36, 19);

-- UNICAMP - Administração (ID 20)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (34, 20), (35, 20), (36, 20);

-- UNICAMP - Ciência da Computação (ID 21)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (34, 21), (35, 21), (36, 21), (38, 21), (39, 21);

-- UNICAMP - Engenharia Química (ID 22)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (34, 22), (35, 22), (36, 22), (38, 22), (39, 22), (49, 22);

-- UERJ - Engenharia Civil (ID 23)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (50, 23), (51, 23), (52, 23), (53, 23), (54, 23), (55, 23), (56, 23), (57, 23), (58, 23), (59, 23);

-- UERJ - Medicina (ID 24)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (60, 24), (61, 24), (62, 24), (63, 24), (64, 24);

-- UERJ - Direito (ID 25)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (50, 25), (51, 25), (52, 25);

-- UERJ - Administração (ID 26)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (50, 26), (51, 26), (52, 26);

-- UERJ - Ciência da Computação (ID 27)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (50, 27), (51, 27), (52, 27), (54, 27), (55, 27);

-- UERJ - Psicologia (ID 28)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (50, 28), (51, 28), (52, 28);

-- UFF - Engenharia Civil (ID 29)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (65, 29), (66, 29), (67, 29), (68, 29), (69, 29), (70, 29), (71, 29), (72, 29), (73, 29), (74, 29);

-- UFF - Engenharia Elétrica (ID 30)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (65, 30), (66, 30), (67, 30), (68, 30), (69, 30), (70, 30);

-- UFF - Medicina (ID 31)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (75, 31), (76, 31), (77, 31), (78, 31), (79, 31);

-- UFF - Direito (ID 32)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (65, 32), (66, 32), (67, 32);

-- UFF - Administração (ID 33)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (65, 33), (66, 33), (67, 33);

-- UFF - Ciência da Computação (ID 34)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (65, 34), (66, 34), (67, 34), (69, 34), (70, 34);

-- UNESP - Engenharia Civil (ID 35)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (80, 35), (81, 35), (82, 35), (83, 35), (84, 35), (85, 35), (86, 35), (87, 35), (88, 35), (89, 35);

-- UNESP - Engenharia Elétrica (ID 36)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (80, 36), (81, 36), (82, 36), (83, 36), (84, 36), (85, 36);

-- UNESP - Medicina (ID 37)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (90, 37), (91, 37), (92, 37), (93, 37), (94, 37);

-- UNESP - Direito (ID 38)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (80, 38), (81, 38), (82, 38);

-- UNESP - Administração (ID 39)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (80, 39), (81, 39), (82, 39);

-- UNESP - Ciência da Computação (ID 40)
INSERT INTO materia_graduacao (materia_id, graduacao_id) VALUES
                                                             (80, 40), (81, 40), (82, 40), (84, 40), (85, 40);