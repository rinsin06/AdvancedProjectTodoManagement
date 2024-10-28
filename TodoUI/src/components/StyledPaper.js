import { Paper, styled } from "@mui/material";



const StyledPaper = styled(Paper)(({ theme }) => ({
    padding: theme.spacing(4),
    marginTop: theme.spacing(2),
    textAlign: 'center',
    borderRadius: '16px',
    boxShadow: theme.shadows[8],
    backgroundColor: 'rgba(34, 34, 34, 0.8)',
    backdropFilter: 'blur(5px)',
    backgroundImage: 'url(https://images.unsplash.com/photo-1629968417850-3505f5180761?q=80&w=1952&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D)', // Set your desired background image
    backgroundSize: 'cover',
    backgroundPosition: 'center',
}));


export default StyledPaper;