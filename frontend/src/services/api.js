import axios from 'axios';
const productionApiUrl='https://civic-ai-2l4v.onrender.com/api';
const api=axios.create({baseURL:import.meta.env.VITE_API_URL||(import.meta.env.PROD?productionApiUrl:'/api')});
api.interceptors.request.use(c=>{const token=localStorage.getItem('city_token');if(token)c.headers.Authorization=`Bearer ${token}`;return c});
api.interceptors.response.use(r=>r,e=>{if(e.response?.status===401){localStorage.removeItem('city_token');localStorage.removeItem('city_user')}return Promise.reject(e)});
export default api;
