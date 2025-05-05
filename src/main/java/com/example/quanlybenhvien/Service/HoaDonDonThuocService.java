package com.example.quanlybenhvien.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quanlybenhvien.Dao.HoaDonDonThuocDao;
import com.example.quanlybenhvien.Dao.ThuocDao;
import com.example.quanlybenhvien.Entity.ChiTietDonThuoc;
import com.example.quanlybenhvien.Entity.DonThuoc;
import com.example.quanlybenhvien.Entity.HoaDonDonThuoc;
import com.example.quanlybenhvien.Entity.Thuoc;

@Service
public class HoaDonDonThuocService {
    @Autowired
    private HoaDonDonThuocDao hoaDonDonThuocDao;
    @Autowired
    private ThuocDao thuocRepository;

    public HoaDonDonThuoc getHoaDonByDonThuocId(Integer maDonThuoc) {
        return hoaDonDonThuocDao.findByDonThuoc_MaDonThuoc(maDonThuoc);

    }

    public void saveHoaDonDonThuoc(HoaDonDonThuoc hoaDonDonThuoc) {
        // Tính tổng tiền cho hóa đơn
        BigDecimal tongTien = tinhTongTien(hoaDonDonThuoc.getDonThuoc());

        // Cập nhật tổng tiền vào hóa đơn
        hoaDonDonThuoc.setTongTien(tongTien);

        // Lưu hóa đơn vào cơ sở dữ liệu
        hoaDonDonThuocDao.save(hoaDonDonThuoc);
    }

    public BigDecimal tinhTongTien(DonThuoc donThuoc) {
        BigDecimal tongTien = BigDecimal.ZERO;

        // Lấy danh sách chi tiết đơn thuốc
        List<ChiTietDonThuoc> chiTietDonThuocs = donThuoc.getChiTietDonThuocs();

        // Tính tổng tiền từ các chi tiết đơn thuốc
        for (ChiTietDonThuoc chiTiet : chiTietDonThuocs) {
            // Lấy thông tin thuốc
            Thuoc thuoc = thuocRepository.findById(chiTiet.getMaThuoc())
                    .orElseThrow(() -> new RuntimeException("Thuốc không tồn tại"));

            // Tính giá trị của mỗi thuốc trong đơn thuốc (số lượng * giá)
            BigDecimal giaTien = thuoc.getGiaThuoc();
            BigDecimal tongTienThuoc = giaTien.multiply(BigDecimal.valueOf(chiTiet.getSoLuong()));

            // Cộng dồn tổng tiền
            tongTien = tongTien.add(tongTienThuoc);
        }

        return tongTien;
    }

    public HoaDonDonThuoc findbyID(Integer MaHD) {
        return hoaDonDonThuocDao.findById(MaHD).orElse(null);
    }

    // Lấy tất cả hóa đơn
    public List<HoaDonDonThuoc> getAllHoaDonDonThuoc() {
        return hoaDonDonThuocDao.findAll();
    }

    public List<HoaDonDonThuoc> getHoaDonDonThuocByDateRange(LocalDate startDate, LocalDate endDate) {
        return hoaDonDonThuocDao.findByNgayThanhToanBetween(startDate, endDate);
    }

    // Tính tổng tiền của tất cả hóa đơn
    public BigDecimal getTotalAmount() {
        return hoaDonDonThuocDao.findAll()
                .stream()
                .map(HoaDonDonThuoc::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalAmountByDate(LocalDate startDate, LocalDate endDate) {
        List<HoaDonDonThuoc> hoaDons = getHoaDonDonThuocByDateRange(startDate, endDate);
        return hoaDons.stream()
                .map(HoaDonDonThuoc::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
