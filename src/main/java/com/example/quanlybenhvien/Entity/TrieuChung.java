package com.example.quanlybenhvien.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "TRIEUCHUNG")
public class TrieuChung {
    @Id
    private Integer ma_trieu_chung;
    @Column(name = "ten_trieu_chung")
    private String tenTrieuChung;
    @ManyToOne
    @JoinColumn(name = "ma_chuyen_khoa")
    private ChuyenKhoa chuyenKhoa;
}
