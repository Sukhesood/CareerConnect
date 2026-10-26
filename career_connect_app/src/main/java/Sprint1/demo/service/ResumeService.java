
package Sprint1.demo.service;

import Sprint1.demo.model.Resume;
import Sprint1.demo.model.User;
import Sprint1.demo.repository.ResumeRepository;
import Sprint1.demo.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipInputStream;
import java.io.ByteArrayInputStream;
import java.util.zip.ZipEntry;

@Service
public class ResumeService {
	private static final int MAX_RESUMES = 5;
    public static final long MAX_SIZE =
            4L * 1024 * 1024;

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    public ResumeService(
            ResumeRepository resumeRepository,
            UserRepository userRepository) {

        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
    }

    private User currentUser(String email) {

        if (email == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED);
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED));
    }

    // List all resumes for logged-in user
    @Transactional(readOnly = true)
    public List<Resume> list(String email) {

        User user = currentUser(email);

        return resumeRepository
                .findByUser_IdOrderByUploadedAtDesc(
                        user.getId());
    }

    // Upload a new resume
    @Transactional
    public void upload(
            String email,
            MultipartFile file) {

        User user = currentUser(email);

long resumeCount =
        resumeRepository.countByUser_Id(user.getId());

if (resumeCount >= MAX_RESUMES) {
    throw new IllegalArgumentException(
        "You can upload a maximum of 5 resumes. "
        + "Please delete an existing resume "
        + "before uploading a new one."
    );
}

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please choose a resume.");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException(
                    "Resume must not exceed 4 MB.");
        }

        String filename = file.getOriginalFilename();

        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid filename.");
        }

        filename = filename.replace('\\', '/');
        filename = filename.substring(
                filename.lastIndexOf('/') + 1);

        filename = filename.replaceAll(
                "[\\p{Cntrl}]", "_");

        if (filename.length() > 200) {
            throw new IllegalArgumentException(
                    "Filename is too long.");
        }

        String lowerName =
                filename.toLowerCase(Locale.ROOT);

        try {

            byte[] bytes = file.getBytes();

            if (bytes.length > MAX_SIZE) {
                throw new IllegalArgumentException(
                        "Resume must not exceed 4 MB.");
            }

            String contentType;

            if (lowerName.endsWith(".pdf")
                    && isPdf(bytes)) {

                contentType = "application/pdf";

            } else if (lowerName.endsWith(".docx")
                    && isDocx(bytes)) {

                contentType =
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

            } else {

                throw new IllegalArgumentException(
                    "Only valid PDF and DOCX files are allowed.");
            }

            Resume resume = new Resume(
                    user,
                    filename,
                    contentType,
                    bytes
            );

            resumeRepository.save(resume);

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Unable to read resume.", e);
        }
    }

    private boolean isPdf(byte[] bytes) {

        return bytes.length >= 5
                && new String(
                    bytes, 0, 5,
                    StandardCharsets.US_ASCII)
                    .equals("%PDF-");
    }

    private boolean isDocx(byte[] bytes) {

        if (bytes.length < 4
                || bytes[0] != 'P'
                || bytes[1] != 'K'
                || bytes[2] != 3
                || bytes[3] != 4) {
            return false;
        }

        boolean hasContentTypes = false;
        boolean hasDocument = false;

        try (ZipInputStream zip =
                new ZipInputStream(
                    new ByteArrayInputStream(bytes))) {

            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {

                if ("[Content_Types].xml".equals(
                        entry.getName())) {
                    hasContentTypes = true;
                }

                if ("word/document.xml".equals(
                        entry.getName())) {
                    hasDocument = true;
                }

                zip.closeEntry();

                if (hasContentTypes && hasDocument) {
                    return true;
                }
            }

        } catch (IOException e) {
            return false;
        }

        return false;
    }

    // Retrieve only a resume owned by the user
    @Transactional(readOnly = true)
    public Resume getOwned(String email, Long id) {

        User user = currentUser(email);

        return resumeRepository
                .findByIdAndUser_Id(
                        id, user.getId())
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Resume not found."));
    }

    // Delete one selected resume
    @Transactional
    public void delete(String email, Long id) {

        Resume resume = getOwned(email, id);

        resumeRepository.delete(resume);
    }
}
