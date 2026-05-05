CREATE OR REPLACE TYPE szemely_t AS OBJECT (
    nev VARCHAR2(100),

    MEMBER FUNCTION bemutatkozas RETURN VARCHAR2,
    STATIC FUNCTION minimalis_eletkor RETURN INTEGER,
    ORDER MEMBER FUNCTION nev_szerint_rendezes (other szemely_t) RETURN INTEGER
) NOT FINAL;

CREATE OR REPLACE TYPE BODY szemely_t AS 

    MEMBER FUNCTION bemutatkozas RETURN INTEGER IS
    BEGIN
        RETURN 'A nevem: ' || SELF.nev;
    END;

    STATIC FUNCTION minimalis_eletkor RETURN INTEGER IS
    BEGIN
        RETURN 18;
    END;

    ORDER MEMBER FUNCTION nev_szerint_rendezes (other szemely_t)
        RETURN INTEGER IS
    BEGIN
        RETURN CASE 
            WHEN SELF.nev < other.nev THEN RETURN -1;
            WHEN SELF.nev > other.nev THEN RETURN 1;
            ELSE 0;
        END;
    END;
  
END;



CREATE OR REPLACE TYPE foszakacs_t UNDER szemely_t (
    fkod VARCHAR2(20),
    eletkor NUMBER,

    OVERRIDING MEMBER FUNCTION bemutatkozas RETURN VARCHAR2
);


CREATE OR REPLACE TYPE BODY foszakacs_t AS

    OVERRIDING MEMBER FUNCTION bemutatkozas RETURN VARCHAR2 IS
    BEGIN
        RETURN 'Főszakács vagyok. A nevem: ' || SELF.nev ||
               ', korom: ' || SELF.eletkor;
    END;

END;


CREATE OR REPLACE TYPE vendeg_t UNDER szemely_t (
    vkod     VARCHAR2(20),
    eletkor  NUMBER,

    OVERRIDING MEMBER FUNCTION bemutatkozas RETURN VARCHAR2
);

CREATE OR REPLACE TYPE BODY vendeg_t AS

    OVERRIDING MEMBER FUNCTION bemutatkozas RETURN VARCHAR2 IS
    BEGIN
        RETURN 'Vendég vagyok. A nevem: ' || SELF.nev;
    END;

END;


CREATE TABLE etterem_szemelyek_tbl OF szemely_t;

INSERT INTO etterem_szemelyek_tbl
SELECT vendeg_t(x.nev, x.vkod, x.eletkor)
FROM vendeglatas_etterem v,
    XMLTABLE('/vendeglatas/vendeg'
    PASSING v.adat
    COLUMNS
        vkod VARCHAR2(10) PATH '@vkod',
        nev VARCHAR2(100) PATH 'nev',
        eletkor MUMBER PATH 'eletkor'
    ) x;


--statikus fgv hívás
SELECT szemely_t.minimalis_eletkor() AS min_eletkor FROM DUAL;


-- poloimorf fv hívások és abc rendezés
SELECT s.nev, s.bemutatkozas() AS bemutatkozas
FROM etterem_szemelyek_tbl s
ORDER BY VALUE(s);

--eredmeny szures vendegekre
SELECT s.nev 
FROM etterem_szemelyek_tbl s
WHERE VALUE(s) IS OF (vendeg_t);


--tipus konverzio altipusra
SELECT s.nev, TREAT(VALUE(s) AS vendeg_t).eletkor AS eletkor
FROM etterem_szemelyek_tbl s
WHERE VALUE(s) IS OF (vendeg_t);


