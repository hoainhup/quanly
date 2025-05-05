package com.example.quanlybenhvien.Controller.NhanVien;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.quanlybenhvien.Dao.DonThuocDao;
import com.example.quanlybenhvien.Entity.DonThuoc;
import com.example.quanlybenhvien.Entity.HoaDonChiTietDonThuoc;
import com.example.quanlybenhvien.Entity.HoaDonDonThuoc;
import com.example.quanlybenhvien.Entity.BenhNhan; // Đảm bảo đã import đúng lớp BenhNhan
import com.example.quanlybenhvien.Service.DonThuocService;
import com.example.quanlybenhvien.Service.HoaDonDonThuocService;

@Controller
@RequestMapping("/nhanvien/trangchu/danhsach-thuoc")
public class DanhSachDonThuocController {

    @Autowired
    private DonThuocDao donThuocDao;

    @Autowired
    private DonThuocService donThuocService;

    @Autowired
    private HoaDonDonThuocService HoaDonDonThuocService;

    @GetMapping()
    public String hienThiDanhSach(Model model) {
        Map<Integer, Boolean> daThanhToan = new HashMap<>();
        List<DonThuoc> donThuocs = donThuocDao.findAllByOrderByNgayLapDesc();
        for (DonThuoc don : donThuocs) {
            HoaDonDonThuoc hoaDon = HoaDonDonThuocService.getHoaDonByDonThuocId(don.getMaDonThuoc());
            boolean isPaid = hoaDon != null && "Đã thanh toán".equalsIgnoreCase(hoaDon.getTrangThai());
            daThanhToan.put(don.getMaDonThuoc(), isPaid);
        }

        model.addAttribute("daThanhToan", daThanhToan);
        model.addAttribute("donThuocList", donThuocs);
        return "nhanvien/donthuoc";
    }

    @GetMapping("/chitiet/{id}")
    public String chiTietDonThuoc(@PathVariable("id") Integer id, Model model) {
        Optional<DonThuoc> donThuoc = donThuocDao.findById(id);
        if (donThuoc.isPresent()) {
            model.addAttribute("donThuoc", donThuoc.get());
            return "nhanvien/donthuoc_chitiet";
        } else {
            return "redirect:/nhanvien/trangchu/danhsach-thuoc";
        }
    }

    @GetMapping("/thanh-toan/{maDonThuoc}")
    public String thanhToan(@PathVariable("maDonThuoc") Integer maDonThuoc, Model model) {
        DonThuoc donThuoc = donThuocService.findById(maDonThuoc)
                .orElse(null); // hoặc orElseThrow(...) nếu muốn báo lỗi
        if (donThuoc == null) {
            return "error/404"; // hoặc trả về trang báo lỗi riêng
        }
        model.addAttribute("donThuoc", donThuoc);
        return "nhanvien/thanh-toan";
    }

    // Xử lý thanh toán và lưu hóa đơn
    @PostMapping("/thanh-toan/{maDonThuoc}")
    public String xuLyThanhToan(@PathVariable("maDonThuoc") Integer maDonThuoc,
            @RequestParam("hinhThuc") String hinhThuc) {

        // Kiểm tra xem giá trị "hinhThuc" có được truyền vào từ form không
        if (hinhThuc == null || hinhThuc.isEmpty()) {
            return "error"; // Nếu không có giá trị "hinhThuc", trả về lỗi
        }

        // Lấy thông tin đơn thuốc
        DonThuoc donThuoc = donThuocService.findById(maDonThuoc)
                .orElse(null); // hoặc orElseThrow(...) nếu muốn báo lỗi
        if (donThuoc == null) {
            return "error/404"; // hoặc trả về trang báo lỗi riêng
        }

        // Tạo hóa đơn và lưu vào bảng HoaDonDonThuoc
        HoaDonDonThuoc hoaDonDonThuoc = new HoaDonDonThuoc();
        hoaDonDonThuoc.setDonThuoc(donThuoc);
        hoaDonDonThuoc.setNgayThanhToan(LocalDateTime.now().toLocalDate());
        hoaDonDonThuoc.setHinhThuc(hinhThuc); // Lấy giá trị "hinhThuc" từ form
        hoaDonDonThuoc.setTrangThai("Đã thanh toán");
        hoaDonDonThuoc.setTongTien(BigDecimal.valueOf(500000)); // Giá trị tổng tiền

        // Lưu hóa đơn vào cơ sở dữ liệu
        HoaDonDonThuocService.saveHoaDonDonThuoc(hoaDonDonThuoc);

        // Chuyển hướng đến trang thanh toán thành công với mã đơn thuốc
        return "redirect:/nhanvien/trangchu/danhsach-thuoc/thanh-toan-thanh-cong/" + maDonThuoc;
    }

    @GetMapping("/thanh-toan-thanh-cong/{maDonThuoc}")
    public String thanhToanThanhCong(@PathVariable("maDonThuoc") Integer maDonThuoc, Model model) {
        DonThuoc donThuoc = donThuocService.findById(maDonThuoc)
                .orElse(null); // hoặc orElseThrow(...) nếu muốn báo lỗi
        if (donThuoc == null) {
            return "error/404"; // hoặc trả về trang báo lỗi riêng
        }

        // Tìm hóa đơn theo mã đơn thuốc
        HoaDonDonThuoc hoaDon = HoaDonDonThuocService.getHoaDonByDonThuocId(maDonThuoc);

        model.addAttribute("donThuoc", donThuoc);
        model.addAttribute("hoaDon", hoaDon);

        return "nhanvien/thanh-toan-thanh-cong";
    }

}