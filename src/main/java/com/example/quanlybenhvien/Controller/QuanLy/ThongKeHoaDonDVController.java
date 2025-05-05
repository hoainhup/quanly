package com.example.quanlybenhvien.Controller.QuanLy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.quanlybenhvien.Entity.HoaDonDonThuoc;
import com.example.quanlybenhvien.Entity.HoaDonLichKham;
import com.example.quanlybenhvien.Service.HoaDonDonThuocService;
import com.example.quanlybenhvien.Service.HoaDonService;

@Controller
@RequestMapping("/quanly/trangchu/thongke")
public class ThongKeHoaDonDVController {

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private HoaDonDonThuocService hoaDonDonThuocService;

    @GetMapping("/hoadonDV")
    public String getHoaDonStatistic(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            Model model) {

        List<HoaDonLichKham> hoaDonList = hoaDonService.findAll();

        // Lọc theo khoảng thời gian nếu có
        if (startDate != null && endDate != null) {
            hoaDonList = hoaDonList.stream()
                    .filter(hd -> {
                        LocalDate ngay = hd.getNgayThanhToan();
                        return (ngay != null && !ngay.isBefore(startDate) && !ngay.isAfter(endDate));
                    })
                    .collect(Collectors.toList());
        }
        hoaDonList.sort((h1, h2) -> h2.getMaHoaDon().compareTo(h1.getMaHoaDon()));
        model.addAttribute("hoaDonList", hoaDonList);

        if (!hoaDonList.isEmpty()) {
            List<List<Object>> chartData = getChartDataGroupedByDate(hoaDonList);
            model.addAttribute("chartData", chartData);
            model.addAttribute("totalAmount", getTotalAmount(hoaDonList)); // Thêm tổng hóa đơn
            model.addAttribute("totalInvoices", hoaDonList.size());
            Map<String, Double> paymentSummary = getPaymentMethodSummary(hoaDonList);
            model.addAttribute("paymentSummary", paymentSummary);
            // Tổng tiền mặt và tổng chuyển khoản
            double tienMat = paymentSummary.getOrDefault("Tiền mặt", 0.0);
            double chuyenKhoan = paymentSummary.getOrDefault("Chuyển khoản", 0.0);
            model.addAttribute("tongTienMat", tienMat);
            model.addAttribute("tongChuyenKhoan", chuyenKhoan);

        }

        return "admin/doanhthuDV";
    }

    private List<List<Object>> getChartDataGroupedByDate(List<HoaDonLichKham> hoaDonList) {
        Map<String, Double> dataMap = hoaDonList.stream()
                .collect(Collectors.groupingBy(
                        hd -> hd.getNgayThanhToan().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        TreeMap::new,
                        Collectors.summingDouble(HoaDonLichKham::getTongTien)));

        List<List<Object>> chartData = new ArrayList<>();
        dataMap.forEach((date, tongTien) -> {
            chartData.add(Arrays.asList(date, tongTien));
        });

        return chartData;
    }

    private Map<String, Double> getPaymentMethodSummary(List<HoaDonLichKham> hoaDonList) {
        return hoaDonList.stream()
                .collect(Collectors.groupingBy(
                        HoaDonLichKham::getHinhThuc,
                        Collectors.summingDouble(HoaDonLichKham::getTongTien)));
    }

    private double getTotalAmount(List<HoaDonLichKham> hoaDonList) {
        return hoaDonList.stream().mapToDouble(HoaDonLichKham::getTongTien).sum();
    }

    @GetMapping("/hoadonDT")
    public String getStatistics(Model model,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<HoaDonDonThuoc> hoaDons;
        BigDecimal totalAmount;

        if (startDate == null || endDate == null) {
            hoaDons = hoaDonDonThuocService.getAllHoaDonDonThuoc();
            totalAmount = hoaDonDonThuocService.getTotalAmount();
        } else {
            hoaDons = hoaDonDonThuocService.getHoaDonDonThuocByDateRange(startDate, endDate);
            totalAmount = hoaDonDonThuocService.getTotalAmountByDate(startDate, endDate);
        }

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("hoaDons", hoaDons);
        model.addAttribute("totalAmount", totalAmount);

        // Biểu đồ: tổng số hóa đơn mỗi ngày
        List<List<Object>> dailyInvoiceChart = prepareChartData(hoaDons);
        model.addAttribute("dailyInvoiceCount", dailyInvoiceChart);

        return "admin/thongkedonthuoc";
    }

    private List<List<Object>> prepareChartData(List<HoaDonDonThuoc> hoaDons) {
        // Gom nhóm theo ngày và đếm số lượng
        Map<LocalDate, Long> grouped = hoaDons.stream()
                .collect(Collectors.groupingBy(
                        HoaDonDonThuoc::getNgayThanhToan,
                        TreeMap::new, // Đảm bảo sắp xếp theo ngày
                        Collectors.counting()));

        // Chuyển sang dạng List<List<Object>>
        return grouped.entrySet().stream()
                .map(entry -> {
                    List<Object> data = new ArrayList<>();
                    data.add((Object) entry.getKey().toString());
                    data.add((Object) entry.getValue());
                    return data;
                })
                .collect(Collectors.toList());
    }

}
