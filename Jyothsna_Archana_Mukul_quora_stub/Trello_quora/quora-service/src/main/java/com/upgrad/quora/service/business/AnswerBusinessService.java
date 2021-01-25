package com.upgrad.quora.service.business;

        import com.upgrad.quora.service.dao.AnswerDao;
        import com.upgrad.quora.service.dao.QuestionDao;
        import com.upgrad.quora.service.dao.UserDao;
        import com.upgrad.quora.service.entity.AnswerEntity;
        import com.upgrad.quora.service.entity.QuestionEntity;
        import com.upgrad.quora.service.entity.UserAuthEntity;
        import com.upgrad.quora.service.entity.UserEntity;
        import java.time.ZonedDateTime;
        import java.util.List;
        import java.util.UUID;

        import com.upgrad.quora.service.exception.AnswerNotFoundException;
        import com.upgrad.quora.service.exception.AuthorizationFailedException;
        import com.upgrad.quora.service.exception.InvalidQuestionException;

        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Propagation;
        import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerBusinessService {

    @Autowired
    private QuestionBusinessService questionBusinessService;
    @Autowired
    private UserAdminBusinessService userBusinessService;
    @Autowired
    private AnswerDao answerDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private QuestionDao questionDao;

    public AnswerBusinessService() {
    }

    /*
    public AnswerEntity getAnswerForAnswerId(String uuid) {
        return answerDao.getAnswerForAnswerId(uuid);
    }

    public boolean isUserAnswerOwner(UserEntity user, UserEntity answerOwner) {
        boolean isUserAnswerOwner = false;
        if (user != null && answerOwner != null && user.getUuid() != null && !user.getUuid().isEmpty() && answerOwner.getUuid() != null && !answerOwner.getUuid().isEmpty() && user.getUuid().equals(answerOwner.getUuid())) {
            isUserAnswerOwner = true;
            return isUserAnswerOwner;
        }
        return isUserAnswerOwner;
    }
*/
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final String userAccessToken, String quesUuid, String answerContent)
                        throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity question = questionDao.getQuestionById(quesUuid);
            if(question == null)
                   throw new InvalidQuestionException("QUES-001","The question entered is invalid");

        UserAuthEntity userAuth = userDao.getUserAuthByToken(userAccessToken);
            if(userAuth == null)
                    throw new AuthorizationFailedException("ATHR-001","User has not signed in");
            else if(userAuth.getLogoutAt() !=null)
                    throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post an answer");

        AnswerEntity answer = new AnswerEntity();
        answer.setUuid(UUID.randomUUID().toString());
        answer.setContent(answerContent);
        answer.setAnsDate(ZonedDateTime.now());
        answer.setUser(userAuth.getUser());
        answer.setQuestion(question);

        AnswerEntity createdAnswer = answerDao.createAnswer(answer);

        return createdAnswer;
    }



    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity updateAnswer( String userAccessToken, String ansUuid, String answerContent)
                        throws AuthorizationFailedException, AnswerNotFoundException {


        UserAuthEntity userAuth = userDao.getUserAuthByToken(userAccessToken);
        AnswerEntity   answer = answerDao.getAnswerById(ansUuid);
        if(userAuth == null)
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        else if(userAuth.getLogoutAt() !=null)
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to edit an answer");
        else if(answer == null)
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        else if(userAuth.getUser().getId() != answer.getUser().getId())
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner can edit the answer");

        answer.setContent(answerContent);
        AnswerEntity  editedAnswer = answerDao.editAnswer(answer);

        return editedAnswer;

    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(final String userAccessToken, String ansUuid)
                        throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthEntity userAuth = userDao.getUserAuthByToken(userAccessToken);
        AnswerEntity   answer = answerDao.getAnswerById(ansUuid);
        if(userAuth == null)
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        else if(userAuth.getLogoutAt() !=null)
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete an answer");
        else if(answer == null)
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        else if(userAuth.getUser().getId() != answer.getUser().getId() && (userAuth.getUser().getRole().equals("nonadmin")))
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");

        answerDao.deleteAnswer(answer);
    }

    public List<AnswerEntity> getAllAnswersToQuestion(final String userAccessToken, String quesUuid)
                              throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuth = userDao.getUserAuthByToken(userAccessToken);
        if(userAuth == null)
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        else if(userAuth.getLogoutAt() !=null)
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get the answers");

        QuestionEntity question = questionDao.getQuestionById(quesUuid);
        if(question == null)
            throw new InvalidQuestionException("QUES-001","The question with entered uuid whose details are to be seen does not exist");

        List<AnswerEntity> answerList = answerDao.getAllAnswers(question);

        return answerList;

    }

}
