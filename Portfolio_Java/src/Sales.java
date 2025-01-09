import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//Muhammad Afdi - 2502037782 - BAD SP

public class Sales extends Application {

	private TextField customerIdField, transactionDateField, totalAmountField, discountField;
	private ComboBox<String> productComboBox;
	private Label productPriceLabel;
	private TableView<Transaction> transactionTable;

	public static class Transaction {
		private final int transactionID;
		private final int customerID;
		private final int productID;
		private final String transactionDate;
		private final double totalAmount;
		private final double discount;

		public Transaction(int transactionID, int customerID, int productID, String transactionDate, double totalAmount,
				double discount) {
			this.transactionID = transactionID;
			this.customerID = customerID;
			this.productID = productID;
			this.transactionDate = transactionDate;
			this.totalAmount = totalAmount;
			this.discount = discount;
		}

		public int getTransactionID() {
			return transactionID;
		}

		public int getCustomerID() {
			return customerID;
		}

		public int getProductID() {
			return productID;
		}

		public String getTransactionDate() {
			return transactionDate;
		}

		public double getTotalAmount() {
			return totalAmount;
		}

		public double getDiscount() {
			return discount;
		}
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Sales Transaction Management");

		customerIdField = new TextField();
		transactionDateField = new TextField();
		totalAmountField = new TextField();
		discountField = new TextField();

		productComboBox = new ComboBox<>();
		productPriceLabel = new Label();

		loadProductData();

		Label labelCustomerId = new Label("Customer ID:");
		Label labelTransactionDate = new Label("Transaction Date (YYYY-MM-DD:");
		Label labelProduct = new Label("Product:");
		Label labelTotalAmount = new Label("Total Amount:");
		Label labelDiscount = new Label("Discount:");
		Label labelProductPrice = new Label("Product Price:");

		Button addTransactionButton = new Button("Add Transaction");
		Button updateTransactionButton = new Button("Update Transaction");
		Button deleteTransactionButton = new Button("Delete Transaction");
		Button viewTransactionButton = new Button("View Transactions");

		// Tampilan UI JavaFX
		GridPane grid = new GridPane();
		grid.setVgap(10);
		grid.setHgap(10);
		grid.setPadding(new Insets(20));

		grid.add(labelCustomerId, 0, 0);
		grid.add(customerIdField, 1, 0);

		grid.add(labelTransactionDate, 0, 1);
		grid.add(transactionDateField, 1, 1);

		grid.add(labelProduct, 0, 2);
		grid.add(productComboBox, 1, 2);

		grid.add(labelProductPrice, 0, 3);
		grid.add(productPriceLabel, 1, 3);

		grid.add(labelDiscount, 0, 4);
		grid.add(discountField, 1, 4);

		grid.add(labelTotalAmount, 0, 5);
		grid.add(totalAmountField, 1, 5);

		grid.add(addTransactionButton, 0, 6);
		grid.add(updateTransactionButton, 1, 6);
		grid.add(deleteTransactionButton, 0, 7);
		grid.add(viewTransactionButton, 1, 7);

		transactionTable = new TableView<>();
		setUpTransactionTable();

		VBox vbox = new VBox(10);
		vbox.getChildren().addAll(grid, transactionTable);
		vbox.setPadding(new Insets(10));
		vbox.setStyle("-fx-background-color: #FFFFE0;");

		Scene scene = new Scene(vbox, 600, 600);
		primaryStage.setScene(scene);
		primaryStage.show();

		productComboBox.setOnAction(e -> calculateTotalAmount());
		discountField.setOnKeyReleased(e -> calculateTotalAmount());
		addTransactionButton.setOnAction(e -> addTransaction());
		viewTransactionButton.setOnAction(e -> viewTransactions());
		deleteTransactionButton.setOnAction(e -> deleteTransaction());
		updateTransactionButton.setOnAction(e -> updateTransaction());

		transactionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				SelectList(newSelection);
			}
		});
	}

	private void setUpTransactionTable() {
		TableColumn<Transaction, Integer> transactionIDCol = new TableColumn<>("Transaction ID");
		transactionIDCol.setCellValueFactory(new PropertyValueFactory<>("transactionID"));

		TableColumn<Transaction, Integer> customerIDCol = new TableColumn<>("Customer ID");
		customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));

		TableColumn<Transaction, Integer> productIDCol = new TableColumn<>("Product ID");
		productIDCol.setCellValueFactory(new PropertyValueFactory<>("productID"));

		TableColumn<Transaction, String> transactionDateCol = new TableColumn<>("Transaction Date");
		transactionDateCol.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));

		TableColumn<Transaction, Double> totalAmountCol = new TableColumn<>("Total Amount");
		totalAmountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

		TableColumn<Transaction, Double> discountCol = new TableColumn<>("Discount");
		discountCol.setCellValueFactory(new PropertyValueFactory<>("discount"));

		transactionTable.getColumns().addAll(transactionIDCol, customerIDCol, productIDCol, transactionDateCol,
				totalAmountCol, discountCol);
	}

	// Tambahan dari saya untuk mengambil data produk agar bisa dipilih untuk
	// transaksi
	private void loadProductData() {
		String query = "SELECT name, price FROM Product";

		try (Connection connection = Connect.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String nameProduct = resultSet.getString("name");
				double priceProduct = resultSet.getDouble("price");
				productComboBox.getItems().add(nameProduct + " - $" + priceProduct);
			}

		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load product data.");
			e.printStackTrace();
		}
	}

	// Melakukan penghitungan terhadap harga produk dan discount
	private void calculateTotalAmount() {
		if (productComboBox.getSelectionModel().getSelectedItem() != null) {
			String selectedProduct = productComboBox.getSelectionModel().getSelectedItem();
			String[] productDetails = selectedProduct.split(" - \\$");
			double productPrice = Double.parseDouble(productDetails[1]);

			productPriceLabel.setText("$" + productPrice);

			double discount = 0.0;
			if (!discountField.getText().trim().isEmpty()) {
				try {
					discount = Double.parseDouble(discountField.getText().trim());
				} catch (NumberFormatException e) {
					showAlert(Alert.AlertType.WARNING, "ERROR", "Salah menginput nilai discount.");
					return;
				}
			}

			double totalAmount = productPrice - (productPrice * discount / 100);
			totalAmountField.setText(String.format("%.2f", totalAmount));
		}
	}

	// Untuk memvalidasi bahwa customerID terdaftar pada database agar bisa
	// melanjutkan transaksi
	private boolean validateCustomerId(int customerId) {
		String query = "SELECT customerID FROM Customer WHERE customerID = ?";

		try (Connection connection = Connect.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setInt(1, customerId);
			ResultSet resultSet = preparedStatement.executeQuery();

			return resultSet.next();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// add ke Database
	private void addTransaction() {
		String customerIdText = customerIdField.getText().trim();
		String transactionDate = transactionDateField.getText().trim();
		String selectedProduct = productComboBox.getSelectionModel().getSelectedItem();
		String totalAmount = totalAmountField.getText().trim();

		if (customerIdText.isEmpty() || transactionDate.isEmpty() || selectedProduct == null || totalAmount.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "ERROR", "Isi semua field.");
			return;
		}

		int customerId = Integer.parseInt(customerIdText);
		if (!validateCustomerId(customerId)) {
			showAlert(Alert.AlertType.WARNING, "ERROR", "ID Customer tidak ada di Database.");
			return;
		}

		String[] productDetails = selectedProduct.split(" - \\$");
		String productName = productDetails[0];
		double totalAmountValue = Double.parseDouble(totalAmount);

		String productQuery = "SELECT productID FROM Product WHERE name = ?";
		Integer productId = null;

		try (Connection connection = Connect.getConnection();
				PreparedStatement productStatement = connection.prepareStatement(productQuery)) {

			productStatement.setString(1, productName);
			ResultSet productResult = productStatement.executeQuery();

			if (productResult.next()) {
				productId = productResult.getInt("productID");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		double discount = 0.0;
		try {
			if (!discountField.getText().trim().isEmpty()) {
				discount = Double.parseDouble(discountField.getText().trim());
			}
		} catch (NumberFormatException e) {
			return;
		}

		String query = "INSERT INTO Sales (customerID, productID, transactionDate, totalAmount, discount) VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = Connect.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setInt(1, customerId);
			preparedStatement.setInt(2, productId);
			preparedStatement.setDate(3, Date.valueOf(transactionDate));
			preparedStatement.setDouble(4, totalAmountValue);
			preparedStatement.setDouble(5, discount);

			int rowsInserted = preparedStatement.executeUpdate();
			if (rowsInserted > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Transaksi berhasil ditambahkan.");
				viewTransactions();
			}

		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "GAGAL", "gagal menambahkan transaksi.");
			e.printStackTrace();
		}
	}

// TADI DISIINII

	// Untuk melihat daftar transaksi pada tableview
	private void viewTransactions() {
		transactionTable.getItems().clear(); // Clear current data

		String query = "SELECT * FROM Sales";
		try (Connection connection = Connect.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				int transactionID = resultSet.getInt("transactionID");
				int customerID = resultSet.getInt("customerID");
				int productID = resultSet.getInt("productID");
				String transactionDate = resultSet.getDate("transactionDate").toString();
				double totalAmount = resultSet.getDouble("totalAmount");
				double discount = resultSet.getDouble("discount");

				Transaction transaction = new Transaction(transactionID, customerID, productID, transactionDate,
						totalAmount, discount);
				transactionTable.getItems().add(transaction);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Memperbarui data transaksi ke Database
	private void updateTransaction() {
		Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
		if (selectedTransaction == null) {
			showAlert(Alert.AlertType.WARNING, "ERROR", "Silahkan pilih untuk data yang ingin diperbarui");
			return;
		}

		String customerId = customerIdField.getText().trim();
		String transactionDate = transactionDateField.getText().trim();
		String selectedProduct = productComboBox.getSelectionModel().getSelectedItem();
		String totalAmount = totalAmountField.getText().trim();

		if (customerId.isEmpty() || transactionDate.isEmpty() || selectedProduct == null || totalAmount.isEmpty()) {
			return;
		}

		String[] productDetails = selectedProduct.split(" - \\$");
		String productName = productDetails[0];
		double totalAmountValue = Double.parseDouble(totalAmount);

		Integer productId = getProductIDFromName(productName);

		double discount = 0.0;
		try {
			if (!discountField.getText().trim().isEmpty()) {
				discount = Double.parseDouble(discountField.getText().trim());
			}
		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.WARNING, "ERROR", "Salah memasukan nilai discount.");
			return;
		}

		String updateQuery = "UPDATE Sales SET customerID = ?, productID = ?, transactionDate = ?, totalAmount = ?, discount = ? WHERE transactionID = ?";

		try (Connection connection = Connect.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

			preparedStatement.setInt(1, Integer.parseInt(customerId));
			preparedStatement.setInt(2, productId);

			try {
				preparedStatement.setDate(3, java.sql.Date.valueOf(transactionDate)); // transactionDate
			} catch (IllegalArgumentException e) {
				showAlert(Alert.AlertType.WARNING, "ERROR", "Format tanggal salah. Gunakan 'yyyy-MM-dd'.");
				return;
			}

			preparedStatement.setDouble(4, totalAmountValue); // totalAmount
			preparedStatement.setDouble(5, discount); // discount
			preparedStatement.setInt(6, selectedTransaction.getTransactionID()); // transactionID

			int rowsUpdated = preparedStatement.executeUpdate();
			if (rowsUpdated > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Transaksi berhasil diperbarui.");
				viewTransactions();
			}

		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "ERROR", "Gagal memperbarui transaksi.");
			e.printStackTrace();
		}
	}

	private int getProductIDFromName(String productName) {
		String[] productDetails = productName.split(" - \\$");
		String productNameOnly = productDetails[0];
		String query = "SELECT product_id FROM Product WHERE name = ?";

		try (Connection connection = Connect.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setString(1, productNameOnly);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getInt("product_id");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// Delete transaksi dari Database
	private void deleteTransaction() {
		Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
		if (selectedTransaction == null) {
			showAlert(Alert.AlertType.WARNING, "ERROR", "Silahkan pilih untuk data yang ingin dihapus");
			return;
		}

		String deleteQuery = "DELETE FROM Sales WHERE transactionID = ?";

		try (Connection connection = Connect.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

			preparedStatement.setInt(1, selectedTransaction.getTransactionID());

			int rowsDeleted = preparedStatement.executeUpdate();
			if (rowsDeleted > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Transaksi berhasil dihapus.");
				viewTransactions();
			}

		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "ERROR", "Gagal menghapus data transaksi.");
			e.printStackTrace();
		}
	}

	private void SelectList(Transaction transaction) {
		customerIdField.setText(String.valueOf(transaction.getCustomerID()));
		transactionDateField.setText(transaction.getTransactionDate());
		totalAmountField.setText(String.valueOf(transaction.getTotalAmount()));
		discountField.setText(String.valueOf(transaction.getDiscount()));

		int productID = transaction.getProductID();
		String query = "SELECT name, price FROM Product WHERE product_id = ?";

		try (Connection connection = Connect.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setInt(1, productID);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				String productName = resultSet.getString("name");
				double productPrice = resultSet.getDouble("price");
				productComboBox.setValue(productName + " - $" + productPrice);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}