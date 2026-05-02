const TokenKey = "Admin-Token";
const RememberKey = "Admin-Remember";

export function getToken(): string | null {
  return localStorage.getItem(TokenKey);
}

export function setToken(token: string, remember = false): void {
  if (remember) {
    localStorage.setItem(TokenKey, token);
  } else {
    sessionStorage.setItem(TokenKey, token);
  }
}

export function removeToken(): void {
  localStorage.removeItem(TokenKey);
  sessionStorage.removeItem(TokenKey);
  localStorage.removeItem(RememberKey);
}

export function setRememberedUser(user: {
  username: string;
  password: string;
}): void {
  const data = JSON.stringify({
    username: user.username,
    password: user.password,
  });
  localStorage.setItem(RememberKey, btoa(data));
}

export function getRememberedUser(): {
  username: string;
  password: string;
} | null {
  const data = localStorage.getItem(RememberKey);
  if (!data) return null;

  try {
    return JSON.parse(atob(data));
  } catch {
    return null;
  }
}

export function clearRememberedUser(): void {
  localStorage.removeItem(RememberKey);
}
