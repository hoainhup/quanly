package com.example.quanlybenhvien.Entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "HOADON_DONTHUOC")
public class HoaDonDonThuoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_hoa_don")
    private Integer maHoaDon;

    @Column(name = "ngay_thanh_toan", nullable = false)
    private LocalDate ngayThanhToan;

    @Column(name = "hinh_thuc", nullable = false, length = 100)
    private String hinhThuc;

    @ManyToOne
    @JoinColumn(name = "ma_don_thuoc", nullable = false)
    private DonThuoc donThuoc;

    @Column(name = "trang_thai", nullable = false, length = 50)
    private String trangThai;

    @Column(name = "tong_tien", nullable = false, precision = 10, scale = 2)
    private BigDecimal tongTien;

}