package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.*;
import javax.transaction.Transactional;

@Repository
public class AdminDao {
    @PersistenceContext
    private EntityManager entityManager;

   @Transactional
    public void deleteUser(UserEntity user) throws Exception{

       try {
           entityManager.remove(user);

       }catch (NoResultException nre){
          System.err.println(nre);
       }

    }
}
