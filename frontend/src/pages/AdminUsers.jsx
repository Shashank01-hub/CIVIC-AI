import {useEffect,useState} from 'react';
import api from '../services/api';
import toast from 'react-hot-toast';

export default function AdminUsers(){
  const [users,setUsers]=useState([]),[loading,setLoading]=useState(true);
  const load=()=>api.get('/admin/users').then(r=>setUsers(r.data)).finally(()=>setLoading(false));
  useEffect(()=>{load()},[]);

  const changeRole=async(user,role)=>{
    try{
      await api.put(`/admin/users/${user.id}/role`,{role});
      toast.success(`${user.name} is now ${role}`);
      load();
    }catch(e){toast.error(e.response?.data?.messages?.[0]||'Could not update role')}
  };
  const deleteUser=async user=>{
    if(!window.confirm(`Delete ${user.name}?`))return;
    try{
      await api.delete(`/admin/users/${user.id}`);
      toast.success('User deleted');
      load();
    }catch(e){toast.error(e.response?.data?.messages?.[0]||'Could not delete user')}
  };

  return <div className="container-app py-8">
    <h1 className="text-3xl font-bold">User management</h1>
    <p className="mt-1 text-slate-600">View registered users and manage account roles</p>
    {loading?<p className="mt-8">Loading users...</p>:<div className="card mt-6 overflow-x-auto">
      <table className="w-full text-left text-sm">
        <thead className="border-b bg-slate-50"><tr>{['User','Role','Issues','Registered','Actions'].map(h=><th className="p-3" key={h}>{h}</th>)}</tr></thead>
        <tbody>{users.map(u=><tr className="border-b" key={u.id}>
          <td className="p-3"><p className="font-semibold">{u.name}</p><p className="text-xs text-slate-500">{u.email}</p></td>
          <td className="p-3"><span className={`rounded-full px-2 py-1 text-xs font-semibold ${u.role==='ADMIN'?'bg-purple-100 text-purple-700':'bg-slate-100 text-slate-700'}`}>{u.role}</span></td>
          <td className="p-3">{u.issueCount}</td>
          <td className="p-3">{new Date(u.createdAt).toLocaleString()}</td>
          <td className="p-3"><div className="flex flex-wrap gap-2">
            {u.role==='ADMIN'
              ?<button className="rounded border px-2 py-1" onClick={()=>changeRole(u,'CITIZEN')}>Make citizen</button>
              :<button className="rounded border px-2 py-1" onClick={()=>changeRole(u,'ADMIN')}>Make admin</button>}
            <button className="rounded border border-red-200 px-2 py-1 text-red-600" onClick={()=>deleteUser(u)} disabled={u.issueCount>0}>Delete</button>
          </div></td>
        </tr>)}</tbody>
      </table>
    </div>}
  </div>;
}
