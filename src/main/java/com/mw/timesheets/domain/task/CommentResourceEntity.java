package com.mw.timesheets.domain.task;

import com.mw.timesheets.commons.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResourceEntity extends CommonEntity {

    @Column(columnDefinition="BLOB")
    private byte[] resource;

    @ManyToOne
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private CommentsEntity comment;
}
