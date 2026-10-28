package com.mottainai.mottainaicrud.service;

import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private final CompanyDao companyDao;
    private final SubscriptionPlanDao subscriptionPlanDao;

    public CompanyService(CompanyDao companyDao, SubscriptionPlanDao subscriptionPlanDao) {
        this.companyDao = companyDao;
        this.subscriptionPlanDao = subscriptionPlanDao;
    }

    public List<CompanyResponseDTO> findAll() {
        return companyDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<CompanyResponseDTO> findByTradeName(String tradeName) {
        return companyDao.findByTradeName(tradeName)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<CompanyResponseDTO> findByOfficialName(String officialName) {
        return companyDao.findByOfficialName(officialName)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public CompanyResponseDTO findByCompanyId(int companyId) {
        Company company = companyDao.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empresa com id " + companyId + " não encontrada."
                ));

        return toResponseDTO(company);
    }

    private Company toEntity(CompanyRequestDTO dto) {
        Company company = new Company();
        company.setCnpj(dto.getCnpj());
        company.setOfficialName(dto.getOfficialName());
        company.setTradeName(dto.getTradeName());
        company.setEmail(dto.getEmail());
        company.setPhone(dto.getPhone());
        company.setLatitude(dto.getLatitude());
        company.setLongitude(dto.getLongitude());
        return company;
    }

    private CompanyResponseDTO toResponseDTO(Company company) {
        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setCompanyId(company.getCompanyId());
        dto.setCnpj(company.getCnpj());
        dto.setOfficialName(company.getOfficialName());
        dto.setTradeName(company.getTradeName());
        dto.setEmail(company.getEmail());
        dto.setPhone(company.getPhone());
        dto.setLatitude(company.getLatitude());
        dto.setLongitude(company.getLongitude());
        dto.setCreatedAt(company.getCreatedAt());
        dto.setSubscriptionPlanId(company.getSubscriptionPlan().getPlanId());
        return dto;
    }
}
