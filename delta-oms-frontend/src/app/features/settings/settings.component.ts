import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent {
  activeTab: string = 'general'; // ✅ Thêm property này

  settings = {
    companyName: 'SportsHub',
    companyEmail: 'info@sportshub.com',
    companyPhone: '+1 234 567 8900',
    companyAddress: '123 Sports Street, Stadium City, SC 12345',
    currency: 'USD',
    timezone: 'UTC+7',
    dateFormat: 'MM/DD/YYYY',

    notifications: {
      email: true,
      push: true,
      sms: false,
      orderUpdates: true,
      inventoryAlerts: true,
      promotionalEmails: false
    },

    appearance: {
      darkMode: false,
      compactMode: false,
      animations: true
    },

    security: {
      twoFactorAuth: false,
      sessionTimeout: 30,
      passwordExpiryDays: 90
    }
  };

  currencies = ['USD', 'EUR', 'GBP', 'VND', 'JPY'];
  timezones = ['UTC-12', 'UTC-11', 'UTC-10', 'UTC-9', 'UTC-8', 'UTC-7', 'UTC-6', 'UTC-5', 'UTC-4', 'UTC-3', 'UTC-2', 'UTC-1', 'UTC+0', 'UTC+1', 'UTC+2', 'UTC+3', 'UTC+4', 'UTC+5', 'UTC+6', 'UTC+7', 'UTC+8', 'UTC+9', 'UTC+10', 'UTC+11', 'UTC+12'];
  dateFormats = ['MM/DD/YYYY', 'DD/MM/YYYY', 'YYYY-MM-DD', 'MMM DD, YYYY'];

  saving: boolean = false;

  changePassword() {
    alert('Change password feature coming soon!');
  }

  saveSettings() {
    this.saving = true;

    setTimeout(() => {
      this.saving = false;
      alert('Settings saved successfully!');
    }, 1500);
  }

  resetSettings() {
    if (confirm('Are you sure you want to reset all settings to default?')) {
      this.settings = {
        companyName: 'SportsHub',
        companyEmail: 'info@sportshub.com',
        companyPhone: '+1 234 567 8900',
        companyAddress: '123 Sports Street, Stadium City, SC 12345',
        currency: 'USD',
        timezone: 'UTC+7',
        dateFormat: 'MM/DD/YYYY',
        notifications: {
          email: true,
          push: true,
          sms: false,
          orderUpdates: true,
          inventoryAlerts: true,
          promotionalEmails: false
        },
        appearance: {
          darkMode: false,
          compactMode: false,
          animations: true
        },
        security: {
          twoFactorAuth: false,
          sessionTimeout: 30,
          passwordExpiryDays: 90
        }
      };
      alert('Settings reset to default');
    }
  }
}
