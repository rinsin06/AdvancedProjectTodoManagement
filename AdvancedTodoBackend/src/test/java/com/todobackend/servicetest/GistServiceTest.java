package com.todobackend.servicetest;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.todobackend.entity.Project;
import com.todobackend.repository.ProjectRepository;
import com.todobackend.service.GistService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class GistServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private GistService gistService;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);

        // Set the private githubToken field via reflection
        Field githubTokenField = GistService.class.getDeclaredField("githubToken");
        githubTokenField.setAccessible(true);
        githubTokenField.set(gistService, "dummy-token");
    }
    @Test
    public void getGistIdByTitle_ShouldReturnGistId_WhenTitleMatches() {
        String title = "Test Gist";
        String gistId = "12345";
        Map<String, Object> gist = new HashMap<>();
        gist.put("description", title);
        gist.put("id", gistId);
        ResponseEntity<Map[]> responseEntity = new ResponseEntity<>(new Map[]{gist}, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map[].class)))
                .thenReturn(responseEntity);

        String result = gistService.getGistIdByTitle(title);

        assertEquals(gistId, result);
    }

    @Test
    public void getGistIdByTitle_ShouldReturnNull_WhenNoMatchFound() {
        String title = "Test Gist";
        ResponseEntity<Map[]> responseEntity = new ResponseEntity<>(new Map[]{}, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map[].class)))
                .thenReturn(responseEntity);

        String result = gistService.getGistIdByTitle(title);

        assertNull(result);
    }

    @Test
    public void createGist_ShouldReturnGistUrl_WhenCreatedSuccessfully() throws Exception {
        String title = "Test Gist";
        String content = "Content of the gist";
        Long projectId = 1L;
        String gistUrl = "https://gist.github.com/12345";

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("html_url", gistUrl);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(new ObjectMapper().writeValueAsString(responseBody), HttpStatus.CREATED);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        doNothing().when(projectRepository).updateGistUrl(eq(projectId), eq(gistUrl));

        ResponseEntity<String> result = gistService.createGist(title, content, projectId);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(gistUrl, result.getBody());
        verify(projectRepository, times(1)).updateGistUrl(projectId, gistUrl);
    }


    @Test
    public void createGist_ShouldReturnError_WhenFailedToCreate() {
        String title = "Test Gist";
        String content = "Content of the gist";
        Long projectId = 1L;
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Failed to create gist", HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        ResponseEntity<String> result = gistService.createGist(title, content, projectId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Failed to create gist", result.getBody());
        verify(projectRepository, never()).updateGistUrl(eq(projectId), anyString());
    }

    @Test
    public void updateGist_ShouldReturnGistUrl_WhenUpdatedSuccessfully() throws Exception {
        String gistId = "12345";
        String title = "Updated Gist";
        String content = "Updated content";
        Long projectId = 1L;
        String gistUrl = "https://gist.github.com/12345";

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("html_url", gistUrl);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(new ObjectMapper().writeValueAsString(responseBody), HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        
        doNothing().when(projectRepository).updateGistUrl(eq(projectId), eq(gistUrl));

        ResponseEntity<String> result = gistService.updateGist(gistId, title, content, projectId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(gistUrl, result.getBody());
        verify(projectRepository, times(1)).updateGistUrl(projectId, gistUrl);
    }


  

}
