package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/")
public class AdminController {

    @Autowired
    private AdminBusinessService adminBusinessService;

    @RequestMapping(method = RequestMethod.DELETE, path = "/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> userDelete(@PathVariable("userId") final String userUuid,
                                                           @RequestHeader("authorization") final String authorization)
                                               throws AuthorizationFailedException, UserNotFoundException {

        UserEntity user = adminBusinessService.deleteUser(userUuid, authorization);

        UserDeleteResponse userDeleteResponse = new UserDeleteResponse();

        userDeleteResponse.setId(user.getUuid());
        userDeleteResponse.setStatus("USER SUCCESSFULLY DELETED");

        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
    }

}
