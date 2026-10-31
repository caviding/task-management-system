import {useEffect,useState} from 'react';
import {Plus,Pencil,Trash2} from 'lucide-react';
import {useAuth} from '../context/AuthContext';
import {taskApi} from '../lib/api';
import {Empty,Modal,Notice,PageHeader,Pagination,Status} from '../components/UI';

const blank={title:'',description:'',status:'TODO'};

export default function TasksPage(){
  const {role}=useAuth();
  const [page,setPage]=useState(0),[data,setData]=useState(null),[status,setStatus]=useState('');
  const [title,setTitle]=useState(''),[userId,setUserId]=useState('');
  const [modal,setModal]=useState(null),[error,setError]=useState('');

  async function load(){
    try{
      setError('');
      if(role==='ADMIN') setData(await taskApi.search({status,title,userId,page,size:6}));
      else setData(await taskApi.mine(page,6));
    }catch(e){setError(e.message)}
  }
  useEffect(()=>{load()},[page,status,role]);

  async function remove(id){
    if(!confirm('Delete this task?'))return;
    try{await taskApi.delete(id);await load()}catch(e){setError(e.message)}
  }
  function applySearch(e){e.preventDefault();setPage(0);load()}
  function clearSearch(){setStatus('');setTitle('');setUserId('');setPage(0);setTimeout(load,0)}

  return <div className="page">
    <PageHeader title="Tasks" subtitle={role==='ADMIN'?'Search and manage tasks in the system.':'Manage your personal tasks.'} action={<button className="primary" onClick={()=>setModal({type:'create',task:blank})}><Plus size={17}/>New task</button>}/>
    <Notice message={error}/>
    {role==='ADMIN'&&<form className="filters" onSubmit={applySearch}>
      <input type="number" min="1" placeholder="User ID" value={userId} onChange={e=>setUserId(e.target.value)}/>
      <input placeholder="Title contains…" value={title} onChange={e=>setTitle(e.target.value)}/>
      <select value={status} onChange={e=>{setStatus(e.target.value);setPage(0)}}><option value="">All statuses</option><option>TODO</option><option>IN_PROGRESS</option><option>DONE</option></select>
      <button className="secondary" type="submit">Search</button><button className="secondary" type="button" onClick={clearSearch}>Clear</button>
    </form>}
    <section className="card table-card">{data?.content?.length?<table><thead><tr><th>Task</th><th>Status</th><th>ID</th><th></th></tr></thead><tbody>{data.content.map(t=><tr key={t.id}><td><b>{t.title}</b><small>{t.description}</small></td><td><Status value={t.status}/></td><td>#{t.id}</td><td className="actions"><button className="icon-btn" onClick={()=>setModal({type:'edit',task:t})} title="Edit"><Pencil size={17}/></button><button className="icon-btn danger" onClick={()=>remove(t.id)} title="Delete"><Trash2 size={17}/></button></td></tr>)}</tbody></table>:<Empty text="No tasks found."/>}<Pagination page={data?.number||0} totalPages={data?.totalPages||0} onChange={setPage}/></section>
    {modal&&<TaskModal initial={modal.task} mode={modal.type} role={role} onClose={()=>setModal(null)} onSaved={()=>{setModal(null);load()}}/>}
  </div>
}

function TaskModal({initial,mode,role,onClose,onSaved}){
  const [form,setForm]=useState({...initial}),[targetUserId,setTargetUserId]=useState(''),[error,setError]=useState(''),[busy,setBusy]=useState(false);
  async function submit(e){
    e.preventDefault();setBusy(true);setError('');
    try{
      if(mode==='create'){
        if(role==='ADMIN'&&targetUserId) await taskApi.createForUser(targetUserId,form);
        else await taskApi.create(form);
      } else await taskApi.update(initial.id,form);
      onSaved();
    }catch(e){setError(e.message)}finally{setBusy(false)}
  }
  return <Modal title={mode==='create'?'Create task':'Edit task'} onClose={onClose}><form className="form" onSubmit={submit}><Notice message={error}/>
    {mode==='create'&&role==='ADMIN'&&<label>Assign to user ID <small>(leave empty to assign to yourself)</small><input type="number" min="1" value={targetUserId} onChange={e=>setTargetUserId(e.target.value)}/></label>}
    <label>Title<input required minLength="3" maxLength="20" value={form.title} onChange={e=>setForm({...form,title:e.target.value})}/></label>
    <label>Description<textarea required minLength="10" maxLength="255" rows="5" value={form.description} onChange={e=>setForm({...form,description:e.target.value})}/></label>
    <label>Status<select value={form.status} onChange={e=>setForm({...form,status:e.target.value})}><option>TODO</option><option>IN_PROGRESS</option><option>DONE</option></select></label>
    <div className="form-actions"><button type="button" className="secondary" onClick={onClose}>Cancel</button><button className="primary" disabled={busy}>{busy?'Saving…':'Save task'}</button></div>
  </form></Modal>
}
