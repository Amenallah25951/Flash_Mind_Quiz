import { useState } from "react";
import { authService } from '../services/authService';


export default function Signup() {
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
    firstName: "",
    lastName: "",
    role: "student" // default role
  });
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };


  const handleSignup = async () => {
    // Validation
    if (!formData.username.trim() || !formData.email.trim() || !formData.password.trim() || 
        !formData.firstName.trim() || !formData.lastName.trim()) {
      alert("Veuillez remplir tous les champs obligatoires !");
      return;
    }
  
    if (formData.password !== formData.confirmPassword) {
      alert("Les mots de passe ne correspondent pas !");
      return;
    }
  
    if (formData.password.length < 8) {
      alert("Le mot de passe doit contenir au moins 8 caractères !");
      return;
    }
  
    try {
      await authService.signup({
        username: formData.username,
        email: formData.email,
        password: formData.password,
        firstName: formData.firstName,
        lastName: formData.lastName,
        role: formData.role
      });
      
      alert("Inscription réussie ! Vous pouvez maintenant vous connecter.");
      window.location.href = '/login';
    } catch (error) {
      alert(error.response?.data?.error || "Erreur lors de l'inscription");
    }
  };

  const handleLogin = () => {
    console.log("Redirection vers connexion");
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
    
    @media (max-width: 768px) {
      .logo-text {
        font-size: 24px !important;
      }
      .logo-circle {
        width: 40px !important;
        height: 40px !important;
        font-size: 22px !important;
      }
      .signup-card {
        padding: 40px 30px !important;
        margin: 20px !important;
      }
      .signup-title {
        font-size: 28px !important;
      }
    }
    
    @media (max-width: 480px) {
      .logo-container {
        top: 10px !important;
        left: 10px !important;
        gap: 8px !important;
      }
      .logo-text {
        font-size: 20px !important;
      }
      .logo-circle {
        width: 35px !important;
        height: 35px !important;
        font-size: 18px !important;
      }
      .lang-button {
        top: 10px !important;
        right: 10px !important;
        padding: 8px 18px !important;
        font-size: 14px !important;
      }
      .signup-card {
        padding: 30px 20px !important;
        margin: 10px !important;
        border-radius: 20px !important;
      }
      .signup-title {
        font-size: 24px !important;
      }
      .role-buttons {
        flex-direction: column !important;
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
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        position: 'relative',
        fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif',
        padding: '20px',
        paddingTop: '100px',
        paddingBottom: '80px',
        overflow: 'auto'
      }}>
        {/* Logo */}
        <div className="logo-container" style={{
          position: 'absolute',
          top: '20px',
          left: '20px',
          display: 'flex',
          alignItems: 'center',
          gap: '10px'
        }}>
          <div className="logo-circle" style={{
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
          <span className="logo-text" style={{
            color: 'white',
            fontSize: '32px',
            fontWeight: 'bold',
            letterSpacing: '1px'
          }}>
            FLASH_MIND
          </span>
        </div>

        {/* Langue */}
        <div className="lang-button" style={{
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

        {/* Signup Card */}
        <div className="fade-in signup-card" style={{
          backgroundColor: 'white',
          borderRadius: '30px',
          padding: '50px 40px',
          boxShadow: '0 20px 60px rgba(0,0,0,0.3)',
          width: '100%',
          maxWidth: '600px',
          textAlign: 'center'
        }}>
          <div style={{
            width: '80px',
            height: '80px',
            backgroundColor: '#667eea',
            borderRadius: '50%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '40px',
            margin: '0 auto 25px',
            boxShadow: '0 5px 15px rgba(102, 126, 234, 0.4)'
          }}>
            ✏️
          </div>

          <h2 className="signup-title" style={{
            fontSize: '32px',
            fontWeight: 'bold',
            marginBottom: '10px',
            color: '#000'
          }}>
            Créer un compte
          </h2>

          <p style={{
            fontSize: '16px',
            color: '#6c757d',
            marginBottom: '30px'
          }}>
            Rejoignez Flash Mind aujourd'hui
          </p>

          {/* Role Selection */}
          <div style={{
            marginBottom: '25px',
            textAlign: 'left'
          }}>
            <label style={{
              display: 'block',
              fontSize: '14px',
              fontWeight: '600',
              color: '#333',
              marginBottom: '10px'
            }}>
              Je suis un(e) :
            </label>
            <div className="role-buttons" style={{
              display: 'flex',
              gap: '15px'
            }}>
              <button
                onClick={() => setFormData({...formData, role: 'student'})}
                style={{
                  flex: 1,
                  padding: '15px',
                  fontSize: '16px',
                  fontWeight: '600',
                  color: formData.role === 'student' ? 'white' : '#333',
                  backgroundColor: formData.role === 'student' ? '#667eea' : 'white',
                  border: `2px solid ${formData.role === 'student' ? '#667eea' : '#e0e0e0'}`,
                  borderRadius: '12px',
                  cursor: 'pointer',
                  transition: 'all 0.3s',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '8px'
                }}
              >
                <span style={{ fontSize: '20px' }}>🎓</span>
                Étudiant
              </button>
              <button
                onClick={() => setFormData({...formData, role: 'professor'})}
                style={{
                  flex: 1,
                  padding: '15px',
                  fontSize: '16px',
                  fontWeight: '600',
                  color: formData.role === 'professor' ? 'white' : '#333',
                  backgroundColor: formData.role === 'professor' ? '#667eea' : 'white',
                  border: `2px solid ${formData.role === 'professor' ? '#667eea' : '#e0e0e0'}`,
                  borderRadius: '12px',
                  cursor: 'pointer',
                  transition: 'all 0.3s',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '8px'
                }}
              >
                <span style={{ fontSize: '20px' }}>👨‍🏫</span>
                Professeur
              </button>
            </div>
          </div>

          {/* Name Fields */}
          <div style={{
            display: 'grid',
            gridTemplateColumns: '1fr 1fr',
            gap: '15px',
            marginBottom: '20px'
          }}>
            <div style={{ textAlign: 'left' }}>
              <label style={{
                display: 'block',
                fontSize: '14px',
                fontWeight: '600',
                color: '#333',
                marginBottom: '8px'
              }}>
                Prénom
              </label>
              <input
                type="text"
                name="firstName"
                placeholder="Prénom"
                value={formData.firstName}
                onChange={handleChange}
                style={{
                  width: '100%',
                  padding: '12px 15px',
                  fontSize: '16px',
                  border: '2px solid #e0e0e0',
                  borderRadius: '12px',
                  backgroundColor: '#f5f5f5',
                  color: '#333',
                  outline: 'none',
                  transition: 'border-color 0.3s'
                }}
                onFocus={(e) => e.target.style.borderColor = '#667eea'}
                onBlur={(e) => e.target.style.borderColor = '#e0e0e0'}
              />
            </div>

            <div style={{ textAlign: 'left' }}>
              <label style={{
                display: 'block',
                fontSize: '14px',
                fontWeight: '600',
                color: '#333',
                marginBottom: '8px'
              }}>
                Nom
              </label>
              <input
                type="text"
                name="lastName"
                placeholder="Nom"
                value={formData.lastName}
                onChange={handleChange}
                style={{
                  width: '100%',
                  padding: '12px 15px',
                  fontSize: '16px',
                  border: '2px solid #e0e0e0',
                  borderRadius: '12px',
                  backgroundColor: '#f5f5f5',
                  color: '#333',
                  outline: 'none',
                  transition: 'border-color 0.3s'
                }}
                onFocus={(e) => e.target.style.borderColor = '#667eea'}
                onBlur={(e) => e.target.style.borderColor = '#e0e0e0'}
              />
            </div>
          </div>

          {/* Username */}
          <div style={{
            marginBottom: '20px',
            textAlign: 'left'
          }}>
            <label style={{
              display: 'block',
              fontSize: '14px',
              fontWeight: '600',
              color: '#333',
              marginBottom: '8px'
            }}>
              Nom d'utilisateur
            </label>
            <div style={{ position: 'relative' }}>
              <span style={{
                position: 'absolute',
                left: '15px',
                top: '50%',
                transform: 'translateY(-50%)',
                fontSize: '20px'
              }}>
                👤
              </span>
              <input
                type="text"
                name="username"
                placeholder="nom_utilisateur"
                value={formData.username}
                onChange={handleChange}
                style={{
                  width: '100%',
                  padding: '15px 15px 15px 50px',
                  fontSize: '16px',
                  border: '2px solid #e0e0e0',
                  borderRadius: '12px',
                  backgroundColor: '#f5f5f5',
                  color: '#333',
                  outline: 'none',
                  transition: 'border-color 0.3s'
                }}
                onFocus={(e) => e.target.style.borderColor = '#667eea'}
                onBlur={(e) => e.target.style.borderColor = '#e0e0e0'}
              />
            </div>
          </div>

          {/* Email */}
          <div style={{
            marginBottom: '20px',
            textAlign: 'left'
          }}>
            <label style={{
              display: 'block',
              fontSize: '14px',
              fontWeight: '600',
              color: '#333',
              marginBottom: '8px'
            }}>
              Email
            </label>
            <div style={{ position: 'relative' }}>
              <span style={{
                position: 'absolute',
                left: '15px',
                top: '50%',
                transform: 'translateY(-50%)',
                fontSize: '20px'
              }}>
                📧
              </span>
              <input
                type="email"
                name="email"
                placeholder="votre.email@exemple.com"
                value={formData.email}
                onChange={handleChange}
                style={{
                  width: '100%',
                  padding: '15px 15px 15px 50px',
                  fontSize: '16px',
                  border: '2px solid #e0e0e0',
                  borderRadius: '12px',
                  backgroundColor: '#f5f5f5',
                  color: '#333',
                  outline: 'none',
                  transition: 'border-color 0.3s'
                }}
                onFocus={(e) => e.target.style.borderColor = '#667eea'}
                onBlur={(e) => e.target.style.borderColor = '#e0e0e0'}
              />
            </div>
          </div>

          {/* Password */}
          <div style={{
            marginBottom: '20px',
            textAlign: 'left'
          }}>
            <label style={{
              display: 'block',
              fontSize: '14px',
              fontWeight: '600',
              color: '#333',
              marginBottom: '8px'
            }}>
              Mot de passe
            </label>
            <div style={{ position: 'relative' }}>
              <span style={{
                position: 'absolute',
                left: '15px',
                top: '50%',
                transform: 'translateY(-50%)',
                fontSize: '20px'
              }}>
                🔒
              </span>
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                placeholder="••••••••"
                value={formData.password}
                onChange={handleChange}
                style={{
                  width: '100%',
                  padding: '15px 50px 15px 50px',
                  fontSize: '16px',
                  border: '2px solid #e0e0e0',
                  borderRadius: '12px',
                  backgroundColor: '#f5f5f5',
                  color: '#333',
                  outline: 'none',
                  transition: 'border-color 0.3s'
                }}
                onFocus={(e) => e.target.style.borderColor = '#667eea'}
                onBlur={(e) => e.target.style.borderColor = '#e0e0e0'}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                style={{
                  position: 'absolute',
                  right: '15px',
                  top: '50%',
                  transform: 'translateY(-50%)',
                  background: 'none',
                  border: 'none',
                  cursor: 'pointer',
                  fontSize: '20px'
                }}
              >
                {showPassword ? '👁️' : '👁️‍🗨️'}
              </button>
            </div>
          </div>

          {/* Confirm Password */}
          <div style={{
            marginBottom: '25px',
            textAlign: 'left'
          }}>
            <label style={{
              display: 'block',
              fontSize: '14px',
              fontWeight: '600',
              color: '#333',
              marginBottom: '8px'
            }}>
              Confirmer le mot de passe
            </label>
            <div style={{ position: 'relative' }}>
              <span style={{
                position: 'absolute',
                left: '15px',
                top: '50%',
                transform: 'translateY(-50%)',
                fontSize: '20px'
              }}>
                🔒
              </span>
              <input
                type={showConfirmPassword ? "text" : "password"}
                name="confirmPassword"
                placeholder="••••••••"
                value={formData.confirmPassword}
                onChange={handleChange}
                onKeyPress={(e) => e.key === 'Enter' && handleSignup()}
                style={{
                  width: '100%',
                  padding: '15px 50px 15px 50px',
                  fontSize: '16px',
                  border: '2px solid #e0e0e0',
                  borderRadius: '12px',
                  backgroundColor: '#f5f5f5',
                  color: '#333',
                  outline: 'none',
                  transition: 'border-color 0.3s'
                }}
                onFocus={(e) => e.target.style.borderColor = '#667eea'}
                onBlur={(e) => e.target.style.borderColor = '#e0e0e0'}
              />
              <button
                type="button"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                style={{
                  position: 'absolute',
                  right: '15px',
                  top: '50%',
                  transform: 'translateY(-50%)',
                  background: 'none',
                  border: 'none',
                  cursor: 'pointer',
                  fontSize: '20px'
                }}
              >
                {showConfirmPassword ? '👁️' : '👁️‍🗨️'}
              </button>
            </div>
          </div>

          {/* Signup Button */}
          <button
            onClick={handleSignup}
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
              marginBottom: '20px',
              transition: 'background-color 0.3s'
            }}
            onMouseOver={(e) => e.target.style.backgroundColor = '#138496'}
            onMouseOut={(e) => e.target.style.backgroundColor = '#17a2b8'}
          >
            S'inscrire →
          </button>

          {/* Login Link */}
          <p style={{
            fontSize: '14px',
            color: '#6c757d'
          }}>
            Vous avez déjà un compte ?{' '}
            <a 
              href="#" 
              onClick={handleLogin}
              style={{
                color: '#667eea',
                fontWeight: '600',
                textDecoration: 'none'
              }}
            >
              Se connecter
            </a>
          </p>
        </div>

        {/* Footer */}
        <div style={{
          marginTop: '30px',
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