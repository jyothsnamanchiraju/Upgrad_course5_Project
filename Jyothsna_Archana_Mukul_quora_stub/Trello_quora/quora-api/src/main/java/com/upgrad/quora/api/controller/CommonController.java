package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.entity.UserEntity;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("common/")
public class CommonController {

    @Autowired
    private CommonBusinessService commonBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/userProfile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> userProfile(@PathVariable("userId") final String userUuid,
                                                           @RequestHeader("authorization") final String authorization)
                                                           throws AuthorizationFailedException, UserNotFoundException {

        UserEntity  user = commonBusinessService.getUserProfile(userUuid, authorization);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
            userDetailsResponse.setFirstName(user.getFirstName());
            userDetailsResponse.setLastName(user.getLastName());
            userDetailsResponse.setUserName(user.getUserName());
            userDetailsResponse.setEmailAddress(user.getEmail());
            userDetailsResponse.setCountry(user.getCountry());
            userDetailsResponse.setAboutMe(user.getAboutMe());
            userDetailsResponse.setDob(user.getDob());
            userDetailsResponse.setContactNumber(user.getContactNumber());

        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);

    }
}
