package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserEntity;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.service.business.UserAdminBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;

import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.Base64;


@RestController
@RequestMapping("user/")
public class UserController {

    @Autowired
    private SignupBusinessService signupBusinessService;
/* The below method signup() is invoked to create new users
** It throws SignUpRestrictedException in case the user already exists.
** New user records will be created in users table of the databae.
* */
    @RequestMapping(method = RequestMethod.POST, path = "/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

        final UserEntity userEntity = new UserEntity();

        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setSalt("1234abc");
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        final UserEntity createdUserEntity = signupBusinessService.signup(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }


    @Autowired
    private UserAdminBusinessService userAdminBusinessService;
   /*The below method signin() is invoked for user signin request
   ** It throws AuthenticationFailedException in case the user does not exist.
   ** User Authorization records will be created in the user_auth table of th edatabase.
   **/
   @RequestMapping(method = RequestMethod.POST, path = "/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
   public ResponseEntity<SigninResponse> Signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

       byte[] decode = Base64.getDecoder().decode(authorization);

       String decodedText = new String(decode);
       String[] decodedArray = decodedText.split(":");

       UserAuthEntity userAuth = userAdminBusinessService.authenticate(decodedArray[0], decodedArray[1]);
       UserEntity user = userAuth.getUser();

       String userId = user.getUuid();

       SigninResponse signinResponse = new SigninResponse();
       signinResponse.setId(userId);
       signinResponse.setMessage("SIGNED IN SUCCESSFULLY");

       HttpHeaders headers = new HttpHeaders();
       headers.add("access-token", userAuth.getAccessToken());
       return new ResponseEntity<SigninResponse>( signinResponse, headers, HttpStatus.OK);
   }

   /*The below method is the Signout method.
   ** This method receives the parameter of "access-token" from UserAuthEntity as the authorization parameter.
   ** Checks for appropriate records in the User_Auth table, updates it with signout time.
   ** Returns User-UUID on successful signout.
   * */

    @RequestMapping(method = RequestMethod.POST, path = "/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> Signout(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {

        UserEntity userEntity = userAdminBusinessService.signout(authorization);
        String userId = userEntity.getUuid();

        SignoutResponse signoutResponse = new SignoutResponse();
        signoutResponse.setId(userId);
        signoutResponse.setMessage("SIGNED OUT SUCCESSFULLY");

        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }

}

