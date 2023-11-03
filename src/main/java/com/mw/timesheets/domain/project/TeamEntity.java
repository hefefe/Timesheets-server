package com.mw.timesheets.domain.project;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.project.type.SprintDurationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TeamEntity extends CommonEntity {

    private String name;

    private boolean deleted;

    private LocalDateTime deletedTime;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "person_team",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private Set<PersonEntity> persons = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamEntity that)) return false;
        return isDeleted() == that.isDeleted() && Objects.equals(getName(), that.getName()) && Objects.equals(getDeletedTime(), that.getDeletedTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), isDeleted(), getDeletedTime());
    }
}
