package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    User loadByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.resetPasswordToken = :resetPasswordToken")
    User loadByResetPasswordToken(@Param("resetPasswordToken") String resetPasswordToken);

    @Query("SELECT u FROM User u WHERE u.accountVerificationToken = :accountVerificationToken")
    User loadByAccountVerificationToken(@Param("accountVerificationToken") String accountVerificationToken);

}
