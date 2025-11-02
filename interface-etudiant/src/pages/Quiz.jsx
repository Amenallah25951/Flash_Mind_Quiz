import { useState, useEffect } from 'react';

export default function Quiz() {
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [timeRemaining, setTimeRemaining] = useState(30);
  const [selectedAnswer, setSelectedAnswer] = useState(null);
  const [score, setScore] = useState(0);
  const [answers, setAnswers] = useState([]);
  const [showResults, setShowResults] = useState(false);
  const [totalTime, setTotalTime] = useState(0);

  const questions = [
    {
      id: 1,
      question: "Which of the following is used in React.js to increase performance?",
      options: ["Virtual DOM", "JSX", "Components", "Props"],
      correctAnswer: "Virtual DOM"
    },
    {
      id: 2,
      question: "What is the correct syntax to create a functional component in React?",
      options: ["function MyComponent() {}", "class MyComponent {}", "const MyComponent = {}", "component MyComponent()"],
      correctAnswer: "function MyComponent() {}"
    },
    {
      id: 3,
      question: "Which hook is used to manage state in functional components?",
      options: ["useEffect", "useState", "useContext", "useReducer"],
      correctAnswer: "useState"
    },
    {
      id: 4,
      question: "What does JSX stand for?",
      options: ["JavaScript XML", "JavaScript Extension", "Java Syntax Extension", "JavaScript Execute"],
      correctAnswer: "JavaScript XML"
    },
    {
      id: 5,
      question: "Which method is used to update state in React?",
      options: ["updateState()", "setState()", "modifyState()", "changeState()"],
      correctAnswer: "setState()"
    },
    {
      id: 6,
      question: "What is the purpose of useEffect hook?",
      options: ["To create state", "To handle side effects", "To create refs", "To optimize performance"],
      correctAnswer: "To handle side effects"
    },
    {
      id: 7,
      question: "How do you pass data from parent to child component?",
      options: ["Using state", "Using props", "Using context", "Using refs"],
      correctAnswer: "Using props"
    },
    {
      id: 8,
      question: "What is the correct way to handle events in React?",
      options: ["onclick='handleClick()'", "onClick={handleClick}", "onClick='handleClick()'", "onCLick={handleClick()}"],
      correctAnswer: "onClick={handleClick}"
    },
    {
      id: 9,
      question: "Which of these is NOT a React hook?",
      options: ["useState", "useEffect", "useComponent", "useContext"],
      correctAnswer: "useComponent"
    },
    {
      id: 10,
      question: "What does React.Fragment do?",
      options: ["Creates a new component", "Groups multiple elements without adding extra nodes", "Handles errors", "Manages state"],
      correctAnswer: "Groups multiple elements without adding extra nodes"
    }
  ];

  const totalQuestions = questions.length;
  const progressPercentage = ((currentQuestion + 1) / totalQuestions) * 100;

  useEffect(() => {
    if (timeRemaining > 0 && !showResults) {
      const timer = setTimeout(() => {
        setTimeRemaining(timeRemaining - 1);
        setTotalTime(totalTime + 1);
      }, 1000);
      return () => clearTimeout(timer);
    } else if (timeRemaining === 0 && !showResults) {
      handleNext();
    }
  }, [timeRemaining, showResults]);

  const handleAnswerSelect = (answer) => {
    setSelectedAnswer(answer);
  };

  const handleNext = () => {
    const isCorrect = selectedAnswer === questions[currentQuestion].correctAnswer;
    
    if (isCorrect && selectedAnswer !== null) {
      setScore(score + 1);
    }

    setAnswers([...answers, {
      question: questions[currentQuestion].question,
      selectedAnswer: selectedAnswer || "No answer",
      correctAnswer: questions[currentQuestion].correctAnswer,
      isCorrect: isCorrect
    }]);

    if (currentQuestion < totalQuestions - 1) {
      setCurrentQuestion(currentQuestion + 1);
      setSelectedAnswer(null);
      setTimeRemaining(30);
    } else {
      setShowResults(true);
    }
  };

  const handlePrevious = () => {
    if (currentQuestion > 0) {
      setCurrentQuestion(currentQuestion - 1);
      setSelectedAnswer(null);
      setTimeRemaining(30);
    }
  };

  const handleReplay = () => {
    setCurrentQuestion(0);
    setTimeRemaining(30);
    setSelectedAnswer(null);
    setScore(0);
    setAnswers([]);
    setShowResults(false);
    setTotalTime(0);
  };

  const handleMainMenu = () => {
    window.location.href = '/';
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
    .btn {
      display: inline-block;
      font-weight: 400;
      text-align: center;
      vertical-align: middle;
      user-select: none;
      border: 1px solid transparent;
      padding: 0.375rem 0.75rem;
      font-size: 1rem;
      line-height: 1.5;
      border-radius: 0.25rem;
      transition: color 0.15s ease-in-out, background-color 0.15s ease-in-out, border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
      cursor: pointer;
    }
    .btn-light {
      color: #212529;
      background-color: #f8f9fa;
      border-color: #f8f9fa;
    }
    .btn-light:hover {
      background-color: #e2e6ea;
      border-color: #dae0e5;
    }
    .btn-primary {
      color: #fff;
      background-color: #007bff;
      border-color: #007bff;
    }
    .btn-secondary {
      color: #fff;
      background-color: #6c757d;
      border-color: #6c757d;
    }
    .btn-secondary:hover {
      background-color: #5a6268;
      border-color: #545b62;
    }
    .btn:disabled {
      opacity: 0.65;
      cursor: not-allowed;
    }
  `;

  if (showResults) {
    const finalScore = score * 125; // 1250 points maximum pour 10 questions
    const minutes = Math.floor(totalTime / 60);
    const seconds = totalTime % 60;
    const timeElapsed = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;

    return (
      <>
        <style>{styles}</style>
        <div style={{
          minHeight: '100vh',
          height: '100vh',
          width: '100vw',
          margin: 0,
          padding: 0,
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif',
          overflow: 'auto',
          boxSizing: 'border-box',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '20px'
        }}>
          {/* Logo */}
          <div style={{
            position: 'absolute',
            top: '20px',
            left: '20px',
            display: 'flex',
            alignItems: 'center',
            gap: '10px'
          }}>
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
            <span style={{
              color: 'white',
              fontSize: '32px',
              fontWeight: 'bold',
              letterSpacing: '1px'
            }}>
              FLASH_MIND
            </span>
          </div>

          {/* Langue */}
          <div style={{
            position: 'absolute',
            top: '20px',
            right: '20px'
          }}>
            <button style={{
              backgroundColor: '#667eea',
              color: 'white',
              border: 'none',
              padding: '10px 24px',
              borderRadius: '50rem',
              fontWeight: '600',
              cursor: 'pointer',
              fontSize: '16px'
            }}>
              🌐 FR
            </button>
          </div>

          {/* Results Card */}
          <div style={{
            backgroundColor: '#f8f9fa',
            borderRadius: '30px',
            padding: '40px',
            boxShadow: '0 20px 60px rgba(0,0,0,0.3)',
            width: '100%',
            maxWidth: '600px',
            textAlign: 'center'
          }}>
            <h1 style={{
              fontSize: '32px',
              fontWeight: 'bold',
              marginBottom: '10px',
              color: '#000'
            }}>
              Student nickname
            </h1>

            <p style={{
              fontSize: '16px',
              color: '#6c757d',
              marginBottom: '5px'
            }}>
              Final Score
            </p>
            <h2 style={{
              fontSize: '48px',
              fontWeight: 'bold',
              color: '#667eea',
              marginBottom: '30px'
            }}>
              {finalScore}
            </h2>

            <div style={{
              display: 'grid',
              gridTemplateColumns: '1fr 1fr',
              gap: '20px',
              marginBottom: '30px'
            }}>
              {/* Left Column - Achievements */}
              <div style={{
                backgroundColor: '#e9ecef',
                borderRadius: '15px',
                padding: '20px',
                textAlign: 'left'
              }}>
                <div style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '10px',
                  marginBottom: '10px'
                }}>
                  <span style={{ fontSize: '24px' }}>🏆</span>
                  <span style={{ fontSize: '16px', color: '#6c757d' }}>Star Winner</span>
                </div>
                <div style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '10px'
                }}>
                  <span style={{ fontSize: '24px' }}>🥉</span>
                  <span style={{ fontSize: '16px', color: '#6c757d' }}>3rd position</span>
                </div>
              </div>

              {/* Right Column - Summary */}
              <div style={{
                backgroundColor: '#e9ecef',
                borderRadius: '15px',
                padding: '20px',
                textAlign: 'left'
              }}>
                <h4 style={{
                  fontSize: '14px',
                  fontWeight: 'bold',
                  marginBottom: '15px',
                  color: '#000'
                }}>
                  Summary of responses
                </h4>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <span style={{ color: '#28a745', fontSize: '18px' }}>✓</span>
                    <span style={{ fontSize: '14px', color: '#6c757d' }}>Correct answers</span>
                  </div>
                  <span style={{ fontSize: '14px', fontWeight: 'bold' }}>{score}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <span style={{ fontSize: '18px' }}>⚪</span>
                    <span style={{ fontSize: '14px', color: '#6c757d' }}>Total questions</span>
                  </div>
                  <span style={{ fontSize: '14px', fontWeight: 'bold' }}>{totalQuestions}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <span style={{ fontSize: '18px' }}>⏱️</span>
                    <span style={{ fontSize: '14px', color: '#6c757d' }}>Time elapsed</span>
                  </div>
                  <span style={{ fontSize: '14px', fontWeight: 'bold' }}>{timeElapsed}</span>
                </div>
              </div>
            </div>

            {/* Buttons */}
            <button
              onClick={handleReplay}
              style={{
                width: '100%',
                padding: '15px',
                fontSize: '18px',
                fontWeight: 'bold',
                color: 'white',
                backgroundColor: '#17a2b8',
                border: 'none',
                borderRadius: '12px',
                cursor: 'pointer',
                marginBottom: '15px'
              }}
            >
              🔄 Replay
            </button>

            <button
              onClick={handleMainMenu}
              style={{
                width: '100%',
                padding: '15px',
                fontSize: '18px',
                fontWeight: 'bold',
                color: '#333',
                backgroundColor: 'white',
                border: '2px solid #dee2e6',
                borderRadius: '12px',
                cursor: 'pointer'
              }}
            >
              🏠 Main Menu
            </button>
          </div>

          {/* Footer */}
          <div style={{
            position: 'absolute',
            bottom: '20px',
            color: 'white',
            textAlign: 'center',
            fontSize: '14px',
            width: '100%',
            padding: '0 20px'
          }}>
            <p style={{ marginBottom: '5px', fontWeight: 'bold' }}>
              © 2025 Quiz Time. Tous droits réservés.
            </p>
            <div>
              <a href="#" style={{
                color: 'white',
                textDecoration: 'underline',
                marginRight: '10px'
              }}>
                Politique de confidentialité
              </a>
              <span>|</span>
              <a href="#" style={{
                color: 'white',
                textDecoration: 'underline',
                marginLeft: '10px'
              }}>
                Conditions d'utilisation
              </a>
            </div>
          </div>
        </div>
      </>
    );
  }

  return (
    <>
      <style>{styles}</style>
      <div style={{
        minHeight: '100vh',
        height: '100vh',
        width: '100vw',
        margin: 0,
        padding: 0,
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif',
        overflow: 'auto',
        boxSizing: 'border-box'
      }}>
        <div style={{ padding: '20px' }}>
          <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginBottom: '30px'
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
                fontSize: '24px',
                color: '#667eea'
              }}>
                Q
              </div>
              <h1 style={{
                color: 'white',
                margin: 0,
                fontSize: '32px',
                fontWeight: 'bold',
                letterSpacing: '1px'
              }}>
                FLASH_MIND
              </h1>
            </div>
            <button className="btn btn-light rounded-pill px-4" style={{ fontWeight: 'bold' }}>
              🌐 FR
            </button>
          </div>

          <div style={{
            borderRadius: '30px',
            border: 'none',
            backgroundColor: '#f8f9fa',
            maxWidth: '800px',
            margin: '0 auto',
            padding: '40px'
          }}>
            <div style={{ textAlign: 'center', marginBottom: '20px' }}>
              <div style={{
                fontSize: '20px',
                color: '#667eea',
                fontWeight: 'bold'
              }}>
                ⏱️ Time Remaining: {String(Math.floor(timeRemaining / 60)).padStart(2, '0')}:{String(timeRemaining % 60).padStart(2, '0')}
              </div>
            </div>

            <div style={{ marginBottom: '30px' }}>
              <div style={{
                height: '40px',
                borderRadius: '20px',
                backgroundColor: '#e9ecef',
                overflow: 'hidden'
              }}>
                <div style={{
                  width: `${progressPercentage}%`,
                  height: '100%',
                  backgroundColor: '#667eea',
                  borderRadius: '20px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '16px',
                  fontWeight: 'bold',
                  color: 'white',
                  transition: 'width 0.3s ease'
                }}>
                  Question {currentQuestion + 1} of {totalQuestions}
                </div>
              </div>
            </div>

            <div style={{ textAlign: 'center', marginBottom: '30px' }}>
              <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', marginBottom: '15px' }}>
                <div style={{ fontSize: '40px', marginRight: '15px' }}>⚙️</div>
                <h3 style={{
                  fontSize: '22px',
                  fontWeight: 'bold',
                  margin: 0,
                  color: '#333'
                }}>
                  Question {currentQuestion + 1}: {questions[currentQuestion].question}
                </h3>
                <div style={{ fontSize: '40px', marginLeft: '15px' }}>💻</div>
              </div>
            </div>

            <h5 style={{
              color: '#667eea',
              fontWeight: 'bold',
              marginBottom: '20px'
            }}>
              Options :
            </h5>

            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(2, 1fr)',
              gap: '15px',
              marginBottom: '30px'
            }}>
              {questions[currentQuestion].options.map((option, index) => (
                <button
                  key={index}
                  onClick={() => handleAnswerSelect(option)}
                  style={{
                    padding: '20px',
                    fontSize: '18px',
                    fontWeight: 'bold',
                    borderRadius: '15px',
                    border: selectedAnswer === option ? '3px solid #667eea' : '2px solid #dee2e6',
                    backgroundColor: selectedAnswer === option ? '#667eea' : 'white',
                    color: selectedAnswer === option ? 'white' : '#333',
                    cursor: 'pointer',
                    transition: 'all 0.3s ease',
                    boxShadow: '0 4px 6px rgba(0,0,0,0.1)'
                  }}
                >
                  {option}
                </button>
              ))}
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
              <button 
                className="btn btn-secondary"
                onClick={handlePrevious}
                disabled={currentQuestion === 0}
                style={{
                  padding: '12px 30px',
                  borderRadius: '10px',
                  fontWeight: 'bold'
                }}
              >
                Previous
              </button>
              <button 
                onClick={handleNext}
                style={{
                  padding: '12px 30px',
                  borderRadius: '10px',
                  fontWeight: 'bold',
                  backgroundColor: '#17a2b8',
                  color: 'white',
                  border: 'none',
                  cursor: 'pointer'
                }}
              >
                {currentQuestion === totalQuestions - 1 ? 'Finish' : 'Next'}
              </button>
            </div>
          </div>

          <div style={{
            textAlign: 'center',
            marginTop: '30px',
            color: 'white'
          }}>
            <p style={{ marginBottom: '10px', fontWeight: 'bold' }}>
              © 2025 Quiz Time. Tous droits réservés.
            </p>
            <div>
              <a href="#" style={{ color: 'white', textDecoration: 'underline', marginRight: '20px' }}>
                Politique de confidentialité
              </a>
              <a href="#" style={{ color: 'white', textDecoration: 'underline' }}>
                Conditions d'utilisation
              </a>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}