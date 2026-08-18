package com.module3.ccafe.controller.admin;

import com.module3.ccafe.dto.CafeTableRequest;
import com.module3.ccafe.dto.CafeTableResponse;
import com.module3.ccafe.service.CafeTableService;
import com.module3.ccafe.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/table")
@RequiredArgsConstructor
public class AdminTableViewController {

    private final CafeTableService cafeTableService;
    private final QrCodeService qrCodeService;

    @GetMapping("/page")
    public String tablePage(Model model) {
        model.addAttribute("tableList", cafeTableService.getAllTables());
        return "admin/table/list";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("tableRequest", new CafeTableRequest());
        return "admin/table/create";
    }

    @PostMapping("/create")
    public String createTable(
            @ModelAttribute("tableRequest") CafeTableRequest request,
            RedirectAttributes redirectAttributes) {

        try {
            CafeTableResponse table =
                    cafeTableService.createTable(request);

            String qrCodeUrl =
                    qrCodeService.generateQrCode(table.getTableId());

            cafeTableService.updateTableQrCode(
                    table.getTableId(),
                    qrCodeUrl
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Thêm bàn thành công!"
            );

            return "redirect:/admin/table/page";

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );

            return "redirect:/admin/table/create";
        }
    }

    @GetMapping("/edit/{tableId}")
    public String editPage(
            @PathVariable Integer tableId,
            Model model) {

        CafeTableResponse table = cafeTableService.getTableById(tableId);

        CafeTableRequest request = CafeTableRequest.builder()
                .tableNumber(table.getTableNumber())
                .build();

        model.addAttribute("tableRequest", request);
        model.addAttribute("table", table);

        return "admin/table/edit";
    }

    @PostMapping("/edit/{tableId}")
    public String updateTable(
            @PathVariable Integer tableId,
            @ModelAttribute("tableRequest") CafeTableRequest request,
            RedirectAttributes redirectAttributes) {

        try {
            cafeTableService.updateTable(tableId, request);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Cập nhật bàn thành công!"
            );

            return "redirect:/admin/table/page";

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );

            return "redirect:/admin/table/edit/" + tableId;
        }
    }

    @PostMapping("/delete/{tableId}")
    public String deleteTable(
            @PathVariable Integer tableId,
            RedirectAttributes redirectAttributes) {

        try {
            cafeTableService.deleteTable(tableId);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Xóa bàn thành công!"
            );

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/table/page";
    }
}
