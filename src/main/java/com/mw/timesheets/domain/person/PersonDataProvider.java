package com.mw.timesheets.domain.person;

import com.mw.timesheets.domain.person.model.SearchPersonDTO;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonDataProvider {

    @Autowired
    EntityManager entityManager;
    JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
    QPersonEntity person = QPersonEntity.personEntity;

    public List<PersonEntity> getPersonByCriteria(SearchPersonDTO searchPersonDTO) {
        return queryFactory.selectFrom(person)
                .where((searchPersonDTO.getFirstName() != null ? person.firstName
                        .like(searchPersonDTO.getFirstName()) : Expressions.asBoolean(true).isTrue())
                        .and(searchPersonDTO.getMiddleName() != null? person.middleName.like(searchPersonDTO.getMiddleName()) : Expressions.asBoolean(true).isTrue())
                        .and(searchPersonDTO.getLastName() != null? person.lastName.like(searchPersonDTO.getLastName()) : Expressions.asBoolean(true).isTrue())
                        .and(searchPersonDTO.getSex() != null && !searchPersonDTO.getSex().isEmpty()? person.sex.in(searchPersonDTO.getSex()) : Expressions.asBoolean(true).isTrue())
                        .and(searchPersonDTO.getPhoneNumber() != null ? person.contact.phone.like(searchPersonDTO.getLastName()) : Expressions.asBoolean(true).isTrue())
                        .and(searchPersonDTO.getExperience() != null && !searchPersonDTO.getExperience().isEmpty()? person.experience.in(searchPersonDTO.getExperience()) : Expressions.asBoolean(true).isTrue())
                        .and(searchPersonDTO.getCountries() != null && !searchPersonDTO.getCountries().isEmpty()? person.address.country.name.in(searchPersonDTO.getCountries()) : Expressions.asBoolean(true).isTrue())
                        .and(searchPersonDTO.getStreetName() != null? person.address.streetName.like(searchPersonDTO.getStreetName()) : Expressions.asBoolean(true).isTrue())
                        .and(searchPersonDTO.getCity() != null? person.address.city.like(searchPersonDTO.getCity()) : Expressions.asBoolean(true).isTrue())
                        .and(searchPersonDTO.getHomeNumber() != null? person.address.homeNumber.like(searchPersonDTO.getHomeNumber()) : Expressions.asBoolean(true).isTrue())
                        .and(searchPersonDTO.getZipCode() != null? person.address.zipCode.like(searchPersonDTO.getZipCode()) : Expressions.asBoolean(true).isTrue())
                        .and(searchPersonDTO.getEmail() != null? person.contact.email.like(searchPersonDTO.getEmail()) : Expressions.asBoolean(true).isTrue())
                        .and(searchPersonDTO.getPosition() != null && !searchPersonDTO.getPosition().isEmpty()? person.position.in(searchPersonDTO.getPosition()) : Expressions.asBoolean(true).isTrue()))
                .fetch();
    }
}
