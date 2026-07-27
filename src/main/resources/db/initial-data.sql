-- Datos iniciales de seguridad y administración del sistema.
-- Se crean permisos, roles, relaciones de acceso y el usuario
-- administrador por defecto para la configuración inicial.

INSERT INTO permission (id, resource, action) VALUES (1, '/gymnast/list.xhtml', 'ALL');
INSERT INTO permission (id, resource, action) VALUES (2, '/competition/list.xhtml', 'ALL');
INSERT INTO permission (id, resource, action) VALUES (3, '/competition/categories.xhtml', 'ALL');
INSERT INTO permission (id, resource, action) VALUES (4, '/admin/judges.xhtml', 'ALL');
INSERT INTO permission (id, resource, action) VALUES (5, '/judge/evaluate.xhtml', 'ALL');
INSERT INTO permission (id, resource, action) VALUES (6, '/judge/myScores.xhtml', 'ALL');
INSERT INTO permission (id, resource, action) VALUES (7, '/judge/ranking.xhtml', 'READ');
INSERT INTO permission (id, resource, action) VALUES (8, '/security/userList.xhtml', 'ALL');

INSERT INTO role (id, name, description) VALUES (1, 'ADMINISTRADOR', 'Administrador del sistema');
INSERT INTO role (id, name, description) VALUES (2, 'JUEZ', 'Juez calificador');

INSERT INTO role_permission (role_id, permission_id) VALUES (1, 1);
INSERT INTO role_permission (role_id, permission_id) VALUES (1, 2);
INSERT INTO role_permission (role_id, permission_id) VALUES (1, 3);
INSERT INTO role_permission (role_id, permission_id) VALUES (1, 4);
INSERT INTO role_permission (role_id, permission_id) VALUES (1, 7);
INSERT INTO role_permission (role_id, permission_id) VALUES (1, 8);
INSERT INTO role_permission (role_id, permission_id) VALUES (2, 5);
INSERT INTO role_permission (role_id, permission_id) VALUES (2, 6);
INSERT INTO role_permission (role_id, permission_id) VALUES (2, 7);

INSERT INTO person (id, person_type, national_id, first_name, last_name) VALUES (1, 'ADMINISTRATOR', '0000000001', 'Administrador', 'Sistema');
INSERT INTO app_user (id, username, password) VALUES (1, 'admin', 'j1c37tPLfzwORcP3Fzb2Ig==');
INSERT INTO administrator (id, user_id) VALUES (1, 1);
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);

COMMIT;
