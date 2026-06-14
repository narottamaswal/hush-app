export interface Item {
  hash: string;
  title: string;
  content: string;
  ownerName: string;
  ownerEmail: string;
  alias: string;
  passwordProtected: boolean;
  viewOnce: boolean;
  viewed: boolean;
  noForward: boolean;
  isExpired: boolean;
  createdAt: string;
  updatedAt: string;
  expiresAt: string | null;
}

export interface User {
  id: number;
  email: string;
  name: string;
  picture: string;
  avatar: string;
}