CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- db layer testing schema
create schema test;
CREATE TABLE test.compound_key_test (
    part1 integer NOT NULL,
    part2 integer NOT NULL,
    val text
);
CREATE TABLE test.lot (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    address text NOT NULL
);
CREATE TABLE test.space (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    lot_id uuid NOT NULL,
    number text
);
ALTER TABLE ONLY test.compound_key_test
    ADD CONSTRAINT compound_key_test_pk PRIMARY KEY (part1, part2);
ALTER TABLE ONLY test.lot
    ADD CONSTRAINT lot_pk PRIMARY KEY (id);
ALTER TABLE ONLY test.space
    ADD CONSTRAINT space_pk PRIMARY KEY (id);
ALTER TABLE ONLY test.space
    ADD CONSTRAINT space_lot_id_fk FOREIGN KEY (lot_id) REFERENCES test.lot(id);

INSERT INTO test.compound_key_test VALUES (1, 3, '1-3!'), (4, 6, null);

INSERT INTO test.lot (id, address) VALUES
    ('f0000000-0000-0000-0001-000000000001', '1 database way'),
    ('f0000000-0000-0000-0001-000000000002', '3 database way'),
    ('f0000000-0000-0000-0001-000000000003', '0 spaces road');

INSERT INTO test.space(id, lot_id, number) VALUES
    ('f0000000-0000-0000-0002-000000000001', 'f0000000-0000-0000-0001-000000000001', '1'),
    ('f0000000-0000-0000-0002-000000000002', 'f0000000-0000-0000-0001-000000000002', '1'),
    ('f0000000-0000-0000-0002-000000000003', 'f0000000-0000-0000-0001-000000000002', '2'),
    ('f0000000-0000-0000-0002-000000000004', 'f0000000-0000-0000-0001-000000000002', '3');
