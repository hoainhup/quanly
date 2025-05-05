package com.example.quanlybenhvien.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quanlybenhvien.Dao.ChuyenKhoaDao;
import com.example.quanlybenhvien.Dao.TrieuChungDao;
import com.example.quanlybenhvien.Entity.TrieuChung;

@Service
public class TrieuChungService {
    @Autowired
    private TrieuChungDao trieuChungRepository;

    @Autowired
    private ChuyenKhoaDao chuyenKhoaRepository;

    public String getSpecializationBySymptom(String userQuery) {
        // Tìm triệu chứng trong cơ sở dữ liệu
        List<TrieuChung> trieuChungs = trieuChungRepository.findByTenTrieuChungContainingIgnoreCase(userQuery);

        if (trieuChungs.isEmpty()) {
            return "Xin lỗi, không thể xác định chuyên khoa cho triệu chứng này.";
        } else {
            // Lấy danh sách các chuyên khoa tương ứng
            Set<String> specializations = new HashSet<>();
            for (TrieuChung trieuChung : trieuChungs) {
                specializations.add(trieuChung.getChuyenKhoa().getTenChuyenKhoa());
            }

            return "Chuyên khoa bạn nên khám là: " + String.join(", ", specializations);
        }
    }
}
