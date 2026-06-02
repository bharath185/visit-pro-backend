package com.visitorapp.controller;

import com.visitorapp.dto.DashboardViewModel;
import com.visitorapp.dto.DropdownViewModel;
import com.visitorapp.dto.FilterViewModel;
import com.visitorapp.dto.UserDashboardViewModel;
import com.visitorapp.dto.VisitorManagementViewModel;
import com.visitorapp.service.VisitorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visitor")
public class VisitorController {

    private static final Logger log = LoggerFactory.getLogger(VisitorController.class);

    private final VisitorService visitorService;

    public VisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardViewModel> getDashboard() {
        return ResponseEntity.ok(visitorService.getDashboardStats());
    }

    @GetMapping("/user-dashboard/{empId}")
    public ResponseEntity<UserDashboardViewModel> getUserDashboard(@PathVariable Integer empId) {
        return ResponseEntity.ok(visitorService.getUserDashboardMetrics(empId));
    }

    @GetMapping("/hr-dashboard")
    public ResponseEntity<DashboardViewModel> getHrDashboard() {
        return ResponseEntity.ok(visitorService.getHrDashboardCounts());
    }

    @GetMapping("/dashboard/plant/{plantId}")
    public ResponseEntity<DashboardViewModel> getDashboardByPlant(@PathVariable Integer plantId) {
        return ResponseEntity.ok(visitorService.getDashboardStatsByPlant(plantId));
    }

    @GetMapping("/today")
    public ResponseEntity<List<VisitorManagementViewModel>> getTodayVisitors() {
        return ResponseEntity.ok(visitorService.getTodayVisitors());
    }

    @PostMapping("/list")
    public ResponseEntity<List<VisitorManagementViewModel>> getAllVisitors() {
        return ResponseEntity.ok(visitorService.getAllVisitors());
    }

    @PostMapping("/all-invites")
    public ResponseEntity<VisitorManagementViewModel> getAllInvites() {
        return ResponseEntity.ok(visitorService.getAllInvite());
    }

    @PostMapping("/by-employee")
    public ResponseEntity<List<VisitorManagementViewModel>> getByEmployee(@RequestBody Map<String, Integer> body) {
        Integer empId = body.getOrDefault("EmpId", 0);
        return ResponseEntity.ok(visitorService.getVisitorsByEmployee(empId));
    }

    @PostMapping("/invite")
    public ResponseEntity<VisitorManagementViewModel> inviteVisit(@RequestBody VisitorManagementViewModel model) {
        return ResponseEntity.ok(visitorService.inviteVisit(model));
    }

    @PostMapping("/accept-invite")
    public ResponseEntity<VisitorManagementViewModel> acceptInvite(@RequestBody VisitorManagementViewModel model) {
        return ResponseEntity.ok(visitorService.acceptInvite(model));
    }

    @PostMapping("/direct-checkin")
    public ResponseEntity<VisitorManagementViewModel> directCheckIn(@RequestBody VisitorManagementViewModel model) {
        return ResponseEntity.ok(visitorService.directCheckIn(model));
    }

    @PostMapping("/approve-direct-checkin")
    public ResponseEntity<VisitorManagementViewModel> approveDirectCheckIn(@RequestBody VisitorManagementViewModel model) {
        return ResponseEntity.ok(visitorService.approveDirectCheckIn(model.getVisitId(), model.getEmpId()));
    }

    @PostMapping("/reject-direct-checkin")
    public ResponseEntity<VisitorManagementViewModel> rejectDirectCheckIn(@RequestBody VisitorManagementViewModel model) {
        return ResponseEntity.ok(visitorService.rejectDirectCheckIn(model.getVisitId(), model.getEmpId()));
    }

    @PostMapping("/checkin")
    public ResponseEntity<VisitorManagementViewModel> checkIn(@RequestBody VisitorManagementViewModel model) {
        return ResponseEntity.ok(visitorService.checkIn(model));
    }

    @PostMapping("/plantadmin-checkin")
    public ResponseEntity<VisitorManagementViewModel> plantAdminCheckIn(@RequestBody VisitorManagementViewModel model) {
        return ResponseEntity.ok(visitorService.plantAdminCheckIn(model));
    }

    @PostMapping("/plantadmin-checkout")
    public ResponseEntity<VisitorManagementViewModel> plantAdminCheckOut(@RequestBody VisitorManagementViewModel model) {
        return ResponseEntity.ok(visitorService.plantAdminCheckOut(model));
    }

    @PostMapping("/checkout")
    public ResponseEntity<VisitorManagementViewModel> checkOut(@RequestBody VisitorManagementViewModel model) {
        return ResponseEntity.ok(visitorService.checkOut(model));
    }

    @PostMapping("/get-by-id")
    public ResponseEntity<VisitorManagementViewModel> getVisitorById(@RequestBody Map<String, Integer> body) {
        Integer visitId = body.getOrDefault("VisitId", 0);
        return ResponseEntity.ok(visitorService.getVisitorById(visitId));
    }

    @PostMapping("/cancel")
    public ResponseEntity<VisitorManagementViewModel> cancelInvite(@RequestBody Map<String, Integer> body) {
        Integer visitId = body.getOrDefault("VisitId", 0);
        return ResponseEntity.ok(visitorService.cancelInvite(visitId));
    }

    @PostMapping("/list/plant/{plantId}")
    public ResponseEntity<List<VisitorManagementViewModel>> getAllVisitorsByPlant(@PathVariable Integer plantId) {
        return ResponseEntity.ok(visitorService.getAllVisitorsByPlant(plantId));
    }

    @PostMapping("/update-details")
    public ResponseEntity<VisitorManagementViewModel> updateDetails(@RequestBody VisitorManagementViewModel model) {
        return ResponseEntity.ok(visitorService.updateVisitorDetails(model));
    }

    @PostMapping("/regenerate-checkout-otp")
    public ResponseEntity<VisitorManagementViewModel> regenerateCheckoutOtp(@RequestBody Map<String, Integer> body) {
        Integer visitId = body.getOrDefault("VisitId", 0);
        return ResponseEntity.ok(visitorService.regenerateCheckoutOtp(visitId));
    }

    @GetMapping("/checkout-otp/{visitId}")
    public ResponseEntity<VisitorManagementViewModel> getCurrentCheckoutOtp(@PathVariable Integer visitId) {
        return ResponseEntity.ok(visitorService.getCurrentCheckoutOtp(visitId));
    }

    @PostMapping("/direct-checkins-by-contact")
    public ResponseEntity<List<VisitorManagementViewModel>> getDirectCheckInsByContact(@RequestBody Map<String, Integer> body) {
        Integer empId = body.getOrDefault("EmpId", 0);
        return ResponseEntity.ok(visitorService.getDirectCheckInsByContact(empId));
    }

    @PostMapping("/direct-checkins-by-contact/plant/{plantId}")
    public ResponseEntity<List<VisitorManagementViewModel>> getDirectCheckInsByContactAndPlant(@PathVariable Integer plantId, @RequestBody Map<String, Integer> body) {
        Integer empId = body.getOrDefault("EmpId", 0);
        return ResponseEntity.ok(visitorService.getDirectCheckInsByContactAndPlant(empId, plantId));
    }

    // Reporting person approve/reject invite (in-app)
    @PostMapping("/approve-pending/{visitId}")
    public ResponseEntity<VisitorManagementViewModel> approvePendingInvite(@PathVariable Integer visitId) {
        return ResponseEntity.ok(visitorService.approvePendingInvite(visitId));
    }

    @PostMapping("/reject-pending/{visitId}")
    public ResponseEntity<VisitorManagementViewModel> rejectPendingInvite(@PathVariable Integer visitId) {
        return ResponseEntity.ok(visitorService.rejectPendingInvite(visitId));
    }

    @PostMapping("/pending-approvals")
    public ResponseEntity<List<VisitorManagementViewModel>> getPendingApprovals(@RequestBody Map<String, Integer> body) {
        Integer empId = body.getOrDefault("EmpId", 0);
        return ResponseEntity.ok(visitorService.getPendingApprovals(empId));
    }

    // Simple plant admin check-in/out for approved direct check-ins (no OTP required)
    @PostMapping("/plantadmin-checkin-direct")
    public ResponseEntity<VisitorManagementViewModel> plantAdminCheckInDirect(@RequestBody VisitorManagementViewModel model) {
        return ResponseEntity.ok(visitorService.checkIn(model));
    }

    @PostMapping("/plantadmin-checkout-direct")
    public ResponseEntity<VisitorManagementViewModel> plantAdminCheckOutDirect(@RequestBody VisitorManagementViewModel model) {
        return ResponseEntity.ok(visitorService.checkOut(model));
    }

    @PostMapping("/visit-filter")
    public ResponseEntity<List<VisitorManagementViewModel>> visitFilter(@RequestBody FilterViewModel filter) {
        return ResponseEntity.ok(visitorService.visitFilter(filter));
    }

    @PostMapping("/export-csv")
    public ResponseEntity<byte[]> exportCsv(@RequestBody FilterViewModel filter) {
        byte[] csv = visitorService.exportCsv(filter);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=VisitorsData.csv");
        return ResponseEntity.ok().headers(headers).body(csv);
    }

    @PostMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel(@RequestBody FilterViewModel filter) {
        byte[] excel = visitorService.exportExcel(filter);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=VisitorsData.xlsx");
        return ResponseEntity.ok().headers(headers).body(excel);
    }
}
