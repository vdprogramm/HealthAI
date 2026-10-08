import { BrowserRouter, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout'
import RoleRoute from './components/RoleRoute'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import AiTriagePage from './pages/AiTriagePage'
import AppointmentsPage from './pages/AppointmentsPage'
import MedicalRecordPage from './pages/MedicalRecordPage'
import DoctorsPage from './pages/DoctorsPage'
import AppointmentDetailPage from './pages/AppointmentDetailPage'
import AdminDashboardPage from './pages/AdminDashboardPage'
import DoctorDashboardPage from './pages/DoctorDashboardPage'
import './styles/portal.css'
export default function App() {
  return <BrowserRouter><Routes>
    <Route path="/login" element={<LoginPage />} />
    <Route path="/register" element={<RegisterPage />} />
    <Route element={<RoleRoute allowed={['PATIENT']} />}>
      <Route element={<Layout />}>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/ai" element={<AiTriagePage />} />
        <Route path="/appointments" element={<AppointmentsPage />} />
        <Route path="/doctors" element={<DoctorsPage />} />
        <Route path="/appointments/:id" element={<AppointmentDetailPage />} />
        <Route path="/medical-record/:appointmentId" element={<MedicalRecordPage />} />
      </Route>
    </Route>
    <Route element={<RoleRoute allowed={['ADMIN']} />}>
      <Route path="/admin/dashboard" element={<AdminDashboardPage />} />
    </Route>
    <Route element={<RoleRoute allowed={['DOCTOR']} />}>
      <Route path="/doctor/dashboard" element={<DoctorDashboardPage />} />
    </Route>
  </Routes></BrowserRouter>
}
