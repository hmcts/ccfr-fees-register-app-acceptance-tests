package uk.gov.hmcts.feesregister.acceptancetests;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.fees.register.api.contract.CalculationDto;
import uk.gov.hmcts.fees.register.api.contract.CategoryDto;
import uk.gov.hmcts.fees.register.api.contract.FixedFeeDto;
import uk.gov.hmcts.fees.register.api.contract.RangeGroupDto;
import uk.gov.hmcts.feesregister.acceptancetests.dsl.FeesRegisterTestDsl;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.fees.register.api.contract.CategoryDto.categoryDtoWith;
import static uk.gov.hmcts.fees.register.api.contract.FixedFeeDto.fixedFeeDtoWith;
import static uk.gov.hmcts.fees.register.api.contract.RangeGroupDto.rangeGroupDtoWith;
public class CategoriesIntegrationForCMCTest extends IntegrationTestBase {

    @Autowired
    private FeesRegisterTestDsl scenario;

    @Test
    public void findAllCategories() throws IOException, NoSuchFieldException {
        scenario.given()
                .when().getAllCategories()
                .then().got(List.class, (result -> assertThat(result).isNotEmpty()));
    }

    @Test
    public void findCategoryByCode() throws IOException {
        scenario.given()
                .when().getCategoryByCode("cmc-hearing")
                .then().got(CategoryDto.class, (category -> assertThat(category).isEqualTo(categoryDtoWith()
                .code("cmc-hearing")
                .description("CMC - Hearing fees")
                .rangeGroup(category.getRangeGroup())
                .fees(category.getFees())
                .build())));
    }

    @Test
    public void getAllFees() throws IOException, NoSuchFieldException {
        scenario.given()
                .when().getAllFees()
                .then().got(List.class, (feesList -> assertThat(feesList).isNotEmpty()));
    }

    @Test
    public void findFeesByCode() throws IOException {
        scenario.given()
                .when().getFeesByCode("X0026")
                .then().got(FixedFeeDto.class, (fee -> assertThat(fee).isEqualTo(fixedFeeDtoWith()
                .code("X0026")
                .description("Civil Court fees - Money Claims Online - Claim Amount - 500.01 upto 1000 GBP")
                .amount(6000)
                .build())));
    }

    @Test
    public void findFeesByPercentage() throws IOException {
        scenario.given()
                .when().getFeesByCaluclations("X0434", 15000)
                .then().got(CalculationDto.class, calculatedFee -> assertThat(calculatedFee).hasFieldOrPropertyWithValue("amount",675));
    }

    @Test
    public void findAllRangeGroups() throws IOException, NoSuchFieldException {
        scenario.given()
                .when().getAllRangeGroups()
                .then().got(List.class, (feesList -> assertThat(feesList).isNotEmpty()));
    }

    @Test
    public void findRangeGroupByCode() throws IOException {
        scenario.given()
                .when().getRangeGroupsByCode("cmc-hearing")
                .then().got(RangeGroupDto.class, (category -> assertThat(category).isEqualTo(rangeGroupDtoWith()
                .code("cmc-hearing")
                .description("CMC - Hearing")
                .ranges(category.getRanges())
                .build())));
    }

    @Test
    public void findPercentageFeesForRangeGroup() throws IOException {
        scenario.given()
                .when().getRangeGroupByCaluclations("cmc-online", 1500000)
                .then().got(CalculationDto.class, calculatedFee -> assertThat(calculatedFee).hasFieldOrPropertyWithValue("amount",67500));
    }

    @Test
    public void findFeesByPercentageForBadRequest() throws IOException {
        scenario.given()
                .when().getFeesByCaluclations("X04341", 1500000001)
                .then().notFound();
    }

    @Test
    public void findPercentageFeesForRangeGroupForNotFound() throws IOException {
        scenario.given()
                .when().getRangeGroupByCaluclations("X0434", 1500001)
                .then().notFound();
    }

    @Test
    public void findCategoryByCodeForBadRequest() throws IOException {
        scenario.given()
                .when().getCategoryByCode("cmc-hearing1")
                .then().notFound();
    }

    @Test
    public void findFeesByCodeForNotFound() throws IOException {
        scenario.given()
                .when().getFeesByCode("X00261")
                .then().notFound();
    }
}

