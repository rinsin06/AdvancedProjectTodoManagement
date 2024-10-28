package com.todobackend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.todobackend.entity.Todo;
import com.todobackend.repository.ProjectRepository;
import com.todobackend.repository.TodoRepository;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public Todo addTodoToProject(Long projectId, Todo todo) {
        return projectRepository.findById(projectId).map(project -> {
            todo.setProject(project);
            todo.setCreatedDate(LocalDateTime.now());
            return todoRepository.save(todo);
        }).orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public Todo updateTodoStatus(Long todoId, Todo todo) {
        return todoRepository.findById(todoId)
                .map(existingTodo -> {
                    existingTodo.setCompleted(todo.getCompleted());  
                    existingTodo.setDescription(todo.getDescription());
                    existingTodo.setUpdatedDate(LocalDateTime.now());
                    return todoRepository.save(existingTodo);
                }).orElseThrow(() -> new RuntimeException("Todo not found"));
    }

    public void deleteTodoById(Long todoId) {
        todoRepository.deleteById(todoId);
    }
}
