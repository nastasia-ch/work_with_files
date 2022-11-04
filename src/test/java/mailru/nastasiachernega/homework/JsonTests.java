package mailru.nastasiachernega.homework;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import mailru.nastasiachernega.homework.model.Company;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonTests {

    @Test
    void jsonTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try(InputStream is = getClass().getClassLoader().getResourceAsStream("company.json")) {
            Company company = mapper.readValue(is, Company.class);
            assertThat(company.nameOfCompany).isEqualTo("Some company name");
            assertThat(company.numberOfEmployees).isEqualTo(38);
            assertThat(company.doesCompanyHaveTaxBenefits).isEqualTo(true);
            assertThat(company.productTypes.get(1)).isEqualTo("Works");
            assertThat(company.structure.forthDepartment).isEqualTo(7);
        }
    }

}
