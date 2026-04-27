
SELECT x.jaratszam,
       x.celallomas,
       x.indulas,
       x.erkezes,
       x.statusz
FROM JJL4WE_OWN_XML r,
     XMLTABLE(
       '/Reptér_JJL4WE/Járat'
       PASSING r.adat
       COLUMNS
         jaratszam  VARCHAR2(10)  PATH '@Járatszám',
         celallomas VARCHAR2(100) PATH 'célállomás',
         indulas    VARCHAR2(50)  PATH 'indulási_idő',
         erkezes    VARCHAR2(50)  PATH 'érkezési_idő',
         statusz    VARCHAR2(50)  PATH 'státusz'
     ) x;


SELECT x.jaratszam, x.celallomas, x.statusz
FROM JJL4WE_OWN_XML r,
XMLTABLE('/Reptér_JJL4WE/Járat'
 PASSING r.adat
 COLUMNS
 jaratszam VARCHAR2(10) PATH '@Járatszám',
 celallomas VARCHAR2(100) PATH 'célállomás',
 statusz VARCHAR2(50) PATH 'státusz'
 ) x
WHERE x.statusz = 'várható';

SELECT u.utlevel, u.nev AS utas_nev, u.szuletesi_ev, 
       j.ar AS jegy_ara, j.legitarsasag
FROM JJL4WE_OWN_XML r,
XMLTABLE('/Reptér_JJL4WE/Utasok'
 PASSING r.adat
 COLUMNS
 utlevel VARCHAR2(20) PATH '@Útlevél',
 jegysorszam VARCHAR2(20) PATH '@Jegysorszám',
 nev VARCHAR2(200) PATH 'név',
 szuletesi_ev NUMBER PATH 'születésiév'
 ) u,
XMLTABLE('/Reptér_JJL4WE/Jegy'
 PASSING r.adat
 COLUMNS
 jegysorszam VARCHAR2(20) PATH '@Jegysorszám',
 ar NUMBER PATH 'ár',
 legitarsasag VARCHAR2(100) PATH 'légitársaság'
 ) j
WHERE u.jegysorszam = j.jegysorszam;


CREATE OR REPLACE TYPE uticel_t AS OBJECT (
    repternev VARCHAR2(100),
    orszag VARCHAR2(100)
);


CREATE OR REPLACE TYPE jegy_t AS OBJECT (
    jegysorszam VARCHAR2(20),
    ar NUMBER,
    legitarsasag VARCHAR2(100),
    uticel uticel_t,
    MEMBER FUNCTION leiras RETURN VARCHAR2
);


CREATE OR REPLACE TYPE BODY jegy_t AS 
    MEMBER FUNCTION leiras RETURN VARCHAR2 IS
    BEGIN
        RETURN SELF.legitarsasag || ' járat, Úticél: ' || SELF.uticel.repternev || 
               ' (' || SELF.uticel.orszag || ') - Ár: ' || SELF.ar || ' Ft';
    END leiras;
END;


CREATE TABLE jegyek_obj_tabla OF jegy_t (
    PRIMARY KEY (jegysorszam)
);

INSERT INTO jegyek_obj_tabla
SELECT jegy_t(
 x.jegysorszam,
 x.ar,
 x.legitarsasag,
 uticel_t(x.repternev, x.orszag)
)
FROM JJL4WE_OWN_XML r,
 XMLTABLE('/Reptér_JJL4WE/Jegy'
 PASSING r.adat
 COLUMNS
 jegysorszam VARCHAR2(20) PATH '@Jegysorszám',
 ar NUMBER PATH 'ár',
 legitarsasag VARCHAR2(100) PATH 'légitársaság',
 repternev VARCHAR2(100) PATH 'uticél/reptérnév',
 orszag VARCHAR2(100) PATH 'uticél/ország'
 ) x;
COMMIT;

SELECT j.leiras() AS jegy_leiras
    FROM jegyek_obj_tabla j;

SELECT j.uticel.orszag AS orszag,
       COUNT(*)        AS darab,
       AVG(j.ar)       AS atlag_ar
    FROM jegyek_obj_tabla j
    GROUP BY j.uticel.orszag
    ORDER BY atlag_ar DESC;

UPDATE jegyek_obj_tabla j
    SET j.ar = 45000
    WHERE j.jegysorszam = '1111';
COMMIT;

DELETE FROM jegyek_obj_tabla j
    WHERE j.ar < 25000;
COMMIT;

CREATE OR REPLACE TYPE telefonszam_va AS VARRAY(5) OF VARCHAR2(20);
/

CREATE OR REPLACE TYPE utas_t AS OBJECT (
    utlevel VARCHAR2(20),
    nev VARCHAR2(200),
    nem VARCHAR2(20),
    szuletesi_ev NUMBER,
    telefonszamok telefonszam_va
);
/

CREATE TABLE utasok_tabla OF utas_t (
    PRIMARY KEY (utlevel)
);

INSERT INTO utasok_tabla VALUES (
    utas_t ('98765432', 'Hauer Attila', 'Férfi', 2002, 
    telefonszam_va('+36201234567', '+36701234567'))
);
COMMIT;

INSERT INTO utasok_tabla VALUES (
    utas_t('87654367','Olivia Thompson','Nő', 2001,
    telefonszam_va('+36209876428','+36702138757', '+36705834653'))
);
COMMIT;


SELECT u.nev AS utas_neve, u.nem, t.COLUMN_VALUE AS telefonszam
    FROM utasok_tabla u,
         TABLE(u.telefonszamok) t;