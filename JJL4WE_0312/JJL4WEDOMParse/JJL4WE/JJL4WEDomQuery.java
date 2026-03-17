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

public class JJL4WEDomQuery {

    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
        File xmlFile = new File("JJL4WE_XML.xml");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();

        Document doc = dBuilder.parse(xmlFile);

        doc.getDocumentElement().normalize();

        System.out.println("Root element: " + doc.getDocumentElement().getNodeName());


        //Szakácsos lekérdezés
        System.out.println("Azok a szakácsok, akiknek a végzettségeik között van szakközép: \n");

        NodeList nList = doc.getElementsByTagName("szakacs");

        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) nNode;
                //Szakácssok végzettségei, akiknek van szakközépiskolai végzettsége
                for (int j = 0; j < elem.getElementsByTagName("vegzettseg").getLength(); j++) {

                    Node node3 = elem.getElementsByTagName("vegzettseg").item(j);
                    String edu1 = node3.getTextContent();

                    if ("Szakközépiskola".equals(edu1)) {
                        String id = elem.getAttribute("szkod");
                        String eid = elem.getAttribute("e_sz");

                        String work = "EZ a szakács a(z) " + eid + " azonosítójú étteremben dolgozik";

                        Node node1 = elem.getElementsByTagName("nev").item(0);
                        String name = node1.getTextContent();

                        Node node2 = elem.getElementsByTagName("reszleg").item(0);
                        String section = node2.getTextContent();

                        String edu2 = "";

                        for (int k = 0; k < elem.getElementsByTagName("vegzettseg").getLength(); k++) {
                            node3 = elem.getElementsByTagName("vegzettseg").item(k);
                            if (k == elem.getElementsByTagName("vegzettseg").getLength() - 1) {
                                edu2 += node3.getTextContent();
                            } else {
                                edu2 += node3.getTextContent() + ", ";
                            }
                        }

                        System.out.println("Szakács ID: " + id);
                        System.out.println("Név: " + name);
                        System.out.println("Részleg: " + section);
                        System.out.println("Végzettség: " + edu2);
                        System.out.println(work + "\n");
                    }
                }
            }
        }


        //Éttermes lekérdezés
        System.out.println("---------------------------\n\n5 csillagos éttermek: \n");
        nList = doc.getElementsByTagName("etterem");

        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) nNode;
                //5 csillagos éttermek
                for (int j = 0; j < elem.getElementsByTagName("csillag").getLength(); j++) {

                    Node node5 = elem.getElementsByTagName("csillag").item(j);
                    String stars = node5.getTextContent();

                    if ("5".equals(stars)) {
                        String id = elem.getAttribute("ekod");

                        Node node1 = elem.getElementsByTagName("nev").item(0);
                        String name = node1.getTextContent();

                        Node node2 = elem.getElementsByTagName("varos").item(0);
                        String city = node2.getTextContent();

                        Node node3 = elem.getElementsByTagName("utca").item(0);
                        String street = node3.getTextContent();

                        Node node4 = elem.getElementsByTagName("hazszam").item(0);
                        String number = node4.getTextContent();

                        String adr = city + ", " + street + " utca " + number + ".";

                        System.out.println("Étterem: " + id);
                        System.out.println("Név: " + name);
                        System.out.println("Cím: " + adr);
                        System.out.println("Csillag: " + stars + "\n");
                    }
                }
            }

        }




        //Gyakorlatos lekérdezés
        System.out.println("---------------------------\n\nAzok a gyakorlnokok, akiknek van délutáni műszak: \n");

        nList = doc.getElementsByTagName("gyakornok");

        for(int i = 0; i<nList.getLength();i++) {

            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) nNode;
                //GYakornokok délutáni műszakkal
                for (int j = 0; j < elem.getElementsByTagName("muszak").getLength(); j++) {

                    Node node4 = elem.getElementsByTagName("muszak").item(j);
                    String shift = node4.getTextContent();

                    if ("délután".equals(shift)) {
                        String id = elem.getAttribute("gykod");

                        Node node1 = elem.getElementsByTagName("nev").item(0);
                        String name = node1.getTextContent();

                        Node node2 = elem.getElementsByTagName("kezdete").item(0);
                        String start = node2.getTextContent();

                        Node node3 = elem.getElementsByTagName("idotartama").item(0);
                        String duration = node3.getTextContent();


                        String shift2 = "";
                        for (int k = 0; k < elem.getElementsByTagName("muszak").getLength(); k++) {
                            node4 = elem.getElementsByTagName("muszak").item(k);
                            if (k == elem.getElementsByTagName("muszak").getLength() - 1) {
                                shift2 += node4.getTextContent();
                            } else {
                                shift2 += node4.getTextContent() + ", ";
                            }
                        }

                        System.out.println("Gyakornok ID: " + id);
                        System.out.println("Név: " + name);
                        System.out.println("Gyakorlat kezdete: " + start);
                        System.out.println("Időtartama: " + duration);
                        System.out.println("Műszak: " + shift2 + "\n");
                    }
                }
            }
        }
    }
}