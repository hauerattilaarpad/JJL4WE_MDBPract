CREATE OR REPLACE TYPE szemely_t AS OBJECT (
    nev VARCHAR2(100),
    MEMBER FUNCTION bemutatkozas RETURN VARCHAR2,
    STATIC FUNCTION minimalis_eletkor RETURN INTEGER,
    ORDER MEMBER FUNCTION nev_szerint_rendezes (other szemely_t) RETURN INTEGER
) NOT FINAL;
/

CREATE OR REPLACE TYPE BODY szemely_t AS
    MEMBER FUNCTION bemutatkozas RETURN VARCHAR2 IS
    BEGIN
        RETURN 'A nevem: ' || SELF.nev;
    END;

    STATIC FUNCTION minimalis_eletkor RETURN INTEGER IS
    BEGIN
        RETURN 0;
    END;

    ORDER MEMBER FUNCTION nev_szerint_rendezes (other szemely_t) RETURN INTEGER IS
    BEGIN
        RETURN CASE 
            WHEN SELF.nev < other.nev THEN -1
            WHEN SELF.nev > other.nev THEN 1
            ELSE 0
        END;
    END;
END;
/

CREATE OR REPLACE TYPE utas_t UNDER szemely_t (
    utlevel_szam VARCHAR2(20),
    szuletesi_ev NUMBER,
    lakcim VARCHAR2(200),
    OVERRIDING MEMBER FUNCTION bemutatkozas RETURN VARCHAR2
);
/

CREATE OR REPLACE TYPE BODY utas_t AS
    OVERRIDING MEMBER FUNCTION bemutatkozas RETURN VARCHAR2 IS
    BEGIN
        RETURN 'Utas vagyok. Nev: ' || SELF.nev || ', Útlevél: ' || SELF.utlevel_szam || ', Szül: ' || SELF.szuletesi_ev;
    END;
END;
/


CREATE OR REPLACE TYPE dolgozo_t UNDER szemely_t (
    d_id VARCHAR2(20),
    munkakor VARCHAR2(50),
    fizetes NUMBER,
    OVERRIDING MEMBER FUNCTION bemutatkozas RETURN VARCHAR2
);
/

CREATE OR REPLACE TYPE BODY dolgozo_t AS
    OVERRIDING MEMBER FUNCTION bemutatkozas RETURN VARCHAR2 IS
    BEGIN
        RETURN 'Repülőtéri dolgozó vagyok. Nev: ' || SELF.nev || ', Munkakör: ' || SELF.munkakor || ', Fizetés: ' || SELF.fizetes;
    END;
END;
/


CREATE TABLE repter_szemelyek_tbl OF szemely_t;


INSERT INTO repter_szemelyek_tbl
SELECT utas_t(x.nev, x.utlevel, x.szulev, x.lakcim)
FROM repter_xml_table r,
     XMLTABLE('/Reptér_JJL4WE/Utasok'
        PASSING r.xml_adat
        COLUMNS
            utlevel VARCHAR2(20)  PATH '@Útlevél',
            nev     VARCHAR2(100) PATH 'név',
            szulev  NUMBER        PATH 'születésiév',
            lakcim  VARCHAR2(200) PATH 'lakcim'
     ) x;


INSERT INTO repter_szemelyek_tbl
SELECT dolgozo_t(x.nev, x.d_id, x.munkakor, x.fizetes)
FROM repter_xml_table r,
     XMLTABLE('/Reptér_JJL4WE/Dolgozó'
        PASSING r.xml_adat
        COLUMNS
            d_id     VARCHAR2(20)  PATH '@D_ID',
            nev      VARCHAR2(100) PATH 'név',
            munkakor VARCHAR2(50)  PATH 'munkakör',
            fizetes  NUMBER        PATH 'fizetés'
     ) x;

SELECT szemely_t.minimalis_eletkor() AS kor_hatar FROM DUAL;

SELECT s.nev, s.bemutatkozas() AS info
FROM repter_szemelyek_tbl s
ORDER BY VALUE(s);

SELECT s.nev
FROM repter_szemelyek_tbl s
WHERE VALUE(s) IS OF (dolgozo_t);

SELECT s.nev, 
       TREAT(VALUE(s) AS utas_t).szuletesi_ev AS szuletett
FROM repter_szemelyek_tbl s
WHERE VALUE(s) IS OF (utas_t);

SELECT s.nev, 
       TREAT(VALUE(s) AS dolgozo_t).fizetes AS havi_ber
FROM repter_szemelyek_tbl s
WHERE VALUE(s) IS OF (dolgozo_t);