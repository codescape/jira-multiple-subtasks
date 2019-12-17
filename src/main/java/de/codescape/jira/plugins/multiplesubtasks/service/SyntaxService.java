package de.codescape.jira.plugins.multiplesubtasks.service;

import de.codescape.jira.plugins.multiplesubtasks.model.SubTask;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class SyntaxService {

    public List<SubTask> parseString(String input) {
        List<SubTask> subTasks = new ArrayList<>();
        new BufferedReader(new StringReader(input)).lines().forEach(taskInput -> {
            if (taskInput.trim().startsWith("-")) {
                String parsedString = taskInput.replaceFirst("-", "").trim();
                SubTask subTask = new SubTask();
                subTask.setSummary(parsedString);
                subTasks.add(subTask);
            }
        });
        return subTasks;
    }

}
