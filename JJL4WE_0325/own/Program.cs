using System;
using System.Linq;
using System.Xml.Linq;

    XDocument dokumentum = XDocument.Load("JJL4WE_XML1.xml");
            XElement gyoker = dokumentum.Descendants("Reptér_JJL4WE").First();

            Console.WriteLine("(0.) A teljes dokumentum:\n\n" + gyoker);
            Console.WriteLine("\n(1.) A \"várható\" státuszú járatok célállomásai:\n");
            var varhatoJaratok = gyoker.Descendants("Járat")
                .Where(elem => elem.Descendants("státusz").First().Value == "várható")
                .ToList();

            varhatoJaratok.ForEach(elem =>
                Console.WriteLine(" - " + elem.Descendants("célállomás").First().Value)
            );

            Console.WriteLine("\n(2.) Melyik utas, hova utazik és mennyibe került a jegye:\n");

            var harmasJoin = gyoker.Descendants("Utasok")
                .Select(utasElem =>
                {

                    var utasNev = utasElem.Descendants("név").First().Value;
                    var jaratSzam = utasElem.Attribute("Járatszám").Value;
                    var jarat = gyoker.Descendants("Járat")
                        .First(j => j.Attribute("Járatszám").Value == jaratSzam)
                        .Descendants("célállomás").First().Value;

                    var jegySorszam = utasElem.Attribute("Jegysorszám").Value;
                    var jegyAr = gyoker.Descendants("Jegy")
                        .First(j => j.Attribute("Jegysorszám").Value == jegySorszam)
                        .Descendants("ár").First().Value;

                    return new
                    {
                        Utas = utasNev,
                        Celallomas = jarat,
                        Ar = jegyAr
                    };
                })
                .ToList();

            harmasJoin.ForEach(j =>
                Console.WriteLine($"{j.Utas} - {j.Celallomas} - {j.Ar} Ft")
            );

            var atlagJegyAr = gyoker.Descendants("Jegy")
                .Select(j => j.Descendants("ár").First().Value)
                .Average(ar => double.Parse(ar));

            Console.WriteLine($"\n(3.) Az átlagos jegyár: {atlagJegyAr:F0} Ft");

            Console.WriteLine("\n(4.) Minden jegy árát megduplázom és mentem:\n");

            gyoker.Descendants("Jegy")
                .ToList()
                .ForEach(j =>
                {
                    var arElem = j.Descendants("ár").First();
                    var ar = double.Parse(arElem.Value);
                    arElem.Value = (ar * 2).ToString();
                });

            new XDocument(gyoker).Save("repter_modositott.xml");
            Console.WriteLine("repter_modositott.xml létrehozva");

            Console.WriteLine("\n(5.) Törlöm a késő ('késik' státuszú) járatokat és mentem:\n");

            gyoker.Descendants("Járat")
                .Where(j => j.Descendants("státusz").First().Value == "késik")
                .ToList()
                .ForEach(j => j.Remove());

            new XDocument(gyoker).Save("repter_torolt.xml");
            Console.WriteLine("repter_torolt.xml létrehozva");
