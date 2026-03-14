export interface Item {
  hash: string;
  title: string;
  content: string;
  ownerName: string;
  ownerEmail: string;
  passwordProtected: boolean;
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