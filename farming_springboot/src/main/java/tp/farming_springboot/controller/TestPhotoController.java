package tp.farming_springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tp.farming_springboot.domain.product.dto.PhotoFileDto;
import tp.farming_springboot.domain.product.dto.ProductCreateDto;
import tp.farming_springboot.domain.product.model.PhotoFile;
import tp.farming_springboot.domain.product.model.Product;
import tp.farming_springboot.domain.product.repository.ProductRepository;
import tp.farming_springboot.domain.product.service.FileService;
import tp.farming_springboot.domain.product.util.MD5Generator;
import tp.farming_springboot.domain.user.model.User;

import java.io.File;
import java.security.Principal;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(value="/photo")
public class TestPhotoController {

    @Autowired
    private FileService fileService;
    @Autowired
    private ProductRepository prodRepo;

    public TestPhotoController(FileService fileService, ProductRepository prodRepo) {
        this.fileService = fileService;
        this.prodRepo = prodRepo;
    }


    @PostMapping
    @ResponseBody
    public PhotoFile create(@RequestPart ProductCreateDto prodDto, @RequestPart(value="PhotoFile")  MultipartFile files) {

        PhotoFile photofile = null;
        try {
            String origFilename = files.getOriginalFilename();
            String filename = new MD5Generator(origFilename).toString();

            String savePath = System.getProperty("user.dir") + "/files";
            if (!new File(savePath).exists()) {
                try{
                    new File(savePath).mkdir();
                }
                catch(Exception e){
                    e.getStackTrace();
                }
            }
            String filePath = savePath + "/" + filename;
            files.transferTo(new File(filePath));

            PhotoFileDto fileDto = new PhotoFileDto();
            fileDto.setOrigFilename(origFilename);
            fileDto.setFilename(filename);
            fileDto.setFilePath(filePath);

            photofile = fileService.saveFile(fileDto);

        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println(prodDto.getTitle());
        return photofile;
    }

}
