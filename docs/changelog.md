---
layout: default
title: Changelog
---

This changelog helps developers and users to keep track of new features, fixes and improvements for Multiple Subtasks for Jira.
Click on the version in the following list to see all changes since the previous version.

* Table of Contents
{:toc}

### [Unreleased]

* ...

### [1.0.0] - Pending

**First runnable version of Multiple Subtasks for Jira**

* chore: initial commit
* docs: add changelog to future online documentation
* chore: remove unused auto-generated classes
* feat: create first draft for the create sub-task popup dialog
* chore: remove duplicate junit dependency and use latest amps version
* feat: create sub-tasks with summary for every line of the provided string
* chore: add markers in code where further implementation is required
* chore: cleanup code and remove debug logging from action
* refactor: format service class according to code style
* feat: render button to start dialog under default create sub-task dialog
* feat: improve internationalization and layout in create dialog
* feat: do only render button for issues that are no sub-tasks
* chore: organize imports and remove unused imports
* feat: only render button for issues in projects with sub-tasks configured
* chore: rename service implementation for sub-tasks creation
* feat: separate syntax parsing and sub-task creation (#21)
* feat: use google splitter to interpret textual sub-task representation (#22)
* docs: add documentation to format exception
* feat: catch errors during task creation and display error details
* feat: inherit priority for new sub-task from parent issue
* docs: add documentation to sub-task request
* feat: improve parsing and accept colons in sub-task summary
* chore: update supported Jira versions and dependency versions
* chore: update Maven profiles using template from Scrum Poker for Jira
* chore: replace deprecated JUnit assertions by Hamcrest assertions
* feat: use provided priority or else fall back to priority of parent issue
* i18n: change label for multiple sub-tasks dialog
* feat: display input string and potential input errors after submit

[Unreleased]: https://github.com/codescape/jira-multiple-subtasks/compare/1.0.0...HEAD
[1.0.0]: https://github.com/codescape/jira-multiple-subtasks/tree/1.0.0
