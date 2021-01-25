package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;

import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.*;
import javax.transaction.Transactional;

import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity question) {

        try {
            entityManager.persist(question);
            return question;
        } catch (NoResultException nre) {
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

    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }


    public void editQuestion(QuestionEntity question) {
        try {
            entityManager.merge(question);

        }catch (NoResultException nre){
            System.err.println(nre);
        }
    }

    public QuestionEntity getQuestionById(final String QuesUuid) {
        try {
            return entityManager.createNamedQuery("getQuestionById", QuestionEntity.class)
                    .setParameter("uuid", QuesUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void deleteQuestion(QuestionEntity question) {
        try {
            entityManager.remove(question);

        }catch (NoResultException nre){
            System.err.println(nre);
        }
    }


    public List<QuestionEntity> getAllQuestionsByUser(UserEntity user) {
        try {
            List questionsList = entityManager.createNamedQuery("getAllQuestionsByUser")
                    .setParameter("user", user)
                    .getResultList();
            return questionsList;
        } catch (NoResultException e) {
            System.out.println(e.getMessage());
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

}
