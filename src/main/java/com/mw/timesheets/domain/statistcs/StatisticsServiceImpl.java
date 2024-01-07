package com.mw.timesheets.domain.statistcs;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mw.timesheets.commons.IdFromToRequestDTO;
import com.mw.timesheets.commons.errorhandling.CustomErrorException;
import com.mw.timesheets.commons.properties.StatisticsProperties;
import com.mw.timesheets.commons.util.DateUtils;
import com.mw.timesheets.domain.person.PersonEntity;
import com.mw.timesheets.domain.person.PersonRepository;
import com.mw.timesheets.domain.project.ProjectEntity;
import com.mw.timesheets.domain.project.ProjectRepository;
import com.mw.timesheets.domain.statistcs.model.employee.PersonStatisticsDTO;
import com.mw.timesheets.domain.statistcs.model.employee.StoryPointsDoneDTO;
import com.mw.timesheets.domain.statistcs.model.employee.TasksAndHoursDTO;
import com.mw.timesheets.domain.statistcs.model.project.BurnDownDTO;
import com.mw.timesheets.domain.statistcs.model.project.ProjectStatisticsDTO;
import com.mw.timesheets.domain.statistcs.model.project.SprintCompletionDTO;
import com.mw.timesheets.domain.statistcs.model.project.TasksDoneDTO;
import com.mw.timesheets.domain.task.TaskEntity;
import com.mw.timesheets.domain.timetrack.HistoryEntity;
import com.mw.timesheets.domain.timetrack.HistoryRepository;
import com.mw.timesheets.domain.timetrack.TimeTrackService;
import com.mw.timesheets.domain.timetrack.model.HistoryWithTotalTimeDTO;
import com.mw.timesheets.domain.timetrack.model.TimeTrackerHistoryDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final TimeTrackService timeTrackService;
    private final PersonRepository personRepository;
    private final StatisticsProperties statisticsProperties;
    private final ProjectRepository projectRepository;
    private final HistoryRepository historyRepository;

    private final String WORK_HOURS = "WORK_HOURS";
    private final String OVERTIME_HOURS = "OVERTIME_HOURS";
    private final String HOLIDAY_HOURS = "HOLIDAY_HOURS";
    private final String WEEKEND_HOURS = "WEEKEND_HOURS";

    @Override
    public PersonStatisticsDTO generateStatisticsForPerson(IdFromToRequestDTO statisticsRequestDTO) {
        var personId = statisticsRequestDTO.getId();
        var from = statisticsRequestDTO.getFrom();
        var to = statisticsRequestDTO.getTo();

        var person = personRepository.findById(personId).orElseThrow(() -> new CustomErrorException("Person not found", HttpStatus.NOT_FOUND));
        return PersonStatisticsDTO.builder()
                .yearsOfEmployment(getYearsOfEmployment(person))
                .pay(calculatePayForUser(person, from, to, historyEntity -> true))
                .completionRate(getCompletionRate(person, from, to))
                .overtimeRatio(overTimeRatioForPerson(person, from, to))
                .tasksAndHours(getTimeSpentOnTasks(person, from, to))
                .storyPointsDone(getStoryPointsDoneByDate(person, from, to))
                .sumOfStoryPointsDone(getTotalOfStoryPoints(person, from, to))
                .build();
    }

    @Override
    public ProjectStatisticsDTO generateStatisticsForProject(IdFromToRequestDTO statisticsRequestDTO) {
        var projectId = statisticsRequestDTO.getId();
        var from = statisticsRequestDTO.getFrom();
        var to = statisticsRequestDTO.getTo();

        var project = projectRepository.findById(projectId).orElseThrow(() -> new CustomErrorException("project does not exist", HttpStatus.NOT_FOUND));
        return ProjectStatisticsDTO.builder()
                .MoneySpent(moneySpentOnProject(project, from, to))
                .timeTracked(timeTracked(project, from, to))
                .data(getBurnDownChartData(project))
                .numberOfEmployees(getNumberOfEmployees(project))
                .sprintCompletion(getSprintCompletion(project))
                .taskDoneByType(getTaskDoneByType(project, from, to))
                .velocity(velocity(project))
                .tasksDone(getTasksDone(project, from, to))
                .build();
    }

    private BigDecimal calculatePayForUser(PersonEntity person, LocalDate from, LocalDate to, Predicate<HistoryEntity> predicate) {

        return person.getHistory().stream()
                .filter(history -> history.getActivityDate().isAfter(from.minusDays(1)) && history.getActivityDate().isBefore(to.plusDays(1)))
                .map(historyEntity -> historyEntity.getHourlyPay()* (ChronoUnit.MINUTES.between(historyEntity.getStarted(), historyEntity.getEnded())/60.0))
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private Map<String, Double> getWorkingHoursMap(PersonEntity person, LocalDate from, LocalDate to, Predicate<HistoryEntity> predicate) {
        Map<String, Double> workingMap = new HashMap<>();
        var history = timeTrackService.getHistoryOfGivenUser(person.getId(), from, to, predicate, true);
        var holidaysDaysRange = DateUtils.getRangeOfDays(from, to, false, false, true);
        var workingDays = DateUtils.getRangeOfDays(from, to, true, false, false);
        var weekendDays = DateUtils.getRangeOfDays(from, to, false, true, false);
        var expectedHourTime = (long) person.getWorkDuringWeekInHours() / statisticsProperties.getWorkingDays() * statisticsProperties.getTimeInterval();

        var work = (double) getWorkingHours(history,
                timeTrackerHistoryDTO -> workingDays.contains(timeTrackerHistoryDTO.getDateOfActivity()),
                time -> time - expectedHourTime <= 0 ? time : expectedHourTime);
        var overTime = (double) getWorkingHours(history,
                timeTrackerHistoryDTO -> workingDays.contains(timeTrackerHistoryDTO.getDateOfActivity()),
                time -> time - expectedHourTime > 0 ? time - expectedHourTime : 0);
        var holidayWork = (double) getWorkingHours(history, timeTrackerHistoryDTO -> holidaysDaysRange.contains(timeTrackerHistoryDTO.getDateOfActivity()), time -> time);
        var weekendWork = (double) getWorkingHours(history, timeTrackerHistoryDTO -> weekendDays.contains(timeTrackerHistoryDTO.getDateOfActivity()), time -> time);

        workingMap.put(WORK_HOURS, work / statisticsProperties.getTimeInterval());
        workingMap.put(OVERTIME_HOURS, overTime / statisticsProperties.getTimeInterval());
        workingMap.put(HOLIDAY_HOURS, holidayWork / statisticsProperties.getTimeInterval());
        workingMap.put(WEEKEND_HOURS, weekendWork / statisticsProperties.getTimeInterval());
        return workingMap;
    }

    private Long getWorkingHours(HistoryWithTotalTimeDTO history, Predicate<TimeTrackerHistoryDTO> workingDaysPredicate, Function<Long, Long> mapHours) {
        return history.getHistoryDTOs().stream()
                .filter(workingDaysPredicate)
                .map(TimeTrackerHistoryDTO::getTime)
                .map(mapHours)
                .reduce(0L, Long::sum);
    }

    private Double calculateExpectedWorkingHours(Integer workDuringWeekInHours, Integer numberOfDays) {
        return (double) workDuringWeekInHours / statisticsProperties.getWorkingDays() * numberOfDays;
    }

    private Double overTimeRatioForPerson(PersonEntity person, LocalDate from, LocalDate to) {
        var workingHoursMap = getWorkingHoursMap(person, from, to, historyEntity -> true);
        var expectedWorkingHours = calculateExpectedWorkingHours(person.getWorkDuringWeekInHours(), DateUtils.getNormalWorkingDaysCount(from, to));
        return (workingHoursMap.get(OVERTIME_HOURS) + workingHoursMap.get(HOLIDAY_HOURS) + workingHoursMap.get(WEEKEND_HOURS)) / expectedWorkingHours * 100;
    }

    private Double getCompletionRate(PersonEntity person, LocalDate from, LocalDate to) {
        if (person.getStatistics() == null) return 0.0;

        var list = person.getStatistics().stream()
                .filter(statistics -> DateUtils.getRangeOfDays(from, to, true, true, true).contains(statistics.getDateOfSnapshot()))
                .toList();

        if (list.isEmpty()) {
            var collection = person.getProjects().stream()
                    .map(project -> project.getTasks().stream().filter(task -> Objects.equals(task.getPerson().getId(), person.getId())).collect(Collectors.toList()))
                    .map(taskEntities -> new ImmutablePair<>(taskEntities.stream().map(TaskEntity::getStoryPoints).mapToInt(task-> task).sum(), taskEntities.stream().filter(task -> task.getDoneDate() != null).map(TaskEntity::getStoryPoints).mapToInt(task-> task).sum()))
                    .map(pair -> (pair.right*1.0)/ pair.left)
                    .collect(Collectors.toList());

            return getMedian(collection) * 100;
        }

        var modifiedList = list.stream().map(PersonStatisticsEntity::getCompletionRate).collect(Collectors.toList());
        return getMedian(modifiedList) * 100;
    }

    private List<StoryPointsDoneDTO> getStoryPointsDoneByDate(PersonEntity person, LocalDate from, LocalDate to) {
        List<StoryPointsDoneDTO> spDone = DateUtils.getRangeOfDays(from, to, true, true, true).stream()
                        .map(date -> StoryPointsDoneDTO.builder()
                                .date(date)
                                .storyPoints(0)
                                .build())
                                .collect(Collectors.toList());

        getTasks(person, from, to).stream()
                .collect(Collectors.groupingBy(TaskEntity::getDoneDate, Collectors.toList()))
                .forEach((key, value) -> spDone.forEach(sp -> sp.setStoryPoints(sp.getDate().isEqual(key) ? value.stream()
                        .map(TaskEntity::getStoryPoints)
                        .reduce(0, Integer::sum) : sp.getStoryPoints()>0?sp.getStoryPoints():0 )));
        return spDone;
    }

    private Integer getYearsOfEmployment(PersonEntity person) {
        return Math.toIntExact(ChronoUnit.YEARS.between(person.getDateOfEmployment(), LocalDate.now()));
    }

    private Integer getTotalOfStoryPoints(PersonEntity person, LocalDate from, LocalDate to) {
        return getStoryPointsDoneByDate(person, from, to).stream()
                .map(StoryPointsDoneDTO::getStoryPoints)
                .reduce(0, Integer::sum);
    }

    private Double getMedian(List<? extends Number> data) {
        if (data.isEmpty()) {
            return 0.0;
        }

        DoubleStream sortedAges = data.stream().mapToDouble(Number::doubleValue).sorted();
        return data.size() % 2 == 0 ?
                sortedAges.skip(data.size() / 2 - 1).limit(2).average().getAsDouble() :
                sortedAges.skip(data.size() / 2).findFirst().getAsDouble();
    }

    private List<TasksAndHoursDTO> getTimeSpentOnTasks(PersonEntity person, LocalDate from, LocalDate to) {
        return getTasks(person, from, to).stream()
                .map(task -> TasksAndHoursDTO.builder()
                        .taskName(task.getName())
                        .timeOfCompletion(getTimeOfCompletion(task, person))
                        .build())
                .filter(task -> task.getTimeOfCompletion() >0)
                .collect(Collectors.toList());


    }

    private Long getTimeOfCompletion(TaskEntity task, PersonEntity person) {
        return person.getHistory().stream()
                .filter(history -> history.getTask() != null)
                .filter(history -> history.getTask().equals(task))
                .map(history -> ChronoUnit.MINUTES.between(history.getStarted(), history.getEnded()))
                .reduce(0L, Long::sum);
    }

    private List<TaskEntity> getTasks(PersonEntity person, LocalDate from, LocalDate to) {
        return person.getHistory().stream()
                .filter(history -> history.getActivityDate().isAfter(from.minusDays(1)) && history.getActivityDate().isBefore(to.plusDays(1)))
                .map(HistoryEntity::getTask).distinct()
                .filter(Objects::nonNull)
                .filter(task -> task.getDoneDate()!=null)
                .collect(Collectors.toList());
    }

    private Long timeTracked(ProjectEntity project, LocalDate from, LocalDate to) {
        return historyRepository.getTimeSpentBetweenDates(project.getId(), from, to);
    }

    private Integer getNumberOfEmployees(ProjectEntity project) {
        return project.getPersonsInProject().size();

    }

    private Integer getTasksDone(ProjectEntity project, LocalDate from, LocalDate to) {
        return project.getTasks().stream()
                .filter(task -> task.getDoneDate() != null)
                .filter(task -> task.getDoneDate().isAfter(from.minusDays(1)) && task.getDoneDate().isBefore(to.plusDays(1)))
                .map(TaskEntity::getStoryPoints)
                .reduce(0, Integer::sum);
    }

    private Double velocity(ProjectEntity project) {
        var projectVelocity = project.getStatistics().stream()
                .filter(statistics -> getListOfSprintNumbers(project).contains(statistics.getSprintNumber()))
                .collect(Collectors.groupingBy(ProjectStatisticsEntity::getSprintNumber, Collectors.toList()))
                .values().stream()
                .map(stats -> stats.stream().max(Comparator.comparing(ProjectStatisticsEntity::getStoryPointsDone)).orElse(ProjectStatisticsEntity.builder().storyPointsDone(0).build()))
                .mapToDouble(ProjectStatisticsEntity::getStoryPointsDone)
                .average()
                .orElse(0.0);
        if (projectVelocity == 0.0) projectVelocity = project.getTasks().stream().filter(task -> !task.isDeleted()).filter(task -> task.getDoneDate() != null).mapToDouble(TaskEntity::getStoryPoints).sum();
        return projectVelocity;
    }

    private List<Integer> getListOfSprintNumbers(ProjectEntity project) {
        var numberOfSprints = 4;
        return IntStream.range(project.getSprintNumber() - numberOfSprints, project.getSprintNumber())
                .boxed()
                .filter(number -> number > 0)
                .collect(Collectors.toList());
    }

    private BigDecimal moneySpentOnProject(ProjectEntity project, LocalDate from, LocalDate to) {
        return project.getPersonsInProject().stream()
                .map(person -> calculatePayForUser(person, from, to, historyEntity -> historyEntity.getTask() != null && historyEntity.getTask().getProject().equals(project)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<BurnDownDTO> getBurnDownChartData(ProjectEntity project) {
        var committedTasks = project.getTasks().stream()
                .filter(task -> !task.isDeleted())
                .mapToDouble(TaskEntity::getStoryPoints)
                .reduce(0, Double::sum);
        var doneTasks = committedTasks;

        List<BurnDownDTO> burnDownDTOS = DateUtils.getRangeOfDays(project.getEndOfSprint().toLocalDate().minusWeeks(project.getSprintDuration().getDuration()),
                        project.getEndOfSprint().toLocalDate(),
                        true,
                        true,
                        true).stream()
                .map(date -> BurnDownDTO.builder().date(date).build())
                .collect(Collectors.toList());

        var weekends = DateUtils.getRangeOfDays(project.getEndOfSprint().toLocalDate().minusWeeks(project.getSprintDuration().getDuration()),
                project.getEndOfSprint().toLocalDate(),
                false,
                true,
                false);
        var holiday = DateUtils.getRangeOfDays(project.getEndOfSprint().toLocalDate().minusWeeks(project.getSprintDuration().getDuration()),
                project.getEndOfSprint().toLocalDate(),
                false,
                false,
                true);
        var subtractDays = project.getEndOfSprint().getDayOfWeek() == DayOfWeek.SATURDAY || project.getEndOfSprint().getDayOfWeek() == DayOfWeek.SUNDAY ? 0 : -1;
        Double subtrahend = committedTasks / (DateUtils.getNormalWorkingDaysCount(project.getEndOfSprint().toLocalDate().minusWeeks(project.getSprintDuration().getDuration()), project.getEndOfSprint().toLocalDate()) + subtractDays);

        for (BurnDownDTO burndown : burnDownDTOS) {

            if (holiday.contains(burndown.getDate()) || weekends.contains(burndown.getDate()) || burndown.getDate().equals(project.getEndOfSprint().toLocalDate().minusWeeks(project.getSprintDuration().getDuration()))) {
                burndown.setUncommitted(committedTasks);
            } else {
                committedTasks -= subtrahend;
                burndown.setUncommitted(committedTasks);
            }

            var doneAtDay = project.getTasks().stream()
                    .filter(Objects::nonNull)
                    .filter(task -> task.getDoneDate() != null)
                    .filter(task -> !task.isDeleted() && task.getDoneDate().equals(burndown.getDate()))
                    .mapToDouble(TaskEntity::getStoryPoints)
                    .sum();
            doneTasks -= doneAtDay;
            burndown.setCommitted(doneTasks);
        }
        return burnDownDTOS;
    }

    private List<TasksDoneDTO> getTaskDoneByType(ProjectEntity project, LocalDate from, LocalDate to) {
        List<TasksDoneDTO> tasksDone = Lists.newArrayList();
        project.getTasks().stream()
                .filter(task -> task.getDoneDate() != null)
                .filter(task -> task.getDoneDate().isAfter(from.minusDays(1)) && task.getDoneDate().isBefore(to.plusDays(1)))
                .collect(Collectors.groupingBy(TaskEntity::getTaskType, Collectors.counting()))
                .forEach((key, value) -> tasksDone.add(TasksDoneDTO.builder()
                        .type(key.getName())
                        .numberOfTasks(value.intValue())
                        .build()));
        return tasksDone;
    }

    private List<SprintCompletionDTO> getSprintCompletion(ProjectEntity project) {
        List<SprintCompletionDTO> sprintCompletion = Lists.newArrayList();
        project.getStatistics().stream()
                .collect(Collectors.groupingBy(ProjectStatisticsEntity::getSprintNumber, Collectors.maxBy(Comparator.comparing(ProjectStatisticsEntity::getDay))))
                .forEach((key, value) -> sprintCompletion.add(SprintCompletionDTO.builder()
                        .sprintNumber(key)
                        .Committed(Double.valueOf(value.get().getStoryPointsDone()))
                        .uncommitted(Double.valueOf(value.get().getStoryPointsCommitted()))
                        .build()));
        return sprintCompletion;
    }
}
