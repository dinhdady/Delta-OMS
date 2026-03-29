@Injectable({ providedIn: 'root' })
export class ProductService {

  private api = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<any>(this.api);
  }

  create(data: any) {
    return this.http.post<any>(this.api, data);
  }
}
