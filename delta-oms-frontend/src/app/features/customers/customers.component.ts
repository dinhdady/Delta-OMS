import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

export interface CustomerType {
  id: number;
  name: string;
  description?: string;
}

export interface Customer {
  id: number;
  code: string;
  name: string;
  phone: string;
  email: string;
  taxCode: string;
  customerTypeName: string;
}

export interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
}

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customers.component.html',
  styleUrls: ['./customers.component.scss']
})
export class CustomersComponent implements OnInit {
  customers: Customer[] = [];
  customerTypes: CustomerType[] = [];

  // Pagination
  currentPage: number = 0;
  pageSize: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  sortBy: string = 'id';

  // Search/filter
  searchTerm: string = '';
  filteredCustomers: Customer[] = [];

  // UI state
  loading: boolean = false;
  showModal: boolean = false;
  isEditing: boolean = false;
  selectedCustomer: Customer | null = null;

  // Form data
  customerForm: {
    code: string;
    name: string;
    phone: string;
    email: string;
    taxCode: string;
    customerTypeId: number | null;
  } = {
    code: '',
    name: '',
    phone: '',
    email: '',
    taxCode: '',
    customerTypeId: null
  };

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadCustomers();
    this.loadCustomerTypes();
  }

  loadCustomers() {
    this.loading = true;
    this.http.get<Customer[]>(`${this.apiUrl}/customers`, {
      withCredentials: true
    }).subscribe({
      next: (data) => {
        this.customers = data;
        this.filteredCustomers = [...data];
        this.totalElements = data.length;
        this.totalPages = Math.ceil(data.length / this.pageSize);
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading customers:', err);
        this.loading = false;
      }
    });
  }

  loadCustomerTypes() {
    this.http.get<ApiResponse<CustomerType[]>>(`${this.apiUrl}/customer-types`, {
      withCredentials: true
    }).subscribe({
      next: (response) => {
        this.customerTypes = response.data || [];
        console.log('Customer types loaded:', this.customerTypes);
        if (this.customerTypes.length === 0) {
          console.warn('No customer types found in database');
        }
      },
      error: (err) => {
        console.error('Error loading customer types:', err);
        // Fallback data
        this.customerTypes = [
          { id: 1, name: 'Retail Customer' },
          { id: 2, name: 'Wholesale Customer' },
          { id: 3, name: 'VIP Customer' }
        ];
      }
    });
  }

  onSearch() {
    if (!this.searchTerm.trim()) {
      this.filteredCustomers = [...this.customers];
    } else {
      const term = this.searchTerm.toLowerCase();
      this.filteredCustomers = this.customers.filter(c =>
        c.code?.toLowerCase().includes(term) ||
        c.name?.toLowerCase().includes(term) ||
        c.email?.toLowerCase().includes(term) ||
        c.phone?.includes(term)
      );
    }
    this.currentPage = 0;
    this.totalPages = Math.ceil(this.filteredCustomers.length / this.pageSize);
  }

  openCreateModal() {
    this.isEditing = false;
    this.selectedCustomer = null;
    this.customerForm = {
      code: '',
      name: '',
      phone: '',
      email: '',
      taxCode: '',
      customerTypeId: null
    };
    this.showModal = true;
  }

  openEditModal(customer: Customer) {
    this.isEditing = true;
    this.selectedCustomer = customer;
    this.customerForm = {
      code: customer.code,
      name: customer.name,
      phone: customer.phone,
      email: customer.email,
      taxCode: customer.taxCode,
      customerTypeId: this.getCustomerTypeIdByName(customer.customerTypeName)
    };
    this.showModal = true;
  }

  getCustomerTypeIdByName(typeName: string): number | null {
    const type = this.customerTypes.find(t => t.name === typeName);
    return type ? type.id : null;
  }

  saveCustomer() {
  console.log('Form data before save:', this.customerForm);
  console.log('Selected customerTypeId:', this.customerForm.customerTypeId);
  console.log('Customer types available:', this.customerTypes);

  // Kiểm tra code
  if (!this.customerForm.code || this.customerForm.code.trim() === '') {
    alert('Please enter customer code');
    return;
  }

  // Kiểm tra name
  if (!this.customerForm.name || this.customerForm.name.trim() === '') {
    alert('Please enter customer name');
    return;
  }

  // Kiểm tra customerTypeId - QUAN TRỌNG
  if (!this.customerForm.customerTypeId) {
    alert('Please select a customer type');
    console.error('Customer type ID is null or undefined');
    return;
  }

  // Kiểm tra customer type có tồn tại trong danh sách không
  const selectedType = this.customerTypes.find(t => t.id === this.customerForm.customerTypeId);
  if (!selectedType) {
    alert(`Invalid customer type selected. Available types: ${this.customerTypes.map(t => `${t.id}:${t.name}`).join(', ')}`);
    return;
  }

  this.loading = true;

  // Tạo payload - ĐẢM BẢO customerTypeId là number
  const payload = {
    code: this.customerForm.code.trim(),
    name: this.customerForm.name.trim(),
    phone: this.customerForm.phone?.trim() || '',
    email: this.customerForm.email?.trim() || '',
    taxCode: this.customerForm.taxCode?.trim() || '',
    customerTypeId: Number(this.customerForm.customerTypeId) // Ép kiểu về number
  };

  console.log('Sending payload to backend:', payload);

  if (this.isEditing && this.selectedCustomer) {
    // Update customer
    this.http.put<Customer>(`${this.apiUrl}/customers/${this.selectedCustomer.id}`, payload, {
      withCredentials: true
    }).subscribe({
      next: () => {
        this.loadCustomers();
        this.showModal = false;
        alert('Customer updated successfully');
        this.loading = false;
      },
      error: (err) => {
        console.error('Error updating customer:', err);
        const errorMsg = err.error?.message || err.message || 'Failed to update customer';
        alert(`Error: ${errorMsg}`);
        this.loading = false;
      }
    });
  } else {
    // Create customer
    this.http.post<ApiResponse<Customer>>(`${this.apiUrl}/customers`, payload, {
      withCredentials: true
    }).subscribe({
      next: (response) => {
        console.log('Customer created successfully:', response);
        this.loadCustomers();
        this.showModal = false;
        alert('Customer created successfully');
        this.loading = false;
      },
      error: (err) => {
        console.error('Error creating customer:', err);
        console.error('Error details:', err.error);
        const errorMsg = err.error?.message || err.message || 'Failed to create customer';
        alert(`Error: ${errorMsg}`);
        this.loading = false;
      }
    });
  }
}

  deleteCustomer(id: number) {
    if (confirm('Are you sure you want to delete this customer?')) {
      this.loading = true;
      this.http.delete(`${this.apiUrl}/customers/${id}`, {
        withCredentials: true
      }).subscribe({
        next: () => {
          this.loadCustomers();
          alert('Customer deleted successfully');
          this.loading = false;
        },
        error: (err) => {
          console.error('Error deleting customer:', err);
          alert('Failed to delete customer');
          this.loading = false;
        }
      });
    }
  }

  // Pagination methods
  get paginatedCustomers(): Customer[] {
    const start = this.currentPage * this.pageSize;
    const end = start + this.pageSize;
    return this.filteredCustomers.slice(start, end);
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
    }
  }

  goToPage(page: number) {
    this.currentPage = page;
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const total = this.totalPages;
    const current = this.currentPage;

    if (total <= 7) {
      for (let i = 0; i < total; i++) pages.push(i);
    } else {
      if (current <= 3) {
        for (let i = 0; i < 5; i++) pages.push(i);
        pages.push(-1);
        pages.push(total - 1);
      } else if (current >= total - 4) {
        pages.push(0);
        pages.push(-1);
        for (let i = total - 5; i < total; i++) pages.push(i);
      } else {
        pages.push(0);
        pages.push(-1);
        for (let i = current - 1; i <= current + 1; i++) pages.push(i);
        pages.push(-1);
        pages.push(total - 1);
      }
    }
    return pages;
  }

  getStartIndex(): number {
    return this.currentPage * this.pageSize + 1;
  }

  getEndIndex(): number {
    const end = (this.currentPage + 1) * this.pageSize;
    return Math.min(end, this.filteredCustomers.length);
  }
  onCustomerTypeChange(event: any) {
  console.log('Selected customer type ID:', this.customerForm.customerTypeId);
  console.log('Event value:', event.target.value);
}
}
