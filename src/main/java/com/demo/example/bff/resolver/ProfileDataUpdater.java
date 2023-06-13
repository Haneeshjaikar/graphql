package com.demo.example.bff.resolver;

import com.demo.example.bff.model.Profile;
import com.demo.example.bff.model.ProfileInput;
import com.demo.example.bff.service.ProfileService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.InputArgument;
import lombok.AllArgsConstructor;

@DgsComponent
@AllArgsConstructor
public class ProfileDataUpdater {
    private final ProfileService service;

    @DgsData(parentType = "Mutation", field = "updateProfile")
    public Profile updateProfile(@InputArgument("profileId") String profileId,
                                 @InputArgument("profileInput") ProfileInput profileInput) {
        return service.updateProfile(profileId, profileInput);
    }

    @DgsData(parentType = "Mutation", field = "createProfile")
    public Profile createProfile(@InputArgument("profileInput") ProfileInput profileInput) {
        return service.createProfile(profileInput);
    }
}