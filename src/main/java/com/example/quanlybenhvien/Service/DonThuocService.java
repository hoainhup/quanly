package com.example.quanlybenhvien.Service;

import com.example.quanlybenhvien.Dao.DonThuocDao;
import com.example.quanlybenhvien.Entity.DonThuoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonThuocService {

    @Autowired
    private DonThuocDao donThuocDao;

    // Lưu đơn thuốc
    public void save(DonThuoc donThuoc) {
        donThuocDao.save(donThuoc);
    }

    // Tìm kiếm đơn thuốc theo bệnh án
    public List<DonThuoc> findByBenhAn(Integer maBenhAn) {
        return donThuocDao.findByBenhAn_MaBenhAn(maBenhAn);
    }

    // Lưu và trả về đơn thuốc đã lưu
    public DonThuoc saveAndReturn(DonThuoc donThuoc) {
        return donThuocDao.save(donThuoc);
    }

    // Sửa phương thức findById để trả về Optional<DonThuoc>
    public Optional<DonThuoc> findById(Integer maDonThuoc) {
        return donThuocDao.findById(maDonThuoc);
    }
}
