package com.chtetsoff.taskdist.components.calendar;


import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class CalendarKeyBoardHolder {

  private static final String DUMMY_PAYLOAD = "N/A";
  private static final String CALLBACK_PREFIX = "calendar_callback ";
  private static final String PAGING_PREFIX = "calendar_callback_paging ";
  private static final int MAX_CALENDAR_ROWS = 6;
  private static final int WEEK_DAYS = 7;


  /**
   * @param localDate init date for calendar markup. Calendar will be built by month of the date
   * @param locale locale for calendar. Currently, support ru, en
   * @param payload callback payload which will be attached to inline button payload
   *                example: if user push on MAY 27th 2021 date, callback will look like
   *                calendar_callback 2021-05-27 payload
   *                        if user push navigation arrows, callback will look like
   *                calendar_callback_paging 2021-04-27 payload
   *                        since everything on calendar markup is a button if user push
   *                any button wich is not actual date, callback will look like
   *                calendar_callback N/A
   * @return
   */
  public InlineKeyboardMarkup getCalendarMarkup(LocalDate localDate, String locale, String payload) {
    List<List<InlineKeyboardButton>> result = new ArrayList<>();
    result.add(getMonthNameAndYear(localDate, locale));
    result.add(daysOfWeekNames(locale));
    result.addAll(getDates(localDate, payload));
    result.add(getCalendarPaginator(localDate, payload));

    return InlineKeyboardMarkup.builder()
        .keyboard(result)
        .build();

  }

  private List<List<InlineKeyboardButton>> getDates(LocalDate localDate, String id) {
    LocalDate[][] primitiveCalendar = getPrimitiveCalendar(localDate);
    List<List<InlineKeyboardButton>> result = new ArrayList<>();

    for (LocalDate[] localDates : primitiveCalendar) {
      boolean b = Arrays.stream(localDates).allMatch(Objects::isNull);
      if (b) {
        break;
      }

      List<InlineKeyboardButton> daysRow = Arrays.stream(localDates)
          .map(value -> {
            String text = " ";
            String callbackData = DUMMY_PAYLOAD;

            if (value != null) {
              text = String.valueOf(value.getDayOfMonth());
              callbackData = value.format(DateTimeFormatter.ISO_DATE) + " " + id;
            }

            callbackData = CALLBACK_PREFIX + callbackData;
            return InlineKeyboardButton.builder().text(text).callbackData(callbackData).build();
          }).collect(Collectors.toList());
      result.add(daysRow);
    }
    return result;
  }

  private LocalDate[][] getPrimitiveCalendar(LocalDate date) {
    int lengthOfTheMonth = YearMonth.from(date).lengthOfMonth();

    LocalDate firstMonthDay = date.with(TemporalAdjusters.firstDayOfMonth());
    List<LocalDate> month = Stream.iterate(firstMonthDay, ld -> ld.plus(1, ChronoUnit.DAYS))
        .limit(lengthOfTheMonth)
        .collect(Collectors.toList());
    int counter = 0;

    LocalDate[][] result = new LocalDate[MAX_CALENDAR_ROWS][WEEK_DAYS];

    for (int i = 0; i < result.length; i++) {
      for (int j = 0; j < result[i].length; j++) {

        if (counter > month.size() - 1) {
          break;
        }

        LocalDate localDate = month.get(counter);
        if (DaysOfTheWeek.getBy(j).getDaysOfTheWeek() == localDate.getDayOfWeek()) {
          result[i][j] = localDate;
          counter++;
        }
      }
    }
    return result;
  }

  private List<InlineKeyboardButton> daysOfWeekNames(String locale) {
    return Arrays.stream(DaysOfTheWeek.values())
        .map(d -> InlineKeyboardButton.builder().callbackData(CALLBACK_PREFIX + DUMMY_PAYLOAD).text(d.getDisplayNameByLocale(locale))
            .build())
        .collect(Collectors.toList());
  }

  private List<InlineKeyboardButton> getMonthNameAndYear(LocalDate date, String locale) {
    int year = date.getYear();
    String monthName = date.getMonth()
        .getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag(locale));
    monthName = monthName.toUpperCase();

    InlineKeyboardButton chosenMonth = InlineKeyboardButton.builder().callbackData(CALLBACK_PREFIX + DUMMY_PAYLOAD).text(monthName + " " + year).build();

    return List.of(chosenMonth);
  }

  private List<InlineKeyboardButton> getCalendarPaginator(LocalDate date, String id) {
    LocalDate prevMonthDate = date.minus(1, ChronoUnit.MONTHS);
    LocalDate nextMonthDate = date.plus(1, ChronoUnit.MONTHS);

    InlineKeyboardButton prevMonth = InlineKeyboardButton.builder()
        .callbackData(PAGING_PREFIX + prevMonthDate.format(DateTimeFormatter.ISO_DATE) + " " + id)
        .text("<")
        .build();

    InlineKeyboardButton nextMonth = InlineKeyboardButton.builder()
        .callbackData(PAGING_PREFIX + nextMonthDate.format(DateTimeFormatter.ISO_DATE) + " " + id)
        .text(">")
        .build();

    InlineKeyboardButton emptyButton = InlineKeyboardButton.builder().callbackData(CALLBACK_PREFIX + DUMMY_PAYLOAD).text(" ").build();

    return List.of(prevMonth, emptyButton, nextMonth);
  }
}
