import {useEffect,useState} from 'react';
import {Plus,Pencil,Trash2} from 'lucide-react';
import {userApi} from '../lib/api';
import {Empty,Modal,Notice,PageHeader,Pagination} from '../components/UI';

export default function UsersPage(){
 const [page,setPage]=useState(0),[data,setData]=useState(null),[modal,setModal]=useState(null),[error,setError]=useState('');
 async function load(){try{setData(await userApi.list(page,6));setError('')}catch(e){setError(e.message)}}
 useEffect(()=>{load()},[page]);
 async function remove(id){if(!confirm('Delete this user and associated tasks?'))return;try{await userApi.delete(id);await load()}catch(e){setError(e.message)}}
 return <div className="page"><PageHeader title="Users" subtitle="Admin-only user management." action={<button className="primary" onClick={()=>setModal({type:'create'})}><Plus size={17}/>Create user</button>}/><Notice message={error}/><section className="card table-card">{data?.content?.length?<table><thead><tr><th>User</th><th>Email</th><th>Tasks</th><th>ID</th><th></th></tr></thead><tbody>{data.content.map(u=><tr key={u.id}><td><b>{u.username}</b></td><td>{u.email}</td><td>{u.tasks?.length??0}</td><td>#{u.id}</td><td className="actions"><button className="icon-btn" onClick={()=>setModal({type:'edit',user:u})} title="Edit"><Pencil size={17}/></button><button className="icon-btn danger" onClick={()=>remove(u.id)} title="Delete"><Trash2 size={17}/></button></td></tr>)}</tbody></table>:<Empty text="No users found."/>}<Pagination page={data?.number||0} totalPages={data?.totalPages||0} onChange={setPage}/></section>{modal?.type==='create'&&<CreateUser onClose={()=>setModal(null)} onSaved={()=>{setModal(null);load()}}/>}{modal?.type==='edit'&&<EditUser user={modal.user} onClose={()=>setModal(null)} onSaved={()=>{setModal(null);load()}}/>}</div>
}

function CreateUser({onClose,onSaved}){
 const [form,setForm]=useState({username:'',email:'',password:'',role:'USER'}),[error,setError]=useState(''),[busy,setBusy]=useState(false);
 async function submit(e){e.preventDefault();setBusy(true);setError('');try{await userApi.create({username:form.username,email:form.email,password:form.password},form.role);onSaved()}catch(e){setError(e.message)}finally{setBusy(false)}}
 return <Modal title="Create user" onClose={onClose}><form className="form" onSubmit={submit}><Notice message={error}/><label>Username<input required minLength="3" maxLength="20" value={form.username} onChange={e=>setForm({...form,username:e.target.value})}/></label><label>Email<input required type="email" value={form.email} onChange={e=>setForm({...form,email:e.target.value})}/></label><label>Password<input required type="password" minLength="6" maxLength="20" value={form.password} onChange={e=>setForm({...form,password:e.target.value})}/></label><label>Role<select value={form.role} onChange={e=>setForm({...form,role:e.target.value})}><option>USER</option><option>ADMIN</option><option>MODERATOR</option></select></label><div className="form-actions"><button type="button" className="secondary" onClick={onClose}>Cancel</button><button className="primary" disabled={busy}>{busy?'Creating…':'Create'}</button></div></form></Modal>
}

function EditUser({user,onClose,onSaved}){
 const [form,setForm]=useState({username:user.username,email:user.email}),[error,setError]=useState(''),[busy,setBusy]=useState(false);
 async function submit(e){e.preventDefault();setBusy(true);setError('');try{await userApi.update(user.id,form);onSaved()}catch(e){setError(e.message)}finally{setBusy(false)}}
 return <Modal title={`Edit ${user.username}`} onClose={onClose}><form className="form" onSubmit={submit}><Notice message={error}/><label>Username<input required minLength="3" maxLength="20" value={form.username} onChange={e=>setForm({...form,username:e.target.value})}/></label><label>Email<input required type="email" value={form.email} onChange={e=>setForm({...form,email:e.target.value})}/></label><div className="form-actions"><button type="button" className="secondary" onClick={onClose}>Cancel</button><button className="primary" disabled={busy}>{busy?'Saving…':'Save'}</button></div></form></Modal>
}
