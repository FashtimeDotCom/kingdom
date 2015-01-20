/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

/**
 *
 * @author Josue
 *
 */
//TODO change to REQUIRED
//TODO update inherited classes TxType
@Transactional(Transactional.TxType.REQUIRES_NEW)
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

    public <T> T find(Class<T> clazz, Object id) {
        return em.find(clazz, id);
    }

    public <T> List<T> findAll(Class<T> clazz) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(clazz));
        return em.createQuery(cq).getResultList();
    }

    public <T> List<T> findRange(Class<T> clazz, int limit, int offset) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(clazz));
        Query q = em.createQuery(cq);
        q.setMaxResults(limit);
        q.setFirstResult(offset);
        return q.getResultList();
    }

    public <T> long count(Class<T> clazz) {
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<T> rt = cq.from(clazz);
        cq.select(em.getCriteriaBuilder().count(rt));
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
