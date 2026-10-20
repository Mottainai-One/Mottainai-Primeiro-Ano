package org.mot.mottainaijdbc.dto;

public class EmployeeRoleEmployeeCountDTO {
    //ATRIBUTOS
    private int roleId;
    private String employeeRole;
    private int totalEmployee;
    //CONSTRUTOR
    public EmployeeRoleEmployeeCountDTO(int roleId, String employeeRole, int totalEmployee) {
        this.roleId = roleId;
        this.employeeRole = employeeRole;
        this.totalEmployee = totalEmployee;
    }
    //GETTERS
    public int getRoleId() {
        return roleId;
    }

    public String getEmployeeRole() {
        return employeeRole;
    }

    public int getTotalEmployee() {
        return totalEmployee;
    }
    //TOSTRING
    @Override
    public String toString() {
        return "EmployeeRoleResponseDTO{" +
                "roleId=" + roleId +
                ", employeeRole='" + employeeRole + '\'' +
                ", totalEmployee=" + totalEmployee +
                '}';
    }
}

