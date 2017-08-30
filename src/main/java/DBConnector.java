import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class DBConnector {

    public static void getConnection(long num) {
        String connectionUrl = "jdbc:sqlite:introDB.db";
        String Driver = "org.sqlite.JDBC";
        String file1 = "1.xml";
        String file2 = "2.xml";

        try {
            Class.forName(Driver);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM TEST");
            statement.executeUpdate("VACUUM");

            //Пакетное выполнение sql запросов
            connection.setAutoCommit(false);
            StringBuilder builder = new StringBuilder();

            for(Integer i = 1; i <= num; i++) {
                builder.append("INSERT INTO TEST (field) VALUES (" + i + ")");
                statement.addBatch(builder.toString());
                builder.delete(0, Integer.MAX_VALUE);
            }
            statement.executeBatch();
            connection.commit();


            //Запись данных из базы в 1.xml
            Element entries = new Element("entries");
            Document doc = new Document(entries);
            ArrayList<Element> entry = new ArrayList<Element>();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM TEST");
            while (resultSet.next()) {
                Integer a = resultSet.getInt("FIELD");
                Element numbers = new Element("entry");
                numbers.addContent(new Element("field").setText(a.toString()));
                entry.add(numbers);
            }
            for (Element tmp : entry)
                doc.getRootElement().addContent(tmp);

            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(doc, new FileWriter(file1));
            entry.clear();

            //Преобразование данных из 1.xml в 2.xml с помощью xslt
            transformXML(file1, file2);

            //Парсинг 2.xml для сложения всех данных
            SAXParserFactoryParse(file2);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    static void transformXML(String inFile, String outFile) {
        //Преобразование данных из 1.xml в 2.xml с помощью xslt
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xsl = new StreamSource(new File("transform.xsl"));
            Transformer transformer = factory.newTransformer(xsl);

            Source text = new StreamSource(inFile);
            transformer.transform(text, new StreamResult(outFile));
        } catch(TransformerConfigurationException e) {
            e.printStackTrace();
        } catch(TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void SAXParserFactoryParse(String fileName) {
        //Парсинг 2.xml для сложения всех данных
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(false);
        SAXParser parser;
        InputStream xmlData = null;

        try {
            xmlData = new FileInputStream(fileName);

            parser = factory.newSAXParser();
            parser.parse(xmlData, new MyHandler());
        } catch(SAXException e) {
            e.printStackTrace();
        } catch(ParserConfigurationException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    static class MyHandler extends DefaultHandler {
        static long count = 0;

        @Override
        public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {
            if(qName.equals("entry")) {
                //System.out.println("Значение поля: " + attributes.getValue("field"));
                count = count + Long.parseLong(attributes.getValue("field"));
            }
            super.startElement(uri, localName, qName, attributes);
        }
    }
}
