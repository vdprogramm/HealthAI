export type Role = 'ADMIN' | 'DOCTOR' | 'PATIENT'
export function getRole(): Role | null {
  const token = localStorage.getItem('token')
  if (!token) return null
  try {
    const payload = token.split('.')[1]
    const json = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')))
    if (typeof json.exp !== 'number' || json.exp * 1000 <= Date.now()) return null
    const role = json.role ?? 'PATIENT'
    return role === 'ADMIN' || role === 'DOCTOR' || role === 'PATIENT' ? role : null
  } catch { return null }
}
export function homeFor(role: Role | null): string {
  return role === 'ADMIN' ? '/admin/dashboard' : role === 'DOCTOR' ? '/doctor/dashboard' : '/'
}
