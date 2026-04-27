INSERT INTO vendeglatas_etterem (adat) VALUES (
XMLType('<?xml version="1.0" encoding="UTF-8"?>
<vendeglatas>
<etterem ekod="e1"><nev>Trófea</nev>
<csillag>4</csillag></etterem>
<etterem ekod="e2"><nev>gundel</nev>
<cim><varos>Budapest</varos><utca>Visegrád</utca><hazszam>13</hazszam></cim>
<cim><varos>Budapest</varos><utca>Gundel
Károly</utca><hazszam>13</hazszam></cim>
<csillag>5</csillag>
</etterem>
<foszakacs fkod="f1" e_f="e1">
<nev>Havas Péter</nev>
<eletkor>35</eletkor>
<vegzettseg>Szakközépiskola</vegzettseg>
<vegzettseg>Paul Bocuse Institute</vegzettseg>
</foszakacs>
<foszakacs fkod="f2" e_f="e2">
<nev>Nagy Károly</nev>
<eletkor>30</eletkor>
<vegzettseg>Le Cordon Bleu</vegzettseg>
</foszakacs>
<!--  szakacsok  -->
<szakacs szkod="sz1" e_sz="e1">
<nev>Ötlet Elek</nev>
<reszleg>Entremetier</reszleg>
<vegzettseg>Szakközépiskola</vegzettseg>
<vegzettseg>Le Cordon Bleu</vegzettseg>
</szakacs>
<szakacs szkod="sz2" e_sz="e2">
<nev>Gulyás Géza</nev>
<reszleg>Rôtisseur</reszleg>
<vegzettseg>Szakközépiskola</vegzettseg>
</szakacs>
<szakacs szkod="sz3" e_sz="e2">
<nev>Fazekas Ferenc</nev>
<reszleg>Poissonnier</reszleg>
<vegzettseg>Szakközépiskola</vegzettseg>
</szakacs>
<!--  gyakornokok  -->
<gyakornok gykod="gy1" e_gy="e1">
<nev>Kovács László</nev>
<gyakorlat>
<kezdete>2023-01-01</kezdete>
<idotartama>6 hónap</idotartama>
</gyakorlat>
<muszak>délelőtt</muszak>
<muszak>este</muszak>
</gyakornok>
<gyakornok gykod="gy2" e_gy="e2">
<nev>Szűcs Eszter</nev>
<gyakorlat>
<kezdete>2023-02-01</kezdete>
<idotartama>3 hónap</idotartama>
</gyakorlat>
<muszak>délután</muszak>
</gyakornok>
<gyakornok gykod="gy3" e_gy="e2">
<nev>Horváth Péter</nev>
<gyakorlat>
<kezdete>2023-03-01</kezdete>
<idotartama>4 hónap</idotartama>
</gyakorlat>
<muszak>délelőtt</muszak>
</gyakornok>
<!--  vendegek  -->
<vendeg vkod="v1">
<nev>Szabó Anna</nev>
<eletkor>28</eletkor>
<cim>
<varos>Budapest</varos>
<utca>Petőfi Sándor</utca>
<hazszam>10</hazszam>
</cim>
</vendeg>
<vendeg vkod="v2">
<nev>Kiss Péter</nev>
<eletkor>35</eletkor>
<cim>
<varos>Debrecen</varos>
<utca>Kossuth Lajos</utca>
<hazszam>5</hazszam>
</cim>
</vendeg>
<vendeg vkod="v3">
<nev>Nagy Eszter</nev>
<eletkor>22</eletkor>
<cim>
<varos>Szeged</varos>
<utca>Rákóczi Ferenc</utca>
<hazszam>15</hazszam>
</cim>
</vendeg>
<vendeg vkod="v4">
<nev>Farkas Gábor</nev>
<eletkor>40</eletkor>
<cim>
<varos>Győr</varos>
<utca>Ady Endre</utca>
<hazszam>20</hazszam>
</cim>
</vendeg>
<!--  rendelesek  -->
<rendeles e_v_e="e1" e_v_v="v1">
<osszeg>23500</osszeg>
<etel>Gulyásleves</etel>
</rendeles>
<rendeles e_v_e="e2" e_v_v="v2">
<osszeg>18000</osszeg>
<etel>Halászlé</etel>
</rendeles>
<rendeles e_v_e="e2" e_v_v="v3">
<osszeg>15000</osszeg>
<etel>Gulyásleves</etel>
</rendeles>
<rendeles e_v_e="e2" e_v_v="v4">
<osszeg>20000</osszeg>
<etel>Túrós csusza</etel>
</rendeles>
</vendeglatas>')
);
COMMIT;

SELECT x.ekod,
       x.nev,
       x.varos,
       x.utca,
       x.hazszam,
       x.csillag
FROM vendeglatas_etterem v,
     XMLTABLE(
       '/vendeglatas/etterem'
       PASSING v.adat
       COLUMNS
         ekod     VARCHAR2(10)  PATH '@ekod',
         nev      VARCHAR2(200) PATH 'nev',
         varos    VARCHAR2(100) PATH 'cim[1]/varos',
         utca     VARCHAR2(200) PATH 'cim[1]/utca',
         hazszam  VARCHAR2(10)  PATH 'cim[1]/hazszam',
         csillag  NUMBER        PATH 'csillag'
     ) x;

   
 SELECT x.ekod, x.nev, x.varos, x.csillag
 FROM vendeglatas_etterem v,
 XMLTABLE('/vendeglatas/etterem'
 PASSING v.adat
 COLUMNS
 ekod VARCHAR2(10) PATH '@ekod',
 nev VARCHAR2(200) PATH 'nev',
 varos VARCHAR2(100) PATH 'cim[1]/varos',
 csillag NUMBER PATH 'csillag'
  ) x
WHERE x.csillag = 5;


SELECT sz.szkod, sz.nev AS szakacs_nev,
 sz.reszleg, sz.eletkor,
 e.nev AS etterem_nev
 FROM vendeglatas_etterem v,
 XMLTABLE('/vendeglatas/szakacs'
 PASSING v.adat
 COLUMNS
 szkod VARCHAR2(10) PATH '@szkod',
 e_sz VARCHAR2(10) PATH '@e_sz',
 nev VARCHAR2(200) PATH 'nev',
 reszleg VARCHAR2(100) PATH 'reszleg',
 eletkor NUMBER PATH 'eletkor'
 ) sz,
 XMLTABLE('/vendeglatas/etterem'
 PASSING v.adat
 COLUMNS
 ekod VARCHAR2(10) PATH '@ekod',
 nev VARCHAR2(200) PATH 'nev'
 ) e
 WHERE sz.e_sz = e.ekod;

 CREATE OR REPLACE TYPE cim_t AS OBJECT (
    varos VARCHAR2(100),
    utca VARCHAR2(200),
    hazszam VARCHAR2(10)
 );

 CREATE OR REPLACE TYPE etterem_t AS OBJECT (
    ekod VARCHAR2(10),
    nev VARCHAR2(200),
    csillag NUMBER(1),
    cim cim_t,
    MEMBER FUNCTION leiras RETURN VARCHAR2
 );

 CREATE OR REPLACE TYPE BODY etterem_t AS 
    MEMBER FUNCTION leiras RETURN VARCHAR2 IS
    BEGIN
        RETURN SELF.nev || '( '|| SELF.csillag || ' csillag) - ' || 
    SELF.cim.varos;
        END leiras;
       END;

CREATE TABLE ettermek OF etterem_t (
    PRIMARY KEY (ekod)
);

 INSERT INTO ettermek
 SELECT etterem_t(
 x.ekod,
 x.nev,
 x.csillag,
 cim_t(x.varos, x.utca, x.hazszam)
 )
FROM vendeglatas_etterem v,
 XMLTABLE('/vendeglatas/etterem'
 PASSING v.adat
 COLUMNS
 ekod VARCHAR2(10) PATH '@ekod',
 nev VARCHAR2(200) PATH 'nev',
 varos VARCHAR2(100) PATH 'cim[1]/varos',
 utca VARCHAR2(200) PATH 'cim[1]/utca',
 hazszam VARCHAR2(10) PATH 'cim[1]/hazszam',
 csillag NUMBER PATH 'csillag'
 ) x;
  COMMIT;


SELECT e.leiras() AS leiras
    FROM ettermek e;

SELECT e.cim.varos AS varos,
        COUNT(*)   AS db,
        AVG(e.csillag) AS atlag_csillag
    FROM ettermek e
    GROUP BY e.cim.varos
    ORDER BY atlag_csillag DESC;

UPDATE ettermek e
    SET e.csillag = 5
    WHERE e.ekod = 'e1';
   COMMIT;


DELETE FROM ettermek e
    WHERE e.csillag < 3;
    COMMIT;


CREATE OR REPLACE TYPE vegzettseg_va AS VARRAY(5) OF VARCHAR2(200);

CREATE OR REPLACE TYPE szakacs_t AS OBJECT (
    szkod VARCHAR2(10),
    nev VARCHAR2(200),
    reszleg VARCHAR2(100),
    eletkor NUMBER,
    vegzettsegek vegzettseg_va
);

CREATE TABLE szakacsok OF szakacs_t (
    PRIMARY KEY (szkod)
);

INSERT INTO szakacsok VALUES (
    szakacs_t ('sz1', 'Ötlet Elek', 'Saucer', 30, 
    vegzettseg_va('Szaközépiskola', 'Le Cordon Blue'))
);

COMMIT;

 INSERT INTO szakacsok VALUES (
 szakacs_t('sz2','Kocsis Tibor','Entremetier', 30,
 vegzettseg_va('Szakközépiskola','Le Cordon Bleu'))
 );
 COMMIT;

 SELECT s.nev ó, s.reszleg, v.COLUMN_VALUE AS vegzettseg
    FROM szakacsok s,
        TABLE(s.vegzettsegek) v;