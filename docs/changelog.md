---
layout: default
title: Changelog
category: Administration
---

This changelog helps developers and users to keep track of new features, fixes and improvements for Multiple Subtasks for Jira.
Click on the version in the following list to see all changes since the previous version.

* Table of Contents
{:toc}

### [Unreleased]

* docs: add Java documentation to model classes
* feat: import templates from Quick Subtasks for Jira (#74)
* docs: fix table of contents for template import documentation (#74)
* docs: fix typo in syntax documentation (#75)
* docs: improve documentation for template import (#74)
* refactor: ensure null safety for warnings of created subtasks
* test: add tests for project administration condition
* test: add tests for dueDate syntax usage (#53)
* test: add tests for constant values

### [23.02.1] - 2023-02-19

**Syntax now features due date and version fields**

* feat: warning for invalid subtask priority (#62)
* refactor: simplify code in subtask creation service
* docs: improve documentation for custom fields
* feat: extend syntax and allow to set fix version and affected version (#56)
* feat: allow to use @inherit on fixVersion and affectedVersion (#72)
* feat: extend syntax and allow to set due date (#53)
* docs: improve java documentation for new classes
* docs: add documentation to create tables in description field

### [23.02.0] - 2023-02-05

**Custom fields can now be set on subtasks**

* feat: ensure compatibility with Jira 9.6.0, 9.4.2 and 8.20.17
* feat: secure configuration REST endpoint against illegal access (#61)
* feat: extend syntax and allow to fill custom fields with values (#50)
* feat: display warnings for each subtask on confirmation screen (#50)
* feat: add documentation for custom field support (#50)

### [23.01.0] - 2023-01-29

**Multiple Subtasks can now be configured**

* docs: improve documentation for configuration of the plugin
* feat: extend syntax and allow to provide watchers for subtasks (#59)
* feat: allow to configure maximum number of templates per user and per project (#55)

### [22.12.0] - 2022-12-12

**Better subtask templates for users and projects (new)**

* refactor: improve implementation for subtask templates persistence
* test: improve overall test coverage for model classes
* docs: add developer documentation for service classes
* chore: format dialog action class
* feat: project specific subtask templates
* refactor: remove dependency on user from subtask template service
* feat: ensure compatibility with Jira 9.5.0
* test: improve test coverage for service classes

### [22.11.1] - 2022-11-21

**Improve user templates and support estimates for subtasks**

* chore: drop support for Jira 8.13.x and ensure support for latest versions
* feat: allow users to edit existing user templates (#51)
* feat: validate syntax when creating and editing user templates (#42)
* feat: extend syntax and allow to provide estimates for subtasks (#18)
* chore: optimize imports for all Java classes

### [22.11.0] - 2022-11-14

**Multiple Subtasks is ready for Data Center**

* feat: prepare application for Data Center compatibility tests
* feat: allow to close the dialog even with missing or invalid license
* chore: adjust dependency versions to versions defined by Jira 9.3.0
* docs: update Marketplace URL in online documentation
* docs: improve developer documentation for all Java classes
* chore: update OWASP dependency check to the latest version for DC review
* chore: add dependency and security report for DC review
* chore: provide dialog with id to be accessible from performance tests
* chore: allow license check to be ignored for testing purposes
* feat: ensure summary length does not exceed 255 characters
* docs: changelog is now part of the administration chapter
* feat: provide full i18n for dialog and template page
* feat: add placeholders for subtask input field
* docs: add developer documentation on releasing new versions
* feat: limit number of subtask templates per user
* feat: ensure label length does not exceed 255 characters
* feat: support Jira 9.3.1 and enterprise versions 8.20.14 and 8.13.27
* feat: ensure template names do not exceed 255 characters
* feat: reduce maximum template name length to 80 characters

### [22.10.0] - 2022-10-09

**Multiple Subtasks for Jira is born!**

* chore: initial commit
* docs: add changelog to future online documentation
* chore: remove unused auto-generated classes
* feat: create first draft for the create subtask popup dialog
* chore: remove duplicate junit dependency and use latest amps version
* feat: create subtasks with summary for every line of the provided string
* chore: add markers in code where further implementation is required
* chore: cleanup code and remove debug logging from action
* refactor: format service class according to code style
* feat: render button to start dialog under default create subtask dialog
* feat: improve internationalization and layout in create dialog
* feat: do only render button for issues that are no subtasks
* chore: organize imports and remove unused imports
* feat: only render button for issues in projects with subtasks configured
* chore: rename service implementation for subtasks creation
* feat: separate syntax parsing and subtask creation (#21)
* feat: use google splitter to interpret textual subtask representation (#22)
* docs: add documentation to format exception
* feat: catch errors during task creation and display error details
* feat: inherit priority for new subtask from parent issue
* docs: add documentation to subtask request
* feat: improve parsing and accept colons in subtask summary
* chore: update supported Jira versions and dependency versions
* chore: update Maven profiles using template from Scrum Poker for Jira
* chore: replace deprecated JUnit assertions by Hamcrest assertions
* feat: use provided priority or else fall back to priority of parent issue
* i18n: change label for multiple subtasks dialog
* feat: display input string and potential input errors after submit
* feat: support explicit input for issue type of the created subtasks
* chore: remove link to Travis CI
* feat: support explicit input for assignee of the created subtasks
* feat: support explicit input for reporter of the created subtasks
* feat: support explicit input for description of the created subtasks
* feat: improve dialog layout
* feat: allow to reset the form after successfully creating subtasks
* chore: cleanup versioning and readme
* feat: hints only display when input element is visible
* feat: correctly style close button on dialog
* chore: extract constants in code
* docs: add documentation of currently supported syntax
* feat: support explicit input for labels of the created subtasks
* feat: support explicit input for components of the created subtasks
* feat: components can be inherited from parent issue
* feat: labels can be inherited from parent issue
* feat: description can be a multi-line description
* feat: assign subtask to current user or inherit from parent issue
* test: add tests for condition checks to render dialog
* feat: reject unknown attributes for subtasks
* feat: improve compatibility with Jira 9.0.0
* feat: include xsrf check for form submission (#31)
* feat: ensure compatibility with Jira 8.x and 9.x
* test: use GitHub actions for continuous integration
* docs: add table of content to syntax page
* feat: enable licensing for plugin
* docs: provide initial version of the documentation pages
* docs: update readme and reference documentation
* feat: allow managing and using user defined subtask templates
* feat: provide app logo (#4)
* docs: add documentation to manage subtasks
* feat: support Jira 9.3.0 and enterprise versions 8.20.13 and 8.13.26
* feat: reload page after subtasks are created and dialog is closed (#30)
* feat: check for valid license in subtask dialog (#6)
* feat: restrict the maximum length of a template to 4000 characters

[Unreleased]: https://github.com/codescape/jira-multiple-subtasks/compare/23.02.1...HEAD
[23.02.1]: https://github.com/codescape/jira-multiple-subtasks/compare/23.02.0...23.02.1
[23.02.0]: https://github.com/codescape/jira-multiple-subtasks/compare/23.01.0...23.02.0
[23.01.0]: https://github.com/codescape/jira-multiple-subtasks/compare/22.12.0...23.01.0
[22.12.0]: https://github.com/codescape/jira-multiple-subtasks/compare/22.11.1...22.12.0
[22.11.1]: https://github.com/codescape/jira-multiple-subtasks/compare/22.11.0...22.11.1
[22.11.0]: https://github.com/codescape/jira-multiple-subtasks/compare/22.10.0...22.11.0
[22.10.0]: https://github.com/codescape/jira-multiple-subtasks/tree/22.10.0
