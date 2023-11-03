package com.mw.timesheets.domain.project;

import com.google.common.collect.Iterables;
import com.mw.timesheets.domain.task.TaskEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ProjectJobs {

    private final ProjectRepository projectRepository;
    private final ProjectStatisticsRepository projectStatisticsRepository;

    //TODO: sprawdzić ostatni element listy project workflow, bo może być przypał
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void modifyProjects() {
        var projects = projectRepository.findByEndOfSprintBefore(LocalDateTime.now());
        if (projects != null){
            var modifiedProject = projects.stream()
                    .peek(project -> project.setEndOfSprint(project.getEndOfSprint().plusWeeks(project.getSprintDuration().getDuration())))
                    .peek(project -> project.setSprintNumber(project.getSprintNumber()+1))
                    .peek(project -> project.setTasks(project.getTasks().stream()
                            .filter(task -> !Iterables.getLast(project.getWorkflow()).getName().equals(task.getWorkflow().getName()))
                            .collect(Collectors.toList())))
                    .peek(project -> project.setSprintGoal(""))
                    .collect(Collectors.toList());
            projectRepository.saveAll(modifiedProject);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
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