package com.todobackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.todobackend.entity.Project;
import com.todobackend.entity.User;

import jakarta.transaction.Transactional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUserId(Long userId); 

	List<Project> findByUser(User user);
	
	@Transactional
    @Modifying
    @Query("UPDATE Project p SET p.gistUrl = ?2 WHERE p.id = ?1")
    void updateGistUrl(Long projectId, String gistUrl);
}
