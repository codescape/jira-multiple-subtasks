---
layout: default
title: Changelog
---

This document explains the syntax to create tasks with Multiple Subtasks for Jira.

## Simple Syntax

To create three simple subtasks just have three lines starting with the `-` followed by the summary of each subtask:

    - This is my first subtask!
    - This is another subtask...
    - And here is another one!

## Advanced Syntax

In addition to the simple syntax you can specify additional attributes for each subtask - one line per attribute.

#### Description

By default, newly created subtasks will not have a description. 
You can set the subtask's description.
In this example you will create a subtask called `This subtask has a description!` with the description `This is the description to be added to the subtask.`: 

    - This subtask has a description!
      description: This is the description to be added to the subtask. 

#### Priority

By default, the priority of the parent issue will be used for the new subtask.
If you want you can specify another priority. 
In this example the subtask will be created with priority `Highest`:

    - Emergency task!
      priority: Highest

Please note: If the provided priority does not exist for this issue the priority of the parent issue will be used.

#### Issue Type

In many projects there is exactly one issue type for subtasks.
If you have a project with multiple types of subtasks you can explicitly configure the issue type for each subtask.
In this example you will create two subtask - one with the issue type `Documentation` and one with the issue type `Implementation`:

    - Fix the bug first
      issueType: Implementation
    - Write a wonderful documentation
      issueType: Documentation

#### Assignee

By default, newly created subtasks will not be assigned to a user.
If you want to assign subtasks to a user you can do that using their usernames.
In this example you will create a subtask assigned to user `bob`:

    - Bob should do that subtask
      assignee: bob

Please note: If the provided assignee cannot be found no assignee will be used.

#### Reporter

By default, the current user will be used as the reporter on newly created subtasks.
If you want to override the report with another user you can do that using their usernames.
In this example you will create a subtask with `stefan` as the reporter:

    - Stefan found a bug
      reporter: stefan

Please note: If the provided reporter cannot be found the current user will be used.

### Label(s)

By default, newly created subtasks will not have any labels.
If you want to add one or more labels to the subtask you specify them.
In this example you will create a subtask with the labels `release` and `bugfix` added.

    - A subtask with labels, yeah!
      label: release
      label: bugfix
