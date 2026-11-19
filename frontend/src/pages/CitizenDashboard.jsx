import {useEffect,useState} from 'react';
import {Link} from 'react-router-dom';
import api from '../services/api';
import {Plus,MapPin} from 'lucide-react';
const badge={SUBMITTED:'bg-slate-100 text-slate-700',VERIFIED:'bg-blue-100 text-blue-700',ASSIGNED:'bg-indigo-100 text-indigo-700',IN_PROGRESS:'bg-amber-100 text-amber-700',RESOLVED:'bg-emerald-100 text-emerald-700',REJECTED:'bg-red-100 text-red-700'};
export default function CitizenDashboard(){
 const [issues,setIssues]=useState([]),[loading,setLoading]=useState(true);
 useEffect(()=>{api.get('/issues/my').then(r=>setIssues(r.data)).finally(()=>setLoading(false))},[]);
 return <div className="container-app py-8"><div className="flex items-center justify-between"><div><h1 className="text-3xl font-bold">My issues</h1><p className="mt-1 text-slate-600">Track reports submitted by you</p></div><Link to="/report" className="btn-primary"><Plus className="mr-2 h-4"/>Report Issue</Link></div>{loading?<p className="mt-10">Loading issues...</p>:issues.length===0?<div className="card mt-8 p-12 text-center text-slate-500">No issues reported yet.</div>:<div className="mt-8 grid gap-5 md:grid-cols-2">{issues.map(i=><Link key={i.id} to={`/issues/${i.id}`} className="card p-5 transition hover:-translate-y-1"><div className="flex justify-between"><h2 className="font-bold">{i.title}</h2><span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${badge[i.status]}`}>{i.status}</span></div><p className="mt-2 line-clamp-2 text-sm text-slate-600">{i.description}</p><div className="mt-4 flex items-center gap-2 text-xs text-slate-500"><MapPin className="h-3"/>{i.locationText}<span className="ml-auto rounded bg-slate-100 px-2 py-1">{i.aiPriority}</span></div></Link>)}</div>}</div>
}
