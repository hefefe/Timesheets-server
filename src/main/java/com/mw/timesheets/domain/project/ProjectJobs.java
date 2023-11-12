package com.mw.timesheets.domain.project;

import com.google.common.collect.Iterables;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.statistcs.PersonStatisticsEntity;
import com.mw.timesheets.domain.statistcs.PersonStatisticsRepository;
import com.mw.timesheets.domain.task.TaskEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ProjectJobs {

    private final ProjectRepository projectRepository;
    private final PersonStatisticsRepository personStatisticsRepository;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void modifyProjects() {
        var projects = projectRepository.findByEndOfSprintBeforeAndDeletedFalse(LocalDateTime.now());
        if (projects != null){
            savePersonStatistics(projects);
            projectNextIteration(projects);
        }
    }

    private void savePersonStatistics(List<ProjectEntity> projects){
        projects.stream()
                .map(project -> project.getTeam()
                        .stream()
                        .map(TeamEntity::getPersons)
                        .flatMap(Collection::stream)
                        .map(person -> PersonStatisticsEntity.builder()
                                .person(person)
                                .project(project)
                                .dateOfSnapshot(LocalDate.now())
                                .completionRate(calculateCompletionRate(person, project))
                                .sprintNumber(project.getSprintNumber())
                                .build())
                        .collect(Collectors.toList()))
                .forEach(personStatisticsRepository::saveAll);
    }

    private Double calculateCompletionRate(PersonEntity person, ProjectEntity project){
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

        return committed <= 0 ? 0 : done/committed;
    }

    private void projectNextIteration(List<ProjectEntity> projects){
        //TODO: sprawdzić ostatni element listy project workflow, bo może być przypał
        var modifiedProject = projects.stream()
                .filter(project -> !project.isDeleted())
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