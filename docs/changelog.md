---
layout: default
title: Changelog
category: Administration
---

{% include changelog-preface.md %}

* Table of Contents
{:toc}

### [Unreleased]

* chore: include jenkins repository for external libraries
* chore: improve Github action for continuous integration

### [25.10.0] -  2025-10-03

**Multiple Subtasks supports Jira 11**

* refactor: use maven compiler setting for Java 17
* feat: ensure compatibility with Jira 10.7.1
* feat: ensure compatibility with Jira 10.7.4
* chore: data center review 2025
* feat: ensure compatibility with Jira 11.1.0

### [25.05.0] -  2025-05-17

**Compatibility updates for Jira 10.3 - 10.6**

* chore: simplify Docker configuration for testing
* feat: ensure compatibility with Jira 10.3.0
* refactor: improve structure of subtask creation service
* feat: ensure compatibility with Jira 10.4.0
* refactor: use Java 16 syntax for lists
* refactor: extract logic for custom field name syntax
* test: improve tests for custom field name logic
* docs: improve formatting of code examples
* feat: ensure compatibility with Jira 10.5.0
* feat: ensure compatibility with Jira 10.6.0

### Older versions

Older versions have been moved into separate changelog documents grouped by their major version:

* [Multiple Subtasks for Jira 24.x](/changelog-24x)
* [Multiple Subtasks for Jira 23.x](/changelog-23x)
* [Multiple Subtasks for Jira 22.x](/changelog-22x)

[Unreleased]: https://github.com/codescape/jira-multiple-subtasks/compare/25.10.0...HEAD
[25.10.0]: https://github.com/codescape/jira-multiple-subtasks/compare/25.05.0...25.10.0
[25.05.0]: https://github.com/codescape/jira-multiple-subtasks/compare/24.11.0...25.05.0
