package com.visitorapp.config;

import com.visitorapp.entity.EmployeeMaster;

/**
 * Thread-local holder for the currently authenticated user.
 * Populated by AuthContextFilter before each request is processed.
 */
public class AuthContext {

    private static final ThreadLocal<EmployeeMaster> EMP_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> PLANT_ADMIN_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> SECURITY_HOLDER = new ThreadLocal<>();

    /**
     * @return the EmployeeMaster for the currently authenticated user, or null
     */
    public static EmployeeMaster get() {
        return EMP_HOLDER.get();
    }

    /**
     * Store the authenticated user in thread-local context.
     */
    public static void set(EmployeeMaster emp) {
        EMP_HOLDER.set(emp);
    }

    /**
     * Store the plant-admin flag (determined at filter time).
     */
    public static void setPlantAdmin(boolean isPlantAdmin) {
        PLANT_ADMIN_HOLDER.set(isPlantAdmin);
    }

    /**
     * Clear the thread-local context. Must be called after request completes.
     */
    public static void clear() {
        EMP_HOLDER.remove();
        PLANT_ADMIN_HOLDER.remove();
        SECURITY_HOLDER.remove();
    }

    /**
     * @return true if the logged-in user is a SuperAdmin
     * (IsAdminUser=true AND CompId=null)
     */
    public static boolean isSuperAdmin() {
        EmployeeMaster emp = EMP_HOLDER.get();
        return emp != null
            && Boolean.TRUE.equals(emp.getIsAdminUser())
            && emp.getCompId() == null;
    }

    /**
     * @return true if the logged-in user is a PlantAdmin
     * (their EmpId matches PlantMaster.PlantAdminId for their plant)
     */
    public static boolean isPlantAdmin() {
        return Boolean.TRUE.equals(PLANT_ADMIN_HOLDER.get());
    }

    /**
     * Store the security flag.
     */
    public static void setSecurity(boolean isSecurity) {
        SECURITY_HOLDER.set(isSecurity);
    }

    /**
     * @return true if the logged-in user is a Security user
     */
    public static boolean isSecurity() {
        return Boolean.TRUE.equals(SECURITY_HOLDER.get());
    }
}
