package tp.farming_springboot.domain.product.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tp.farming_springboot.domain.product.dto.PhotoFileDto;
import tp.farming_springboot.domain.product.model.PhotoFile;
import tp.farming_springboot.domain.product.repository.FileRepository;

import javax.transaction.Transactional;

@Service
public class FileService{

    private FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Transactional
    public PhotoFile saveFile(PhotoFileDto fileDto) {
        return fileRepository.save(fileDto.toEntity());
    }

    @Transactional
    public PhotoFileDto getFile(Long id) {
        PhotoFile file = fileRepository.findById(id).get();

        PhotoFileDto fileDto = PhotoFileDto.builder()
                .origFilename(file.getOrigFilename())
                .filename(file.getFilename())
                .filePath(file.getFilePath())
                .build();
        return fileDto;
    }
}