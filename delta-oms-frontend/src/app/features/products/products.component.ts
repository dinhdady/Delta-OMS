import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DashboardService, ProductResponseDTO, CategoryResponseDTO } from '../../core/services/dashboard.service';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit {
  products: ProductResponseDTO[] = [];
  filteredProducts: ProductResponseDTO[] = [];
  categories: CategoryResponseDTO[] = [];
  search: string = '';
  categoryFilter: string = 'all';
  page: number = 1;
  pageSize: number = 10;
  loading: boolean = false;

  showModal: boolean = false;
  editingProduct: ProductResponseDTO | null = null;
  productForm: any = {
    sku: '',
    name: '',
    description: '',
    importPrice: 0,
    salePrice: 0,
    quantity: 0,
    status: 'ACTIVE',
    categoryId: null,
    unitId: 1
  };

  constructor(private dashboardService: DashboardService) {}

  ngOnInit() {
    this.loadProducts();
    this.loadCategories();
  }

  loadProducts() {
    this.loading = true;
    this.dashboardService.getProducts().subscribe({
      next: (res: any) => {
        this.products = res.data || [];
        this.applyFilters();
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error loading products:', err);
        this.loading = false;
      }
    });
  }

  loadCategories() {
    this.dashboardService.getCategories().subscribe({
      next: (res: any) => {
        this.categories = res.data || [];
      },
      error: (err: any) => {
        console.error('Error loading categories:', err);
      }
    });
  }

  applyFilters() {
    let filtered = [...this.products];

    // Apply category filter
    if (this.categoryFilter !== 'all') {
      filtered = filtered.filter(p => p.categoryName === this.categoryFilter);
    }

    // Apply search filter
    if (this.search.trim()) {
      const searchLower = this.search.toLowerCase();
      filtered = filtered.filter(p =>
        p.name?.toLowerCase().includes(searchLower) ||
        p.sku?.toLowerCase().includes(searchLower) ||
        p.categoryName?.toLowerCase().includes(searchLower)
      );
    }

    this.filteredProducts = filtered;
    this.page = 1;
  }

  onSearch() {
    this.applyFilters();
  }

  onCategoryFilterChange() {
    this.applyFilters();
  }

  openModal(product?: ProductResponseDTO) {
    if (product) {
      this.editingProduct = product;
      this.productForm = {
        sku: product.sku,
        name: product.name,
        description: product.description,
        importPrice: 0,
        salePrice: product.salePrice,
        quantity: product.quantity,
        status: product.status,
        categoryId: this.categories.find(c => c.name === product.categoryName)?.id || null,
        unitId: 1
      };
    } else {
      this.editingProduct = null;
      this.productForm = {
        sku: '',
        name: '',
        description: '',
        importPrice: 0,
        salePrice: 0,
        quantity: 0,
        status: 'ACTIVE',
        categoryId: null,
        unitId: 1
      };
    }
    this.showModal = true;
  }

  saveProduct() {
    if (this.editingProduct) {
      this.dashboardService.updateProduct(this.editingProduct.id, this.productForm).subscribe({
        next: () => {
          this.loadProducts();
          this.showModal = false;
          alert('Product updated successfully');
        },
        error: (err: any) => {
          console.error('Error updating product:', err);
          alert('Failed to update product');
        }
      });
    } else {
      this.dashboardService.createProduct(this.productForm).subscribe({
        next: () => {
          this.loadProducts();
          this.showModal = false;
          alert('Product created successfully');
        },
        error: (err: any) => {
          console.error('Error creating product:', err);
          alert('Failed to create product');
        }
      });
    }
  }

  deleteProduct(id: number) {
    if (confirm('Are you sure you want to delete this product?')) {
      this.dashboardService.deleteProduct(id).subscribe({
        next: () => {
          this.loadProducts();
          alert('Product deleted successfully');
        },
        error: (err: any) => {
          console.error('Error deleting product:', err);
          alert('Failed to delete product');
        }
      });
    }
  }

  formatCurrency(amount: number): string {
    if (!amount) return '$0';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(amount);
  }

  getStatusClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'active': return 'active';
      case 'inactive': return 'inactive';
      case 'out of stock': return 'out-of-stock';
      default: return '';
    }
  }

  get paginated() {
    const start = (this.page - 1) * this.pageSize;
    const end = start + this.pageSize;
    return this.filteredProducts.slice(start, end);
  }

  get totalPages() {
    return Math.ceil(this.filteredProducts.length / this.pageSize);
  }

  prevPage() {
    if (this.page > 1) this.page--;
  }

  nextPage() {
    if (this.page < this.totalPages) this.page++;
  }

  goToPage(page: number) {
    this.page = page;
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const total = this.totalPages;
    const current = this.page;

    if (total <= 7) {
      for (let i = 1; i <= total; i++) pages.push(i);
    } else {
      if (current <= 3) {
        for (let i = 1; i <= 5; i++) pages.push(i);
        pages.push(-1);
        pages.push(total);
      } else if (current >= total - 2) {
        pages.push(1);
        pages.push(-1);
        for (let i = total - 4; i <= total; i++) pages.push(i);
      } else {
        pages.push(1);
        pages.push(-1);
        for (let i = current - 1; i <= current + 1; i++) pages.push(i);
        pages.push(-1);
        pages.push(total);
      }
    }
    return pages;
  }

  min(a: number, b: number): number {
    return Math.min(a, b);
  }
}
