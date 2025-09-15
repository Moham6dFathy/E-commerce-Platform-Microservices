export interface LoginRequest { username: string; password: string; }
export interface LoginResponse { username: string; accessToken: string; }

export interface RegisterRequest { username: string; email: string; password: string; }
export interface RegisterResponse { username: string; email: string; role: string; accessToken: string; }
