export function getUserIdFromToken(): number | null {
  const token = localStorage.getItem('accessToken');
  if (!token) return null;

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.userId ?? null;
  } catch (e) {
    console.error('Invalid JWT', e);
    return null;
  }
}