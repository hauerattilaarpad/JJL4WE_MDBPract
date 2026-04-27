using System;
using System.Linq;
using System.Xml.Linq;


        XDocument dokumentum = XDocument.Load("etterem.xml");
        XElement gyoker = dokumentum.Descendants("vendeglatas").First();

        Console.WriteLine("(0.) A teljes dokumentum:\n\n" + gyoker);

        Console.WriteLine("(1.) Az ötcsillagos éttermek:\n");
        var otCsillagosEttermek = gyoker.Descendants("etterem")
            .Where(elem => elem.Descendants("csillag").First().Value == "5")
            .ToList();

        otCsillagosEttermek.ForEach(elem =>
            Console.WriteLine(" - " + elem.Descendants("nev").First().Value)
        );

        Console.WriteLine("\n(2.) Melyik vendég, melyik étteremben, mit rendelt, mennyiért:\n");

        var harmasJoin = gyoker.Descendants("rendeles")
            .Select(elem =>
            {
                var vendegID = elem.Attribute("e_v_v").Value;
                var vendeg = gyoker.Descendants("vendeg")
                    .First(v => v.Attribute("vkod").Value == vendegID)
                    .Descendants("nev").First().Value;

                var etteremID = elem.Attribute("e_v_e").Value;
                var etterem = gyoker.Descendants("etterem")
                    .First(e => e.Attribute("ekod").Value == etteremID)
                    .Descendants("nev").First().Value;

                var rendeltEtel = elem.Descendants("etel").First().Value;
                var osszeg = elem.Descendants("osszeg").First().Value;

                return new
                {
                    Vendeg = vendeg,
                    Etterem = etterem,
                    Etel = rendeltEtel,
                    Osszeg = osszeg
                };
            })
            .ToList();

        harmasJoin.ForEach(j =>
            Console.WriteLine($"{j.Vendeg} - {j.Etterem} - {j.Etel} - {j.Osszeg}")
        );

        var atlagKoltes = gyoker.Descendants("rendeles")
            .Select(r => r.Descendants("osszeg").First().Value)
            .Average(o => double.Parse(o));

        Console.WriteLine($"\n(3.) Az átlagos költés: {atlagKoltes}");

        Console.WriteLine("\n(4.) Minden rendelés összegét megduplázom és mentem:\n");

        gyoker.Descendants("rendeles")
            .ToList()
            .ForEach(r =>
            {
                var osszegElem = r.Descendants("osszeg").First();
                var osszeg = double.Parse(osszegElem.Value);
                osszegElem.Value = (osszeg * 2).ToString();
            });

        new XDocument(gyoker).Save("etterem_modositott.xml");
        Console.WriteLine("etterem_modositott.xml létrehozva");

        Console.WriteLine("\n(5.) Törlöm a 3 csillagos éttermeket és mentem:\n");

        gyoker.Descendants("etterem")
            .Where(e => e.Descendants("csillag").First().Value == "3")
            .ToList()
            .ForEach(e => e.Remove());

        new XDocument(gyoker).Save("etterem_torolt.xml");
        Console.WriteLine("etterem_torolt.xml létrehozva");
