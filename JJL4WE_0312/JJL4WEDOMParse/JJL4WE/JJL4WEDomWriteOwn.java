
import java.io.File;
import java.text.ParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JJL4WEDomWriteOwn {
    public static void main(String[] args) throws ParseException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            createAirport(document);


            // Kiírás konzolra és fájlba a DOMReadJjl4we osztályt felhasználva
            File newXmlFile = new File("JJL4WEDomWrite.xml");
            StreamResult xmlToWrite = new StreamResult(newXmlFile);
            JJL4WEDomReadOwn.writeDocument(document, xmlToWrite);
            StreamResult console = new StreamResult(System.out);
            System.out.println("A felépített dokumentum:\n");
            JJL4WEDomReadOwn.writeDocument(document, console);

        } catch (ParserConfigurationException | TransformerException | DOMException e) {
            e.printStackTrace();
        }
    }
    // Járat elem létrehozása és hozzáadása a DOM fához
    public static Element createJárat(Document document, String járatszám, String légitársaságID,
                                      String célállomás, String indulásiIdő, String érkezésiIdő, String státusz) {
        Element járatElement = document.createElement("Járat");
        járatElement.setAttribute("Járatszám", járatszám);
        járatElement.setAttribute("Légitársaság_ID", légitársaságID);

        createElementWithTextContent(document, járatElement, "célállomás", célállomás);
        createElementWithTextContent(document, járatElement, "indulási_idő", indulásiIdő);
        createElementWithTextContent(document, járatElement, "érkezési_idő", érkezésiIdő);
        createElementWithTextContent(document, járatElement, "státusz", státusz);

        return járatElement;
    }

    // Jegy elem létrehozása és hozzáadása a DOM fához
    public static Element createJegy(Document document, String jegySorszám, String ár,
                                     String légitársaság, String reptérNév, String ország, String indulásiIdő, String érkezésiIdő,
                                     String osztály, String székszám) {
        Element jegyElement = document.createElement("Jegy");
        jegyElement.setAttribute("Jegysorszám", jegySorszám);

        createElementWithTextContent(document, jegyElement, "ár", ár);
        createElementWithTextContent(document, jegyElement, "légitársaság", légitársaság);

        Element uticélElement = document.createElement("uticél");
        createElementWithTextContent(document, uticélElement, "reptérnév", reptérNév);
        createElementWithTextContent(document, uticélElement, "ország", ország);
        jegyElement.appendChild(uticélElement);

        createElementWithTextContent(document, jegyElement, "indulási_idő", indulásiIdő);
        createElementWithTextContent(document, jegyElement, "érkezési_idő", érkezésiIdő);

        Element helyElement = document.createElement("hely");
        createElementWithTextContent(document, helyElement, "osztály", osztály);
        createElementWithTextContent(document, helyElement, "székszám", székszám);
        jegyElement.appendChild(helyElement);

        return jegyElement;
    }

    // Utasok elem létrehozása és hozzáadása a DOM fához
    public static Element createUtasok(Document document, String útlevél, String jegySorszám,
                                       String járatszám, String név, String születésiÉv, String nem, String lakcím,
                                       String... telefonszámok) {
        Element utasokElement = document.createElement("Utasok");
        utasokElement.setAttribute("Útlevél", útlevél);
        utasokElement.setAttribute("Jegysorszám", jegySorszám);
        utasokElement.setAttribute("Járatszám", járatszám);

        createElementWithTextContent(document, utasokElement, "név", név);
        createElementWithTextContent(document, utasokElement, "születésiév", születésiÉv);
        createElementWithTextContent(document, utasokElement, "nem", nem);
        createElementWithTextContent(document, utasokElement, "lakcím", lakcím);

        for (String telefonszám : telefonszámok) {
            createElementWithTextContent(document, utasokElement, "telefonszám", telefonszám);
        }

        return utasokElement;
    }

    // Szolgáltatás elem létrehozása és hozzáadása a DOM fához
    public static Element createSzolgáltatás(Document document, String szolgáltatásID,
                                             String dolgozóID, String útlevél, String típus) {
        Element szolgáltatásElement = document.createElement("Szolgáltatás");
        szolgáltatásElement.setAttribute("SZ_ID", szolgáltatásID);
        szolgáltatásElement.setAttribute("D_ID", dolgozóID);
        szolgáltatásElement.setAttribute("Útlevél", útlevél);

        createElementWithTextContent(document, szolgáltatásElement, "típus", típus);

        return szolgáltatásElement;
    }

    // Dolgozó elem létrehozása és hozzáadása a DOM fához
    public static Element createDolgozó(Document document, String dolgozóID, String reptérID,
                                        String név, String munkakör, String lakcím, String fizetés, String pozíció, String... telefonszámok) {
        Element dolgozóElement = document.createElement("Dolgozó");
        dolgozóElement.setAttribute("D_ID", dolgozóID);
        dolgozóElement.setAttribute("Reptér_ID", reptérID);

        createElementWithTextContent(document, dolgozóElement, "név", név);
        createElementWithTextContent(document, dolgozóElement, "munkakör", munkakör);
        createElementWithTextContent(document, dolgozóElement, "lakcím", lakcím);

        for (String telefonszám : telefonszámok) {
            createElementWithTextContent(document, dolgozóElement, "telefonszám", telefonszám);
        }

        createElementWithTextContent(document, dolgozóElement, "fizetés", fizetés);
        createElementWithTextContent(document, dolgozóElement, "pozíció", pozíció);

        return dolgozóElement;
    }

    // Reptér elem létrehozása és hozzáadása a DOM fához
    public static Element createReptér(Document document, String reptérID, String ország,
                                       String város, String irányítószám) {
        Element reptérElement = document.createElement("Reptér");
        reptérElement.setAttribute("Reptér_ID", reptérID);

        Element elhelyezkedésElement = document.createElement("elhelyezkedés");
        createElementWithTextContent(document, elhelyezkedésElement, "ország", ország);
        createElementWithTextContent(document, elhelyezkedésElement, "város", város);
        createElementWithTextContent(document, elhelyezkedésElement, "irányítószám", irányítószám);
        reptérElement.appendChild(elhelyezkedésElement);

        return reptérElement;
    }

    // Légitársaság elem létrehozása és hozzáadása a DOM fához
    public static Element createLégitársaság(Document document, String légitársaságID,
                                             String reptérID, String légitársaságNév) {
        Element légitársaságElement = document.createElement("Légitársaság");
        légitársaságElement.setAttribute("Légitársaság_ID", légitársaságID);
        légitársaságElement.setAttribute("Reptér_ID", reptérID);

        createElementWithTextContent(document, légitársaságElement, "légitársaságnév", légitársaságNév);

        return légitársaságElement;
    }

    // Segédfüggvény szöveges tartalommal rendelkező elem létrehozására és hozzáadására
    private static void createElementWithTextContent(Document document, Element parentElement,
                                                     String elementName, String textContent) {
        Element element = document.createElement(elementName);
        element.setTextContent(textContent);
        parentElement.appendChild(element);
    }
    private static void createAirport(Document document) throws DOMException, ParseException {
        // Gyökérelem felvétele
        Element root = document.createElement("Reptér_JJL4WE");
        document.appendChild(root);

        // Járat felvétele
        root.appendChild(document.createComment("Járat"));
        root.appendChild(createJárat(document, "111", "12", "Budapest", "2023/11/17 17:00", "2023/11/17 19:00", "késik"));
        root.appendChild(createJárat(document, "112", "11", "Atlanta", "2023/11/20 11:00", "2023/11/20 23:00", "várható"));
        root.appendChild(createJárat(document, "113", "13", "London", "2023/11/18 23:00", "2023/11/19 06:00", "várható"));
        //Jegy felvétele
        root.appendChild(document.createComment("Jegy"));
        root.appendChild(createJegy(document, "1111", "40000", "Emirates", "Heathrow", "Egyesült Királyság", "2023/11/18 23:00", "2023/11/19 06:00", "1", "31"));
        root.appendChild(createJegy(document, "1112", "20000", "Qatar Airways", "Ferihegy", "Magyarország", "2023/11/17 17:00", "2023/11/17 19:00", "2", "52"));
        root.appendChild(createJegy(document, "1113", "50000", "Lufthansa", "Hartsfield-Jackson", "Egyesült Államok", "2023/11/20 11:00", "2023/11/20 23:00", "1", "11"));
        //Utasok felvétele
        root.appendChild(document.createComment("Utasok"));
        root.appendChild(createUtasok(document, "98765432", "1113", "113", "Hauer Attila", "2002", "Férfi", "Magyarország, 3527 Miskolc József Attila u. 12","+36201234567","+36701234567"));
        root.appendChild(createUtasok(document, "87654367", "1112", "111", "Olivia Thompson", "2001", "Nő", "Nagy-Britannia, SW1A 2AB London Downing Street 10","+36209876428","+36702138757","+36705834653"));
        root.appendChild(createUtasok(document, "87575645", "1111", "112", "Benjamin Mitchell", "1970", "Nő", "USA, CA 90212 Beverly Hills Rodeo Drive 123","+36202345965"));
        //Szolgáltatás felvétele
        root.appendChild(document.createComment("Szolgáltatás"));
        root.appendChild(createSzolgáltatás(document, "3001", "2001", "98765432", "Információ nyújtás"));
        root.appendChild(createSzolgáltatás(document, "3002", "2002", "87575645", "Felszolgálás"));
        root.appendChild(createSzolgáltatás(document, "3003", "2003", "87654367", "Bőrönd mérés"));
        //Dolgozók felvétele
        root.appendChild(document.createComment("Dolgozó"));
        root.appendChild(createDolgozó(document, "2001", "1", "Balla Sándor", "Biztonságiőr", "Magyarország, 3530 Miskolc Arany János u. 32", "450000", "Biztonsági vezető", "+36204567534"));
        root.appendChild(createDolgozó(document, "2002", "3", "Kobe Briant", "Felszolgáló", "USA, CA 90212 Beverly Hills Rodeo Drive 25", "750000", "Légi forgalmi irányító", "+36203641243"));
        root.appendChild(createDolgozó(document, "2003", "2", "Charles Hamilton", "Bőröndmérő", "Nagy-Britannia, SW1A 2AD London Downing Street 76", "350000", "Utasellátó", "+36301237659","+36708646856","+36207363457"));
        //Reptér felvétele
        root.appendChild(document.createComment("Reptér"));
        root.appendChild(createReptér(document, "1", "Magyarország", "Budapest", "1185"));
        root.appendChild(createReptér(document, "2", "Egyesült Királyság", "London", "TW62GA"));
        root.appendChild(createReptér(document, "3", "Egyesült Államok", "Atlanta", "30337"));
        //Légitársaság felvétele
        root.appendChild(document.createComment("Légitársaság"));
        root.appendChild(createLégitársaság(document, "11", "1", "Lufthansa"));
        root.appendChild(createLégitársaság(document, "12", "2", "Qatar Airways"));
        root.appendChild(createLégitársaság(document, "13", "3", "Emirates"));
    }
}