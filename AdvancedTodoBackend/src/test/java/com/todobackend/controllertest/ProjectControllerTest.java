package com.todobackend.controllertest;



import com.todobackend.controller.ProjectController;
import com.todobackend.entity.Project;
import com.todobackend.entity.Todo;
import com.todobackend.entity.User;
import com.todobackend.service.CustomUserDetails;
import com.todobackend.service.GistService;
import com.todobackend.service.ProjectService;
import com.todobackend.service.TodoService;
import com.todobackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ProjectControllerTest {

    @InjectMocks
    private ProjectController projectController;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    @Mock
    private TodoService todoService;

    @Mock
    private GistService gistService;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails userDetails;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContext securityContext = org.mockito.Mockito.mock(SecurityContext.class);
        
       
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true); 
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
    @Test
    public void createProject_ShouldReturnCreatedProject() {
        Project project = new Project();
        project.setTitle("Test Project");
        User user = new User();
        user.setUsername("testuser");

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(projectService.save(any(Project.class), eq(user))).thenReturn(project);

        ResponseEntity<Project> response = projectController.createProject(project, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(project, response.getBody());
    }

    @Test
    public void updateProject_ShouldReturnUpdatedProject() {
        Project updatedProject = new Project();
        updatedProject.setTitle("Updated Project");
        User user = new User();
        user.setUsername("testuser");

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(projectService.update(anyLong(), anyString(), eq(user))).thenReturn(Optional.of(updatedProject));

        ResponseEntity<Project> response = projectController.updateProject(1L, updatedProject, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedProject, response.getBody());
    }

    @Test
    public void updateProject_UserNotFound_ShouldReturnForbidden() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(Optional.empty());

        ResponseEntity<Project> response = projectController.updateProject(1L, new Project(), authentication);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void deleteProject_ShouldReturnNoContent() {
        doNothing().when(projectService).delete(anyLong());

        ResponseEntity<Void> response = projectController.deleteProject(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void getProjects_ShouldReturnProjectList() {
        // Mock data
        User user = new User();
        user.setUsername("testuser");
        
        List<Project> projects = new ArrayList<>();
        projects.add(new Project());

        
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(projectService.findAllByUser(user)).thenReturn(projects);

        
        ResponseEntity<List<Project>> response = projectController.getProjects();

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projects, response.getBody());
    }


    @Test
    public void getProjects_UserNotAuthenticated_ShouldReturnUnauthorized() {
        when(authentication.isAuthenticated()).thenReturn(false);
        ResponseEntity<List<Project>> response = projectController.getProjects();
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void addTodo_ShouldReturnTodo() {
        Todo todo = new Todo();
        when(authentication.isAuthenticated()).thenReturn(true);
        when(todoService.addTodoToProject(anyLong(), any(Todo.class))).thenReturn(todo);

        ResponseEntity<Todo> response = projectController.addTodo(1L, todo, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(todo, response.getBody());
    }

    @Test
    public void updateTodoStatus_ShouldReturnUpdatedTodo() {
        Todo updatedTodo = new Todo();
        when(authentication.isAuthenticated()).thenReturn(true);
        when(todoService.updateTodoStatus(anyLong(), any(Todo.class))).thenReturn(updatedTodo);

        ResponseEntity<Todo> response = projectController.updateTodoStatus(1L, updatedTodo, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedTodo, response.getBody());
    }

    @Test
    public void deleteTodo_ShouldReturnNoContent() {
        when(authentication.isAuthenticated()).thenReturn(true);
        doNothing().when(todoService).deleteTodoById(anyLong());

        ResponseEntity<Void> response = projectController.deleteTodo(1L, authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void exportGist_ShouldReturnGistExported() {
        Map<String, Map<String, String>> files = new HashMap<>();
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("content", "Gist content");
        files.put("Gist Title", contentMap);

        Map<String, Map<String, Map<String, String>>> payload = new HashMap<>();
        payload.put("files", files);

        when(gistService.getGistIdByTitle("Gist Title")).thenReturn(null);
        when(gistService.createGist(anyString(), anyString(), anyLong())).thenReturn(ResponseEntity.ok("Gist created"));

        ResponseEntity<String> response = projectController.exportGist(1L, payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Gist created", response.getBody());
    }

    @Test
    public void exportGist_NoFiles_ShouldReturnBadRequest() {
        Map<String, Map<String, Map<String, String>>> payload = new HashMap<>();
        payload.put("files", new HashMap<>());

        ResponseEntity<String> response = projectController.exportGist(1L, payload);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No files found in the payload.", response.getBody());
    }

    @Test
    public void exportGist_ContentMissing_ShouldReturnBadRequest() {
        Map<String, Map<String, String>> files = new HashMap<>();
        files.put("Gist Title", new HashMap<>());

        Map<String, Map<String, Map<String, String>>> payload = new HashMap<>();
        payload.put("files", files);

        ResponseEntity<String> response = projectController.exportGist(1L, payload);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Content is missing for the file: Gist Title", response.getBody());
    }

    @Test
    public void exportGist_ExceptionThrown_ShouldReturnInternalServerError() {
        Map<String, Map<String, String>> files = new HashMap<>();
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("content", "Gist content");
        files.put("Gist Title", contentMap);

        Map<String, Map<String, Map<String, String>>> payload = new HashMap<>();
        payload.put("files", files);

        when(gistService.getGistIdByTitle("Gist Title")).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<String> response = projectController.exportGist(1L, payload);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to export Gist: Service error", response.getBody());
    }
}

