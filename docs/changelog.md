---
layout: default
title: Changelog
category: Administration
---

{% include changelog-preface.md %}

* Table of Contents
{:toc}

### [Unreleased]

* fix: always open documentation in subtasks dialog in new window
* feat: ensure compatibility with Jira 9.15.0, 9.12.5, 9.4.18
* fix: robustness of template import for non-UTF8 characters 

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

[Unreleased]: https://github.com/codescape/jira-multiple-subtasks/compare/24.01.0...HEAD
[24.01.0]: https://github.com/codescape/jira-multiple-subtasks/compare/23.12.0...24.01.0
