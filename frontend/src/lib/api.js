const BASE_URL = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1').replace(/\/$/, '');

export class ApiError extends Error {
  constructor(message, status, payload) {
    super(message); this.name = 'ApiError'; this.status = status; this.payload = payload;
  }
}

function errorMessage(payload, fallback) {
  const messages = payload?.errors?.errorMessages;
  if (Array.isArray(messages) && messages.length) return messages.join('\n');
  if (typeof messages === 'string' && messages.trim()) return messages;
  if (typeof payload === 'string' && payload.trim()) return payload;
  return fallback;
}

export async function api(path, options = {}) {
  const token = localStorage.getItem('token');
  const headers = {
    ...(options.body ? {'Content-Type': 'application/json'} : {}),
    ...(token ? {Authorization: `Bearer ${token}`} : {}),
    ...options.headers,
  };
  let response;
  try {
    response = await fetch(`${BASE_URL}${path}`, {...options, headers});
  } catch {
    throw new ApiError('Backend serverə qoşulmaq mümkün olmadı. Spring Boot-un işlədiyini yoxla.', 0, null);
  }

  let payload = null;
  if (response.status !== 204) {
    const text = await response.text();
    if (text) { try { payload = JSON.parse(text); } catch { payload = text; } }
  }
  if (!response.ok) throw new ApiError(errorMessage(payload, `Request failed (${response.status})`), response.status, payload);
  return payload?.data ?? null;
}

export const authApi = {
  login: body => api('/auth/login', {method: 'POST', body: JSON.stringify(body)}),
  register: body => api('/auth/register', {method: 'POST', body: JSON.stringify(body)}),
};

export const userApi = {
  me: () => api('/users/me'),
  list: (page=0, size=5, sort='id') => api(`/users?pageNumber=${page}&pageSize=${size}&sort=${encodeURIComponent(sort)}`),
  get: id => api(`/users/${id}`),
  create: (body, role='USER') => api(`/users?role=${encodeURIComponent(role)}`, {method:'POST', body:JSON.stringify(body)}),
  update: async (id, body) => {
    const result = await api(`/users/${id}`, {method:'PUT', body:JSON.stringify(body)});
    // When the authenticated user changes their own username, the backend
    // returns a replacement JWT whose subject contains the new username.
    // Store it before any follow-up authenticated request is made.
    if (result?.token) localStorage.setItem('token', result.token);
    return result;
  },
  delete: id => api(`/users/${id}`, {method:'DELETE'}),
};

export const taskApi = {
  mine: (page=0, size=6) => api(`/tasks/my-tasks?pageNumber=${page}&pageSize=${size}`),
  list: ({status='', page=0, size=6, sort='id'}={}) => api(`/tasks/list?pageNumber=${page}&pageSize=${size}&sort=${encodeURIComponent(sort)}${status ? `&status=${encodeURIComponent(status)}` : ''}`),
  search: ({userId='', status='', title='', page=0, size=6, sort='id'}={}) => {
    const p = new URLSearchParams({pageNumber:String(page), pageSize:String(size), sort});
    if (userId !== '') p.set('user_id', userId);
    if (status) p.set('status', status);
    if (title.trim()) p.set('title', title.trim());
    return api(`/tasks/search?${p.toString()}`);
  },
  byUser: (id, {status='', page=0, size=6, sort='id'}={}) => api(`/tasks/user-tasks/${id}?pageNumber=${page}&pageSize=${size}&sort=${encodeURIComponent(sort)}${status ? `&status=${encodeURIComponent(status)}` : ''}`),
  create: body => api('/tasks', {method:'POST', body:JSON.stringify(body)}),
  createForUser: (id, body) => api(`/tasks/admin/${id}`, {method:'POST', body:JSON.stringify(body)}),
  get: id => api(`/tasks/${id}`),
  update: (id, body) => api(`/tasks/${id}`, {method:'PUT', body:JSON.stringify(body)}),
  status: (id, status) => api(`/tasks/${id}/status?status=${encodeURIComponent(status)}`, {method:'PATCH'}),
  delete: id => api(`/tasks/${id}`, {method:'DELETE'}),
};
