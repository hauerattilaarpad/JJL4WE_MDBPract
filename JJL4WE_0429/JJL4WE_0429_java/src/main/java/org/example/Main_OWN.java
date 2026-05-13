package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Accumulators.*;

public class Main_OWN{
    public static void main(String[] args) {
        String connectionString = "mongodb://localhost:27017";

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("RepterDB");

            database.getCollection("legitarsasag").drop();
            database.getCollection("utasok").drop();
            database.getCollection("jegyek").drop();

            MongoCollection<Document> legitarsasagColl = database.getCollection("legitarsasag");

            Document l1 = new Document("_id", "11")
                    .append("nev", "Lufthansa")
                    .append("repter_id", "1");

            Document l2 = new Document("_id", "12")
                    .append("nev", "Qatar Airways")
                    .append("repter_id", "2");

            Document l3 = new Document("_id", "13")
                    .append("nev", "Emirates")
                    .append("repter_id", "3");

            legitarsasagColl.insertMany(Arrays.asList(l1, l2, l3));
            String jsonRaw = Files.readString(Paths.get("JSON_OWN.json"));

            Document rootDoc = Document.parse(jsonRaw).get("Reptér_JJL4WE", Document.class);

            List<Document> utasList = rootDoc.getList("Utasok", Document.class);
            List<Document> jegyList = rootDoc.getList("Jegy", Document.class);

            MongoCollection<Document> utasColl = database.getCollection("utasok");
            MongoCollection<Document> jegyColl = database.getCollection("jegyek");

            List<Document> formattedUtasok = utasList.stream().map(doc -> {
                String utlevel = doc.getString("_Útlevél");
                doc.put("_id", utlevel);
                doc.remove("_Útlevél");

                if (doc.containsKey("születésiév")) {
                    doc.put("születésiév", Integer.parseInt(doc.getString("születésiév")));
                }
                return doc;
            }).collect(Collectors.toList());

            utasColl.insertMany(formattedUtasok);

            List<Document> formattedJegyek = jegyList.stream().map(doc -> {
                String jegyId = doc.getString("_Jegysorszám");
                doc.put("_id", jegyId);
                doc.remove("_Jegysorszám");

                if (doc.containsKey("ár")) {
                    doc.put("ár", Integer.parseInt(doc.getString("ár")));
                }
                return doc;
            }).collect(Collectors.toList());

            jegyColl.insertMany(formattedJegyek);

            System.out.println("--- a) Összes légitársaság lekérdezése ---");
            List<Document> tarsasagok = legitarsasagColl.find().into(new ArrayList<>());
            for (Document doc : tarsasagok) {
                System.out.println(doc.toJson());
            }

            System.out.println("\n--- b) Légitársaság lekérdezése (id: 12) ---");
            Document t12 = legitarsasagColl.find(eq("_id", "12")).first();
            if (t12 != null) {
                System.out.println(t12.toJson());
            }

            System.out.println("\n--- c) Utasok, akik 2000 után születtek ---");
            List<Document> fiatalUtasok = utasColl.find(gt("születésiév", 2000)).into(new ArrayList<>());
            for (Document doc : fiatalUtasok) {
                System.out.println(doc.toJson());
            }

            System.out.println("\n--- d) Utasok és jegyeik (JOIN) ---");
            List<Document> utasJegyekkel = utasColl.aggregate(Arrays.asList(
                    lookup("jegyek", "_Jegysorszám", "_id", "jegy_info")
            )).into(new ArrayList<>());

            for (Document doc : utasJegyekkel) {
                System.out.println(doc.toJson());
            }

            System.out.println("\n--- e) Átlagos jegyár ---");
            Document atlagAr = jegyColl.aggregate(Arrays.asList(
                    group(null, avg("atlagosAr", "$ár"))
            )).first();

            if (atlagAr != null) {
                System.out.println("Átlagos jegyár: " + atlagAr.get("atlagosAr") + " Ft");
            }

            legitarsasagColl.updateOne(
                    eq("_id", "12"),
                    new Document("$set", new Document("nev", "Qatar Airways - Módosított"))
            );

            legitarsasagColl.deleteOne(eq("_id", "13"));

            utasColl.deleteMany(lt("születésiév", 1980));

        } catch (Exception e) {
            System.err.println("Hiba történt: " + e.getMessage());
            e.printStackTrace();
        }
    }
}