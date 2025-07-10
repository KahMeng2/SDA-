import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import Layout from './components/Layout';
import { Toaster } from './components/ui/toaster';
import { AuthProvider } from './hooks/AuthProvider';

const App: React.FC = () => {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Layout />
        <Toaster />
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
