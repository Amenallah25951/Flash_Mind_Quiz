import api from './api';

export const quizService = {
  // Récupérer tous les quiz publics
  getPublicQuizzes: async () => {
    const response = await api.get('/student/quizzes/public');
    return response.data;
  },

  // Récupérer un quiz par code
  getQuizByCode: async (code) => {
    const response = await api.get(`/student/quiz/code/${code}`);
    return response.data;
  },

  // Récupérer un quiz par ID
  getQuizById: async (id) => {
    const response = await api.get(`/student/quiz/${id}`);
    return response.data;
  },

  // Démarrer un quiz
  startQuiz: async (quizId) => {
    const response = await api.post(`/student/quiz/${quizId}/start`);
    return response.data;
  },

  // Soumettre une réponse
  submitAnswer: async (quizId, questionId, answerId) => {
    const response = await api.post(`/student/quiz/${quizId}/answer`, {
      questionId,
      answerId
    });
    return response.data;
  },

  // Terminer un quiz
  finishQuiz: async (quizId, answers) => {
    const response = await api.post(`/student/quiz/${quizId}/finish`, {
      answers
    });
    return response.data;
  }
};