import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JJL4WEDomModifyOwn {
    public static void main(String[] args) throws TransformerException {

        try {
            Document document = JJL4WEDomReadOwn.parseXML("Jjl4weXMLOwnRead.xml");
            File newXmlFile = new File("XMLModifyjjl4we.xml");
            StreamResult newXmlStream = new StreamResult(newXmlFile);

            // 1. Névmódosítás ID alapon
            modifyUtasNev(document, "98765432", "Nagy Gergő");
            // 2. Célmódosítás ID alapon
            modifyCélállomás(document, "112", "Dallas");
            // 3. Minden dolgozónak emeljük a fizetését 10%-al akinek a megadott összeg
            // alatti a fizetése
            modifyFizetés(document, 500000);
            // 4. Adjunk hozzá egy új telefonszámot az adott IDhez a dolgozókon belül
            addTelefonszám(document, "2001", "+36202233567");
            // 5.Szültési év módosítás Név lakcím és nem alapján
            modifySzuletesiEv(document, "Nagy Gergő", "Magyarország, 3527 Miskolc József Attila u. 12", "Férfi", 2000);
            // 6. Osztály és székszám módosítás
            modifyOsztalyEsSzekszam(document, "Heathrow", "Egyesült Királyság", "2023/11/18 23:00", "2", "98");

            System.out.println("A módosított dokumentum:");
            System.out.println(JJL4WEDomReadOwn.formatXML(document));
            writeDocument(document, newXmlStream);

        } catch (IOException | SAXException | ParserConfigurationException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void modifyUtasNev(Document document, String utlevel, String ujNev) {
        NodeList utasokList = document.getElementsByTagName("Utasok");

        for (int i = 0; i < utasokList.getLength(); i++) {
            Node utasNode = utasokList.item(i);

            if (utasNode.getNodeType() == Node.ELEMENT_NODE) {
                Element utasElement = (Element) utasNode;
                String utlevelAttr = utasElement.getAttribute("Útlevél");

                // Ellenőrizzük az útlevél azonosítót
                if (utlevel.equals(utlevelAttr)) {
                    // Módosítsuk az utas nevét
                    NodeList nevList = utasElement.getElementsByTagName("név");
                    Element nevElement = (Element) nevList.item(0);
                    nevElement.setTextContent(ujNev);

                    System.out.println("Az utas neve módosítva: " + ujNev);
                    return; // Kilépünk a ciklusból, mivel megtaláltuk és módosítottuk az utast
                }
            }
        }

        // Ha nem találtuk meg az utast
        System.out.println("Nem található ilyen útlevél azonosítóval rendelkező utas: " + utlevel);
    }

    public static void modifyCélállomás(Document document, String járatszám, String újCélállomás) {
        NodeList járatList = document.getElementsByTagName("Járat");

        for (int i = 0; i < járatList.getLength(); i++) {
            Node járatNode = járatList.item(i);

            if (járatNode.getNodeType() == Node.ELEMENT_NODE) {
                Element járatElement = (Element) járatNode;
                String járatszámAttr = járatElement.getAttribute("Járatszám");

                // Ellenőrizzük a járatszám azonosítót
                if (járatszám.equals(járatszámAttr)) {
                    // Módosítsuk a célállomást
                    NodeList célállomásList = járatElement.getElementsByTagName("célállomás");
                    Element célállomásElement = (Element) célállomásList.item(0);
                    célállomásElement.setTextContent(újCélállomás);

                    System.out.println("A célállomás módosítva: " + újCélállomás);
                    return; // Kilépünk a ciklusból, mivel megtaláltuk és módosítottuk a járatot
                }
            }
        }

        // Ha nem találtuk meg a járatot
        System.out.println("Nem található ilyen járatszám azonosítóval rendelkező járat: " + járatszám);
    }

    public static void modifyFizetés(Document document, double referenciaFizetés) {
        NodeList dolgozóList = document.getElementsByTagName("Dolgozó");

        for (int i = 0; i < dolgozóList.getLength(); i++) {
            Node dolgozóNode = dolgozóList.item(i);

            if (dolgozóNode.getNodeType() == Node.ELEMENT_NODE) {
                Element dolgozóElement = (Element) dolgozóNode;
                String fizetésString = dolgozóElement.getElementsByTagName("fizetés").item(0).getTextContent();
                double fizetés = Double.parseDouble(fizetésString);

                // Ellenőrizzük a fizetést
                if (fizetés < referenciaFizetés) {
                    // Módosítsuk a fizetést és kerekítsük az egész részre
                    double újFizetés = Math.round(fizetés * 1.1);
                    Element fizetésElement = (Element) dolgozóElement.getElementsByTagName("fizetés").item(0);
                    fizetésElement.setTextContent(String.valueOf((int) újFizetés));

                    System.out.println("A fizetés módosítva a dolgozónál: "
                            + dolgozóElement.getElementsByTagName("név").item(0).getTextContent() + ", Új fizetés: "
                            + (int) újFizetés);
                }
            }
        }
    }

    public static void writeDocument(Document document, StreamResult output) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(document);
        transformer.transform(source, output);
    }

    public static void addTelefonszám(Document document, String dolgozoId, String ujTelefonszam) {
        NodeList dolgozoList = document.getElementsByTagName("Dolgozó");

        for (int i = 0; i < dolgozoList.getLength(); i++) {
            Node dolgozoNode = dolgozoList.item(i);

            if (dolgozoNode.getNodeType() == Node.ELEMENT_NODE) {
                Element dolgozoElement = (Element) dolgozoNode;
                String dolgozoIdAttr = dolgozoElement.getAttribute("D_ID");

                // Ellenőrizzük a Dolgozó-ID-t
                if (dolgozoId.equals(dolgozoIdAttr)) {
                    // Hozzáadjuk az új telefonszámot közvetlenül a meglévő telefonszámok után
                    NodeList telefonszamList = dolgozoElement.getElementsByTagName("telefonszám");
                    Element lastTelefonszamElement = (Element) telefonszamList.item(telefonszamList.getLength() - 1);
                    Element telefonszamElement = document.createElement("telefonszám");
                    telefonszamElement.setTextContent(ujTelefonszam);
                    dolgozoElement.insertBefore(telefonszamElement, lastTelefonszamElement.getNextSibling());

                    System.out.println(
                            "Új telefonszám hozzáadva a dolgozónak (D_ID: " + dolgozoId + "): " + ujTelefonszam);
                    return; // Kilépünk a ciklusból, mivel megtaláltuk és hozzáadtuk a telefonszámot
                }
            }
        }

        // Ha nem találtuk meg a dolgozót
        System.out.println("Nem található ilyen Dolgozó-ID-vel rendelkező dolgozó: " + dolgozoId);
    }

    public static void modifyOsztalyEsSzekszam(Document document, String repterNev, String orszag, String indulasiIdo,
            String ujOsztaly, String ujSzekszam) {
        NodeList jegyList = document.getElementsByTagName("Jegy");

        for (int i = 0; i < jegyList.getLength(); i++) {
            Node jegyNode = jegyList.item(i);

            if (jegyNode.getNodeType() == Node.ELEMENT_NODE) {
                Element jegyElement = (Element) jegyNode;
                Element uticelElement = (Element) jegyElement.getElementsByTagName("uticél").item(0);
                String jegyRepterNev = uticelElement.getElementsByTagName("reptérnév").item(0).getTextContent();
                String jegyOrszag = uticelElement.getElementsByTagName("ország").item(0).getTextContent();
                String jegyIndulasiIdo = jegyElement.getElementsByTagName("indulási_idő").item(0).getTextContent();

                // Ellenőrizzük a feltételeket
                if (jegyRepterNev.equals(repterNev) && jegyOrszag.equals(orszag)
                        && jegyIndulasiIdo.equals(indulasiIdo)) {
                    // Módosítjuk az osztályt és a székszámot
                    Element helyElement = (Element) jegyElement.getElementsByTagName("hely").item(0);
                    Element osztalyElement = (Element) helyElement.getElementsByTagName("osztály").item(0);
                    Element szekszamElement = (Element) helyElement.getElementsByTagName("székszám").item(0);

                    osztalyElement.setTextContent(ujOsztaly);
                    szekszamElement.setTextContent(ujSzekszam);

                    System.out.println(
                            "Osztály és székszám módosítva a jegynél: " + jegyElement.getAttribute("Jegysorszám"));
                    return; // Kilépünk a ciklusból, mivel megtaláltuk és módosítottuk a Jegyet
                }
            }
        }
    }

    public static void modifySzuletesiEv(Document document, String nev, String lakcim, String nem, int ujSzuletesiEv) {
        NodeList utasokList = document.getElementsByTagName("Utasok");

        for (int i = 0; i < utasokList.getLength(); i++) {
            Node utasokNode = utasokList.item(i);

            if (utasokNode.getNodeType() == Node.ELEMENT_NODE) {
                Element utasokElement = (Element) utasokNode;
                String utasNev = utasokElement.getElementsByTagName("név").item(0).getTextContent();
                String utasLakcim = utasokElement.getElementsByTagName("lakcím").item(0).getTextContent();
                String utasNem = utasokElement.getElementsByTagName("nem").item(0).getTextContent();

                // Ellenőrizzük a feltételeket
                if (utasNev.equals(nev) && utasLakcim.equals(lakcim) && utasNem.equals(nem)) {
                    // Módosítjuk a születési évet
                    Element szuletesiEvElement = (Element) utasokElement.getElementsByTagName("születésiév").item(0);
                    szuletesiEvElement.setTextContent(String.valueOf(ujSzuletesiEv));

                    System.out.println("Születési év módosítva az utasnak (Név: " + nev + ", Lakcím: " + lakcim
                            + ", Nem: " + nem + "): " + ujSzuletesiEv);
                    return; // Kilépünk a ciklusból, mivel megtaláltuk és módosítottuk az Utast
                }
            }
        }
    }
}