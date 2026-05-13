using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace JJL4WE.Models;

[BsonIgnoreExtraElements]
public class Etterem // <-- struct helyett class
{
    [BsonId]
    public ObjectId Id { get; set; }
    public required string nev { get; set; }
    public required string varos { get; set; }
    public required Cim cim { get; set; }
    public required int csillag { get; set; }
    public List<string> specialitasok { get; set; }
}

[BsonIgnoreExtraElements]
public class Cim // <-- struct helyett class
{
    public required string utca { get; set; }
    public required int hazszam { get; set; }
}

[BsonIgnoreExtraElements]
public class Foszakacs
{
    [BsonId]
    public ObjectId Id { get; set; }
    public required string nev { get; set; }
    public required string reszleg { get; set; }
    public required int eletkor { get; set; }
    public required int fizetes { get; set; }
    public required List<string> vegzettsegek { get; set; }
    public required string etterem_nev { get; set; }
}