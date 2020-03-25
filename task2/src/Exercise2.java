import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;

import org.apache.tika.exception.TikaException;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class Exercise2 {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private OptimaizeLangDetector langDetector;

    public static void main(String[] args) {
        Exercise2 exercise = new Exercise2();
        exercise.run();
    }

    private void run() {
        try {
            if (!new File("./outputDocuments").exists()) {
                Files.createDirectory(Paths.get("./outputDocuments"));
            }

            initLangDetector();

            File directory = new File("src/resources/documents");
            File[] files = directory.listFiles();
            for (File file : files) {
                processFile(file);
            }
        } catch (IOException | SAXException | TikaException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void initLangDetector() throws IOException {
        langDetector = new OptimaizeLangDetector();
        langDetector.loadModels();
    }

    private void processFile(File file) throws IOException, SAXException, TikaException, ParseException {
        // call saveResult method to save the data

        InputStream inputStream = new FileInputStream(file);
        AutoDetectParser autoDetectParser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        BodyContentHandler bodyContentHandler = new BodyContentHandler(-1);
        autoDetectParser.parse(inputStream, bodyContentHandler, metadata);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-ddhh:mm:ss");
        Date creationDate = null;
        Date lastModification = null;
        String mimeType = null;

        for(String name : metadata.names()) {
            System.out.println(name + ": " + metadata.get(name));
            if (name.contentEquals("Creation-Date") || name.contentEquals("meta:creation-date") || name.contentEquals("dcterms:created"))
                creationDate=dateFormat.parse(metadata.get(name).substring(0, 10)+metadata.get(name).substring(11, 19));
            if (name.contentEquals("Last-Save-Date") || name.contentEquals("Last-Modified") || name.contentEquals("meta:save-date")) {
                try {
                    lastModification=dateFormat.parse(metadata.get(name).substring(0, 10)+metadata.get(name).substring(11, 19));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (name.contentEquals("creator") || name.contentEquals("meta:author") || name.contentEquals("Author"))
                mimeType=metadata.get(name);
        }

        LanguageResult languageResult = langDetector.detect(bodyContentHandler.toString());
        MimeTypes mimeTypes = new MimeTypes();
        InputStream bufferedIn = new BufferedInputStream(inputStream);
        MediaType mediaType = mimeTypes.detect(bufferedIn, metadata);

        saveResult(file.getName(), languageResult.getLanguage(), mimeType, creationDate, lastModification, mediaType.toString(), bodyContentHandler.toString());
    }

    private void saveResult(String fileName, String language, String creatorName, Date creationDate,
                            Date lastModification, String mimeType, String content) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        int index = fileName.lastIndexOf(".");
        String outName = fileName.substring(0, index) + ".txt";
        try {
            PrintWriter printWriter = new PrintWriter("./outputDocuments/" + outName);
            printWriter.write("Name: " + fileName + "\n");
            printWriter.write("Language: " + (language != null ? language : "") + "\n");
            printWriter.write("Creator: " + (creatorName != null ? creatorName : "") + "\n");
            String creationDateStr = creationDate == null ? "" : dateFormat.format(creationDate);
            printWriter.write("Creation date: " + creationDateStr + "\n");
            String lastModificationStr = lastModification == null ? "" : dateFormat.format(lastModification);
            printWriter.write("Last modification: " + lastModificationStr + "\n");
            printWriter.write("MIME type: " + (mimeType != null ? mimeType : "") + "\n");
            printWriter.write("\n");
            printWriter.write(content + "\n");
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}