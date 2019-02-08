package com.university.unicornslayer.todoistextension.utils.reminder.agent;

import android.app.Notification;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.TodoistNotifHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.model.BeforeDuePrefsProvider;
import com.university.unicornslayer.todoistextension.utils.reminder.model.Reminder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RemindBeforeDueAgent.class, RingtoneManager.class })
public class RemindBeforeDueAgentTest {
    private static final long intervalMin = 0;
    private static final long intervalMax = 10;
    private static final String defaultItemContent = "foo";

    private RemindBeforeDueAgent target;

    @Mock
    private BeforeDuePrefsProvider prefs;

    @Mock
    private TodoistNotifHelper notifHelper;

    @Mock
    List<TodoistItem> testItems;

    @Mock
    NotificationCompat.Builder notifBuilder;

    private Date now;

    @Before
    public void setUp() throws Exception {
        target = new RemindBeforeDueAgent(prefs, notifHelper);

        now = new Date(0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        mockStatic(Calendar.class);
        when(Calendar.getInstance()).thenReturn(calendar);

        whenNew(Date.class).withNoArguments().thenReturn(now);
    }

    @Test
    public void createReminders_ItsDisabled_DoNothing() {
        when(prefs.getRemindBeforeDueMax()).thenReturn(new Long(-1));
        when(testItems.size()).thenReturn(1);

        target.createReminders(null, testItems);

        // No notifications are made
        verifyZeroInteractions(notifHelper);
    }

    @Test
    public void createReminders_NoItems_DoNothing() {
        when(prefs.getRemindBeforeDueMax()).thenReturn(new Long(1));
        when(testItems.size()).thenReturn(0);

        target.createReminders(null, testItems);

        // No notifications are made
        verifyZeroInteractions(notifHelper);
    }

    @Test
    public void createReminders_ItemNotInRange_DoNothing() throws Exception {
        // Set limits
        when(prefs.getRemindBeforeDueMin()).thenReturn(intervalMin);
        when(prefs.getRemindBeforeDueMax()).thenReturn(intervalMax);

        // Mock the item
        TodoistItem item = mock(TodoistItem.class);
        when(item.getDueDate()).thenReturn(now.getTime() + intervalMax + 1);

        // Create an array with the mocked item
        List<TodoistItem> items = new ArrayList<>();
        items.add(item);

        target.createReminders(null, items);

        // No notifications are made
        verifyZeroInteractions(notifHelper);
    }

    @Test
    public void createReminders_ItemAlreadyMentioned_DoNothing() throws Exception {
        // Set limits
        when(prefs.getRemindBeforeDueMin()).thenReturn(intervalMin);
        when(prefs.getRemindBeforeDueMax()).thenReturn(intervalMax);

        // Mock the item. He should be in range.
        TodoistItem item = mock(TodoistItem.class);
        when(item.getDueDate()).thenReturn(now.getTime() + intervalMax - 1);

        // These fields are used for creating the Reminder object.
        when(item.getId()).thenReturn(0);
        when(item.getContent()).thenReturn("foo");

        // Create an array with the mocked item
        List<TodoistItem> items = new ArrayList<>();
        items.add(item);

        Map<Integer, Reminder> data = new HashMap<>();
        data.put(item.getId(), new Reminder(item));

        target.createReminders(data, items);

        // No notifications are made
        verifyZeroInteractions(notifHelper);
    }

    @Test
    public void createReminders_InRangeNotMentioned_Notify() throws Exception {
        setDefaultPrefs();
        TodoistItem item = buildTodoistItem(now.getTime() + intervalMax - 1);

        // Create an array with the mocked item
        List<TodoistItem> items = new ArrayList<>();
        items.add(item);

        // Init empty data
        Map<Integer, Reminder> data = new HashMap<>();

        // Mock to return the notif builder.
        when(notifHelper.getBaseBuilder(eq(item.getContent()), anyString())).thenReturn(notifBuilder);

        // Mock the return of .build()
        Notification targetNotif = new Notification();
        when(notifBuilder.build()).thenReturn(targetNotif);

        target.createReminders(data, items);

        // Build the notif
        verify(notifHelper).notify(item.getId(), targetNotif);
    }

    @Test
    public void createReminders_InRangeMentionedButModifiedContent_Notify() throws Exception {
        setDefaultPrefs();
        TodoistItem item = buildTodoistItem(now.getTime() + intervalMax - 1);

        // Create an array with the mocked item
        List<TodoistItem> items = new ArrayList<>();
        items.add(item);

        // Init empty data
        Map<Integer, Reminder> data = new HashMap<>();
        data.put(item.getId(), new Reminder(item));
        when(item.getContent()).thenReturn("bar");

        // Mock to return the notif builder.
        when(notifHelper.getBaseBuilder(eq(item.getContent()), anyString())).thenReturn(notifBuilder);

        // Mock the return of .build()
        Notification targetNotif = new Notification();
        when(notifBuilder.build()).thenReturn(targetNotif);

        target.createReminders(data, items);

        // Build the notif
        verify(notifHelper).notify(item.getId(), targetNotif);
    }

    @Test
    public void createReminders_InRangeMentionedButModifiedDue_Notify() throws Exception {
        setDefaultPrefs();
        TodoistItem item = buildTodoistItem(now.getTime() + intervalMax - 1);

        // Create an array with the mocked item
        List<TodoistItem> items = new ArrayList<>();
        items.add(item);

        // Init empty data
        Map<Integer, Reminder> data = new HashMap<>();
        data.put(item.getId(), new Reminder(item));
        when(item.getDueDate()).thenReturn(now.getTime() + intervalMax - 2);

        // Mock to return the notif builder.
        when(notifHelper.getBaseBuilder(eq(item.getContent()), anyString())).thenReturn(notifBuilder);

        // Mock the return of .build()
        Notification targetNotif = new Notification();
        when(notifBuilder.build()).thenReturn(targetNotif);

        target.createReminders(data, items);

        // Build the notif
        verify(notifHelper).notify(item.getId(), targetNotif);
    }

    @Test
    public void createReminders_Normal_NotifyNoSound() throws Exception {
        setDefaultPrefs();
        TodoistItem item = buildTodoistItem(now.getTime() + intervalMax);

        // Create an array with the mocked item
        List<TodoistItem> items = new ArrayList<>();
        items.add(item);

        // Init empty data
        Map<Integer, Reminder> data = new HashMap<>();

        // Mock to return the notif builder.
        when(notifHelper.getBaseBuilder(eq(item.getContent()), anyString())).thenReturn(notifBuilder);

        // Mock the return of .build()
        Notification targetNotif = new Notification();
        when(notifBuilder.build()).thenReturn(targetNotif);

        target.createReminders(data, items);

        // Don't produce sound
        verify(notifBuilder, never()).setSound(any(Uri.class));

        // Build the notif
        verify(notifHelper).notify(item.getId(), targetNotif);
    }

    @Test
    public void createReminders_Normal_NotifyWithSound() throws Exception {
        setDefaultPrefs();
        when(prefs.getProduceSoundBeforeDue()).thenReturn(true);
        TodoistItem item = buildTodoistItem(now.getTime() + intervalMax);

        // Create an array with the mocked item
        List<TodoistItem> items = new ArrayList<>();
        items.add(item);

        // Init empty data
        Map<Integer, Reminder> data = new HashMap<>();

        // Mock to return the notif builder.
        when(notifHelper.getBaseBuilder(eq(item.getContent()), anyString())).thenReturn(notifBuilder);

        // Mock the return of .build()
        Notification targetNotif = new Notification();
        when(notifBuilder.build()).thenReturn(targetNotif);

        Uri uri = mock(Uri.class);
        mockStatic(RingtoneManager.class);
        when(RingtoneManager.getDefaultUri(any(int.class))).thenReturn(uri);

        target.createReminders(data, items);

        // Produce sound
        verify(notifBuilder).setSound(any(Uri.class));

        // Build the notif
        verify(notifHelper).notify(item.getId(), targetNotif);
    }

    private void setDefaultPrefs() {
        when(prefs.getRemindBeforeDueMin()).thenReturn(intervalMin);
        when(prefs.getRemindBeforeDueMax()).thenReturn(intervalMax);
        when(prefs.getProduceSoundBeforeDue()).thenReturn(false);
    }

    private TodoistItem buildTodoistItem(long due) {
        return buildTodoistItem(due, defaultItemContent, 0);
    }

    private TodoistItem buildTodoistItem(long due, String content, int id) {
        TodoistItem item = mock(TodoistItem.class);
        when(item.getDueDate()).thenReturn(due);
        when(item.getContent()).thenReturn(content);
        when(item.getId()).thenReturn(id);

        return item;
    }
}