package org.mot.mottainaijdbc.dto;

public class EmployeeCountByStateDTO {
    //ATRIBUTOS
    private String state;
    private int totalEmplyee;
    //CONSTRUTOR
    public EmployeeCountByStateDTO(String state, int totalEmplyee) {
        this.state = state;
        this.totalEmplyee = totalEmplyee;
    }
    //GETTERS
    public String getState() {
        return state;
    }

    public int getTotalEmplyee() {
        return totalEmplyee;
    }
    //CONSTRUTOR
    @Override
    public String toString() {
        return "EmployeeCountByStateDTO{" +
                "state='" + state + '\'' +
                ", totalEmplyee=" + totalEmplyee +
                '}';
    }
}

