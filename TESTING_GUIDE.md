# Testing Guide for Rwanda Government ERP System

This guide provides instructions on how to test the Rwanda Government ERP System using Swagger UI and Postman.

## Table of Contents
1. [Testing with Swagger UI](#testing-with-swagger-ui)
2. [Testing with Postman](#testing-with-postman)
3. [Step-by-Step Testing Guide](#step-by-step-testing-guide)
4. [Sample Requests and Responses](#sample-requests-and-responses)

## Testing with Swagger UI

### Accessing Swagger UI
1. Start the application by running the Spring Boot application
2. Open your browser and navigate to: `http://localhost:8000/swagger-ui.html`
3. You should see the Swagger UI interface with all available API endpoints

### Authentication
1. Expand the "Authentication" section
2. Use the `/auth/admin/register` endpoint to create an admin user
3. Use the `/auth/login` endpoint to authenticate and get a JWT token
4. Click the "Authorize" button at the top right of the page
5. Enter the JWT token in the format: `Bearer <your_token>`
6. Click "Authorize" to apply the token to all subsequent requests

### Testing Endpoints
1. Expand the endpoint section you want to test (e.g., "Employee Management")
2. Click on the specific endpoint you want to test
3. Click "Try it out"
4. Fill in the required parameters or request body
5. Click "Execute"
6. Review the response

## Testing with Postman

### Setting Up Postman Collection
1. Open Postman
2. Create a new collection named "Rwanda Government ERP"
3. Create folders for different endpoint categories:
   - Authentication
   - Employee Management
   - Employment Management
   - Deduction Management
   - Payroll Management
   - Message Management

### Configuring Authentication
1. Create a POST request to `http://localhost:8000/api/v1/auth/login`
2. In the Body tab, select "raw" and "JSON"
3. Enter login credentials:
   ```json
   {
     "email": "your_admin_email@example.com",
     "password": "your_password"
   }
   ```
4. Send the request and copy the JWT token from the response
5. Create a variable in your collection:
   - Name: `jwt_token`
   - Value: The token you received
6. For each request that requires authentication:
   - Go to the "Authorization" tab
   - Select "Bearer Token"
   - Enter `{{jwt_token}}` as the token value

## Step-by-Step Testing Guide

### 1. Creating an Admin User
1. Use the `/auth/admin/register` endpoint
2. Request body:
   ```json
   {
     "firstName": "Admin",
     "lastName": "User",
     "email": "admin@example.com",
     "phoneNumber": "0788123456",
     "nationalId": "1234567890123456",
     "password": "Password@123",
     "adminCreateCode": "create_admin_2025"
   }
   ```
3. This will create an admin user with ROLE_ADMIN

### 2. Authentication
1. Use the `/auth/login` endpoint
2. Request body:
   ```json
   {
     "email": "admin@example.com",
     "password": "Password@123"
   }
   ```
3. Save the JWT token for subsequent requests

### 3. Creating Employees

#### Creating an Admin
1. Use the `/api/v1/employees/admin` endpoint with admin role
2. Request body:
   ```json
   {
     "firstName": "Admin",
     "lastName": "User",
     "email": "admin2@example.com",
     "phoneNumber": "0788123457",
     "nationalId": "1234567890123457",
     "password": "Password@123",
     "adminCreateCode": "create_admin_2025"
   }
   ```
3. This will create a user with ADMIN role

#### Creating an Employee with Specific Role
1. Use the `/api/v1/employees` endpoint with admin role
2. Request body:
   ```json
   {
     "firstName": "Employee",
     "lastName": "One",
     "email": "employee1@example.com",
     "phoneNumber": "0788123458",
     "nationalId": "1234567890123458",
     "password": "Password@123",
     "role": "ROLE_EMPLOYEE"
   }
   ```
3. This will create an employee with the EMPLOYEE role

4. To create a manager, use:
   ```json
   {
     "firstName": "Manager",
     "lastName": "One",
     "email": "manager1@example.com",
     "phoneNumber": "0788123459",
     "nationalId": "1234567890123459",
     "password": "Password@123",
     "role": "ROLE_MANAGER"
   }
   ```
5. This will create an employee with the MANAGER role

### 4. Assigning Roles to Employees
1. Use the `/api/v1/users/{userId}/roles/add` endpoint
2. Request body:
   ```json
   {
     "roles": ["ROLE_MANAGER", "ROLE_EMPLOYEE"]
   }
   ```
3. This will assign the specified roles to the employee

### 5. Creating Employment Records
1. Use the `/api/v1/employments` endpoint
2. Request body:
   ```json
   {
     "employeeId": "employee_uuid_here",
     "department": "Finance",
     "position": "Accountant",
     "baseSalary": 70000,
     "status": "ACTIVE",
     "joiningDate": "2023-01-01"
   }
   ```
3. This will create an employment record for the employee

### 6. Managing Deductions
1. Verify existing deductions using the `/api/v1/deductions` endpoint
2. The system should already have the following deductions:
   - EmployeeTax: 30%
   - Pension: 6% (updated from 3%)
   - MedicalInsurance: 5%
   - Others: 5%
   - Housing: 14%
   - Transport: 14%
3. If needed, create or update deductions using the appropriate endpoints

### 7. Generating Payslips
1. Use the `/api/v1/payroll/generate` endpoint
2. Request body:
   ```json
   {
     "employeeId": "employee_uuid_here",
     "month": 1,
     "year": 2025
   }
   ```
3. This will generate a payslip for the specified employee, month, and year
4. Alternatively, use `/api/v1/payroll/generate/all?month=1&year=2025` to generate payslips for all active employees

### 8. Approving Payslips
1. Use the `/api/v1/payroll/approve/{id}` endpoint to approve a specific payslip
2. Alternatively, use `/api/v1/payroll/approve/all` to approve all pending payslips
3. When a payslip is approved, a message is automatically generated and sent to the employee

### 9. Verifying Message Generation and Sending
1. Use the `/api/v1/messages` endpoint to view all messages
2. Use the `/api/v1/messages/pending` endpoint to view pending messages
3. Use the `/api/v1/messages/sent` endpoint to view sent messages
4. Use the `/api/v1/messages/send/{id}` endpoint to manually send a specific message
5. Use the `/api/v1/messages/send/all` endpoint to send all pending messages

### 10. Accessing Generated PDF Payslips
1. When a payslip is approved, a PDF version is automatically generated and saved
2. PDF files are stored in the directory specified by the `application.payslip.pdf.storage-path` property (default: `./payslips`)
3. The PDF filename format is: `payslip_[employee_id]_[last_name]_[year]_[month].pdf`
4. The PDF contains the same information as the email notification sent to the employee
5. To verify PDF generation:
   - Approve a payslip using the `/api/v1/payroll/approve/{id}` endpoint
   - Check the application logs for a message like "PDF saved to: [path]"
   - Navigate to the specified directory and verify that the PDF file exists
   - Open the PDF file to ensure it contains the correct information

## Sample Requests and Responses

### Login Request
**Endpoint:** `POST /api/v1/auth/login`

**Request Body:**
```json
{
  "email": "admin@example.com",
  "password": "Password@123"
}
```

### Login Response
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer"
  }
}
```

### Generate Payslip Request
**Endpoint:** `POST /api/v1/payroll/generate`

**Request Body:**
```json
{
  "employeeId": "123e4567-e89b-12d3-a456-426614174000",
  "month": 1,
  "year": 2025
}
```

### Generate Payslip Response
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174001",
  "employee": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "firstName": "Employee",
    "lastName": "One",
    "email": "employee1@example.com",
    "phoneNumber": "0788123457",
    "roles": ["ROLE_EMPLOYEE", "ROLE_MANAGER"]
  },
  "houseAmount": 9800.0,
  "transportAmount": 9800.0,
  "employeeTaxAmount": 21000.0,
  "pensionAmount": 4200.0,
  "medicalInsuranceAmount": 3500.0,
  "otherTaxAmount": 3500.0,
  "grossSalary": 89600.0,
  "netSalary": 57400.0,
  "month": 1,
  "year": 2025,
  "status": "PENDING"
}
```

### Approve Payslip Request
**Endpoint:** `PUT /api/v1/payroll/approve/123e4567-e89b-12d3-a456-426614174001`

**Request Body:** None (No body required for this request)

### Approve Payslip Response
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174001",
  "employee": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "firstName": "Employee",
    "lastName": "One",
    "email": "employee1@example.com",
    "phoneNumber": "0788123457",
    "roles": ["ROLE_EMPLOYEE", "ROLE_MANAGER"]
  },
  "houseAmount": 9800.0,
  "transportAmount": 9800.0,
  "employeeTaxAmount": 21000.0,
  "pensionAmount": 4200.0,
  "medicalInsuranceAmount": 3500.0,
  "otherTaxAmount": 3500.0,
  "grossSalary": 89600.0,
  "netSalary": 57400.0,
  "month": 1,
  "year": 2025,
  "status": "PAID"
}
```

### Get Messages Response
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174002",
    "employee": {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "firstName": "Employee",
      "lastName": "One",
      "email": "employee1@example.com",
      "phoneNumber": "0788123457",
      "roles": ["ROLE_EMPLOYEE", "ROLE_MANAGER"]
    },
    "content": "Dear Employee, Your salary of JANUARY/2025 from Rwanda Government 57400.00 has been credited to your 123e4567-e89b-12d3-a456-426614174000 account successfully.",
    "month": 1,
    "year": 2025,
    "sent": true,
    "createdAt": "2023-06-01T12:00:00",
    "sentAt": "2023-06-01T12:01:00"
  }
]
```

This testing guide provides a comprehensive approach to verify all aspects of the Rwanda Government ERP System.
