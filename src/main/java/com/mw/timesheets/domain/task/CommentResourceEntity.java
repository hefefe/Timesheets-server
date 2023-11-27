package com.mw.timesheets.domain.task;

import com.mw.timesheets.commons.CommonEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "COMMENT_RESOURCES")
public class CommentResourceEntity extends CommonEntity {

    @Column(columnDefinition = "LONGBLOB")
    private byte[] resource;

    @ManyToOne
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private CommentEntity comment;

    private String extension;
}
