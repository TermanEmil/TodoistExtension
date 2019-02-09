package com.university.unicornslayer.todoistextension.utils.reminder;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.reminder.agent.ReminderAgent;
import com.university.unicornslayer.todoistextension.utils.reminder.model.NextReminderModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppReminderManager.class })
public class AppReminderManagerTest {
    private AppReminderManager target;
    private Date now;

    @Before
    public void setUp() throws Exception {
        target = new AppReminderManager(null);

        now = new Date(0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        mockStatic(Calendar.class);
        Mockito.when(Calendar.getInstance()).thenReturn(calendar);

        whenNew(Date.class).withNoArguments().thenReturn(now);
    }


    @Test
    public void getNextItemToRemind_NoAgents_ReturnNull() {
        List<TodoistItem> items = new ArrayList<>();

        NextReminderModel result = target.getNextItemToRemind(items);
        assert result == null;
    }

    @Test
    public void getNextItemToRemind_TwoAgents_ReturnClosest() {
        List<TodoistItem> items = new ArrayList<>();

        ReminderAgent agent1 = mock(ReminderAgent.class);
        ReminderAgent agent2 = mock(ReminderAgent.class);

        NextReminderModel agent1Result = new NextReminderModel(null, 0);
        NextReminderModel agent2Result = new NextReminderModel(null, 1);

        when(agent1.getNextItemToRemind(items)).thenReturn(agent1Result);
        when(agent2.getNextItemToRemind(items)).thenReturn(agent2Result);

        target.addReminderAgent(agent1);
        target.addReminderAgent(agent2);

        NextReminderModel result = target.getNextItemToRemind(items);

        assert result != null;
        assert result == agent1Result;
    }
}