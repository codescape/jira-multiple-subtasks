---
layout: default
title: Template Import
category: Administration
---

This page describes how to import subtask templates from other plugins to make them available for users and projects in Multiple Subtasks for Jira.

* Table of Contents
{:toc}

### General information

The template import is only available to users with the global roles `Jira Administrators` or `Jira System Administrators` assigned.

To locate the import navigate to the `Administration` menu, then select the entry labelled `Manage apps`.
You will now find a section called `Multiple Subtasks` in the left sidebar.

Navigate to the page `Template Import`.

### Quick Subtasks for Jira

The import will be performed for all user and project subtask templates from `Quick Subtasks for Jira`.

Please note: `Quick Subtask for Jira` supports issue links (syntax: `link: ...`).
This information cannot be migrated and will be lost on the imported templates.

The import can be performed in two steps:
1. `Precheck import` - This button allows you to scan for user and project templates in your Jira database.
2. `Start import` - If a minimum of one template is found you can start the import.

All results are displayed on this page after any operation was performed.
If the import fails please consult the application logs and [let us know](/support) about it so that we can help you.

Please note: The import allows to be restarted without creating duplicate entries.

#### Logging

The import uses minimal logging on the graphical user interface. 
To see more details please consult the application log.
By default, only errors are logged.
To also enable informational logging please follow these steps:

1. Navigate into the `System` tab in your Jira `Administration` 
2. Locate and open the menu entry `Logging and profiling`
3. Scroll down to the `Default Loggers`
4. `Configure` a new logger with the package `de.codescape.jira.plugins.multiplesubtasks` and logging level `INFO`
