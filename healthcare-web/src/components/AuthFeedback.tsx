import { CheckCircle2, XCircle, LoaderCircle } from 'lucide-react'
type Props = { kind: 'success' | 'error' | 'loading'; title: string; message: string; onContinue?: () => void }
export default function AuthFeedback({ kind, title, message, onContinue }: Props) {
  return <div className="auth-feedback-backdrop" role="presentation"><div className={'auth-feedback auth-feedback-' + kind} role={kind === 'error' ? 'alertdialog' : 'dialog'} aria-modal="true" aria-label={title}>
    {kind === 'success' ? <CheckCircle2 size={44}/> : kind === 'error' ? <XCircle size={44}/> : <LoaderCircle size={44} className="auth-spin"/>}
    <h2>{title}</h2><p>{message}</p>
    {onContinue && <button type="button" className="primary-button" onClick={onContinue}>{kind === 'success' ? 'Tiếp tục' : 'Đóng'}</button>}
  </div></div>
}
