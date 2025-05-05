package com.example.quanlybenhvien.Dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quanlybenhvien.Entity.BacSi;
import com.example.quanlybenhvien.Entity.BenhNhan;
import com.example.quanlybenhvien.Entity.ChuyenKhoa;
import com.example.quanlybenhvien.Entity.LichKham;

@Repository
public interface LichKhamDao extends JpaRepository<LichKham, Integer> {
    List<LichKham> findByTrangThai(String trangThai);

    List<LichKham> findByBenhNhan(BenhNhan benhNhan);

    List<LichKham> findByBacSiAndTrangThai(BacSi bacSi, String trangThai);

    List<LichKham> findByNgayKhamAndChuyenKhoa(LocalDate ngayKham, ChuyenKhoa chuyenKhoa);
}
