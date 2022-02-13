package tp.farming_springboot.domain.product.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class S3UploaderService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;


    public String putS3(MultipartFile multipartFile, String fileName) throws IOException {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), null).withCannedAcl(
                CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    public void deleteS3(String hashFileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, hashFileName));
    }


}
