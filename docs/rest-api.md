---
layout: default
title: REST API
category: User Guide
---

This page explains how to use the REST API to create subtasks provided by Multiple Subtasks for Jira.

Please note:
The REST API requires a minimum version `24.08.0` of Multiple Subtasks for Jira.
Please make sure you have updates Multiple Subtasks for Jira to the latest version for full support of all endpoints.

* Table of Contents
{:toc}

### Authentication

The REST API provided by Multiple Subtasks for Jira requires you to be authenticated as a valid and licensed user of Jira.
Please see the documentation on [Basic Authentication](https://developer.atlassian.com/server/jira/platform/basic-authentication/) or [Personal Access Tokens](https://confluence.atlassian.com/enterprise/using-personal-access-tokens-1026032365.html) for different ways to identify as a valid user.

### Create Subtasks `POST /subtasks/{issueKey}`

    Request URL: /rest/multiple-subtasks/1.0/subtasks/{issueKey}
    Request Method: POST
    Content-Type: text/plain
    Accept: application/json, text/plain

Create the subtasks from the payload for the given `issueKey`.

#### Status Codes

| Code | Status       | Reason and Payload                                                                                        |
|------|--------------|-----------------------------------------------------------------------------------------------------------|
| 200  | Success      | The request was successful and returns a list of created subtasks and all warnings in `application/json`. |
| 400  | Bad Request  | The request failed and returns an error message in `text/plain`.                                          |
| 401  | Unauthorized | The request failed because of missing authentication or authorization.                                    |

#### Example 

This is an example for the payload in the request in order to create two subtasks:

    - Hello World
      fixVersion: unknown
    - Hello Moon
      label: rest-api-demo

The plugin successfully creates two subtasks and responds with the following JSON:

    [
      {
        "issueKey": "YEAH-43",
        "issueSummary": "Hello World",
        "warnings": [
          "Invalid fixVersion: unknown"
        ]
      },
      {
        "issueKey": "YEAH-44",
        "issueSummary": "Hello Moon",
        "warnings": []
      }
    ]

### Missing features?

Do you miss any features on the REST API?
Feel free to [get in contact](/support) with us in order to find a solution!
