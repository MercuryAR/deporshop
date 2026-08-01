import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';

export default function LoginForm() {
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [nombre, setNombre] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [submitError, setSubmitError] = useState<string | null>(null);

  const login = useAuthStore((s) => s.login);
  const register = useAuthStore((s) => s.register);
  const loading = useAuthStore((s) => s.loading);
  const navigate = useNavigate();

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitError(null);
    try {
      if (mode === 'login') {
        await login(email, password);
      } else {
        await register(nombre, email, password);
      }
      navigate('/perfil');
    } catch {
      setSubmitError(
        mode === 'login'
          ? 'Email o contraseña incorrectos.'
          : 'No se pudo crear la cuenta. Intenta nuevamente.',
      );
    }
  }

  return (
    <div className="max-w-md mx-auto bg-white p-8 rounded-xl shadow-lg border border-black/5">
      <div className="flex mb-6 rounded-lg bg-gray-100 p-1">
        <button
          type="button"
          className={`flex-1 py-2 rounded-md font-semibold transition-colors ${
            mode === 'login' ? 'bg-white shadow-sm text-primary' : 'text-gray-500'
          }`}
          onClick={() => setMode('login')}
        >
          Iniciar sesión
        </button>
        <button
          type="button"
          className={`flex-1 py-2 rounded-md font-semibold transition-colors ${
            mode === 'register' ? 'bg-white shadow-sm text-primary' : 'text-gray-500'
          }`}
          onClick={() => setMode('register')}
        >
          Crear cuenta
        </button>
      </div>

      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        {mode === 'register' && (
          <div>
            <label htmlFor="nombre" className="form-label">
              Nombre
            </label>
            <input
              id="nombre"
              type="text"
              required
              className="form-input"
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              placeholder="Tu nombre"
            />
          </div>
        )}

        <div>
          <label htmlFor="email" className="form-label">
            Email
          </label>
          <input
            id="email"
            type="email"
            required
            className="form-input"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="tu@email.com"
          />
        </div>

        <div>
          <label htmlFor="password" className="form-label">
            Contraseña
          </label>
          <input
            id="password"
            type="password"
            required
            minLength={6}
            className="form-input"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
          />
        </div>

        {submitError && <p className="text-error text-sm font-medium">{submitError}</p>}

        <button type="submit" className="btn-accent w-full mt-2" disabled={loading}>
          {loading ? 'Enviando…' : mode === 'login' ? 'Iniciar sesión' : 'Crear cuenta'}
        </button>
      </form>
    </div>
  );
}
