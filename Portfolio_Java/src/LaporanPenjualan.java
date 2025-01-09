import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LaporanPenjualan {

    public LaporanPenjualan() {
    }

    public void generateLaporanPenjualan() {
        String totalSalesQuery = "SELECT SUM(totalAmount) AS TotalSales FROM sales WHERE MONTH(transactionDate) = 6 AND YEAR(transactionDate) = 2024;";
        String topProductQuery = "SELECT p.Name AS ProductName, COUNT(s.productID) AS TotalSold " +
                                 "FROM sales s JOIN product p ON s.productID = p.ProductID " +
                                 "WHERE MONTH(s.transactionDate) = 6 AND YEAR(s.transactionDate) = 2024 " +
                                 "GROUP BY s.productID ORDER BY TotalSold DESC LIMIT 1;";
        String bestCustomerQuery = "SELECT c.Name AS CustomerName, SUM(s.totalAmount) AS TotalSpent " +
                                   "FROM sales s JOIN customer c ON s.customerID = c.CustomerID " +
                                   "WHERE MONTH(s.transactionDate) = 6 AND YEAR(s.transactionDate) = 2024 " +
                                   "GROUP BY s.customerID ORDER BY TotalSpent DESC LIMIT 1;";

        try (Connection conn = Connect.getConnection(); 
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(totalSalesQuery);
            if (rs.next()) {
                System.out.println("Total Penjualan Bulan Ke-6: Rp " + rs.getBigDecimal("TotalSales"));
            }

            rs = stmt.executeQuery(topProductQuery);
            if (rs.next()) {
                System.out.println("Produk Paling Laku: " + rs.getString("ProductName") + " = " + rs.getInt("TotalSold")  + " Produk terjual");
            }

            rs = stmt.executeQuery(bestCustomerQuery);
            if (rs.next()) {
                System.out.println("Pelanggan Paling TERBAIKKKK: " + rs.getString("CustomerName") + " - Rp " + rs.getBigDecimal("TotalSpent"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LaporanPenjualan laporan = new LaporanPenjualan();
        laporan.generateLaporanPenjualan();
    }
}

