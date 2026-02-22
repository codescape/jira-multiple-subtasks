---
layout: default
title: Changelog 25.x
category: Administration
---

{% include changelog-preface.md %}

* Table of Contents
{:toc}

### [25.12.0] - 2025-12-30

**Improve importer and minor refactorings**

* chore: include jenkins repository for external libraries
* chore: improve Github action for continuous integration
* test: add test creating high number of subtasks
* chore: add code of conduct to repository
* refactor: use parameterized log messages
* refactor: remove unused or obsolete imports
* feat: ensure compatibility with Jira 11.2.0
* refactor: extract URL validation into util class
* feat: ensure compatibility with Jira 11.3.0
* docs: improve documentation for assignee in syntax
* fix: importer ignores attributes without values
* fix: improve logging for empty attributes in importer

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

[25.12.0]: https://github.com/codescape/jira-multiple-subtasks/compare/25.10.0...25.12.0
[25.10.0]: https://github.com/codescape/jira-multiple-subtasks/compare/25.05.0...25.10.0
[25.05.0]: https://github.com/codescape/jira-multiple-subtasks/compare/24.11.0...25.05.0
