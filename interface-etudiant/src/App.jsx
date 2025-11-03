import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Landing from './pages/Landing';
import Home from './pages/Home';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Quiz from './pages/Quiz';
import StudentDashboard from './pages/StudentDashboard';
import Error404 from './pages/Error404';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  return (
    <BrowserRouter>
    
      <Routes>
        {/* Routes publiques */}
        <Route path="/" element={<Landing />} />
        <Route path="/home" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        
        
        {/* Routes protégées - Étudiant */}
        <Route 
          path="/student/dashboard" 
          element={
            <ProtectedRoute allowedRoles={['student']}>
              <StudentDashboard />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/student/quiz/:id" 
          element={
            <ProtectedRoute allowedRoles={['student']}>
              <Quiz />
            </ProtectedRoute>
          } 
        />
        
        {/* Routes protégées - Professeur */}
        <Route 
          path="/professor/dashboard" 
          element={
            <ProtectedRoute allowedRoles={['professor']}>
              <div style={{
                minHeight: '100vh',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                color: 'white',
                fontSize: '32px',
                fontWeight: 'bold'
              }}>
                Professor Dashboard (à créer)
              </div>
            </ProtectedRoute>
          } 
        />
        
        {/* Routes protégées - Admin */}
        <Route 
          path="/admin/dashboard" 
          element={
            <ProtectedRoute allowedRoles={['admin']}>
              <div style={{
                minHeight: '100vh',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                color: 'white',
                fontSize: '32px',
                fontWeight: 'bold'
              }}>
                Admin Dashboard (à créer)
              </div>
            </ProtectedRoute>
          } 
        />
        
        {/* Route 404 */}
        <Route path="*" element={<Error404 />} />
      </Routes>
      
    </BrowserRouter>
  );
}

export default App;