package com.demo.example.bff.contract_test;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.neobank.common.json.Json;
import com.neobank.common.webclient.HttpService;
import com.neobank.common.webclient.WebClientService;
import com.neobank.common.webclient.exception.NeobankClientException;
import com.neobank.common.webclient.restclient.customizer.DefaultWebClientErrorConfig;
import com.neobank.common.webclient.restclient.customizer.ErrorHandlingCustomizer;
import com.neobank.common.webclient.restclient.customizer.LoggingCustomizer;
import com.neobank.common.webclient.restclient.customizer.WebClientErrorConfig;
import com.demo.example.bff.TestUtility;
import com.demo.example.bff.model.Profile;
import com.demo.example.bff.model.ProfileInput;
import com.demo.example.bff.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(PactConsumerTestExt.class)
public class ConsumerApplicantRestTest {
    private static final String CONTENT_TYPE = "Content-Type";
    public static final Map<String, String> HEADERS = Map.of(CONTENT_TYPE, String.valueOf(APPLICATION_JSON));
    private static final String PROFILE_BASE_URL = "/api/v1/profiles";

    private final HttpService httpService = new WebClientService(aWebClient());

    @Pact(provider = "template-system-service", consumer = "template-experience-bff")
    public RequestResponsePact getProfileDetailsForValidProfile(PactDslWithProvider builder) {
        return builder
                .given("System API is ready to return profile details for areza")
                .uponReceiving("Test request received for profile details for areza")
                .method(GET.name())
                .path(PROFILE_BASE_URL + "/areza")
                .willRespondWith()
                .status(OK.value())
                .body(Json.toJson(TestUtility.getSampleProfile("areza")))
                .headers(HEADERS)
                .toPact();
    }

    @Pact(provider = "template-system-service", consumer = "template-experience-bff")
    public RequestResponsePact getProfileDetailsForInvalidProfile(PactDslWithProvider builder) {
        return builder
                .given("System API is ready to return profile details for dummy")
                .uponReceiving("Test request received for profile details for dummy")
                .method(GET.name())
                .path(PROFILE_BASE_URL + "/dummy")
                .willRespondWith()
                .status(NOT_FOUND.value())
                .headers(HEADERS)
                .toPact();
    }

    @Pact(provider = "template-system-service", consumer = "template-experience-bff")
    public RequestResponsePact updateProfile(PactDslWithProvider builder) {
        Profile sampleProfileUpdateInput = TestUtility.getSampleProfile("areza", TestUtility.getSampleProfileUpdateInput());

        return builder
                .given("Server is ready to update a profile")
                .uponReceiving("Test request received for updating a profile")
                .path(PROFILE_BASE_URL)
                .method(PUT.name())
                .headers(HEADERS)
                .body(Json.toJson(sampleProfileUpdateInput))
                .willRespondWith()
                .status(OK.value())
                .headers(HEADERS)
                .body(Json.toJson(sampleProfileUpdateInput))
                .toPact();
    }

    @Pact(provider = "template-system-service", consumer = "template-experience-bff")
    public RequestResponsePact createProfile(PactDslWithProvider builder) {
        ProfileInput sampleProfileUpdateInput = TestUtility.getSampleProfileUpdateInput();
        Profile profile = TestUtility.getSampleProfile("user-id", sampleProfileUpdateInput);

        return builder
                .given("Server is ready to create a profile")
                .uponReceiving("Test request received for creating a profile")
                .path(PROFILE_BASE_URL)
                .method(POST.name())
                .headers(HEADERS)
                .body(Json.toJson(sampleProfileUpdateInput))
                .willRespondWith()
                .status(OK.value())
                .headers(HEADERS)
                .body(Json.toJson(profile))
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getProfileDetailsForValidProfile")
     void testGetProfileDetailsForValidProfileId(MockServer mockServer) {
        ProfileService profileService = new ProfileService(mockServer.getUrl()+ PROFILE_BASE_URL, httpService);
        Profile profile = profileService.lookupUser("areza");
        Profile expectedProfile = TestUtility.getSampleProfile("areza");

        assertThat(profile, samePropertyValuesAs(expectedProfile, "address"));
        assertThat(profile.getAddress(), samePropertyValuesAs(expectedProfile.getAddress()));
    }

    @Test
    @PactTestFor(pactMethod = "getProfileDetailsForInvalidProfile")
    void testGetProfileDetailsForInvalidProfileId(MockServer mockServer) {
        ProfileService profileService = new ProfileService(mockServer.getUrl() + PROFILE_BASE_URL, httpService);
        Throwable ex = catchThrowable(() -> profileService.lookupUser("dummy"));

        assertThat(ex, notNullValue());
        assertThat(ex, instanceOf(NeobankClientException.class));
        NeobankClientException actualException = (NeobankClientException) ex;
        assertThat(actualException.getStatusCode(), equalTo(NOT_FOUND));
    }

    @Test
    @PactTestFor(pactMethod = "updateProfile")
    void testUpdateProfile(MockServer mockServer) {
        ProfileService profileService = new ProfileService(mockServer.getUrl() + PROFILE_BASE_URL,  httpService);
        ProfileInput sampleProfileUpdateInput = TestUtility.getSampleProfileUpdateInput();
        Profile profile = profileService.updateProfile("areza", sampleProfileUpdateInput);

        Profile expectedProfile = TestUtility.getSampleProfile("areza", sampleProfileUpdateInput);

        assertThat(profile, samePropertyValuesAs(expectedProfile, "address"));
        assertThat(profile.getAddress(), samePropertyValuesAs(sampleProfileUpdateInput.getAddress()));
    }

    @Test
    @PactTestFor(pactMethod = "createProfile")
     void testCreateProfile(MockServer mockServer) {
        ProfileService profileService = new ProfileService(mockServer.getUrl() + PROFILE_BASE_URL,  httpService);
        ProfileInput sampleProfileUpdateInput = TestUtility.getSampleProfileUpdateInput();
        Profile profile = profileService.createProfile(sampleProfileUpdateInput);

        assertThat(profile.getId(), notNullValue());
        assertThat(profile.getAddress(), samePropertyValuesAs(sampleProfileUpdateInput.getAddress()));
    }

    private WebClient aWebClient() {
        WebClientErrorConfig webClientErrorConfig = new DefaultWebClientErrorConfig();
        WebClient.Builder builder = WebClient.builder();
        new ErrorHandlingCustomizer(webClientErrorConfig).customize(builder);
        new LoggingCustomizer().customize(builder);
        return builder.build();
    }
}
