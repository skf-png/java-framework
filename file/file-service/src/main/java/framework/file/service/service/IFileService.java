package framework.file.service.service;

import framework.file.service.domain.DTO.FileDTO;
import framework.file.service.domain.DTO.SignDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    FileDTO upload(MultipartFile file);

    SignDTO getSign();
}