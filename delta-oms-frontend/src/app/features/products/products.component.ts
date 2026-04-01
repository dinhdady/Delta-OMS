import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClickOutsideDirective } from '../../shared/directives/click-outside.directive';
import { DashboardService, ProductResponseDTO, CategoryResponseDTO, ProductRequestDTO } from '../../core/services/dashboard.service';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule,FormsModule,ClickOutsideDirective],
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit {
  products: ProductResponseDTO[] = [];
  filteredProducts: ProductResponseDTO[] = [];
  categories: CategoryResponseDTO[] = [];
  search: string = '';
  categoryFilter: string = 'all';
  statusFilter: string = 'all';
  page: number = 1;
  pageSize: number = 10;
  loading: boolean = false;

  showModal: boolean = false;
  editingProduct: ProductResponseDTO | null = null;
  showRestoreConfirm: boolean = false;
  productToRestore: ProductResponseDTO | null = null;

  // Category input
  categoryInput: string = '';
  private isCreatingCategory: boolean = false;
  showCategoryDropdown = false;
  filteredCategories: CategoryResponseDTO[] = [];
  productForm: ProductRequestDTO = {
    sku: '',
    name: '',
    description: '',
    importPrice: 0,
    salePrice: 0,
    quantity: 0,
    status: 'ACTIVE',
    categoryId: 0,
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
      next: (res: ApiResponse<ProductResponseDTO[]>) => {
        this.products = res.data || [];
        this.applyFilters();
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error loading products:', err);
        this.loading = false;
        this.showErrorToast('Failed to load products');
      }
    });
  }

  loadCategories() {
  this.dashboardService.getCategories().subscribe({
    next: (res: any) => {
      console.log('Raw categories response:', res); // Debug

      // Xử lý nhiều định dạng response
      let categoryList: CategoryResponseDTO[] = [];

      if (Array.isArray(res)) {
        categoryList = res;
      } else if (res && res.data && Array.isArray(res.data)) {
        categoryList = res.data;
      } else if (res && res.content && Array.isArray(res.content)) {
        categoryList = res.content;
      } else if (res && typeof res === 'object') {
        // Nếu response là object nhưng không có data, thử lấy tất cả giá trị
        const values = Object.values(res);
        if (values.length > 0 && Array.isArray(values[0])) {
          categoryList = values[0];
        }
      }

      this.categories = categoryList;
      this.filteredCategories = [...this.categories];
      console.log('Processed categories:', this.categories);
    },
    error: (err: any) => {
      console.error('Error loading categories:', err);
      this.showErrorToast('Failed to load categories');
    }
  });
}

  // Xử lý khi chọn category từ datalist hoặc nhập
  onCategorySelect() {
    const selectedCategoryName = this.categoryInput?.trim();
    if (!selectedCategoryName) return;

    // Tìm category trong danh sách
    const existingCategory = this.categories.find(
      c => c.name.toLowerCase() === selectedCategoryName.toLowerCase()
    );

    if (existingCategory) {
      this.productForm.categoryId = existingCategory.id;
    } else {
      // Nếu category chưa tồn tại, để nguyên categoryId = 0
      // Sẽ được xử lý khi save
      this.productForm.categoryId = 0;
    }
  }
  onCategoryInput() {
    const searchTerm = this.categoryInput.toLowerCase().trim();
    if (searchTerm) {
      this.filteredCategories = this.categories.filter(cat =>
        cat.name.toLowerCase().includes(searchTerm)
      );
    } else {
      this.filteredCategories = [...this.categories];
    }
    this.showCategoryDropdown = true;
  }
  applyFilters() {
    let filtered = [...this.products];

    if (this.statusFilter !== 'all' && this.statusFilter !== 'DELETED') {
      filtered = filtered.filter(p => !p.deleted);
    }

    if (this.categoryFilter !== 'all') {
      filtered = filtered.filter(p => p.categoryName === this.categoryFilter);
    }

    if (this.statusFilter !== 'all') {
      filtered = filtered.filter(p => p.status === this.statusFilter);
    }

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

  onStatusFilterChange() {
    this.applyFilters();
  }

  openModal(product?: ProductResponseDTO) {
    if (product) {
      if (product.deleted || product.status === 'DELETED') {
        alert('Cannot edit deleted product. Please restore it first.');
        return;
      }

      this.editingProduct = product;
      this.categoryInput = product.categoryName || '';
      this.productForm = {
        sku: product.sku,
        name: product.name,
        description: product.description || '',
        importPrice: product.importPrice || 0,
        salePrice: product.salePrice,
        quantity: product.quantity,
        status: product.status,
        categoryId: this.categories.find(c => c.name === product.categoryName)?.id || 0,
        unitId: product.unitId || 1
      };
    } else {
      this.editingProduct = null;
      this.categoryInput = '';
      this.productForm = {
        sku: '',
        name: '',
        description: '',
        importPrice: 0,
        salePrice: 0,
        quantity: 0,
        status: 'ACTIVE',
        categoryId: 0,
        unitId: 1
      };
    }
    this.showModal = true;
    this.closeCategoryDropdown();
  }

  closeModal() {
    this.showModal = false;
    this.editingProduct = null;
    this.categoryInput = '';
    this.isCreatingCategory = false;
  }

  async saveProduct() {
    // Validate form
    if (!this.productForm.sku || !this.productForm.name || !this.productForm.salePrice) {
      alert('Please fill in all required fields (SKU, Name, Sale Price)');
      return;
    }

    if (!this.categoryInput?.trim()) {
      alert('Please enter or select a category');
      return;
    }

    // Kiểm tra xem category đã tồn tại chưa
    const existingCategory = this.categories.find(
      c => c.name.toLowerCase() === this.categoryInput.toLowerCase().trim()
    );

    if (existingCategory) {
      this.productForm.categoryId = existingCategory.id;
      this.performSaveProduct();
    } else {
      // Nếu category chưa tồn tại và chưa đang tạo category
      if (!this.isCreatingCategory) {
        if (confirm(`Category "${this.categoryInput}" does not exist. Do you want to create it?`)) {
          await this.createNewCategoryAndSave();
        }
      }
    }
  }

  async createNewCategoryAndSave() {
    this.isCreatingCategory = true;

    const newCategory = {
      name: this.categoryInput.trim(),
      description: `Category for ${this.categoryInput}`
    };

    this.dashboardService.createCategory(newCategory).subscribe({
      next: (res: any) => {
        // Thêm category mới vào danh sách
        const newCat: CategoryResponseDTO = {
          id: res.data.id,
          name: res.data.name,
          description: res.data.description
        };
        this.categories.push(newCat);

        // Set category ID cho product
        this.productForm.categoryId = newCat.id;

        // Lưu product
        this.performSaveProduct();

        this.isCreatingCategory = false;
      },
      error: (err: any) => {
        console.error('Error creating category:', err);
        this.showErrorToast('Failed to create category. Please try again.');
        this.isCreatingCategory = false;
      }
    });
  }

  performSaveProduct() {
    // Auto update status based on quantity
    if (this.productForm.quantity <= 0 && this.productForm.status === 'ACTIVE') {
      this.productForm.status = 'OUT_OF_STOCK';
    }

    if (this.editingProduct) {
      this.dashboardService.updateProduct(this.editingProduct.id, this.productForm).subscribe({
        next: () => {
          this.loadProducts();
          this.loadCategories();
          this.closeModal();
          this.showSuccessToast('Product updated successfully');
        },
        error: (err: any) => {
          console.error('Error updating product:', err);
          this.showErrorToast(err.error?.message || 'Failed to update product');
        }
      });
    } else {
      this.dashboardService.createProduct(this.productForm).subscribe({
        next: () => {
          this.loadProducts();
          this.loadCategories();
          this.closeModal();
          this.showSuccessToast('Product created successfully');
        },
        error: (err: any) => {
          console.error('Error creating product:', err);
          this.showErrorToast(err.error?.message || 'Failed to create product');
        }
      });
    }
  }

  deleteProduct(id: number) {
    const product = this.products.find(p => p.id === id);
    if (product?.deleted || product?.status === 'DELETED') {
      alert('Product is already deleted');
      return;
    }

    if (confirm('Are you sure you want to delete this product?\n\nThis product will be soft deleted and can be restored later.')) {
      this.dashboardService.deleteProduct(id).subscribe({
        next: () => {
          this.loadProducts();
          this.showSuccessToast('Product deleted successfully');
        },
        error: (err: any) => {
          console.error('Error deleting product:', err);
          this.showErrorToast(err.error?.message || 'Failed to delete product');
        }
      });
    }
  }

  confirmRestore(product: ProductResponseDTO) {
    this.productToRestore = product;
    this.showRestoreConfirm = true;
  }

  restoreProduct() {
    if (this.productToRestore) {
      this.dashboardService.restoreProduct(this.productToRestore.id).subscribe({
        next: () => {
          this.loadProducts();
          this.showRestoreConfirm = false;
          this.productToRestore = null;
          this.showSuccessToast('Product restored successfully');
        },
        error: (err: any) => {
          console.error('Error restoring product:', err);
          this.showErrorToast(err.error?.message || 'Failed to restore product');
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
    switch (status?.toUpperCase()) {
      case 'ACTIVE': return 'active';
      case 'INACTIVE': return 'inactive';
      case 'OUT_OF_STOCK': return 'out-of-stock';
      case 'DELETED': return 'deleted';
      default: return '';
    }
  }

  getStatusText(status: string): string {
    switch (status?.toUpperCase()) {
      case 'ACTIVE': return 'Active';
      case 'INACTIVE': return 'Inactive';
      case 'OUT_OF_STOCK': return 'Out of Stock';
      case 'DELETED': return 'Deleted';
      default: return status || 'Unknown';
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

  private showSuccessToast(message: string) {
    alert(message);
  }

  private showErrorToast(message: string) {
    alert(message);
  }
  openCategoryDropdown() {
    this.filteredCategories = [...this.categories];
    this.showCategoryDropdown = true;
  }
  toggleCategoryDropdown(event: Event) {
    event.stopPropagation();
    this.showCategoryDropdown = !this.showCategoryDropdown;
    if (this.showCategoryDropdown) {
      this.filteredCategories = [...this.categories];
    }
  }
  closeCategoryDropdown() {
  this.showCategoryDropdown = false;
}

// Chọn category từ dropdown
selectCategory(category: CategoryResponseDTO) {
  this.categoryInput = category.name;
  this.productForm.categoryId = category.id;
  this.closeCategoryDropdown();
}
}

interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
}
