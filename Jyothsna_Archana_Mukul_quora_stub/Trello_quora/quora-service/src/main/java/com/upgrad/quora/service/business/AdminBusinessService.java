package com.upgrad.quora.service.business;

import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;

import com.upgrad.quora.service.dao.AdminDao;
import com.upgrad.quora.service.dao.UserDao;

import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {

    @Autowired
    private AdminDao adminDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(final String userUuid, final String accessToken)
            throws AuthorizationFailedException, UserNotFoundException {

        UserAuthEntity userAuth = userDao.getUserAuthByToken(accessToken);

        if(userAuth == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else if(userAuth.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }

        UserEntity adminUser = userAuth.getUser();
        UserEntity delUser = userDao.getUserByUuId(userUuid);

        if(delUser == null){
            throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
        }
        else if(adminUser.getRole().equals("nonadmin")){
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }

        //delete the user records from all the files where the User-UUID exists.
        //return the User-uuid.
        //String UUID =
                adminDao.deleteUser(delUser);

        return delUser;
    }
}
