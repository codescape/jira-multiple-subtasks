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

The import will be performed for all user and project specific subtask templates from `Quick Subtasks for Jira`.

Please note: Not all templates will be fully functional and might need some additional tuning later.
This can have multiple reasons: 
Templates can already be invalid in `Quick Subtasks for Jira` or use advanced features from the plugin that are not available in Multiple Subtasks for Jira.    

The migration can be performed by any user with access to the Jira administration pages in two steps:

1. `Precheck import` - This button allows you to scan for user and project templates in your Jira database.
2. `Start import` - If a minimum of one template is found you can new start the migration.

All results are displayed on this page after any operation was performed.
If the migration fails please consult the logs and [let us know](/support) about it so that we can help you.
