import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Product extends Application {

	private TableView<ProductManagement> tableProduct;
	private ObservableList<ProductManagement> productList;
	private TextField nameField, categoryField, priceField, quantityField, expdateField, productIDField;

	public static class ProductManagement {
		private String nameProduct;
		private String categoryProduct;
		private Integer priceProduct;
		private Integer quantityProduct;
		private Date expdateProduct;
		private Integer productID;

		public ProductManagement(String name, String category, Integer price, Integer quantity, Date expdate,
				Integer productID) {
			this.nameProduct = name;
			this.categoryProduct = category;
			this.priceProduct = price;
			this.quantityProduct = quantity;
			this.expdateProduct = expdate;
			this.productID = productID;
		}

		public String getNameProduct() {
			return nameProduct;
		}

		public void setNameProduct(String nameProduct) {
			this.nameProduct = nameProduct;
		}

		public String getCategoryProduct() {
			return categoryProduct;
		}

		public void setCategoryProduct(String categoryProduct) {
			this.categoryProduct = categoryProduct;
		}

		public Integer getPriceProduct() {
			return priceProduct;
		}

		public void setPriceProduct(Integer priceProduct) {
			this.priceProduct = priceProduct;
		}

		public Integer getQuantityProduct() {
			return quantityProduct;
		}

		public void setQuantityProduct(Integer quantityProduct) {
			this.quantityProduct = quantityProduct;
		}

		public Date getExpdateProduct() {
			return expdateProduct;
		}

		public void setExpdateProduct(Date expdateProduct) {
			this.expdateProduct = expdateProduct;
		}

		public Integer getProductID() {
			return productID;
		}

		public void setProductID(Integer productID) {
			this.productID = productID;
		}
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Product Management");

		productList = FXCollections.observableArrayList();

		nameField = new TextField();
		categoryField = new TextField();
		priceField = new TextField();
		quantityField = new TextField();
		expdateField = new TextField();
		productIDField = new TextField();

		Label labelNameProduct = new Label("Name:");
		Label labelCategoryProduct = new Label("Category:");
		Label labelPrice = new Label("Price:");
		Label labelQuantity = new Label("Stock Quantity:");
		Label labelExpdate = new Label("Expiry Date (yyyy/MM/dd):");
		Label labelProductID = new Label("Product ID:");

		Button addProductButton = new Button("Add Product");
		Button deleteProductButton = new Button("Delete Product");
		Button updateProductButton = new Button("Update Product");
		Button viewProductButton = new Button("View Product");

		tableProduct = new TableView<>();
		tableProduct.setItems(productList);

		TableColumn<ProductManagement, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("nameProduct"));

		TableColumn<ProductManagement, String> categoryColumn = new TableColumn<>("Category");
		categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryProduct"));

		TableColumn<ProductManagement, Integer> priceColumn = new TableColumn<>("Price");
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("priceProduct"));

		TableColumn<ProductManagement, Integer> quantityColumn = new TableColumn<>("Quantity");
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityProduct"));

		TableColumn<ProductManagement, Date> expdateColumn = new TableColumn<>("Expiry Date");
		expdateColumn.setCellValueFactory(new PropertyValueFactory<>("expdateProduct"));

		TableColumn<ProductManagement, Integer> productIDColumn = new TableColumn<>("Product ID");
		productIDColumn.setCellValueFactory(new PropertyValueFactory<>("productID"));

		tableProduct.getColumns().addAll(nameColumn, categoryColumn, priceColumn, quantityColumn, expdateColumn,
				productIDColumn);

		// Bagian untuk melakukan add Product
		addProductButton.setOnAction(e -> {
			String name = nameField.getText().trim();
			String category = categoryField.getText().trim();
			Integer price = Integer.parseInt(priceField.getText().trim());
			Integer quantity = Integer.parseInt(quantityField.getText().trim());
			Date expdate = parseDate(expdateField.getText().trim());
			Integer productID = Integer.parseInt(productIDField.getText().trim());

			addData(productID, name, category, price, quantity, expdate);
			loadProductData();

			ProductManagement newProduct = new ProductManagement(name, category, price, quantity, expdate, productID);
			productList.add(newProduct);

			clearFields();
		});

		// Bagian untuk melakukan delete product
		deleteProductButton.setOnAction(e -> {
			ProductManagement selectedProduct = tableProduct.getSelectionModel().getSelectedItem();
			if (selectedProduct != null) {
				deleteData(selectedProduct.getProductID()); // Call deleteData with the selected product ID
				productList.remove(selectedProduct);
			} else {
				showAlert(Alert.AlertType.WARNING, "Belum dipilih", "Silahkan Pilih Produk yang Ingin Dihapus.");
			}
		});

		// Bagian untuk mengupdate data product
		updateProductButton.setOnAction(e -> {
			ProductManagement selectedProduct = tableProduct.getSelectionModel().getSelectedItem();
			if (selectedProduct != null) {
				String name = nameField.getText().trim();
				String category = categoryField.getText().trim();
				Integer price = Integer.parseInt(priceField.getText().trim());
				Integer quantity = Integer.parseInt(quantityField.getText().trim());
				Date expdate = parseDate(expdateField.getText().trim());
				Integer productID = Integer.parseInt(productIDField.getText().trim());

				updateData(productID, name, category, price, quantity, expdate);

				selectedProduct.setNameProduct(name);
				selectedProduct.setCategoryProduct(category);
				selectedProduct.setPriceProduct(price);
				selectedProduct.setQuantityProduct(quantity);
				selectedProduct.setExpdateProduct(expdate);
				selectedProduct.setProductID(productID);

				tableProduct.refresh();
				clearFields();
			} else {
				showAlert(Alert.AlertType.WARNING, "Belum dipilih", "Silahkan Pilih Produk yang Ingin Diperbarui.");
			}
		});

		viewProductButton.setOnAction(e -> {
			loadProductData();
		});

		tableProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				nameField.setText(newSelection.getNameProduct());
				categoryField.setText(newSelection.getCategoryProduct());
				priceField.setText(newSelection.getPriceProduct().toString());
				quantityField.setText(newSelection.getQuantityProduct().toString());
				expdateField.setText(new SimpleDateFormat("yyyy/MM/dd").format(newSelection.getExpdateProduct()));
				productIDField.setText(newSelection.getProductID().toString());
			}
		});

		// Tampilan UI JavaFX
		GridPane grid = new GridPane();
		grid.setVgap(10);
		grid.setHgap(10);
		grid.setPadding(new Insets(20));

		grid.add(labelNameProduct, 0, 0);
		grid.add(nameField, 1, 0);

		grid.add(labelCategoryProduct, 0, 1);
		grid.add(categoryField, 1, 1);

		grid.add(labelPrice, 0, 2);
		grid.add(priceField, 1, 2);

		grid.add(labelQuantity, 0, 3);
		grid.add(quantityField, 1, 3);

		grid.add(labelExpdate, 0, 4);
		grid.add(expdateField, 1, 4);

		grid.add(labelProductID, 0, 5);
		grid.add(productIDField, 1, 5);

		grid.add(addProductButton, 0, 6);
		grid.add(deleteProductButton, 1, 6);
		grid.add(updateProductButton, 0, 7);
		grid.add(viewProductButton, 1, 7);

		VBox vbox = new VBox(10);
		vbox.getChildren().addAll(grid, tableProduct);
		vbox.setPadding(new Insets(10));
		vbox.setStyle("-fx-background-color: #FFFFE0;");

		Scene scene = new Scene(vbox, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// Add ke Database
	public Integer addData(Integer productID, String name, String category, Integer price, Integer quantity,
			Date expdate) {
		String query = "INSERT INTO Product (ProductID, Name, Category, Price, StockQuantity, ExpiryDate) VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection connection = Connect.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query,
						Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setInt(1, productID);
			preparedStatement.setString(2, name);
			preparedStatement.setString(3, category);
			preparedStatement.setInt(4, price);
			preparedStatement.setInt(5, quantity);
			preparedStatement.setDate(6, new java.sql.Date(expdate.getTime()));

			int rowsInserted = preparedStatement.executeUpdate();

			if (rowsInserted > 0) {
				ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
				if (generatedKeys.next()) {
					productID = generatedKeys.getInt(1);
					System.out.println("Data produk baru berhasil ditambahkan dengan ID: " + productID);
				}
			}

		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "Database Eror", "Gagal menambahkan data produk ke database.");
			e.printStackTrace();
		}

		return productID;
	}

	// Delete dari Database
	public void deleteData(Integer productID) {
		String query = "DELETE FROM Product WHERE ProductID = ?";

		try (Connection connection = Connect.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setInt(1, productID);

			int rowsDeleted = preparedStatement.executeUpdate();

			if (rowsDeleted > 0) {
				System.out.println("Produk dengan ID " + productID + " berhasil dihapus.");
			} else {
				System.out.println("Tidak ada produk dengan ID " + productID);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Memperbarui data ke Database
	public void updateData(Integer productID, String name, String category, Integer price, Integer quantity,
			Date expdate) {
		String query = "UPDATE Product SET Name = ?, Category = ?, Price = ?, StockQuantity = ?, ExpiryDate = ? WHERE ProductID = ?";

		try (Connection connection = Connect.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setString(1, name);
			preparedStatement.setString(2, category);
			preparedStatement.setInt(3, price);
			preparedStatement.setInt(4, quantity);
			preparedStatement.setDate(5, new java.sql.Date(expdate.getTime()));
			preparedStatement.setInt(6, productID);

			int rowsUpdated = preparedStatement.executeUpdate();

			if (rowsUpdated > 0) {
				System.out.println("Produk dengan ID " + productID + " berhasil diperbarui.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadProductData() {
		String query = "SELECT * FROM Product ORDER BY ProductID";

		try (Connection connection = Connect.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			productList.clear();

			while (resultSet.next()) {
				Integer productID = resultSet.getInt("ProductID");
				String name = resultSet.getString("Name");
				String category = resultSet.getString("Category");
				int price = resultSet.getInt("Price");
				int quantity = resultSet.getInt("StockQuantity");
				java.sql.Date expdateSql = resultSet.getDate("ExpiryDate");
				java.util.Date expdateUtil = new java.util.Date(expdateSql.getTime());

				ProductManagement product = new ProductManagement(name, category, price, quantity, expdateUtil,
						productID);
				productList.add(product);
			}

		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "Database Error", "Gagal memuat data produk dari database.");
			e.printStackTrace();
		}
	}

	private Date parseDate(String dateStr) {
		try {
			return new SimpleDateFormat("yyyy/MM/dd").parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void clearFields() {
		nameField.clear();
		categoryField.clear();
		priceField.clear();
		quantityField.clear();
		expdateField.clear();
		productIDField.clear();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
