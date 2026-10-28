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

    public CompanyResponseDTO create(CompanyRequestDTO dto) {
        if (companyDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException(
                    "Já existe uma empresa cadastrada com esse CNPJ."
            );
        }

        SubscriptionPlan plan = subscriptionPlanDao
                .findById(dto.getSubscriptionPlanId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plano de assinatura não encontrado."
                ));

        Company company = toEntity(dto);
        company.setSubscriptionPlan(plan);

        Company saved = companyDao.save(company);
        return toResponseDTO(saved);
    }

    public CompanyResponseDTO update(int companyId, CompanyRequestDTO dto) {
        SubscriptionPlan plan = subscriptionPlanDao
                .findById(dto.getSubscriptionPlanId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plano de assinatura não encontrado."
                ));

        Company existing = companyDao.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empresa com id " + companyId + " não encontrada."
                ));

        if (!existing.getCnpj().equals(dto.getCnpj())
                && companyDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException(
                    "Já existe uma empresa cadastrada com esse CNPJ."
            );
        }

        existing.setCnpj(dto.getCnpj());
        existing.setOfficialName(dto.getOfficialName());
        existing.setTradeName(dto.getTradeName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setLatitude(dto.getLatitude());
        existing.setLongitude(dto.getLongitude());
        existing.setSubscriptionPlan(plan);

        return toResponseDTO(companyDao.save(existing));
    }
}
