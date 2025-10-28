package org.project.ninjas.minyala.currency.bot.bot;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.ninjas.minyala.currency.bot.bot.service.InvokersService;
import org.project.ninjas.minyala.currency.bot.bot.service.UserStateService;
import org.project.ninjas.minyala.currency.bot.bot.state.BotState;
import org.project.ninjas.minyala.currency.bot.bot.state.BotStateInvoker;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Tests for {@link BotController}.
 */
public class BotControllerTest {
    private UserStateService userStateServiceMock;
    private InvokersService invokersServiceMock;
    private BotController controller;
    private Update updateMock;
    private Message messageMock;
    private CallbackQuery callbackMock;
    private User userMock;
    private BotState currentStateMock;
    private BotStateInvoker botStateInvokerMock;
    private BotResponse botResponseMock;
    private BotState nextStateMock;

    /**
     * Setup before each test.
     */
    @BeforeEach
    public void setup() {
        userStateServiceMock = mock(UserStateService.class);
        invokersServiceMock = mock(InvokersService.class);
        controller = new BotController(userStateServiceMock, invokersServiceMock);
        updateMock = mock(Update.class);
        messageMock = mock(Message.class);
        callbackMock = mock(CallbackQuery.class);
        userMock = mock(User.class);
        currentStateMock = mock(BotState.class);
        botStateInvokerMock = mock(BotStateInvoker.class);
        botResponseMock = mock(BotResponse.class);
        nextStateMock = mock(BotState.class);
    }

    @Test
    void givenUpdateWithMessage_whenHandleUpdate_thenReturnsMessageAndSavesState() {
        // GIVEN
        given(updateMock.hasMessage()).willReturn(true);
        given(updateMock.getMessage()).willReturn(messageMock);
        given(messageMock.getFrom()).willReturn(userMock);
        given(userMock.getId()).willReturn(123L);
        given(userStateServiceMock.getUserState(updateMock)).willReturn(currentStateMock);
        given(invokersServiceMock.process(currentStateMock)).willReturn(botStateInvokerMock);
        given(botStateInvokerMock.invoke(updateMock)).willReturn(botResponseMock);

        SendMessage expectedMsg = new SendMessage();
        expectedMsg.setText("reply-for-message");

        given(botResponseMock.message()).willReturn(expectedMsg);
        given(botResponseMock.nextState()).willReturn(nextStateMock);

        // WHEN
        SendMessage actual = controller.handleUpdate(updateMock);

        // THEN
        assertSame(expectedMsg, actual);
        verify(userStateServiceMock).setUserState(123L, nextStateMock);
        verify(userStateServiceMock).getUserState(updateMock);
        verify(invokersServiceMock).process(currentStateMock);
        verify(botStateInvokerMock).invoke(updateMock);
    }

    @Test
    void givenUpdateWithCallbackQuery_whenHandleUpdate_thenReturnsMessageAndSavesState() {
        // GIVEN
        given(updateMock.hasMessage()).willReturn(false);
        given(updateMock.getCallbackQuery()).willReturn(callbackMock);
        given(callbackMock.getFrom()).willReturn(userMock);
        given(userMock.getId()).willReturn(456L);
        given(userStateServiceMock.getUserState(updateMock)).willReturn(currentStateMock);
        given(invokersServiceMock.process(currentStateMock)).willReturn(botStateInvokerMock);

        SendMessage expectedMsg = new SendMessage();
        expectedMsg.setText("reply-for-callback");

        given(botStateInvokerMock.invoke(updateMock)).willReturn(botResponseMock);
        given(botResponseMock.message()).willReturn(expectedMsg);
        given(botResponseMock.nextState()).willReturn(nextStateMock);

        // WHEN
        SendMessage actual = controller.handleUpdate(updateMock);

        // THEN
        assertSame(expectedMsg, actual);
        verify(userStateServiceMock).setUserState(456L, nextStateMock);
        verify(userStateServiceMock).getUserState(updateMock);
        verify(invokersServiceMock).process(currentStateMock);
        verify(botStateInvokerMock).invoke(updateMock);
    }
}
