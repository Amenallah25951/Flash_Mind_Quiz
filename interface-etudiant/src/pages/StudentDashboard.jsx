import { useState } from "react";

export default function StudentDashboard() {
  const [quizCode, setQuizCode] = useState("");
  const [showCodeInput, setShowCodeInput] = useState(false);
  
  // Données de l'étudiant (à remplacer par les vraies données de l'API)
  const studentName = "Amen Allah El Azzouni";
  const studentEmoji = "😀";

  // Quiz publics disponibles (à remplacer par les données de l'API)
  const publicQuizzes = [
    {
      id: 1,
      title: "React.js Fundamentals",
      description: "Test your knowledge of React basics",
      questions: 10,
      duration: 15,
      difficulty: "Facile",
      participants: 245
    },
    {
      id: 2,
      title: "JavaScript ES6+",
      description: "Modern JavaScript features and syntax",
      questions: 15,
      duration: 20,
      difficulty: "Moyen",
      participants: 189
    },
    {
      id: 3,
      title: "CSS Flexbox & Grid",
      description: "Master modern CSS layout techniques",
      questions: 12,
      duration: 18,
      difficulty: "Facile",
      participants: 312
    },
    {
      id: 4,
      title: "Node.js Backend",
      description: "Server-side JavaScript with Node",
      questions: 20,
      duration: 25,
      difficulty: "Difficile",
      participants: 156
    }
  ];

  const handleJoinByCode = () => {
    if (!quizCode.trim()) {
      alert("Veuillez entrer un code de quiz !");
      return;
    }
    console.log("Rejoindre le quiz avec le code:", quizCode);
    // Logique pour rejoindre le quiz
  };

  const handleStartQuiz = (quizId) => {
    console.log("Démarrer le quiz:", quizId);
    // Redirection vers la page du quiz
  };

  const handleLogout = () => {
    console.log("Déconnexion");
    window.location.href = '/login';
  };

  const getDifficultyColor = (difficulty) => {
    switch(difficulty) {
      case "Facile": return "#28a745";
      case "Moyen": return "#ffc107";
      case "Difficile": return "#dc3545";
      default: return "#6c757d";
    }
  };

  const styles = `
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }
    body {
      margin: 0;
      padding: 0;
    }
    @keyframes fadeIn {
      from {
        opacity: 0;
        transform: translateY(20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }
    .fade-in {
      animation: fadeIn 0.6s ease-out;
    }
    .quiz-card {
      transition: transform 0.3s, box-shadow 0.3s;
    }
    .quiz-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 15px 40px rgba(0,0,0,0.2) !important;
    }
    
    @media (max-width: 768px) {
      .logo-text {
        font-size: 24px !important;
      }
      .dashboard-title {
        font-size: 28px !important;
      }
      .quiz-grid {
        grid-template-columns: 1fr !important;
      }
    }
  `;

  return (
    <>
      <style>{styles}</style>
      <div style={{
        minHeight: '100vh',
        width: '100vw',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif',
        overflow: 'auto',
        paddingBottom: '40px'
      }}>
        {/* Header */}
        <header style={{
          padding: '20px 40px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          backgroundColor: 'rgba(255, 255, 255, 0.1)',
          backdropFilter: 'blur(10px)',
          borderBottom: '1px solid rgba(255, 255, 255, 0.2)'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
            <div style={{
              width: '50px',
              height: '50px',
              backgroundColor: 'white',
              borderRadius: '50%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontWeight: 'bold',
              fontSize: '28px',
              color: '#667eea'
            }}>
              Q
            </div>
            <h1 className="logo-text" style={{
              color: 'white',
              margin: 0,
              fontSize: '32px',
              fontWeight: 'bold',
              letterSpacing: '1px'
            }}>
              FLASH_MIND
            </h1>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '10px',
              backgroundColor: 'rgba(255, 255, 255, 0.2)',
              padding: '8px 20px',
              borderRadius: '25px'
            }}>
              <span style={{ fontSize: '24px' }}>{studentEmoji}</span>
              <span style={{ color: 'white', fontWeight: '600' }}>{studentName}</span>
            </div>
            <button
              onClick={handleLogout}
              style={{
                padding: '10px 24px',
                fontSize: '16px',
                fontWeight: 'bold',
                color: '#667eea',
                backgroundColor: 'white',
                border: 'none',
                borderRadius: '25px',
                cursor: 'pointer',
                transition: 'transform 0.2s'
              }}
              onMouseOver={(e) => e.target.style.transform = 'scale(1.05)'}
              onMouseOut={(e) => e.target.style.transform = 'scale(1)'}
            >
              Déconnexion
            </button>
          </div>
        </header>

        {/* Main Content */}
        <div style={{
          maxWidth: '1400px',
          margin: '0 auto',
          padding: '40px 20px'
        }}>
          {/* Welcome Section */}
          <div className="fade-in" style={{
            textAlign: 'center',
            marginBottom: '50px'
          }}>
            <h2 className="dashboard-title" style={{
              fontSize: '42px',
              fontWeight: 'bold',
              color: 'white',
              marginBottom: '15px'
            }}>
              Bienvenue, {studentName} ! 👋
            </h2>
            <p style={{
              fontSize: '18px',
              color: 'rgba(255, 255, 255, 0.9)',
              marginBottom: '30px'
            }}>
              Prêt à tester vos connaissances ?
            </p>

            {/* Join by Code Button */}
            <button
              onClick={() => setShowCodeInput(!showCodeInput)}
              style={{
                padding: '18px 50px',
                fontSize: '20px',
                fontWeight: 'bold',
                color: 'white',
                backgroundColor: '#17a2b8',
                border: 'none',
                borderRadius: '30px',
                cursor: 'pointer',
                boxShadow: '0 10px 30px rgba(0,0,0,0.3)',
                transition: 'all 0.3s',
                display: 'inline-flex',
                alignItems: 'center',
                gap: '10px'
              }}
              onMouseOver={(e) => {
                e.target.style.transform = 'translateY(-3px)';
                e.target.style.boxShadow = '0 15px 40px rgba(0,0,0,0.4)';
              }}
              onMouseOut={(e) => {
                e.target.style.transform = 'translateY(0)';
                e.target.style.boxShadow = '0 10px 30px rgba(0,0,0,0.3)';
              }}
            >
              <span style={{ fontSize: '24px' }}>🔑</span>
              Rejoindre un quiz par code
            </button>

            {/* Code Input */}
            {showCodeInput && (
              <div className="fade-in" style={{
                marginTop: '30px',
                display: 'flex',
                justifyContent: 'center',
                gap: '15px',
                flexWrap: 'wrap'
              }}>
                <input
                  type="text"
                  placeholder="Entrez le code du quiz"
                  value={quizCode}
                  onChange={(e) => setQuizCode(e.target.value.toUpperCase())}
                  onKeyPress={(e) => e.key === 'Enter' && handleJoinByCode()}
                  style={{
                    padding: '15px 25px',
                    fontSize: '18px',
                    border: 'none',
                    borderRadius: '15px',
                    backgroundColor: 'white',
                    color: '#333',
                    minWidth: '250px',
                    textAlign: 'center',
                    fontWeight: '600',
                    letterSpacing: '2px',
                    outline: 'none'
                  }}
                />
                <button
                  onClick={handleJoinByCode}
                  style={{
                    padding: '15px 35px',
                    fontSize: '18px',
                    fontWeight: 'bold',
                    color: 'white',
                    backgroundColor: '#28a745',
                    border: 'none',
                    borderRadius: '15px',
                    cursor: 'pointer',
                    transition: 'background-color 0.3s'
                  }}
                  onMouseOver={(e) => e.target.style.backgroundColor = '#218838'}
                  onMouseOut={(e) => e.target.style.backgroundColor = '#28a745'}
                >
                  Rejoindre →
                </button>
              </div>
            )}
          </div>

          {/* Public Quizzes Section */}
          <div className="fade-in" style={{
            backgroundColor: 'rgba(255, 255, 255, 0.1)',
            backdropFilter: 'blur(10px)',
            borderRadius: '30px',
            padding: '40px',
            marginBottom: '40px'
          }}>
            <div style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              marginBottom: '30px'
            }}>
              <h3 style={{
                fontSize: '32px',
                fontWeight: 'bold',
                color: 'white',
                margin: 0
              }}>
                📚 Quiz Publics
              </h3>
              <span style={{
                fontSize: '16px',
                color: 'rgba(255, 255, 255, 0.8)'
              }}>
                {publicQuizzes.length} quiz disponibles
              </span>
            </div>

            {/* Quiz Grid */}
            <div className="quiz-grid" style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))',
              gap: '25px'
            }}>
              {publicQuizzes.map((quiz) => (
                <div
                  key={quiz.id}
                  className="quiz-card"
                  style={{
                    backgroundColor: 'white',
                    borderRadius: '20px',
                    padding: '25px',
                    boxShadow: '0 10px 30px rgba(0,0,0,0.2)',
                    cursor: 'pointer'
                  }}
                >
                  <div style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'flex-start',
                    marginBottom: '15px'
                  }}>
                    <h4 style={{
                      fontSize: '20px',
                      fontWeight: 'bold',
                      color: '#333',
                      margin: 0,
                      flex: 1
                    }}>
                      {quiz.title}
                    </h4>
                    <span style={{
                      padding: '4px 12px',
                      fontSize: '12px',
                      fontWeight: '600',
                      color: 'white',
                      backgroundColor: getDifficultyColor(quiz.difficulty),
                      borderRadius: '12px',
                      marginLeft: '10px'
                    }}>
                      {quiz.difficulty}
                    </span>
                  </div>

                  <p style={{
                    fontSize: '14px',
                    color: '#6c757d',
                    marginBottom: '20px',
                    lineHeight: '1.5'
                  }}>
                    {quiz.description}
                  </p>

                  <div style={{
                    display: 'flex',
                    gap: '15px',
                    marginBottom: '20px',
                    flexWrap: 'wrap'
                  }}>
                    <div style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '5px',
                      fontSize: '14px',
                      color: '#6c757d'
                    }}>
                      <span>📝</span>
                      <span>{quiz.questions} questions</span>
                    </div>
                    <div style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '5px',
                      fontSize: '14px',
                      color: '#6c757d'
                    }}>
                      <span>⏱️</span>
                      <span>{quiz.duration} min</span>
                    </div>
                    <div style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: '5px',
                      fontSize: '14px',
                      color: '#6c757d'
                    }}>
                      <span>👥</span>
                      <span>{quiz.participants}</span>
                    </div>
                  </div>

                  <button
                    onClick={() => handleStartQuiz(quiz.id)}
                    style={{
                      width: '100%',
                      padding: '12px',
                      fontSize: '16px',
                      fontWeight: 'bold',
                      color: 'white',
                      backgroundColor: '#667eea',
                      border: 'none',
                      borderRadius: '12px',
                      cursor: 'pointer',
                      transition: 'background-color 0.3s'
                    }}
                    onMouseOver={(e) => e.target.style.backgroundColor = '#5568d3'}
                    onMouseOut={(e) => e.target.style.backgroundColor = '#667eea'}
                  >
                    Commencer le quiz →
                  </button>
                </div>
              ))}
            </div>
          </div>

          {/* Stats Section */}
          <div className="fade-in" style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
            gap: '20px'
          }}>
            <div style={{
              backgroundColor: 'rgba(255, 255, 255, 0.15)',
              backdropFilter: 'blur(10px)',
              borderRadius: '20px',
              padding: '30px',
              textAlign: 'center'
            }}>
              <div style={{ fontSize: '48px', marginBottom: '10px' }}>🏆</div>
              <h4 style={{ fontSize: '32px', fontWeight: 'bold', color: 'white', marginBottom: '5px' }}>
                12
              </h4>
              <p style={{ fontSize: '16px', color: 'rgba(255, 255, 255, 0.8)' }}>
                Quiz complétés
              </p>
            </div>

            <div style={{
              backgroundColor: 'rgba(255, 255, 255, 0.15)',
              backdropFilter: 'blur(10px)',
              borderRadius: '20px',
              padding: '30px',
              textAlign: 'center'
            }}>
              <div style={{ fontSize: '48px', marginBottom: '10px' }}>⭐</div>
              <h4 style={{ fontSize: '32px', fontWeight: 'bold', color: 'white', marginBottom: '5px' }}>
                8.5/10
              </h4>
              <p style={{ fontSize: '16px', color: 'rgba(255, 255, 255, 0.8)' }}>
                Score moyen
              </p>
            </div>

            <div style={{
              backgroundColor: 'rgba(255, 255, 255, 0.15)',
              backdropFilter: 'blur(10px)',
              borderRadius: '20px',
              padding: '30px',
              textAlign: 'center'
            }}>
              <div style={{ fontSize: '48px', marginBottom: '10px' }}>🔥</div>
              <h4 style={{ fontSize: '32px', fontWeight: 'bold', color: 'white', marginBottom: '5px' }}>
                7 jours
              </h4>
              <p style={{ fontSize: '16px', color: 'rgba(255, 255, 255, 0.8)' }}>
                Série actuelle
              </p>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div style={{
          textAlign: 'center',
          color: 'white',
          fontSize: '14px',
          padding: '20px',
          marginTop: '40px'
        }}>
          <p style={{ marginBottom: '5px', fontWeight: 'bold' }}>
            © 2025 Flash Mind Quiz Time. Tous droits réservés.
          </p>
        </div>
      </div>
    </>
  );
}