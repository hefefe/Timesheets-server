package com.mw.timesheets.domain.person;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.person.type.Experience;
import com.mw.timesheets.domain.person.type.Position;
import com.mw.timesheets.domain.project.TeamEntity;
import com.mw.timesheets.domain.task.CommentsEntity;
import com.mw.timesheets.domain.task.TaskEntity;
import com.mw.timesheets.domain.timetrack.HistoryEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.validator.internal.util.stereotypes.Lazy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE PERSON SET deleted = true, deleted_time = NOW() WHERE id=?")
public class PersonEntity extends CommonEntity {

    private String firstName;

    private String middleName;

    private String lastName;

    private String sex;

    private String peselPassportNumber;

    private LocalDate dateOfBirth;

    private LocalDate dateOfEmployment;

    @Column(columnDefinition="BLOB")
    private byte[] photo;

    @Enumerated(EnumType.STRING)
    private Experience experience;

    @Enumerated(EnumType.STRING)
    private Position position;

    private boolean deleted = Boolean.FALSE;

    private LocalDateTime deletedTime;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private AddressEntity address;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", referencedColumnName = "id")
    private ContactEntity contact;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToMany(mappedBy = "persons")
    private Set<TeamEntity> teams = new HashSet<>();

    @OneToMany(mappedBy = "person")
    private List<TaskEntity> tasks;

    @OneToMany(mappedBy = "person")
    private List<HistoryEntity> history;

}
