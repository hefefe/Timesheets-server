package com.mw.timesheets.domain.team;

import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.project.ProjectEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

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
@Table(name = "TEAM")
@SQLDelete(sql = "UPDATE TEAM SET deleted = true, deleted_time = NOW() WHERE id=?")
public class TeamEntity extends CommonEntity {

    private String name;

    private boolean deleted;

    private LocalDateTime deletedTime;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "person_team",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private Set<PersonEntity> persons = new HashSet<>();

    @ManyToMany(mappedBy = "team")
    private Set<ProjectEntity> projects = new HashSet<>();

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
