---
layout: default
title: Workflow Function
category: User Guide
---

This page explains how to use the workflow function to create subtasks provided by Multiple Subtasks for Jira.
A workflow post function can be configured on any workflow transition.
Multiple Subtasks for Jira offers a custom workflow post-function to create subtasks based on a template.

* Table of Contents
{:toc}

### Add new workflow post function

The following steps are required to add a post function to your workflow:

1. Open the `Administration` and navigate to the `Issues` tab. 
2. In the left-hand menu select `Workflows` and `Edit` the workflow you want to change.
3. Select the workflow transition where you want to add subtasks with Multiple Subtasks for Jira.
4. Navigate to the `Post Functions` tab.
5. Select `Add post function` in the table on the bottom.
6. From the list of available post functions choose `Create Subtasks (Multiple Subtasks for Jira)` and confirm with `Add`.
![Add Post Function](/images/workflow-function-add-post-function.png)
7. Provide the subtasks in the already known [Syntax](/syntax) and confirm again with `Add`.
![Add Parameters to Function](/images/workflow-function-add-parameters-to-function.png)
8. `Publish` the workflow to apply the changes you have made. 

Your workflow is ready to create subtasks during the workflow transition you just configured!

### Edit existing workflow post function

The following steps are required to edit an existing post function for your workflow:

1. Follow steps 1 - 4 from above.
2. Look at the list of post functions and use the pencil on the row to create subtasks.
![Edit Post Function](/images/workflow-function-edit-post-function.png)
3. Provide the subtasks in the already known [Syntax](/syntax) and confirm  with `Update`.
![Update Parameters for Function](/images/workflow-function-update-parameters-of-function.png)
4. `Publish` the workflow to apply the changes you have made. 

Your workflow is ready to create subtasks during the workflow transition you just configured!
