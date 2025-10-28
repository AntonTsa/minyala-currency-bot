package org.project.ninjas.minyala.currency.bot.bot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Test class for {@link UserStateService}.
 */
public class UserStateServiceTest {
    private UserStateService service;
    private Update updateMock;
    private Message messageMock;
    private CallbackQuery callbackMock;
    private User userMock;
    
    /** Setup before each test. */
    @BeforeEach
    public void setup() {
        service = new UserStateService();
        updateMock = mock(Update.class);
        messageMock = mock(Message.class);
        callbackMock = mock(CallbackQuery.class);
        userMock = mock(User.class);
    }
    
    @Test
    void givenStoredState_givenGetUserState_willReturnStoredState() {
        // GIVEN
        Long userId = 1L;
        BotState stored = BotState.HANDLE_START;
        service.setUserState(userId, stored);

        given(updateMock.hasMessage()).willReturn(true);
        given(updateMock.getMessage()).willReturn(messageMock);
        given(messageMock.getFrom()).willReturn(userMock);
        given(userMock.getId()).willReturn(userId);
        // WHEN
        BotState result = service.getUserState(updateMock);

        // THEN
        assertSame(stored, result);
    }

    @Test
    void givenMessageStart_givenGetUserState_willReturnHandleStart() {
        // GIVEN
        given(updateMock.hasMessage()).willReturn(true);
        given(updateMock.getMessage()).willReturn(messageMock);
        given(messageMock.getFrom()).willReturn(userMock);
        given(userMock.getId()).willReturn(2L);
        given(messageMock.hasText()).willReturn(true);
        given(messageMock.getText()).willReturn("/start");
        // WHEN
        BotState result = service.getUserState(updateMock);
        
        // THEN
        assertEquals(BotState.HANDLE_START, result);
    }

    @Test
    void givenMessageWithoutStateAndNotStart_givenGetUserState_thenThrow() {
        // GIVEN
        given(updateMock.hasMessage()).willReturn(true);
        given(updateMock.getMessage()).willReturn(messageMock);
        given(messageMock.getFrom()).willReturn(userMock);
        given(userMock.getId()).willReturn(3L);
        given(messageMock.hasText()).willReturn(true);
        given(messageMock.getText()).willReturn("hello");
        // WHEN
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.getUserState(updateMock));
        
        // THEN
        assertTrue(ex.getMessage().contains("No state found for user: 3"));
    }

    @Test
    void givenCallbackWithoutState_givenGetUserState_thenThrow() {
        // GIVEN
        given(updateMock.hasMessage()).willReturn(false);
        given(updateMock.getCallbackQuery()).willReturn(callbackMock);
        given(callbackMock.getFrom()).willReturn(userMock);
        given(userMock.getId()).willReturn(4L);
        // WHEN
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.getUserState(updateMock));
        // THEN
        assertTrue(ex.getMessage().contains("No state found for user: 4"));
    }

    @Test
    void givenSetUserState_thenGetUserStateForCallbackReturnsIt() {
        // GIVEN
        Long userId = 5L;
        BotState stored = BotState.HANDLE_START;
        service.setUserState(userId, stored);

        given(updateMock.hasMessage()).willReturn(false);
        given(updateMock.getCallbackQuery()).willReturn(callbackMock);
        given(callbackMock.getFrom()).willReturn(userMock);
        given(userMock.getId()).willReturn(userId);
        // WHEN
        BotState result = service.getUserState(updateMock);
        // THEN
        assertSame(stored, result);
    }
}
