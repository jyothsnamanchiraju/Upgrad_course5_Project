package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity) {
       try {
           entityManager.persist(userEntity);
           return userEntity;
       }catch (NoResultException nre){
           return null;
       }
    }

    public UserEntity getUserByUuId(final String uuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", uuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserByUserName(final String userName) {
        try {
            return entityManager.createNamedQuery("userByUserName", UserEntity.class).setParameter("username", userName).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

   public UserAuthEntity createUserAuth(final UserAuthEntity userAuth){
       try {
           entityManager.persist(userAuth);
           return userAuth;
       }catch (NoResultException nre){
           return null;
       }
   }

    public UserAuthEntity getUserAuthByToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthByToken", UserAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuthEntity updateLogoutTime(final UserAuthEntity userAuth){
        try {
            entityManager.merge(userAuth);
            return userAuth;
        }catch (NoResultException nre){
            return null;
        }
    }

}
