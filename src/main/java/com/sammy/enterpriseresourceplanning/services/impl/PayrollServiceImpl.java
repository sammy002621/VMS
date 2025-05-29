package com.sammy.enterpriseresourceplanning.services.impl;

import com.sammy.enterpriseresourceplanning.dtos.request.payslip.PaySlipRequestDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.message.MessageResponseDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.payslip.PaySlipResponseDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.user.UserResponseDTO;
import com.sammy.enterpriseresourceplanning.enums.EEmploymentStatus;
import com.sammy.enterpriseresourceplanning.enums.EPaySlipStatus;
import com.sammy.enterpriseresourceplanning.exceptions.BadRequestException;
import com.sammy.enterpriseresourceplanning.exceptions.NotFoundException;
import com.sammy.enterpriseresourceplanning.models.Deduction;
import com.sammy.enterpriseresourceplanning.models.Employment;
import com.sammy.enterpriseresourceplanning.models.PaySlip;
import com.sammy.enterpriseresourceplanning.models.User;
import com.sammy.enterpriseresourceplanning.repositories.IDeductionRepository;
import com.sammy.enterpriseresourceplanning.repositories.IEmploymentRepository;
import com.sammy.enterpriseresourceplanning.repositories.IPaySlipRepository;
import com.sammy.enterpriseresourceplanning.services.IMessageService;
import com.sammy.enterpriseresourceplanning.services.IPayrollService;
import com.sammy.enterpriseresourceplanning.services.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements IPayrollService {

    private final IPaySlipRepository paySlipRepository;
    private final IEmploymentRepository employmentRepository;
    private final IDeductionRepository deductionRepository;
    private final IUserService userService;
    private final IMessageService messageService;

    @Override
    @Transactional
    public PaySlipResponseDTO generatePaySlip(PaySlipRequestDTO paySlipRequestDTO) {
        User employee = userService.findUserById(paySlipRequestDTO.getEmployeeId());

        // Check if employee has an active employment
        List<Employment> employments = employmentRepository.findByEmployee(employee);
        if (employments.isEmpty()) {
            throw new BadRequestException("Employee has no employment record");
        }

        Employment employment = employments.stream()
                .filter(e -> e.getStatus() == EEmploymentStatus.ACTIVE)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Employee has no active employment"));

        // Check if pay slip already exists for this month and year
        Optional<PaySlip> existingPaySlip = paySlipRepository.findByEmployeeAndMonthAndYear(
                employee, paySlipRequestDTO.getMonth(), paySlipRequestDTO.getYear());
        if (existingPaySlip.isPresent()) {
            throw new BadRequestException("Pay slip already exists for this employee in the specified month and year");
        }

        // Get all deductions
        List<Deduction> deductions = deductionRepository.findAll();
        if (deductions.isEmpty()) {
            throw new BadRequestException("No deductions configured in the system");
        }

        // Calculate pay slip amounts
        double baseSalary = employment.getBaseSalary();

        // Find specific deductions
        Deduction employeeTaxDeduction = findDeductionByName("EmployeeTax", deductions);
        Deduction pensionDeduction = findDeductionByName("Pension", deductions);
        Deduction medicalInsuranceDeduction = findDeductionByName("MedicalInsurance", deductions);
        Deduction otherDeduction = findDeductionByName("Others", deductions);
        Deduction housingDeduction = findDeductionByName("Housing", deductions);
        Deduction transportDeduction = findDeductionByName("Transport", deductions);

        // Calculate amounts
        double employeeTaxAmount = calculateAmount(baseSalary, employeeTaxDeduction);
        double pensionAmount = calculateAmount(baseSalary, pensionDeduction);
        double medicalInsuranceAmount = calculateAmount(baseSalary, medicalInsuranceDeduction);
        double otherTaxAmount = calculateAmount(baseSalary, otherDeduction);
        double houseAmount = calculateAmount(baseSalary, housingDeduction);
        double transportAmount = calculateAmount(baseSalary, transportDeduction);

        // Calculate gross and net salary
        double grossSalary = baseSalary + houseAmount + transportAmount;
        double totalDeductions = employeeTaxAmount + pensionAmount + medicalInsuranceAmount + otherTaxAmount;
        double netSalary = grossSalary - totalDeductions;

        // Create pay slip
        PaySlip paySlip = PaySlip.builder()
                .employee(employee)
                .houseAmount(houseAmount)
                .transportAmount(transportAmount)
                .employeeTaxAmount(employeeTaxAmount)
                .pensionAmount(pensionAmount)
                .medicalInsuranceAmount(medicalInsuranceAmount)
                .otherTaxAmount(otherTaxAmount)
                .grossSalary(grossSalary)
                .netSalary(netSalary)
                .month(paySlipRequestDTO.getMonth())
                .year(paySlipRequestDTO.getYear())
                .status(EPaySlipStatus.PENDING)
                .build();

        paySlip = paySlipRepository.save(paySlip);

        return mapToResponseDTO(paySlip);
    }

    @Override
    @Transactional
    public PaySlipResponseDTO approvePaySlip(UUID id) {
        PaySlip paySlip = findPaySlipEntity(id);

        if (paySlip.getStatus() == EPaySlipStatus.PAID) {
            throw new BadRequestException("Pay slip is already approved");
        }

        paySlip.setStatus(EPaySlipStatus.PAID);
        paySlip = paySlipRepository.save(paySlip);

        // Create and send a message to the employee
        MessageResponseDTO messageResponseDTO = messageService.createMessage(paySlip);
        messageService.sendMessage(messageResponseDTO.getId());

        return mapToResponseDTO(paySlip);
    }

    @Override
    public PaySlipResponseDTO getPaySlipById(UUID id) {
        PaySlip paySlip = findPaySlipEntity(id);
        return mapToResponseDTO(paySlip);
    }

    @Override
    public List<PaySlipResponseDTO> getPaySlipsByEmployee(UUID employeeId) {
        User employee = userService.findUserById(employeeId);
        return paySlipRepository.findByEmployee(employee).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaySlipResponseDTO> getPaySlipsByMonth(Integer month, Integer year) {
        return paySlipRepository.findByMonthAndYear(month, year).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaySlipResponseDTO> getAllPendingPaySlips() {
        return paySlipRepository.findByStatus(EPaySlipStatus.PENDING).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaySlipResponseDTO> getAllPaidPaySlips() {
        return paySlipRepository.findByStatus(EPaySlipStatus.PAID).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaySlipResponseDTO> getAllPaySlips() {
        return paySlipRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePaySlip(UUID id) {
        if (!paySlipRepository.existsById(id)) {
            throw new NotFoundException("Pay slip not found with id: " + id);
        }
        paySlipRepository.deleteById(id);
    }

    @Override
    public PaySlip findPaySlipEntity(UUID id) {
        return paySlipRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pay slip not found with id: " + id));
    }

    @Override
    @Transactional
    public List<PaySlipResponseDTO> generatePaySlipsForAllEmployees(Integer month, Integer year) {
        // Get all users with active employment
        List<Employment> activeEmployments = employmentRepository.findAll().stream()
                .filter(e -> e.getStatus() == EEmploymentStatus.ACTIVE)
                .collect(Collectors.toList());

        List<PaySlipResponseDTO> generatedPaySlips = new ArrayList<>();

        for (Employment employment : activeEmployments) {
            User employee = employment.getEmployee();

            // Check if pay slip already exists for this employee in the specified month and year
            Optional<PaySlip> existingPaySlip = paySlipRepository.findByEmployeeAndMonthAndYear(
                    employee, month, year);
            if (existingPaySlip.isPresent()) {
                continue; // Skip this employee
            }

            // Create pay slip request DTO
            PaySlipRequestDTO paySlipRequestDTO = PaySlipRequestDTO.builder()
                    .employeeId(employee.getId())
                    .month(month)
                    .year(year)
                    .build();

            // Generate pay slip
            try {
                PaySlipResponseDTO paySlipResponseDTO = generatePaySlip(paySlipRequestDTO);
                generatedPaySlips.add(paySlipResponseDTO);
            } catch (Exception e) {
                // Log error and continue with next employee
                System.err.println("Error generating pay slip for employee " + employee.getId() + ": " + e.getMessage());
            }
        }

        return generatedPaySlips;
    }

    @Override
    @Transactional
    public List<PaySlipResponseDTO> approveAllPendingPaySlips() {
        List<PaySlip> pendingPaySlips = paySlipRepository.findByStatus(EPaySlipStatus.PENDING);
        List<PaySlipResponseDTO> approvedPaySlips = new ArrayList<>();

        for (PaySlip paySlip : pendingPaySlips) {
            paySlip.setStatus(EPaySlipStatus.PAID);
            paySlip = paySlipRepository.save(paySlip);

            // Create and send a message to the employee
            MessageResponseDTO messageResponseDTO = messageService.createMessage(paySlip);
            messageService.sendMessage(messageResponseDTO.getId());

            approvedPaySlips.add(mapToResponseDTO(paySlip));
        }

        return approvedPaySlips;
    }

    private Deduction findDeductionByName(String name, List<Deduction> deductions) {
        return deductions.stream()
                .filter(d -> d.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Deduction not found with name: " + name));
    }

    private double calculateAmount(double baseSalary, Deduction deduction) {
        return baseSalary * (deduction.getPercentage() / 100.0);
    }

    private PaySlipResponseDTO mapToResponseDTO(PaySlip paySlip) {
        return PaySlipResponseDTO.builder()
                .id(paySlip.getId())
                .employee(new UserResponseDTO(paySlip.getEmployee()))
                .houseAmount(paySlip.getHouseAmount())
                .transportAmount(paySlip.getTransportAmount())
                .employeeTaxAmount(paySlip.getEmployeeTaxAmount())
                .pensionAmount(paySlip.getPensionAmount())
                .medicalInsuranceAmount(paySlip.getMedicalInsuranceAmount())
                .otherTaxAmount(paySlip.getOtherTaxAmount())
                .grossSalary(paySlip.getGrossSalary())
                .netSalary(paySlip.getNetSalary())
                .month(paySlip.getMonth())
                .year(paySlip.getYear())
                .status(paySlip.getStatus())
                .build();
    }
}
