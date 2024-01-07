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
import com.mw.timesheets.domain.statistcs.model.MapsForStatistics;
import com.mw.timesheets.domain.statistcs.model.employee.PersonStatisticsDTO;
import com.mw.timesheets.domain.statistcs.model.employee.StoryPointsDoneDTO;
import com.mw.timesheets.domain.statistcs.model.employee.TasksAndHoursDTO;
import com.mw.timesheets.domain.statistcs.model.project.BurnDownDTO;
import com.mw.timesheets.domain.statistcs.model.project.ProjectStatisticsDTO;
import com.mw.timesheets.domain.statistcs.model.project.SprintCompletionDTO;
import com.mw.timesheets.domain.statistcs.model.project.TasksDoneDTO;
import com.mw.timesheets.domain.task.TaskEntity;
import com.mw.timesheets.domain.timetrack.*;
import com.mw.timesheets.domain.timetrack.model.HistoryWithTotalTimeDTO;
import com.mw.timesheets.domain.timetrack.model.TimeTrackerHistoryDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
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
    private final TimeTrackRepository timeTrackRepository;
    private final TimeTrackerMapper timeTrackerMapper;

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

    private BigDecimal calculatePayForUser(PersonEntity person, LocalDate from, LocalDate to, Predicate<HistoryEntity> projectPredicate) {

        var maps = getMapsForStatistics(person, from, to, projectPredicate);
        List<BigDecimal> values = Lists.newArrayList();
        maps.getUserData().forEach((key, value) -> {
            if(maps.getUserMinutes().get(key)-value.getLeft() * statisticsProperties.getTimeInterval().longValue() > 0){
                values.add(BigDecimal.valueOf(value.getLeft() * value.getRight()));
                values.add(BigDecimal.valueOf((maps.getUserMinutes().get(key)/statisticsProperties.getTimeInterval().doubleValue()-value.getLeft())*value.getRight()*statisticsProperties.getOvertimePayRatio()));
            }else{
                values.add(BigDecimal.valueOf(maps.getUserMinutes().get(key)/statisticsProperties.getTimeInterval().doubleValue() * value.getRight()));
            }
        });
        return values.stream().reduce(BigDecimal::add).orElse(new BigDecimal(0));
    }

    private MapsForStatistics getMapsForStatistics(PersonEntity person, LocalDate from, LocalDate to, Predicate<HistoryEntity> projectPredicate){

        var history =  person.getHistory().stream()
                .filter(projectPredicate)
                .filter(historyEntity -> historyEntity.getActivityDate().isAfter(from.minusDays(1)) && historyEntity.getActivityDate().isBefore(to.plusDays(1)))
                .collect(Collectors.toList());

        var timer = timeTrackRepository.findByPersonUserEmail(person.getUser().getEmail());
        if(timer.isPresent()) {
            var timerToHistory = timeTrackerMapper.timeTrackerToHistoryEntity(timer.get());
            if (timerToHistory != null) {
                timerToHistory.setEnded(DateUtils.getSystemTime().toLocalTime());
                history.add(timerToHistory);
            }
        }

        var userData = new HashMap<LocalDate, Pair<Integer, Double>>();
        history.stream()
                .collect(Collectors.groupingBy(HistoryEntity::getActivityDate, Collectors.toList()))
                .forEach((key, value) -> userData.put(key, new ImmutablePair<>(value.stream().map(HistoryEntity::getWorkToDoInHours).max(Integer::compare).get(), value.stream().map(HistoryEntity::getHourlyPay).max(Double::compare).get())));

        var userMinutes = new HashMap<LocalDate, Long>();
        history.stream()
                .collect(Collectors.groupingBy(HistoryEntity::getActivityDate, Collectors.toList()))
                .forEach((key, value) -> userMinutes.put(key, value.stream().map(historyEntity -> ChronoUnit.MINUTES.between(historyEntity.getStarted(), historyEntity.getEnded())).reduce(Long::sum).orElse(0L)));
        return MapsForStatistics.builder()
                .userData(userData)
                .userMinutes(userMinutes)
                .build();
    }

    private Double overTimeRatioForPerson(PersonEntity person, LocalDate from, LocalDate to) {
        var maps = getMapsForStatistics(person, from, to, historyEntity -> true);
        List<Pair<Integer, Long>> overtimeHours = Lists.newArrayList();
        maps.getUserData().forEach((key, value) -> {
            if(maps.getUserMinutes().get(key)-value.getLeft() * statisticsProperties.getTimeInterval().longValue() > 0){
                overtimeHours.add(new ImmutablePair<>(value.getLeft(),maps.getUserMinutes().get(key)-value.getLeft()*statisticsProperties.getTimeInterval().longValue()));
            }
        });
        var regularHours = overtimeHours.stream().mapToInt(Pair::getLeft).sum();
        var overTimeMinutes = overtimeHours.stream().mapToLong(Pair::getRight).sum();
        return overTimeMinutes/statisticsProperties.getTimeInterval().doubleValue()/regularHours * 100;
    }

    private Double getCompletionRate(PersonEntity person, LocalDate from, LocalDate to) {
        if (person.getStatistics() == null) return calculateCompletionForCurrentSprint(person, from, to);

        var list = person.getStatistics().stream()
                .filter(statistics -> DateUtils.getRangeOfDays(from, to, true, true, true).contains(statistics.getDateOfSnapshot()))
                .toList();

        if (list.isEmpty()) return calculateCompletionForCurrentSprint(person, from, to);

        var modifiedList = list.stream().map(PersonStatisticsEntity::getCompletionRate).collect(Collectors.toList());
        return getMedian(modifiedList) * 100;
    }

    private Double calculateCompletionForCurrentSprint(PersonEntity person, LocalDate from, LocalDate to){
        var collection = person.getProjects().stream()
                .map(project -> project.getTasks().stream().filter(task -> Objects.equals(task.getPerson().getId(), person.getId())).collect(Collectors.toList()))
                .map(taskEntities -> new ImmutablePair<>(taskEntities.stream().map(TaskEntity::getStoryPoints).mapToInt(task-> task).sum(), taskEntities.stream().filter(task -> task.getDoneDate() != null).map(TaskEntity::getStoryPoints).mapToInt(task-> task).sum()))
                .map(pair -> (pair.right*1.0)/ pair.left)
                .collect(Collectors.toList());

        return getMedian(collection) * 100;
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
