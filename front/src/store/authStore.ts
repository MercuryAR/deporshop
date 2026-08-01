import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import * as api from '../services/api';
import type { Usuario } from '../types/Usuario';

interface AuthState {
  user: Usuario | null;
  token: string | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: string | null;
  login: (email: string, password: string) => Promise<void>;
  register: (nombre: string, email: string, password: string) => Promise<void>;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      isAuthenticated: false,
      loading: false,
      error: null,

      login: async (email, password) => {
        set({ loading: true, error: null });
        try {
          const { token, usuario } = await api.login({ email, password });
          set({ user: usuario, token, isAuthenticated: true, loading: false });
        } catch (err) {
          set({ loading: false, error: 'No se pudo iniciar sesión. Verifica tus credenciales.' });
          throw err;
        }
      },

      register: async (nombre, email, password) => {
        set({ loading: true, error: null });
        try {
          const { token, usuario } = await api.register({ nombre, email, password });
          set({ user: usuario, token, isAuthenticated: true, loading: false });
        } catch (err) {
          set({ loading: false, error: 'No se pudo crear la cuenta. Intenta nuevamente.' });
          throw err;
        }
      },

      logout: () => {
        set({ user: null, token: null, isAuthenticated: false });
      },
    }),
    {
      name: 'deporshop-auth',
      partialize: (state) => ({
        user: state.user,
        token: state.token,
        isAuthenticated: state.isAuthenticated,
      }),
    },
  ),
);
