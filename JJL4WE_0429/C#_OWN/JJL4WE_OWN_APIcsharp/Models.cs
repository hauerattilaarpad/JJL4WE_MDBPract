using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace RepterApp.Models;

[BsonIgnoreExtraElements]
public class Jarat
{
    [BsonId]
    public ObjectId Id { get; set; }
    public required string celallomas { get; set; }
    public required string indulasi_ido { get; set; }
    public required string erkezesi_ido { get; set; }
    public required string statusz { get; set; }
    public required string Jaratszam { get; set; }
}

[BsonIgnoreExtraElements]
public class Utas
{
    [BsonId]
    public ObjectId Id { get; set; }
    public required string nev { get; set; }
    public required string szuletesiev { get; set; }
    public required string nem { get; set; }
    public required string lakcim { get; set; }
    public required object telefonszam { get; set; }
    public required string Utlevel { get; set; }
    public required string Jegysorszam { get; set; }
    public required string Jaratszam { get; set; }
}