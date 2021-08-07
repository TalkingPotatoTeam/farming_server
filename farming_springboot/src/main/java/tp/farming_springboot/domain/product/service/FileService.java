package tp.farming_springboot.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tp.farming_springboot.domain.product.dto.PhotoFileDto;
import tp.farming_springboot.domain.product.model.PhotoFile;
import tp.farming_springboot.domain.product.repository.FileRepository;
import tp.farming_springboot.domain.product.util.MD5Generator;
import tp.farming_springboot.exception.PhotoFileException;

import javax.transaction.Transactional;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileService{

    private final FileRepository fileRepository;

    public PhotoFile photoFileCreate(MultipartFile receiptFile) throws PhotoFileException {
        String origFilename = receiptFile.getOriginalFilename();
        List<String> allowedExtNameList = Arrays.asList(new String[]{"jpg", "gif", "png", "bmp", "rle", "dib", "tif", "tiff"});
        String extensionName = Objects.requireNonNull(origFilename).substring(origFilename.lastIndexOf(".") + 1);


        try {
            if(!allowedExtNameList.contains( extensionName.toLowerCase())) {
                throw new IllegalArgumentException("File Extension Not allowed. Only allow Photo File.");
            }
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
            throw new PhotoFileException(e.getMessage());
        }

    }


    public List<PhotoFile> photoFileListCreate(List<MultipartFile> files) throws PhotoFileException {
        List<PhotoFile> photoFileList = new ArrayList<PhotoFile>();

        if(files.size() > 10) {
            throw new PhotoFileException("Photo can be uploaded within size 10.");
        }

        try {
            for (MultipartFile file : files) {

                String origFilename = file.getOriginalFilename();
                List<String> allowedExtNameList = Arrays.asList(new String[]{"jpg", "gif", "png", "bmp", "rle", "dib", "tif", "tiff"});
                String extensionName = Objects.requireNonNull(origFilename).substring(origFilename.lastIndexOf(".") + 1);

                if(!allowedExtNameList.contains( extensionName.toLowerCase())) {
                    throw new IllegalArgumentException("File Extension Not allowed. Only allow Photo File.");
                }

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
            throw new PhotoFileException("PhotoFile creating error.");
        }


    }

    public void deleteFiles(List<PhotoFile> photoList) throws PhotoFileException {

        if(photoList == null || photoList.size() == 0){
            return;
        } else {
            for (PhotoFile pf : photoList) {
                try {
                    Path filePath = Paths.get(pf.getFilePath());
                    Files.deleteIfExists(filePath);
                } catch (Exception e) {
                    throw new PhotoFileException("Can't delete Photo File from file directory.");
                }
            }
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