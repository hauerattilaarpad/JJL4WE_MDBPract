
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JJL4WEDomReadOwn {

    public static void main(String[] args) {
        try {
            // Létrehoz egy új XML fájlt
            File newXmlFile = new File("Jjl4weXMLOwnRead.xml");
            StreamResult newXmlStream = new StreamResult(newXmlFile);

            // Olvassa be az eredeti XML fájlt és távolítsa el az üres szövegeket
            Document document = parseXML("Jjl4weXMLOwn.xml");

            //Kiírás fileba
            writeDocument(document, newXmlStream);

            // Kiírja a formázott XML-t a konzolra
            System.out.println("Formázott XML:\n\n" + formatXML(document));
        } catch (IOException | SAXException | ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    // XML fájl beolvasása
    public static Document parseXML(String fileName) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // Dokumentum builder létrehozása a DOM objektumok létrehozásához
        DocumentBuilder builder = factory.newDocumentBuilder();
        // XML fájl beolvasása és DOM dokumentummá alakítása
        Document document = builder.parse(new File(fileName));
        // Üres szövegek eltávolítása
        cleanDocument(document.getDocumentElement());
        return document;
    }

    // Rekurzív függvény az üres szövegek eltávolítására
    private static void cleanDocument(Node root) {
        // A gyökérelem összes gyermek elemének lekérése
        NodeList nodes = root.getChildNodes();
        // Az üres szövegek eltávolítandó listájának inicializálása
        List<Node> toDelete = new ArrayList<>();
        // Az összes gyermek elem ellenőrzése
        for (int i = 0; i < nodes.getLength(); i++) {
            // Ellenőrzi, hogy a jelenlegi elem szöveges (TEXT_NODE) típusú és üres-e
            if (nodes.item(i).getNodeType() == Node.TEXT_NODE && nodes.item(i).getTextContent().strip().isEmpty()) {
                //Ha üres listához adja
                toDelete.add(nodes.item(i));
            } else {
                cleanDocument(nodes.item(i));
            }
        }
        // Az üres szövegeket tartalmazó elemek eltávolítása a DOM dokumentumból
        for (Node node : toDelete) {
            root.removeChild(node);
        }
    }

    // XML fájl írása
    public static void writeDocument(Document document, StreamResult output) throws TransformerException {
        // TransformerFactory létrehozása
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        // Transformer létrehozása a formázási beállításokkal
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        // DOM forrás létrehozása a dokumentumból
        DOMSource source = new DOMSource(document);
        transformer.transform(source, output);
    }

    // Formázott XML szöveg létrehozása
    public static String formatXML(Document document) {
        return "<?xml version=\"" + document.getXmlVersion() + "\" encoding=\"" + document.getXmlEncoding() + "\" ?>\n" +
                formatElement(document.getDocumentElement(), 0);
    }

    // Rekurzív függvény az XML elemek formázására
    public static String formatElement(Node node, int indent) {
        // Ellenőrzés, hogy a Node objektum egy ELEMENT_NODE típusú-e vagy COMMENT_NODE
        if (node.getNodeType() != Node.ELEMENT_NODE && node.getNodeType() != Node.COMMENT_NODE) {
            return "";
        }
        // StringBuilder létrehozása a formázott XML szöveg gyűjtésére
        StringBuilder output = new StringBuilder();

        // Ha a Node objektum COMMENT_NODE típusú, akkor megjegyzést írunk ki
        if (node.getNodeType() == Node.COMMENT_NODE) {
            output.append(getIndentation(indent)).append("<!--").append(((Comment) node).getTextContent()).append("-->\n");
            return output.toString();
        }

        // Nyitó címke (tag) hozzáadása a StringBuilder-hez
        output.append(getIndentation(indent)).append("<").append(((Element) node).getTagName());

        // Ha a Node objektumnak vannak attribútumai, azok hozzáadása a StringBuilder-hez
        if (node.hasAttributes()) {
            for (int i = 0; i < node.getAttributes().getLength(); i++) {
                Node attribute = node.getAttributes().item(i);
                output.append(" ").append(attribute.getNodeName()).append("=\"").append(attribute.getNodeValue()).append("\"");
            }
        }

        // A Node objektum gyerekeinek lekérése
        NodeList children = node.getChildNodes();

        // Ha csak egy szöveges tartalom van, azt egy sorban megjelenítjük
        if (children.getLength() == 1 && children.item(0).getNodeType() == Node.TEXT_NODE) {
            output.append(">").append(children.item(0).getTextContent().trim()).append("</").append(((Element) node).getTagName()).append(">\n");
        } else {
            // Nyitó címke (tag) befejezése és újsor karakter hozzáadása
            output.append(">\n");

            // Gyerekek formázása rekurzívan a megfelelő indentációval
            for (int i = 0; i < children.getLength(); i++) {
                output.append(formatElement(children.item(i), indent + 1));
            }

            // Befejező címke (tag) hozzáadása a StringBuilder-hez, újsor karakterrel és indentációval
            output.append(getIndentation(indent)).append("</").append(((Element) node).getTagName()).append(">\n");
        }

        // A StringBuilder tartalmának visszaadása formázott XML szövegként
        return output.toString();
    }

    // Üres szóközök generálása az indentation szám alapján
    private static String getIndentation(int indent) {
        StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            indentation.append("    ");
        }
        return indentation.toString();
    }
}

