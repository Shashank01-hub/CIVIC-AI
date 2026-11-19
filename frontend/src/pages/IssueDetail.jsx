import {useEffect,useState} from 'react';
import {Link,useLocation,useParams} from 'react-router-dom';
import api from '../services/api';
import {CheckCircle2,MapPin,User,Calendar,Image as ImageIcon} from 'lucide-react';

export default function IssueDetail(){
  const {id}=useParams();
  const location=useLocation();
  const backLink=location.pathname.startsWith('/admin')?'/admin/issues':'/dashboard';
  const [issue,setIssue]=useState(null);
  useEffect(()=>{api.get(`/issues/${id}`).then(r=>setIssue(r.data))},[id]);
  if(!issue)return <div className="container-app py-10">Loading issue details...</div>;

  const imageUrl=issue.imageUrl
    ?(issue.imageUrl.startsWith('/uploads')?`${window.location.origin}${issue.imageUrl}`:issue.imageUrl)
    :null;

  return <div className="container-app py-8">
    <Link to={backLink} className="text-sm text-blue-600">← Back</Link>
    <div className="mt-4 grid gap-6 lg:grid-cols-[1fr_360px]">
      <div className="card p-6">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div><p className="text-sm text-slate-500">Issue #{issue.id}</p><h1 className="mt-1 text-3xl font-bold">{issue.title}</h1></div>
          <span className="rounded-full bg-slate-100 px-3 py-1 text-sm font-semibold">{issue.status}</span>
        </div>
        <p className="mt-5 text-slate-700">{issue.description}</p>

        <div className="mt-6 grid gap-4 rounded-xl bg-slate-50 p-4 sm:grid-cols-2">
          <div className="flex gap-2"><MapPin className="h-5 text-city-blue"/><div><p className="text-xs text-slate-500">Location</p><p className="font-semibold">{issue.locationText}</p>{issue.latitude!=null&&<p className="text-xs text-slate-500">{issue.latitude}, {issue.longitude}</p>}</div></div>
          <div className="flex gap-2"><User className="h-5 text-city-blue"/><div><p className="text-xs text-slate-500">Reported by</p><p className="font-semibold">{issue.reporter?.name}</p><p className="text-xs text-slate-500">{issue.reporter?.email}</p></div></div>
          <div className="flex gap-2"><Calendar className="h-5 text-city-blue"/><div><p className="text-xs text-slate-500">Submitted</p><p className="font-semibold">{new Date(issue.createdAt).toLocaleString()}</p></div></div>
          <div className="flex gap-2"><Calendar className="h-5 text-city-blue"/><div><p className="text-xs text-slate-500">Last updated</p><p className="font-semibold">{new Date(issue.updatedAt).toLocaleString()}</p></div></div>
        </div>

        <h2 className="mt-8 flex items-center gap-2 text-xl font-bold"><ImageIcon className="h-5"/>Reported image</h2>
        {imageUrl?<a href={imageUrl} target="_blank" rel="noreferrer"><img className="mt-4 max-h-[520px] w-full rounded-xl border object-contain bg-slate-100" src={imageUrl} alt="Reported issue"/></a>:<div className="mt-4 rounded-xl border border-dashed p-8 text-center text-slate-500">No image uploaded</div>}

        <h2 className="mt-8 text-xl font-bold">Status timeline</h2>
        <div className="mt-5 space-y-5">{issue.history?.length?issue.history.map(h=><div key={h.id} className="relative border-l-2 border-blue-200 pl-6"><CheckCircle2 className="absolute -left-3 top-0 h-6 rounded-full bg-white text-city-blue"/><p className="font-semibold">{h.oldStatus} → {h.newStatus}</p><p className="text-sm text-slate-600">{h.comment}</p><p className="mt-1 text-xs text-slate-400">By {h.updatedBy} · {new Date(h.createdAt).toLocaleString()}</p></div>):<p className="text-sm text-slate-500">No status updates recorded.</p>}</div>
      </div>

      <aside className="card h-fit p-6">
        <h2 className="text-lg font-bold">AI assessment</h2>
        <dl className="mt-4 space-y-4 text-sm">
          <div><dt className="text-slate-500">Category</dt><dd className="font-semibold">{issue.aiCategory||'Not classified'}</dd></div>
          <div><dt className="text-slate-500">Priority</dt><dd className="font-semibold">{issue.aiPriority||'Not classified'}</dd></div>
          <div><dt className="text-slate-500">Suggested department</dt><dd className="font-semibold">{issue.aiSuggestedDepartment?.name||'Not suggested'}</dd></div>
          <div><dt className="text-slate-500">Assigned department</dt><dd className="font-semibold">{issue.assignedDepartment?.name||'Not assigned'}</dd></div>
          <div><dt className="text-slate-500">AI summary</dt><dd className="mt-1 text-slate-700">{issue.aiSummary||'No summary available'}</dd></div>
        </dl>
      </aside>
    </div>
  </div>;
}
