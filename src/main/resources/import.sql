insert into categories (id, name)
values (1, 'Fruit'),
       (2, 'Vegetables'),
       (3, 'Sweets'),
       (4, 'Dairy'),
       (5, 'Meat'),
       (6, 'Bakery'),
       (7, 'Drink');

insert into products (id, name, price, category_id)
values (1, 'Mutsu apple (1kg)', 2.29, 1),
       (2, 'Coca-cola (0.5l)', 1.42, 7),
       (3, 'Fanta (1.5l)', 2.54, 7),
       (4, 'Bread "Vkusny" (450g)', 1.19, 6),
       (5, 'Frozen donut "Berlinsky" (60g)', 0.94, 3),
       (6, 'Chocolate nestle (82g)', 2.62, 3),
       (7, 'Milk "Svezhee" (1l)', 1.81, 4),
       (8, 'Condensed milk "Hlubokoye" (380g)', 3.22, 4),
       (9, 'White cabbage (1kg)', 0.85, 2),
       (10, 'Potato (1kg)', 1.95, 2);

insert into departments (id, address)
values (1, 'Minsk, ul. Bobruiskaya, 9'),
       (2, 'Minsk, ul. Yesenina, 2'),
       (3, 'Gomel, ul. Ozyornaya, 76');

insert into product_warehouse (department_id, quantity, product_id)
values (1, 29, 1),
       (1, 39, 2),
       (1, 53, 3),
       (1, 53, 4),
       (1, 15, 5),
       (1, 12, 6),
       (1, 21, 7),
       (1, 3, 8),
       (1, 2, 9),
       (1, 8, 10),
       (2, 12, 1),
       (2, 45, 2),
       (2, 87, 3),
       (2, 10, 4),
       (2, 2, 8),
       (2, 8, 9),
       (2, 18, 10),
       (3, 14, 1),
       (3, 35, 2),
       (3, 25, 3),
       (3, 14, 4),
       (3, 85, 5),
       (3, 12, 6),
       (3, 21, 7),
       (3, 3, 8),
       (3, 6, 9),
       (3, 18, 10);

/*password is "root" for all rows */
insert into users (id, email, encrypted_password, first_name, registration_date, role, user_id)
values (1, 'admin1@test.com', '$2a$12$CdXmXIMpnyiyL8zVpbGSHu7JzpqXetT.8keWJxv7HDD9jlyAtuEpu',
        'Kiryl', now(), 'ROLE_ADMIN', 'HBOpigA8m00zc5Z'),
       (2, 'admin2@test.com', '$2a$12$CdXmXIMpnyiyL8zVpbGSHu7JzpqXetT.8keWJxv7HDD9jlyAtuEpu',
        'Anton', now(), 'ROLE_ADMIN', 'kiQ1s2IxzFnp86P'),
       (3, 'dbeditor@test.com', '$2a$12$CdXmXIMpnyiyL8zVpbGSHu7JzpqXetT.8keWJxv7HDD9jlyAtuEpu',
        'Alexei', now(), 'ROLE_DB_EDITOR', 'uXf6gx7WmC9bqAq'),
       (4, 'dephead@test.com', '$2a$12$CdXmXIMpnyiyL8zVpbGSHu7JzpqXetT.8keWJxv7HDD9jlyAtuEpu',
        'Mikhail', now(), 'ROLE_DEPARTMENT_HEAD', 'ed0t5iYzoCqpN5Z'),
       (5, 'collector1@test.com', '$2a$12$CdXmXIMpnyiyL8zVpbGSHu7JzpqXetT.8keWJxv7HDD9jlyAtuEpu',
        'Denis', now(), 'ROLE_COLLECTOR', 'ccuRZwJRnySB9As'),
       (6, 'courier1@test.com', '$2a$12$CdXmXIMpnyiyL8zVpbGSHu7JzpqXetT.8keWJxv7HDD9jlyAtuEpu',
        'Nikolay', now(), 'ROLE_COURIER', 'f50jqzAEhbWbGHO');

insert into employees (id, department_id)
values (4, 1),
       (5, 1),
       (6, 1);