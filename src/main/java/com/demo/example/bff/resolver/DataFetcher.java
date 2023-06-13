package com.demo.example.bff.resolver;

import com.demo.example.bff.model.Profile;
import com.demo.example.bff.service.ProfileService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.AllArgsConstructor;

import static org.apache.commons.lang3.StringUtils.isBlank;

@DgsComponent
@AllArgsConstructor
public class DataFetcher {
    private final ProfileService service;

    @DgsQuery
    public Profile profile(@InputArgument String profileId) {
        if (isBlank(profileId)) {
            throw new RuntimeException("Profile Id must be specified");
        }
        return service.lookupUser(profileId);
    }
}