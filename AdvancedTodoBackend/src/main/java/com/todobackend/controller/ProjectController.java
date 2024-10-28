package com.todobackend.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todobackend.entity.Project;
import com.todobackend.entity.Todo;
import com.todobackend.entity.User;
import com.todobackend.service.CustomUserDetails;
import com.todobackend.service.GistService;
import com.todobackend.service.ProjectService;
import com.todobackend.service.TodoService;
import com.todobackend.service.UserService;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    TodoService todoService;
    
    @Autowired
    GistService gistService;


    @PostMapping("/create")
    public ResponseEntity<Project> createProject(@RequestBody Project project, Authentication authentication) {
    	CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal(); 
    	
    	 User user =userService.findByUsername(userDetails.getUsername()).get();
    	 
        project = projectService.save(project, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project updatedTitle, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal(); 
        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); 
        }

       
        String newTitle = updatedTitle.getTitle(); 

        Optional<Project> updatedProject = projectService.update(id, newTitle, user);

        if (updatedProject.isPresent()) {
            return ResponseEntity.ok(updatedProject.get()); 
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); 
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getprojects")
    public ResponseEntity<List<Project>> getProjects() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            
            User user =userService.findByUsername(userDetails.getUsername()).get() ; 
            
           
            List<Project> projects = projectService.findAllByUser(user);
            return ResponseEntity.ok(projects);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    @PostMapping("todos/{projectId}")
    public ResponseEntity<Todo> addTodo(@PathVariable Long projectId, @RequestBody Todo todo, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }
        
      
      

        return ResponseEntity.ok(todoService.addTodoToProject(projectId, todo));
    }

    @PutMapping("todos/{todoId}")
    public ResponseEntity<Todo> updateTodoStatus(@PathVariable Long todoId, @RequestBody Todo todo, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }
        
      
        return ResponseEntity.ok(todoService.updateTodoStatus(todoId, todo));
    }

    @DeleteMapping("todos/{todoId}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long todoId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }
        todoService.deleteTodoById(todoId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/export/{projectId}")
    public ResponseEntity<String> exportGist(@PathVariable Long projectId,@RequestBody Map<String, Map<String, Map<String, String>>> payload) {
        try {
            Map<String, Map<String, String>> files = payload.get("files");
            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest().body("No files found in the payload.");
            }

            String title = files.keySet().iterator().next();
            String content = files.get(title).get("content");

            if (content == null) {
                return ResponseEntity.badRequest().body("Content is missing for the file: " + title);
            }

           
            String existingGistId = gistService.getGistIdByTitle(title);
            ResponseEntity<String> response;
            
            if (existingGistId != null) {
               
                response = gistService.updateGist(existingGistId, title, content,projectId);
            } else {
               
                response = gistService.createGist(title, content,projectId);
            }
            
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to export Gist: " + e.getMessage());
        }
    }



}
