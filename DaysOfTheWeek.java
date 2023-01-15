package com.chtetsoff.taskdist.components.calendar;

import java.time.DayOfWeek;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DaysOfTheWeek {
  MON(DayOfWeek.MONDAY, "Пн", "Mo", 0),
  TUE(DayOfWeek.TUESDAY, "Вт", "Tu", 1),
  WED(DayOfWeek.WEDNESDAY, "Ср", "We", 2),
  THU(DayOfWeek.THURSDAY, "Чт", "Th", 3),
  FRI(DayOfWeek.FRIDAY, "Пт", "Fr", 4),
  SAT(DayOfWeek.SATURDAY, "Сб", "Sa", 5),
  SUN(DayOfWeek.SUNDAY, "Вс", "Su", 6);


  private DayOfWeek daysOfTheWeek;
  private String ruDisplayName;
  private String enDisplayName;
  private int index;


  public static DaysOfTheWeek getBy(DayOfWeek daysOfTheWeek) {
    return Arrays.stream(DaysOfTheWeek.values())
        .filter(day -> day.getDaysOfTheWeek() == daysOfTheWeek)
        .findFirst().get();
  }

  public String getDisplayNameByLocale(String locale) {
    if ("en".equalsIgnoreCase(locale)) {
      return enDisplayName;
    } else {
      return ruDisplayName;
    }
  }

  public static DaysOfTheWeek getBy(int index) {
    return Arrays.stream(DaysOfTheWeek.values())
        .filter(day -> day.index == index)
        .findFirst().get();
  }
}
