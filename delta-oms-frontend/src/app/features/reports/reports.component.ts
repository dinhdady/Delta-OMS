import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss']
})
export class ReportsComponent {
  reportType: string = 'sales';
  dateRange: string = 'last30days';
  startDate: string = '';
  endDate: string = '';
  generating: boolean = false;

  reportTypes = [
    { value: 'sales', label: 'Sales Report', icon: '📊' },
    { value: 'customers', label: 'Customer Report', icon: '👥' },
    { value: 'inventory', label: 'Inventory Report', icon: '📦' },
    { value: 'products', label: 'Product Performance', icon: '⚽' },
    { value: 'revenue', label: 'Revenue Report', icon: '💰' }
  ];

  dateRanges = [
    { value: 'today', label: 'Today' },
    { value: 'yesterday', label: 'Yesterday' },
    { value: 'last7days', label: 'Last 7 Days' },
    { value: 'last30days', label: 'Last 30 Days' },
    { value: 'last90days', label: 'Last 90 Days' },
    { value: 'thisMonth', label: 'This Month' },
    { value: 'lastMonth', label: 'Last Month' },
    { value: 'thisYear', label: 'This Year' },
    { value: 'custom', label: 'Custom Range' }
  ];

  generateReport() {
    this.generating = true;

    // Simulate API call
    setTimeout(() => {
      this.generating = false;
      alert(`${this.reportTypes.find(r => r.value === this.reportType)?.label} generated successfully!\n\nDate Range: ${this.getDateRangeLabel()}\n\nThis feature will download the report file.`);
    }, 2000);
  }

  getDateRangeLabel(): string {
    const range = this.dateRanges.find(r => r.value === this.dateRange);
    if (this.dateRange === 'custom' && this.startDate && this.endDate) {
      return `${this.startDate} to ${this.endDate}`;
    }
    return range?.label || 'Selected Period';
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }
}
