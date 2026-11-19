import {Link,NavLink,useNavigate} from 'react-router-dom';
import {MapPin,LogOut,ShieldCheck,User} from 'lucide-react';
import {useAuth} from '../context/AuthContext';
import {useState} from 'react';
export default function Layout({children}){
 const {user,logout}=useAuth();const nav=useNavigate();
 const [confirmLogout,setConfirmLogout]=useState(false);
 const off=()=>{setConfirmLogout(false);logout();nav('/')};
 return <div className="min-h-screen"><nav className="border-b bg-white"><div className="container-app flex h-16 items-center justify-between">
  <Link to="/" className="flex items-center gap-2 font-bold text-city-navy"><MapPin className="text-city-blue"/>AI Smart City</Link>
  <div className="flex items-center gap-3 text-sm">
   {user?<><NavLink to={user.role==='ADMIN'?'/admin':'/dashboard'} className="rounded-lg px-3 py-2 hover:bg-slate-100">{user.role==='ADMIN'?<ShieldCheck className="inline h-4"/>:<User className="inline h-4"/>} <span className="hidden sm:inline">Dashboard</span></NavLink><button onClick={()=>setConfirmLogout(true)} className="rounded-lg px-3 py-2 hover:bg-red-50 hover:text-red-600" title="Logout"><LogOut className="h-4"/></button></>:<><Link to="/login" className="px-3 py-2">Login</Link><Link to="/register" className="btn-primary">Register</Link></>}
  </div></div></nav><main>{children}</main>
  {confirmLogout&&<div className="fixed inset-0 z-50 grid place-items-center bg-slate-900/40 p-4" role="dialog" aria-modal="true" aria-labelledby="logout-title">
   <div className="card w-full max-w-sm p-6">
    <h2 id="logout-title" className="text-xl font-bold">Confirm logout</h2>
    <p className="mt-2 text-sm text-slate-600">Are you sure you want to log out of your account?</p>
    <div className="mt-6 flex justify-end gap-3">
     <button type="button" className="rounded-lg border px-4 py-2 font-semibold" onClick={()=>setConfirmLogout(false)}>Cancel</button>
     <button type="button" className="rounded-lg bg-red-600 px-4 py-2 font-semibold text-white hover:bg-red-700" onClick={off}>Logout</button>
    </div>
   </div>
  </div>}
  </div>;
}
