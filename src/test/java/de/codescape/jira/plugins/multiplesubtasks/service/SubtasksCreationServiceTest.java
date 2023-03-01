package de.codescape.jira.plugins.multiplesubtasks.service;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.watchers.WatcherManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.google.common.collect.ArrayListMultimap;
import de.codescape.jira.plugins.multiplesubtasks.model.CreatedSubtask;
import de.codescape.jira.plugins.multiplesubtasks.model.Markers;
import de.codescape.jira.plugins.multiplesubtasks.model.Subtask;
import de.codescape.jira.plugins.multiplesubtasks.service.syntax.SubtasksSyntaxService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SubtasksCreationServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private IssueService issueService;

    @Mock
    private IssueFactory issueFactory;

    @Mock
    private IssueManager issueManager;

    @Mock
    private WatcherManager watcherManager;

    @Mock
    private UserManager userManager;

    @Mock
    private SubTaskManager subTaskManager;

    @Mock
    private SubtasksSyntaxService subtasksSyntaxService;

    @Mock
    private JiraAuthenticationContext jiraAuthenticationContext;

    @InjectMocks
    private SubtasksCreationService subtasksCreationService;

    @Mock
    private MutableIssue parent;

    @Mock
    private ApplicationUser currentUser;

    @Mock
    private Project project;

    @Mock
    private IssueType subtaskIssueType;

    private static final String ISSUE_KEY = "DEMO-1";

    @Before
    public void before() {
        // current user is known
        when(jiraAuthenticationContext.getLoggedInUser()).thenReturn(currentUser);

        // parent issue is known
        IssueService.IssueResult issueResult = mock(IssueService.IssueResult.class);
        when(issueService.getIssue(eq(currentUser), eq(ISSUE_KEY))).thenReturn(issueResult);
        when(issueResult.getIssue()).thenReturn(parent);

        // current project is known
        when(parent.getProjectObject()).thenReturn(project);

        // project supports sub tasks
        ArrayList<IssueType> issueTypes = new ArrayList<>();
        issueTypes.add(subtaskIssueType);
        when(project.getIssueTypes()).thenReturn(issueTypes);
        when(subtaskIssueType.isSubTask()).thenReturn(true);
    }

    @Test
    public void shouldAddKnownWatcher() throws Exception {
        // expect subtask with attributes
        ArrayList<Subtask> subtasks = new ArrayList<>();
        ArrayListMultimap<String, String> attributes = ArrayListMultimap.create();
        attributes.put("summary", "a task");
        attributes.put("watcher", "known.user");
        subtasks.add(new Subtask(attributes));
        when(subtasksSyntaxService.parseString(eq("input"))).thenReturn(subtasks);

        // expect subtask to be created
        MutableIssue subtask = expectNewSubtaskIssue();

        // expect user to be looked up
        ApplicationUser knownUser = expectUserWithUsername("known.user");

        subtasksCreationService.subtasksFromString(ISSUE_KEY, "input");

        // verify issue is created and linked to parent
        verify(issueManager).createIssueObject(eq(currentUser), eq(subtask));
        verify(subTaskManager).createSubTaskIssueLink(eq(parent), eq(subtask), eq(currentUser));

        // verify watchers
        verify(watcherManager).startWatching(knownUser, subtask);
    }

    @Test
    public void shouldIgnoreUnknownWatchers() throws Exception {
        // expect subtask with attributes
        ArrayList<Subtask> subtasks = new ArrayList<>();
        ArrayListMultimap<String, String> attributes = ArrayListMultimap.create();
        attributes.put("summary", "a task");
        attributes.put("watcher", "unknown.user");
        subtasks.add(new Subtask(attributes));
        when(subtasksSyntaxService.parseString(eq("input"))).thenReturn(subtasks);

        // expect subtask to be created
        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, "input");

        // verify issue is created and linked to parent
        verify(issueManager).createIssueObject(eq(currentUser), eq(subtask));
        verify(subTaskManager).createSubTaskIssueLink(eq(parent), eq(subtask), eq(currentUser));

        // verify watchers
        verify(watcherManager, new Times(0)).startWatching(any(ApplicationUser.class), eq(subtask));
    }

    @Test
    public void shouldUseCurrentUserAsWatcher() throws Exception {
        // expect subtask with attributes
        ArrayList<Subtask> subtasks = new ArrayList<>();
        ArrayListMultimap<String, String> attributes = ArrayListMultimap.create();
        attributes.put("summary", "a task");
        attributes.put("watcher", Markers.CURRENT_MARKER);
        subtasks.add(new Subtask(attributes));
        when(subtasksSyntaxService.parseString(eq("input"))).thenReturn(subtasks);

        // expect subtask to be created
        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, "input");

        // verify issue is created and linked to parent
        verify(issueManager).createIssueObject(eq(currentUser), eq(subtask));
        verify(subTaskManager).createSubTaskIssueLink(eq(parent), eq(subtask), eq(currentUser));

        // verify watchers
        verify(watcherManager).startWatching(currentUser, subtask);
    }

    /* helpers */

    private ApplicationUser expectUserWithUsername(String username) {
        ApplicationUser knownUser = mock(ApplicationUser.class);
        when(userManager.getUserByName(eq(username))).thenReturn(knownUser);
        return knownUser;
    }

    private MutableIssue expectNewSubtaskIssue() {
        MutableIssue subtask = mock(MutableIssue.class);
        when(issueFactory.getIssue()).thenReturn(subtask);
        return subtask;
    }

}
