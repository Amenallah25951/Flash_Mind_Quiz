import api from './api';

export const studentService = {
  // Récupérer les statistiques de l'étudiant
  getStats: async () => {
    const response = await api.get('/student/stats');
    return response.data;
  },

  // Récupérer l'historique des quiz
  getQuizHistory: async () => {
    const response = await api.get('/student/history');
    return response.data;
  },

  // Récupérer les détails d'une participation
  getParticipationDetails: async (participationId) => {
    const response = await api.get(`/student/participation/${participationId}`);
    return response.data;
  }
};