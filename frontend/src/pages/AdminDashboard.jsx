import {useEffect,useState} from 'react';
import api from '../services/api';
import {BarChart,Bar,XAxis,YAxis,Tooltip,ResponsiveContainer,PieChart,Pie,Cell} from 'recharts';
import {Link} from 'react-router-dom';
const colors=['#2563eb','#0d9488','#f59e0b','#ef4444','#8b5cf6','#64748b'];
const chart=(obj)=>Object.entries(obj||{}).map(([name,value])=>({name,value}));
export default function AdminDashboard(){
 const [stats,setStats]=useState(null);
 useEffect(()=>{api.get('/admin/stats').then(r=>setStats(r.data))},[]);
 if(!stats)return <div className="container-app py-10">Loading dashboard...</div>;
 return <div className="container-app py-8"><div className="flex flex-wrap justify-between gap-3"><h1 className="text-3xl font-bold">Admin dashboard</h1><div className="flex gap-2"><Link to="/admin/issues" className="btn-primary">Manage Issues</Link><Link to="/admin/users" className="rounded-lg border px-4 py-2.5 font-semibold">Manage Users</Link></div></div><div className="mt-6 grid gap-4 sm:grid-cols-3"><div className="card p-5"><p className="text-sm text-slate-500">Total issues</p><p className="mt-2 text-3xl font-bold">{stats.totals.issues}</p></div><div className="card p-5"><p className="text-sm text-slate-500">Pending</p><p className="mt-2 text-3xl font-bold text-amber-600">{stats.totals.pending}</p></div><div className="card p-5"><p className="text-sm text-slate-500">Resolved</p><p className="mt-2 text-3xl font-bold text-emerald-600">{stats.totals.resolved}</p></div></div><div className="mt-6 grid gap-6 lg:grid-cols-2"><div className="card p-5"><h2 className="font-bold">Issues by status</h2><div className="h-72 mt-4"><ResponsiveContainer><BarChart data={chart(stats.byStatus)}><XAxis dataKey="name"/><YAxis/><Tooltip/><Bar dataKey="value" fill="#2563eb"/></BarChart></ResponsiveContainer></div></div><div className="card p-5"><h2 className="font-bold">Issues by category</h2><div className="h-72 mt-4"><ResponsiveContainer><PieChart><Pie data={chart(stats.byCategory)} dataKey="value" nameKey="name" label>{chart(stats.byCategory).map((_,i)=><Cell key={i} fill={colors[i%colors.length]}/>)}</Pie><Tooltip/></PieChart></ResponsiveContainer></div></div></div></div>
}
