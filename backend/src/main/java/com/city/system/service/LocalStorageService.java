package com.city.system.service;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.util.UUID;
@Service @ConditionalOnProperty(name="app.storage-type",havingValue="local",matchIfMissing=true)
public class LocalStorageService implements StorageService {
  @Value("${app.upload-dir:/uploads}") private String uploadDir;
  @PostConstruct void init(){ try{Files.createDirectories(Paths.get(uploadDir));}catch(Exception e){throw new RuntimeException(e);} }
  public String store(MultipartFile file){
    validate(file);
    try{
      String ext=org.springframework.util.StringUtils.getFilenameExtension(file.getOriginalFilename());
      String name=UUID.randomUUID()+"."+ext;
      Files.copy(file.getInputStream(),Paths.get(uploadDir,name),StandardCopyOption.REPLACE_EXISTING);
      return "/uploads/"+name;
    }catch(Exception e){throw new RuntimeException("Could not store uploaded image",e);}
  }
  static void validate(MultipartFile file){
    if(file==null||file.isEmpty()) return;
    if(file.getSize()>5*1024*1024) throw new IllegalArgumentException("Image must not exceed 5 MB");
    String type=file.getContentType();
    if(type==null||!(type.equals("image/jpeg")||type.equals("image/png")||type.equals("image/webp"))) throw new IllegalArgumentException("Only JPEG, PNG and WEBP images are allowed");
  }
}
