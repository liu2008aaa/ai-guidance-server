package com.guidance.utils;

import java.util.List;

/**
 * 表格输出格式化
 */
public class TableBuilder {
    private StringBuilder sb = new StringBuilder();
    private int[] columnWidths;
    private boolean hasHeader = false;
    
    // 初始化，指定每列宽度
    public TableBuilder(int... widths) {
        this.columnWidths = widths;
    }
    
    // 添加表头
    public TableBuilder header(String... headers) {
        appendRow(headers);
        appendSeparator();
        hasHeader = true;
        return this;
    }

    public TableBuilder rows(List<Object[]> rows) {
        for (Object[] o : rows){
            appendRow(o);
        }
        return this;
    }

    // 添加数据行
    public TableBuilder row(Object... cells) {
        appendRow(cells);
        return this;
    }
    
    private void appendRow(Object[] cells) {
        sb.append("|");
        for (int i = 0; i < cells.length; i++) {
            String cell = String.valueOf(cells[i]);
            // 截断或填充
            cell = cell.length() > columnWidths[i] 
                ? cell.substring(0, columnWidths[i]-3) + "..." 
                : cell;
            sb.append(String.format(" %-"+columnWidths[i]+"s |", cell));
        }
        sb.append("\n");
    }
    
    private void appendSeparator() {
        sb.append("|");
        for (int width : columnWidths) {
            sb.append("-".repeat(width + 2)).append("|");
        }
        sb.append("\n");
    }
    
    @Override
    public String toString() {
        return sb.toString();
    }
    
    // 使用示例
    public static void main(String[] args) {
        String table = new TableBuilder(12, 8, 15, 10)
            .header("产品名称", "数量", "单价", "总价")
            .row("iPhone 15", 2, "¥5999", "¥11998")
            .row("MacBook Pro", 1, "¥14999", "¥14999")
            .row("AirPods Pro", 3, "¥1899", "¥5697")
            .toString();
            
        System.out.println(table);
    }
}