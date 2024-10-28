package com.todobackend.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todobackend.repository.ProjectRepository;

import jakarta.annotation.PostConstruct;

@Service
public class GistService {

    @Value("${github.token}")
    private String githubToken;

    private RestTemplate restTemplate;
    
    @Autowired
    ProjectRepository projectRepository;

    @PostConstruct
    public void init() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    public String getGistIdByTitle(String title) {
        String url = "https://api.github.com/gists";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<Map[]> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            for (Map gist : response.getBody()) {
                String description = (String) gist.get("description");
                if (title.equals(description)) {
                    return (String) gist.get("id");
                }
            }
        }
        return null;
    }

    public ResponseEntity<String> createGist(String title, String content,Long projectId) {
        String url = "https://api.github.com/gists";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(githubToken);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("description", title);
        requestBody.put("public", true);

        Map<String, Object> files = new HashMap<>();
        Map<String, String> fileContent = new HashMap<>();
        fileContent.put("content", content); 
        files.put(title + ".md", fileContent);

        requestBody.put("files", files);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response=restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        
        
     
        if (response.getStatusCode() == HttpStatus.CREATED) {
            try {
               
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> responseBody = mapper.readValue(response.getBody(), Map.class);
                String gistUrl = (String) responseBody.get("html_url");
                
                updateProjectGistUrl(projectId, gistUrl); 
                
                return new ResponseEntity<>(gistUrl, HttpStatus.CREATED);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>("Failed to parse gist URL", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>("Failed to create gist", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<String> updateGist(String gistId, String title, String content,Long projectId) {
        String url = "https://api.github.com/gists/" + gistId;
        
        updateProjectGistUrl(projectId, url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(githubToken);

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> files = new HashMap<>();
        Map<String, String> fileContent = new HashMap<>();
        
        fileContent.put("content", content);
        files.put(title + ".md", fileContent);

        requestBody.put("files", files);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
              
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> responseBody = mapper.readValue(response.getBody(), Map.class);
                String gistUrl = (String) responseBody.get("html_url");

                updateProjectGistUrl(projectId, gistUrl); 
                
                return new ResponseEntity<>(gistUrl, HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>("Failed to parse gist URL", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>("Failed to update gist", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private void updateProjectGistUrl(Long projectId, String gistUrl) {
       
        projectRepository.updateGistUrl(projectId, gistUrl);
    }
}
