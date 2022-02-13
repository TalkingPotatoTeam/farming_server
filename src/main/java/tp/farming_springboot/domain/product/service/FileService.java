package tp.farming_springboot.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tp.farming_springboot.domain.product.model.PhotoFile;
import tp.farming_springboot.domain.product.model.Product;

import tp.farming_springboot.domain.product.repository.FileRepository;
import tp.farming_springboot.domain.product.util.MD5Generator;
import tp.farming_springboot.exception.PhotoFileException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileService{
    private final S3UploaderService s3UploaderService;
    private final FileRepository fileRepository;

    public PhotoFile photoFileCreate(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        String originalName = file.getOriginalFilename();
        checkFileExtensions(originalName);
        String hashFileName = new MD5Generator(originalName + LocalDateTime.now()).toString();

        String s3BucketUrl = s3UploaderService.putS3(file, hashFileName);
        PhotoFile photoFile = PhotoFile.of(file.getOriginalFilename(), s3BucketUrl, hashFileName);

        return photoFile;
    }


    public List<PhotoFile> photoFileListCreate(List<MultipartFile> files, Product product) throws PhotoFileException, IOException, NoSuchAlgorithmException {
        List<PhotoFile> photoFileList = new ArrayList<>();

        if(files.size() > 10) {
            throw new PhotoFileException("Photo can be uploaded within size 10.");
        }

        for (MultipartFile file : files) {
            PhotoFile photoFile = photoFileCreate(file);
            photoFile.addProduct(product);
            photoFileList.add(photoFile);
        }

        return photoFileList;
    }
    @Transactional
    public void clearFileFromProduct(Product product) {
        List<PhotoFile> photoFile = product.getPhotoFile();
        PhotoFile receipt = product.getReceipt();
        if (photoFile.size() > 0) {
            photoFile.forEach(f -> s3UploaderService.deleteS3(f.getHashFilename()));
            product.deletePhotoFile();;
            fileRepository.deleteRelatedProductId(product.getId());
        }

        if (receipt != null) {
            s3UploaderService.deleteS3(receipt.getHashFilename());
            product.deleteReceiptAndCertified();;
        }

    }


    public void checkFileExtensions(String origFilename) {
        List<String> allowedExtNameList = Arrays.asList(new String[]{"jpg", "gif", "png", "bmp", "rle", "dib", "tif", "tiff", "jpeg"});
        String extensionName = Objects.requireNonNull(origFilename).substring(origFilename.lastIndexOf(".") + 1);

        if(!allowedExtNameList.contains( extensionName.toLowerCase())) {
            throw new IllegalArgumentException("File Extension Not allowed. Only allow Photo File.");
        }

    }

}