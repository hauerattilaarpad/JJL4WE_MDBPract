import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class JJL4WEDomRead {
    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
        File xmlFile = new File("JJL4WE_XML1.xml");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();

        Document doc = dBuilder.parse(xmlFile);

        doc.getDocumentElement().normalize();

        System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

        //-------- ettermek --------------
        NodeList nList = doc.getElementsByTagName("etterem");

        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);
            System.out.println("Current element: " + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element elem = ( Element ) nNode;

                String id = elem.getAttribute("ekod");
                String name = elem.getElementsByTagName("nev").item(0).getTextContent();
                String city = elem.getElementsByTagName("varos").item(0).getTextContent();
                String street = elem.getElementsByTagName("utca").item(0).getTextContent();
                String number = elem.getElementsByTagName("hazszam").item(0).getTextContent();
                String stars = elem.getElementsByTagName("csillag").item(0).getTextContent();

                String adr = city + ", " + street + " utca " + number + ".";

                System.out.println("Étterem ID:" + id);
                System.out.println("Név: " + name);
                System.out.println("Cím: " + adr);
                System.out.println("Csillag: " + stars + "\n");
            }

        }

        // --------- foszakacsok ------------
        nList = doc.getElementsByTagName("foszakacs");

        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);
            System.out.println("Current element: " + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element elem = ( Element ) nNode;

                String id = elem.getAttribute("fkod");
                String name = elem.getElementsByTagName("nev").item(0).getTextContent();
                String age = elem.getElementsByTagName("eletkor").item(0).getTextContent();

                String edu = "";
                NodeList vegzettsegek = elem.getElementsByTagName("vegzettseg");
                for (int j = 0; j < vegzettsegek.getLength(); j++) {
                    if (j > 0) edu += ", ";
                    edu += vegzettsegek.item(j).getTextContent();
                }

                System.out.println("Főszakács ID:" + id);
                System.out.println("Név: " + name);
                System.out.println("Életkor: " + age);
                System.out.println("Végzettség: " + edu  + "\n");
            }
        }

        // --------- szakacsok ------------
        nList = doc.getElementsByTagName("szakacs");

        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);
            System.out.println("Current element: " + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element elem = ( Element ) nNode;


                String id = elem.getAttribute("szkod");
                String name = elem.getElementsByTagName("nev").item(0).getTextContent();
                String section = elem.getElementsByTagName("reszleg").item(0).getTextContent();

                StringBuilder edu = new StringBuilder();
                NodeList vegzettsegek = elem.getElementsByTagName("vegzettseg");
                for (int j = 0; j < vegzettsegek.getLength(); j++) {
                    if (j > 0) edu.append(", ");
                    edu.append(vegzettsegek.item(j).getTextContent());
                }

                System.out.println("Szakács ID:" + id);
                System.out.println("Név: " + name);
                System.out.println("Részleg: " + section);
                System.out.println("Végzettség: " + edu  + "\n");
            }
        }

        // --------- gyakornokok ------------
        nList = doc.getElementsByTagName("gyakornok");

        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);

            System.out.println("Current element: " + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element elem = ( Element ) nNode;


                String id = elem.getAttribute("gykod");
                String name = elem.getElementsByTagName("nev").item(0).getTextContent();
                String start = elem.getElementsByTagName("kezdete").item(0).getTextContent();
                String duration = elem.getElementsByTagName("idotartama").item(0).getTextContent();

                String shifts = "";
                NodeList muszakLista = elem.getElementsByTagName("muszak");
                for (int j = 0; j < muszakLista.getLength(); j++) {
                    if (j > 0) shifts += ", ";
                    shifts += muszakLista.item(j).getTextContent();
                }

                System.out.println("GYakornok ID:" + id);
                System.out.println("Név: " + name);
                System.out.println("Gyakorlat kezdete: " + start + ", időtartama: " + duration);
                System.out.println("Műszak: " + shifts  + "\n");
            }
        }

        // --------- vendegek ------------
        nList = doc.getElementsByTagName("vendeg");

        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);
            System.out.println("Current element: " + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element elem = ( Element ) nNode;


                String id = elem.getAttribute("vkod");
                String name = elem.getElementsByTagName("nev").item(0).getTextContent();
                String age = elem.getElementsByTagName("eletkor").item(0).getTextContent();
                String city = elem.getElementsByTagName("varos").item(0).getTextContent();
                String street = elem.getElementsByTagName("utca").item(0).getTextContent();
                String number = elem.getElementsByTagName("hazszam").item(0).getTextContent();


                String adr = city + ", " + street + " utca " + number + ".";

                System.out.println("Vendég ID:" + id);
                System.out.println("Név: " + name);
                System.out.println("Életkor: " + age);
                System.out.println("Cím: " + adr  + "\n");
            }
        }

        // --------- rendelesek ------------
        nList = doc.getElementsByTagName("rendeles");

        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            System.out.println("Current element: " + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) nNode;

                String etteremRef = elem.getAttribute("e_v_e");
                String vendegRef = elem.getAttribute("e_v_v");

                String osszeg = elem.getElementsByTagName("osszeg").item(0).getTextContent();
                String etel = elem.getElementsByTagName("etel").item(0).getTextContent();

                System.out.println("Rendelés= étterem kód: " + etteremRef + ", vendég kód: " + vendegRef + ")");
                System.out.println("Étel: " + etel);
                System.out.println("Összeg: " + osszeg + " Ft\n");
            }
        }
    }
}