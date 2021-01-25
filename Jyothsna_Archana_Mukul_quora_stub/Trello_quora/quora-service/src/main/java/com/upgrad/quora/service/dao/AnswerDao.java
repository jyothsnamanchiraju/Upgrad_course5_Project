package com.upgrad.quora.service.dao;

        import com.upgrad.quora.service.entity.AnswerEntity;
        import javax.persistence.EntityManager;
        import javax.persistence.NoResultException;

        import com.upgrad.quora.service.entity.QuestionEntity;
        import com.upgrad.quora.service.entity.UserAuthEntity;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Repository;

        import java.util.List;

@Repository
public class AnswerDao {
    @Autowired
    EntityManager entityManager;

    public AnswerDao() {
    }

    public AnswerEntity createAnswer(AnswerEntity answer) {
        try {
            this.entityManager.persist(answer);
            return answer;
        }catch (NoResultException nre){
            return null;
        }
    }

    public AnswerEntity getAnswerById(final String ansUuid) {
        try {
            return entityManager.createNamedQuery("getAnswerById", AnswerEntity.class)
                    .setParameter("uuid", ansUuid)
                    .getSingleResult();
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

    public AnswerEntity editAnswer(AnswerEntity answer) {
        try {
            return entityManager.merge(answer);
        }catch (NoResultException nre) {
            return null;
        }
    }

    public void deleteAnswer(AnswerEntity answerEntity) {
        try {
            entityManager.remove(answerEntity);
        }catch(NoResultException nre){
            System.err.println(nre);
        }
    }

    public List<AnswerEntity> getAllAnswers(QuestionEntity question) {
        try {
            return this.entityManager.createNamedQuery("getAllAnsForQues", AnswerEntity.class).setParameter("question",question).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
    /*
    public AnswerEntity editAnswerContent(AnswerEntity answerEntity) {
        return entityManager.merge(answerEntity);
    }

    public void deleteAnswer(AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
    }

    public List<AnswerEntity> getAllAnswersToQuestion(int id) {
        try {
            return this.entityManager.createNamedQuery("getAnswersForQuestionId", AnswerEntity.class).setParameter("uuid", id).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public AnswerEntity getAnswerForAnswerId(String uuid) {
        try {
            return this.entityManager.createNamedQuery("getAnswerForAnswerId", AnswerEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<AnswerEntity> getAnswersForUserId(Integer userId) {
        try {
            return this.entityManager.createNamedQuery("getAnswersByUserId", AnswerEntity.class).setParameter("user_id", userId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<AnswerEntity> getAllAnswers() {
        try {
            return this.entityManager.createNamedQuery("getAllAnswers", AnswerEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }*/
}
