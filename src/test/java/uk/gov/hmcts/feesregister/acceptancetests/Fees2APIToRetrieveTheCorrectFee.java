package uk.gov.hmcts.feesregister.acceptancetests;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.fees.register.api.contract.*;
import uk.gov.hmcts.feesregister.acceptancetests.dsl.FeesRegisterTestDsl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.fees.register.api.contract.CategoryDto.categoryDtoWith;
import static uk.gov.hmcts.fees.register.api.contract.CategoryUpdateDto.categoryUpdateDtoWith;
import static uk.gov.hmcts.fees.register.api.contract.FixedFeeDto.fixedFeeDtoWith;
import static uk.gov.hmcts.fees.register.api.contract.PercentageFeeDto.percentageFeeDtoWith;
import static uk.gov.hmcts.fees.register.api.contract.RangeGroupDto.rangeGroupDtoWith;
import static uk.gov.hmcts.fees.register.api.contract.RangeGroupUpdateDto.rangeGroupUpdateDtoWith;


public class Fees2APIToRetrieveTheCorrectFee extends IntegrationTestBase{
    @Autowired
    private FeesRegisterTestDsl scenario;

    @Test
    public void findAllchannelTypes() throws IOException, NoSuchFieldException {
        scenario.given()
                .when().getAllchannelTypes()
                .then().got(List.class, (result -> assertThat(result).isNotEmpty()));
    }

    @Test
    public void findAllDirectionTypes() throws IOException, NoSuchFieldException {
        scenario.given()
                .when().getAllDirectionTypes()
                .then().got(List.class, (result -> assertThat(result).isNotEmpty()));
    }

    @Test
    public void findAllEventTypes() throws IOException, NoSuchFieldException {
        scenario.given()
                .when().getAllEventTypes()
                .then().got(List.class, (result -> assertThat(result).isNotEmpty()));
    }

    @Test
    public void findAllJurisdictions1Types() throws IOException, NoSuchFieldException {
        scenario.given()
                .when().getAllJurisdictions1Types()
                .then().got(List.class, (result -> assertThat(result).isNotEmpty()));
    }
    @Test
    public void findAllJurisdictions2Types() throws IOException, NoSuchFieldException {
        scenario.given()
                .when().getAllJurisdictions2Types()
                .then().got(List.class, (result -> assertThat(result).isNotEmpty()));
    }
    @Test
    public void findAllServiceTypes() throws IOException, NoSuchFieldException {
        scenario.given()
                .when().getAllServiceTypes()
                .then().got(List.class, (result -> assertThat(result).isNotEmpty()));
    }
}
