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
import com.mw.timesheets.domain.team.TeamEntity;
import com.mw.timesheets.domain.timetrack.HistoryRepository;
import com.mw.timesheets.domain.timetrack.TimeTrackService;
import com.mw.timesheets.domain.timetrack.model.HistoryWithTotalTimeDTO;
import com.mw.timesheets.domain.timetrack.model.TimeTrackerHistoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
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

    @Override
    public PersonStatisticsDTO generateStatisticsForPerson(IdFromToRequestDTO statisticsRequestDTO) {
        var personId = statisticsRequestDTO.getId();
        var from = statisticsRequestDTO.getFrom();
        var to = statisticsRequestDTO.getTo();

        var person = personRepository.findById(personId).orElseThrow(() -> new CustomErrorException("Person not found", HttpStatus.NOT_FOUND));
        return PersonStatisticsDTO.builder()
                .yearsOfEmployment(getYearsOfEmployment(person))
                .Pay(calculatePayForUser(person, from, to))
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
                .velocity(velocity(project, from, to))
                .tasksDone(getTasksDone(project, from, to))
                .build();
    }


    private BigDecimal calculatePayForUser(PersonEntity person, LocalDate from, LocalDate to) {

        var history = timeTrackService.getHistoryOfGivenUser(person.getId(), from, to);
        var holidaysDaysRange = DateUtils.getRangeOfDays(from, to, false, false, true);
        var countOfNormalWorkingDays = DateUtils.getNormalWorkingDaysCount(from, to);

        var work = getWorkingHours(history, timeTrackerHistoryDTO -> !holidaysDaysRange.contains(timeTrackerHistoryDTO.getDateOfActivity()));
        var holidayWork = getWorkingHours(history, timeTrackerHistoryDTO -> holidaysDaysRange.contains(timeTrackerHistoryDTO.getDateOfActivity()));

        var overtime = (double) calculateOvertime(work, person.getWorkDuringWeekInHours(), countOfNormalWorkingDays) / statisticsProperties.getTimeInterval();
        var normalWork = (work - overtime) / statisticsProperties.getTimeInterval();
        var holidayHours = (double) holidayWork / statisticsProperties.getTimeInterval();
        return BigDecimal.valueOf(normalWork * person.getHourlyPay() + overtime * person.getHourlyPay() * statisticsProperties.getOvertimePayRatio() + holidayHours * person.getHourlyPay() * statisticsProperties.getHolidayPayRatio());

    }

    private Long calculateOvertime(Long work, Integer workDuringWeekInHours, Integer numberOfDays) {
        var expectedWorkTime = calculateExpectedWorkingHours(workDuringWeekInHours, numberOfDays);
        return work - expectedWorkTime < 0 ? 0 : work - expectedWorkTime;
    }

    private Long calculateExpectedWorkingHours(Integer workDuringWeekInHours, Integer numberOfDays) {

        return workDuringWeekInHours.longValue() / statisticsProperties.getWorkingDays() * statisticsProperties.getTimeInterval() * numberOfDays;
    }


    private Double overTimeRatioForPerson(PersonEntity person, LocalDate from, LocalDate to) {
        var history = timeTrackService.getHistoryOfGivenUser(person.getId(), from, to);
        var holidaysDaysRange = DateUtils.getRangeOfDays(from, to, false, false, true);
        var countOfNormalWorkingDays = DateUtils.getNormalWorkingDaysCount(from, to);

        var work = getWorkingHours(history, timeTrackerHistoryDTO -> !holidaysDaysRange.contains(timeTrackerHistoryDTO.getDateOfActivity()));
        var holidayWork = getWorkingHours(history, timeTrackerHistoryDTO -> holidaysDaysRange.contains(timeTrackerHistoryDTO.getDateOfActivity()));

        var overtime = calculateOvertime(work + holidayWork, person.getWorkDuringWeekInHours(), countOfNormalWorkingDays) / statisticsProperties.getTimeInterval();
        var normalHours = calculateExpectedWorkingHours(person.getWorkDuringWeekInHours(), countOfNormalWorkingDays).doubleValue();
        return normalHours == 0 ? 0 : overtime / normalHours;
    }

    private Long getWorkingHours(HistoryWithTotalTimeDTO history, Predicate<TimeTrackerHistoryDTO> holidayPredicate) {
        return history.getHistoryDTOs().stream()
                .filter(holidayPredicate)
                .map(TimeTrackerHistoryDTO::getTime)
                .reduce(0L, Long::sum);
    }

    private Double getCompletionRate(PersonEntity person, LocalDate from, LocalDate to) {
        if (person.getStatistics() == null) return 0.0;

        var list = person.getStatistics().stream()
                .filter(statistics -> DateUtils.getRangeOfDays(from, to, true, false, false).contains(statistics.getDateOfSnapshot()))
                .toList();

        if (list.isEmpty()) {
            var completion = person.getStatistics().stream()
                    .collect(Collectors.groupingBy(PersonStatisticsEntity::getProject, Collectors.toList()))
                    .values().stream()
                    .map(value -> Iterables.getLast(value.stream().sorted(Comparator.comparing(PersonStatisticsEntity::getSprintNumber)).collect(Collectors.toList())))
                    .map(PersonStatisticsEntity::getCompletionRate)
                    .collect(Collectors.toList());

            if (completion.isEmpty()) return 0.0;
            return getMedian(completion);
        }

        var modifiedList = list.stream().map(PersonStatisticsEntity::getCompletionRate).collect(Collectors.toList());
        return getMedian(modifiedList);
    }

    private List<StoryPointsDoneDTO> getStoryPointsDoneByDate(PersonEntity person, LocalDate from, LocalDate to) {
        List<StoryPointsDoneDTO> spDone = Lists.newArrayList();
        getTasks(person, from, to).stream()
                .collect(Collectors.groupingBy(TaskEntity::getDoneDate, Collectors.toList()))
                .forEach((key, value) -> spDone.add(StoryPointsDoneDTO.builder()
                        .date(key)
                        .storyPoints(value.stream()
                                .map(TaskEntity::getStoryPoints)
                                .reduce(0, Integer::sum))
                        .build()));
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
        if (!data.isEmpty()) {
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
                .collect(Collectors.toList());


    }

    private Long getTimeOfCompletion(TaskEntity task, PersonEntity person) {
        return person.getHistory().stream()
                .filter(history -> history.getTaskName().equals(task.getName()))
                .map(history -> ChronoUnit.MINUTES.between(history.getStarted(), history.getEnded()))
                .reduce(0L, Long::sum);
    }

    private List<TaskEntity> getTasks(PersonEntity person, LocalDate from, LocalDate to) {
        return person.getTasks().stream()
                .filter(task -> task.getDoneDate() != null)
                .filter(task -> task.getDoneDate().isAfter(from) && task.getDoneDate().isBefore(to))
                .collect(Collectors.toList());
    }

    private Long timeTracked(ProjectEntity project, LocalDate from, LocalDate to) {
        return historyRepository.getTimeSpentBetweenDates(project.getKey(), from, to);
    }

    private Integer getNumberOfEmployees(ProjectEntity project) {
        return Math.toIntExact(project.getTeam().stream()
                .map(TeamEntity::getPersons)
                .mapToLong(Collection::size)
                .sum());
    }

    private Integer getTasksDone(ProjectEntity project, LocalDate from, LocalDate to) {
        return project.getTasks().stream()
                .filter(task -> task.getDoneDate().isAfter(from) && task.getDoneDate().isBefore(to))
                .map(TaskEntity::getStoryPoints)
                .reduce(0, Integer::sum);
    }

    private Double velocity(ProjectEntity project, LocalDate from, LocalDate to) {
        return project.getStatistics().stream()
                .filter(statistics -> getListOfSprintNumbers(project, from, to).contains(statistics.getSprintNumber()))
                .collect(Collectors.groupingBy(ProjectStatisticsEntity::getSprintNumber, Collectors.toList()))
                .values().stream()
                .map(stats -> stats.stream().max(Comparator.comparing(ProjectStatisticsEntity::getStoryPointsDone)).orElse(ProjectStatisticsEntity.builder().storyPointsDone(0).build()))
                .mapToDouble(ProjectStatisticsEntity::getStoryPointsDone)
                .average()
                .orElse(0);
    }

    private List<Integer> getListOfSprintNumbers(ProjectEntity project, LocalDate from, LocalDate to) {
        var weeks = ChronoUnit.WEEKS.between(from, to);
        var numberOfSprints = (int) Math.ceil((double) weeks / project.getSprintDuration().getDuration());
        return IntStream.range(project.getSprintNumber() - numberOfSprints, project.getSprintNumber())
                .boxed()
                .filter(number -> number > 0)
                .collect(Collectors.toList());
    }

    private BigDecimal moneySpentOnProject(ProjectEntity project, LocalDate from, LocalDate to) {
        return project.getTeam().stream()
                .map(TeamEntity::getPersons)
                .map(persons -> persons.stream()
                        .map(person -> getHistoryForProject(person, project, from, to))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getHistoryForProject(PersonEntity person, ProjectEntity project, LocalDate from, LocalDate to) {
        var filteredHistory = person.getHistory().stream()
                .filter(historyEntity -> project.getKey().equals(historyEntity.getProjectKey()) && historyEntity.getActivityDate().isAfter(from) && historyEntity.getActivityDate().isBefore(to))
                .toList();

        var normalTime = filteredHistory.stream()
                .filter(historyEntity -> !DateUtils.getRangeOfDays(from, to, false, false, true).contains(historyEntity.getActivityDate()))
                .map(historyEntity -> ChronoUnit.MINUTES.between(historyEntity.getStarted(), historyEntity.getEnded()))
                .reduce(0L, Long::sum);

        var holidayTime = filteredHistory.stream()
                .filter(historyEntity -> DateUtils.getRangeOfDays(from, to, false, false, true).contains(historyEntity.getActivityDate()))
                .map(historyEntity -> ChronoUnit.MINUTES.between(historyEntity.getStarted(), historyEntity.getEnded()))
                .reduce(0L, Long::sum);
        var overtime = (double) calculateOvertime(normalTime, person.getWorkDuringWeekInHours(), DateUtils.getNormalWorkingDaysCount(from, to)) / statisticsProperties.getTimeInterval();
        var normalWork = (normalTime - overtime) / statisticsProperties.getTimeInterval() / statisticsProperties.getTimeInterval();
        var holidayHours = holidayTime / statisticsProperties.getTimeInterval() / statisticsProperties.getTimeInterval();

        return BigDecimal.valueOf(normalWork * person.getHourlyPay() + overtime * person.getHourlyPay() * statisticsProperties.getOvertimePayRatio() + holidayHours * person.getHourlyPay() * statisticsProperties.getHolidayPayRatio());
    }

    private List<BurnDownDTO> getBurnDownChartData(ProjectEntity project) {
        List<BurnDownDTO> burnDownDTOS = DateUtils.getRangeOfDays(project.getEndOfSprint().toLocalDate().minusWeeks(project.getSprintDuration().getDuration()),
                        project.getEndOfSprint().toLocalDate(),
                        true,
                        true,
                        true).stream()
                .map(date -> BurnDownDTO.builder().date(date).build())
                .collect(Collectors.toList());
        var committedTasks = project.getTasks().stream()
                .mapToDouble(TaskEntity::getStoryPoints)
                .reduce(0, Double::sum);

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

        Double subtrahend = committedTasks / DateUtils.getNormalWorkingDaysCount(project.getEndOfSprint().toLocalDate().minusWeeks(project.getSprintDuration().getDuration()), project.getEndOfSprint().toLocalDate());
        Double previousCommitted = 0.0;
        for (BurnDownDTO burndown : burnDownDTOS) {
            if (holiday.contains(burndown.getDate()) || weekends.contains(burndown.getDate())) {
                burndown.setUncommitted(committedTasks);
            } else {
                burndown.setUncommitted(committedTasks);
                committedTasks -= subtrahend;
            }
            burndown.setCommitted(project.getStatistics().stream()
                    .filter(statistics -> statistics.getSprintNumber().equals(project.getSprintNumber()) && statistics.getDay().isEqual(burndown.getDate()))
                    .mapToDouble(ProjectStatisticsEntity::getStoryPointsDone)
                    .findFirst()
                    .orElse(previousCommitted));

            if (burndown.getDate().equals(LocalDate.now())) {
                burndown.setCommitted(project.getTasks().stream()
                        .filter(task -> !task.isDeleted() && task.getDoneDate().equals(LocalDate.now()))
                        .mapToDouble(TaskEntity::getStoryPoints)
                        .sum());
            }
            previousCommitted = burndown.getCommitted();
        }
        return burnDownDTOS;
    }

    private List<TasksDoneDTO> getTaskDoneByType(ProjectEntity project, LocalDate from, LocalDate to) {
        List<TasksDoneDTO> tasksDone = Lists.newArrayList();
        project.getTasks().stream()
                .filter(task -> task.getDoneDate() != null)
                .filter(task -> task.getDoneDate().isAfter(from) && task.getDoneDate().isBefore(to))
                .collect(Collectors.groupingBy(TaskEntity::getTaskType, Collectors.counting()))
                .forEach((key, value) -> tasksDone.add(TasksDoneDTO.builder()
                        .type(key.getName())
                        .numberOfTasks(value.intValue())
                        .build()));
        return tasksDone;
    }

    //TODO: get bardzo nieprzyjaźnie zrobiony
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
