---
layout: default
title: Changelog
---

This document explains the syntax to create tasks with Multiple Subtasks for Jira.

* Table of Contents
{:toc}

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

You can also provide a multi-line description using `{n}` to declare a newline.
In this example you will create a subtask with a multi-line description that also has a list in it:

    - demo for multi-line description
      description: This description{n}- comes with a list item{n}- and another list item{n}{n}and a new paragraph

The result will look like this:

    This description
    - comes with a list item
    - and another list item
    and a new paragraph

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

You can also assign the subtask to the current user:

    - This subtask is assigned to the logged in user
      assignee: @current

And you can also inherit the assignee of the parent issue:

    - This subtask is assigned to the assignee of the parent issue
      assignee: @inherit

#### Reporter

By default, the current user will be used as the reporter on newly created subtasks.
If you want to override the report with another user you can do that using their usernames.
In this example you will create a subtask with `stefan` as the reporter:

    - Stefan found a bug
      reporter: stefan

Please note: If the provided reporter cannot be found the current user will be used.

#### Label(s)

By default, newly created subtasks will not have any labels.
If you want to add one or more labels to the subtask you can specify them.
In this example you will create a subtask with the labels `release` and `bugfix` added.

    - A subtask with labels, yeah!
      label: release
      label: bugfix

If you want to inherit the labels used on the parent issue you can specify this.
In this example you will inherit the labels from the parent issue:

    - A subtask with labels, yeah!
      label: @inherit

Please note: You can still add other labels while inheriting labels from the parent issue.

#### Component(s)

By default, newly created subtasks will not have any components.
If you want to add one or more components to the subtask you can specify them.
In this example you will create a subtask with the components `backend` and `frontend` added.

    - A subtask with components
      component: backend
      component: frontend

Please note: If a provided component does not exist it will be ignored.

If you want to inherit the components used on the parent issue you can specify this.
You can also add other components explicitly.
Given an issue with the component `backend` you can use the following statement to create a subtask with the component `backend` and another component `testing` added:

    - A subtask that takes components from the parent issue
      component: @inherit
      component: testing
