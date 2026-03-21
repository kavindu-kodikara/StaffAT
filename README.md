# PeopleHub

Welcome to **PeopleHub**, a clean, modern, and professional desktop employee attendance management system. This application provides a premium, offline-first dashboard designed to help you efficiently track team presence, manage profiles, and secure your data locally without requiring any internet connection.

---

## 📥 Download and Installation

### ⚠️ IMPORTANT INSTALLATION WARNING: DO NOT INSTALL IN C:\ DRIVE

PeopleHub uses a local SQLite database (`attendance.db`) to store all your data securely on your own computer. Windows strictly restricts read and write permissions in the `C:\` drive (especially inside `C:\Program Files`). If installed there, the application will not be permitted to create or update its database, and your data **cannot be saved**.

**To ensure the application works correctly, please follow these steps:**

1. Download the latest `PeopleHub-Installer.exe` from the [Releases page](../../releases).
2. Run the installer. When prompted to select the destination folder, **change the installation directory** to a location outside of your protected `C:\` drive directories.
   - **Recommended locations:** 
     - `D:\PeopleHub` 
     - `E:\Applications\PeopleHub`
     - Or inside your user folder (e.g., `C:\Users\YourName\Documents\PeopleHub`).
3. Proceed with the installation. 

The database file (`attendance.db`) will be automatically generated inside that folder the very first time you open the app.

---

## 🚀 Getting Started

Once installed correctly, launch PeopleHub from your desktop shortcut or the Windows Start menu.

### 1. Initial Setup
Upon opening the application for the first time, you will be greeted with the Login Screen. Enter your administrative credentials to access the Command Center (Dashboard).

**Default Admin Credentials:**
- **Password:** `admin`

### 2. Dashboard Overview
The Command Center gives you a high-level view of:
- **Total Staff**: Quickly see how many employees are currently registered in your system.
- **Present Today**: See who is currently marked as present at a glance.
- **Success Rate**: View overall attendance trends and metrics.

### 3. Adding Employees
To start using the app, you need to add your team members:
1. Navigate to the **Employees** tab on the left sidebar.
2. Click on the **New Employee** button.
3. Fill in their details (Name, Contact, Onboarding Status, etc.).
4. Click Save. They will now appear in your active directory.

### 4. Marking Daily Attendance
Tracking daily presence is quick and easy:
1. Navigate to the **Daily Attendance** tab.
2. You will see a list of all active employees.
3. Simply click **Present**, **Absent**, or **Leave** next to each employee's name for the current date. All changes are saved automatically.

### 5. Viewing Employee Profiles
You can view detailed records for any staff member:
1. Go to the **Employees** directory or the **Dashboard**.
2. Click directly on an employee's row.
3. Here, you can view their personal records, historical attendance grid, onboarding status, and safely add private HR/administrative notes or external links (like Google Sheets).

### 6. Reports and History
Navigate to the **Reports** tab to see an aggregated historical view. This allows you to measure attendance tracking adherence and review past records seamlessly.

---

## 💾 Backing Up Your Data

Since PeopleHub is entirely offline-first, your data is stored **only** on your local machine. We highly recommend regularly backing up your database to prevent data loss.

**How to Backup:**
1. Go to your **Dashboard**.
2. Locate the **Database Backup** section (usually at the bottom right of the screen).
3. Click **Export Database**.
4. Choose a secure location (such as a USB drive, an external hard drive, or a cloud-synced folder like Google Drive/OneDrive) to save the downloaded `.db` file.

**How to Restore or Move to a New Computer:**
If you get a new computer or need to restore your data:
1. Install PeopleHub on the new computer (Remember: Do not install in `C:\`).
2. Run the application once so it creates a fresh `attendance.db` file, then close the app.
3. Navigate to the folder where you installed PeopleHub.
4. Replace the newly generated `attendance.db` file with your backed-up `.db` file.
5. Launch the app, and all your previous data will be restored.

---

## ❓ Troubleshooting

- **The app won't save any data or crashes on startup:** 
  You most likely installed the application in `C:\Program Files` or another protected Windows directory. Please uninstall the application and reinstall it in a location where it has write permissions, such as `D:\PeopleHub` or your `Documents` folder.
  
- **Where is my data stored?** 
  Your data is completely private. It is never sent to the internet. It is stored solely in the `attendance.db` file located inside the exact folder where you installed the application.

---
*Thank you for choosing PeopleHub for your employee management needs!*
