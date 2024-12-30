// Owner Class
export class Owner {
    id: number;
    username: string;
    email: string;
    password: string;
    phone: string | null;
  
    constructor(
      id: number,
      username: string,
      email: string,
      password: string,
      phone: string | null
    ) {
      this.id = id;
      this.username = username;
      this.email = email;
      this.password = password;
      this.phone = phone;
    }
  } 
  
  export class Application {
    id: number;
    name: string;
    version: string;
    owner: Owner;
  
    constructor(id: number, name: string, version: string, owner: Owner) {
      this.id = id;
      this.name = name;
      this.version = version;
      this.owner = owner;
    }
  }
  