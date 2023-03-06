INSERT INTO roles(id, name) VALUES(1, 'ROLE_ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO roles(id, name) VALUES(2, 'ROLE_MEMBER') ON CONFLICT DO NOTHING;

INSERT INTO login_user(id, name, email, password) VALUES(1, '管理者', 'admin@example.com', '$2a$10$SJTWvNl16fCU7DaXtWC0DeN/A8IOakpCkWWNZ/FKRV2CHvWElQwMS') ON CONFLICT DO NOTHING;

INSERT INTO user_role(user_id, role_id) VALUES(1, 1) ON CONFLICT DO NOTHING;

INSERT INTO studio_member(
    id,
    name,
    furigana,
    address,
    phone,
    email,
    artist_name,
    member_count,
    note,
    registration_date)
VALUES(1,
       '管理者',
       'カンリシャ',
       '南国',
       '000-0000-0000',
       'admin@example.com',
       'master',
       1,
       '管理者',
       '2023-02-28 13:00:00.0') ON CONFLICT DO NOTHING;
