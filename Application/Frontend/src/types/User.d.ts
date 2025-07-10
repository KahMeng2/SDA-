interface User {
  authorities: string[];
  id: number;
  email: string;
  username: string;
  firstName: string;
  middleName: string;
  lastName: string;
  dob: number;
  role: string;
  administratedClubs: any[]; // You might want to define a more specific type for this
}

export { User };
