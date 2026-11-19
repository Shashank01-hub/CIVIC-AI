import {Routes,Route} from 'react-router-dom';
import Layout from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';
import Landing from './pages/Landing';
import {Login,Register} from './pages/AuthPages';
import CitizenDashboard from './pages/CitizenDashboard';
import ReportIssue from './pages/ReportIssue';
import IssueDetail from './pages/IssueDetail';
import AdminDashboard from './pages/AdminDashboard';
import AdminIssues from './pages/AdminIssues';
import AdminUsers from './pages/AdminUsers';
function NotFound(){return <div className="grid min-h-[70vh] place-items-center text-center"><div><p className="text-6xl font-bold text-slate-300">404</p><h1 className="mt-2 text-2xl font-bold">Page not found</h1></div></div>}
export default function App(){return <Layout><Routes>
 <Route path="/" element={<Landing/>}/><Route path="/login" element={<Login/>}/><Route path="/register" element={<Register/>}/>
 <Route path="/dashboard" element={<ProtectedRoute><CitizenDashboard/></ProtectedRoute>}/><Route path="/report" element={<ProtectedRoute><ReportIssue/></ProtectedRoute>}/><Route path="/issues/:id" element={<ProtectedRoute><IssueDetail/></ProtectedRoute>}/>
 <Route path="/admin" element={<ProtectedRoute admin><AdminDashboard/></ProtectedRoute>}/><Route path="/admin/issues" element={<ProtectedRoute admin><AdminIssues/></ProtectedRoute>}/>
 <Route path="/admin/users" element={<ProtectedRoute admin><AdminUsers/></ProtectedRoute>}/><Route path="/admin/issues/:id" element={<ProtectedRoute admin><IssueDetail/></ProtectedRoute>}/>
 <Route path="*" element={<NotFound/>}/>
</Routes></Layout>}
