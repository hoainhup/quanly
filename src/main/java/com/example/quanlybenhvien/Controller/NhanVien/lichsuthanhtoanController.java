package com.example.quanlybenhvien.Controller.NhanVien;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.quanlybenhvien.Entity.HoaDonLichKham;
import com.example.quanlybenhvien.Service.HoaDonService;

@Controller
@RequestMapping("/nguoidung/hoadon/lichsuhoadon")
public class lichsuthanhtoanController {

    @Autowired
    private HoaDonService hoaDonService;

    @GetMapping
    public String lichsuThanhToan(Model model) {
        List<HoaDonLichKham> hoaDonList = hoaDonService.findAll();
        model.addAttribute("hoadonList", hoaDonList);
        return "lichsuhoadon";
    }
}