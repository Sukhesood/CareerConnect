package Sprint1.demo.service;

import Sprint1.demo.model.*;
import Sprint1.demo.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(
            CompanyRepository companyRepository,
            UserRepository userRepository) {

        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveCompany(
            String recruiterEmail,
            Company submittedCompany) {

        User recruiter = userRepository
                .findByEmail(recruiterEmail)
                .orElseThrow(() ->
                    new IllegalArgumentException("User not found."));

        if (!"RECRUITER".equals(recruiter.getRole())) {
            throw new IllegalArgumentException(
                    "Only recruiters can manage companies.");
        }

        Company company = recruiter.getCompany();

        if (company == null) {
            company = new Company();
        }

        company.setName(submittedCompany.getName().trim());
        company.setContactEmail(
                submittedCompany.getContactEmail().trim());
        company.setWebsite(submittedCompany.getWebsite());
        company.setDescription(submittedCompany.getDescription());

        companyRepository.save(company);

        recruiter.setCompany(company);
        userRepository.save(recruiter);
    }
}