import api from './api';

export const authService = {
  // Connexion
  login: async (email, password) => {
    const response = await api.post('/auth/login', { email, password });
    
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('refreshToken', response.data.refreshToken);
      localStorage.setItem('user', JSON.stringify({
        username: response.data.username,
        email: response.data.email,
        role: response.data.role,
        firstName: response.data.firstName,
        lastName: response.data.lastName
      }));
    }
    
    return response.data;
  },

  // Inscription
  signup: async (userData) => {
    const response = await api.post('/auth/signup', userData);
    return response.data;
  },

  // Déconnexion
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    window.location.href = '/login';
  },

  // Obtenir l'utilisateur actuel
  getCurrentUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },

  // Vérifier si l'utilisateur est connecté
  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  },

  // Obtenir le rôle de l'utilisateur
  getUserRole: () => {
    const user = authService.getCurrentUser();
    return user?.role;
  },

  // Redirection selon le rôle
  redirectToDashboard: (role) => {
    switch(role?.toLowerCase()) {
      case 'student':
        window.location.href = '/student/dashboard';
        break;
      case 'professor':
        window.location.href = '/professor/dashboard';
        break;
      case 'admin':
        window.location.href = '/admin/dashboard';
        break;
      default:
        window.location.href = '/';
    }
  }
};