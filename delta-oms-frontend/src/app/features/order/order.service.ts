@Injectable({ providedIn: 'root' })
export class OrderService {

  private api = 'http://localhost:8080/api/orders';

  constructor(private http: HttpClient) {}

  create(order: any) {
    return this.http.post<any>(this.api, order);
  }

  getByCustomer(id: number) {
    return this.http.get<any>(`${this.api}/customer/${id}`);
  }
}
