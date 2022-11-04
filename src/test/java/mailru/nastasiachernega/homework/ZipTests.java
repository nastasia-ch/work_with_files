package mailru.nastasiachernega.homework;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ZipTests {

    @Test
    void zipTest() throws Exception {
        ZipFile zipFile = new ZipFile("src/test/resources/files.zip");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("files.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                try(InputStream inputStream = zipFile.getInputStream(entry)) {
                    if(entry.getName().endsWith(".pdf")) {
                        PDF pdf = new PDF(inputStream);
                        assertThat(pdf.text).contains("Introduction to Programming Using Java");
                    }
                    else if (entry.getName().endsWith(".xlsx")) {
                        XLS xls = new XLS(inputStream);
                        assertThat(xls.excel.
                                getSheetAt(0).
                                getRow(2).
                                getCell(1).
                                getStringCellValue()).
                                isEqualTo("Mara");
                        assertThat(xls.excel.
                                getSheetAt(0).
                                getRow(2).
                                getCell(2).
                                getStringCellValue()).
                                isEqualTo("Hashimoto");
                        assertThat(xls.excel.
                                getSheetAt(0).
                                getRow(2).
                                getCell(6).
                                getStringCellValue()).
                                isEqualTo("16/08/2016");
                    }
                    else if(entry.getName().endsWith(".csv")) {
                        try(CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {
                            List<String[]> csvContent = csvReader.readAll();
                            String[] row = csvContent.get(183);
                            assertThat(row[0]).isEqualTo("Russian Federation");
                            assertThat(row[1]).isEqualTo("RU");
                        }
                    }
                }
            }
        }
    }
}
