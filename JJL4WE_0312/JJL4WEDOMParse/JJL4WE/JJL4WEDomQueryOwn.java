import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JJL4WEDomQueryOwn {
    public static void main(String[] args) {
        try {
            Document document = JJL4WEDomReadOwn.parseXML("Jjl4weXMLOwnRead.xml");

            // 1. Összes utas név
            String osszesUtasNev = getAllPassangerName(document);
            System.out.println("Az összes utas neve: ");
            System.out.println(osszesUtasNev);

            // 2. Összes járat indulási idővel
            String jaratIndulasiIdovel = getAllPlanes(document);
            System.out.println("Az összes járat indulási idővel: ");
            System.out.println(jaratIndulasiIdovel);

            // 3.Az összes dolgozó kevesebb mint 500000 fizetéssel
            String dolgozofizetes = getAllEployesWithSalary(document, 500000);
            System.out.println("Az összes dolgozó kevesebb mint 500000 fizetéssel");
            System.out.println(dolgozofizetes);
            // 4 Átlagos fizetés
            String atlagDolgozofizetes = getAverageSalary(document);
            System.out.println(atlagDolgozofizetes);

            // 5 legtöbbet kereső dolgozó
            printHighestEarningEmployee(document);

        } catch (IOException | SAXException | ParserConfigurationException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getAllPassangerName(Document document) {
        StringBuilder result = new StringBuilder();

        // Lekérdezés az Utasok elemekre
        NodeList utasokList = document.getElementsByTagName("Utasok");

        for (int i = 0; i < utasokList.getLength(); i++) {
            Element utasokElement = (Element) utasokList.item(i);

            // Név lekérdezése az Utasok elem alól
            String nev = utasokElement.getElementsByTagName("név").item(0).getTextContent();

            // Eredményhez hozzáadás
            result.append(nev).append("\n");
        }

        return result.toString();
    }

    public static String getAllPlanes(Document document) {
        StringBuilder result = new StringBuilder();

        // Lekérdezés a Jarat elemekre
        NodeList jaratokList = document.getElementsByTagName("Járat");

        for (int i = 0; i < jaratokList.getLength(); i++) {
            Element jaratElement = (Element) jaratokList.item(i);

            // Járat adatainak lekérdezése
            String celallomas = jaratElement.getElementsByTagName("célállomás").item(0).getTextContent();
            String indulasiIdo = jaratElement.getElementsByTagName("indulási_idő").item(0).getTextContent();

            // Eredményhez hozzáadás
            result.append("Célállomás: ").append(celallomas).append(", Indulási idő: ").append(indulasiIdo)
                    .append("\n");
        }

        return result.toString();
    }

    public static String getAllEployesWithSalary(Document document, int minSalary) {
        StringBuilder result = new StringBuilder();


        // Lekérdezés a Dolgozó elemekre
        NodeList dolgozokList = document.getElementsByTagName("Dolgozó");

        for (int i = 0; i < dolgozokList.getLength(); i++) {
            Element dolgozoElement = (Element) dolgozokList.item(i);

            // Fizetés lekérdezése a Dolgozo elem alól
            int fizetes = Integer.parseInt(dolgozoElement.getElementsByTagName("fizetés").item(0).getTextContent());

            // Ellenőrzés, hogy a fizetés kisebb-e a megadott értéknél
            if (fizetes < minSalary) {
                // Név lekérdezése a Dolgozo elem alól
                String nev = dolgozoElement.getElementsByTagName("név").item(0).getTextContent();

                // Eredményhez hozzáadás
                result.append(nev).append("\n");
            }
        }

        return result.toString();
    }

    public static String getAverageSalary(Document document) {
        NodeList dolgozokList = document.getElementsByTagName("Dolgozó");

        int totalSalary = 0;
        int numberOfEmployees = dolgozokList.getLength();

        for (int i = 0; i < numberOfEmployees; i++) {
            Element dolgozoElement = (Element) dolgozokList.item(i);
            int fizetes = Integer.parseInt(dolgozoElement.getElementsByTagName("fizetés").item(0).getTextContent());
            totalSalary += fizetes;
        }

        if (numberOfEmployees > 0) {
            double averageSalary = (double) totalSalary / numberOfEmployees;
            return "Az átlagfizetés a dolgozók között: \n" + averageSalary;
        } else {
            return "Nincs elérhető dolgozói információ.";
        }
    }

    public static void printHighestEarningEmployee(Document document) {
        NodeList dolgozokList = document.getElementsByTagName("Dolgozó");

        int maxSalary = 0;
        String highestEarningEmployeeName = "";

        for (int i = 0; i < dolgozokList.getLength(); i++) {
            Element dolgozoElement = (Element) dolgozokList.item(i);
            int fizetes = Integer.parseInt(dolgozoElement.getElementsByTagName("fizetés").item(0).getTextContent());

            if (fizetes > maxSalary) {
                maxSalary = fizetes;
                highestEarningEmployeeName = dolgozoElement.getElementsByTagName("név").item(0).getTextContent();
            }
        }

        if (!highestEarningEmployeeName.isEmpty()) {
            System.out.println("\nA legtöbbet kereső dolgozó neve: \n" + highestEarningEmployeeName);
        } else {
            System.out.println("Nincs elérhető dolgozói információ.");
        }
    }

}
