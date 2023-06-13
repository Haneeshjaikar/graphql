package com.demo.example.bff.resolver;

import com.demo.example.bff.TestUtility;
import com.demo.example.bff.model.Profile;
import com.demo.example.bff.service.ProfileService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

 class DataFetcherTest {
    private final ProfileService profileService = mock(ProfileService.class);
    private final DataFetcher dataFetcher = new DataFetcher(profileService);

    @Test
    void getValidProfile() {
        when(profileService.lookupUser(any())).thenReturn(TestUtility.getSampleProfile());
        Profile expectedProfile = TestUtility.getSampleProfile();
        Profile actualProfile = dataFetcher.profile("user-id");
        assertThat(actualProfile).usingRecursiveComparison().isEqualTo(expectedProfile);
    }

    @Test
    void getBlankProfile() {
        Exception exception = assertThrows(RuntimeException.class, () -> dataFetcher.profile(""));
        String expectedMessage = "Profile Id must be specified";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}

