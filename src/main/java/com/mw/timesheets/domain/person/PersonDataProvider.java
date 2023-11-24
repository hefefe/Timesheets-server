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
        Join<PersonEntity, AddressEntity> address = person.join("address");
        Join<PersonEntity, UserEntity> user = person.join("user");
        if (searchPersonDTO.getFirstName() != null)
            predicates.add(cb.like(person.get("firstName"), "%" + searchPersonDTO.getFirstName() + "%"));
        if (searchPersonDTO.getMiddleName() != null)
            predicates.add(cb.like(person.get("middleName"), "%" + searchPersonDTO.getMiddleName() + "%"));
        if (searchPersonDTO.getLastName() != null)
            predicates.add(cb.like(person.get("lastName"), "%" + searchPersonDTO.getLastName() + "%"));
        if (searchPersonDTO.getSex() != null && !searchPersonDTO.getSex().isEmpty())
            predicates.add(person.get("sex").in(searchPersonDTO.getSex()));
        if (searchPersonDTO.getExperience() != null && !searchPersonDTO.getExperience().isEmpty())
            predicates.add(person.get("experience").in(searchPersonDTO.getExperience()));
        if (searchPersonDTO.getCountries() != null && !searchPersonDTO.getCountries().isEmpty())
            predicates.add(address.get("name").in(searchPersonDTO.getCountries()));
        if (searchPersonDTO.getPosition() != null && !searchPersonDTO.getPosition().isEmpty())
            predicates.add(person.get("position").in(searchPersonDTO.getPosition()));
        if (searchPersonDTO.getPhoneNumber() != null)
            predicates.add(cb.like(person.get("phone"), "%" + searchPersonDTO.getPhoneNumber() + "%"));
        if (searchPersonDTO.getStreetName() != null)
            predicates.add(cb.like(address.get("streetName"), "%" + searchPersonDTO.getStreetName() + "%"));
        if (searchPersonDTO.getCity() != null)
            predicates.add(cb.like(address.get("city"), "%" + searchPersonDTO.getCity() + "%"));
        if (searchPersonDTO.getHomeNumber() != null)
            predicates.add(cb.like(address.get("homeNumber"), "%" + searchPersonDTO.getHomeNumber() + "%"));
        if (searchPersonDTO.getZipCode() != null)
            predicates.add(cb.like(address.get("zipCode"), "%" + searchPersonDTO.getZipCode() + "%"));
        if (searchPersonDTO.getEmail() != null)
            predicates.add(cb.like(user.get("email"), "%" + searchPersonDTO.getEmail() + "%"));
        predicates.add(cb.isFalse(person.get("deleted")));
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.select(person);

        return personMapper.toDtos(em.createQuery(cq).getResultList());
    }
}
