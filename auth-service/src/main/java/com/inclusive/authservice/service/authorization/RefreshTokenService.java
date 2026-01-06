package com.inclusive.authservice.service.authorization;

import com.inclusive.authservice.entity.UserAccount;

public interface RefreshTokenService {

    String create(UserAccount userAccount);
}
