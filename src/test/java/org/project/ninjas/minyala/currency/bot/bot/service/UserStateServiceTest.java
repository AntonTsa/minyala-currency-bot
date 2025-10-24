package org.project.ninjas.minyala.currency.bot.bot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Tests for {@link UserStateService}.
 */
public class UserStateServiceTest {
    private UserStateService service;

    @BeforeEach
    void setUp() {
        service = new UserStateService();
    }

    @Test
    void givenStoredState_whenGetUserState_thenReturnsStoredState() {
        // Given: an update from user with id 123 and a stored state
        Update update = Mockito.mock(Update.class);
        Message msg = Mockito.mock(Message.class);
        User user = Mockito.mock(User.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(msg);
        when(msg.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(123L);

        service.setUserState(123L, BotState.HANDLE_MAIN_MENU);

        // When
        BotState result = service.getUserState(update);

        // Then
        assertEquals(BotState.HANDLE_MAIN_MENU, result);
    }

    @Test
    void givenStartMessage_whenGetUserState_thenReturnsHandleStart() {
        // Given: an update with message text "/start" and no stored state
        Update update = Mockito.mock(Update.class);
        Message msg = Mockito.mock(Message.class);
        User user = Mockito.mock(User.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(msg);
        when(msg.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(321L);
        when(msg.hasText()).thenReturn(true);
        when(msg.getText()).thenReturn("/start");

        // When
        BotState result = service.getUserState(update);

        // Then
        assertEquals(BotState.HANDLE_START, result);
    }

    @Test
    void givenNonStartMessage_whenGetUserState_thenThrowsIllegalStateException() {
        // Given: an update with a text message that is not "/start" and no stored state
        Update update = Mockito.mock(Update.class);
        Message msg = Mockito.mock(Message.class);
        User user = Mockito.mock(User.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(msg);
        when(msg.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(111L);
        when(msg.hasText()).thenReturn(true);
        when(msg.getText()).thenReturn("hello");

        // When / Then
        assertThrows(IllegalStateException.class, () -> service.getUserState(update));
    }

    @Test
    void givenCallbackQueryWithoutStoredState_whenGetUserState_thenThrowsIllegalStateException() {
        // Given: an update without message (callback query) and no stored state
        Update update = Mockito.mock(Update.class);
        CallbackQuery cq = Mockito.mock(CallbackQuery.class);
        User user = Mockito.mock(User.class);

        when(update.hasMessage()).thenReturn(false);
        when(update.getCallbackQuery()).thenReturn(cq);
        when(cq.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(222L);

        // When / Then
        assertThrows(IllegalStateException.class, () -> service.getUserState(update));
    }
}
