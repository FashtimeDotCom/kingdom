/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom;

import com.josue.kingdom.application.entity.Application;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

/**
 *
 * @author Josue
 *
 */
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class JpaRepository {

    @PersistenceContext
    protected EntityManager em;

    @Transactional(Transactional.TxType.REQUIRED)
    public <T> void create(T entity) {
        em.persist(entity);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public <T> T update(T entity) {
        return em.merge(entity);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public <T> void delete(T entity) {
        em.remove(em.merge(entity));
    }

    public <T> T find(Class<T> clazz, String appUuid, Object id) {
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<T> c = qb.createQuery(clazz);
        Root<T> root = c.from(clazz);
        Join<T, Application> owner = root.join("application");

        Predicate condition1 = qb.equal(root.get("uuid"), id);
        Predicate condition2 = qb.equal(owner.get("uuid"), appUuid);
        Predicate predicate = qb.and(condition1, condition2);
        c.where(predicate);
        TypedQuery<T> q = em.createQuery(c);
        List<T> result = q.getResultList();
        return extractSingleResultFromList(result);
    }

    public <T> List<T> findAll(Class<T> clazz, String appUuid) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(clazz));
        return em.createQuery(cq).getResultList();
    }

    public <T> long count(Class<T> clazz, String appUuid) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<T> root = cq.from(clazz);

        Join<T, Application> owner = root.join("application");
        Predicate appPredicate = em.getCriteriaBuilder().equal(owner.get("uuid"), appUuid);

        cq.select(em.getCriteriaBuilder().count(root)).where(appPredicate);
        Query q = em.createQuery(cq);
        return (long) q.getSingleResult();
    }

    /*
     * Extract the first result from a given List, its useful to avoid exception handling for each query of single result
     */
    protected <T> T extractSingleResultFromList(List<T> results) {
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

}
