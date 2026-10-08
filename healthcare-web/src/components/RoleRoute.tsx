import { Navigate, Outlet } from 'react-router-dom'
import { getRole, homeFor, type Role } from '../auth/roles'
export default function RoleRoute({ allowed }: { allowed: Role[] }) {
  const role = getRole()
  if (!role) return <Navigate to="/login" replace />
  if (!allowed.includes(role)) return <Navigate to={homeFor(role)} replace />
  return <Outlet />
}
