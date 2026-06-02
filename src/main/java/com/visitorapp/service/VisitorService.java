package com.visitorapp.service;

import com.visitorapp.dto.DashboardViewModel;
import com.visitorapp.dto.DropdownViewModel;
import com.visitorapp.dto.FilterViewModel;
import com.visitorapp.dto.UserDashboardViewModel;
import com.visitorapp.dto.VisitorManagementViewModel;
import com.visitorapp.entity.CompanyMaster;
import com.visitorapp.entity.EmployeeMaster;
import com.visitorapp.entity.PlantMaster;
import com.visitorapp.entity.VisitorInviteHistory;
import com.visitorapp.entity.VisitorManagement;
import com.visitorapp.repository.CompanyMasterRepository;
import com.visitorapp.repository.EmployeeMasterRepository;
import com.visitorapp.repository.PlantMasterRepository;
import com.visitorapp.repository.VisitorInviteHistoryRepository;
import com.visitorapp.repository.VisitorManagementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VisitorService {

    private static final Logger log = LoggerFactory.getLogger(VisitorService.class);

    private final VisitorManagementRepository visitorRepo;
    private final VisitorInviteHistoryRepository historyRepo;
    private final EmployeeMasterRepository employeeRepo;
    private final CompanyMasterRepository companyRepo;
    private final PlantMasterRepository plantRepo;
    private final OTPService otpService;
    private final QRCodeService qrCodeService;
    private final EmailService emailService;
    private final NotificationService notificationService;

    public VisitorService(VisitorManagementRepository visitorRepo,
                          VisitorInviteHistoryRepository historyRepo,
                          EmployeeMasterRepository employeeRepo,
                          CompanyMasterRepository companyRepo,
                          PlantMasterRepository plantRepo,
                          OTPService otpService,
                          QRCodeService qrCodeService,
                          EmailService emailService,
                          NotificationService notificationService) {
        this.visitorRepo = visitorRepo;
        this.historyRepo = historyRepo;
        this.employeeRepo = employeeRepo;
        this.companyRepo = companyRepo;
        this.plantRepo = plantRepo;
        this.otpService = otpService;
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    public DashboardViewModel getDashboardStats() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date todayStart = cal.getTime();

        List<VisitorManagement> all = visitorRepo.findByIsActiveAndIsDeletedOrderByVisitIdDesc(true, false);
        DashboardViewModel stats = new DashboardViewModel();
        stats.setTotalVisitors((long) all.size());
        stats.setTodayVisitors(visitorRepo.countTodayVisitors(todayStart));
        stats.setTotalInvited(all.stream().filter(v -> Boolean.TRUE.equals(v.getInvited())).count());
        stats.setTotalAccepted(all.stream().filter(v -> Boolean.TRUE.equals(v.getAccept())).count());
        stats.setTotalCheckedIn(all.stream().filter(v -> v.getCheckIn() != null).count());
        stats.setTotalCheckedOut(all.stream().filter(v -> v.getCheckOut() != null).count());
        stats.setTotalPending(all.stream().filter(v -> Boolean.TRUE.equals(v.getInvited()) && !Boolean.TRUE.equals(v.getAccept()) && v.getCheckIn() == null).count());
        return stats;
    }

    public UserDashboardViewModel getUserDashboardMetrics(Integer empId) {
        if (empId == null) empId = 0;

        List<VisitorManagement> allMyVisitors = visitorRepo.findByCreatedByAndIsDeletedOrderByVisitDateDesc(empId, false);
        List<VisitorManagement> allContacted = visitorRepo.findByWhomToMeetAndIsDeleted(empId, false);
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();

        UserDashboardViewModel m = new UserDashboardViewModel();
        m.setTotalVisitors((long) allMyVisitors.size());
        m.setTotalContacted((long) allContacted.size());
        m.setTotalInvited(allMyVisitors.stream().filter(v -> Boolean.TRUE.equals(v.getInvited())).count());
        m.setTotalAccepted(allMyVisitors.stream().filter(v -> Boolean.TRUE.equals(v.getAccept())).count());
        m.setTotalCheckedIn(allMyVisitors.stream().filter(v -> v.getCheckIn() != null).count());
        m.setTotalCheckedOut(allMyVisitors.stream().filter(v -> v.getCheckOut() != null).count());
        m.setTodayVisitors(allMyVisitors.stream().filter(v -> v.getVisitDate() != null && v.getVisitDate().equals(today)).count());
        m.setTodayCheckIns(allMyVisitors.stream().filter(v -> v.getCheckIn() != null && v.getVisitDate() != null && v.getVisitDate().equals(today)).count());
        m.setTodayCheckOuts(allMyVisitors.stream().filter(v -> v.getCheckOut() != null && v.getVisitDate() != null && v.getVisitDate().equals(today)).count());
        return m;
    }

    public List<VisitorManagementViewModel> getTodayVisitors() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();

        return visitorRepo.findTodayVisitors(today).stream()
            .map(this::toViewModel)
            .collect(Collectors.toList());
    }

    public List<VisitorManagementViewModel> getAllVisitors() {
        return visitorRepo.findByIsActiveAndIsDeletedOrderByVisitIdDesc(true, false).stream()
            .map(this::toViewModel)
            .collect(Collectors.toList());
    }

    // === PLANT-SCOPED VISITOR QUERIES ===
    public List<VisitorManagementViewModel> getTodayVisitorsByPlant(Integer plantId) {
        if (plantId == null) return getTodayVisitors();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        return visitorRepo.findTodayVisitorsByPlant(plantId, today).stream()
            .map(this::toViewModel)
            .collect(Collectors.toList());
    }

    public List<VisitorManagementViewModel> getAllVisitorsByPlant(Integer plantId) {
        if (plantId == null) return getAllVisitors();
        return visitorRepo.findByPlantId(plantId).stream()
            .map(this::toViewModel)
            .collect(Collectors.toList());
    }

    public VisitorManagementViewModel getVisitorById(Integer visitId) {
        return visitorRepo.findById(visitId).map(this::toViewModel).orElseGet(() -> {
            VisitorManagementViewModel r = new VisitorManagementViewModel();
            r.setMsg("Visitor not found");
            return r;
        });
    }

    public List<VisitorManagementViewModel> getVisitorsByEmployeeAndPlant(Integer empId, Integer plantId) {
        if (plantId == null) return getVisitorsByEmployee(empId);
        return visitorRepo.findByPlantIdAndCreatedBy(plantId, empId).stream()
            .map(this::toViewModel)
            .collect(Collectors.toList());
    }

    public List<VisitorManagementViewModel> getVisitorsContactedByPlant(Integer empId, Integer plantId) {
        if (plantId == null) return visitorRepo.findByWhomToMeetAndIsDeleted(empId, false).stream()
            .map(this::toViewModel).collect(Collectors.toList());
        return visitorRepo.findByPlantIdAndWhomToMeet(plantId, empId).stream()
            .map(this::toViewModel)
            .collect(Collectors.toList());
    }

    public DashboardViewModel getDashboardStatsByPlant(Integer plantId) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date todayStart = cal.getTime();

        DashboardViewModel stats = new DashboardViewModel();
        List<VisitorManagement> all;
        if (plantId != null) {
            all = visitorRepo.findByPlantId(plantId);
            long todayCount = visitorRepo.countTodayVisitorsByPlant(plantId, todayStart);
            stats.setTodayVisitors(todayCount);
        } else {
            all = visitorRepo.findByIsActiveAndIsDeletedOrderByVisitIdDesc(true, false);
            long todayCount = visitorRepo.countTodayVisitors(todayStart);
            stats.setTodayVisitors(todayCount);
        }
        stats.setTotalVisitors((long) all.size());
        stats.setTotalInvited(all.stream().filter(v -> Boolean.TRUE.equals(v.getInvited())).count());
        stats.setTotalAccepted(all.stream().filter(v -> Boolean.TRUE.equals(v.getAccept())).count());
        stats.setTotalCheckedIn(all.stream().filter(v -> v.getCheckIn() != null).count());
        stats.setTotalCheckedOut(all.stream().filter(v -> v.getCheckOut() != null).count());
        stats.setTotalPending(all.stream().filter(v -> Boolean.TRUE.equals(v.getInvited()) && !Boolean.TRUE.equals(v.getAccept()) && v.getCheckIn() == null).count());
        return stats;
    }

    public List<VisitorManagementViewModel> getVisitorsByEmployee(Integer empId) {
        return visitorRepo.findByCreatedByAndIsDeletedOrderByCreatedDateDesc(empId, false).stream()
            .map(this::toViewModel)
            .collect(Collectors.toList());
    }

    @Transactional
    public VisitorManagementViewModel inviteVisit(VisitorManagementViewModel model) {
        if (model.getEmpId() == null || model.getEmpId() == 0) {
            model.setMsg("EmpId is required");
            return model;
        }

        VisitorManagement vm = new VisitorManagement();
        vm.setName(model.getName());
        vm.setDesignation(model.getDesignation());
        vm.setCompany(model.getCompany());
        vm.setPurpose(model.getPurpose());
        vm.setpMail(model.getpMail());
        vm.setoMail(model.getoMail());
        vm.setMobile(model.getMobile());
        vm.setaMobile(model.getaMobile());
        vm.setPhoto(model.getPhoto());
        vm.setCategory(model.getCategory());
        vm.setCompId(model.getCompId());
        vm.setWhomToMeet(model.getEmpId());

        // Set plant from the employee who creates the invite
        Optional<EmployeeMaster> creatorOpt = employeeRepo.findById(model.getEmpId());
        if (creatorOpt.isPresent()) {
            vm.setPlantId(creatorOpt.get().getPlantId());
        }

        vm.setVisitDate(model.getVisitDate() != null ? model.getVisitDate() : new Date());
        vm.setTime(model.getTime());
        vm.setAccept(false);
        vm.setApproved(false);
        vm.setExpired(false);
        vm.setDirectCheckIn(false);
        vm.setIsActive(true);
        vm.setIsUpdated(false);
        vm.setIsDeleted(false);
        vm.setCreatedBy(model.getEmpId());
        vm.setCreatedDate(new Date());
        vm = visitorRepo.save(vm);

        String otp = otpService.generateOTP();
        VisitorInviteHistory history = new VisitorInviteHistory();
        history.setVisitorId(vm.getVisitId());
        history.setInviteCode(otp);
        history.setCheckoutCode(null);
        history.setMail(false);
        history.setMobile(false);
        history.setCheckIn(false);
        history.setCheckOut(false);
        history.setIsActive(true);
        history.setIsUpdated(false);
        history.setIsDeleted(false);
        history.setCreatedBy(model.getEmpId());
        history.setCreatedDate(new Date());
        historyRepo.save(history);

        try {
            String qrText = "REG" + (vm.getRegNo() != null ? vm.getRegNo() : vm.getVisitId().toString());
            byte[] qrBytes = qrCodeService.generateQRCode(qrText, 300, 300);
            vm.setQr(qrText);
            visitorRepo.save(vm);

            String empName = "";
            String hostCompany = "";
            String hostPlant = "";
            Optional<EmployeeMaster> empOpt = employeeRepo.findById(model.getEmpId());
            if (empOpt.isPresent()) {
                EmployeeMaster emp = empOpt.get();
                // If employee has a reporting person, use that person's info for the email
                if (emp.getReportId() != null) {
                    Optional<EmployeeMaster> reportOpt = employeeRepo.findById(emp.getReportId());
                    if (reportOpt.isPresent()) {
                        EmployeeMaster reporter = reportOpt.get();
                        empName = (reporter.getFirstName() != null ? reporter.getFirstName() : "")
                            + (reporter.getLastName() != null ? " " + reporter.getLastName() : "");
                        if (reporter.getCompId() != null) {
                            Optional<CompanyMaster> compOpt = companyRepo.findById(reporter.getCompId());
                            if (compOpt.isPresent()) {
                                hostCompany = compOpt.get().getCompany() != null ? compOpt.get().getCompany() : "";
                            }
                        }
                        if (reporter.getPlantId() != null) {
                            Optional<PlantMaster> plantOpt = plantRepo.findById(reporter.getPlantId());
                            if (plantOpt.isPresent()) {
                                hostPlant = plantOpt.get().getPlantName() != null ? plantOpt.get().getPlantName() : "";
                            }
                        }
                    }
                } else {
                    empName = (emp.getFirstName() != null ? emp.getFirstName() : "")
                        + (emp.getLastName() != null ? " " + emp.getLastName() : "");
                    if (emp.getCompId() != null) {
                        Optional<CompanyMaster> compOpt = companyRepo.findById(emp.getCompId());
                        if (compOpt.isPresent()) {
                            hostCompany = compOpt.get().getCompany() != null ? compOpt.get().getCompany() : "";
                        }
                    }
                    if (emp.getPlantId() != null) {
                        Optional<PlantMaster> plantOpt = plantRepo.findById(emp.getPlantId());
                        if (plantOpt.isPresent()) {
                            hostPlant = plantOpt.get().getPlantName() != null ? plantOpt.get().getPlantName() : "";
                        }
                    }
                }
            }

            String visitorEmail = model.getpMail() != null ? model.getpMail() : model.getoMail();

            boolean hasReporter = creatorOpt.isPresent() && creatorOpt.get().getReportId() != null;

            if (hasReporter) {
                vm.setInvited(false);
                visitorRepo.save(vm);
                notificationService.createNotification(creatorOpt.get().getReportId(),
                    "Approval Required",
                    model.getName() + " has been invited and requires your approval.",
                    "alert", vm.getVisitId());
            } else {
                vm.setInvited(true);
                visitorRepo.save(vm);
                if (visitorEmail != null && !visitorEmail.isEmpty()) {
                    emailService.sendInviteEmail(visitorEmail, model.getName(), empName.trim(), otp, hostCompany, hostPlant);
                }
            }
        } catch (Exception e) {
            log.error("Error in visitor service operation", e);
        }

        VisitorManagementViewModel result = toViewModel(vm);
        result.setInviteCode(otp);
        result.setMsg("Visitor invited successfully");
        return result;
    }

    @Transactional
    public VisitorManagementViewModel acceptInvite(VisitorManagementViewModel model) {
        if (model.getVisitId() == null) {
            model.setMsg("VisitId is required");
            return model;
        }

        Optional<VisitorManagement> opt = visitorRepo.findById(model.getVisitId());
        if (opt.isEmpty()) {
            model.setMsg("Visitor not found");
            return model;
        }

        VisitorManagement vm = opt.get();
        vm.setName(model.getName());
        vm.setDesignation(model.getDesignation());
        vm.setCompany(model.getCompany());
        vm.setPurpose(model.getPurpose());
        vm.setpMail(model.getpMail());
        vm.setoMail(model.getoMail());
        vm.setMobile(model.getMobile());
        vm.setaMobile(model.getaMobile());
        vm.setPhoto(model.getPhoto());
        vm.setCategory(model.getCategory());
        vm.setCompId(model.getCompId());
        vm.setAccept(true);
        vm.setIsUpdated(true);
        vm.setLastUpdatedDate(new Date());
        visitorRepo.save(vm);

        String checkInOtp = otpService.generateOTP();
        String visitorEmail = model.getpMail() != null ? model.getpMail() : model.getoMail();
        if (visitorEmail == null || visitorEmail.isEmpty()) {
            visitorEmail = vm.getpMail() != null ? vm.getpMail() : vm.getoMail();
        }
        Optional<VisitorInviteHistory> histOpt = historyRepo.findFirstByVisitorIdAndIsDeletedOrderByLastUpdatedDateDesc(vm.getVisitId(), false);
        if (histOpt.isPresent()) {
            VisitorInviteHistory hist = histOpt.get();
            hist.setCheckInCode(checkInOtp);
            hist.setLastUpdatedDate(new Date());
            historyRepo.save(hist);
        }

        try {
            String empName = "";
            String empEmail = "";
            String hostCompany = "";
            String hostPlant = "";
            if (vm.getCreatedBy() != null) {
                Optional<EmployeeMaster> empOpt = employeeRepo.findById(vm.getCreatedBy());
                if (empOpt.isPresent()) {
                    EmployeeMaster emp = empOpt.get();
                    empName = (emp.getFirstName() != null ? emp.getFirstName() : "")
                        + (emp.getLastName() != null ? " " + emp.getLastName() : "");
                    empEmail = emp.getEmailId();
                    if (emp.getCompId() != null) {
                        Optional<CompanyMaster> compOpt = companyRepo.findById(emp.getCompId());
                        if (compOpt.isPresent()) {
                            hostCompany = compOpt.get().getCompany() != null ? compOpt.get().getCompany() : "";
                        }
                    }
                    if (emp.getPlantId() != null) {
                        Optional<PlantMaster> plantOpt = plantRepo.findById(emp.getPlantId());
                        if (plantOpt.isPresent()) {
                            hostPlant = plantOpt.get().getPlantName() != null ? plantOpt.get().getPlantName() : "";
                        }
                    }
                }
            }

            String visitDateStr = vm.getVisitDate() != null ? vm.getVisitDate().toString() : "";
            String visitorName = vm.getName() != null ? vm.getName() : "Visitor";

            if (empEmail != null && !empEmail.isEmpty()) {
                emailService.sendAcceptanceEmailToEmp(empEmail, visitorName, visitDateStr, checkInOtp);
            }
            if (visitorEmail != null && !visitorEmail.isEmpty()) {
                emailService.sendAcceptanceEmailToVisitor(visitorEmail, visitorName, empName.trim(), checkInOtp, hostCompany, hostPlant);
            }
            if (vm.getCreatedBy() != null) {
                notificationService.createNotification(vm.getCreatedBy(), "Invitation Accepted",
                    visitorName + " has accepted the invitation.", "accept", vm.getVisitId());
            }
        } catch (Exception e) {
            log.error("Error in visitor service operation", e);
        }

        VisitorManagementViewModel result = toViewModel(vm);
        result.setOtp(checkInOtp);
        result.setMsg("Invite accepted successfully");
        return result;
    }

    @Transactional
    public VisitorManagementViewModel directCheckIn(VisitorManagementViewModel model) {
        if (model.getEmpId() == null || model.getEmpId() == 0) {
            model.setMsg("EmpId is required");
            return model;
        }

        VisitorManagement vm = new VisitorManagement();
        vm.setName(model.getName());
        vm.setDesignation(model.getDesignation());
        vm.setCompany(model.getCompany());
        vm.setPurpose(model.getPurpose());
        vm.setpMail(model.getpMail());
        vm.setoMail(model.getoMail());
        vm.setMobile(model.getMobile());
        vm.setaMobile(model.getaMobile());
        vm.setPhoto(model.getPhoto());
        vm.setCategory(model.getCategory());
        vm.setCompId(model.getCompId());
        vm.setWhomToMeet(model.getWhomToMeet());

        // Set plant from the employee who does the direct check-in
        Optional<EmployeeMaster> creatorOpt = employeeRepo.findById(model.getEmpId());
        if (creatorOpt.isPresent()) {
            vm.setPlantId(creatorOpt.get().getPlantId());
        }

        vm.setVisitDate(model.getVisitDate() != null ? model.getVisitDate() : new Date());
        vm.setTime(model.getTime());
        vm.setIdCard(model.getIdCard());
        vm.setAccessories(model.getAccessories());
        // Set as pending approval (not auto-approved)
        vm.setInvited(true);
        vm.setAccept(false);
        vm.setApproved(false);
        vm.setDirectCheckIn(true);
        vm.setCheckIn(null);
        vm.setIsActive(true);
        vm.setIsUpdated(false);
        vm.setIsDeleted(false);
        vm.setCreatedBy(model.getEmpId());
        vm.setCreatedDate(new Date());

        vm = visitorRepo.save(vm);

        String otp = otpService.generateOTP();
        VisitorInviteHistory history = new VisitorInviteHistory();
        history.setVisitorId(vm.getVisitId());
        history.setInviteCode(otp);
        history.setCheckInCode(otp);
        history.setCheckIn(false);
        history.setIsActive(true);
        history.setIsDeleted(false);
        history.setCreatedBy(model.getEmpId());
        history.setCreatedDate(new Date());
        historyRepo.save(history);

        try {
            String empName = "";
            if (vm.getWhomToMeet() != null) {
                Optional<EmployeeMaster> empOpt = employeeRepo.findById(vm.getWhomToMeet());
                if (empOpt.isPresent()) {
                    empName = (empOpt.get().getFirstName() != null ? empOpt.get().getFirstName() : "")
                        + (empOpt.get().getLastName() != null ? " " + empOpt.get().getLastName() : "");
                    // Send approval request notification to the host
                    notificationService.createNotification(vm.getWhomToMeet(), "Visit Approval Request",
                        vm.getName() + " is requesting to meet you. Please approve or reject the visit.",
                        "check-circle", vm.getVisitId());
                }
            }
        } catch (Exception e) {
            log.error("Error in direct check-in notification", e);
        }

        VisitorManagementViewModel result = toViewModel(vm);
        result.setMsg("Approval request sent to host");
        return result;
    }

    @Transactional
    public VisitorManagementViewModel approveDirectCheckIn(Integer visitId, Integer empId) {
        VisitorManagementViewModel result = new VisitorManagementViewModel();
        Optional<VisitorManagement> opt = visitorRepo.findById(visitId);
        if (opt.isEmpty()) {
            result.setMsg("Visitor not found");
            return result;
        }

        VisitorManagement vm = opt.get();
        if (!Boolean.TRUE.equals(vm.getDirectCheckIn()) || Boolean.TRUE.equals(vm.getApproved())) {
            result.setMsg("Invalid or already approved");
            return result;
        }

        if (!vm.getWhomToMeet().equals(empId)) {
            result.setMsg("You are not authorized to approve this visit");
            return result;
        }

        vm.setApproved(true);
        vm.setAccept(true);
        vm.setIsUpdated(true);
        vm.setLastUpdatedDate(new Date());
        visitorRepo.save(vm);

        try {
            // Notify the plant admin (creator)
            if (vm.getCreatedBy() != null && vm.getName() != null) {
                notificationService.createNotification(vm.getCreatedBy(), "Visit Approved",
                    vm.getName() + " has been approved by the contact person.", "check-circle", vm.getVisitId());
            }
            // Notify the contact person (if different from creator)
            if (vm.getWhomToMeet() != null && !vm.getWhomToMeet().equals(vm.getCreatedBy()) && vm.getName() != null) {
                notificationService.createNotification(vm.getWhomToMeet(), "Visit Approved",
                    "You have approved the visit of " + vm.getName() + ".", "check-circle", vm.getVisitId());
            }
        } catch (Exception e) {
            log.error("Error in approval notification", e);
        }

        result = toViewModel(vm);
        result.setMsg("Visit approved successfully");
        return result;
    }

    @Transactional
    public VisitorManagementViewModel rejectDirectCheckIn(Integer visitId, Integer empId) {
        VisitorManagementViewModel result = new VisitorManagementViewModel();
        Optional<VisitorManagement> opt = visitorRepo.findById(visitId);
        if (opt.isEmpty()) {
            result.setMsg("Visitor not found");
            return result;
        }

        VisitorManagement vm = opt.get();
        if (!Boolean.TRUE.equals(vm.getDirectCheckIn()) || Boolean.TRUE.equals(vm.getApproved())) {
            result.setMsg("Invalid or already approved");
            return result;
        }

        if (!vm.getWhomToMeet().equals(empId)) {
            result.setMsg("You are not authorized to reject this visit");
            return result;
        }

        // Soft delete to reject
        vm.setIsDeleted(true);
        vm.setIsActive(false);
        vm.setIsUpdated(true);
        vm.setLastUpdatedDate(new Date());
        visitorRepo.save(vm);

        try {
            if (vm.getCreatedBy() != null && vm.getName() != null) {
                notificationService.createNotification(vm.getCreatedBy(), "Visit Rejected",
                    vm.getName() + " has been rejected by the contact person.", "close-circle", vm.getVisitId());
            }
            // Send rejection email to visitor
            String visitorEmail = vm.getpMail() != null ? vm.getpMail() : vm.getoMail();
            String hostCompany = "";
            if (vm.getCreatedBy() != null) {
                Optional<EmployeeMaster> empOpt = employeeRepo.findById(vm.getCreatedBy());
                if (empOpt.isPresent() && empOpt.get().getCompId() != null) {
                    Optional<CompanyMaster> compOpt = companyRepo.findById(empOpt.get().getCompId());
                    if (compOpt.isPresent()) {
                        hostCompany = compOpt.get().getCompany() != null ? compOpt.get().getCompany() : "";
                    }
                }
            }
            if (visitorEmail != null && !visitorEmail.isEmpty() && vm.getName() != null) {
                emailService.sendDeclinedEmailToVisitor(visitorEmail, vm.getName(), hostCompany);
            }
        } catch (Exception e) {
            log.error("Error in rejection notification", e);
        }

        result.setVisitId(vm.getVisitId());
        result.setMsg("Visit rejected successfully");
        return result;
    }

    public List<VisitorManagementViewModel> getDirectCheckInsByContact(Integer empId) {
        return visitorRepo.findDirectCheckInsByContact(empId).stream()
            .map(this::toViewModel)
            .collect(Collectors.toList());
    }

    public List<VisitorManagementViewModel> getDirectCheckInsByContactAndPlant(Integer empId, Integer plantId) {
        return visitorRepo.findDirectCheckInsByPlantAndContact(plantId, empId).stream()
            .map(this::toViewModel)
            .collect(Collectors.toList());
    }

    @Transactional
    public VisitorManagementViewModel checkIn(VisitorManagementViewModel model) {
        if (model.getVisitId() == null) {
            model.setMsg("VisitId is required");
            return model;
        }

        Optional<VisitorManagement> opt = visitorRepo.findById(model.getVisitId());
        if (opt.isEmpty()) {
            model.setMsg("Visitor not found");
            return model;
        }

        VisitorManagement vm = opt.get();

        if (Boolean.TRUE.equals(vm.getInvited()) && !Boolean.TRUE.equals(vm.getApproved()) && !Boolean.TRUE.equals(vm.getDirectCheckIn())) {
            model.setMsg("Visitor must self-verify before check-in");
            return model;
        }

        // For direct check-in, auto-accept on approval
        if (Boolean.TRUE.equals(vm.getDirectCheckIn())) {
            vm.setAccept(true);
        }

        vm.setIdCard(model.getIdCard());
        vm.setAccessories(model.getAccessories());
        vm.setApproved(true);
        vm.setCheckIn(new Date());
        vm.setIsUpdated(true);
        vm.setLastUpdatedDate(new Date());
        visitorRepo.save(vm);

        Optional<VisitorInviteHistory> histOpt = historyRepo.findFirstByVisitorIdAndIsDeletedOrderByLastUpdatedDateDesc(vm.getVisitId(), false);
        if (histOpt.isPresent()) {
            VisitorInviteHistory hist = histOpt.get();
            hist.setCheckIn(true);
            String checkoutOtp = otpService.generateOTP();
            hist.setCheckoutCode(checkoutOtp);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MINUTE, 2);
            hist.setCheckoutOtpExpiry(cal.getTime());
            hist.setLastUpdatedDate(new Date());
            historyRepo.save(hist);
        }

        try {
            String empEmail = "";
            String empNameForVisitor = "";
            String hostCompany = "";
            String hostPlant = "";
            String visitorEmail = vm.getpMail() != null ? vm.getpMail() : vm.getoMail();
            if (vm.getCreatedBy() != null) {
                Optional<EmployeeMaster> empOpt = employeeRepo.findById(vm.getCreatedBy());
                if (empOpt.isPresent()) {
                    EmployeeMaster emp = empOpt.get();
                    empEmail = emp.getEmailId();
                    empNameForVisitor = (emp.getFirstName() != null ? emp.getFirstName() : "")
                        + (emp.getLastName() != null ? " " + emp.getLastName() : "");
                    if (emp.getCompId() != null) {
                        Optional<CompanyMaster> compOpt = companyRepo.findById(emp.getCompId());
                        if (compOpt.isPresent()) hostCompany = compOpt.get().getCompany() != null ? compOpt.get().getCompany() : "";
                    }
                    if (emp.getPlantId() != null) {
                        Optional<PlantMaster> plantOpt = plantRepo.findById(emp.getPlantId());
                        if (plantOpt.isPresent()) hostPlant = plantOpt.get().getPlantName() != null ? plantOpt.get().getPlantName() : "";
                    }
                }
            }
            // Also notify the contact person (WhomToMeet) for direct check-ins
            if (Boolean.TRUE.equals(vm.getDirectCheckIn()) && vm.getWhomToMeet() != null
                && !vm.getWhomToMeet().equals(vm.getCreatedBy()) && vm.getName() != null) {
                notificationService.createNotification(vm.getWhomToMeet(), "Visitor Checked In",
                    vm.getName() + " has checked in and is ready to meet you.", "checkin", vm.getVisitId());
                // Also send email to contact person
                Optional<EmployeeMaster> contactOpt = employeeRepo.findById(vm.getWhomToMeet());
                if (contactOpt.isPresent()) {
                    String contactEmail = contactOpt.get().getEmailId();
                    if (contactEmail != null && !contactEmail.isEmpty() && !contactEmail.equals(empEmail)) {
                        emailService.sendCheckInEmailToEmp(contactEmail, vm.getName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error in visitor service operation", e);
        }

        VisitorManagementViewModel result = toViewModel(vm);
        result.setMsg("Check-in successful");
        return result;
    }

    @Transactional
    public VisitorManagementViewModel plantAdminCheckIn(VisitorManagementViewModel model) {
        if (model.getVisitId() == null) {
            model.setMsg("VisitId is required");
            return model;
        }

        Optional<VisitorManagement> opt = visitorRepo.findById(model.getVisitId());
        if (opt.isEmpty()) {
            model.setMsg("Visitor not found");
            return model;
        }

        VisitorManagement vm = opt.get();

        // Validate check-in code
        if (model.getOtp() == null || model.getOtp().isEmpty()) {
            model.setMsg("Check-in code is required");
            return model;
        }
        Optional<VisitorInviteHistory> histOpt = historyRepo.findByInviteCodeAndIsDeleted(model.getOtp(), false);
        if (histOpt.isEmpty() || !histOpt.get().getVisitorId().equals(vm.getVisitId())) {
            model.setMsg("Invalid check-in code");
            return model;
        }

        if (vm.getIsDeleted() || vm.getCheckIn() != null) {
            model.setMsg("Visitor already checked in or cancelled");
            return model;
        }

        if (model.getIdCard() == null || model.getIdCard().trim().isEmpty()) {
            model.setMsg("ID Card is required for check-in");
            return model;
        }

        vm.setApproved(true);
        vm.setAccept(true);
        vm.setIdCard(model.getIdCard());
        vm.setAccessories(model.getAccessories());
        vm.setCheckIn(new Date());
        vm.setIsUpdated(true);
        vm.setLastUpdatedDate(new Date());
        visitorRepo.save(vm);

        Optional<VisitorInviteHistory> hOpt = historyRepo.findFirstByVisitorIdAndIsDeletedOrderByLastUpdatedDateDesc(vm.getVisitId(), false);
        if (hOpt.isPresent()) {
            VisitorInviteHistory hist = hOpt.get();
            hist.setCheckIn(true);
            String checkoutOtp = otpService.generateOTP();
            hist.setCheckoutCode(checkoutOtp);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.add(java.util.Calendar.MINUTE, 2);
            hist.setCheckoutOtpExpiry(cal.getTime());
            hist.setLastUpdatedDate(new Date());
            historyRepo.save(hist);
        }

        try {
            String empEmail = "";
            String empNameForVisitor = "";
            String hostCompany = "";
            String hostPlant = "";
            String visitorEmail = vm.getpMail() != null ? vm.getpMail() : vm.getoMail();
            if (vm.getCreatedBy() != null) {
                Optional<EmployeeMaster> empOpt = employeeRepo.findById(vm.getCreatedBy());
                if (empOpt.isPresent()) {
                    EmployeeMaster emp = empOpt.get();
                    empEmail = emp.getEmailId();
                    String fullName = emp.getFirstName() != null ? emp.getFirstName() : "";
                    if (emp.getLastName() != null && !emp.getLastName().isEmpty()) {
                        fullName += " " + emp.getLastName();
                    }
                    empNameForVisitor = fullName.trim();
                    if (emp.getCompId() != null) {
                        Optional<CompanyMaster> compOpt = companyRepo.findById(emp.getCompId());
                        if (compOpt.isPresent()) hostCompany = compOpt.get().getCompany() != null ? compOpt.get().getCompany() : "";
                    }
                    if (emp.getPlantId() != null) {
                        Optional<PlantMaster> plantOpt = plantRepo.findById(emp.getPlantId());
                        if (plantOpt.isPresent()) hostPlant = plantOpt.get().getPlantName() != null ? plantOpt.get().getPlantName() : "";
                    }
                }
            }
            if (empEmail != null && !empEmail.isEmpty() && vm.getName() != null) {
                emailService.sendCheckInEmailToEmp(empEmail, vm.getName());
            }
            if (visitorEmail != null && !visitorEmail.isEmpty() && vm.getName() != null) {
                String CheckoutCode = hOpt.map(h -> h.getCheckoutCode() != null ? h.getCheckoutCode() : "").orElse("");
                emailService.sendCheckInEmailToVisitor(visitorEmail, vm.getName(), empNameForVisitor, hostCompany, hostPlant, CheckoutCode);
            }
            if (vm.getCreatedBy() != null && vm.getName() != null) {
                notificationService.createNotification(vm.getCreatedBy(), "Visitor Checked In",
                    vm.getName() + " has checked in.", "checkin", vm.getVisitId());
            }
        } catch (Exception e) {
            log.error("Error in visitor service operation", e);
        }

        VisitorManagementViewModel result = toViewModel(vm);
        result.setMsg("Check-in successful");
        return result;
    }

    @Transactional
    public VisitorManagementViewModel plantAdminCheckOut(VisitorManagementViewModel model) {
        if (model.getVisitId() == null) {
            model.setMsg("VisitId is required");
            return model;
        }

        Optional<VisitorManagement> opt = visitorRepo.findById(model.getVisitId());
        if (opt.isEmpty()) {
            model.setMsg("Visitor not found");
            return model;
        }

        VisitorManagement vm = opt.get();

        if (vm.getCheckIn() == null) {
            model.setMsg("Visitor has not checked in yet");
            return model;
        }
        if (vm.getCheckOut() != null) {
            model.setMsg("Visitor has already checked out");
            return model;
        }

        // Validate check-out OTP
        if (model.getOtp() == null || model.getOtp().isEmpty()) {
            model.setMsg("Check-out OTP is required");
            return model;
        }
        Optional<VisitorInviteHistory> histOpt = historyRepo.findByCheckoutCodeAndIsDeleted(model.getOtp(), false);
        if (histOpt.isEmpty() || !histOpt.get().getVisitorId().equals(vm.getVisitId())) {
            model.setMsg("Invalid check-out code");
            return model;
        }

        if (histOpt.get().getCheckoutOtpExpiry() != null && histOpt.get().getCheckoutOtpExpiry().before(new Date())) {
            model.setMsg("Check-out OTP has expired. Please request a new one.");
            return model;
        }

        if (vm.getCheckIn() == null) {
            model.setMsg("Visitor has not checked in yet");
            return model;
        }

        vm.setCheckOut(new Date());
        vm.setIsUpdated(true);
        vm.setLastUpdatedDate(new Date());
        visitorRepo.save(vm);

        VisitorInviteHistory hist = histOpt.get();
        hist.setCheckOut(true);
        hist.setLastUpdatedDate(new Date());
        historyRepo.save(hist);

        try {
            String empEmail = "";
            String hostCompany = "";
            String visitorEmail = vm.getpMail() != null ? vm.getpMail() : vm.getoMail();
            String CheckoutCode = hist.getCheckoutCode() != null ? hist.getCheckoutCode() : "N/A";
            if (vm.getCreatedBy() != null) {
                Optional<EmployeeMaster> empOpt = employeeRepo.findById(vm.getCreatedBy());
                if (empOpt.isPresent()) {
                    EmployeeMaster emp = empOpt.get();
                    empEmail = emp.getEmailId();
                    if (emp.getCompId() != null) {
                        Optional<CompanyMaster> compOpt = companyRepo.findById(emp.getCompId());
                        if (compOpt.isPresent()) hostCompany = compOpt.get().getCompany() != null ? compOpt.get().getCompany() : "";
                    }
                }
            }
            if (empEmail != null && !empEmail.isEmpty() && vm.getName() != null) {
                emailService.sendCheckOutEmailToEmp(empEmail, vm.getName());
            }
            if (visitorEmail != null && !visitorEmail.isEmpty() && vm.getName() != null) {
                emailService.sendCheckOutEmailToVisitor(visitorEmail, vm.getName(), CheckoutCode, hostCompany);
            }
            if (vm.getCreatedBy() != null && vm.getName() != null) {
                notificationService.createNotification(vm.getCreatedBy(), "Visitor Checked Out",
                    vm.getName() + " has checked out.", "checkout", vm.getVisitId());
            }
        } catch (Exception e) {
            log.error("Error in visitor service operation", e);
        }

        VisitorManagementViewModel result = toViewModel(vm);
        result.setMsg("Check-out successful");
        return result;
    }

    @Transactional
    public VisitorManagementViewModel regenerateCheckoutOtp(Integer visitId) {
        VisitorManagementViewModel result = new VisitorManagementViewModel();
        Optional<VisitorManagement> vmOpt = visitorRepo.findById(visitId);
        if (vmOpt.isEmpty()) {
            result.setMsg("Visitor not found"); return result;
        }
        VisitorManagement vm = vmOpt.get();
        if (vm.getCheckIn() == null) {
            result.setMsg("Visitor has not checked in yet"); return result;
        }
        if (vm.getCheckOut() != null) {
            result.setMsg("Visitor has already checked out"); return result;
        }
        Optional<VisitorInviteHistory> histOpt = historyRepo.findFirstByVisitorIdAndIsDeletedOrderByLastUpdatedDateDesc(visitId, false);
        if (histOpt.isEmpty()) {
            result.setMsg("Visitor history not found"); return result;
        }
        VisitorInviteHistory hist = histOpt.get();
        String newOtp = otpService.generateOTP();
        hist.setCheckoutCode(newOtp);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.MINUTE, 2);
        hist.setCheckoutOtpExpiry(cal.getTime());
        hist.setLastUpdatedDate(new Date());
        historyRepo.save(hist);
        if (vm.getCreatedBy() != null && vm.getName() != null) {
            notificationService.createNotification(vm.getCreatedBy(), "Checkout OTP Regenerated",
                "A new checkout OTP has been generated for " + vm.getName() + ".", "refresh", visitId);
        }
        // Send email to visitor with new OTP
        String visitorEmail = vm.getpMail() != null ? vm.getpMail() : vm.getoMail();
        if (visitorEmail != null && !visitorEmail.isEmpty()) {
            String hostCompany = "";
            if (vm.getCreatedBy() != null) {
                Optional<EmployeeMaster> empOpt = employeeRepo.findById(vm.getCreatedBy());
                if (empOpt.isPresent() && empOpt.get().getCompId() != null) {
                    Optional<CompanyMaster> compOpt = companyRepo.findById(empOpt.get().getCompId());
                    if (compOpt.isPresent()) hostCompany = compOpt.get().getCompany() != null ? compOpt.get().getCompany() : "";
                }
            }
            emailService.sendRegeneratedOtpEmail(visitorEmail, vm.getName(), newOtp, hostCompany);
        }
        // Use toViewModel to get fully populated response (CheckInCode, status, etc.)
        result = toViewModel(vm);
        result.setOtp(newOtp);
        result.setCheckInCode(newOtp);
        result.setMsg("New checkout OTP generated. Valid for 2 minutes.");
        return result;
    }

    @Transactional
    public VisitorManagementViewModel checkOut(VisitorManagementViewModel model) {
        if (model.getVisitId() == null) {
            model.setMsg("VisitId is required");
            return model;
        }

        Optional<VisitorManagement> opt = visitorRepo.findById(model.getVisitId());
        if (opt.isEmpty()) {
            model.setMsg("Visitor not found");
            return model;
        }

        VisitorManagement vm = opt.get();
        vm.setCheckOut(new Date());
        vm.setIsUpdated(true);
        vm.setLastUpdatedDate(new Date());
        visitorRepo.save(vm);

        Optional<VisitorInviteHistory> histOpt = historyRepo.findFirstByVisitorIdAndIsDeletedOrderByLastUpdatedDateDesc(vm.getVisitId(), false);
        if (histOpt.isPresent()) {
        VisitorInviteHistory hist = histOpt.get();
        hist.setCheckOut(true);
        hist.setLastUpdatedDate(new Date());
        historyRepo.save(hist);

        try {
            String empEmail = "";
            String hostCompany = "";
            String visitorEmail = vm.getpMail() != null ? vm.getpMail() : vm.getoMail();
            String CheckoutCode = hist.getCheckoutCode() != null ? hist.getCheckoutCode() : "N/A";
            if (vm.getCreatedBy() != null) {
                Optional<EmployeeMaster> empOpt = employeeRepo.findById(vm.getCreatedBy());
                if (empOpt.isPresent()) {
                    EmployeeMaster emp = empOpt.get();
                    empEmail = emp.getEmailId();
                    if (emp.getCompId() != null) {
                        Optional<CompanyMaster> compOpt = companyRepo.findById(emp.getCompId());
                        if (compOpt.isPresent()) hostCompany = compOpt.get().getCompany() != null ? compOpt.get().getCompany() : "";
                    }
                }
            }
            if (empEmail != null && !empEmail.isEmpty() && vm.getName() != null) {
                emailService.sendCheckOutEmailToEmp(empEmail, vm.getName());
            }
            if (visitorEmail != null && !visitorEmail.isEmpty() && vm.getName() != null) {
                emailService.sendCheckOutEmailToVisitor(visitorEmail, vm.getName(), CheckoutCode, hostCompany);
            }
            if (vm.getCreatedBy() != null && vm.getName() != null) {
                notificationService.createNotification(vm.getCreatedBy(), "Visitor Checked Out",
                    vm.getName() + " has checked out.", "checkout", vm.getVisitId());
            }
        } catch (Exception e) {
            log.error("Error in visitor service operation", e);
        }

        VisitorManagementViewModel result = toViewModel(vm);
        result.setMsg("Check-out successful");
        return result;
    }

        try {
            String empEmail = "";
            String hostCompany = "";
            String visitorEmail = vm.getpMail() != null ? vm.getpMail() : vm.getoMail();
            String CheckoutCode = "";
            if (histOpt.isPresent()) {
                CheckoutCode = histOpt.get().getCheckoutCode() != null ? histOpt.get().getCheckoutCode() : "";
            }
            if (vm.getCreatedBy() != null) {
                Optional<EmployeeMaster> empOpt = employeeRepo.findById(vm.getCreatedBy());
                if (empOpt.isPresent()) {
                    EmployeeMaster emp = empOpt.get();
                    empEmail = emp.getEmailId();
                    if (CheckoutCode.isEmpty()) CheckoutCode = "N/A";
                    if (emp.getCompId() != null) {
                        Optional<CompanyMaster> compOpt = companyRepo.findById(emp.getCompId());
                        if (compOpt.isPresent()) hostCompany = compOpt.get().getCompany() != null ? compOpt.get().getCompany() : "";
                    }
                }
            }
            // Send checkout email to contact person
            if (empEmail != null && !empEmail.isEmpty() && vm.getName() != null) {
                emailService.sendCheckOutEmailToEmp(empEmail, vm.getName());
            }
            // Send checkout email to visitor (no OTP for direct check-ins)
            if (visitorEmail != null && !visitorEmail.isEmpty() && vm.getName() != null) {
                if (Boolean.TRUE.equals(vm.getDirectCheckIn())) {
                    emailService.sendDirectCheckOutEmailToVisitor(visitorEmail, vm.getName(), hostCompany);
                } else {
                    emailService.sendCheckOutEmailToVisitor(visitorEmail, vm.getName(), CheckoutCode, hostCompany);
                }
            }
            if (vm.getCreatedBy() != null && vm.getName() != null) {
                notificationService.createNotification(vm.getCreatedBy(), "Visitor Checked Out",
                    vm.getName() + " has checked out.", "checkout", vm.getVisitId());
            }
            if (Boolean.TRUE.equals(vm.getDirectCheckIn()) && vm.getWhomToMeet() != null
                && !vm.getWhomToMeet().equals(vm.getCreatedBy()) && vm.getName() != null) {
                notificationService.createNotification(vm.getWhomToMeet(), "Visitor Checked Out",
                    vm.getName() + " has checked out.", "checkout", vm.getVisitId());
                Optional<EmployeeMaster> contactOpt = employeeRepo.findById(vm.getWhomToMeet());
                if (contactOpt.isPresent()) {
                    String contactEmail = contactOpt.get().getEmailId();
                    if (contactEmail != null && !contactEmail.isEmpty() && !contactEmail.equals(empEmail)) {
                        emailService.sendCheckOutEmailToEmp(contactEmail, vm.getName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error in visitor service operation", e);
        }

        VisitorManagementViewModel result = toViewModel(vm);
        result.setMsg("Check-out successful");
        return result;
    }

    @Transactional
    public List<VisitorManagementViewModel> getPendingApprovals(Integer reporterEmpId) {
        List<EmployeeMaster> reporters = employeeRepo.findByIsActiveAndIsDeleted(true, false).stream()
            .filter(e -> e.getReportId() != null && e.getReportId().equals(reporterEmpId))
            .collect(Collectors.toList());
        if (reporters.isEmpty()) return new ArrayList<>();
        List<Integer> creatorIds = reporters.stream().map(EmployeeMaster::getEmpId).collect(Collectors.toList());
        return visitorRepo.findPendingApprovals(creatorIds).stream()
            .map(this::toViewModel).collect(Collectors.toList());
    }

    @Transactional
    public VisitorManagementViewModel approvePendingInvite(Integer visitId) {
        VisitorManagementViewModel result = new VisitorManagementViewModel();
        Optional<VisitorManagement> opt = visitorRepo.findById(visitId);
        if (opt.isEmpty()) {
            result.setMsg("Visitor not found"); return result;
        }
        VisitorManagement vm = opt.get();
        vm.setInvited(true);
        vm.setIsUpdated(true);
        vm.setLastUpdatedDate(new Date());
        visitorRepo.save(vm);
        // Send invite email to visitor
        try {
            String otp = otpService.generateOTP();
            Optional<VisitorInviteHistory> histOpt = historyRepo.findFirstByVisitorIdAndIsDeletedOrderByLastUpdatedDateDesc(visitId, false);
            if (histOpt.isPresent()) {
                VisitorInviteHistory hist = histOpt.get();
                hist.setInviteCode(otp);
                historyRepo.save(hist);
            }
            String visitorEmail = vm.getpMail() != null ? vm.getpMail() : vm.getoMail();
            String empName = "";
            String hostCompany = "";
            if (vm.getCreatedBy() != null) {
                Optional<EmployeeMaster> empOpt = employeeRepo.findById(vm.getCreatedBy());
                if (empOpt.isPresent()) {
                    EmployeeMaster emp = empOpt.get();
                    empName = (emp.getFirstName() != null ? emp.getFirstName() : "") + (emp.getLastName() != null ? " " + emp.getLastName() : "");
                    if (emp.getCompId() != null) {
                        Optional<CompanyMaster> compOpt = companyRepo.findById(emp.getCompId());
                        if (compOpt.isPresent()) hostCompany = compOpt.get().getCompany() != null ? compOpt.get().getCompany() : "";
                    }
                }
            }
            if (visitorEmail != null && !visitorEmail.isEmpty()) {
                emailService.sendInviteEmail(visitorEmail, vm.getName(), empName.trim(), otp, hostCompany, "");
            }
        } catch (Exception e) {
            log.error("Error sending invite after approval", e);
        }
        result.setMsg("Invite approved and email sent");
        return result;
    }

    @Transactional
    public VisitorManagementViewModel rejectPendingInvite(Integer visitId) {
        VisitorManagementViewModel result = new VisitorManagementViewModel();
        Optional<VisitorManagement> opt = visitorRepo.findById(visitId);
        if (opt.isEmpty()) {
            result.setMsg("Visitor not found"); return result;
        }
        VisitorManagement vm = opt.get();
        vm.setIsDeleted(true);
        vm.setIsActive(false);
        vm.setIsUpdated(true);
        vm.setLastUpdatedDate(new Date());
        visitorRepo.save(vm);
        result.setVisitId(vm.getVisitId());
        result.setMsg("Invite rejected");
        return result;
    }

    public VisitorManagementViewModel cancelInvite(Integer visitId) {
        VisitorManagementViewModel result = new VisitorManagementViewModel();
        Optional<VisitorManagement> opt = visitorRepo.findById(visitId);
        if (opt.isEmpty()) {
            result.setMsg("Visitor not found");
            return result;
        }

        VisitorManagement vm = opt.get();
        vm.setIsDeleted(true);
        vm.setIsActive(false);
        vm.setIsUpdated(true);
        vm.setLastUpdatedDate(new Date());
        visitorRepo.save(vm);

        result.setVisitId(vm.getVisitId());
        result.setMsg("Invite cancelled successfully");
        return result;
    }

    public VisitorManagementViewModel getAllInvite() {
        List<VisitorManagement> list = visitorRepo.findByIsDeletedOrderByVisitIdDesc(false);
        VisitorManagementViewModel result = new VisitorManagementViewModel();
        result.setVisitorList(list.stream().map(this::toViewModel).collect(Collectors.toList()));
        result.setMsg("Success");
        return result;
    }

    public VisitorManagementViewModel getAllInviteByPlant(Integer plantId) {
        if (plantId == null) return getAllInvite();
        List<VisitorManagement> list = visitorRepo.findByPlantId(plantId).stream()
            .filter(v -> Boolean.TRUE.equals(v.getInvited()))
            .collect(Collectors.toList());
        VisitorManagementViewModel result = new VisitorManagementViewModel();
        result.setVisitorList(list.stream().map(this::toViewModel).collect(Collectors.toList()));
        result.setMsg("Success");
        return result;
    }

    public VisitorManagementViewModel getEmployeeInvites(Integer empId) {
        List<VisitorManagement> list = visitorRepo.findByCreatedByAndIsDeletedOrderByVisitDateDesc(empId, false);
        VisitorManagementViewModel result = new VisitorManagementViewModel();
        result.setVisitorList(list.stream().map(this::toViewModel).collect(Collectors.toList()));
        result.setMsg("Success");
        return result;
    }

    public VisitorManagementViewModel getEmployeeInvitesByPlant(Integer empId, Integer plantId) {
        if (plantId == null) return getEmployeeInvites(empId);
        List<VisitorManagement> list = visitorRepo.findByPlantIdAndCreatedBy(plantId, empId);
        VisitorManagementViewModel result = new VisitorManagementViewModel();
        result.setVisitorList(list.stream().map(this::toViewModel).collect(Collectors.toList()));
        result.setMsg("Success");
        return result;
    }

    public List<VisitorManagementViewModel> visitFilter(FilterViewModel filter) {
        List<VisitorManagement> all = visitorRepo.findAll();

        // Role-based filtering
        if (filter.getPlantId() != null) {
            all = all.stream()
                .filter(v -> filter.getPlantId().equals(v.getPlantId()))
                .collect(Collectors.toList());
        }
        if (filter.getEmpId() != null) {
            all = all.stream()
                .filter(v -> filter.getEmpId().equals(v.getCreatedBy()) || filter.getEmpId().equals(v.getWhomToMeet()))
                .collect(Collectors.toList());
        }

        if (filter.getFromDate() != null && filter.getToDate() != null) {
            Calendar toCal = Calendar.getInstance();
            toCal.setTime(filter.getToDate());
            toCal.set(Calendar.HOUR_OF_DAY, 23);
            toCal.set(Calendar.MINUTE, 59);
            toCal.set(Calendar.SECOND, 59);
            Date toEnd = toCal.getTime();

            Date fromStart = filter.getFromDate();
            all = all.stream()
                .filter(v -> v.getVisitDate() != null && !v.getVisitDate().before(fromStart) && !v.getVisitDate().after(toEnd))
                .collect(Collectors.toList());
        }

        if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
            String status = filter.getStatus().toUpperCase();
            all = all.stream()
                .filter(v -> {
                    if ("INVITED".equals(status)) return Boolean.TRUE.equals(v.getInvited()) && !Boolean.TRUE.equals(v.getAccept());
                    if ("INVITE ACCEPTED".equals(status)) return Boolean.TRUE.equals(v.getAccept()) && v.getCheckIn() == null;
                    if ("CHECKED IN".equals(status)) return v.getCheckIn() != null && v.getCheckOut() == null;
                    if ("CHECKED OUT".equals(status)) return v.getCheckOut() != null;
                    if ("EXPIRED".equals(status)) return Boolean.TRUE.equals(v.getExpired());
                    return true;
                })
                .collect(Collectors.toList());
        }

        all.sort((a, b) -> {
            if (a.getVisitDate() != null && b.getVisitDate() != null) {
                int todayFirst = Boolean.compare(isToday(b.getVisitDate()), isToday(a.getVisitDate()));
                if (todayFirst != 0) return todayFirst;
                int dateCmp = b.getVisitDate().compareTo(a.getVisitDate());
                if (dateCmp != 0) return dateCmp;
            }
            return b.getVisitId().compareTo(a.getVisitId());
        });

        return all.stream().map(this::toViewModel).collect(Collectors.toList());
    }

    private boolean isToday(Date date) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
            && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public DashboardViewModel getHrDashboardCounts() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date todayStart = cal.getTime();

        DashboardViewModel d = new DashboardViewModel();
        long todayCount = visitorRepo.countTodayVisitors(todayStart);
        d.setTotalVisitors(todayCount);
        d.setTodayVisitors(todayCount);
        d.setTotalCheckedIn(visitorRepo.countTodayCheckIns(todayStart));
        d.setTotalCheckedOut(visitorRepo.countTodayCheckOuts(todayStart));
        return d;
    }

    @Transactional
    public VisitorManagementViewModel visitorCheckIn(VisitorManagementViewModel model) {
        if (model.getVisitId() == null) {
            model.setMsg("VisitId is required");
            return model;
        }
        Optional<VisitorManagement> opt = visitorRepo.findById(model.getVisitId());
        if (opt.isEmpty()) {
            model.setMsg("Visitor not found");
            return model;
        }
        VisitorManagement vm = opt.get();
        vm.setCheckIn(new Date());
        vm.setIsUpdated(true);
        vm.setLastUpdatedDate(new Date());
        visitorRepo.save(vm);

        Optional<VisitorInviteHistory> histOpt = historyRepo.findFirstByVisitorIdAndIsDeletedOrderByLastUpdatedDateDesc(vm.getVisitId(), false);
        if (histOpt.isPresent()) {
            VisitorInviteHistory hist = histOpt.get();
            hist.setCheckIn(true);
            hist.setLastUpdatedDate(new Date());
            historyRepo.save(hist);
        }

        try {
            String empEmail = "";
            String hostCompany = "";
            String hostPlant = "";
            String empNameForVisitor = "";
            String visitorEmail = vm.getpMail() != null ? vm.getpMail() : vm.getoMail();
                String CheckoutCode = histOpt.map(h -> h.getCheckoutCode() != null ? h.getCheckoutCode() : "").orElse("");
            if (vm.getCreatedBy() != null) {
                Optional<EmployeeMaster> empOpt = employeeRepo.findById(vm.getCreatedBy());
                if (empOpt.isPresent()) {
                    EmployeeMaster emp = empOpt.get();
                    empEmail = emp.getEmailId();
                    String fullName = emp.getFirstName() != null ? emp.getFirstName() : "";
                    if (emp.getLastName() != null && !emp.getLastName().isEmpty()) {
                        fullName += " " + emp.getLastName();
                    }
                    empNameForVisitor = fullName.trim();
                    if (emp.getCompId() != null) {
                        Optional<CompanyMaster> compOpt = companyRepo.findById(emp.getCompId());
                        if (compOpt.isPresent()) hostCompany = compOpt.get().getCompany() != null ? compOpt.get().getCompany() : "";
                    }
                    if (emp.getPlantId() != null) {
                        Optional<PlantMaster> plantOpt = plantRepo.findById(emp.getPlantId());
                        if (plantOpt.isPresent()) hostPlant = plantOpt.get().getPlantName() != null ? plantOpt.get().getPlantName() : "";
                    }
                }
            }
            if (empEmail != null && !empEmail.isEmpty() && vm.getName() != null) {
                emailService.sendCheckInEmailToEmp(empEmail, vm.getName());
            }
            if (visitorEmail != null && !visitorEmail.isEmpty() && vm.getName() != null) {
                emailService.sendCheckInEmailToVisitor(visitorEmail, vm.getName(),
                    empNameForVisitor.isEmpty() ? "the host" : empNameForVisitor,
                    hostCompany, hostPlant, CheckoutCode);
            }
            if (vm.getCreatedBy() != null && vm.getName() != null) {
                notificationService.createNotification(vm.getCreatedBy(), "Visitor Checked In",
                    vm.getName() + " has checked in.", "checkin", vm.getVisitId());
            }
        } catch (Exception e) {
            log.error("Error in visitor service operation", e);
        }

        VisitorManagementViewModel result = toViewModel(vm);
        result.setMsg("Check-in successful");
        return result;
    }

    @Transactional
    public VisitorManagementViewModel visitorCheckOut(VisitorManagementViewModel model) {
        if (model.getOtp() == null || model.getOtp().trim().isEmpty()) {
            model.setMsg("Check-out code (OTP) is required");
            return model;
        }

        Optional<VisitorInviteHistory> histOpt = historyRepo.findByCheckoutCodeAndIsDeleted(model.getOtp().trim(), false);
        if (histOpt.isEmpty()) {
            model.setMsg("Invalid check-out code");
            return model;
        }

        if (histOpt.get().getCheckoutOtpExpiry() != null && histOpt.get().getCheckoutOtpExpiry().before(new Date())) {
            model.setMsg("Check-out OTP has expired. Please request a new one.");
            return model;
        }

        Optional<VisitorManagement> vmOpt = visitorRepo.findById(histOpt.get().getVisitorId());
        if (vmOpt.isEmpty()) {
            model.setMsg("Visitor not found");
            return model;
        }

        VisitorManagement vm = vmOpt.get();
        if (vm.getCheckIn() == null) {
            model.setMsg("Visitor has not checked in yet");
            return model;
        }
        if (vm.getCheckOut() != null) {
            model.setMsg("Visitor has already checked out");
            return model;
        }

        vm.setCheckOut(new Date());
        vm.setIsUpdated(true);
        vm.setLastUpdatedDate(new Date());
        visitorRepo.save(vm);

        VisitorInviteHistory hist = histOpt.get();
        hist.setCheckOut(true);
        hist.setLastUpdatedDate(new Date());
        historyRepo.save(hist);

        try {
            String empEmail = "";
            String hostCompany = "";
            String visitorEmail = vm.getpMail() != null ? vm.getpMail() : vm.getoMail();
            String CheckoutCode = hist.getCheckoutCode() != null ? hist.getCheckoutCode() : "N/A";
            if (vm.getCreatedBy() != null) {
                Optional<EmployeeMaster> empOpt = employeeRepo.findById(vm.getCreatedBy());
                if (empOpt.isPresent()) {
                    EmployeeMaster emp = empOpt.get();
                    empEmail = emp.getEmailId();
                    if (emp.getCompId() != null) {
                        Optional<CompanyMaster> compOpt = companyRepo.findById(emp.getCompId());
                        if (compOpt.isPresent()) hostCompany = compOpt.get().getCompany() != null ? compOpt.get().getCompany() : "";
                    }
                }
            }
            if (empEmail != null && !empEmail.isEmpty() && vm.getName() != null) {
                emailService.sendCheckOutEmailToEmp(empEmail, vm.getName());
            }
            if (visitorEmail != null && !visitorEmail.isEmpty() && vm.getName() != null) {
                emailService.sendCheckOutEmailToVisitor(visitorEmail, vm.getName(), CheckoutCode, hostCompany);
            }
            if (vm.getCreatedBy() != null && vm.getName() != null) {
                notificationService.createNotification(vm.getCreatedBy(), "Visitor Checked Out",
                    vm.getName() + " has checked out.", "checkout", vm.getVisitId());
            }
        } catch (Exception e) {
            log.error("Error in visitor service operation", e);
        }

        VisitorManagementViewModel result = toViewModel(vm);
        result.setMsg("Check-out successful");
        return result;
    }

    @Transactional
    public VisitorManagementViewModel visitorDirectCheckIn(VisitorManagementViewModel model) {
        VisitorManagement vm = new VisitorManagement();
        vm.setName(model.getName());
        vm.setDesignation(model.getDesignation());
        vm.setCompany(model.getCompany());
        vm.setPurpose(model.getPurpose());
        vm.setpMail(model.getpMail());
        vm.setoMail(model.getoMail());
        vm.setMobile(model.getMobile());
        vm.setaMobile(model.getaMobile());
        vm.setPhoto(model.getPhoto());
        vm.setCategory(model.getCategory());
        vm.setCompId(model.getCompId());
        vm.setVisitDate(new Date());
        vm.setTime(model.getTime());
        vm.setAccessories(model.getAccessories());
        vm.setInvited(true);
        vm.setAccept(true);
        vm.setApproved(true);
        vm.setDirectCheckIn(true);
        vm.setCheckIn(new Date());
        vm.setIsActive(true);
        vm.setIsUpdated(false);
        vm.setIsDeleted(false);
        vm.setCreatedDate(new Date());

        vm = visitorRepo.save(vm);

        String checkInCode = otpService.generateOTP();
        VisitorInviteHistory history = new VisitorInviteHistory();
        history.setVisitorId(vm.getVisitId());
        history.setInviteCode("0");
        history.setCheckoutCode(checkInCode);
        history.setCheckIn(true);
        history.setIsActive(true);
        history.setIsDeleted(false);
        history.setCreatedDate(new Date());
        historyRepo.save(history);

        VisitorManagementViewModel result = toViewModel(vm);
        result.setOtp(checkInCode);
        result.setMsg("Direct check-in successful");
        return result;
    }

    @Transactional
    public void expireExpiredInvites() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date yesterday = cal.getTime();

        List<VisitorManagement> expired = visitorRepo.findByVisitDateBeforeAndIsDeleted(yesterday, false);
        for (VisitorManagement vm : expired) {
            if (!Boolean.TRUE.equals(vm.getExpired()) && !Boolean.TRUE.equals(vm.getCheckOut()) && vm.getCheckIn() == null) {
                vm.setExpired(true);
                vm.setIsUpdated(true);
                vm.setLastUpdatedDate(new Date());
                visitorRepo.save(vm);
            }
        }
    }

    public VisitorManagementViewModel verifyOTP(String otp) {
        VisitorManagementViewModel result = new VisitorManagementViewModel();
        Optional<VisitorInviteHistory> histOpt = historyRepo.findByInviteCodeAndIsDeleted(otp, false);
        if (histOpt.isEmpty()) {
            result.setMsg("Invalid OTP");
            return result;
        }

        VisitorInviteHistory hist = histOpt.get();
        Optional<VisitorManagement> vmOpt = visitorRepo.findById(hist.getVisitorId());
        if (vmOpt.isEmpty()) {
            result.setMsg("Visitor not found");
            return result;
        }

        VisitorManagement vm = vmOpt.get();
        if (vm.getIsDeleted() || !Boolean.TRUE.equals(vm.getInvited()) || Boolean.TRUE.equals(vm.getAccept()) || Boolean.TRUE.equals(vm.getExpired())) {
            if (Boolean.TRUE.equals(vm.getIsDeleted()) && vm.getRejectRemark() != null) {
                result.setMsg("Your invitation has been rejected. Reason: " + vm.getRejectRemark());
            } else {
                result.setMsg("Invalid or expired invitation");
            }
            return result;
        }

        result = toViewModel(vm);
        result.setInviteCode(otp);
        result.setMsg("OTP verified successfully");
        return result;
    }

    public VisitorManagementViewModel verifyCheckInOTP(String otp) {
        VisitorManagementViewModel result = new VisitorManagementViewModel();
        Optional<VisitorInviteHistory> histOpt = historyRepo.findByInviteCodeAndIsDeleted(otp, false);
        if (histOpt.isEmpty()) {
            result.setMsg("Invalid OTP");
            return result;
        }

        VisitorInviteHistory hist = histOpt.get();
        Optional<VisitorManagement> vmOpt = visitorRepo.findById(hist.getVisitorId());
        if (vmOpt.isEmpty()) {
            result.setMsg("Visitor not found");
            return result;
        }

        result = toViewModel(vmOpt.get());
        result.setOtp(otp);
        result.setMsg("OTP verified successfully");
        return result;
    }

    @Transactional
    public VisitorManagementViewModel selfCheckInVerify(String otp) {
        VisitorManagementViewModel result = new VisitorManagementViewModel();
        Optional<VisitorInviteHistory> histOpt = historyRepo.findByInviteCodeAndIsDeleted(otp, false);
        if (histOpt.isEmpty()) {
            result.setMsg("Invalid OTP");
            return result;
        }

        VisitorInviteHistory hist = histOpt.get();
        Optional<VisitorManagement> vmOpt = visitorRepo.findById(hist.getVisitorId());
        if (vmOpt.isEmpty()) {
            result.setMsg("Visitor not found");
            return result;
        }

        VisitorManagement vm = vmOpt.get();
        if (vm.getIsDeleted() || !Boolean.TRUE.equals(vm.getInvited())) {
            if (Boolean.TRUE.equals(vm.getIsDeleted()) && vm.getRejectRemark() != null) {
                result.setMsg("Your invitation has been rejected. Reason: " + vm.getRejectRemark());
            } else {
                result.setMsg("Invalid or expired invitation");
            }
            return result;
        }

        if (vm.getCheckIn() != null) {
            result.setMsg("Already checked in");
            return result;
        }

        vm.setAccept(true);
        vm.setApproved(true);
        vm.setCheckIn(new Date());
        vm.setIsUpdated(true);
        vm.setLastUpdatedDate(new Date());
        visitorRepo.save(vm);

        hist.setCheckIn(true);
        String checkoutOtpVal = otpService.generateOTP();
        hist.setCheckoutCode(checkoutOtpVal);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.MINUTE, 2);
        hist.setCheckoutOtpExpiry(cal.getTime());
        hist.setLastUpdatedDate(new Date());
        historyRepo.save(hist);

        try {
            String empEmail = "";
            String empNameForVisitor = "";
            String hostCompany = "";
            String hostPlant = "";
            String visitorEmail = vm.getpMail() != null ? vm.getpMail() : vm.getoMail();
            if (vm.getCreatedBy() != null) {
                Optional<EmployeeMaster> empOpt = employeeRepo.findById(vm.getCreatedBy());
                if (empOpt.isPresent()) {
                    EmployeeMaster emp = empOpt.get();
                    empEmail = emp.getEmailId();
                    String fullName = emp.getFirstName() != null ? emp.getFirstName() : "";
                    if (emp.getLastName() != null && !emp.getLastName().isEmpty()) {
                        fullName += " " + emp.getLastName();
                    }
                    empNameForVisitor = fullName.trim();
                    if (emp.getCompId() != null) {
                        Optional<CompanyMaster> compOpt = companyRepo.findById(emp.getCompId());
                        if (compOpt.isPresent()) hostCompany = compOpt.get().getCompany() != null ? compOpt.get().getCompany() : "";
                    }
                    if (emp.getPlantId() != null) {
                        Optional<PlantMaster> plantOpt = plantRepo.findById(emp.getPlantId());
                        if (plantOpt.isPresent()) hostPlant = plantOpt.get().getPlantName() != null ? plantOpt.get().getPlantName() : "";
                    }
                }
            }
            if (empEmail != null && !empEmail.isEmpty() && vm.getName() != null) {
                emailService.sendCheckInEmailToEmp(empEmail, vm.getName());
            }
            if (visitorEmail != null && !visitorEmail.isEmpty() && vm.getName() != null) {
            String CheckoutCode = histOpt.map(h -> h.getCheckoutCode() != null ? h.getCheckoutCode() : "").orElse("");
                emailService.sendCheckInEmailToVisitor(visitorEmail, vm.getName(), empNameForVisitor, hostCompany, hostPlant, CheckoutCode);
            }
            if (vm.getCreatedBy() != null && vm.getName() != null) {
                notificationService.createNotification(vm.getCreatedBy(), "Visitor Checked In",
                    vm.getName() + " has checked in.", "checkin", vm.getVisitId());
            }
        } catch (Exception e) {
            log.error("Error sending check-in notifications", e);
        }

        result = toViewModel(vm);
        result.setOtp(otp);
        result.setMsg("Check-in completed");
        return result;
    }

    public List<DropdownViewModel> getEmployeeDropdown() {
        return employeeRepo.findByIsActiveAndIsDeleted(true, false).stream()
            .map(emp -> {
                String fullName = emp.getFirstName() != null ? emp.getFirstName() : "";
                if (emp.getMiddleName() != null && !emp.getMiddleName().isEmpty()) {
                    fullName += " " + emp.getMiddleName();
                }
                if (emp.getLastName() != null && !emp.getLastName().isEmpty()) {
                    fullName += " " + emp.getLastName();
                }
                return new DropdownViewModel(emp.getEmpId(), fullName.trim(), emp.getEmpCode());
            })
            .collect(Collectors.toList());
    }

    public byte[] exportCsv(FilterViewModel filter) {
        List<VisitorManagementViewModel> data = visitFilter(filter);
        StringBuilder sb = new StringBuilder();
        sb.append("Name,Company,Category,Email,Mobile,Date,CheckIn,CheckOut,Duration,Status\n");
        for (VisitorManagementViewModel v : data) {
            sb.append(escapeCsv(v.getName())).append(",");
            sb.append(escapeCsv(v.getCompany())).append(",");
            sb.append(escapeCsv(v.getCategory())).append(",");
            sb.append(escapeCsv(v.getpMail())).append(",");
            sb.append(escapeCsv(v.getMobile())).append(",");
            sb.append(v.getVisitDate() != null ? v.getVisitDate().toString() : "").append(",");
            sb.append(v.getCheckIn() != null ? v.getCheckIn().toString() : "").append(",");
            sb.append(v.getCheckOut() != null ? v.getCheckOut().toString() : "").append(",");
            sb.append(v.getCheckIn() != null && v.getCheckOut() != null ?
                (v.getCheckOut().getTime() - v.getCheckIn().getTime()) / 60000 + "m" : "").append(",");
            sb.append(escapeCsv(v.getStatus())).append("\n");
        }
        return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public byte[] exportExcel(FilterViewModel filter) {
        List<VisitorManagementViewModel> data = visitFilter(filter);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Visitors");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Headers matching .NET VisitExportExcel pattern
            String[] headers = {
                "Serial No.", "Name", "Designation", "Company", "Purpose",
                "Mail", "Mobile", "Photo", "CompName", "Accessories",
                "WhomtoMeet", "EmpCode", "Date", "Time", "IdCard", "CheckIn", "CheckOut"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 15 * 256);
            }

            // Populate data rows
            for (int i = 0; i < data.size(); i++) {
                VisitorManagementViewModel v = data.get(i);
                Row row = sheet.createRow(i + 1);

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(i + 1);
                cell0.setCellStyle(dataStyle);

                setCellValue(row, 1, v.getName(), dataStyle);
                setCellValue(row, 2, v.getDesignation(), dataStyle);
                setCellValue(row, 3, v.getCompany(), dataStyle);
                setCellValue(row, 4, v.getPurpose(), dataStyle);
                setCellValue(row, 5, v.getoMail(), dataStyle);
                setCellValue(row, 6, v.getMobile(), dataStyle);
                setCellValue(row, 7, v.getPhoto(), dataStyle);
                setCellValue(row, 8, v.getCompName(), dataStyle);
                setCellValue(row, 9, v.getAccessories(), dataStyle);
                setCellValue(row, 10, v.getwName(), dataStyle);
                setCellValue(row, 11, v.getwEmpCode(), dataStyle);
                setCellValue(row, 12, v.getVisitDate() != null ? v.getVisitDate().toString() : "", dataStyle);
                setCellValue(row, 13, v.getTime(), dataStyle);
                setCellValue(row, 14, v.getIdCard(), dataStyle);
                setCellValue(row, 15, v.getCheckIn() != null ? v.getCheckIn().toString() : "", dataStyle);
                setCellValue(row, 16, v.getCheckOut() != null ? v.getCheckOut().toString() : "", dataStyle);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export Excel", e);
        }
    }

    private void setCellValue(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    public List<DropdownViewModel> getCompanyDropdown() {
        return companyRepo.findByIsActiveAndIsDeleted(true, false).stream()
            .map(c -> new DropdownViewModel(c.getCompId(), c.getCompany(), c.getCompanyCode()))
            .collect(Collectors.toList());
    }

    private VisitorManagementViewModel toViewModel(VisitorManagement vm) {
        VisitorManagementViewModel model = new VisitorManagementViewModel();
        model.setVisitId(vm.getVisitId());
        model.setRegNo(vm.getRegNo());
        model.setQr(vm.getQr());
        model.setName(vm.getName());
        model.setDesignation(vm.getDesignation());
        model.setCompany(vm.getCompany());
        model.setPurpose(vm.getPurpose());
        model.setpMail(vm.getpMail());
        model.setoMail(vm.getoMail());
        model.setMobile(vm.getMobile());
        model.setaMobile(vm.getaMobile());
        model.setPhoto(vm.getPhoto());
        model.setCategory(vm.getCategory());
        model.setCompId(vm.getCompId());
        model.setPlantId(vm.getPlantId());
        model.setWhomToMeet(vm.getWhomToMeet());
        model.setVisitDate(vm.getVisitDate());
        model.setTime(vm.getTime());
        model.setInvited(vm.getInvited());
        model.setAccept(vm.getAccept());
        model.setApproved(vm.getApproved());
        model.setExpired(vm.getExpired());
        model.setAccessories(vm.getAccessories());
        model.setDirectCheckIn(vm.getDirectCheckIn());
        model.setCheckIn(vm.getCheckIn());
        model.setCheckOut(vm.getCheckOut());
        model.setIdCard(vm.getIdCard());
        model.setCreatedBy(vm.getCreatedBy());
        model.setCreatedDate(vm.getCreatedDate());
        model.setLastUpdatedBy(vm.getLastUpdatedBy());
        model.setLastUpdatedDate(vm.getLastUpdatedDate());
        model.setIsActive(vm.getIsActive());
        model.setIsUpdated(vm.getIsUpdated());
        model.setIsDeleted(vm.getIsDeleted());
        model.setRejectRemark(vm.getRejectRemark());

        if (Boolean.TRUE.equals(vm.getIsDeleted())) model.setStatus("Cancelled");
        else if (Boolean.TRUE.equals(vm.getIsDeleted()) && vm.getRejectRemark() != null) model.setStatus("Rejected");
        else if (vm.getCheckIn() != null && vm.getCheckOut() != null) model.setStatus("Checked Out");
        else if (vm.getCheckIn() != null) model.setStatus("Checked In");
        else if (Boolean.TRUE.equals(vm.getDirectCheckIn()) && Boolean.TRUE.equals(vm.getApproved())) model.setStatus("Approved");
        else if (Boolean.TRUE.equals(vm.getDirectCheckIn()) && !Boolean.TRUE.equals(vm.getApproved())) model.setStatus("Pending Approval");
        else if (Boolean.TRUE.equals(vm.getApproved()) && Boolean.TRUE.equals(vm.getAccept())) model.setStatus("Self Verified");
        else if (Boolean.TRUE.equals(vm.getAccept())) model.setStatus("Invite Accepted");
        else if (Boolean.TRUE.equals(vm.getInvited())) model.setStatus("Invited");
        else model.setStatus("Pending");

        // Use ordered query to get the latest history record
        Optional<VisitorInviteHistory> histOpt = historyRepo.findFirstByVisitorIdAndIsDeletedOrderByLastUpdatedDateDesc(vm.getVisitId(), false);
        if (histOpt.isPresent()) {
            VisitorInviteHistory hist = histOpt.get();
            model.setCheckInCode(hist.getCheckInCode());
            String coCode = hist.getCheckoutCode();
            if (coCode == null || coCode.isEmpty()) coCode = hist.getCheckInCode();
            model.setCheckoutCode(coCode);
            model.setInviteCode(hist.getInviteCode());
        }

        if (vm.getWhomToMeet() != null) {
            Optional<EmployeeMaster> empOpt = employeeRepo.findById(vm.getWhomToMeet());
            if (empOpt.isPresent()) {
                EmployeeMaster emp = empOpt.get();
                String fullName = emp.getFirstName() != null ? emp.getFirstName() : "";
                if (emp.getMiddleName() != null && !emp.getMiddleName().isEmpty()) fullName += " " + emp.getMiddleName();
                if (emp.getLastName() != null && !emp.getLastName().isEmpty()) fullName += " " + emp.getLastName();
                model.setwName(fullName.trim());
                model.setwEmpCode(emp.getEmpCode());
            }
        }

        return model;
    }

    /**
     * Returns the current checkout OTP for a given visit.
     * The frontend can call this to always get the latest OTP value.
     */
    public VisitorManagementViewModel getCurrentCheckoutOtp(Integer visitId) {
        VisitorManagementViewModel result = new VisitorManagementViewModel();
        Optional<VisitorManagement> vmOpt = visitorRepo.findById(visitId);
        if (vmOpt.isEmpty()) {
            result.setMsg("Visitor not found");
            return result;
        }
        VisitorManagement vm = vmOpt.get();
        if (vm.getCheckIn() == null) {
            result.setMsg("Visitor has not checked in yet");
            return result;
        }
        if (vm.getCheckOut() != null) {
            result.setMsg("Visitor has already checked out");
            return result;
        }
        Optional<VisitorInviteHistory> histOpt = historyRepo.findFirstByVisitorIdAndIsDeletedOrderByLastUpdatedDateDesc(visitId, false);
        if (histOpt.isEmpty()) {
            result.setMsg("Visitor history not found");
            return result;
        }
        VisitorInviteHistory hist = histOpt.get();
        result = toViewModel(vm);
        result.setOtp(hist.getCheckInCode());
        result.setCheckInCode(hist.getCheckInCode());
        result.setMsg("OK");
        return result;
    }

    @Transactional
    public VisitorManagementViewModel updateVisitorDetails(VisitorManagementViewModel model) {
        if (model.getVisitId() == null) {
            model.setMsg("VisitId is required");
            return model;
        }
        Optional<VisitorManagement> opt = visitorRepo.findById(model.getVisitId());
        if (opt.isEmpty()) {
            model.setMsg("Visitor not found");
            return model;
        }
        VisitorManagement vm = opt.get();
        vm.setIdCard(model.getIdCard());
        vm.setAccessories(model.getAccessories());
        vm.setLastUpdatedDate(new Date());
        visitorRepo.save(vm);
        VisitorManagementViewModel result = toViewModel(vm);
        result.setMsg("Details updated successfully");
        return result;
    }
}
