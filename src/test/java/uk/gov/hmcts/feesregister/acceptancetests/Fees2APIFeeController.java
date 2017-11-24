package uk.gov.hmcts.feesregister.acceptancetests;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.fees.register.api.contract.*;
import uk.gov.hmcts.fees2.register.api.contract.Fee2Dto;
import uk.gov.hmcts.fees2.register.api.contract.FeeVersionDto;
import uk.gov.hmcts.fees2.register.api.contract.amount.FlatAmountDto;
import uk.gov.hmcts.fees2.register.api.contract.amount.PercentageAmountDto;
import uk.gov.hmcts.fees2.register.api.contract.request.ApproveFeeDto;
import uk.gov.hmcts.fees2.register.api.contract.request.CreateFixedFeeDto;
import uk.gov.hmcts.fees2.register.api.contract.request.CreateRangedFeeDto;
import uk.gov.hmcts.fees2.register.api.contract.FeeVersionDto;
import uk.gov.hmcts.fees2.register.api.contract.request.CreateFeeDto;
import uk.gov.hmcts.fees2.register.data.dto.LookupFeeDto;
import uk.gov.hmcts.fees2.register.data.dto.response.FeeLookupResponseDto;
import uk.gov.hmcts.fees2.register.data.model.FeeVersionStatus;
import uk.gov.hmcts.feesregister.acceptancetests.dsl.FeesRegisterTestDsl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.fees.register.api.contract.RangeGroupDto.rangeGroupDtoWith;
import static uk.gov.hmcts.fees2.register.api.contract.request.CreateFeeDto.*;
import static uk.gov.hmcts.fees2.register.api.contract.request.CreateRangedFeeDto.*;
import static uk.gov.hmcts.fees2.register.api.contract.request.ApproveFeeDto.*;
import static uk.gov.hmcts.fees2.register.api.contract.request.CreateFixedFeeDto.*;

public class Fees2APIFeeController extends IntegrationTestBase{

    @Autowired
    private FeesRegisterTestDsl scenario;

    private String feeCode;
    private String feeCode1;
    private String feeCode2;

    CreateRangedFeeDto proposeRangedFees;//new CreateRangedFeeDto("X0011", version, "civil","county court", "civil money claims", "online", "enhanced","issue", "Test memo line", "CMC online fee order name","Natural code 001", new BigDecimal(1), new BigDecimal(10000));

    CreateFixedFeeDto proposeFixedFees;

    ApproveFeeDto approveFeeCode;

    @Test
    public void createRangedFees201() throws IOException {

        feeCode = UUID.randomUUID().toString();

        proposeRangedFees = getRangedFeeDto(feeCode);
        approveFeeCode = getApproveFeeDto(feeCode);

        scenario.given().userId("1")
                .when().createRangedFee(proposeRangedFees).approvedFeeCode(approveFeeCode)
                .then().ok().getFeeCodeAndVerifyStatus1(feeCode).got(Fee2Dto.class, fee2Dtos -> {
            assertThat(fee2Dtos.getFeeVersionDtos().get(0).getStatus()).isEqualTo(FeeVersionStatus.approved);
        }).deleteFeeCode1(feeCode).isDeleted();
    }

    private CreateRangedFeeDto getRangedFeeDto(String feeCode) {
        //feeCode = UUID.randomUUID().toString();
        FlatAmountDto amount = new FlatAmountDto(new BigDecimal(100));

        MutableDateTime validTo = new MutableDateTime(new Date());
        validTo.addDays(90);

        FeeVersionDto version = new FeeVersionDto(1, new Date(), validTo.toDate(), "Testing", FeeVersionStatus.draft, amount, null);
        return new CreateRangedFeeDto(feeCode, version, "civil","family court", "civil money claims", "online", "enhanced","issue", "Test memo line", "CMC online fee order name","Natural code 001", new BigDecimal(10000), new BigDecimal(1));
    }

    private ApproveFeeDto getApproveFeeDto(String feeCode){
        return new ApproveFeeDto(feeCode, 1);
    }

    @Test
    public void createFixedFees201() throws IOException {

        feeCode1 = UUID.randomUUID().toString();
        proposeFixedFees = getFixedFeeDto(feeCode1);

        scenario.given().userId("1")
                .when().createFixedFee(proposeFixedFees)
                .then().isCreated()
                .deleteFeeCode1(feeCode1).isDeleted();
    }

    private CreateFixedFeeDto getFixedFeeDto(String feeCode) {
        FlatAmountDto amount = new FlatAmountDto(new BigDecimal(550));
        DateTime to = new DateTime();
        to.plusDays(90);
        FeeVersionDto version = new FeeVersionDto(1, new Date(), to.toDate(), "Testing", FeeVersionStatus.approved, amount, null);
        CreateFixedFeeDto createfixedfee = new CreateFixedFeeDto(feeCode, version, "family","family court", "divorce", "online", "enhanced","issue", "Test memo line", "CMC online fee order name","Natural code 001");
        createfixedfee.setUnspecifiedClaimAmount(false);
        return createfixedfee;
    }

    @Test
    public void getlookupresponseMessage() throws IOException {

        scenario.given().userId("1")
                .when().getLookUpResponse("divorce", "family","family court", "default","issue")
                .then().ok().got(FeeLookupResponseDto.class, FeeLookupResponseDto -> {
            assertThat(FeeLookupResponseDto.getCode()).isEqualTo("X0165");
            assertThat(FeeLookupResponseDto.getVersion()).isEqualTo(1);
            assertThat(FeeLookupResponseDto.getFeeAmount()).isEqualTo("550.00");
        });
    }

    @Ignore
    @Test
    public void createFixedFeesForUnspecifiedAmount201() throws IOException {

        feeCode2 = UUID.randomUUID().toString();
        proposeFixedFees = getFixedFeeUnspecifiedDto(feeCode2);

        scenario.given().userId("1")
                .when().createFixedFee(proposeFixedFees)
                .then().isCreated()
                .deleteFeeCode1(feeCode2).isDeleted();
    }

    private CreateFixedFeeDto getFixedFeeUnspecifiedDto(String feeCode2) {
        FlatAmountDto amount = new FlatAmountDto(new BigDecimal(550));
        DateTime to = new DateTime();
        to.plusDays(90);
        FeeVersionDto version = new FeeVersionDto(1, new Date(), to.toDate(), "Testing", FeeVersionStatus.approved, amount, null);
        CreateFixedFeeDto createfixedfeeforunspecified = new CreateFixedFeeDto(feeCode2, version, "family","family court", "divorce", "online", "enhanced","issue", "Test memo line", "CMC online fee order name","Natural code 001");
        createfixedfeeforunspecified.setUnspecifiedClaimAmount(true);
        return createfixedfeeforunspecified;
    }

}
