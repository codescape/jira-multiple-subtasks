package de.codescape.jira.plugins.multiplesubtasks.service;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.watchers.WatcherManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.google.common.collect.ArrayListMultimap;
import de.codescape.jira.plugins.multiplesubtasks.model.CreatedSubtask;
import de.codescape.jira.plugins.multiplesubtasks.model.Markers;
import de.codescape.jira.plugins.multiplesubtasks.model.Subtask;
import de.codescape.jira.plugins.multiplesubtasks.service.syntax.EstimateStringService;
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

import static de.codescape.jira.plugins.multiplesubtasks.model.Markers.CURRENT_MARKER;
import static de.codescape.jira.plugins.multiplesubtasks.model.Markers.INHERIT_MARKER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class SubtasksCreationServiceTest {

    private static final String INPUT_STRING = "inputString";
    private static final String ISSUE_KEY = "DEMO-1";

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

    @Mock
    private EstimateStringService estimateStringService;

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

    /* pre-checks */

    @Test(expected = RuntimeException.class)
    public void shouldStopIfIssueToCreateSubtasksForIsSubtaskItself() {
        expectSubtaskWithSummary("subtask for another subtask?");
        when(parent.isSubTask()).thenReturn(true);

        subtasksCreationService.subtasksFromString(ISSUE_KEY, "- subtask for another subtask?");
        fail("Should return a RuntimeException.");
    }

    /* summary */

    @Test
    public void shouldUseSimpleSummary() {
        expectSubtaskWithSummary("a simple summary");
        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        verify(subtask, new Times(1)).setSummary("a simple summary");
    }

    @Test
    public void shouldInheritSummaryFromParent() {
        expectSubtaskWithSummary("@inherit");
        expectParentWithSummary("using summary of parent");
        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        verify(subtask, new Times(1)).setSummary("using summary of parent");
    }

    @Test
    public void shouldReplaceInheritWithSummaryFromParent() {
        expectSubtaskWithSummary("prefix for @inherit");
        expectParentWithSummary("parent summary");
        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        verify(subtask, new Times(1)).setSummary("prefix for parent summary");
    }

    @Test
    public void shouldNotReplaceEscapedInherit() {
        expectSubtaskWithSummary("do not replace \\@inherit");
        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        verify(subtask, new Times(1)).setSummary("do not replace @inherit");
    }

    /* description */

    @Test
    public void shouldUseSimpleDescription() {
        expectSubtaskWithDescription("Simple text!");
        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        verify(subtask, new Times(1)).setDescription("Simple text!");
    }

    @Test
    public void shouldInheritDescriptionFromParent() {
        expectSubtaskWithDescription(INHERIT_MARKER);
        MutableIssue subtask = expectNewSubtaskIssue();

        when(parent.getDescription()).thenReturn("Parent text!");

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        verify(subtask, new Times(1)).setDescription("Parent text!");
    }

    @Test
    public void shouldReplaceInheritWithDescriptionFromParent() {
        expectSubtaskWithDescription("Let's say " + INHERIT_MARKER);
        MutableIssue subtask = expectNewSubtaskIssue();

        when(parent.getDescription()).thenReturn("hello world!");

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        verify(subtask, new Times(1)).setDescription("Let's say hello world!");
    }

    /* security level */

    @Test
    public void shouldNotSetAnySecurityLevelIfParentHasNone() {
        expectSubtaskWithSummary("with security on parent");
        when(parent.getSecurityLevelId()).thenReturn(null);
        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        verify(subtask, new Times(0)).setSecurityLevelId(anyLong());
    }

    @Test
    public void shouldSetSecurityLevelAccordingToParentSecurityLevel() {
        expectSubtaskWithSummary("without security on parent");
        when(parent.getSecurityLevelId()).thenReturn(10004L);
        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        verify(subtask, new Times(1)).setSecurityLevelId(eq(10004L));
    }

    /* priority */

    @Test
    public void shouldAllowToInheritPriorityExplicitly() {
        List<Subtask> subtasksRequest = new ArrayList<>();
        Subtask subtaskRequest = mock(Subtask.class);
        when(subtaskRequest.getSummary()).thenReturn("Inherit priority please");
        when(subtaskRequest.getPriority()).thenReturn(INHERIT_MARKER);
        subtasksRequest.add(subtaskRequest);
        expectSubtasksFromInputString(subtasksRequest);

        MutableIssue subtask = expectNewSubtaskIssue();

        Priority priority = mock(Priority.class);
        when(parent.getPriority()).thenReturn(priority);

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        verify(subtask, times(1)).setPriority(priority);
    }

    /* reporter */

    @Test
    public void shouldAllowToInheritReporterFromParent() {
        List<Subtask> subtasksRequest = new ArrayList<>();
        Subtask subtaskRequest = mock(Subtask.class);
        when(subtaskRequest.getSummary()).thenReturn("Inherit reporter please");
        when(subtaskRequest.getReporter()).thenReturn(INHERIT_MARKER);
        subtasksRequest.add(subtaskRequest);
        expectSubtasksFromInputString(subtasksRequest);

        ApplicationUser reporter = mock(ApplicationUser.class);
        when(parent.getReporter()).thenReturn(reporter);

        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        verify(subtask, times(1)).setReporter(reporter);
    }

    @Test
    public void shouldAllowToSetCurrentUserAsReporterExplicitly() {
        List<Subtask> subtasksRequest = new ArrayList<>();
        Subtask subtaskRequest = mock(Subtask.class);
        when(subtaskRequest.getSummary()).thenReturn("Inherit reporter please");
        when(subtaskRequest.getReporter()).thenReturn(CURRENT_MARKER);
        subtasksRequest.add(subtaskRequest);
        expectSubtasksFromInputString(subtasksRequest);

        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        verify(subtask, times(1)).setReporter(currentUser);
    }

    @Test
    public void shouldAllowToSetUserAsReporter() {
        List<Subtask> subtasksRequest = new ArrayList<>();
        Subtask subtaskRequest = mock(Subtask.class);
        when(subtaskRequest.getSummary()).thenReturn("Inherit reporter please");
        when(subtaskRequest.getReporter()).thenReturn("codescape");
        subtasksRequest.add(subtaskRequest);
        expectSubtasksFromInputString(subtasksRequest);

        ApplicationUser explicitUser = mock(ApplicationUser.class);
        when(userManager.getUserByName("codescape")).thenReturn(explicitUser);

        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        verify(subtask, times(1)).setReporter(explicitUser);
    }

    @Test
    public void shouldFallBackToCurrentUserAsReporterIfCustomUserDoesNotExist() {
        List<Subtask> subtasksRequest = new ArrayList<>();
        Subtask subtaskRequest = mock(Subtask.class);
        when(subtaskRequest.getSummary()).thenReturn("Inherit reporter please");
        when(subtaskRequest.getReporter()).thenReturn("unknown");
        subtasksRequest.add(subtaskRequest);
        expectSubtasksFromInputString(subtasksRequest);

        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        verify(subtask, times(1)).setReporter(currentUser);
    }

    /* watcher(s) */

    @Test
    public void shouldAddKnownWatcher() throws Exception {
        // expect subtask with attributes
        ArrayList<Subtask> subtasks = new ArrayList<>();
        ArrayListMultimap<String, String> attributes = ArrayListMultimap.create();
        attributes.put(Subtask.Attributes.SUMMARY, "a task");
        attributes.put(Subtask.Attributes.WATCHER, "known.user");
        subtasks.add(new Subtask(attributes));
        expectSubtasksFromInputString(subtasks);

        // expect subtask to be created
        MutableIssue subtask = expectNewSubtaskIssue();

        // expect user to be looked up
        ApplicationUser knownUser = expectUserWithUsername("known.user");

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

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
        attributes.put(Subtask.Attributes.SUMMARY, "a task");
        attributes.put(Subtask.Attributes.WATCHER, "unknown.user");
        subtasks.add(new Subtask(attributes));
        expectSubtasksFromInputString(subtasks);

        // expect subtask to be created
        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

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
        attributes.put(Subtask.Attributes.SUMMARY, "a task");
        attributes.put(Subtask.Attributes.WATCHER, Markers.CURRENT_MARKER);
        subtasks.add(new Subtask(attributes));
        expectSubtasksFromInputString(subtasks);

        // expect subtask to be created
        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        // verify issue is created and linked to parent
        verify(issueManager).createIssueObject(eq(currentUser), eq(subtask));
        verify(subTaskManager).createSubTaskIssueLink(eq(parent), eq(subtask), eq(currentUser));

        // verify watchers
        verify(watcherManager).startWatching(currentUser, subtask);
    }

    /* estimate */

    @Test
    public void shouldSetEstimateAndOriginalEstimateIfValidEstimateIsProvidedForSubtask() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        ArrayListMultimap<String, String> attributes = ArrayListMultimap.create();
        attributes.put(Subtask.Attributes.SUMMARY, "a task");
        attributes.put(Subtask.Attributes.ESTIMATE, "4d");
        subtasks.add(new Subtask(attributes));
        expectSubtasksFromInputString(subtasks);

        // expect estimate service to resolve the long value
        when(estimateStringService.estimateStringToSeconds(eq("4d"))).thenReturn(999L);

        // expect subtask to be created
        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        // verify that both values are set
        verify(subtask, times(1)).setEstimate(999L);
        verify(subtask, times(1)).setOriginalEstimate(999L);
    }

    @Test
    public void shouldInheritEstimateFromParentIssue() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        ArrayListMultimap<String, String> attributes = ArrayListMultimap.create();
        attributes.put(Subtask.Attributes.SUMMARY, "a task");
        attributes.put(Subtask.Attributes.ESTIMATE, INHERIT_MARKER);
        subtasks.add(new Subtask(attributes));
        expectSubtasksFromInputString(subtasks);

        Long parentEstimate = 42L;
        when(parent.getEstimate()).thenReturn(parentEstimate);
        Long parentOriginalEstimate = 84L;
        when(parent.getOriginalEstimate()).thenReturn(parentOriginalEstimate);

        // expect subtask to be created
        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        // verify that both values are set
        verify(subtask, times(1)).setEstimate(parentEstimate);
        verify(subtask, times(1)).setOriginalEstimate(parentOriginalEstimate);
    }

    @Test
    public void shouldNotSetEstimateWhenInheritEstimateFromParentIssueWithoutEstimate() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        ArrayListMultimap<String, String> attributes = ArrayListMultimap.create();
        attributes.put(Subtask.Attributes.SUMMARY, "a task");
        attributes.put(Subtask.Attributes.ESTIMATE, INHERIT_MARKER);
        subtasks.add(new Subtask(attributes));
        expectSubtasksFromInputString(subtasks);

        when(parent.getEstimate()).thenReturn(null);

        MutableIssue subtask = expectNewSubtaskIssue();

        subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        verify(subtask, times(0)).setEstimate(anyLong());
    }

    /* multiple tasks */

    @Test
    public void shouldCreateMultipleTasks() {
        expectSubtasksFromInputString(randomSubtasks(100));
        expectNewSubtaskIssue();

        List<CreatedSubtask> createdSubtasks = subtasksCreationService.subtasksFromString(ISSUE_KEY, INPUT_STRING);

        assertThat(createdSubtasks.size(), is(equalTo((100))));
    }

    /* helpers */

    private List<Subtask> randomSubtasks(int numberOfSubtasks) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (int j = 0; j <numberOfSubtasks; j++) {
            ArrayListMultimap<String, String> attributes = ArrayListMultimap.create();
            attributes.put(Subtask.Attributes.SUMMARY, "random " + j +" of " + numberOfSubtasks);
            subtasks.add(new Subtask(attributes));
        }
        return subtasks;
    }

    private void expectParentWithSummary(String summary) {
        when(parent.getSummary()).thenReturn(summary);
    }

    private void expectSubtaskWithSummary(String summary) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        ArrayListMultimap<String, String> attributes = ArrayListMultimap.create();
        attributes.put(Subtask.Attributes.SUMMARY, summary);
        subtasks.add(new Subtask(attributes));
        expectSubtasksFromInputString(subtasks);
    }

    private void expectSubtaskWithDescription(String description) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        ArrayListMultimap<String, String> attributes = ArrayListMultimap.create();
        attributes.put(Subtask.Attributes.SUMMARY, "a task");
        attributes.put(Subtask.Attributes.DESCRIPTION, description);
        subtasks.add(new Subtask(attributes));
        expectSubtasksFromInputString(subtasks);
    }

    private void expectSubtasksFromInputString(List<Subtask> subtasks) {
        when(subtasksSyntaxService.parseString(eq(INPUT_STRING))).thenReturn(subtasks);
    }

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
