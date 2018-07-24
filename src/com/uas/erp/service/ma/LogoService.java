package com.uas.erp.service.ma;

import org.springframework.web.multipart.MultipartFile;

public interface LogoService {
  public void saveLogo(MultipartFile file);
  public boolean hasLogo();
  public byte[] getLogo();
  public void del();
}
