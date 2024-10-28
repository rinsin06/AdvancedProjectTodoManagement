package com.todobackend.servicetest;



import com.todobackend.entity.Project;
import com.todobackend.entity.Todo;
import com.todobackend.repository.ProjectRepository;
import com.todobackend.repository.TodoRepository;
import com.todobackend.service.TodoService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TodoService todoService;

    public TodoServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void addTodoToProject_ShouldReturnSavedTodo_WhenProjectExists() {
        Long projectId = 1L;
        Project project = new Project();
        Todo todo = new Todo();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        Todo savedTodo = todoService.addTodoToProject(projectId, todo);

        assertNotNull(savedTodo);
        verify(todoRepository, times(1)).save(todo);
    }

    @Test
    public void addTodoToProject_ShouldThrowException_WhenProjectDoesNotExist() {
        Long projectId = 1L;
        Todo todo = new Todo();
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> todoService.addTodoToProject(projectId, todo));

        assertEquals("Project not found", exception.getMessage());
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    public void updateTodoStatus_ShouldReturnUpdatedTodo_WhenTodoExists() {
        Long todoId = 1L;
        Todo existingTodo = new Todo();
        existingTodo.setCompleted(false);
        existingTodo.setDescription("Old description");
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(existingTodo);

        Todo updatedTodo = new Todo();
        updatedTodo.setCompleted(true);
        updatedTodo.setDescription("New description");
        Todo result = todoService.updateTodoStatus(todoId, updatedTodo);

        assertNotNull(result);
        assertTrue(result.getCompleted());
        assertEquals("New description", result.getDescription());
        verify(todoRepository, times(1)).save(existingTodo);
    }

    @Test
    public void updateTodoStatus_ShouldThrowException_WhenTodoDoesNotExist() {
        Long todoId = 1L;
        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> todoService.updateTodoStatus(todoId, new Todo()));

        assertEquals("Todo not found", exception.getMessage());
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    public void deleteTodoById_ShouldInvokeDeleteById() {
        Long todoId = 1L;
        todoService.deleteTodoById(todoId);

        verify(todoRepository, times(1)).deleteById(todoId);
    }
}

