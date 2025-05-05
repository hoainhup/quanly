package com.example.quanlybenhvien.Dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.quanlybenhvien.Entity.HoaDonDonThuoc;

@Repository
public interface HoaDonDonThuocDao extends JpaRepository<HoaDonDonThuoc, Integer> {
    HoaDonDonThuoc findByDonThuoc_MaDonThuoc(Integer maDonThuoc);

    List<HoaDonDonThuoc> findByNgayThanhToanBetween(LocalDate startDate, LocalDate endDate);

    // @Query("SELECT h.hinhThuc, COUNT(h) FROM HoaDonDonThuoc h GROUP BY
    // h.hinhThuc")
    // List<Object[]> countByHinhThuc();
}
