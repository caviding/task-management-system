import {ChevronLeft,ChevronRight,X} from 'lucide-react';
export function PageHeader({title,subtitle,action}){return <div className="page-header"><div><h1>{title}</h1><p>{subtitle}</p></div>{action}</div>}
export function Status({value}){return <span className={`status ${value?.toLowerCase()}`}>{value?.replace('_',' ')}</span>}
export function Empty({text='Nothing here yet.'}){return <div className="empty">{text}</div>}
export function Pagination({page,totalPages,onChange}){if(totalPages<=1)return null;return <div className="pagination"><button disabled={page===0} onClick={()=>onChange(page-1)}><ChevronLeft size={16}/></button><span>Page {page+1} of {totalPages}</span><button disabled={page+1>=totalPages} onClick={()=>onChange(page+1)}><ChevronRight size={16}/></button></div>}
export function Modal({title,onClose,children}){return <div className="modal-backdrop" onMouseDown={e=>e.target===e.currentTarget&&onClose()}><div className="modal"><div className="modal-head"><h2>{title}</h2><button className="icon-btn" onClick={onClose}><X/></button></div>{children}</div></div>}
export function Notice({message,type='error'}){return message?<div className={`notice ${type}`}>{message}</div>:null}
