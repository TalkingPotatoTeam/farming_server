package tp.farming_springboot.domain.product.service;


import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tp.farming_springboot.domain.product.dto.PhotoFileDto;
import tp.farming_springboot.domain.product.model.PhotoFile;
import tp.farming_springboot.domain.product.repository.FileRepository;
import tp.farming_springboot.domain.product.util.MD5Generator;
import tp.farming_springboot.response.Message;
import tp.farming_springboot.response.StatusEnum;
import tp.farming_springboot.domain.product.util.MD5Generator;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileService{

    private final FileRepository fileRepository;


    public PhotoFile photoFileCreate(MultipartFile receiptFile)  {
        try {
            String origFilename = receiptFile.getOriginalFilename();

            System.out.println("origFilename = " + origFilename);
            
            String filename = new MD5Generator(origFilename + LocalDateTime.now()).toString(); // file name 암호화
            String savePath = System.getProperty("user.dir") + "/receipt_photo_files";

            if (!new File(savePath).exists()) {
                new File(savePath).mkdir();
            }

            String filePath = savePath + "/" + filename;
            receiptFile.transferTo(new File(filePath));

            PhotoFileDto fileDto = new PhotoFileDto();
            fileDto.setOrigFilename(origFilename);
            fileDto.setFilename(filename);
            fileDto.setFilePath(filePath);

            PhotoFile receipt = fileRepository.save(fileDto.toEntity());

            return receipt;
        }
        catch(Exception e) {
            return null;
        }
    }


    public List<PhotoFile> photoFileListCreate(List<MultipartFile> files) {
        List<PhotoFile> photoFileList = new ArrayList<PhotoFile>();

        if(files.size() > 10) {
            return null;
        }

        try {
            for (MultipartFile file : files) {

                String origFilename = file.getOriginalFilename();
                String filename = new MD5Generator(origFilename + LocalDateTime.now()).toString(); // file name 암호화
                String savePath = System.getProperty("user.dir") + "/photo_files";

                if (!new File(savePath).exists()) {
                    new File(savePath).mkdir();
                }
                String filePath = savePath + "/" + filename;
                file.transferTo(new File(filePath));

                PhotoFileDto fileDto = new PhotoFileDto();
                fileDto.setOrigFilename(origFilename);

                fileDto.setFilename(filename);
                fileDto.setFilePath(filePath);

                PhotoFile photoFile = fileRepository.save(fileDto.toEntity());

                System.out.println("photoFile = " + photoFile);
                photoFileList.add(photoFile);

            }
            return photoFileList;

        } catch(Exception e) {
            return null;
        }


    }


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