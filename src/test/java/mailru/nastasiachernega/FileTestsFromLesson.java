package mailru.nastasiachernega;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Configuration;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

public class FileTestsFromLesson {

    @Test
    void selenideFileDownloadTryFinallyTest() throws Exception {
        open("https://github.com/junit-team/junit5/blob/main/README.md");
        File downloadedFile = $("#raw-url").download(); //абстракция, определяющая путь к файлу
        InputStream is = new FileInputStream(downloadedFile); // открывает поток для побайтового чтения файла
        // конструкция try/finally - блок finally выполнится в любом случае, независимо от того,
        // что произойдет в блоке try (выбросится ли исключение или нет)
        try {
            byte[] fileSource = is.readAllBytes(); // прочитали байты из InputStream и положили в массив FileSource
            // (возвращает содержимое в виде массива байтов
            String fileContent = new String(fileSource, StandardCharsets.UTF_8); // создали из этих байтов строку
            assertThat(fileContent).contains("This repository is the home of the next generation " +
                    "of JUnit, _JUnit 5_."); // проверили, что строка содержит то, что нужно
        } finally {
            is.close(); // закрываем поток
        }
    }

    @Test
    void selenideFileDownloadTryWithResourcesTest() throws Exception {
        open("https://github.com/junit-team/junit5/blob/main/README.md");
        File downloadedFile = $("#raw-url").download();
        // конструкция try with resources
        // если какой-то класс имплементирует класс closable => java умеет сама его закрывать,
        // если он объявлен внутри конструкции try with resources
        // Т.е. мы объявляем InputStream внутри скобок, чтобы java обязательно закрыла за собой
        // этот ресурс, независимо от того, что произойдет в фигурных скобках
        try (InputStream is = new FileInputStream(downloadedFile)) {
            byte[] fileSource = is.readAllBytes(); // прочитали байты из InputStream и положили в массив FileSource
            String fileContent = new String(fileSource, StandardCharsets.UTF_8); // создали из этих байтов строку
            assertThat(fileContent).contains("This repository is the home of the next generation " +
                    "of JUnit, _JUnit 5_."); // проверили, что строка содержит то, что нужно
        }
    }

    //есть библиотеки, которые реализуют чтение файла в одну строку кода
    @Test
    void selenideFileDownloadLibraryTest() throws Exception {
        open("https://github.com/junit-team/junit5/blob/main/README.md");
        File downloadedFile = $("#raw-url").download();
        String fileContent = FileUtils.readFileToString(downloadedFile, StandardCharsets.UTF_8);
        assertThat(fileContent).contains("This repository is the home of the next generation " +
                "of JUnit, _JUnit 5_.");
    }

    @Test
    void uploadFileTest() {
        open("https://fineuploader.com/demos.html");
        Configuration.holdBrowserOpen = true;
        $("input[type=file]").uploadFromClasspath("kotik.jpg");
        $(".qq-file-name").shouldHave(text("kotik.jpg"));
    }

    @Test
    void filePdfDownloadTest() throws Exception {
        open("https://junit.org/junit5/docs/current/user-guide/");
        File downloadFile = $("a[href*='junit-user-guide-5.9.1.pdf']").download();
        PDF pdf = new PDF(downloadFile);
        assertThat(pdf.author).contains("Sam Brannen");
    }

    ClassLoader cl = FileTestsFromLesson.class.getClassLoader();

    @Test
    void filePdfCheckTest() throws IOException {
        try (InputStream is = cl.getResourceAsStream("junit-user-guide-5.9.1.pdf")) {
            PDF pdf = new PDF(is);
            assertThat(pdf.text).contains("JUnit 5 User Guide");
        }
    }

    @Test
    void fileXlsCheckTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("Candidate-Screening.xlsx")) {
            XLS xls = new XLS(is);
            assertThat(xls.excel.getSheetAt(0).getRow(4).getCell(0).
                    getStringCellValue()).isEqualTo("Алешкевич Алла Ивановна");
        }

    }

    @Test
    void fileCVSDownloadTestFromInternet() throws Exception {
        open("https://www.sample-videos.com/download-sample-csv.php");
        File downloadedFile =
                $("a[href='csv/Sample-Spreadsheet-10-rows.csv']").
                        download();
        try (InputStream is = new FileInputStream(downloadedFile);
             CSVReader csvreader = new CSVReader(new InputStreamReader(is))) {
            List<String[]> content = csvreader.readAll();
            String[] row = content.get(1);
            assertThat(row[1]).isEqualTo("1.7 Cubic Foot Compact \"Cube\" Office Refrigerators");
            assertThat(row[2]).isEqualTo("Barry French");
        }
    }

    @Test
    void fileCVSDownloadTestFromResources() throws Exception {
        try (InputStream stream = cl.getResourceAsStream("SampleCSVFile_11kb.csv");
             CSVReader csvreader = new CSVReader(new InputStreamReader(stream))) {
            List<String[]> content = csvreader.readAll(); // возвращает лист массивов стрингов,
            // т.е. лист - это совокупность строк, а каждый массив - ячеек строки
            String[] row = content.get(3);
            assertThat(row[0]).isEqualTo("4");
            assertThat(row[1]).isEqualTo("R380");
            assertThat(row[2]).isEqualTo("Clay Rozendal");
            assertThat(row[3]).isEqualTo("483");
        }
    }

    @Test
    void zipTest() throws Exception {
        ZipFile zipFile = new ZipFile("src/test/resources/files.zip");
        try (InputStream is = cl.getResourceAsStream("files.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println(entry.getName());
            }
        }
    }
}
