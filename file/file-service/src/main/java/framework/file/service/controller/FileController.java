package framework.file.service.controller;

import framework.domain.R;
import framework.file.service.domain.DTO.FileDTO;
import framework.file.service.domain.DTO.SignDTO;
import framework.file.service.domain.VO.FileVO;
import framework.file.service.domain.VO.SignVO;
import framework.file.service.service.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class FileController {

    @Autowired
    private IFileService fileService;

    @PostMapping("/upload")
    public R<FileVO> upload(MultipartFile file) {
        FileDTO fileDTO = fileService.upload(file);
        FileVO fileVO = new FileVO();
        BeanUtils.copyProperties(fileDTO, fileVO);
        return R.success(fileVO);
    }

    @GetMapping("/sign")
    public R<SignVO> getSign() {
        SignDTO signDTO = fileService.getSign();
        SignVO signVO = new SignVO();
        BeanUtils.copyProperties(signDTO, signVO);
        return R.success(signVO);
    }
}