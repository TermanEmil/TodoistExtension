package com.university.unicornslayer.todoistextension.ui.token_input;

import com.university.unicornslayer.todoistextension.data.network.ApiHelper;
import com.university.unicornslayer.todoistextension.data.prefs.TokenPrefHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.net.HttpURLConnection;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TokenInputPresenterTest {
    private static final String testingToken = "I love unicorns";

    @Mock
    private TokenInputMvpView view;

    @Mock
    private TokenPrefHelper tokenPrefHelper;

    @Mock
    private ApiHelper apiHelper;

    // The target
    private TokenInputPresenter presenter;

    @Before
    public void setUp() throws Exception {
        presenter = new TokenInputPresenter(tokenPrefHelper, apiHelper);
        presenter.setView(view);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void onCreateWithToken() {
        when(tokenPrefHelper.getToken()).thenReturn(testingToken);
        presenter.onCreate();

        verify(view).setTokenInputText(testingToken);
    }

    @Test
    public void onCreateWithoutToken() {
        when(tokenPrefHelper.getToken()).thenReturn(null);
        presenter.onCreate();

        verifyZeroInteractions(view);
    }

    @Test
    public void onSubmitTokenPressedWithoutToken() {
        when(view.getTokenInputText()).thenReturn("");
        presenter.onSubmitTokenPressed();

        verify(view).disableUserInput();
        verify(view).enableUserInput();
        verify(view).showEmptyTokenInputError();
    }

    @Test
    public void onSubmitTokenPressedWithValidToken() {
        when(view.getTokenInputText()).thenReturn(testingToken);
        presenter.onSubmitTokenPressed();

        verify(view).showRequestIsBeingProcessed();
        verify(apiHelper).setToken(testingToken);
        verify(apiHelper).validateToken(any(ApiHelper.TokenValidationListener.class));
    }

    @Test
    public void onSubmitTokenPressed_TokenValidation_ItsValid() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((ApiHelper.TokenValidationListener) (invocation.getArgument(0))).onResponse(true);
                return null;
            }
        }).when(apiHelper).validateToken(any(ApiHelper.TokenValidationListener.class));

        when(view.getTokenInputText()).thenReturn(testingToken);
        presenter.onSubmitTokenPressed();

        verify(apiHelper).validateToken(any(ApiHelper.TokenValidationListener.class));
        verify(tokenPrefHelper).setToken(testingToken);
        verify(view).showTokenIsSaved();
        verify(view).enableUserInput();
        verify(view).hideRequestIsBeingProcessed();
        verify(view).finish();
    }

    @Test
    public void onSubmitTokenPressed_TokenValidation_ItsNotValid() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((ApiHelper.TokenValidationListener) (invocation.getArgument(0))).onResponse(false);
                return null;
            }
        }).when(apiHelper).validateToken(any(ApiHelper.TokenValidationListener.class));

        when(view.getTokenInputText()).thenReturn(testingToken);
        presenter.onSubmitTokenPressed();

        verify(apiHelper).validateToken(any(ApiHelper.TokenValidationListener.class));

        verify(tokenPrefHelper, never()).setToken(anyString());
        verify(view).showTokenIsInvalid();
        verify(view).enableUserInput();
        verify(view).hideRequestIsBeingProcessed();
        verify(view, never()).finish();
    }

    @Test
    public void onSubmitTokenPressed_TokenValidation_FailedCozOfNet() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ApiHelper.TokenValidationListener listener = invocation.getArgument(0);
                listener.onError(HttpURLConnection.HTTP_UNAVAILABLE);
                return null;
            }
        }).when(apiHelper).validateToken(any(ApiHelper.TokenValidationListener.class));

        when(view.getTokenInputText()).thenReturn(testingToken);
        presenter.onSubmitTokenPressed();

        verify(apiHelper).validateToken(any(ApiHelper.TokenValidationListener.class));

        verify(tokenPrefHelper, never()).setToken(anyString());
        verify(view).showServiceUnavailableError();
        verify(view).enableUserInput();
        verify(view).hideRequestIsBeingProcessed();
        verify(view, never()).finish();
    }

    public void onSubmitTokenPressed_TokenValidation_FailedCozOfUnknownError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ApiHelper.TokenValidationListener listener = invocation.getArgument(0);
                listener.onError(-1);
                return null;
            }
        }).when(apiHelper).validateToken(any(ApiHelper.TokenValidationListener.class));

        when(view.getTokenInputText()).thenReturn(testingToken);
        presenter.onSubmitTokenPressed();

        verify(apiHelper).validateToken(any(ApiHelper.TokenValidationListener.class));

        verify(tokenPrefHelper, never()).setToken(anyString());
        verify(view).showTokenValidationFailedError();
        verify(view).enableUserInput();
        verify(view).hideRequestIsBeingProcessed();
        verify(view, never()).finish();
    }

    @Test
    public void cancelTokenValidation() {
        presenter.cancelTokenValidation();
        verify(apiHelper).cancelTokenValiation();
        verify(view).enableUserInput();
        verify(view).hideRequestIsBeingProcessed();
    }
}