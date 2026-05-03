import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'
import Login from '@/pages/Login'
import Layout from '@/components/Layout'
import Dashboard from '@/pages/Dashboard'
import Residents from '@/pages/Residents'
import HealthRecords from '@/pages/HealthRecords'
import Appointments from '@/pages/Appointments'
import MedicalRecords from '@/pages/MedicalRecords'
import MedicationReminders from '@/pages/MedicationReminders'
import Communities from '@/pages/Communities'
import UserManagement from '@/pages/UserManagement'

const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated } = useAuthStore()
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />
}

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="residents" element={<Residents />} />
        <Route path="health-records" element={<HealthRecords />} />
        <Route path="appointments" element={<Appointments />} />
        <Route path="medical-records" element={<MedicalRecords />} />
        <Route path="medication-reminders" element={<MedicationReminders />} />
        <Route path="communities" element={<Communities />} />
        <Route path="users" element={<UserManagement />} />
      </Route>
    </Routes>
  )
}

export default App
