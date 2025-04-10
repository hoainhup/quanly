package com.example.quanlybenhvien.Controller.BacSi;

import com.example.quanlybenhvien.Dao.BacSiDao;
import com.example.quanlybenhvien.Dao.BenhAnDao;
import com.example.quanlybenhvien.Entity.BacSi;
import com.example.quanlybenhvien.Entity.BenhAn;
import com.example.quanlybenhvien.Entity.BenhNhan;
import com.example.quanlybenhvien.Service.BacSiService;
import com.example.quanlybenhvien.Service.BenhAnService;
import com.example.quanlybenhvien.Service.BenhNhanService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/bacsi/benhan")
public class BenhAnController {
    @Autowired
    private  BenhAnDao benhAnDao;

    @Autowired
    private BenhAnService benhAnService;

    @Autowired
    private BacSiService bacSiService;

    @Autowired
    private BacSiDao bacSiDao;

    @Autowired
    private BenhNhanService benhNhanService;

    

  

    // Hiển thị danh sách bệnh án và form thêm/sửa
    @GetMapping("")
    public String listBenhAnByMaBenhNhan(@RequestParam(value = "maBenhNhan", required = false) Integer maBenhNhan, Model model) {
        List<BenhAn> benhAns;
        if (maBenhNhan != null) {
            benhAns = benhAnService.getBenhAnByMaBenhNhan(maBenhNhan);
            model.addAttribute("maBenhNhan", maBenhNhan);
        } else {
            benhAns = benhAnService.getAllBenhAn(); // Lấy tất cả bệnh án nếu không có mã bệnh nhân
        }
        model.addAttribute("benhAns", benhAns);
        model.addAttribute("benhAn", new BenhAn());
        List<BacSi> dsBacSi = bacSiService.getAllBacSi();
        model.addAttribute("dsBacSi", dsBacSi); 
        List<BenhNhan> dsBenhNhan = benhNhanService.getAllBenhNhans();
        model.addAttribute("dsBenhNhan", dsBenhNhan);
        return "bacsi/benhan"; // Hiển thị danh sách bệnh án trong giao diện
    }

    // findById
    @GetMapping("/edit/{id}")
public String getBenhAnById(@PathVariable("id") Integer id, Model model) {
    // Lấy bệnh án theo ID
    Optional<BenhAn> benhAnOptional = benhAnService.getBenhAnById(id);
    if (benhAnOptional.isPresent()) {
        model.addAttribute("benhAn", benhAnOptional.get());
    } else {
        model.addAttribute("error", "Không tìm thấy bệnh án với ID: " + id);
        // Truyền danh sách bác sĩ và bệnh nhân để hiển thị dropdown
        model.addAttribute("dsBacSi", bacSiService.getAllBacSi());
        model.addAttribute("dsBenhNhan", benhNhanService.getAllBenhNhans());
        return "bacsi/benhan"; // Trả về trang danh sách bệnh án
    }

    // Truyền danh sách bác sĩ và bệnh nhân để hiển thị dropdown
    model.addAttribute("dsBacSi", bacSiService.getAllBacSi());
    model.addAttribute("dsBenhNhan", benhNhanService.getAllBenhNhans());

    return "bacsi/benhan"; // Trả về trang sửa bệnh án
}
    


    // Xử lý thêm hoặc cập nhật bệnh án
    @PostMapping("/save")
    public String saveBenhAn(@ModelAttribute BenhAn benhAn) {
        if (benhAn.getMaBenhAn() != null && benhAnDao.existsById(benhAn.getMaBenhAn())) {
            // Cập nhật bệnh án
            BenhAn existingBenhAn = benhAnDao.findById(benhAn.getMaBenhAn()).orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh án."));
            existingBenhAn.setTenBenhAn(benhAn.getTenBenhAn());
            existingBenhAn.setNgayKham(benhAn.getNgayKham());
            existingBenhAn.setTrieuChung(benhAn.getTrieuChung());
            existingBenhAn.setDieuTri(benhAn.getDieuTri());
            existingBenhAn.setGhiChu(benhAn.getGhiChu());
            benhAnDao.save(existingBenhAn);
        } else {
            // Thêm mới bệnh án
            benhAnDao.save(benhAn);
        }
        benhAnDao.save(benhAn);
    return "redirect:/bacsi/benhan";
}
    

    // Xóa bệnh án
    @GetMapping("/delete/{id}")
public String deleteBenhAn(@PathVariable("id") Integer id) {
    if (id == null) {
        throw new RuntimeException("ID không được để trống.");
    }
    if (benhAnService.existsById(id)) {
        benhAnService.deleteBenhAn(id);
    } else {
        throw new RuntimeException("Không tìm thấy bệnh án với ID: " + id);
    }
    return "redirect:/bacsi/benhan";
}
}