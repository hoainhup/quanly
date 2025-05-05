package com.example.quanlybenhvien.Controller.QuanLy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.quanlybenhvien.Entity.LichKham;
import com.example.quanlybenhvien.Service.LichKhamService;

@Controller
@RequestMapping("/quanly/trangchu/thongke/lichkham")
public class ThongKeLichKhamController {

    @Autowired
    private LichKhamService lichKhamService;

    @GetMapping()
    public String getLichKhamStatistics(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            Model model) {

        List<LichKham> lichKhamList = lichKhamService.getAllLichKhams();

        // Lọc theo ngày
        if (startDate != null && endDate != null) {
            lichKhamList = lichKhamList.stream()
                    .filter(lk -> {
                        LocalDate ngay = lk.getNgayKham();
                        return (ngay != null && !ngay.isBefore(startDate) && !ngay.isAfter(endDate));
                    })
                    .collect(Collectors.toList());
        }

        lichKhamList.sort(Comparator.comparing(LichKham::getNgayKham).reversed());
        model.addAttribute("lichKhamList", lichKhamList);
        model.addAttribute("totalAppointments", lichKhamList.size());

        // Dữ liệu thống kê biểu đồ
        List<List<Object>> appointmentChartData = getChartDataByDate(lichKhamList);
        model.addAttribute("appointmentChartData", appointmentChartData);

        List<List<Object>> doctorChartData = getChartDataByDoctor(lichKhamList);
        model.addAttribute("doctorChartData", doctorChartData);

        // Thống kê theo trạng thái
        Map<String, Long> statusSummary = lichKhamList.stream()
                .collect(Collectors.groupingBy(LichKham::getTrangThai, Collectors.counting()));
        model.addAttribute("statusSummary", statusSummary);

        return "admin/thongkelichkham";
    }

    private List<List<Object>> getChartDataByDate(List<LichKham> lichKhamList) {
        Map<String, Long> dataMap = lichKhamList.stream()
                .collect(Collectors.groupingBy(
                        lk -> lk.getNgayKham().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        TreeMap::new,
                        Collectors.counting()));

        List<List<Object>> chartData = new ArrayList<>();
        dataMap.forEach((date, count) -> {
            chartData.add(Arrays.asList(date, count));
        });

        return chartData;
    }

    private List<List<Object>> getChartDataByDoctor(List<LichKham> lichKhamList) {
        Map<String, Long> dataMap = lichKhamList.stream()
                .collect(Collectors.groupingBy(
                        lk -> lk.getBacSi().getHoTen(), // Giả sử LichKham có phương thức getBacSi() trả về đối tượng
                                                        // Bác sĩ với phương thức getTen()
                        TreeMap::new,
                        Collectors.counting()));

        List<List<Object>> chartData = new ArrayList<>();
        dataMap.forEach((doctor, count) -> {
            chartData.add(Arrays.asList(doctor, count));
        });

        return chartData;
    }

}
