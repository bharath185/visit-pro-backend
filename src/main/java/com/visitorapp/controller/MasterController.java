package com.visitorapp.controller;

import com.visitorapp.dto.DropdownViewModel;
import com.visitorapp.dto.MasterViewModel;
import com.visitorapp.service.MasterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/master")
public class MasterController {

    private final MasterService masterService;

    public MasterController(MasterService masterService) {
        this.masterService = masterService;
    }

    @GetMapping("/departments")
    public ResponseEntity<List<MasterViewModel>> getDepartments() {
        return ResponseEntity.ok(masterService.getAllDepartments());
    }

    @PostMapping("/departments")
    public ResponseEntity<MasterViewModel> saveDepartment(@RequestBody MasterViewModel model) {
        return ResponseEntity.ok(masterService.saveDepartment(model));
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<Map<String, String>> deleteDepartment(@PathVariable Integer id) {
        masterService.deleteDepartment(id);
        return ResponseEntity.ok(Map.of("Msg", "Deleted successfully"));
    }

    @GetMapping("/designations")
    public ResponseEntity<List<MasterViewModel>> getDesignations() {
        return ResponseEntity.ok(masterService.getAllDesignations());
    }

    @GetMapping("/designations/by-dept/{deptId}")
    public ResponseEntity<List<MasterViewModel>> getDesignationsByDept(@PathVariable Integer deptId) {
        return ResponseEntity.ok(masterService.getDesignationsByDept(deptId));
    }

    @GetMapping("/designations/by-plant/{plantId}")
    public ResponseEntity<List<MasterViewModel>> getDesignationsByPlant(@PathVariable Integer plantId) {
        return ResponseEntity.ok(masterService.getDesignationsByPlant(plantId));
    }

    @PostMapping("/designations")
    public ResponseEntity<MasterViewModel> saveDesignation(@RequestBody MasterViewModel model) {
        return ResponseEntity.ok(masterService.saveDesignation(model));
    }

    @DeleteMapping("/designations/{id}")
    public ResponseEntity<Map<String, String>> deleteDesignation(@PathVariable Integer id) {
        masterService.deleteDesignation(id);
        return ResponseEntity.ok(Map.of("Msg", "Deleted successfully"));
    }

    @GetMapping("/companies")
    public ResponseEntity<List<MasterViewModel>> getCompanies() {
        return ResponseEntity.ok(masterService.getAllCompanies());
    }

    @PostMapping("/companies")
    public ResponseEntity<MasterViewModel> saveCompany(@RequestBody MasterViewModel model) {
        return ResponseEntity.ok(masterService.saveCompany(model));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Map<String, String>> deleteCompany(@PathVariable Integer id) {
        masterService.deleteCompany(id);
        return ResponseEntity.ok(Map.of("Msg", "Deleted successfully"));
    }

    @GetMapping("/dept-list")
    public ResponseEntity<List<DropdownViewModel>> getDepartmentList() {
        return ResponseEntity.ok(masterService.getDepartmentList());
    }

    @GetMapping("/designation-list")
    public ResponseEntity<List<DropdownViewModel>> getDesignationList(@RequestParam(required = false) Integer deptId) {
        return ResponseEntity.ok(masterService.getDesignationList(deptId));
    }

    @GetMapping("/employees-by-designation/{designationId}")
    public ResponseEntity<List<MasterViewModel>> getEmployeesByDesignation(@PathVariable Integer designationId) {
        return ResponseEntity.ok(masterService.getEmployeesByDesignationId(designationId));
    }

    @GetMapping("/employees-by-dept")
    public ResponseEntity<List<DropdownViewModel>> getEmployeesByDepartment(@RequestParam String deptName) {
        return ResponseEntity.ok(masterService.getEmployeesByDepartment(deptName));
    }

    @GetMapping("/dept-names")
    public ResponseEntity<List<String>> getDepartmentNames() {
        return ResponseEntity.ok(masterService.getDepartmentNames());
    }

    @GetMapping("/grades")
    public ResponseEntity<List<DropdownViewModel>> getGrades() {
        return ResponseEntity.ok(masterService.getGradeList());
    }

    // === EMPLOYEE MASTER (User CRUD) ===
    @GetMapping("/employees/{id}")
    public ResponseEntity<MasterViewModel> getEmployeeById(@PathVariable Integer id) {
        MasterViewModel emp = masterService.getEmployeeById(id);
        if (emp == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(emp);
    }

    @GetMapping("/employees-all")
    public ResponseEntity<List<MasterViewModel>> getAllEmployees() {
        return ResponseEntity.ok(masterService.getAllEmployees());
    }

    @PostMapping("/employees")
    public ResponseEntity<MasterViewModel> saveEmployee(@RequestBody MasterViewModel model) {
        return ResponseEntity.ok(masterService.saveEmployee(model));
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Map<String, String>> deleteEmployee(@PathVariable Integer id) {
        masterService.deleteEmployee(id);
        return ResponseEntity.ok(Map.of("Msg", "Deleted successfully"));
    }

    // Cascading employee filter
    @GetMapping("/employees-filter")
    public ResponseEntity<List<DropdownViewModel>> getFilteredEmployees(
            @RequestParam(required = false) Integer compId,
            @RequestParam(required = false) String deptName,
            @RequestParam(required = false) Integer designationId) {
        return ResponseEntity.ok(masterService.getFilteredEmployees(compId, deptName, designationId));
    }

    @GetMapping("/dept-names-by-company")
    public ResponseEntity<List<String>> getDeptNamesByCompany(@RequestParam Integer compId) {
        return ResponseEntity.ok(masterService.getDeptNamesByCompany(compId));
    }

    // === PLANT ENDPOINTS ===
    @GetMapping("/plants")
    public ResponseEntity<List<MasterViewModel>> getAllPlants() {
        return ResponseEntity.ok(masterService.getAllPlants());
    }

    @GetMapping("/plants/by-company/{compId}")
    public ResponseEntity<List<MasterViewModel>> getPlantsByCompany(@PathVariable Integer compId) {
        return ResponseEntity.ok(masterService.getPlantsByCompany(compId));
    }

    @PostMapping("/plants")
    public ResponseEntity<MasterViewModel> savePlant(@RequestBody MasterViewModel model) {
        return ResponseEntity.ok(masterService.savePlant(model));
    }

    @DeleteMapping("/plants/{id}")
    public ResponseEntity<Map<String, String>> deletePlant(@PathVariable Integer id) {
        masterService.deletePlant(id);
        return ResponseEntity.ok(Map.of("Msg", "Deleted successfully"));
    }

    // === CATEGORY MASTER ===
    @GetMapping("/categories")
    public ResponseEntity<List<MasterViewModel>> getCategories() {
        return ResponseEntity.ok(masterService.getAllCategories());
    }

    @GetMapping("/categories/list")
    public ResponseEntity<List<DropdownViewModel>> getCategoryList() {
        return ResponseEntity.ok(masterService.getCategoryList());
    }

    @PostMapping("/categories")
    public ResponseEntity<MasterViewModel> saveCategory(@RequestBody MasterViewModel model) {
        return ResponseEntity.ok(masterService.saveCategory(model));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Integer id) {
        masterService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("Msg", "Deleted successfully"));
    }

    // === VISIT PURPOSE MASTER ===
    @GetMapping("/visit-purposes")
    public ResponseEntity<List<MasterViewModel>> getVisitPurposes() {
        return ResponseEntity.ok(masterService.getAllVisitPurposes());
    }

    @GetMapping("/visit-purposes/list")
    public ResponseEntity<List<DropdownViewModel>> getVisitPurposeList() {
        return ResponseEntity.ok(masterService.getVisitPurposeList());
    }

    @PostMapping("/visit-purposes")
    public ResponseEntity<MasterViewModel> saveVisitPurpose(@RequestBody MasterViewModel model) {
        return ResponseEntity.ok(masterService.saveVisitPurpose(model));
    }

    @DeleteMapping("/visit-purposes/{id}")
    public ResponseEntity<Map<String, String>> deleteVisitPurpose(@PathVariable Integer id) {
        masterService.deleteVisitPurpose(id);
        return ResponseEntity.ok(Map.of("Msg", "Deleted successfully"));
    }

    // === CASCADE ENDPOINTS: Company → Location → Plant → Designation ===

    // === CASCADE ENDPOINTS: Company → Location → Plant → Designation ===
    @GetMapping("/locations/by-company/{compId}")
    public ResponseEntity<List<MasterViewModel>> getLocationsByCompany(@PathVariable Integer compId) {
        return ResponseEntity.ok(masterService.getLocationsByCompany(compId));
    }

    @GetMapping("/plants/by-location/{locationId}")
    public ResponseEntity<List<MasterViewModel>> getPlantsByLocation(@PathVariable Integer locationId) {
        return ResponseEntity.ok(masterService.getPlantsByLocation(locationId));
    }

    @GetMapping("/designations/by-plant-dropdown/{plantId}")
    public ResponseEntity<List<MasterViewModel>> getDesignationsByPlantDropdown(@PathVariable Integer plantId) {
        return ResponseEntity.ok(masterService.getDesignationsByPlantForDropdown(plantId));
    }

    // === PLANT-SCOPED EMPLOYEE HIERARCHY ===
    @GetMapping("/plant-list")
    public ResponseEntity<List<DropdownViewModel>> getPlantList() {
        return ResponseEntity.ok(masterService.getPlantList());
    }

    @GetMapping("/employees-by-plant/{plantId}")
    public ResponseEntity<List<MasterViewModel>> getEmployeesByPlant(@PathVariable Integer plantId) {
        return ResponseEntity.ok(masterService.getEmployeesByPlantId(plantId));
    }

    @GetMapping("/dept-names-by-plant/{plantId}")
    public ResponseEntity<List<String>> getDeptNamesByPlant(@PathVariable Integer plantId) {
        return ResponseEntity.ok(masterService.getDeptNamesByPlant(plantId));
    }

    @GetMapping("/employees-by-plant-dept")
    public ResponseEntity<List<DropdownViewModel>> getEmployeesByPlantAndDept(@RequestParam Integer plantId, @RequestParam String deptName) {
        return ResponseEntity.ok(masterService.getEmployeesByPlantAndDept(plantId, deptName));
    }

    @GetMapping("/employee-hierarchy/{plantId}")
    public ResponseEntity<List<java.util.Map<String, Object>>> getEmployeeHierarchy(@PathVariable Integer plantId) {
        return ResponseEntity.ok(masterService.getEmployeeHierarchy(plantId));
    }

    @GetMapping("/reporting-employees")
    public ResponseEntity<List<DropdownViewModel>> getReportingEmployees() {
        return ResponseEntity.ok(masterService.getReportingEmployees());
    }

    @GetMapping("/employees-filter-by-plant")
    public ResponseEntity<List<DropdownViewModel>> getFilteredEmployeesByPlant(
            @RequestParam Integer plantId,
            @RequestParam(required = false) String deptName,
            @RequestParam(required = false) Integer designationId) {
        return ResponseEntity.ok(masterService.getFilteredEmployeesByPlant(plantId, deptName, designationId));
    }
}
