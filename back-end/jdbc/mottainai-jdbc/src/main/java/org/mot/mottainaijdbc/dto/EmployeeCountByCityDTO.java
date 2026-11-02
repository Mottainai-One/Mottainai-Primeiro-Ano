package org.mot.mottainaijdbc.dto;

public class EmployeeCountByCityDTO {
    //ATRIBUTOS
    private String city;
    private String state;
    private int totalEmployees;
    //CONSTRUTOR
    public EmployeeCountByCityDTO(String city, String state, int totalEmployees) {
        this.city = city;
        this.state = state;
        this.totalEmployees = totalEmployees;
    }
    //GETTERS
    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }
    //TOSTRING
    @Override
    public String toString() {
        return "EmployeeCountByCity{" +
                "city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", totalEmployees=" + totalEmployees +
                '}';
    }
}
