import {createContext,useContext,useMemo,useState} from 'react';
import api from '../services/api';
const AuthContext=createContext(null);
export function AuthProvider({children}){
 const [user,setUser]=useState(()=>JSON.parse(localStorage.getItem('city_user')||'null'));
 const login=async data=>{const r=await api.post('/auth/login',data);localStorage.setItem('city_token',r.data.token);localStorage.setItem('city_user',JSON.stringify(r.data));setUser(r.data);return r.data};
 const register=async data=>{const r=await api.post('/auth/register',data);localStorage.setItem('city_token',r.data.token);localStorage.setItem('city_user',JSON.stringify(r.data));setUser(r.data);return r.data};
 const logout=()=>{localStorage.removeItem('city_token');localStorage.removeItem('city_user');setUser(null)};
 return <AuthContext.Provider value={useMemo(()=>({user,login,register,logout}),[user])}>{children}</AuthContext.Provider>;
}
export const useAuth=()=>useContext(AuthContext);
