package com.example.quanlybenhvien.Controller.NguoiDung;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;

import com.example.quanlybenhvien.Service.TrieuChungService;

@Controller
public class ChatBotController {

    @Autowired
    private TrieuChungService trieuChungService;

    // Hiển thị form chat
    @GetMapping("/chat")
    public String chatForm() {
        return "chat";
    }

    // Xử lý khi người dùng gửi câu hỏi
    @PostMapping("/chat")
    public String chat(@RequestParam("userQuery") String userQuery, Model model) {
        String response = trieuChungService.getSpecializationBySymptom(userQuery);
        System.out.println("Kết quả tìm kiếm: " + response);

        model.addAttribute("response", response);
        return "chat"; // Trả về view 'chat'
    }
}
