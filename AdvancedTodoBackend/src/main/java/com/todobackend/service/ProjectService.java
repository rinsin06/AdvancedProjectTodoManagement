package com.todobackend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.todobackend.entity.Project;
import com.todobackend.entity.User;
import com.todobackend.repository.ProjectRepository;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

  
    public Project save(Project project, User user) {
        project.setUser(user); 
        return projectRepository.save(project);
    }

    public Optional<Project> update(Long projectId, String newTitle, User user) {
      
        Optional<Project> existingProjectOptional = projectRepository.findById(projectId);

       
        if (existingProjectOptional.isPresent()) {
            Project existingProject = existingProjectOptional.get();

           
             if (!existingProject.getUser().equals(user)) {
                 return Optional.empty(); 
             }

           
            existingProject.setTitle(newTitle);

           
            Project updatedProject = projectRepository.save(existingProject);
            return Optional.of(updatedProject);
        }

        
        return Optional.empty();
    }


    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    public List<Project> findAllByUser(User user) {
        return projectRepository.findByUser(user); 
    }
}
