package tp.farming_springboot.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tp.farming_springboot.domain.product.model.PhotoFile;
import tp.farming_springboot.domain.product.model.Product;

import tp.farming_springboot.exception.PhotoFileException;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileService{

    public PhotoFile photoFileCreate(MultipartFile file) throws IOException {
        checkFileExtensions(file.getOriginalFilename());

        PhotoFile photoFile = PhotoFile.of(file.getOriginalFilename(), file.getBytes());

        return photoFile;
    }

    public List<PhotoFile> photoFileListCreate(List<MultipartFile> files, Product product) throws PhotoFileException, IOException {
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

    public void checkFileExtensions(String origFilename) {
        List<String> allowedExtNameList = Arrays.asList(new String[]{"jpg", "gif", "png", "bmp", "rle", "dib", "tif", "tiff"});
        String extensionName = Objects.requireNonNull(origFilename).substring(origFilename.lastIndexOf(".") + 1);

        if(!allowedExtNameList.contains( extensionName.toLowerCase())) {
            throw new IllegalArgumentException("File Extension Not allowed. Only allow Photo File.");
        }

    }

}