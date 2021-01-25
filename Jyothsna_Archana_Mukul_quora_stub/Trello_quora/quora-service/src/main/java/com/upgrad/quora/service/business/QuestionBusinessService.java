package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;

import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

//import javax.transaction.Transactional;
import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    /***
     *
     * @param newQuestion
     * @param userAccessToken
     * @return
     * @throws AuthorizationFailedException
     *
     * below method is invoked to create new question records in the database
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity newQuestion, String userAccessToken)
                        throws AuthorizationFailedException {

       UserAuthEntity userAuth = questionDao.getUserAuthByToken(userAccessToken);
       if(userAuth == null)
       {
           throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
       }else if(userAuth.getLogoutAt() != null)
        {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

       newQuestion.setUser(userAuth.getUser());

       QuestionEntity createdQuestion = questionDao.createQuestion(newQuestion);

        return createdQuestion;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(String userAccessToken)
                                throws AuthorizationFailedException {

        UserAuthEntity userAuth = questionDao.getUserAuthByToken(userAccessToken);
        if(userAuth == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        else if(userAuth.getLogoutAt() != null)
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");

        List<QuestionEntity> questions = questionDao.getAllQuestions();
        return questions;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public String editQuestion(String QUuid, String questionContent, String userAccessToken)
                              throws InvalidQuestionException, AuthorizationFailedException {

        UserAuthEntity userAuth = questionDao.getUserAuthByToken(userAccessToken);
        if(userAuth == null)
              throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        else if(userAuth.getLogoutAt() != null)
              throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");

        QuestionEntity editedQuestion = questionDao.getQuestionById(QUuid);
        if(editedQuestion == null)
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        else if(editedQuestion.getUser().getId() != userAuth.getUser().getId())
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");

            editedQuestion.setContent(questionContent);
            questionDao.editQuestion(editedQuestion);
            return QUuid;

    }


    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteQuestion(String quesUuid, String userAccessToken) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuth = questionDao.getUserAuthByToken(userAccessToken);
        if(userAuth == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        else if(userAuth.getLogoutAt() != null)
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");

        QuestionEntity question = questionDao.getQuestionById(quesUuid);

        if(question == null)
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        else if(question.getUser().getId() != userAuth.getUser().getId() && (userAuth.getUser().getRole().equals("nonadmin")))
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");

            questionDao.deleteQuestion(question);
            return quesUuid;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionsByUser(String quesUserUuid, String loginUserAccessToken)
                                throws AuthorizationFailedException, UserNotFoundException{

        UserAuthEntity userAuth = questionDao.getUserAuthByToken(loginUserAccessToken);
        if(userAuth == null)
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        else if(userAuth.getLogoutAt() != null)
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");

        UserEntity quesUser = questionDao.getUserByUuId(quesUserUuid);
        if(quesUser == null)
            throw new UserNotFoundException("USR-001","User with entered uuid whose question details are to be seen does not exist");

        List<QuestionEntity> questionsList = questionDao.getAllQuestionsByUser(quesUser); //.getId());

        return questionsList;
    }
}