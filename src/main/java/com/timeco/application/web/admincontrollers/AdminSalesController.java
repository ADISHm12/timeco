package com.timeco.application.web.admincontrollers;

import com.timeco.application.Repository.CartItemsRepository;
import com.timeco.application.Repository.OrderItemRepository;
import com.timeco.application.Service.OrderService.OrderItemService;
import com.timeco.application.model.cart.CartItems;
import com.timeco.application.model.order.OrderItem;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/admin")
public class AdminSalesController {

    @Autowired
    private OrderItemRepository orderItemRepository ;
    @Autowired
    private OrderItemService orderItemService ;



//    @GetMapping("/sales-report")
//    public String salesReport(Model model)
//    {
//        List <OrderItem> orderItem = orderItemRepository.findByOrderStatus("delivered");
//        Collections.reverse(orderItem);
//        model.addAttribute("soldItems",orderItem);
//        return"sales-report";
//    }
@GetMapping("/sales-report")
public String salesReport(
        @RequestParam(name = "startDate", required = false) @DateTimeFormat (iso = DateTimeFormat.ISO.DATE) LocalDate  startDate,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        Model model) {
    List<OrderItem> orderItems;

    if (startDate != null && endDate != null) {
        // Filter sales based on the start and end dates
        orderItems = orderItemRepository.findByOrderStatusAndOrderOrderedDateBetween("delivered", startDate, endDate) ;
    } else {
        // If no start and end date provided, get all delivered items
        orderItems = orderItemRepository.findByOrderStatus("delivered");
    }

    Collections.reverse(orderItems);
    model.addAttribute("soldItems", orderItems);
    model.addAttribute("start",startDate);
    model.addAttribute("end",endDate);
    return "sales-report";
}

    @PostMapping("/sales-report")
    @ResponseBody
    public ResponseEntity<List<OrderItem>> salesReport(@RequestParam  String selectedTimePeriod) {
        List<OrderItem> orderItem;

        if ("day".equals(selectedTimePeriod)) {
            orderItem = orderItemRepository.findDeliveredOrderItemsInCurrentDay();

        } else if ("week".equals(selectedTimePeriod)) {
            orderItem = orderItemRepository.findDeliveredOrderItemsInCurrentWeek();
        } else if ("month".equals(selectedTimePeriod)) {
            orderItem = orderItemRepository.findDeliveredOrderItemsInCurrentMonth();

        } else if ("year".equals(selectedTimePeriod)) {
            orderItem = orderItemRepository.findDeliveredOrderItemsInCurrentYear();

        } else {
            return ResponseEntity.badRequest().build();
        }

        Collections.reverse(orderItem);
        return ResponseEntity.ok(orderItem);
    }

    @GetMapping("/salesCount-month")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> countOrderItemsByMonth() {
        Map<String, Integer> orderItemCountByMonth = orderItemService.countOrderItemsByMonth();
        return ResponseEntity.ok(orderItemCountByMonth);
    }



    @GetMapping("/downloadExcel")
    public void downloadSalesReport(HttpServletResponse  response, @RequestParam(name = "start", required = false) String start,
                                    @RequestParam(name = "end", required = false) String end) throws IOException  {


        List<OrderItem> orderItemList = new ArrayList<>() ;
        if(end != null && start !=null) {
            LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ISO_LOCAL_DATE);
            orderItemList = orderItemRepository.findByOrderStatusAndOrderOrderedDateBetween("delivered",startDate,endDate);
        }
        else {
            orderItemList = orderItemRepository.findByOrderStatus("delivered");
        }
        Workbook  workbook = new HSSFWorkbook();
        Sheet  sheet = workbook.createSheet("Sales Report");

        // Create header row
        Row  headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Order Id");
        headerRow.createCell(1).setCellValue("Products-Qty");
        headerRow.createCell(2).setCellValue("Date");
        headerRow.createCell(3).setCellValue("Customer");
        headerRow.createCell(4).setCellValue("Total Amount");

        // Fill data rows
        int rowNum = 1;
        for (OrderItem orderItem : orderItemList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(orderItem.getOrder().getOrderId());
            row.createCell(1).setCellValue(orderItem.getProduct().getProductName() + "-" + orderItem.getOrderItemCount());
            row.createCell(2).setCellValue(orderItem.getOrder().getOrderedDate() );
            row.createCell(3).setCellValue(orderItem.getOrder().getUser().getFirstName());
            row.createCell(4).setCellValue(orderItem.getProduct().getPrice() * orderItem.getOrderItemCount());

            // Add more cells as needed
        }

        // Set content type and headers for the response
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=SalesReport.xls");


        // Write workbook to the response output stream
        OutputStream  outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }



}
