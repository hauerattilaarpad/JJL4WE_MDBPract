
using JJL4WE.Models;
using MongoDB.Driver;



class Program
{
    static void Main(string[] args)
    {
        var client = new MongoClient("mongodb://localhost:27017");
        Console.WriteLine("Connected to MongoDB");
        var database = client.GetDatabase("vendeglatas");
        var etteremCollection = database.GetCollection<Etterem>("ettermek");
        var foszakacsCollection = database.GetCollection<Foszakacs>("foszakacsok");
        
        Console.WriteLine("Étteremek:");
        var ettermek = etteremCollection.Find(_ => true).ToList();
        foreach (var etterem in ettermek)
        {
            Console.WriteLine(
                $"Név: {etterem.nev} \n" +
                $"Város: {etterem.varos} \n" +
                $"Cím: {etterem.cim.utca} {etterem.cim.hazszam} \n" +
                $"Csillag: {etterem.csillag} \n" +
                $"Specialitások: {string.Join(", ", etterem.specialitasok)} \n\n"
            );
        }

        Console.WriteLine("Főszakácsok:");
        var foszakacsok = foszakacsCollection.Find(_ => true).ToList();
        foreach (var foszakacs in foszakacsok)        {
            Console.WriteLine(
                $"Név: {foszakacs.nev} \n" +
                $"Részleg: {foszakacs.reszleg} \n" +
                $"Életkor: {foszakacs.eletkor} \n" +
                $"Fizetés: {foszakacs.fizetes} \n" +
                $"Végzettségek: {string.Join(", ", foszakacs.vegzettsegek)} \n" +
                $"Étterem: {foszakacs.etterem_nev} \n\n"
            );
        }

        var ujEtterem = new Etterem
        {
            nev = "Kedvenc Éttermem",
            varos = "Budapest",
            cim = new Cim { utca = "Fő utca", hazszam = 1 },
            csillag = 3,
            specialitasok = new List<string> { "Gulyásleves", "Rántott hús" }
        };
        etteremCollection.InsertOne(ujEtterem);
        Console.WriteLine("Új étterem hozzáadva.");

        var ujFoszakacs = new Foszakacs
        {
            nev = "Kovács Péter",
            reszleg = "Konyha",
            eletkor = 45,
            fizetes = 500000,
            vegzettsegek = new List<string> { "Szakács", "Mesterszakács" },
            etterem_nev = "Kedvenc Éttermem"
        };
        foszakacsCollection.InsertOne(ujFoszakacs);
        Console.WriteLine("Új főszakács hozzáadva.");

        var anyukamMondta = Builders<Etterem>.Filter.Eq(e => e.nev, "Kedvenc Éttermem");
        var update = Builders<Etterem>.Update.Set(e => e.csillag, 4);
        etteremCollection.UpdateOne(anyukamMondta, update);
        Console.WriteLine("Étterem csillagának frissítése sikeres.");

        var harmincAlatt = Builders<Foszakacs>.Filter.Lt(f => f.eletkor, 30);
        foszakacsCollection.DeleteMany(harmincAlatt);
        Console.WriteLine("30 év alatti főszakácsok törlése sikeres.");

        var kissPeter = Builders<Foszakacs>.Filter.Eq(f => f.nev, "Kiss Péter");
        var ujVegzettseg = Builders<Foszakacs>.Update.Push(f => f.vegzettsegek, "Sütőmester");
        foszakacsCollection.UpdateOne(kissPeter, ujVegzettseg);
        Console.WriteLine("Kiss Péter új végzettségének hozzáadása sikeres.");

        // Lekérdezések
        Console.WriteLine("Szakácsok részlegek szerint:");
        var reszlegek = foszakacsCollection.Find(_ => true).ToList();

        foreach (var sz in reszlegek)
        {
            Console.WriteLine($"Név: {sz.nev}, Részleg: {sz.reszleg}");
        }

        Console.WriteLine("4+ csillagos éttermek:");
        var csillagosEttermek = etteremCollection.Find(e => e.csillag >= 4).ToList();
        foreach (var e in csillagosEttermek) {
            Console.WriteLine($"Név: {e.nev}, Csillag: {e.csillag}");
        }

        Console.WriteLine("Miskolc vagy 5 csillagos éttermek:");
        var miskolcVagyCsillagos = 
            Builders<Etterem>.Filter.Eq(e => e.varos, "Miskolc") | 
            Builders<Etterem>.Filter.Gte(e => e.csillag, 5);

        var result = etteremCollection.Find(miskolcVagyCsillagos).ToList();
        foreach (var e in result) {
            Console.WriteLine($"Név: {e.nev}, Város: {e.varos}, Csillag: {e.csillag}");
        }

        Console.WriteLine("25-40 év közti főszakácsok:");
        var korSzures = 
            Builders<Foszakacs>.Filter.Gte(f => f.eletkor, 25) & 
            Builders<Foszakacs>.Filter.Lte(f => f.eletkor, 40);
        
        var korResult = foszakacsCollection.Find(korSzures).ToList();
        foreach (var f in korResult)
        {
            Console.WriteLine($"Név: {f.nev}, Életkor: {f.eletkor}");
        }

        // Aggregáció
        Console.WriteLine("Városonkénti átlag csillag:");
        var atlagCsillag = etteremCollection.Aggregate()
            .Group(e => e.varos, g => new
            {
                Varos = g.Key,
                Darab = g.Count(),
                AtlagCsillag = g.Average(e => e.csillag)
            })
            .ToList();

        foreach (var item in atlagCsillag)        {
            Console.WriteLine($"Város: {item.Varos}, Éttermek száma: {item.Darab}, Átlag csillag: {item.AtlagCsillag}");
        }

        Console.WriteLine("Föszakácsok száma éttermenként:");
        var foszakacsSzam = foszakacsCollection.Aggregate()
            .Group(f => f.etterem_nev, g => new
            {
                EtteremNev = g.Key,
                FoszakacsokSzama = g.Count()
            })
            .ToList();

        foreach (var item in foszakacsSzam)        {
            Console.WriteLine($"Étterem: {item.EtteremNev}, Főszakácsok száma: {item.FoszakacsokSzama}");
        }

        Console.WriteLine("Legidősebb főszakács:");
        var legidosebb = foszakacsCollection.Aggregate()
            .SortByDescending(f => f.eletkor)
            .Limit(1)
            .FirstOrDefault();
        if (legidosebb != null)        {
            Console.WriteLine($"Név: {legidosebb.nev}, Életkor: {legidosebb.eletkor}");
        }

        Console.WriteLine("Program vége.");
    }
}