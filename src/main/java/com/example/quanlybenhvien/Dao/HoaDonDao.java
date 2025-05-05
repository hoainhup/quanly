package com.example.quanlybenhvien.Dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quanlybenhvien.Entity.HoaDonLichKham;

@Repository
public interface HoaDonDao extends JpaRepository<HoaDonLichKham, Integer> {
        // Thống kê hóa đơn theo ngày
        List<HoaDonLichKham> findByNgayThanhToan(LocalDate ngayThanhToan);

        // Thống kê hóa đơn theo tháng (chỉ lấy năm và tháng từ ngày thanh toán)
        List<HoaDonLichKham> findByNgayThanhToanBetween(LocalDate startDate, LocalDate endDate);

        Optional<HoaDonLichKham> findByLichKham_MaLichKham(Integer maLichKham);
}
