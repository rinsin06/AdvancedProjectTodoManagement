import axios from 'axios';

const API_BASE_URL = "http://localhost:8080/api";

const axiosInstance = axios.create({
    baseURL: API_BASE_URL,
    withCredentials: true,
});


export const fetchProjects = async () => {
    return axiosInstance.get("/projects/getprojects");
};


export const createProject = async (title) => {
    return axiosInstance.post("/projects/create", { title });
};


export const addTodo = async (projectId, description) => {
    return axiosInstance.post(`/projects/todos/${projectId}`, { description });
};


export const updateTodoStatus = async (todoId, updatedTodo) => {
    return axiosInstance.put(`/projects/todos/${todoId}`, updatedTodo);
};

export const deleteTodo = async (todoId) => {
    return axiosInstance.delete(`projects/todos/${todoId}`); 
};


export const exportGist = async (projectId ,title, content) => {
    return axiosInstance.post(`/projects/export/${projectId}`, {
        files: {
            [title]: {
                content,
            },
        },
    });
};


export const updateProjectTitle = async (projectId, newTitle) => {
    return axiosInstance.put(`/projects/${projectId}/update`, { title: newTitle });
};

export const deleteProject = async (projectId) => {
    return axiosInstance.delete(`/projects/${projectId}`);
};

