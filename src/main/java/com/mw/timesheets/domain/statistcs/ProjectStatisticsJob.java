package com.mw.timesheets.domain.statistcs;

import com.google.common.collect.Iterables;
import com.mw.timesheets.domain.project.ProjectEntity;
import com.mw.timesheets.domain.project.ProjectRepository;
import com.mw.timesheets.domain.task.TaskEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ProjectStatisticsJob {

    private final ProjectRepository projectRepository;
    private final ProjectStatisticsRepository projectStatisticsRepository;

    @Scheduled(cron = "55 59 23 * * *")
    @Transactional
    public void saveProgress() {
        var projects = projectRepository.findAll();
        var projectStatistics = projects.stream()
                .map(project -> ProjectStatisticsEntity.builder()
                        .day(LocalDate.now())
                        .project(project)
                        .sprintNumber(project.getSprintNumber())
                        .storyPointsCommitted(calculateCommittedStoryPoints(project))
                        .storyPointsDone(calculateDoneStoryPoints(project))
                        .build())
                .collect(Collectors.toList());
        projectStatisticsRepository.saveAll(projectStatistics);

    }

    private Integer calculateCommittedStoryPoints(ProjectEntity project){
        return project.getTasks().stream()
                .map(TaskEntity::getStoryPoints)
                .reduce(0, Integer::sum);
    }
    private Integer calculateDoneStoryPoints(ProjectEntity project){
        return project.getTasks().stream()
                .filter(task -> Iterables.getLast(project.getWorkflow()).getName().equals(task.getWorkflow().getName()))
                .map(TaskEntity::getStoryPoints)
                .reduce(0, Integer::sum);
    }
}
