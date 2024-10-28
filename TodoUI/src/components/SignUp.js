import React, { useState } from "react";
import { TextField, Button, Container, Typography, Paper, Snackbar, Alert } from "@mui/material";
import { styled } from "@mui/material/styles";
import axios from "axios";
import { useNavigate } from "react-router-dom";


const StyledPaper = styled(Paper)(({ theme }) => ({
    padding: theme.spacing(4),
    marginTop: theme.spacing(8),
    textAlign: 'center',
    borderRadius: '8px',
    boxShadow: theme.shadows[5],
    backgroundImage: 'url(https://images.unsplash.com/photo-1629968417850-3505f5180761?q=80&w=1952&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D)', // Set your desired background image
    backgroundSize: 'cover',
    backgroundPosition: 'center',
}));

const Signup = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");

    const navigate = useNavigate();

    const handleSignup = async () => {
        if (!username || !password) {
            setSnackbarMessage("SignUp failed. Please fill in both username and password.");
            setSnackbarOpen(true);
            return;
        }
        try {
            const response = await axios.post("http://localhost:8080/api/auth/signup", {
                username: username,
                password: password,
                role: "ROLE_USER"
            });
            console.log(response.data);
            setSnackbarMessage("Signup successful! Please log in.");
            setSnackbarOpen(true);

            setUsername("");
            setPassword("");
        } catch (error) {
            console.error("Signup failed");
            setSnackbarMessage("Signup failed. Please try again.");
            setSnackbarOpen(true);
        }
    };

    const handleSnackbarClose = () => {
        setSnackbarOpen(false);
    };

    const handleLogin = () => {
        navigate("/login");
    };

    return (
        <Container maxWidth="sm">
            <StyledPaper>
                <Typography variant="h4" sx={{
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
                }} >
                    Signup
                </Typography>
                <TextField
                    label="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    fullWidth
                    margin="normal"
                    variant="outlined"
                    required
                />
                <TextField
                    label="Password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    fullWidth
                    margin="normal"
                    variant="outlined"
                    required
                />
                <Button
                    variant="contained"
                    color="primary"

                    onClick={handleSignup}
                    fullWidth
                    sx={{ marginTop: 2 }}
                >
                    Signup
                </Button>

                <Button
                    variant="outlined"
                    color="primary"
                    onClick={handleLogin}
                    fullWidth
                    sx={{ marginTop: 2 }}
                >
                    Login
                </Button>
            </StyledPaper>

            <Snackbar
                open={snackbarOpen}
                autoHideDuration={6000}
                onClose={handleSnackbarClose}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
            >
                <Alert onClose={handleSnackbarClose} severity={snackbarMessage.includes("failed") ? "error" : "success"} sx={{ width: '100%' }}>
                    {snackbarMessage}
                </Alert>
            </Snackbar>
        </Container>
    );
};

export default Signup;

