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

### [22.5.0] - Pending

**First runnable version of Multiple Subtasks for Jira**

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

[Unreleased]: https://github.com/codescape/jira-multiple-subtasks/compare/22.5.0...HEAD
[22.5.0]: https://github.com/codescape/jira-multiple-subtasks/tree/22.5.0
