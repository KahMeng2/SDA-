import React, { createContext, useContext, useState, useEffect } from 'react';
import axiosInstance from '@/api/apiConfig';

interface User {
  id: number;
  email: string;
  username: string;
  firstName: string;
  middleName: string;
  lastName: string;
  dob: number;
  role: string;
  studentId: string | null;
  administratedClubs: Array<{
    id: number;
    name: string;
    balance: number;
  }>;
  authorities: string[];
}

interface AuthContextType {
  user: User | null;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, []);

  const login = async (username: string, password: string) => {
    try {
      const response = await axiosInstance.post('/auth/login', { username, password });
      const { user, token } = response.data;

      localStorage.setItem('authToken', token);
      localStorage.setItem('user', JSON.stringify({ ...user }));
      setUser({ ...user });
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    setUser(null);
  };

  const value = {
    user,
    login,
    logout,
    isAuthenticated: !!user,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
