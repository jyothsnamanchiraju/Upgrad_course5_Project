package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.api.model.QuestionEditResponse;
import com.upgrad.quora.api.model.QuestionDeleteResponse;

import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;

import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.Base64;
import java.util.List;
import java.util.LinkedList;

@RestController
@RequestMapping("question/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/create",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("authorization") final String authorization ) throws AuthorizationFailedException {

        final QuestionEntity newQuestion = new QuestionEntity();

        newQuestion.setUuid(UUID.randomUUID().toString());
        newQuestion.setContent(questionRequest.getContent());
        newQuestion.setqDate(ZonedDateTime.now());

        final QuestionEntity createdQuestion = questionBusinessService.createQuestion(newQuestion, authorization);

        QuestionResponse qResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(qResponse, HttpStatus.CREATED);

        }

/*****
    /// Below method fetches Get all questions
 *****/
   @RequestMapping(method = RequestMethod.GET, path = "/all")
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String accessToken)
                                                        throws AuthorizationFailedException {

     //   UserAuthEntity userAuth = userDao.getUserAuthByToken(accessToken);

        List<QuestionEntity> allQuestions = questionBusinessService.getAllQuestions(accessToken);
        List<QuestionDetailsResponse> questionDetailsList = buildQuestionDetailsResponseList(allQuestions);

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsList, HttpStatus.OK) ;
        }

/*****
        //Below method is used to build list of questions
 *****/
    private List<QuestionDetailsResponse> buildQuestionDetailsResponseList(List<QuestionEntity> allQuestions) {
        List<QuestionDetailsResponse> questionDetailsList = new LinkedList<>();

        for (QuestionEntity question : allQuestions) {
            QuestionDetailsResponse questionDetails = new QuestionDetailsResponse();
            questionDetails.setId(question.getUuid());
            questionDetails.setContent(question.getContent());
            questionDetailsList.add(questionDetails);
        }

        return questionDetailsList;
    }

/*******
 ****   Below method is invoked to edit an existing question
 ******/
@RequestMapping(method = RequestMethod.PUT, path = "/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public ResponseEntity<QuestionEditResponse> editQuestion(@PathVariable("questionId") final String questionId,
                                                         @RequestHeader("authorization") final String accessToken,
                                                         final QuestionEditRequest questionEditRequest)
                                                         throws InvalidQuestionException, AuthorizationFailedException {

    questionBusinessService.editQuestion(questionId, questionEditRequest.getContent(), accessToken);
    QuestionEditResponse response = new QuestionEditResponse().id(questionId).status("QUESTION EDITED");

    return new ResponseEntity<QuestionEditResponse>(response, HttpStatus.OK);
}

/*******
 ****   Below methos id invoked to delete the question by its Id
*********/
@RequestMapping(method = RequestMethod.DELETE, path = "/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@RequestHeader("authorization") final String accessToken,
                                                             @PathVariable("questionId") final String questionId)
                                                             throws InvalidQuestionException, AuthorizationFailedException {

    questionBusinessService.deleteQuestion(questionId, accessToken);
    QuestionDeleteResponse response = new QuestionDeleteResponse().id(questionId).status("QUESTION DELETED");

    return new ResponseEntity<QuestionDeleteResponse>(response, HttpStatus.OK);
}

/****
****    Below method is ivoked to fetch all questions posted by the user
 ****/
@RequestMapping(method = RequestMethod.GET, path = "/all/{userId}")
public ResponseEntity<List<QuestionDetailsResponse>> getQuestionsByUser(@RequestHeader("authorization") final String loginUserAccessToken,
                                                                        @PathVariable("userId") final String quesUserId)
                                                     throws AuthorizationFailedException, UserNotFoundException {

    //UserEntity user = userDao.getUserByUuId(userId);
    //UserAuthEntity userAuth = userDao.getUserAuthByToken(accessToken);

    List<QuestionEntity> allQuestions = questionBusinessService.getAllQuestionsByUser(quesUserId, loginUserAccessToken);
    List<QuestionDetailsResponse> questionDetailsList = buildQuestionDetailsResponseList(allQuestions);


    return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsList, HttpStatus.OK);

}

}
