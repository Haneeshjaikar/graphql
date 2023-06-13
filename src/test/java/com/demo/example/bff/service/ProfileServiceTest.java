package com.demo.example.bff.service;

import com.demo.example.bff.TestUtility;
import com.demo.example.bff.model.Profile;
import com.demo.example.bff.model.ProfileInput;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.mockserver.springtest.MockServerPort;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;

import static com.neobank.common.json.Json.toJson;
import static com.demo.example.bff.TestUtility.getSampleProfile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@MockServerTest(("server.url=${profile.url}" + "${profile.path}"))
@ActiveProfiles("test")
 class ProfileServiceTest {

    @MockServerPort
    Integer mockServerPort;

    @Autowired
    private ProfileService profileService;

    private MockServerClient mockServerClient;

    @Test
    void lookupUserTest() {
        Profile sampleProfile = getSampleProfile();
        String profile = toJson(sampleProfile);
        mockServerClient.when(request().withMethod(HttpMethod.GET.name()).withPath("/api/v1/profiles/areza"))
                .respond(response().withStatusCode(200).withBody(profile).withContentType(
                        APPLICATION_JSON)
                );

        Profile actualProfile = profileService.lookupUser("areza");
        assertThat(actualProfile, samePropertyValuesAs(sampleProfile));
    }

    @Test
    void updateProfile() {
        ProfileInput sampleProfileUpdateInput = TestUtility.getSampleProfileUpdateInput();
        Profile profile = TestUtility.getSampleProfile("user-id", sampleProfileUpdateInput);

        mockServerClient.when(request().withMethod(HttpMethod.PUT.name())
                        .withPath("/api/v1/profiles")
                        .withContentType(APPLICATION_JSON)
                        .withBody(new JsonBody(toJson(profile))))
                        .respond(response().withStatusCode(200)
                        .withBody(toJson(profile)).withContentType(APPLICATION_JSON)
                );

        Profile actualProfile = profileService.updateProfile("user-id", sampleProfileUpdateInput);
        assertThat(actualProfile.getId(), equalTo(profile.getId()));
    }
}
