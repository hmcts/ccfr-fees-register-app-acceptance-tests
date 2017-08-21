package uk.gov.hmcts.feesregister.acceptancetests.dsl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.fees.register.api.contract.FixedFeeDto;
import uk.gov.hmcts.fees.register.api.contract.PercentageFeeDto;
import uk.gov.hmcts.feesregister.acceptancetests.dto.ChargeableFeeWrapperDto;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
@Scope("prototype")
public class FeesRegisterTestDsl {
    private final ObjectMapper objectMapper;
    private final Map<String, String> headers = new HashMap<>();
    private final String baseUri;
    private Response response;

    @Autowired
    public FeesRegisterTestDsl(@Value("${base-urls.fees-register}") String baseUri) {
        this.objectMapper = new ObjectMapper();
        this.baseUri = baseUri;
    }

    public FeesRegisterGivenDsl given() {
        return new FeesRegisterGivenDsl();
    }

    public class FeesRegisterGivenDsl {
        public FeesRegisterWhenDsl when() {
            return new FeesRegisterWhenDsl();
        }
    }

    public class FeesRegisterWhenDsl {
        private RequestSpecification newRequest() {
            return RestAssured.given().baseUri(baseUri).contentType(ContentType.JSON).headers(headers);
        }

       /*public FeesRegisterWhenDsl getFeeByCategoryAndAmount(String categoryId, String amount) {
          response = newRequest().get("/fees-register/categories/{categoryId}/ranges/{amount}/fees", categoryId, amount);
          return this;
       }*/

        public FeesRegisterWhenDsl getCategoryByCode(String code) {
            response = newRequest().get("/categories/{code}", code);
            return this;
        }

        public FeesRegisterWhenDsl getAllCategories() {
            response = newRequest().get("/categories");
            return this;
        }

        public FeesRegisterWhenDsl getFeesByCode(String code) {
            response = newRequest().get("/fees/{code}", code);
            return this;
        }

        public FeesRegisterWhenDsl getFeesByCaluclations(String code, int value) {
            response = newRequest().get("/fees/{code}/calculations?value={value}", code, value);
            return this;
        }

        public FeesRegisterWhenDsl getAllFees() {
            response = newRequest().get("/fees");
            return this;
        }

        public FeesRegisterWhenDsl getRangeGroupsByCode(String code) {
            response = newRequest().get("/range-groups/{code}", code);
            return this;
        }

        public FeesRegisterWhenDsl getRangeGroupByCaluclations(String code, int value) {
            response = newRequest().get("/range-groups/{code}/calculations?value={value}", code, value);
            return this;
        }

        public FeesRegisterWhenDsl getAllRangeGroups() {
            response = newRequest().get("/range-groups");
            return this;
        }

        /*public FeesRegisterWhenDsl getAllFlatFeesByCategoryId(String categoryId) {
            response = newRequest().get("/fees-register/categories/{categoryId}/flat", categoryId);
            return this;
        }

        public FeesRegisterWhenDsl getFlatFeesByCategoryId(String categoryId, String feeId) {
            response = newRequest().get("/fees-register/categories/{categoryId}/flat/{feeId}", categoryId, feeId);
            return this;
        }*/

       /* public FeesRegisterWhenDsl getAllFees() {
            response = newRequest().get("/fees-register");
            return this;
        }*/

        public FeesRegisterWhenDsl getBuildInfo() {
            response = newRequest().get("/info");
            return this;
        }

        public FeesRegisterThenDsl then() {
            return new FeesRegisterThenDsl();
        }
    }

    public class FeesRegisterThenDsl {
        public FeesRegisterThenDsl notFound() {
            response.then().statusCode(404);
            return this;
        }

        public FeesRegisterThenDsl badRequest() {
            response.then().statusCode(400);
            return this;
        }


        public FeesRegisterThenDsl ok() {
            response.then().statusCode(200);
            return this;
        }

        public <T> FeesRegisterThenDsl got(Class<T> type, Consumer<T> assertions) {
            T dto = response.then().statusCode(200).extract().as(type);
            assertions.accept(dto);
            return this;
        }

        @SneakyThrows
        public <T> FeesRegisterThenDsl gotChargeablePercentageFee(Consumer<ChargeableFeeWrapperDto> assertions) {
            InputStream responseInputStream = response.then().statusCode(200).extract().asInputStream();
            JavaType javaType = TypeFactory.defaultInstance().constructParametricType(ChargeableFeeWrapperDto.class, PercentageFeeDto.class);
            ChargeableFeeWrapperDto actual = objectMapper.readValue(responseInputStream, javaType);
            assertions.accept(actual);
            return this;
        }

        @SneakyThrows
        public <T> FeesRegisterThenDsl gotChargeableFixedFee(Consumer<ChargeableFeeWrapperDto> assertions) {
            InputStream responseInputStream = response.then().statusCode(200).extract().asInputStream();
            JavaType javaType = TypeFactory.defaultInstance().constructParametricType(ChargeableFeeWrapperDto.class, FixedFeeDto.class);
            ChargeableFeeWrapperDto actual = objectMapper.readValue(responseInputStream, javaType);
            assertions.accept(actual);
            return this;
        }
    }
}

