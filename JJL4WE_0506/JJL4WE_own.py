import pymongo as mongo

client = mongo.MongoClient("mongodb://localhost:27017/")

db = client["RepterDB"]


jarat_coll = db["jarat"]
utas_coll = db["utas"]

jarat_coll.delete_many({})
utas_coll.delete_many({})

# --- 1. ADATOK FELTÖLTÉSE (CREATE) ---

jaratok_adatok = [
    {
        "_id": "111",
        "legitarsasag_id": "12",
        "celallomas": "Budapest",
        "indulasi_ido": "2023/11/17 17:00",
        "erkezesi_ido": "2023/11/17 19:00",
        "statusz": "késik"
    },
    {
        "_id": "112",
        "legitarsasag_id": "11",
        "celallomas": "Atlanta",
        "indulasi_ido": "2023/11/20 11:00",
        "erkezesi_ido": "2023/11/20 23:00",
        "statusz": "várható"
    },
    {
        "_id": "113",
        "legitarsasag_id": "13",
        "celallomas": "London",
        "indulasi_ido": "2023/11/18 23:00",
        "erkezesi_ido": "2023/11/19 06:00",
        "statusz": "várható"
    }
]

# Utasok hozzáadása (telefonszámok listaként, születési év számként!)
utasok_adatok = [
    {
        "_id": "98765432",        
        "jarat_id": "113",        
        "jegy_id": "1113",
        "nev": "Hauer Attila",
        "szuletesi_ev": 2002,
        "nem": "Férfi",
        "lakcim": "Magyarország, 3527 Miskolc József Attila u. 12",
        "telefonszam": ["+36201234567", "+36701234567"]
    },
    {
        "_id": "87654367",
        "jarat_id": "111",
        "jegy_id": "1112",
        "nev": "Olivia Thompson",
        "szuletesi_ev": 2001,
        "nem": "Nő",
        "lakcim": "Nagy-Britannia, SW1A 2AB London Downing Street 10",
        "telefonszam": ["+36209876428", "+36702138757", "+36705834653"]
    },
    {
        "_id": "87575645",
        "jarat_id": "112",
        "jegy_id": "1111",
        "nev": "Sophie Mitchell",
        "szuletesi_ev": 1970,
        "nem": "Nő",
        "lakcim": "USA, CA 90212 Beverly Hills Rodeo Drive 123",
        "telefonszam": ["+36202345965"]
    }
]

jarat_coll.insert_many(jaratok_adatok)
print("Járatok sikeresen feltöltve.")

utas_coll.insert_many(utasok_adatok)
print("Utasok feltöltve.")


# --- 2. LEKÉRDEZÉSEK (READ) ---

print("\n--- 2.a) Összes járat ---")
for jarat in jarat_coll.find():
    print(jarat)

print("\n--- 2.a) Összes utas ---")
for utas in utas_coll.find():
    print(utas)

print("\n--- 2.b) Járat lekérdezése (_id: 112) ---")
j112_jarat = jarat_coll.find_one({"_id": "112"})
print(j112_jarat)

print("\n--- 2.c) Utasok, akik 2002 előtt születtek ---")
for idos_utas in utas_coll.find({"szuletesi_ev": {"$lt": 2002}}):
    print(idos_utas)

print("\n--- 2.d) Utasok átlagos születési éve ---")
pipeline_avg = [
    {
        "$group": {
            "_id": None,
            "atlagSzuletesiEv": {"$avg": "$szuletesi_ev"}
        }
    }
]
atlag_eredmeny = list(utas_coll.aggregate(pipeline_avg))
if atlag_eredmeny:
    atlag = atlag_eredmeny[0]['atlagSzuletesiEv']
    print(f"Az utasok átlagos születési éve: {atlag:.0f}")

print("\n--- 2.e) Női utasok és az ő járataik (JOIN) ---")
pipeline_join = [
    {
        "$match": {
            "nem": "Nő"
        }
    },
    {
        "$lookup": {
            "from": "jarat",
            "localField": "jarat_id",
            "foreignField": "_id",
            "as": "jarat_adatok"
        }
    }
]

for doc in utas_coll.aggregate(pipeline_join):
    j_celallomas = doc['jarat_adatok'][0]['celallomas'] if doc['jarat_adatok'] else "Nincs adat"
    print(f"Utas: {doc['nev']} -> Célállomás: {j_celallomas}")


# --- 3. MÓDOSÍTÁSOK (UPDATE) ---

print("\n--- 3.a) 111-es ID járat státuszának frissítése ---")
jarat_coll.update_one(
    {"_id": "111"},              
    {"$set": {"statusz": "megérkezett"}}    
)

print("A 111-es járat lekérdezése a módosítás után:")
print(jarat_coll.find_one({"_id": "111"}))


# --- 4. TÖRLÉSEK (DELETE) ---

print("\n--- 4.a) Konkrét utas törlése (Benjamin Mitchell, _id: 87575645) ---")
utas_coll.delete_one({"_id": "87575645"})

print("\n--- Ellenőrzés: Megmaradt utasok listája ---")
for u in utas_coll.find():
    print(u['nev'])

print("\n--- 4.b) 2001-ben vagy azelőtt született utasok törlése ---")
torles_eredmeny = utas_coll.delete_many(
    {"szuletesi_ev": {"$lte": 2001}}
)
print(f"Törölt utasok száma: {torles_eredmeny.deleted_count}")

print("\n--- Ellenőrzés: Megmaradt utasok listája ---")
for u in utas_coll.find():
    print(u['nev'])