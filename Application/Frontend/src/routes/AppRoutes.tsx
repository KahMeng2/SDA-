import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Home from '../pages/home/Home';
import Events from '../pages/events/Events';
import EventDetails from '../pages/events/EventDetails';
import Clubs from '../pages/clubs/Clubs';
import ClubDetails from '../pages/clubs/ClubDetails';
import Faculty from '../pages/faculty/Faculty';
import FundingApplicationDetails from '../pages/faculty/FundingApplicationDetails';
import { useAuth } from '@/hooks/AuthProvider';

const ProtectedFacultyRoute: React.FC<{ element: React.ReactElement }> = ({ element }) => {
  const { user } = useAuth();

  if (user?.role !== 'FACULTY') {
    return <Navigate to="/" replace />;
  }

  return element;
};

const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/events" element={<Events />} />
      <Route path="/events/:id" element={<EventDetails />} />
      <Route path="/clubs" element={<Clubs />} />
      <Route path="/clubs/:id" element={<ClubDetails />} />
      <Route path="/faculty" element={<ProtectedFacultyRoute element={<Faculty />} />} />
      <Route
        path="/funding-applications/:id"
        element={<ProtectedFacultyRoute element={<FundingApplicationDetails />} />}
      />
    </Routes>
  );
};

export default AppRoutes;
