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

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.joda.time.DateTime;
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
        BodyContentHandler bodyContentHandler = new BodyContentHandler(250000);
        autoDetectParser.parse(inputStream, bodyContentHandler, metadata);

        Tika tika = new Tika();
        String mimeType = tika.detect(file);
        String tikaContent = tika.parseToString(file);

        langDetector.reset();
        langDetector.addText(tikaContent);
        LanguageResult languageResult = langDetector.detect();

        String creatorName = metadata.get(TikaCoreProperties.CREATOR);
        DateTime creationDate = new DateTime(metadata.get(TikaCoreProperties.CREATED));
        DateTime lastModification = new DateTime(metadata.get(TikaCoreProperties.MODIFIED));

        saveResult(file.getName(), languageResult.getLanguage(), creatorName, creationDate.toDate(), lastModification.toDate(), mimeType, bodyContentHandler.toString());
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