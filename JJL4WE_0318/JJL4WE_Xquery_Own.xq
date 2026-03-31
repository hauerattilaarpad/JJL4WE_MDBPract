xquery version "3.1";

(:for $j in /Reptér_JJL4WE/Járat
return <jarat_info>
    <cel>{ $j/célállomás/text() }</cel>
    <allapot>{ $j/státusz/text() }</allapot>
</jarat_info> :)

(:  :for $u in /Reptér_JJL4WE/Utasok
where xs:integer($u/születésiév) > 2000
return $u/név:)

(:  :for $j in /Reptér_JJL4WE/Jegy
order by xs:integer($j/ár) descending
return <jegy_ar>
    <legitarsasag>{ $j/légitársaság/text() }</legitarsasag>
    <ar>{ $j/ár/text() }</ar>
</jegy_ar> :)

(:  :for $d in /Reptér_JJL4WE/Dolgozó
where count($d/telefonszám) > 1
return $d/név :)

(:  :for $u in /Reptér_JJL4WE/Utasok[nem = "Nő"]
return $u/név :)

(:  :for $r in /Reptér_JJL4WE/Reptér
return <repter_hely>
    { concat($r/elhelyezkedés/város, ", ", $r/elhelyezkedés/ország) }
</repter_hely> :)

(:  :for $d in /Reptér_JJL4WE/Dolgozó
where xs:integer($d/fizetés) >= 500000
return <jol_kereso_dolgozo>
    <nev>{ $d/név/text() }</nev>
    <fizetes>{ $d/fizetés/text() }</fizetes>
</jol_kereso_dolgozo> :)

(:  :for $u in /Reptér_JJL4WE/Utasok
let $j := /Reptér_JJL4WE/Jegy[@Jegysorszám = $u/@Jegysorszám]
return <utas_jegy_info>
    <nev>{ $u/név/text() }</nev>
    <fizetett_ar>{ $j/ár/text() }</fizetett_ar>
</utas_jegy_info> :)

(:  :for $sz in /Reptér_JJL4WE/Szolgáltatás
let $d := /Reptér_JJL4WE/Dolgozó[@D_ID = $sz/@D_ID]
let $u := /Reptér_JJL4WE/Utasok[@Útlevél = $sz/@Útlevél]
return <szolgaltatas_reszletek>
    <szolgaltatas_tipus>{ $sz/típus/text() }</szolgaltatas_tipus>
    <kiszolgalo_dolgozo>{ $d/név/text() }</kiszolgalo_dolgozo>
    <kiszolgalt_utas>{ $u/név/text() }</kiszolgalt_utas>
</szolgaltatas_reszletek> :)

let $atlag := avg(/Reptér_JJL4WE/Jegy/ár)
return <atlagos_jegyar>{ $atlag }</atlagos_jegyar>
