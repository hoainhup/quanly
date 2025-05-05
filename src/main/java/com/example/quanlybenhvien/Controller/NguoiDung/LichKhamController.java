package com.example.quanlybenhvien.Controller.NguoiDung;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quanlybenhvien.Entity.BacSi;
import com.example.quanlybenhvien.Entity.BenhNhan;
import com.example.quanlybenhvien.Entity.ChuyenKhoa;
import com.example.quanlybenhvien.Entity.HoaDonLichKham;
import com.example.quanlybenhvien.Entity.LichKham;
import com.example.quanlybenhvien.Service.BacSiService;
import com.example.quanlybenhvien.Service.BenhNhanService;
import com.example.quanlybenhvien.Service.ChuyenKhoaService;
import com.example.quanlybenhvien.Service.HoaDonService;
import com.example.quanlybenhvien.Service.LichKhamService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/nguoidung/lichkham")
public class LichKhamController {

    @Autowired
    private LichKhamService lichKhamService;

    @Autowired
    private ChuyenKhoaService chuyenKhoaService;

    @Autowired
    private BacSiService bacSiService;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    BenhNhanService benhNhanService;

    @GetMapping
    public String lichkham(@RequestParam(value = "maChuyenKhoa", required = false) String maChuyenKhoa,
            HttpSession session, Model model) {
        List<ChuyenKhoa> listChuyenKhoa = chuyenKhoaService.getAllChuyenKhoa();
        List<BacSi> listBacSi;

        // Lấy user từ SecurityContext hoặc Session
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        BenhNhan benhNhan = null;

        if (auth.getPrincipal() instanceof BenhNhan) {
            benhNhan = (BenhNhan) auth.getPrincipal();
        } else if (auth.getPrincipal() instanceof DefaultOAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");
            benhNhan = benhNhanService.findByEmail(email);
        }
        session.setAttribute("loggedInUser", benhNhan);
        model.addAttribute("user", benhNhan);

        if (maChuyenKhoa != null && !maChuyenKhoa.isEmpty()) {
            listBacSi = bacSiService.getBacSiByChuyenKhoa(maChuyenKhoa);
        } else {
            listBacSi = bacSiService.getAllBacSi();
        }

        model.addAttribute("dsChuyenKhoa", listChuyenKhoa);
        model.addAttribute("dsBacSi", listBacSi);
        model.addAttribute("selectedChuyenKhoa", maChuyenKhoa);
        model.addAttribute("lichKham", new LichKham());

        return "lichkhamND";
    }

    // Xử lý khi người dùng đặt lịch
    @PostMapping
    public String datLich(@ModelAttribute("lichKham") LichKham lichKham,
            @RequestParam("maChuyenKhoa") String maChuyenKhoa,
            RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login";
        }

        Object principal = auth.getPrincipal();

        // Lấy chuyên khoa từ database trước khi set
        Optional<ChuyenKhoa> chuyenKhoa = chuyenKhoaService.findById(maChuyenKhoa);
        if (chuyenKhoa.isPresent()) {
            lichKham.setChuyenKhoa(chuyenKhoa.get()); // Gán chuyên khoa hợp lệ vào lịch khám
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Chuyên khoa không hợp lệ!");
            return "redirect:/nguoidung/lichkham";
        }

        // Kiểm tra giờ đặt lịch
        LocalTime startTime = LocalTime.of(7, 30); // 7h30
        LocalTime endTime = LocalTime.of(16, 0); // 16h00
        LocalTime appointmentTime = lichKham.getGioKham(); // Lấy thời gian đặt lịch từ LichKham

        if (appointmentTime.isBefore(startTime) || appointmentTime.isAfter(endTime)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Giờ đặt lịch phải trong khoảng từ 7h30 đến 16h00!");
            return "redirect:/nguoidung/lichkham";
        }

        // Kiểm tra ngày đặt lịch (ví dụ, chỉ cho phép đặt trong các ngày làm việc)
        LocalDate appointmentDate = lichKham.getNgayKham(); // Lấy ngày đặt lịch
        LocalDate today = LocalDate.now();

        if (appointmentDate.isBefore(today)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ngày đặt lịch không hợp lệ!");
            return "redirect:/nguoidung/lichkham";
        }

        // Kiểm tra nếu đã có lịch khám tại giờ đã chọn trong ngày đặt lịch
        List<LichKham> existingAppointments = lichKhamService.findByNgayKhamAndChuyenKhoa(appointmentDate,
                chuyenKhoa.get());
        for (LichKham existingAppointment : existingAppointments) {
            if (existingAppointment.getGioKham().equals(appointmentTime)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Giờ này đã có người đặt lịch rồi, vui lòng chọn giờ khác.");
                return "redirect:/nguoidung/lichkham";
            }
        }

        // Kiểm tra nếu người dùng đăng nhập bằng tài khoản thông thường
        if (principal instanceof BenhNhan user) {
            lichKham.setBenhNhan(user);
            lichKham.setTrangThai("Chờ xác nhận");
            lichKhamService.save(lichKham);

            redirectAttributes.addFlashAttribute("successMessage", "Đặt lịch thành công! Chúng tôi sẽ sớm xác nhận.");
            return "redirect:/nguoidung/lichkham?success";
        }

        // Kiểm tra nếu người dùng đăng nhập bằng OAuth2 (Google, Facebook, ...)
        if (principal instanceof DefaultOAuth2User oauth2User) {
            String email = oauth2User.getAttribute("email");
            BenhNhan benhNhan = benhNhanService.findByEmail(email);

            if (benhNhan != null) {
                lichKham.setBenhNhan(benhNhan);
                lichKham.setTrangThai("Chờ xác nhận");
                lichKhamService.save(lichKham);

                redirectAttributes.addFlashAttribute("successMessage",
                        "Đặt lịch thành công! Chúng tôi sẽ sớm xác nhận.");
                return "redirect:/nguoidung/lichkham?success";
            }
        }

        return "redirect:/login";
    }

    @GetMapping("/lichsu")
    public String lichSuLichKham(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login";
        }

        Object principal = auth.getPrincipal();
        BenhNhan benhNhan = null;

        if (principal instanceof BenhNhan) {
            benhNhan = (BenhNhan) principal;
        } else if (principal instanceof DefaultOAuth2User) {
            String email = ((DefaultOAuth2User) principal).getAttribute("email");
            benhNhan = benhNhanService.findByEmail(email);
        }

        if (benhNhan != null) {
            List<LichKham> lichSu = lichKhamService.findByBenhNhan(benhNhan);
            model.addAttribute("lichSu", lichSu);
            model.addAttribute("benhNhan", benhNhan); // <-- Gửi thêm thông tin bệnh nhân ra view
        }

        return "lichsudatlich";
    }

    @GetMapping("/huylichkham")
    public String huyLichKham(@RequestParam("maLichKham") Integer maLichKham,
            RedirectAttributes redirectAttributes) {
        Optional<LichKham> optionalLichKham = lichKhamService.findById(maLichKham);

        if (optionalLichKham.isPresent()) {
            LichKham lich = optionalLichKham.get();

            if (!"Chờ xác nhận".equalsIgnoreCase(lich.getTrangThai())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Lịch khám đã được xác nhận, không thể hủy.");
            } else {
                lich.setTrangThai("Đã hủy");
                lichKhamService.save(lich);
                redirectAttributes.addFlashAttribute("successMessage", "Hủy lịch khám thành công.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy lịch khám.");
        }

        return "redirect:/nguoidung/lichkham/lichsu";

    }

    @GetMapping("/xemhoadon")
    public String xemHoaDon(@RequestParam("maLichKham") Integer maLichKham, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<HoaDonLichKham> optionalHoaDon = hoaDonService.findByMaLichKham(maLichKham);

        if (optionalHoaDon.isPresent()) {
            model.addAttribute("hoaDon", optionalHoaDon.get());
            return "lichsuhoadon";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy hóa đơn cho lịch khám này.");
            return "redirect:/nguoidung/lichkham/lichsu";
        }
    }
}
