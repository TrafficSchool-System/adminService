package com.example.adminService.Service;

import com.example.adminService.Dto.AdminLoginRequest;
import com.example.adminService.Dto.AdminLoginResponse;
import com.example.adminService.Dto.AdminResponseDTO;

public interface AdminServiceInterface {
    
    AdminLoginResponse login(AdminLoginRequest request);
   
    AdminResponseDTO getAdminById(Long adminId);
    
    String validateTokenAndGetRole(String token);
}