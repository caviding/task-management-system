import {createContext,useContext,useEffect,useState} from 'react';
import {authApi,userApi,ApiError} from '../lib/api';
const AuthContext=createContext(null);
export function AuthProvider({children}){
 const [user,setUser]=useState(null),[role,setRole]=useState(null),[loading,setLoading]=useState(true);
 async function hydrate(){
  if(!localStorage.getItem('token')){setLoading(false);return;}
  try{
   const me=await userApi.me(); setUser(me);
   try { await userApi.list(0,1); setRole('ADMIN'); } catch(e){ if(e instanceof ApiError && (e.status===403||e.status===401)) setRole('USER'); else throw e; }
  } catch { localStorage.removeItem('token'); setUser(null);setRole(null); }
  finally{setLoading(false)}
 }
 useEffect(()=>{hydrate()},[]);
 async function authenticate(mode,values){const data=mode==='login'?await authApi.login(values):await authApi.register(values);localStorage.setItem('token',data.token);setLoading(true);await hydrate();}
 function logout(){localStorage.removeItem('token');setUser(null);setRole(null)}
 return <AuthContext.Provider value={{user,role,loading,authenticate,logout,refresh:hydrate}}>{children}</AuthContext.Provider>
}
export const useAuth=()=>useContext(AuthContext);
