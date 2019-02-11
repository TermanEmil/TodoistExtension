package com.university.unicornslayer.todoistextension.utils.reminder.agents;

import android.app.Notification;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.university.unicornslayer.todoistextension.data.model.TodoistItem;
import com.university.unicornslayer.todoistextension.utils.notif.TodoistNotifHelper;
import com.university.unicornslayer.todoistextension.utils.reminder.model.NextReminderModel;
import com.university.unicornslayer.todoistextension.utils.reminder.model.RelativeToNowPrefsProvider;
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
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ReminderRelativeToNowAgent.class, RingtoneManager.class })
public class ReminderRelativeToNowAgentTest {
    private static final long intervalMin = 0;
    private static final long intervalMax = 10;
    private static final String defaultItemContent = "foo";

    private ReminderRelativeToNowAgent target;

    @Mock
    private RelativeToNowPrefsProvider prefs;

    @Mock
    private TodoistNotifHelper notifHelper;

    @Mock
    List<TodoistItem> testItems;

    @Mock
    NotificationCompat.Builder notifBuilder;

    private Date now;

    @Before
    public void setUp() throws Exception {
        target = new ReminderRelativeToNowAgent(prefs, notifHelper) {
            @Override
            public String getResourceKey() {
                return "test key";
            }
        };

        when(notifBuilder.setContentTitle(anyString())).thenReturn(notifBuilder);
        when(notifBuilder.setContentText(anyString())).thenReturn(notifBuilder);

        now = new Date(0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        mockStatic(Calendar.class);
        when(Calendar.getInstance()).thenReturn(calendar);

        whenNew(Date.class).withNoArguments().thenReturn(now);
    }

    @Test
    public void createReminders_ItsDisabled_DoNothing() {
        when(prefs.getIntervalMax()).thenReturn(new Long(-1));
        when(testItems.size()).thenReturn(1);

        target.createReminders(null, testItems);

        // No notifications are made
        verifyZeroInteractions(notifHelper);
    }

    @Test
    public void createReminders_NoItems_DoNothing() {
        when(prefs.getIntervalMax()).thenReturn(new Long(1));
        when(testItems.size()).thenReturn(0);

        target.createReminders(null, testItems);

        // No notifications are made
        verifyZeroInteractions(notifHelper);
    }

    @Test
    public void createReminders_ItemNotInRange_DoNothing() throws Exception {
        // Set limits
        when(prefs.getIntervalMin()).thenReturn(intervalMin);
        when(prefs.getIntervalMax()).thenReturn(intervalMax);

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
        when(prefs.getIntervalMin()).thenReturn(intervalMin);
        when(prefs.getIntervalMax()).thenReturn(intervalMax);

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
        when(notifHelper.getBaseBuilder()).thenReturn(notifBuilder);

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
        when(notifHelper.getBaseBuilder()).thenReturn(notifBuilder);

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
        when(notifHelper.getBaseBuilder()).thenReturn(notifBuilder);

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
        when(notifHelper.getBaseBuilder()).thenReturn(notifBuilder);

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
        when(prefs.produceSound()).thenReturn(true);
        TodoistItem item = buildTodoistItem(now.getTime() + intervalMax);

        // Create an array with the mocked item
        List<TodoistItem> items = new ArrayList<>();
        items.add(item);

        // Init empty data
        Map<Integer, Reminder> data = new HashMap<>();

        // Mock to return the notif builder.
        when(notifHelper.getBaseBuilder()).thenReturn(notifBuilder);

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

    @Test
    public void createReminders_Normal_NotifHasTitleAndMsg() throws Exception {
        setDefaultPrefs();
        when(prefs.produceSound()).thenReturn(true);
        TodoistItem item = buildTodoistItem(now.getTime() + intervalMax);

        // Create an array with the mocked item
        List<TodoistItem> items = new ArrayList<>();
        items.add(item);

        // Init empty data
        Map<Integer, Reminder> data = new HashMap<>();

        // Mock to return the notif builder.
        when(notifHelper.getBaseBuilder()).thenReturn(notifBuilder);

        // Mock the return of .build()
        Notification targetNotif = new Notification();
        when(notifBuilder.build()).thenReturn(targetNotif);

        Uri uri = mock(Uri.class);
        mockStatic(RingtoneManager.class);
        when(RingtoneManager.getDefaultUri(any(int.class))).thenReturn(uri);

        target.createReminders(data, items);

        verify(notifBuilder).setContentTitle(item.getContent());
        verify(notifBuilder).setContentText(matches(".*[Dd]ue.*"));
    }

    @Test
    public void createReminders_TwiceNormal_NotifFirstOnly() throws Exception {
        setDefaultPrefs();
        TodoistItem item = buildTodoistItem(now.getTime() + intervalMax);

        // Create an array with the mocked item
        List<TodoistItem> items = new ArrayList<>();
        items.add(item);

        // Init empty data
        Map<Integer, Reminder> data = new HashMap<>();

        // Mock to return the notif builder.
        when(notifHelper.getBaseBuilder()).thenReturn(notifBuilder);

        // Mock the return of .build()
        Notification targetNotif = new Notification();
        when(notifBuilder.build()).thenReturn(targetNotif);

        target.createReminders(data, items);

        // Build the notif
        verify(notifHelper).notify(item.getId(), targetNotif);

        reset(notifHelper);
        target.createReminders(data, items);

        // Shouldn't notify since it's saved in the data
        verifyNoMoreInteractions(notifHelper);
    }

    @Test
    public void getNextItemToRemind_NotInRange_GiveNull() throws Exception {
        setDefaultPrefs();
        TodoistItem item = buildTodoistItem(now.getTime() + intervalMax - 1);
        when(item.dueIsInFuture(any(long.class))).thenReturn(Boolean.TRUE);

        // Create an array with the mocked item
        List<TodoistItem> items = new ArrayList<>();
        items.add(item);

        NextReminderModel result = target.getNextItemToRemind(items);

        assert result == null;
    }

    @Test
    public void getNextItemToRemind_InRange_ReturnIt() throws Exception {
        setDefaultPrefs();
        TodoistItem item = buildTodoistItem(now.getTime() + intervalMax);
        when(item.dueIsInFuture(any(long.class))).thenReturn(Boolean.TRUE);

        // Create an array with the mocked item
        List<TodoistItem> items = new ArrayList<>();
        items.add(item);

        NextReminderModel result = target.getNextItemToRemind(items);

        assert result != null;
        assert result.getItem() == item;
        assert result.getWhen() == item.getDueDate() - prefs.getIntervalMax();
    }

    @Test
    public void getNextItemToRemind_MultipleInRange_ReturnClosest() throws Exception {
        setDefaultPrefs();

        TodoistItem item1 = buildTodoistItem(now.getTime() + intervalMax + 2);
        when(item1.dueIsInFuture(any(long.class))).thenReturn(Boolean.TRUE);

        TodoistItem item2 = buildTodoistItem(now.getTime() + intervalMax + 1);
        when(item2.dueIsInFuture(any(long.class))).thenReturn(Boolean.TRUE);

        // Create an array with the mocked item
        List<TodoistItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        NextReminderModel result = target.getNextItemToRemind(items);

        assert result != null;
        assert result.getItem() == item2;
        assert result.getWhen() == item2.getDueDate() - prefs.getIntervalMax();
    }

    @Test
    public void removeOldData_OneOldOneNewItem_RemoveItLeaveIt() throws Exception {
        setDefaultPrefs();

        TodoistItem item1 = buildTodoistItem(now.getTime() + intervalMin - 1, 0);
        TodoistItem item2 = buildTodoistItem(now.getTime() + intervalMin + 1, 1);

        Map<Integer, Reminder> data = new HashMap<>();
        data.put(item1.getId(), new Reminder(item1));
        data.put(item2.getId(), new Reminder(item2));

        target.removeOldData(data, null);

        assert data.size() == 1;
        assert data.containsKey(item2.getId());
    }

    private void setDefaultPrefs() {
        when(prefs.getIntervalMin()).thenReturn(intervalMin);
        when(prefs.getIntervalMax()).thenReturn(intervalMax);
        when(prefs.produceSound()).thenReturn(false);
    }

    private TodoistItem buildTodoistItem(long due) {
        return buildTodoistItem(due, 0);
    }

    private TodoistItem buildTodoistItem(long due, int id) {
        return buildTodoistItem(due, defaultItemContent, id);
    }

    private TodoistItem buildTodoistItem(long due, String content, int id) {
        TodoistItem item = mock(TodoistItem.class);
        when(item.getDueDate()).thenReturn(due);
        when(item.getContent()).thenReturn(content);
        when(item.getId()).thenReturn(id);

        return item;
    }
}