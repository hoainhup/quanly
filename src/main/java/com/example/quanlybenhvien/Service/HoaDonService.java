package com.example.quanlybenhvien.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quanlybenhvien.Dao.HoaDonDao;
import com.example.quanlybenhvien.Entity.HoaDonLichKham;

@Service
public class HoaDonService {
    @Autowired
    private HoaDonDao hoadDonDao;

    // Phương thức để tìm hóa đơn theo mã hóa đơn
    public Optional<HoaDonLichKham> findById(Integer maHoaDon) {
        return hoadDonDao.findById(maHoaDon);
    }

    public List<HoaDonLichKham> findAll() {
        return hoadDonDao.findAll(); // Giả sử bạn đã có phương thức này trong HoadDonDao
    }

    // Thống kê hóa đơn theo ngày
    public List<HoaDonLichKham> getHoaDonByNgay(LocalDate ngay) {
        return hoadDonDao.findByNgayThanhToan(ngay);
    }

    // Thống kê hóa đơn theo tháng
    public List<HoaDonLichKham> getHoaDonByThang(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return hoadDonDao.findByNgayThanhToanBetween(startDate, endDate);
    }

    // Phương thức để tính tổng tiền cho hóa đơn (nếu bạn có thông tin dịch vụ)
    public Double tinhTongTien(HoaDonLichKham hoaDonLichKham) {
        return hoaDonLichKham.getTongTien();
    }

    public void hoaDonLichKham(HoaDonLichKham hoaDonLichKham) {
        hoadDonDao.save(hoaDonLichKham);
    }

    public Optional<HoaDonLichKham> findByMaLichKham(Integer maLichKham) {
        return hoadDonDao.findByLichKham_MaLichKham(maLichKham);
    }

}
