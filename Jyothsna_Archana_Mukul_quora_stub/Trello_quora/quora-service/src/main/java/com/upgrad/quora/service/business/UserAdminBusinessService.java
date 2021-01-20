package com.upgrad.quora.service.business;

/*import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;*/

import com.upgrad.quora.service.exception.*;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.AnswerEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(final UserEntity userEntity) throws SignUpRestrictedException {

       //check if the user exists by same email-id and throw exception if needed
        UserEntity checkUser = userDao.getUserByEmail(userEntity.getEmail());
        if( checkUser != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        //check if the userName already exists and throw exception if needed
        if(checkUser == null) {
            checkUser = userDao.getUserByUserName(userEntity.getUserName());
            if(checkUser != null)
                throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);

    }
}
