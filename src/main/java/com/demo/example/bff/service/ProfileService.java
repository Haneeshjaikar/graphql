package com.demo.example.bff.service;

import com.neobank.common.json.Json;
import com.neobank.common.webclient.HttpService;
import com.neobank.common.webclient.model.RequestAttributes;
import com.demo.example.bff.model.Profile;
import com.demo.example.bff.model.ProfileInput;
import lombok.extern.flogger.Flogger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import java.util.Map;

import static java.util.Collections.emptyMap;

@Service
@Flogger
public class ProfileService {
    private static final Map<String, String> HEADERS = Map.of(
            "Content-Type", String.valueOf(MediaType.APPLICATION_JSON));
    private final String profileUrl;
    private final HttpService httpService;

    public ProfileService(@Value("${data.api.get-profile}") String profileUrl, @Qualifier("webClientService") HttpService httpService) {
        this.profileUrl = profileUrl;
        this.httpService = httpService;
    }

    public Profile lookupUser(@NotNull String id) {
        log.atInfo().log("Calling domain profile service");
        String url = String.valueOf(new StringBuilder(profileUrl).append("/").append(id));
        Profile newProfile = httpService.get(getRequestAttributes(url, emptyMap()), Profile.class);
        log.atInfo().log("Profile returned: %s", newProfile.getId());
        return newProfile;
    }

    public Profile updateProfile(@NotNull String id, @NotNull ProfileInput profileInput) {
        String message = "calling domain profile service for update for id: " + id;
        Map<String, Object> map = Json.toMap(profileInput);
        map.put("Id", id);
        Profile profile = httpService.execute(getRequestAttributes(profileUrl,
                HEADERS), Profile.class, map, HttpMethod.PUT);
        log.atInfo().log(message);
        return profile;
    }

    public Profile createProfile(@NotNull ProfileInput profileInput) {
        String message = "calling domain profile service for creation of profile...";
        Profile profile = httpService.post(getRequestAttributes(profileUrl, HEADERS), Profile.class, profileInput);
        log.atInfo().log(message);
        return profile;
    }

    private static RequestAttributes getRequestAttributes(String url, Map<String, String> headers) {
        return (headers == null || headers.size() == 0)
                ? RequestAttributes.builder().url(url).build() : RequestAttributes.builder().url(url)
                .requestHeaders(headers).build();
    }
}