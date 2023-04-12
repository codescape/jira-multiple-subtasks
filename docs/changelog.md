---
layout: default
title: Changelog
category: Administration
---

{% include changelog-preface.md %}

* Table of Contents
{:toc}

### [Unreleased]

* feat: ensure compatibility with Jira 9.7.0, 9.4.4 and 8.20.20
* feat: improve editing of a subtask template (#84)

### [23.03.4] - 2023-03-29

**Security levels for subtasks and improved imports**

* docs: optimize anchor links on syntax documentation page
* docs: improve headings on syntax documentation page
* refactor: simplify regex for Quick Subtasks template syntax
* feat: improve output for Quick Subtasks template import
* feat: always inherit issue security level from parent issue

### [23.03.3] - 2023-03-19

**Inherit and use summary from parent issue**

* feat: add support for @inherit in summary (#82)

### [23.03.2] - 2023-03-15

**Assign relative dates and improved template import**

* feat: extend syntax and allow to set relative dates (#73)
* feat: validate for absolute or relative date on due date field (#73)
* docs: add documentation for relative dates (#73)
* feat: improve logging and output for template import
* feat: limit subtask creation to users with permission (#20)

### [23.03.1] - 2023-03-13

**More robustness for template imports**

* feat: ignore dividing slashes for Quick Subtasks template imports 

### [23.03.0] - 2023-03-05

**Support for all Jira standard custom fields**

* feat: add support for @inherit and @current on watcher (#71)
* feat: add warning for invalid user provided as subtask watcher (#64)
* feat: add support for custom fields of type checkbox (#69)
* feat: add support for custom fields of type url (#68)
* feat: add support for custom fields of type user (#66)
* feat: add support for custom fields of type select list cascading (#77)
* feat: add support for custom fields of type date (#65)
* docs: fix documentation for select list cascading format (#77)
* feat: add support for custom fields of type date and time (#67)
* feat: add support for custom fields of type labels (#76)
* feat: add warning for invalid value provided as subtask component (#63)
* docs: improve documentation for custom fields (#78)
* docs: separate changelog for every major version
* docs: improve developer documentation for subtask creation service

### [23.02.2] - 2023-02-26

**Import project and user templates**

* docs: add Java documentation to model classes
* feat: import templates from Quick Subtasks for Jira (#74)
* docs: fix table of contents for template import documentation (#74)
* docs: fix typo in syntax documentation (#75)
* docs: improve documentation for template import (#74)
* refactor: ensure null safety for warnings of created subtasks
* test: add tests for project administration condition
* test: add tests for dueDate syntax usage (#53)
* test: add tests for constant values
* refactor: ignore unchecked conversion for database updater
* refactor: move syntax related services into separate package
* feat: fix typo on German template import page
* feat: transform syntax for imported templates (#74)

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

### Older versions

Older versions have been moved into separate changelog documents grouped by their major version:

* [Multiple Subtasks for Jira 22.x](/changelog-22x)

[Unreleased]: https://github.com/codescape/jira-multiple-subtasks/compare/23.03.4...HEAD
[23.03.4]: https://github.com/codescape/jira-multiple-subtasks/compare/23.03.3...23.03.4
[23.03.3]: https://github.com/codescape/jira-multiple-subtasks/compare/23.03.2...23.03.3
[23.03.2]: https://github.com/codescape/jira-multiple-subtasks/compare/23.03.1...23.03.2
[23.03.1]: https://github.com/codescape/jira-multiple-subtasks/compare/23.03.0...23.03.1
[23.03.0]: https://github.com/codescape/jira-multiple-subtasks/compare/23.02.2...23.03.0
[23.02.2]: https://github.com/codescape/jira-multiple-subtasks/compare/23.02.1...23.02.2
[23.02.1]: https://github.com/codescape/jira-multiple-subtasks/compare/23.02.0...23.02.1
[23.02.0]: https://github.com/codescape/jira-multiple-subtasks/compare/23.01.0...23.02.0
[23.01.0]: https://github.com/codescape/jira-multiple-subtasks/compare/22.12.0...23.01.0
