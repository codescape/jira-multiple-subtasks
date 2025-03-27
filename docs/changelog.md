---
layout: default
title: Changelog
category: Administration
---

{% include changelog-preface.md %}

* Table of Contents
{:toc}

### [Unreleased]

* chore: simplify Docker configuration for testing 
* feat: ensure compatibility with Jira 10.3.0
* refactor: improve structure of subtask creation service
* feat: ensure compatibility with Jira 10.4.0
* refactor: use Java 16 syntax for lists
* refactor: extract logic for custom field name syntax
* test: improve tests for custom field name logic
* docs: improve formatting of code examples

### [24.11.0] - 2024-11-11

**Multiple Subtasks supports Jira 10**

* feat: ensure compatibility with Jira 9.17.2, 9.12.12, 9.4.25
* feat: Multiple Subtasks supports Jira 10
* chore: switch to new Jackson library for REST
* chore: migration to REST 2 scheme
* chore: add Velocity allow-list for allowed methods
* chore: use @Inject instead of @Autowired
* chore: compile against Java 17
* chore: remove @Component from conditions
* chore: add docker configuration
* docs: update compatibility matrix
* chore: change dialog behaviour to work with Jira 10
* fix: improve css classes for jQuery selectors from board

### [24.08.0] - 2024-08-20

**Introducing a new REST API**

* feat: introduce first version of REST API for subtasks creation
* docs: provide documentation for REST API
* docs: improve REST API documentation
* docs: provide example payload in documentation
* docs: add documentation for authentication

### [24.06.0] - 2024-06-30

**Better import for Quick Subtasks for Jira templates**

* fix: always open documentation in subtasks dialog in new window
* feat: ensure compatibility with Jira 9.15.0, 9.12.5, 9.4.18
* feat: documentation link in dialog footer is always visible
* feat: ensure compatibility with Jira 9.16.0, 9.12.9, 9.4.22
* fix: robustness of template import for non-UTF8 characters
* feat: ensure compatibility with Jira 9.17.0
* refactor: optimize imports for import action

### [24.01.0] - 2024-01-27

**Multiple Subtasks speaks 28 languages**

* feat: ensure compatibility with Jira 9.12.1, 9.4.14
* docs: move changelog for 23.x releases into archive
* feat: add translations into 27 additional languages
* docs: list supported languages in documentation
* fix: fix encoding for French translation
* docs: improve supported languages page
* feat: ensure compatibility with Jira 9.13.0, 9.12.2, 9.4.15
* chore: adjust dependency versions to versions defined by Jira 9.12.1
* chore: use jira-project to define all library versions

### Older versions

Older versions have been moved into separate changelog documents grouped by their major version:

* [Multiple Subtasks for Jira 23.x](/changelog-23x)
* [Multiple Subtasks for Jira 22.x](/changelog-22x)

[Unreleased]: https://github.com/codescape/jira-multiple-subtasks/compare/24.11.0...HEAD
[24.11.0]: https://github.com/codescape/jira-multiple-subtasks/compare/24.08.0...24.11.0
[24.08.0]: https://github.com/codescape/jira-multiple-subtasks/compare/24.06.0...24.08.0
[24.06.0]: https://github.com/codescape/jira-multiple-subtasks/compare/24.01.0...24.06.0
[24.01.0]: https://github.com/codescape/jira-multiple-subtasks/compare/23.12.0...24.01.0
