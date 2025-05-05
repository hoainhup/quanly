package com.example.quanlybenhvien.Controller.NhanVien;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quanlybenhvien.Entity.HoaDonLichKham;
import com.example.quanlybenhvien.Entity.LichKham;
import com.example.quanlybenhvien.Entity.QrCodeUtil;
import com.example.quanlybenhvien.Service.HoaDonService;
import com.example.quanlybenhvien.Service.LichKhamService;

import java.net.URLEncoder;
import java.time.LocalDate;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/nhanvien/trangchu/thanhtoan")
public class ThanhToanLichKhamController {
    @Autowired
    LichKhamService lichKhamService;
    @Autowired
    HoaDonService hoaDonService;

    @GetMapping
    public String thanhToan(@RequestParam("id") int maLichKham, Model model) {
        Optional<LichKham> lichKhamOptional = lichKhamService.findById(maLichKham);

        if (lichKhamOptional.isPresent()) {
            LichKham lichKham = lichKhamOptional.get();

            // 👉 Tính tổng tiền
            double tongTien = 0.0;
            if (lichKham.getChiTietDichVus() != null) {
                tongTien = lichKham.getChiTietDichVus().stream()
                        .mapToDouble(ct -> ct.getDichVu().getGia() * ct.getSoLuong())
                        .sum();
            }

            model.addAttribute("lich", lichKham);
            model.addAttribute("maLichKham", lichKham.getMaLichKham());
            model.addAttribute("ngayKham", lichKham.getNgayKham());
            model.addAttribute("gioKham", lichKham.getGioKham());
            model.addAttribute("dichVukham", lichKham.getChiTietDichVus());
            model.addAttribute("tenBenhNhan", lichKham.getBenhNhan().getHoTen());
            model.addAttribute("nhanVien", lichKham.getNhanVien().getHoTen());
            model.addAttribute("bacSi", lichKham.getBacSi().getHoTen());
            model.addAttribute("tongTien", tongTien);

            return "nhanvien/thanhtoan";
        } else {
            model.addAttribute("errorMessage", "Lịch khám không tồn tại.");
            return "nhanvien/lich-kham-da-hoan-thanh";
        }
    }

    @GetMapping("/hoadon")

    public String hienThiDanhSachHoaDon(Model model) {

        List<HoaDonLichKham> hoaDonList = hoaDonService.findAll();

        model.addAttribute("hoadonList", hoaDonList);

        return "nhanvien/hoadon";
    }

    @PostMapping("/xacnhanthanhtoan")
    public String hienThiQr(HoaDonLichKham hoaDonLichKham, Model model) {
        if ("Chuyển khoản".equals(hoaDonLichKham.getHinhThuc())) {
            // Chỉ sinh QR, chưa lưu database
            String payload = "ACC:012345678;AMT:" + hoaDonLichKham.getTongTien();
            try {
                String qrBase64 = QrCodeUtil.generateQrBase64(payload, 300, 300);
                model.addAttribute("qrBase64", qrBase64);
            } catch (Exception e) {
                model.addAttribute("qrError", "Không sinh được QR");
            }

            // Bổ sung trạng thái mặc định để tránh lỗi not-null
            hoaDonLichKham.setTrangThai("Đang chờ thanh toán");

            // Truyền hoaDon sang trang QR để hiển thị thông tin
            model.addAttribute("hoaDon", hoaDonLichKham);
            return "nhanvien/qr";
        }

        // Nếu là tiền mặt, xử lý thanh toán luôn
        return hoanTatThanhToan(hoaDonLichKham, model);
    }

    @PostMapping("/xacnhanthanhtoan/hoantat")
    public String hoanTatThanhToan(HoaDonLichKham hoaDonLichKham, Model model) {
        if (hoaDonLichKham.getLichKham() == null)
            return "error";

        // Đặt trạng thái trước khi lưu để tránh lỗi not-null
        hoaDonLichKham.setTrangThai("Đã thanh toán");

        hoaDonService.hoaDonLichKham(hoaDonLichKham);

        Optional<LichKham> optionalLichKham = lichKhamService.findById(hoaDonLichKham.getLichKham().getMaLichKham());
        if (optionalLichKham.isPresent()) {
            LichKham lichKham = optionalLichKham.get();
            lichKham.setDaThanhToan(true);
            lichKhamService.save(lichKham);
        } else {
            return "error";
        }

        return "redirect:/nhanvien/trangchu/thanhtoan/hoadon";
    }

}