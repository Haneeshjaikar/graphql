package com.demo.example.bff.resolver;

import com.demo.example.bff.TestUtility;
import com.demo.example.bff.model.Profile;
import com.demo.example.bff.service.ProfileService;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

 class ProfileDataUpdaterTest {
    private final ProfileService profileService = mock(ProfileService.class);
    private final ProfileDataUpdater profileDataUpdater = new ProfileDataUpdater(profileService);

    @Test
    void updateProfile() {
        var sampleProfileUpdate = TestUtility.getSampleProfile();
        var sampleProfileInput = TestUtility.getSampleProfileUpdateInput();
        when(profileService.updateProfile(any(), any())).thenReturn(sampleProfileUpdate);
        Profile actualProfileResponse = profileDataUpdater.updateProfile("user-id", sampleProfileInput);
        assertThat(actualProfileResponse, samePropertyValuesAs(sampleProfileUpdate));
    }

    @Test
    void createProfile() {
        var sampleProfileCreate = TestUtility.getSampleProfile();
        var sampleProfileInput = TestUtility.getSampleProfileUpdateInput();
        when(profileService.createProfile(any())).thenReturn(sampleProfileCreate);
        Profile actualProfileResponse = profileDataUpdater.createProfile(sampleProfileInput);
        assertThat(actualProfileResponse, samePropertyValuesAs(sampleProfileCreate));
    }
}

