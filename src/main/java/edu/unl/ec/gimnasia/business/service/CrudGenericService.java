package edu.unl.ec.gimnasia.business.service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Stateless
public class CrudGenericService {

    @PersistenceContext
    private EntityManager em;

    public <T> T create(T entity) {
        em.persist(entity);
        em.flush();
        em.refresh(entity);
        return entity;
    }

    public <T> T update(T entity) {
        T merged = em.merge(entity);
        em.flush();
        return merged;
    }

    public <T> T find(Class<T> type, Object id) {
        return em.find(type, id);
    }

    public <T> void delete(Class<T> type, Object id) {
        T reference = em.find(type, id);
        if (reference != null) {
            em.remove(reference);
        }
    }

    public <T> List<T> findWithNamedQuery(String namedQueryName) {
        return em.createNamedQuery(namedQueryName).getResultList();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters) {
        Query query = em.createNamedQuery(namedQueryName);
        setParameters(query, parameters);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public <T> T findSingleResultOrNullWithNamedQuery(String namedQueryName, Map<String, Object> parameters) {
        List<T> results = findWithNamedQuery(namedQueryName, parameters);
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() == 1) {
            return results.get(0);
        }
        throw new NonUniqueResultException(
                "El NamedQuery [" + namedQueryName + "] devolvió más de un resultado.");
    }

    private void setParameters(Query query, Map<String, Object> parameters) {
        Set<Map.Entry<String, Object>> entries = parameters.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
    }

    public EntityManager getEntityManager() {
        return em;
    }
}
