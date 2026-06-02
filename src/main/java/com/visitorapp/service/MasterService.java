package com.visitorapp.service;

import com.visitorapp.config.AuthContext;
import com.visitorapp.dto.DropdownViewModel;
import com.visitorapp.dto.MasterViewModel;
import com.visitorapp.entity.*;
import com.visitorapp.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MasterService {

    private final DeptMasterRepository deptRepo;
    private final DesignationMasterRepository designationRepo;
    private final CompanyMasterRepository companyRepo;
    private final EmployeeMasterRepository employeeRepo;
    private final GradeMasterRepository gradeRepo;
    private final PlantMasterRepository plantRepo;
    private final CategoryMasterRepository categoryRepo;
    private final VisitPurposeMasterRepository visitPurposeRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public MasterService(DeptMasterRepository deptRepo,
                          DesignationMasterRepository designationRepo,
                          CompanyMasterRepository companyRepo,
                          EmployeeMasterRepository employeeRepo,
                          GradeMasterRepository gradeRepo,
                  CategoryMasterRepository categoryRepo,
                  PlantMasterRepository plantRepo,
                  VisitPurposeMasterRepository visitPurposeRepo,
                  PasswordEncoder passwordEncoder,
                  EmailService emailService) {
        this.emailService = emailService;
        this.deptRepo = deptRepo;
        this.designationRepo = designationRepo;
        this.companyRepo = companyRepo;
        this.employeeRepo = employeeRepo;
        this.gradeRepo = gradeRepo;
        this.categoryRepo = categoryRepo;
        this.plantRepo = plantRepo;
        this.visitPurposeRepo = visitPurposeRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // === DEPARTMENT MASTER ===
    public List<MasterViewModel> getAllDepartments() {
        return deptRepo.findByIsDeletedOrderByDeptName(false).stream()
            .map(d -> { MasterViewModel m = new MasterViewModel();
                m.setId(d.getDeptId()); m.setName(d.getDeptName());
                m.setShortName(d.getDeptShortName()); m.setParentId(d.getCompId());
                m.setIsActive(d.getIsActive()); return m; })
            .collect(Collectors.toList());
    }

    @Transactional
    public MasterViewModel saveDepartment(MasterViewModel model) {
        DeptMaster d = model.getId() != null ? deptRepo.findById(model.getId()).orElse(new DeptMaster()) : new DeptMaster();
        d.setDeptName(model.getName());
        d.setDeptShortName(model.getShortName());
        d.setCompId(model.getParentId());
        d.setIsActive(true); d.setIsDeleted(false);
        if (model.getId() == null) d.setCreatedDate(new Date());
        d.setLastUpdatedDate(new Date());
        deptRepo.save(d);
        model.setId(d.getDeptId());
        model.setMsg("Department saved successfully");
        return model;
    }

    @Transactional
    public void deleteDepartment(Integer id) {
        deptRepo.findById(id).ifPresent(d -> { d.setIsDeleted(true); d.setIsActive(false); d.setLastUpdatedDate(new Date()); deptRepo.save(d); });
    }

    // === DESIGNATION MASTER ===
    public List<MasterViewModel> getAllDesignations() {
        return designationRepo.findByIsDeletedOrderByDesignation(false).stream()
            .map(d -> { MasterViewModel m = new MasterViewModel();
                m.setId(d.getDesignationId()); m.setName(d.getDesignation());
                m.setShortName(d.getDesignationShortName()); m.setDescription(d.getDescription());
                m.setLocationId(d.getDeptId()); m.setPlantId(d.getPlantId()); m.setIsActive(d.getIsActive());
                if (d.getDeptId() != null) {
                    deptRepo.findById(d.getDeptId()).ifPresent(dept -> {
                        m.setParentName(dept.getDeptName());
                        m.setParentId(dept.getCompId());
                    });
                }
                return m; })
            .collect(Collectors.toList());
    }

    public List<MasterViewModel> getDesignationsByDept(Integer deptId) {
        return designationRepo.findByDeptIdAndIsDeleted(deptId, false).stream()
            .map(d -> { MasterViewModel m = new MasterViewModel();
                m.setId(d.getDesignationId()); m.setName(d.getDesignation());
                m.setParentId(d.getDeptId()); m.setPlantId(d.getPlantId()); return m; })
            .collect(Collectors.toList());
    }

    public List<MasterViewModel> getDesignationsByPlant(Integer plantId) {
        return designationRepo.findByPlantIdAndIsDeleted(plantId, false).stream()
            .map(d -> { MasterViewModel m = new MasterViewModel();
                m.setId(d.getDesignationId()); m.setName(d.getDesignation());
                m.setPlantId(d.getPlantId()); m.setDescription(d.getDescription());
                return m; })
            .collect(Collectors.toList());
    }

    @Transactional
    public MasterViewModel saveDesignation(MasterViewModel model) {
        DesignationMaster d = model.getId() != null ? designationRepo.findById(model.getId()).orElse(new DesignationMaster()) : new DesignationMaster();
        d.setDesignation(model.getName());
        d.setDesignationShortName(model.getShortName());
        d.setDescription(model.getDescription());
        // PlantAdmin/Admin: force PlantId to their plant
        EmployeeMaster lu = AuthContext.get();
        if (lu != null && lu.getPlantId() != null && (Boolean.TRUE.equals(lu.getIsAdminUser()) || AuthContext.isPlantAdmin())) {
            d.setPlantId(lu.getPlantId());
            d.setDeptId(model.getLocationId() != null ? model.getLocationId() : model.getParentId());
        } else {
            d.setDeptId(model.getLocationId() != null ? model.getLocationId() : model.getParentId());
            d.setPlantId(model.getPlantId());
        }
        d.setIsActive(true); d.setIsDeleted(false);
        if (model.getId() == null) d.setCreatedDate(new Date());
        d.setLastUpdatedDate(new Date());
        designationRepo.save(d);
        model.setId(d.getDesignationId());
        model.setMsg("Designation saved successfully");
        return model;
    }

    @Transactional
    public void deleteDesignation(Integer id) {
        designationRepo.findById(id).ifPresent(d -> { d.setIsDeleted(true); d.setIsActive(false); d.setLastUpdatedDate(new Date()); designationRepo.save(d); });
    }

    // === COMPANY MASTER ===
    public List<MasterViewModel> getAllCompanies() {
        return companyRepo.findByIsDeletedOrderByCompany(false).stream()
            .map(c -> { MasterViewModel m = new MasterViewModel();
                m.setId(c.getCompId()); m.setName(c.getCompany());
                m.setCode(c.getCompanyCode()); m.setIsActive(c.getIsActive()); return m; })
            .collect(Collectors.toList());
    }

    @Transactional
    public MasterViewModel saveCompany(MasterViewModel model) {
        CompanyMaster c = model.getId() != null ? companyRepo.findById(model.getId()).orElse(new CompanyMaster()) : new CompanyMaster();
        c.setCompany(model.getName());
        c.setCompanyCode(model.getCode());
        c.setIsActive(true); c.setIsDeleted(false);
        if (model.getId() == null) c.setCreatedDate(new Date());
        c.setLastUpdatedDate(new Date());
        companyRepo.save(c);
        model.setId(c.getCompId());
        model.setMsg("Company saved successfully");
        return model;
    }

    @Transactional
    public void deleteCompany(Integer id) {
        companyRepo.findById(id).ifPresent(c -> { c.setIsDeleted(true); c.setIsActive(false); c.setLastUpdatedDate(new Date()); companyRepo.save(c); });
    }

    // === CASCADING DROPDOWNS FOR HIERARCHY: Company → Location → Plant → Designation ===
    public List<MasterViewModel> getLocationsByCompany(Integer compId) {
        return deptRepo.findByCompIdAndIsActiveAndIsDeleted(compId, true, false).stream()
            .map(d -> { MasterViewModel m = new MasterViewModel();
                m.setId(d.getDeptId()); m.setName(d.getDeptName());
                m.setParentId(d.getCompId()); return m; })
            .collect(Collectors.toList());
    }

    public List<MasterViewModel> getPlantsByLocation(Integer locationId) {
        return plantRepo.findByLocationIdAndIsActiveAndIsDeleted(locationId, true, false).stream()
            .map(p -> { MasterViewModel m = new MasterViewModel();
                m.setId(p.getPlantId()); m.setName(p.getPlantName());
                m.setParentId(p.getCompId()); m.setLocationId(p.getLocationId()); m.setCode(p.getPlantCode());
                return m; })
            .collect(Collectors.toList());
    }

    public List<MasterViewModel> getDesignationsByPlantForDropdown(Integer plantId) {
        return designationRepo.findByPlantIdAndIsActiveAndIsDeleted(plantId, true, false).stream()
            .map(d -> { MasterViewModel m = new MasterViewModel();
                m.setId(d.getDesignationId()); m.setName(d.getDesignation());
                m.setPlantId(d.getPlantId()); return m; })
            .collect(Collectors.toList());
    }

    // === CASCADING DROPDOWNS ===
    public List<DropdownViewModel> getDepartmentList() {
        return deptRepo.findByIsActiveAndIsDeleted(true, false).stream()
            .map(d -> new DropdownViewModel(d.getDeptId(), d.getDeptName(), d.getDeptShortName()))
            .collect(Collectors.toList());
    }

    public List<DropdownViewModel> getDesignationList(Integer deptId) {
        List<DesignationMaster> list = deptId != null ?
            designationRepo.findByDeptIdAndIsDeleted(deptId, false) :
            designationRepo.findByIsDeletedOrderByDesignation(false);
        return list.stream()
            .map(d -> new DropdownViewModel(d.getDesignationId(), d.getDesignation(), d.getDesignationShortName()))
            .collect(Collectors.toList());
    }

    public List<DropdownViewModel> getEmployeesByDesignation(Integer designationId) {
        return employeeRepo.findByDesignationIdAndIsActiveAndIsDeleted(designationId, true, false).stream()
            .map(e -> {
                String fullName = (e.getFirstName() != null ? e.getFirstName() : "");
                if (e.getMiddleName() != null && !e.getMiddleName().isEmpty()) fullName += " " + e.getMiddleName();
                if (e.getLastName() != null && !e.getLastName().isEmpty()) fullName += " " + e.getLastName();
                return new DropdownViewModel(e.getEmpId(), fullName.trim(), e.getEmpCode());
            })
            .collect(Collectors.toList());
    }

    public List<DropdownViewModel> getEmployeesByDepartment(String deptName) {
        return employeeRepo.findByDeptName(deptName).stream()
            .map(e -> {
                String fullName = (e.getFirstName() != null ? e.getFirstName() : "");
                if (e.getMiddleName() != null && !e.getMiddleName().isEmpty()) fullName += " " + e.getMiddleName();
                if (e.getLastName() != null && !e.getLastName().isEmpty()) fullName += " " + e.getLastName();
                return new DropdownViewModel(e.getEmpId(), fullName.trim(), e.getEmpCode());
            })
            .collect(Collectors.toList());
    }

    public List<DropdownViewModel> getEmployeesByDeptAndDesignation(String deptName, Integer designationId) {
        return employeeRepo.findByDeptNameAndDesignationId(deptName, designationId).stream()
            .map(e -> {
                String fullName = (e.getFirstName() != null ? e.getFirstName() : "");
                if (e.getMiddleName() != null && !e.getMiddleName().isEmpty()) fullName += " " + e.getMiddleName();
                if (e.getLastName() != null && !e.getLastName().isEmpty()) fullName += " " + e.getLastName();
                return new DropdownViewModel(e.getEmpId(), fullName.trim(), e.getEmpCode());
            })
            .collect(Collectors.toList());
    }

    public List<String> getDepartmentNames() {
        return employeeRepo.findDistinctDeptNames();
    }

    public List<DropdownViewModel> getGradeList() {
        return gradeRepo.findAll().stream()
            .map(g -> new DropdownViewModel(g.getGradeId(), g.getGrade(), ""))
            .collect(Collectors.toList());
    }

    // === EMPLOYEE MASTER (User CRUD) ===
    public MasterViewModel getEmployeeById(Integer id) {
        return employeeRepo.findById(id).map(e -> {
            MasterViewModel m = new MasterViewModel();
            m.setId(e.getEmpId()); m.setName(getFullName(e)); m.setCode(e.getEmpCode());
            m.setShortName(e.getDeptName()); m.setDescription(e.getDesignationName());
            m.setParentId(e.getCompId()); m.setPlantId(e.getPlantId()); m.setField1(e.getEmailId());
            m.setField2(e.getMobileNo()); m.setField3(e.getUserName());
            m.setIsAdminUser(e.getIsAdminUser() != null && e.getIsAdminUser());
            return m;
        }).orElse(null);
    }

    public List<MasterViewModel> getAllEmployees() {
        EmployeeMaster loggedUser = AuthContext.get();
        // PlantAdmin: only see their own plant's users
        if (loggedUser != null && AuthContext.isPlantAdmin() && loggedUser.getPlantId() != null) {
            return getEmployeesByPlantId(loggedUser.getPlantId());
        }
        return employeeRepo.findAllActive().stream()
            .map(e -> { MasterViewModel m = new MasterViewModel();
                m.setId(e.getEmpId()); m.setName(getFullName(e)); m.setCode(e.getEmpCode());
                m.setShortName(e.getDeptName()); m.setDescription(e.getDesignationName());
                m.setParentId(e.getCompId()); m.setPlantId(e.getPlantId()); m.setIsActive(e.getIsActive());
                m.setField1(e.getEmailId()); m.setField2(e.getMobileNo()); m.setField3(e.getUserName());
                m.setIsAdminUser(e.getIsAdminUser() != null && e.getIsAdminUser());
                m.setReportId(e.getReportId()); m.setReportName(e.getReportName());
                return m; })
            .collect(Collectors.toList());
    }

    public List<DropdownViewModel> getReportingEmployees() {
        java.util.Set<Integer> plantAdminIds = plantRepo.findByIsActiveAndIsDeleted(true, false).stream()
            .map(p -> p.getPlantAdminId())
            .filter(id -> id != null)
            .collect(java.util.stream.Collectors.toSet());
        return employeeRepo.findByIsActiveAndIsDeleted(true, false).stream()
            .filter(e -> !Boolean.TRUE.equals(e.getIsAdminUser()) && !plantAdminIds.contains(e.getEmpId()))
            .map(e -> { DropdownViewModel d = new DropdownViewModel();
                d.setId(e.getEmpId()); d.setName(getFullName(e)); return d; })
            .collect(Collectors.toList());
    }

    @Transactional
    public MasterViewModel saveEmployee(MasterViewModel model) {
        EmployeeMaster e = model.getId() != null ? employeeRepo.findById(model.getId()).orElse(new EmployeeMaster()) : new EmployeeMaster();
        e.setUserName(model.getCode());
        e.setEmpCode(model.getCode());
        e.setFirstName(model.getName());
        e.setDeptName(model.getShortName());
        e.setDesignationName(model.getDescription());
        // PlantAdmin/Admin: force CompId and PlantId from logged-in user
        EmployeeMaster loggedUser = AuthContext.get();
        if (loggedUser != null && loggedUser.getPlantId() != null && (Boolean.TRUE.equals(loggedUser.getIsAdminUser()) || AuthContext.isPlantAdmin())) {
            e.setCompId(loggedUser.getCompId());
            e.setPlantId(loggedUser.getPlantId());
        } else {
            e.setCompId(model.getParentId());
            e.setPlantId(model.getPlantId());
        }
        e.setEmailId(model.getField1());
        e.setMobileNo(model.getField2());
        e.setIsActive(true); e.setIsDeleted(false);
        if (model.getIsAdminUser() != null) e.setIsAdminUser(model.getIsAdminUser());
        e.setIsSecurity(model.getIsSecurity() != null && model.getIsSecurity());
        e.setReportId(model.getReportId());
        if (model.getReportId() != null) {
            employeeRepo.findById(model.getReportId()).ifPresent(r -> e.setReportName(r.getFirstName()));
        } else {
            e.setReportName(null);
        }
        boolean isNewUser = (model.getId() == null || model.getId() == 0);
        String rawPassword = null;
        if (model.getPassword() != null && !model.getPassword().isEmpty()) {
            rawPassword = model.getPassword();
            e.setPassword(passwordEncoder.encode(rawPassword));
        } else if (isNewUser || e.getPassword() == null || e.getPassword().isEmpty()) {
            rawPassword = "Welcome@123";
            e.setPassword(passwordEncoder.encode(rawPassword));
        }
        if (isNewUser) {
            e.setIsTempPassword(true);
        }
        employeeRepo.save(e);
        model.setId(e.getEmpId());
        model.setMsg("Employee saved successfully");

        // Send credential email for new users
        if (isNewUser && rawPassword != null && e.getEmailId() != null && !e.getEmailId().isEmpty()) {
            String fullName = (e.getFirstName() != null ? e.getFirstName() : e.getUserName());
            emailService.sendCredentialsEmail(e.getEmailId(), fullName, e.getUserName(), rawPassword);
            model.setMsg("Employee saved successfully. Credentials sent to " + e.getEmailId());
        }

        // Handle Plant Admin role: update PlantMaster.PlantAdminId
        if (Boolean.TRUE.equals(model.getIsPlantAdmin()) && e.getPlantId() != null) {
            plantRepo.findById(e.getPlantId()).ifPresent(plant -> {
                plant.setPlantAdminId(e.getEmpId());
                plant.setLastUpdatedDate(new Date());
                plantRepo.save(plant);
            });
        }

        return model;
    }

    @Transactional
    public void deleteEmployee(Integer id) {
        employeeRepo.findById(id).ifPresent(e -> { e.setIsDeleted(true); e.setIsActive(false); employeeRepo.save(e); });
    }

    // Custom filtered employee lookup for cascading dropdowns
    public List<DropdownViewModel> getFilteredEmployees(Integer compId, String deptName, Integer designationId) {
        // PlantAdmin/Admin: auto-scope to their plant without requiring plantId in params
        EmployeeMaster lu = AuthContext.get();
        if (lu != null && lu.getPlantId() != null && Boolean.TRUE.equals(lu.getIsAdminUser())) {
            return getFilteredEmployeesByPlant(lu.getPlantId(), deptName, designationId);
        }

        List<EmployeeMaster> list;
        if (compId != null && deptName != null && !deptName.isEmpty() && designationId != null) {
            list = employeeRepo.findByDeptNameAndDesignationId(deptName, designationId);
            list = list.stream().filter(e -> compId.equals(e.getCompId())).collect(Collectors.toList());
        } else if (compId != null && deptName != null && !deptName.isEmpty()) {
            list = employeeRepo.findByCompIdAndDeptName(compId, deptName);
        } else if (deptName != null && !deptName.isEmpty() && designationId != null) {
            list = employeeRepo.findByDeptNameAndDesignationId(deptName, designationId);
        } else if (deptName != null && !deptName.isEmpty()) {
            list = employeeRepo.findByDeptName(deptName);
        } else if (compId != null) {
            list = employeeRepo.findByCompId(compId);
        } else {
            list = employeeRepo.findAllActive();
        }
        return list.stream()
            .map(emp -> new DropdownViewModel(emp.getEmpId(), getFullName(emp), emp.getEmpCode()))
            .collect(Collectors.toList());
    }

    public List<String> getDeptNamesByCompany(Integer compId) {
        return employeeRepo.findDistinctDeptNamesByComp(compId);
    }

    // === PLANT MANAGEMENT ===
    public List<MasterViewModel> getAllPlants() {
        return plantRepo.findByIsActiveAndIsDeleted(true, false).stream()
            .map(p -> { MasterViewModel m = new MasterViewModel();
                m.setId(p.getPlantId()); m.setName(p.getPlantName());
                m.setCode(p.getPlantCode()); m.setParentId(p.getCompId());
                m.setLocationId(p.getLocationId());
                m.setIsActive(p.getIsActive()); m.setDescription(p.getAddress());
                m.setField1(p.getCity()); m.setField2(p.getState());
                m.setPlantAdminId(p.getPlantAdminId());
                return m; })
            .collect(Collectors.toList());
    }

    public List<MasterViewModel> getPlantsByCompany(Integer compId) {
        return plantRepo.findByCompIdAndIsActiveAndIsDeleted(compId, true, false).stream()
            .map(p -> { MasterViewModel m = new MasterViewModel();
                m.setId(p.getPlantId()); m.setName(p.getPlantName());
                m.setCode(p.getPlantCode()); m.setParentId(p.getCompId());
                m.setLocationId(p.getLocationId()); m.setIsActive(p.getIsActive());
                return m; })
            .collect(Collectors.toList());
    }

    @Transactional
    public MasterViewModel savePlant(MasterViewModel model) {
        PlantMaster p = model.getId() != null ? plantRepo.findById(model.getId()).orElse(new PlantMaster()) : new PlantMaster();
        p.setPlantName(model.getName());
        p.setPlantCode(model.getCode());
        p.setCompId(model.getParentId());
        p.setLocationId(model.getLocationId());
        p.setAddress(model.getDescription());
        p.setCity(model.getField1());
        p.setState(model.getField2());
        p.setPlantAdminId(model.getPlantAdminId());
        p.setIsActive(true); p.setIsDeleted(false);
        if (model.getId() == null) p.setCreatedDate(new Date());
        p.setLastUpdatedDate(new Date());
        plantRepo.save(p);
        model.setId(p.getPlantId());
        model.setMsg("Plant saved successfully");
        return model;
    }

    @Transactional
    public void deletePlant(Integer id) {
        plantRepo.findById(id).ifPresent(p -> { p.setIsDeleted(true); p.setIsActive(false); p.setLastUpdatedDate(new Date()); plantRepo.save(p); });
    }

    // === FULL-DETAIL EMPLOYEE QUERIES (MasterViewModel) ===
    public List<MasterViewModel> getEmployeesByPlantId(Integer plantId) {
        return employeeRepo.findByPlantId(plantId).stream()
            .map(e -> toMasterViewModel(e))
            .collect(Collectors.toList());
    }

    public List<MasterViewModel> getEmployeesByDesignationId(Integer designationId) {
        return employeeRepo.findByDesignationIdAndIsActiveAndIsDeleted(designationId, true, false).stream()
            .map(e -> toMasterViewModel(e))
            .collect(Collectors.toList());
    }

    private MasterViewModel toMasterViewModel(EmployeeMaster e) {
        MasterViewModel m = new MasterViewModel();
        m.setId(e.getEmpId());
        m.setName(getFullName(e));
        m.setCode(e.getEmpCode());
        m.setShortName(e.getDeptName());
        m.setDescription(e.getDesignationName());
        m.setParentId(e.getCompId());
        m.setPlantId(e.getPlantId());
        m.setField1(e.getEmailId());
        m.setField2(e.getMobileNo());
        m.setField3(e.getUserName());
        m.setIsActive(e.getIsActive());
        m.setIsAdminUser(e.getIsAdminUser() != null && e.getIsAdminUser());
        m.setIsSecurity(e.getIsSecurity() != null && e.getIsSecurity());
        m.setReportId(e.getReportId());
        m.setReportName(e.getReportName());
        return m;
    }

    // === PLANT-SCOPED HIERARCHICAL EMPLOYEES ===
    public List<DropdownViewModel> getPlantList() {
        return plantRepo.findByIsActiveAndIsDeleted(true, false).stream()
            .map(p -> new DropdownViewModel(p.getPlantId(), p.getPlantName(), p.getPlantCode()))
            .collect(Collectors.toList());
    }

    public List<DropdownViewModel> getEmployeesByPlant(Integer plantId) {
        return employeeRepo.findByPlantId(plantId).stream()
            .map(e -> new DropdownViewModel(e.getEmpId(), getFullName(e), e.getEmpCode()))
            .collect(Collectors.toList());
    }

    public List<String> getDeptNamesByPlant(Integer plantId) {
        return employeeRepo.findDistinctDeptNamesByPlant(plantId);
    }

    public List<DropdownViewModel> getEmployeesByPlantAndDept(Integer plantId, String deptName) {
        return employeeRepo.findByPlantIdAndDeptName(plantId, deptName).stream()
            .map(e -> new DropdownViewModel(e.getEmpId(), getFullName(e), e.getEmpCode()))
            .collect(Collectors.toList());
    }

    public List<DropdownViewModel> getEmployeesByPlantAndDesig(Integer plantId, Integer designationId) {
        return employeeRepo.findByPlantIdAndDesignationId(plantId, designationId).stream()
            .map(e -> new DropdownViewModel(e.getEmpId(), getFullName(e), e.getEmpCode()))
            .collect(Collectors.toList());
    }

    // Hierarchical employee tree: grouped by dept > designation > employees
    public List<Map<String, Object>> getEmployeeHierarchy(Integer plantId) {
        List<EmployeeMaster> employees = employeeRepo.findByPlantId(plantId);
        Map<String, Map<String, List<EmployeeMaster>>> tree = new LinkedHashMap<>();

        for (EmployeeMaster e : employees) {
            String dept = e.getDeptName() != null ? e.getDeptName() : "Unassigned";
            String desig = e.getDesignationName() != null ? e.getDesignationName() : "General";
            tree.computeIfAbsent(dept, k -> new LinkedHashMap<>())
                .computeIfAbsent(desig, k -> new ArrayList<>())
                .add(e);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, List<EmployeeMaster>>> deptEntry : tree.entrySet()) {
            Map<String, Object> deptNode = new LinkedHashMap<>();
            deptNode.put("label", deptEntry.getKey());
            deptNode.put("type", "department");
            List<Map<String, Object>> designations = new ArrayList<>();
            for (Map.Entry<String, List<EmployeeMaster>> desigEntry : deptEntry.getValue().entrySet()) {
                Map<String, Object> desigNode = new LinkedHashMap<>();
                desigNode.put("label", desigEntry.getKey());
                desigNode.put("type", "designation");
                List<Map<String, Object>> empList = new ArrayList<>();
                for (EmployeeMaster e : desigEntry.getValue()) {
                    Map<String, Object> empNode = new LinkedHashMap<>();
                    empNode.put("id", e.getEmpId());
                    empNode.put("label", getFullName(e));
                    empNode.put("code", e.getEmpCode());
                    empNode.put("mobile", e.getMobileNo());
                    empNode.put("email", e.getEmailId());
                    empNode.put("type", "employee");
                    empList.add(empNode);
                }
                desigNode.put("children", empList);
                designations.add(desigNode);
            }
            deptNode.put("children", designations);
            result.add(deptNode);
        }

        return result;
    }

    // Plant-scoped filtered employee lookup
    public List<DropdownViewModel> getFilteredEmployeesByPlant(Integer plantId, String deptName, Integer designationId) {
        List<EmployeeMaster> list;
        if (deptName != null && !deptName.isEmpty() && designationId != null) {
            list = employeeRepo.findByPlantIdAndDesignationId(plantId, designationId);
            list = list.stream().filter(e -> deptName.equals(e.getDeptName())).collect(Collectors.toList());
        } else if (deptName != null && !deptName.isEmpty()) {
            list = employeeRepo.findByPlantIdAndDeptName(plantId, deptName);
        } else if (designationId != null) {
            list = employeeRepo.findByPlantIdAndDesignationId(plantId, designationId);
        } else {
            list = employeeRepo.findByPlantId(plantId);
        }
        return list.stream()
            .map(emp -> new DropdownViewModel(emp.getEmpId(), getFullName(emp), emp.getEmpCode()))
            .collect(Collectors.toList());
    }

    // === CATEGORY MASTER ===
    public List<DropdownViewModel> getCategoryList() {
        return categoryRepo.findByIsActiveAndIsDeleted(true, false).stream()
            .map(c -> new DropdownViewModel(c.getCategoryId(), c.getCategoryName(), ""))
            .collect(Collectors.toList());
    }

    public List<MasterViewModel> getAllCategories() {
        return categoryRepo.findByIsActiveAndIsDeleted(true, false).stream()
            .map(c -> { MasterViewModel m = new MasterViewModel();
                m.setId(c.getCategoryId()); m.setName(c.getCategoryName()); return m; })
            .collect(Collectors.toList());
    }

    @Transactional
    public MasterViewModel saveCategory(MasterViewModel model) {
        CategoryMaster c = model.getId() != null ? categoryRepo.findById(model.getId()).orElse(new CategoryMaster()) : new CategoryMaster();
        c.setCategoryName(model.getName());
        c.setIsActive(true); c.setIsDeleted(false);
        categoryRepo.save(c);
        model.setId(c.getCategoryId());
        model.setMsg("Category saved");
        return model;
    }

    @Transactional
    public void deleteCategory(Integer id) {
        categoryRepo.findById(id).ifPresent(c -> { c.setIsDeleted(true); c.setIsActive(false); categoryRepo.save(c); });
    }

    // === VISIT PURPOSE MASTER ===
    public List<DropdownViewModel> getVisitPurposeList() {
        return visitPurposeRepo.findByIsActiveAndIsDeleted(true, false).stream()
            .map(p -> new DropdownViewModel(p.getPurposeId(), p.getPurposeName(), ""))
            .collect(Collectors.toList());
    }

    public List<MasterViewModel> getAllVisitPurposes() {
        return visitPurposeRepo.findByIsActiveAndIsDeleted(true, false).stream()
            .map(p -> { MasterViewModel m = new MasterViewModel();
                m.setId(p.getPurposeId()); m.setName(p.getPurposeName()); m.setDescription(p.getDescription()); return m; })
            .collect(Collectors.toList());
    }

    @Transactional
    public MasterViewModel saveVisitPurpose(MasterViewModel model) {
        VisitPurposeMaster p = model.getId() != null ? visitPurposeRepo.findById(model.getId()).orElse(new VisitPurposeMaster()) : new VisitPurposeMaster();
        p.setPurposeName(model.getName());
        p.setDescription(model.getDescription());
        p.setIsActive(true); p.setIsDeleted(false);
        visitPurposeRepo.save(p);
        model.setId(p.getPurposeId());
        model.setMsg("Visit purpose saved");
        return model;
    }

    @Transactional
    public void deleteVisitPurpose(Integer id) {
        visitPurposeRepo.findById(id).ifPresent(p -> { p.setIsDeleted(true); p.setIsActive(false); visitPurposeRepo.save(p); });
    }

    private String getFullName(EmployeeMaster e) {
        String fullName = (e.getFirstName() != null ? e.getFirstName() : "");
        if (e.getMiddleName() != null && !e.getMiddleName().isEmpty()) fullName += " " + e.getMiddleName();
        if (e.getLastName() != null && !e.getLastName().isEmpty()) fullName += " " + e.getLastName();
        return fullName.trim();
    }
}
