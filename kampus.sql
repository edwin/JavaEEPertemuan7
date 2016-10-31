DROP TABLE dosen;
CREATE TABLE dosen ( iddosen varchar(10) NOT NULL, namadosen varchar(20), PRIMARY KEY (iddosen) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
DROP TABLE matakuliah;
CREATE TABLE matakuliah ( kodematakuliah varchar(10) NOT NULL, iddosen varchar(10) NOT NULL, namamatakuliah varchar(20), PRIMARY KEY (kodematakuliah) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
ALTER TABLE matakuliah ADD CONSTRAINT fk1 FOREIGN KEY (iddosen) REFERENCES dosen (iddosen) ON DELETE CASCADE ON UPDATE CASCADE;
