import React, { useState, useEffect } from "react";
import {
    Container,
    Typography,
    Button,
    TextField,
    List,
    ListItem,
    Checkbox,
    Box, AppBar,
    Toolbar,
    Snackbar,
    Alert,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    IconButton,
    Stack
} from "@mui/material";
import LogoutIcon from "@mui/icons-material/Logout";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

import {
    fetchProjects,
    createProject as createProjectApi,
    addTodo as addTodoApi,
    updateTodoStatus as updateTodoStatusApi,
    exportGist as exportGistApi,
    updateProjectTitle,
    deleteProject,
    deleteTodo,
} from "../api";
import { useNavigate } from "react-router-dom";
import StyledPaper from "./StyledPaper";

const TodoApp = () => {
    const [projects, setProjects] = useState([]);
    const [selectedProject, setSelectedProject] = useState(null);
    const [newProjectTitle, setNewProjectTitle] = useState("");
    const [newTodoDescription, setNewTodoDescription] = useState("");
    const [gistUrl, setGistUrl] = useState(null);
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState("success");


    const [isUpdateDialogOpen, setUpdateDialogOpen] = useState(false);
    const [isDeleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [projectTitleToUpdate, setProjectTitleToUpdate] = useState("");

    useEffect(() => {
        fetchProjectsData();

        console.log("rendered");

    }, []);

    const [isTodoUpdateDialogOpen, setTodoUpdateDialogOpen] = useState(false);
    const [todoToUpdate, setTodoToUpdate] = useState(null);



    const handleOpenTodoUpdateDialog = (todo) => {
        setTodoToUpdate(todo);
        setTodoUpdateDialogOpen(true);
    };

    const handleCloseTodoUpdateDialog = () => {
        setTodoUpdateDialogOpen(false);
        setTodoToUpdate(null);
    };

    const handleUpdateTodo = async () => {
        if (!todoToUpdate || !todoToUpdate.description) {
            handleError("Please provide a todo description");
            return;
        }

        try {
            const response = await updateTodoStatusApi(todoToUpdate.id, todoToUpdate);
            setSelectedProject({
                ...selectedProject,
                todos: selectedProject.todos.map((t) => (t.id === response.data.id ? response.data : t)),
            });
            handleCloseTodoUpdateDialog();
            handleSuccess("Todo updated successfully!");
        } catch (error) {
            handleError("Failed to update todo");
        }
    };

    const handleDeleteTodo = async (todoId) => {
        try {
            await deleteTodo(todoId);
            setSelectedProject(prev => ({
                ...prev,
                todos: prev.todos.filter((t) => t.id !== todoId),
            }));
            handleSuccess("Todo deleted successfully!");
        } catch (error) {
            handleError("Failed to delete todo");
        }
    };

    const fetchProjectsData = async () => {
        try {
            const response = await fetchProjects();
            setProjects(response.data);
        } catch (error) {
            handleError("Failed to fetch projects");
        }
    };

    const createProject = async () => {
        if (!newProjectTitle.trim()) {
            setSnackbarMessage("Project title cannot be empty.");
            setSnackbarSeverity("error");
            setSnackbarOpen(true);
            return;
        }
        try {
            const response = await createProjectApi(newProjectTitle);
            setProjects([...projects, response.data]);
            setNewProjectTitle("");
            handleSuccess("Project created successfully!");
        } catch (error) {
            handleError("Failed to create project");
        }
    };

    const addTodo = async () => {
        if (!selectedProject) {
            handleError("Please select a project first");
            return;
        }

        if (!newTodoDescription) {
            handleError("Todo description cannot be empty");
            return;
        }

        try {
            const response = await addTodoApi(selectedProject.id, newTodoDescription);
            setSelectedProject((prev) => ({
                ...prev,
                todos: [...prev.todos, response.data],
            }));
            setNewTodoDescription("");
            handleSuccess("Todo added successfully!");
        } catch (error) {
            handleError("Failed to add todo");
        }
    };


    const updateTodoStatus = async (todo) => {
        try {
            const response = await updateTodoStatusApi(todo.id, { ...todo, completed: !todo.completed });
            setSelectedProject({
                ...selectedProject,
                todos: selectedProject.todos.map((t) => (t.id === todo.id ? response.data : t)),
            });
            handleSuccess("Todo status updated successfully!");
        } catch (error) {
            handleError("Failed to update todo status");
        }
    };

    const exportGist = async () => {
        if (!selectedProject) {
            handleError("Please select a project first");
            return;
        }

        const completedTodos = selectedProject.todos.filter(todo => todo.completed);
        const pendingTodos = selectedProject.todos.filter(todo => !todo.completed);

        const markdownContent = `\
# ${selectedProject.title}

Summary: ${completedTodos.length} / ${selectedProject.todos.length} completed.

## Section 1: Pending Todos
${pendingTodos.map(todo => `- [ ] ${todo.description}`).join('\n')}

## Section 2: Completed Todos
${completedTodos.map(todo => `- [x] ${todo.description}`).join('\n')}`;

        try {
            const response = await exportGistApi(selectedProject.id,selectedProject.title, markdownContent);
            setSelectedProject({
                ...selectedProject,
                gistUrl: response.data
            });
            console.log(response);
            
            
            handleSuccess("Gist exported successfully!");
        } catch (error) {
            handleError("Failed to export gist");
        }
    };

    const handleSuccess = (message) => {
        setSnackbarMessage(message);
        setSnackbarSeverity("success");
        setSnackbarOpen(true);
    };

    const handleError = (message) => {
        setSnackbarMessage(message);
        setSnackbarSeverity("error");
        setSnackbarOpen(true);
    };

    const handleSnackbarClose = () => {
        setSnackbarOpen(false);
    };

    const handleOpenUpdateDialog = () => {
        if (selectedProject) {
            setProjectTitleToUpdate(selectedProject.title);
            setUpdateDialogOpen(true);
        }
    };

    const handleCloseUpdateDialog = () => {
        setUpdateDialogOpen(false);
    };

    const handleUpdateProject = async () => {
        if (!selectedProject || !projectTitleToUpdate) {
            handleError("Please provide a new title");
            return;
        }

        try {
            const response = await updateProjectTitle(selectedProject.id, projectTitleToUpdate);
            const updatedProjects = projects.map((project) =>
                project.id === selectedProject.id ? response.data : project
            );
            setProjects(updatedProjects);
            setSelectedProject({ ...selectedProject, title: projectTitleToUpdate });
            setProjectTitleToUpdate("");
            handleCloseUpdateDialog();
            handleSuccess("Project updated successfully!");
        } catch (error) {
            handleError("Failed to update project title");
        }
    };

    const handleOpenDeleteDialog = () => {
        setDeleteDialogOpen(true);
    };

    const handleCloseDeleteDialog = () => {
        setDeleteDialogOpen(false);
    };

    const handleDeleteProject = async () => {
        if (!selectedProject) {
            handleError("Please select a project to delete");
            return;
        }

        try {
            await deleteProject(selectedProject.id);
            setProjects(projects.filter((project) => project.id !== selectedProject.id));
            setSelectedProject(null);
            handleCloseDeleteDialog();
            handleSuccess("Project deleted successfully!");
        } catch (error) {
            handleError("Failed to delete project");
        }
    };

    const calculateSummary = (todos) => {
        const total = todos.length;
        const completed = todos.filter((todo) => todo.completed).length;
        const pending = total - completed;
        return { total, completed, pending };
    };

    const navigate = useNavigate();


    const handleSignOut = () => {
        localStorage.setItem("authenticated", "false");
        navigate("/login");
    };

    const handleTitleChange = (event) => {
        setNewProjectTitle(event.target.value);
    };



    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            <AppBar
                position="static"
                sx={{
                    background: 'linear-gradient(60deg, #1e88e5 30%, #42a5f5 90%)',
                    boxShadow: '0px 4px 12px rgba(0, 0, 0, 0.3)',
                    borderRadius: '0 0 10px 10px',
                }}
            >
                <Toolbar>
                    <Typography
                        sx={{
                            flexGrow: 1,
                            fontWeight: 'bold',
                            fontFamily: '"Poppins", sans-serif',
                            letterSpacing: '1px',
                            color: '#FFFFFF',
                        }}
                        variant="h6"
                    >
                        Todo Management App
                    </Typography>
                    <Button
                        color="inherit"
                        onClick={handleSignOut}
                        startIcon={<LogoutIcon />}
                        sx={{
                            marginLeft: 2,
                            padding: '6px 16px',
                            borderRadius: '20px',
                            textTransform: 'capitalize',
                            fontWeight: '500',
                            backgroundColor: 'rgba(255, 255, 255, 0.1)',
                            '&:hover': { backgroundColor: 'rgba(255, 255, 255, 0.2)' },
                            transition: 'background-color 0.3s ease',
                        }}
                    >
                        Sign Out
                    </Button>
                </Toolbar>
            </AppBar>

            <StyledPaper elevation={3}>
                <Typography sx={{
                    fontFamily: '"Comic Sans MS", cursive, sans-serif',
                    fontSize: '2rem',
                    color: '#4a90e2',
                    textShadow: '2px 2px #f39c12',
                    padding: '10px',
                    backgroundColor: 'rgba(250, 250, 250, 0.8)',
                    borderRadius: '8px',
                    border: '2px solid #f39c12',
                    display: 'inline-block',
                    marginBottom: '20px',
                    animation: 'pulse 2s infinite',
                }} variant="h4" gutterBottom>
                    Manage Your Projects
                </Typography>

                <TextField
                    label="New Project"
                    value={newProjectTitle}
                    onChange={handleTitleChange}
                    fullWidth
                    margin="normal"
                    variant="outlined"
                   

                />
                <Button variant="contained" color="primary" onClick={(e) => {
                    e.preventDefault();
                    createProject();
                }}>
                    Create Project
                </Button>

                <List sx={{ marginTop: 2 }}>
                    {projects.map((project) => (
                        <ListItem key={project.id} button onClick={() => setSelectedProject(project)}>
                            <Typography variant="h6" sx={{ flexGrow: 1, fontFamily: 'Poppins, sans-serif' }}>{project.title}</Typography>
                            <IconButton onClick={handleOpenUpdateDialog}>
                                <EditIcon />
                            </IconButton>
                            <IconButton onClick={handleOpenDeleteDialog}>
                                <DeleteIcon />
                            </IconButton>
                        </ListItem>
                    ))}
                </List>

                {selectedProject && (
                    <>
                        <Typography sx={{ fontFamily: 'Poppins, sans-serif' }} variant="h5" gutterBottom>
                            {selectedProject.title}
                        </Typography>

                        <Box mb={2}>
                            <Typography variant="body1">
                                <strong>Project Summary:</strong> {calculateSummary(selectedProject.todos).completed} / {calculateSummary(selectedProject.todos).total} todos completed
                            </Typography>
                            <Typography variant="body1">
                                Pending: {calculateSummary(selectedProject.todos).pending}, Completed: {calculateSummary(selectedProject.todos).completed}
                            </Typography>
                        </Box>

                        <TextField
                            label="New Todo"
                            value={newTodoDescription}
                            onChange={(e) => { setNewTodoDescription(e.target.value) }}
                            fullWidth
                            margin="normal"

                        />
                        <Button variant="contained" color="primary" onClick={addTodo}>
                            Add Todo
                        </Button>

                        <List sx={{ marginTop: 2 }}>
                            {selectedProject.todos.map((todo) => (
                                <ListItem key={todo.id}>
                                    <Stack direction="row" spacing={1} alignItems="center" sx={{ flexGrow: 1 }}>
                                        <Checkbox
                                            checked={todo.completed}
                                            onChange={() => updateTodoStatus(todo)}
                                        />
                                        <Box>

                                            <Typography variant="body1" sx={{ textDecoration: todo.completed ? "line-through" : "none" }}>
                                                {todo.description}
                                            </Typography>
                                            <Stack direction="column" spacing={0.5} sx={{ marginTop: 1 }}>
                                                <Typography variant="caption" color="textSecondary">
                                                    Created: {new Date(todo.createdDate).toLocaleDateString()}
                                                </Typography>
                                                <Typography variant="caption" color="textSecondary">
                                                    Updated: {todo.updatedDate ? new Date(todo.updatedDate).toLocaleDateString() : "-------"}
                                                </Typography>
                                            </Stack>
                                        </Box>
                                    </Stack>
                                    <IconButton onClick={() => handleOpenTodoUpdateDialog(todo)}>
                                        <EditIcon />
                                    </IconButton>
                                    <IconButton onClick={() => handleDeleteTodo(todo.id)}>
                                        <DeleteIcon />
                                    </IconButton>
                                </ListItem>
                            ))}
                        </List>

                        <Button variant="contained" color="secondary" onClick={exportGist} sx={{ mt: 2 }}>
                            Export to Gist
                        </Button>

                        {selectedProject.gistUrl && (
                            <Typography variant="body1" mt={2}>
                                Gist URL: <a href={selectedProject.gistUrl} target="_blank" rel="noopener noreferrer">{selectedProject.gistUrl}</a>
                            </Typography>
                        )}
                    </>
                )}


                <Snackbar open={snackbarOpen} autoHideDuration={6000} onClose={handleSnackbarClose}>
                    <Alert onClose={handleSnackbarClose} severity={snackbarSeverity}>
                        {snackbarMessage}
                    </Alert>
                </Snackbar>


                <Dialog open={isUpdateDialogOpen} onClose={handleCloseUpdateDialog}>
                    <DialogTitle>Update Project Title</DialogTitle>
                    <DialogContent>
                        <TextField
                            label="Project Title"
                            value={projectTitleToUpdate}
                            onChange={(e) => setProjectTitleToUpdate(e.target.value)}
                            fullWidth
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleCloseUpdateDialog}>Cancel</Button>
                        <Button onClick={handleUpdateProject}>Update</Button>
                    </DialogActions>
                </Dialog>


                <Dialog open={isDeleteDialogOpen} onClose={handleCloseDeleteDialog}>
                    <DialogTitle>Confirm Delete</DialogTitle>
                    <DialogContent>
                        <Typography>Are you sure you want to delete this project?</Typography>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleCloseDeleteDialog}>Cancel</Button>
                        <Button onClick={handleDeleteProject} color="error">Delete</Button>
                    </DialogActions>
                </Dialog>

                <Dialog open={isTodoUpdateDialogOpen} onClose={handleCloseTodoUpdateDialog}>
                    <DialogTitle>Update Todo</DialogTitle>
                    <DialogContent>
                        <TextField
                            label="Todo Description"
                            value={todoToUpdate ? todoToUpdate.description : ""}
                            onChange={(e) => setTodoToUpdate({ ...todoToUpdate, description: e.target.value })}
                            fullWidth
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleCloseTodoUpdateDialog}>Cancel</Button>
                        <Button onClick={handleUpdateTodo}>Update</Button>
                    </DialogActions>
                </Dialog>
            </StyledPaper>
        </Container>
    );
};

export default TodoApp;
