package com.upgrad.quora.service.business;


import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUserProfile(final String userUuid, final String accessToken)
                        throws AuthorizationFailedException, UserNotFoundException {

        UserAuthEntity userAuth = userDao.getUserAuthByToken(accessToken);

        if(userAuth == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else if(userAuth.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        UserEntity user = userDao.getUserByUuId(userUuid);

        if(user == null){
            throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
        }

        return user;

    }

}
