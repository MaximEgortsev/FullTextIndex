package tst.tessa;

import org.json.JSONObject;

import tst.tessa.Helper.FileStateEnum;
import tst.tessa.Helper.SqlHelper;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import java.io.*;
import java.util.Scanner;
import java.util.UUID;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.parser.xml.XMLParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;

import org.apache.commons.io.FileUtils;

import org.xml.sax.SAXException;

public class Main {

    public static void main(String[] args)
    {
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            SQLServerDataSource ds = GetConnectionInfo();
            TestReceiver(ds);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private static void TestReceiver(SQLServerDataSource ds)
    {
        System.out.print("Enter the file path or exit to exit: \n");
        Scanner terminalInput = new Scanner(System.in);
        String s = terminalInput.nextLine();
        if(s.equals("exit"))
            return;

        FileObject fileInfo = new FileObject(UUID.randomUUID(), UUID.randomUUID(),
                s, FileStateEnum.Insert);

        WorkWithData(ds, fileInfo);

        TestReceiver(ds);
    }

    private static SQLServerDataSource GetConnectionInfo()
    {
        File file = new File("./app.json");

        try {
            String content = FileUtils.readFileToString(file, "utf-8");

            JSONObject obj = new JSONObject(content);
            JSONObject connect = obj.getJSONObject("ConnectionStrings");

            SQLServerDataSource ds = new SQLServerDataSource();
            ds.setUser(connect.getString("User"));
            ds.setPassword(connect.getString("Password"));
            ds.setServerName(connect.getString("Server"));
            ds.setPortNumber(connect.getInt("Port"));
            ds.setDatabaseName(connect.getString("Database"));

            return ds;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * в зависимости от того, какой статус у файла в тессе, выполняем разные действия
     * @param ds - SQLServerDataSource
     * @param fileInfo - информация о файле из тессы
     */
    private static void WorkWithData(SQLServerDataSource ds, FileObject fileInfo)
    {
        switch (fileInfo.State)
        {
            case Insert:
                GetContentVar(fileInfo);

                if(fileInfo.TxtContent != null && !fileInfo.TxtContent.isEmpty())
                {
                    SqlHelper.InsertIntoTable(ds, fileInfo);
                }
                break;
            case Update:
                //todo:
                break;
            case Delete:
                SqlHelper.DeleteFromTable(ds, fileInfo);
                break;
        }
    }

    /**
     * получаем контент файла
     * @param fileInfo - информация о файле
     */
    private static void GetContentVar(FileObject fileInfo)
    {
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        try
        {
            FileInputStream inputStream = new FileInputStream(new File(fileInfo.Path));

            //в зависимости от типа файла используем разные парсеры
            switch (fileInfo.Type)
            {
                case PowerPoint:
                case Visio:
                case Word:
                case Excel:
                    OOXMLParser msOfficeParser = new OOXMLParser();
                    msOfficeParser.parse(inputStream, handler, metadata, context);
                    break;
                case Pdf:
                    PDFParser pdfparser = new PDFParser();
                    pdfparser.parse(inputStream, handler, metadata, context);
                    break;
                case Txt:
                    TXTParser TexTParser = new TXTParser();
                    TexTParser.parse(inputStream, handler, metadata, context);
                    break;
                case Html:
                    HtmlParser htmlparser = new HtmlParser();
                    htmlparser.parse(inputStream, handler, metadata, context);
                    break;
                case Xml:
                    XMLParser xmlparser = new XMLParser();
                    xmlparser.parse(inputStream, handler, metadata, context);
                    break;
                //если не подходит не под один из вариантов пробуем распарсить автодетектором
                //если применять для всех типов файлов, то теряется около 1000 милисекунд на большие файлы
                case Project:
                case None:
                    AutoDetectParser parser = new AutoDetectParser();
                    parser.parse(inputStream, handler, metadata);
                    break;
            }
        }
        catch (SAXException | TikaException | IOException e)
        {
            e.printStackTrace();
            return;
        }

        fileInfo.TxtContent = handler.toString();
    }

}
