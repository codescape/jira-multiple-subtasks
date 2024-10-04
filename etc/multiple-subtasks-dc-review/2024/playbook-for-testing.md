## jira.yml

custom_dataset_query: project = KANBAN and issuetype in standardIssueTypes()         # Write JQL query to add JQL output to the app/datasets/jira/custom-issues.csv, e.g. "summary ~ 'AppIssue*'"

## jira_ui.py:

def test_1_selenium_custom_action(jira_webdriver, jira_datasets, jira_screen_shots):
    extension_ui.app_specific_action(jira_webdriver, jira_datasets)

## extension_ui.py:

def app_specific_action(webdriver, datasets):
    page = BasePage(webdriver)
    if datasets['custom_issues']:
        issue_key = datasets['custom_issue_key']

    # To run action as specific user uncomment code bellow.
    # NOTE: If app_specific_action is running as specific user, make sure that app_specific_action is running
    # just before test_2_selenium_z_log_out action
    #
    # @print_timing("selenium_app_specific_user_login")
    # def measure():
    #     def app_specific_user_login(username='admin', password='admin'):
    #         login_page = Login(webdriver)
    #         login_page.delete_all_cookies()
    #         login_page.go_to()
    #         login_page.set_credentials(username=username, password=password)
    #         if login_page.is_first_login():
    #             login_page.first_login_setup()
    #         if login_page.is_first_login_second_page():
    #             login_page.first_login_second_page_setup()
    #         login_page.wait_for_page_loaded()
    #     app_specific_user_login(username='admin', password='admin')
    # measure()

    @print_timing("selenium_app_custom_action")
    def measure():
        @print_timing("selenium_app_custom_action:open_dialog")
        def sub_measure():
            page.go_to_url(f"{JIRA_SETTINGS.server_url}/secure/MultipleSubtasksDialog!default.jspa?issueKey={issue_key}")
            page.wait_until_visible((By.ID, "subtaskInputString"))  # Wait for the textarea for subtasks input available
        sub_measure()
    measure()
