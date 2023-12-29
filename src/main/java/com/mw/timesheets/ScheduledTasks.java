package com.mw.timesheets;

import com.google.common.collect.Iterables;
import com.mw.timesheets.commons.CommonEntity;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.project.ProjectEntity;
import com.mw.timesheets.domain.project.ProjectRepository;
import com.mw.timesheets.domain.statistcs.PersonStatisticsEntity;
import com.mw.timesheets.domain.statistcs.PersonStatisticsRepository;
import com.mw.timesheets.domain.statistcs.ProjectStatisticsEntity;
import com.mw.timesheets.domain.statistcs.ProjectStatisticsRepository;
import com.mw.timesheets.domain.task.TaskEntity;
import com.mw.timesheets.domain.task.TaskRepository;
import com.mw.timesheets.domain.task.TaskService;
import com.mw.timesheets.domain.timetrack.TimeTrackEntity;
import com.mw.timesheets.domain.timetrack.TimeTrackRepository;
import com.mw.timesheets.domain.timetrack.TimeTrackService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.mw.timesheets.commons.util.DateUtils.getSystemTime;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTasks {

    private final ProjectRepository projectRepository;
    private final ProjectStatisticsRepository projectStatisticsRepository;
    private final TimeTrackRepository timeTrackRepository;
    private final TimeTrackService timeTrackService;
    private final PersonStatisticsRepository personStatisticsRepository;
    private final TaskService taskService;

    private void endTimers() {
        timeTrackRepository.findAll().stream()
                .map(TimeTrackEntity::getPerson)
                .toList()
                .forEach(person -> timeTrackService.stopTrackingTime(person.getId()));
    }

    public void saveProgress(List<ProjectEntity> projects) {
        var projectStatistics = projects.stream()
                .filter(project -> project.getSprintNumber() != 0)
                .filter(project -> !project.isDeleted())
                .map(project -> ProjectStatisticsEntity.builder()
                        .day(getSystemTime().toLocalDate())
                        .project(project)
                        .sprintNumber(project.getSprintNumber())
                        .storyPointsCommitted(calculateCommittedStoryPoints(project))
                        .storyPointsDone(calculateDoneStoryPoints(project))
                        .build())
                .collect(Collectors.toList());
        projectStatisticsRepository.saveAll(projectStatistics);
    }

    private Integer calculateCommittedStoryPoints(ProjectEntity project) {
        return project.getTasks().stream()
                .filter(task -> !task.isDeleted())
                .map(TaskEntity::getStoryPoints)
                .reduce(0, Integer::sum);
    }

    private Integer calculateDoneStoryPoints(ProjectEntity project) {
        return project.getTasks().stream()
                .filter(task -> !task.isDeleted())
                .filter(task -> Iterables.getLast(project.getWorkflow()).getName().equals(task.getWorkflow().getName()))
                .map(TaskEntity::getStoryPoints)
                .reduce(0, Integer::sum);
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void modifyProjects() {
        var projects = projectRepository.findByEndOfSprintBeforeAndDeletedFalse(getSystemTime().plusHours(1));
        if (projects.isEmpty()) return;
        saveProgress(projects);
        savePersonStatistics(projects);
        projectNextIteration(projects);
    }

    private void savePersonStatistics(List<ProjectEntity> projects) {
        projects.stream()
                .filter(project -> project.getSprintNumber() != 0)
                .map(project -> project.getPersonsInProject().stream()
                        .map(person -> PersonStatisticsEntity.builder()
                                .person(person)
                                .project(project)
                                .dateOfSnapshot(getSystemTime().toLocalDate())
                                .completionRate(calculateCompletionRate(person, project))
                                .sprintNumber(project.getSprintNumber())
                                .build())
                        .collect(Collectors.toList()))
                .forEach(personStatisticsRepository::saveAll);
    }

    private Double calculateCompletionRate(PersonEntity person, ProjectEntity project) {
        var tasks = person.getTasks();
        var committed = tasks.stream()
                .filter(task -> task.getProject().equals(project) && !task.isDeleted())
                .map(TaskEntity::getStoryPoints)
                .reduce(0, Integer::sum)
                .doubleValue();
        var done = tasks.stream()
                .filter(task -> task.getProject().equals(project) && !task.isDeleted())
                .filter(task -> Iterables.getLast(project.getWorkflow()).getName().equals(task.getWorkflow().getName()))
                .map(TaskEntity::getStoryPoints)
                .reduce(0, Integer::sum)
                .doubleValue();
        return committed <= 0 ? 0 : done / committed;
    }

    private void projectNextIteration(List<ProjectEntity> projects) {
        var modifiedProject = projects.stream()
                .filter(project -> !project.isDeleted())
                .peek(project -> project.setEndOfSprint(calculateEndOfSprint(project)))
                .peek(project -> project.setSprintNumber(project.getSprintNumber() + 1))
                .peek(this::addItemsToCollection)
                .collect(Collectors.toList());
        projectRepository.saveAll(modifiedProject);
    }

    private void addItemsToCollection(ProjectEntity project){
        taskService.rejectTask(project.getTasks().stream().filter(task -> Iterables.getLast(project.getWorkflow()).getName().equals(task.getWorkflow().getName())).map(CommonEntity::getId).collect(Collectors.toList()));
    }

    private LocalDateTime calculateEndOfSprint(ProjectEntity project) {
        var endOfSprint = project.getEndOfSprint();
        var sprintLength = project.getSprintDuration();
        do {
            endOfSprint = endOfSprint.plusWeeks(sprintLength.getDuration());
        } while (endOfSprint.isBefore(getSystemTime()));
        return endOfSprint;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void saveProjectStats() {
        saveProgress(projectRepository.findAll());
        endTimers();
    }
}
