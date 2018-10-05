package com.ispirit.digitalsky.service.api;

import com.ispirit.digitalsky.domain.ApplicantType;
import com.ispirit.digitalsky.domain.UserProfile;

public interface UserProfileService {

    UserProfile profile(long id);

    String resolveOperatorBusinessIdentifier(ApplicantType applicantType, long operatorId);
}
