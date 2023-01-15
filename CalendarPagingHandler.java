package com.chtetsoff.taskdist.components.calendar;

import com.chtetsoff.taskdist.components.calendar.CalendarKeyBoardHolder;
import com.chtetsoff.taskdist.service.handler.BotCommandsHolder;
import com.chtetsoff.taskdist.service.handler.BotHandler;
import java.time.LocalDate;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class CalendarPagingHandler implements BotHandler {

    private final CalendarKeyBoardHolder calendarKeyBoardHolder;

    @Override
    public void handle(Update update, Consumer<BotApiMethod> sender) {
        String command = update.getCallbackQuery().getData();
        String[] month = command.split(" ");
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        InlineKeyboardMarkup calendarMarkup = calendarKeyBoardHolder.getCalendarMarkup(LocalDate.parse(month[1]), "en", month[2]);

        var edit = EditMessageReplyMarkup.builder()
            .chatId(chatId)
            .messageId(update.getCallbackQuery().getMessage().getMessageId())
            .replyMarkup(calendarMarkup)
            .build();

        sender.accept(edit);
    }

    @Override
    public BotCommandsHolder getCommand() {
        return BotCommandsHolder.DATE_PAGING;
    }
}
