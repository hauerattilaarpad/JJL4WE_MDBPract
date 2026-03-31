xquery version "3.1";
(:
doc("/db/etterem.xml")//vendeg

for $s in doc("/db/etterem.xml")//szakacs 
where $s/vegzettseg = "Szakközépiskola”
return 
<szakacs>  
<SzakacsID>{data($s/@id)}</SzakacsID>  
<Nev>{data($s/nev)}</Nev>  
<Reszleg>{data($s/reszleg)}</Reszleg>
<Vegzettsegek>{$s/vegzettseg}</Vegzettsegek>
</szakacs>

for $e in doc("/db/etterem.xml")//etterem
where $e/csillag = 5
return
<etterem>
<EtteremID>{data($e/@id)}</EtteremID>
<Nev>{data($e/nev)}</Nev>
<Cim>{$e/cim}</Cim>
<Csillag>{data($e/csillag)}</Csillag>
</etterem>

for $g in doc("/db/etterem.xml")//gyakornokwhere
$g/muszak = "délután” 
return
<gyakornok>
<GyakornokID>{data($g/@id)}</GyakornokID>
<Nev>{data($g/nev)}</Nev>
<GyakorlatKezdete>{data($g/gyakorlat/kezdete)}</GyakorlatKezdete>
<Muszak>{data($g/muszak)}</Muszak>
</gyakornok>

for $v in doc("/db/etterem.xml")//vendeg 
let $rendelesek :=  doc("/db/etterem.xml")//rendeles
[@e_v_v = $v/@vkod]
for $r in $rendelesek
return
<adat>
<nev>{$v/nev}</nev>
<osszeg>{$r/osszeg}</osszeg>
</adat>

for $r in doc("/db/etterem.xml")//rendeles
return
update value $r/osszeg with $r/osszeg + 1000

count(doc("/db/etterem.xml")//rendeles)

sum(doc("/db/etterem.xml")//rendeles/osszeg)

:)

avg(doc("/db/etterem.xml")//rendeles/osszeg)