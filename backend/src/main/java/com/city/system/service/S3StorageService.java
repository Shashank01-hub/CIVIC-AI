package com.city.system.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.util.UUID;
@Service @ConditionalOnProperty(name="app.storage-type",havingValue="s3")
public class S3StorageService implements StorageService {
  @Value("${app.s3-bucket}") private String bucket;
  @Value("${app.aws-region}") private String region;
  public String store(MultipartFile file){
    LocalStorageService.validate(file);
    try{
      String ext=org.springframework.util.StringUtils.getFilenameExtension(file.getOriginalFilename());
      String key="issues/"+UUID.randomUUID()+"."+ext;
      S3Client.builder().region(software.amazon.awssdk.regions.Region.of(region)).build().putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType(file.getContentType()).build(),RequestBody.fromBytes(file.getBytes()));
      return "https://"+bucket+".s3."+region+".amazonaws.com/"+key;
    }catch(Exception e){throw new RuntimeException("Could not store image in S3",e);}
  }
}
