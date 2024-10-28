package com.todobackend.servicetest;


import com.todobackend.entity.Project;
import com.todobackend.entity.User;
import com.todobackend.repository.ProjectRepository;
import com.todobackend.service.ProjectService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {
    
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    public ProjectServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void save_ShouldReturnSavedProject() {
        User user = new User();
        Project project = new Project();
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project savedProject = projectService.save(project, user);

        assertNotNull(savedProject);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void update_ShouldReturnUpdatedProject_WhenProjectExistsAndBelongsToUser() {
        User user = new User();
        user.setId(1L);
        Project existingProject = new Project();
        existingProject.setUser(user);
        existingProject.setId(1L);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(existingProject);

        Optional<Project> updatedProject = projectService.update(1L, "New Title", user);

        assertTrue(updatedProject.isPresent());
        assertEquals("New Title", updatedProject.get().getTitle());
        verify(projectRepository, times(1)).save(existingProject);
    }

    @Test
    public void update_ShouldReturnEmpty_WhenProjectDoesNotExist() {
        User user = new User();
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Project> updatedProject = projectService.update(1L, "New Title", user);

        assertFalse(updatedProject.isPresent());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void update_ShouldReturnEmpty_WhenProjectDoesNotBelongToUser() {
        User user1 = new User();
        User user2 = new User();
        user1.setId(1L);
        user2.setId(2L);
        Project existingProject = new Project();
        existingProject.setUser(user1);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));

        Optional<Project> updatedProject = projectService.update(1L, "New Title", user2);

        assertFalse(updatedProject.isPresent());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void delete_ShouldInvokeDeleteById() {
        Long projectId = 1L;
        projectService.delete(projectId);

        verify(projectRepository, times(1)).deleteById(projectId);
    }

    @Test
    public void findAllByUser_ShouldReturnListOfProjects() {
        User user = new User();
        when(projectRepository.findByUser(user)).thenReturn(List.of(new Project()));

        List<Project> projects = projectService.findAllByUser(user);

        assertNotNull(projects);
        assertEquals(1, projects.size());
        verify(projectRepository, times(1)).findByUser(user);
    }
}

