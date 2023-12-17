package com.mw.timesheets.domain.person;

import com.google.common.collect.Lists;
import com.mw.timesheets.domain.person.model.PersonDTO;
import com.mw.timesheets.domain.person.model.SearchPersonDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonDataProvider {

    private final PersonMapper personMapper;

    @PersistenceContext
    private EntityManager em;

    public List<PersonDTO> getPersonByCriteria(SearchPersonDTO searchPersonDTO) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PersonEntity> cq = cb.createQuery(PersonEntity.class);
        List<Predicate> predicates = Lists.newArrayList();

        Root<PersonEntity> person = cq.from(PersonEntity.class);
        cq.select(person);
        if (searchPersonDTO.getFirstName() != null)
            predicates.add(cb.like(person.get("firstName"), "%" + searchPersonDTO.getFirstName() + "%"));
        if (searchPersonDTO.getLastName() != null)
            predicates.add(cb.like(person.get("lastName"), "%" + searchPersonDTO.getLastName() + "%"));
        if (searchPersonDTO.getExperience() != null && !searchPersonDTO.getExperience().isEmpty())
            predicates.add(person.get("experience").in(searchPersonDTO.getExperience()));
        if (searchPersonDTO.getPosition() != null && !searchPersonDTO.getPosition().isEmpty())
            predicates.add(person.get("position").in(searchPersonDTO.getPosition()));
        predicates.add(cb.isFalse(person.get("deleted")));
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.select(person);

        return personMapper.toDtos(em.createQuery(cq).getResultList());
    }
}
