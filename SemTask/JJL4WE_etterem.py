import pymongo as mongo

#Kapcsolódás a MongoDB szerverhez
client = mongo.MongoClient("mongodb://localhost:27017/")

db = client["VendeglatasDB"]

#Collection létrehozása
etterem_coll = db["etterem"]
foszakacs_coll = db["foszakacs"]

etterem_coll.delete_many({})
foszakacs_coll.delete_many({})

ettermek_adatok = [
    {
        "_id": "e1",
        "nev": "Aranyhal Étterem",
        "cim": {
            "varos": "Miskolc",
            "utca": "Széchenyi u.",
            "hazszam": 107
        },
        "csillag": 3
    },
    {
        "_id": "e2",
        "nev": "Aranyhal Étterem",
        "cim": {
            "varos": "Miskolc",
            "utca": "Széchenyi u.",
            "hazszam": 107
        },
        "csillag": 4
    },
    {
        "_id": "e3",
        "nev": "Creppy Palacsintaház Étterem",
        "cim": {
            "varos": "Miskolc",
            "utca": "Méylvölgy utca",
            "hazszam": 15
        },
        "csillag": 5
    }
]

#Főszakácsok hozzáadása
foszakacsok = [
    {
        "_id": "f1",
        "e_f": "e1",
        "nev": "Fő István",
        "eletkor": 45,
        "vegzettseg": ["Szakközépiskola", "Főiskola"]
    },
    {
        "_id": "f2",
        "e_f": "e2",
        "nev": "Kovács János",
        "eletkor": 38,
        "vegzettseg": ["Szakközépiskola", "Főiskola"]
    },
    {
        "_id": "f3",
        "e_f": "e3",
        "nev": "Nemes Géza",
        "eletkor": 28,
        "vegzettseg": ["Főiskola"]
    }
]
foszakacs_coll.insert_many(foszakacsok)
print("Főszakácsok feltöltve.")

etterem_coll.insert_many(ettermek_adatok)
print("Éttermek sikeresen feltöltve.")






#Éttermek lekérdezése
print("\n --- 2.a) Összes étterem ---")
for etterem in etterem_coll.find():
    print(etterem)

#Főszakácsok lekérdezése    
print("\n--- 2.a) Összes főszakács ---")
for foszakacs in foszakacs_coll.find():
    print(foszakacs)

#Kettes ID étterem lekérdezése
print("\n--- 2.b) Étterem lekérdezése (_id: e2) ---")
e2_etterem = etterem_coll.find_one({"_id": "e2"})
print(e2_etterem)

#5 csillag alatti éttermek lekérdezése
print("\n--- 2.c) Éttermek, amelynek az értékelése kevesebb vagy egyenlő mint 4 csillag ---")
for etterem_csillag in etterem_coll.find({"csillag": {"$lte": 4}}):
    print(etterem_csillag)

# Főszakácsok átlagos életkorának lekérdezése
print("\n--- 2.d) Főszakácsok átlagos életkora ---")
pipeline_avg = [
    {
        "$group": {
            "_id": None,
            "atlagEletkor": {"$avg": "$eletkor"}
        }
    }
]
atlag_eredmeny = list(foszakacs_coll.aggregate(pipeline_avg))

atlag = atlag_eredmeny[0]['atlagEletkor']
print(f"A főszakácsok átlagos életkora: {atlag:.2f} év")



print("\n--- Szakközépiskolát végzett Főszakácsok és éttermeik ---")
pipeline = [
    {
        "$match": {
            "vegzettseg": "Szakközépiskola"
        }
    },
    {
        "$lookup": {
            "from": "etterem",
            "localField": "e_f",
            "foreignField": "_id",
            "as": "etterem_adatok"
        }
    }
]

for doc in foszakacs_coll.aggregate(pipeline):
    e_nev = doc['etterem_adatok'][0]['nev'] if doc['etterem_adatok'] else "Nincs adat"
    print(f"Főszakács: {doc['nev']} -> Étterem: {e_nev}")

#Módosítások
print("\n--- 3.a)e1-es ID étterem módosítása ---")
etterem_coll.update_one(
    {"_id": "e1"},              
    {"$set": {"csillag": 4}}    
)

print("\n --- 3.a)Összes étterem ---")
for etterem in etterem_coll.find():
    print(etterem)



#Törlések
print("\n--- 4.a) Konkrét főszakács törlése (_id: f2) ---")
torles_f2 = foszakacs_coll.delete_one(
    {"_id": "f2"}
)
print("\n--- Ellenőrzés: Megmaradt főszakácsok listája ---")
for f in foszakacs_coll.find():
    print(f)

print("\n--- 4.b)30 év alatti főszakácsok törlése ---")
torles_eredmeny = foszakacs_coll.delete_many(
    {"eletkor": {"$lt": 30}}
)
print(f"Törölt főszakácsok száma: {torles_eredmeny.deleted_count}")

print("\n--- Ellenőrzés: Megmaradt főszakácsok listája ---")
for f in foszakacs_coll.find():
    print(f)

