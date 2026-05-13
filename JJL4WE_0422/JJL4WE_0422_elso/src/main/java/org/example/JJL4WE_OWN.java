package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.File;
import java.util.Set;

public class JJL4WE_OWN {
    public static void main(String[] args) throws Exception {
        ObjectMapper m = new ObjectMapper();

        JsonNode root = m.readTree(new File("JSON_OWN.json"));
        JsonNode schemaNode = m.readTree(new File("JSON_SCHEMA_OWN.json"));

        JsonSchema schema = JsonSchemaFactory
                .getInstance(SpecVersion.VersionFlag.V4)
                .getSchema(schemaNode);

        Set<ValidationMessage> errors = schema.validate(root);

        if (errors.isEmpty()) {
            System.out.println("Valid JSON \n");
        } else {
            System.out.println("Hibás JSON:");
            errors.forEach(e -> System.out.println(e.getMessage()));
            return;
        }

        //ADATOK BEOLVASÁSA
        JsonNode repterKezelo = root.get("Reptér_JJL4WE");
        JsonNode dolgozok = repterKezelo.get("Dolgozó");
        JsonNode legitarsasagok = repterKezelo.get("Légitársaság");
        JsonNode jaratok = repterKezelo.get("Járat");
        JsonNode jegyek = repterKezelo.get("Jegy");
        JsonNode utasok = repterKezelo.get("Utasok");

        // --- FELADATOK ---

        System.out.println("=== 1. Dolgozók adatai ===");
        for (JsonNode d : dolgozok) {
            String nev = d.get("név").asText();
            String munkakor = d.get("munkakör").asText();
            String fizetes = d.get("fizetés").asText();
            System.out.println("Név: " + nev + " | Munkakör: " + munkakor + " | Fizetés: " + fizetes + " Ft");
        }

        System.out.println("\n=== 2. Légitársaságok és Járataik ===");
        for (JsonNode l : legitarsasagok) {
            String legitId = l.get("_Légitársaság_ID").asText();
            String legitNev = l.get("légitársaságnév").asText();

            System.out.println("\nLégitársaság: " + legitNev + " [" + legitId + "]");
            System.out.println("---------------------------");

            for (JsonNode j : jaratok) {
                if (j.get("_Légitársaság_ID").asText().equals(legitId)) {
                    System.out.println("- Járat " + j.get("_Járatszám").asText() + " -> " + j.get("célállomás").asText());
                }
            }
        }

        System.out.println("\n=== 3. Átlagos jegyár ===");
        double osszeg = 0;
        int db = 0;
        for (JsonNode j : jegyek) {
            osszeg += j.get("ár").asDouble();
            db++;
        }
        System.out.println("AVG: " + (osszeg / db) + " Ft");

        System.out.println("\n=== 4. Feladat: Késő járatok szűrése ===");
        for (JsonNode j : jaratok) {
            if (j.get("státusz").asText().equals("késik")) {
                System.out.println("- Járatszám: " + j.get("_Járatszám").asText() + " (" + j.get("célállomás").asText() + ")");
            }
        }

        System.out.println("\n=== 5. Feladat: Ki, hova utazik? ===");
        for (JsonNode u : utasok) {
            String nev = u.get("név").asText();
            String jegySorszam = u.get("_Jegysorszám").asText();

            String uticel = "Ismeretlen";
            for (JsonNode j : jegyek) {
                if (j.get("_Jegysorszám").asText().equals(jegySorszam)) {
                    uticel = j.get("uticél").get("reptérnév").asText();
                }
            }
            System.out.println(nev + " utazik ide: " + uticel + " (Jegy: " + jegySorszam + ")");
        }

        System.out.println("\n=== 6. Feladat: JSON adatok manipulációja ===");
        for (JsonNode j : jaratok) {
            ObjectNode obj = (ObjectNode) j;
            obj.put("ellenőrzött", true);
            obj.put("státusz", "frissítve");
        }
        System.out.println("JSON tree modified (ellenorzott added, csillag removed).");

        System.out.println("\n=== 7. Feladat: Legdrágább jegy ===");
        double maxAr = 0;
        String maxJegySorszam = "";

        for (JsonNode j : jegyek) {
            double ar = j.get("ár").asDouble();
            if (ar > maxAr) {
                maxAr = ar;
                maxJegySorszam = j.get("_Jegysorszám").asText();
            }
        }

        String utasNev = "";
        for (JsonNode u : utasok) {
            if (u.get("_Jegysorszám").asText().equals(maxJegySorszam)) {
                utasNev = u.get("név").asText();
                break;
            }
        }
        System.out.println("Utas: " + utasNev + " (Legdrágább jegy: " + maxAr + " Ft)");

        System.out.println("\n=== 8. Feladat: Új JSON fájl készítése és mentése ===");
        ArrayNode ujLista = m.createArrayNode();

        for (JsonNode u : utasok) {
            ObjectNode csomopont = m.createObjectNode();
            csomopont.put("utas_név", u.get("név").asText());
            csomopont.set("telefonszámok", u.get("telefonszám"));
            ujLista.add(csomopont);
        }

        m.writerWithDefaultPrettyPrinter().writeValue(new File("uj_utasok.json"), ujLista);
        System.out.println("Fájl kiírva: uj_utasok.json");
    }
}