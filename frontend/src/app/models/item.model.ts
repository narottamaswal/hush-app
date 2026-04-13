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
  createdAt: string;
  updatedAt: string;
}

export interface User {
  id: number;
  email: string;
  name: string;
  picture: string;
  avatar: string;
}