package com.upgrad.quora.service.business;

import com.upgrad.quora.service.exception.*;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.AnswerEntity;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(final UserEntity userEntity) throws SignUpRestrictedException {

        //check if the user exists by same email-id and throw exception if needed
        UserEntity checkUser = userDao.getUserByEmail(userEntity.getEmail());
        if (checkUser != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        //check if the userName already exists and throw exception if needed
        if (checkUser == null) {
            checkUser = userDao.getUserByUserName(userEntity.getUserName());
            if (checkUser != null)
                throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);

    }


    /*
     ** The Authenticate() method is invoked by UserController for Signin funcionality.*
     ** This method records the user - login details in the UserAuth table of the database.
     */
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(final String username, final String password) throws AuthenticationFailedException {

        UserEntity userEntity = userDao.getUserByUserName(username);
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
        if (encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuth = new UserAuthEntity();
            userAuth.setUser(userEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuth.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));

            userAuth.setUuid(UUID.randomUUID().toString());
            userAuth.setLoginAt(now);
            userAuth.setExpiresAt(expiresAt);

            userDao.createUserAuth(userAuth);

            return userAuth;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password Failed");
        }
    }

    /*
     ** The signout() method is invoked by UserController for Signout funcionality.*
     ** This method updates the user - logout time in the UserAuth table of the database.
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signout(final String accessToken) throws SignOutRestrictedException {
        UserAuthEntity userAuth = userDao.getUserAuthByToken(accessToken);

        if (userAuth == null || userAuth.getLogoutAt() != null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }

        final ZonedDateTime now = ZonedDateTime.now();
        userAuth.setLogoutAt(now);
        userDao.updateLogoutTime(userAuth);

        UserEntity user = userAuth.getUser();

        return user;
    }


    public boolean isUserSignedIn(UserAuthEntity userAuthTokenEntity) {
        boolean isUserSignedIn = false;
        if (userAuthTokenEntity != null && userAuthTokenEntity.getLoginAt() != null && userAuthTokenEntity.getExpiresAt() != null) {
            if ((userAuthTokenEntity.getLogoutAt() == null)) {
                isUserSignedIn = true;
            }
        }
        return isUserSignedIn;
    }

    public boolean isUserAdmin(UserEntity user) {
        boolean isUserAdmin = false;
        if (user != null && "admin".equals(user.getRole())) {
            isUserAdmin = true;
        }
        return isUserAdmin;
    }
}
