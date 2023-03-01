---
layout: default
title: Syntax for subtask creation
category: User Guide
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

The multi-line functionality can be also used for tables in combination with the Jira markup for tables: 

    - Create a subtask with a table
      description: || first heading || second heading ||{n}| first content | second content |

This will render a table like this in the description of your subtask:

| first heading | second heading |
|---------------|----------------|
| first content | second content |

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

#### Estimate

By default, newly created subtasks will not have an estimate.
If you want to provide an estimate for the subtask you can do that with a simple syntax.
The estimate can be provided in weeks (`w`), days (`d`), hours (`h`), minutes (`m`) and a combination of all of them:

    - This subtask takes one hour and 15 minutes
      estimate: 1h 15m
    - This is a take for one week
      estimate: 1w

Please note: The order of the estimate attributes is fixed. A value of `2d 1w` will not be accepted while a value of `1w 2d` is valid and will be accepted.

#### Affected Version(s)

By default, newly created subtasks will not have any affected versions.
If you want to provide affected versions you can do that by providing the version names.
This example will create a subtask with two affected versions `1.0` and `2.0`:

    - This bug affects two versions
      affectedVersion: 1.0
      affectedVersion: 2.0

Please note: If a provided affected version does not exist it will be ignored.

You can also inherit the affected version(s) from the parent issue.
This example will create a subtask with the affected version(s) that are already set on the parent issue:

    - This task inherits the affected version
      affectedVersion: @inherit

#### Fix Version(s)

By default, newly created subtasks will not have any fix versions.
If you want to provide fix versions you can do that by providing the version names.
This example will create a subtask with one fix version `2.1`:

    - This bug will be solved by the next major version
      fixVersion: 2.1

Please note: If a provided fix version does not exist it will be ignored.

You can also inherit the fix version(s) from the parent issue.
This example will create a subtask with the fix version(s) that are already set on the parent issue:

    - This task inherits the fix version
      fixVersion: @inherit

#### Due Date

By default, newly created subtasks will not have a due date.
If you want to define a due date on your subtasks you can do that in two ways - either inherit the due date of the parent issue or define an absolute date.

    - Task with the due date from the parent issue
      dueDate: @inherit
    - Task with an absolute due date
      dueDate: 2023-02-11

Please note:
The required format for absolute dates is `yyyy-mm-dd`.
For single-digit months and days, you can omit the leading zero.

#### Watcher(s)

By default, newly created subtasks will not have any watchers.
If you want users to watch the subtask you can do that using their usernames.
In this example you will create a subtask and make the users with usernames `bob` and `ron` watch it:

    - Bob and Ron should know this subtask
      watcher: bob
      watcher: ron

Please note: If the provided username cannot be found it will be ignored.

You can also inherit the watcher(s) from the parent issue.
This example will create a subtask with the watchers(s) that are already set on the parent issue:

    - This task inherits the watchers
      watcher: @inherit

Alternatively you can explicitly add yourself (the current user) as a watcher of the new subtask:

    - I am watching this issue
      watcher: @current

Please note: In the user profile of every Jira user they can choose whether they automatically watch newly created or commented issues.
So in most cases it is not necessary to add the current user as a watcher.

#### Custom Field(s)

Custom fields can be set in the same way other fields are provided with values.
The custom field must be referenced by its ID which has the format `customfield_xxxxx` where `xxxxx` is a 5-digit number.
Atlassian provides a documentation that helps to find out the custom field's IDs in their Jira knowledge base:
[How to find any custom field's IDs](https://confluence.atlassian.com/jirakb/how-to-find-any-custom-field-s-ids-744522503.html)

Here is an example with a subtask called `This subtask has custom fields` that applies the numeric value `42` to the first custom field, chooses the option `female` in the second custom field and applies the multi-line text to the third custom field:

    - This subtask has custom fields
      customfield_10001: 42
      customfield_10099: female
      customfield_10007: this is{n}a multi-line{n}text

Since custom fields can have many data types not all custom field types are yet supported.
The following custom field types can be used:

* Number field
* Text field (single-line)
* Text field (multi-line)
* Radio Buttons
* Select List (single-select)
* Select List (multi-select)

If you need support for any other custom field type please [let us know](/support) about it.
