using RepterApp.Models;
using MongoDB.Driver;

class Program
{
    static void Main(string[] args)
    {
        var client = new MongoClient("mongodb://localhost:27017");
        var database = client.GetDatabase("repter_db");
        var jaratCollection = database.GetCollection<Jarat>("jaratok");
        var utasCollection = database.GetCollection<Utas>("utasok");

        // 1. Összes járat listázása
        Console.WriteLine("--- Járatok listája ---");
        var jaratok = jaratCollection.Find(_ => true).ToList();
        foreach (var j in jaratok)
        {
            Console.WriteLine($"Szám: {j.Jaratszam} | Cél: {j.celallomas} | Státusz: {j.statusz}");
        }

        // 2. Új utas hozzáadása (Insert)
        var ujUtas = new Utas
        {
            nev = "Teszt Elek",
            szuletesiev = "1995",
            nem = "Férfi",
            lakcim = "1234 Budapest, Példa u. 1.",
            telefonszam = "+361234567",
            Utlevel = "AB123456",
            Jegysorszam = "9999",
            Jaratszam = "111"
        };
        utasCollection.InsertOne(ujUtas);
        Console.WriteLine("\nÚj utas hozzáadva.");

        var ujUtasok = new List<Utas>
            {
                new Utas
                {
                    nev = "Kovács János",
                    szuletesiev = "1985",
                    nem = "Férfi",
                    lakcim = "Magyarország, 1051 Budapest, Erzsébet tér 3.",
                    telefonszam = "+36301112233",
                    Utlevel = "KA123456",
                    Jegysorszam = "1114",
                    Jaratszam = "112"
                },
                new Utas
                {
                    nev = "Nagy Szabina",
                    szuletesiev = "1998",
                    nem = "Nő",
                    lakcim = "Magyarország, 4024 Debrecen, Piac utca 10.",
                    telefonszam = new List<string> { "+36205556677", "+36708889900" },
                    Utlevel = "NS789012",
                    Jegysorszam = "1115",
                    Jaratszam = "113"
                }
            };

            utasCollection.InsertMany(ujUtasok);
            Console.WriteLine("\nKét új utas sikeresen hozzáadva (InsertMany).");    

        // 3. Járat státuszának frissítése (Update)
        var filterJarat = Builders<Jarat>.Filter.Eq(j => j.Jaratszam, "111");
        var updateStatusz = Builders<Jarat>.Update.Set(j => j.statusz, "indult");
        jaratCollection.UpdateOne(filterJarat, updateStatusz);
        Console.WriteLine("Járat státusza frissítve.");

        // 4. Régi utasok törlése (Delete) - pl. 1980 előtt születettek
        var regiUtasok = Builders<Utas>.Filter.Lt(u => u.szuletesiev, "1980");
        utasCollection.DeleteMany(regiUtasok);
        Console.WriteLine("1980 előtt született utasok törölve.");

        // 5. Összetett lekérdezés: Budapest célállomás VAGY késő járatok
        Console.WriteLine("\n--- Kiemelt járatok (Budapest vagy Késik) ---");
        var szures = Builders<Jarat>.Filter.Eq(j => j.celallomas, "Budapest") | 
                     Builders<Jarat>.Filter.Eq(j => j.statusz, "késik");
        
        var kiemeltJaratok = jaratCollection.Find(szures).ToList();
        foreach (var j in kiemeltJaratok)
        {
            Console.WriteLine($"Cél: {j.celallomas}, Státusz: {j.statusz}");
        }

        // 6. Aggregáció: Utasok száma járatonként
        Console.WriteLine("\n--- Utasok száma járatonként ---");
        var utasStat = utasCollection.Aggregate()
            .Group(u => u.Jaratszam, g => new
            {
                JaratSzam = g.Key,
                UtasSzam = g.Count()
            })
            .ToList();

        foreach (var stat in utasStat)
        {
            Console.WriteLine($"Járat: {stat.JaratSzam}, Utasok: {stat.UtasSzam}");
        }

        // 7. Legfiatalabb utas keresése (Sort + Limit)
        Console.WriteLine("\n--- Legfiatalabb utas ---");
        var legfiatalabb = utasCollection.Aggregate()
            .SortByDescending(u => u.szuletesiev)
            .Limit(1)
            .FirstOrDefault();

        if (legfiatalabb != null)
        {
            Console.WriteLine($"Név: {legfiatalabb.nev}, Született: {legfiatalabb.szuletesiev}");
        }

        Console.WriteLine("\nProgram vége.");
    }
}