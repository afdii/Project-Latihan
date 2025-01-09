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
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

//Muhammad Afdi - 2502037782 - BAD SP

public class Customer extends Application {

	private TableView<CustomerManagement> tableCustomer;
	private ObservableList<CustomerManagement> customerList;
	private TextField nameField, addressField, phoneField, emailField, membershipStatusField;

	public static class CustomerManagement {
		private Integer customerID;
		private String nameCust;
		private String addressCust;
		private String phoneCust;
		private String emailCust;
		private String membershipCust;

		public CustomerManagement(Integer customerID, String name, String address, String phone, String email,
				String membership) {
			this.customerID = customerID;
			this.nameCust = name;
			this.addressCust = address;
			this.phoneCust = phone;
			this.emailCust = email;
			this.membershipCust = membership;
		}

		public Integer getCustomerID() {
			return customerID;
		}

		public void setCustomerID(Integer customerID) {
			this.customerID = customerID;
		}

		public String getNameCust() {
			return nameCust;
		}

		public void setNameCust(String nameCust) {
			this.nameCust = nameCust;
		}

		public String getAddressCust() {
			return addressCust;
		}

		public void setAddressCust(String addressCust) {
			this.addressCust = addressCust;
		}

		public String getPhoneCust() {
			return phoneCust;
		}

		public void setPhoneCust(String phoneCust) {
			this.phoneCust = phoneCust;
		}

		public String getEmailCust() {
			return emailCust;
		}

		public void setEmailCust(String emailCust) {
			this.emailCust = emailCust;
		}

		public String getMembershipCust() {
			return membershipCust;
		}

		public void setMembershipCust(String membershipCust) {
			this.membershipCust = membershipCust;
		}
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Customer Management");

		customerList = FXCollections.observableArrayList();

		nameField = new TextField();
		addressField = new TextField();
		phoneField = new TextField();
		emailField = new TextField();
		membershipStatusField = new TextField();

		Label labelNameCust = new Label("Name:");
		Label labelAddressCust = new Label("Address:");
		Label labelPhoneCust = new Label("Phone:");
		Label labelEmailCust = new Label("Email:");
		Label labelMembership = new Label("Membership Status (Active/InActive):");

		Button addCustomerButton = new Button("Add Customer");
		Button deleteCustomerButton = new Button("Delete Customer");
		Button updateCustomerButton = new Button("Update Customer");
		Button viewCustomerButton = new Button("View Customer");

		tableCustomer = new TableView<>();
		tableCustomer.setItems(customerList);

		TableColumn<CustomerManagement, Integer> idColumn = new TableColumn<>("Customer ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));

		TableColumn<CustomerManagement, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("nameCust"));

		TableColumn<CustomerManagement, String> addressColumn = new TableColumn<>("Address");
		addressColumn.setCellValueFactory(new PropertyValueFactory<>("addressCust"));

		TableColumn<CustomerManagement, String> phoneColumn = new TableColumn<>("Phone");
		phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneCust"));

		TableColumn<CustomerManagement, String> emailColumn = new TableColumn<>("Email");
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("emailCust"));

		TableColumn<CustomerManagement, String> membershipColumn = new TableColumn<>("Membership Status");
		membershipColumn.setCellValueFactory(new PropertyValueFactory<>("membershipCust"));

		tableCustomer.getColumns().addAll(idColumn, nameColumn, addressColumn, phoneColumn, emailColumn,
				membershipColumn);

		// Bagian untuk melakukan add customer
		addCustomerButton.setOnAction(e -> {
			String name = nameField.getText().trim();
			String address = addressField.getText().trim();
			String phone = phoneField.getText().trim();
			String email = emailField.getText().trim();
			String membership = membershipStatusField.getText().trim();

			Integer customerID = addData(name, address, phone, email, membership);

			if (customerID != null) {
				CustomerManagement newCustomer = new CustomerManagement(customerID, name, address, phone, email,
						membership);
				customerList.add(newCustomer);
				clearFields();
			}
		});

		// Bagian untuk melakukan delete customer
		deleteCustomerButton.setOnAction(e -> {
			CustomerManagement selectedCustomer = tableCustomer.getSelectionModel().getSelectedItem();
			if (selectedCustomer != null) {
				// Hapus data dari database
				deleteData(selectedCustomer.getCustomerID());

				customerList.remove(selectedCustomer);
			} else {
				showAlert(Alert.AlertType.WARNING, "Belum dipilih", "Silahkan Pilih Customer yang Ingin Dihapus.");
			}
		});

		// Bagian untuk mengupdate data customer
		updateCustomerButton.setOnAction(e -> {
			CustomerManagement selectedCustomer = tableCustomer.getSelectionModel().getSelectedItem();
			if (selectedCustomer != null) {
				String name = nameField.getText().trim();
				String address = addressField.getText().trim();
				String phone = phoneField.getText().trim();
				String email = emailField.getText().trim();
				String membership = membershipStatusField.getText().trim();

				updateData(selectedCustomer.getCustomerID(), name, address, phone, email, membership);

				selectedCustomer.setNameCust(name);
				selectedCustomer.setAddressCust(address);
				selectedCustomer.setPhoneCust(phone);
				selectedCustomer.setEmailCust(email);
				selectedCustomer.setMembershipCust(membership);

				tableCustomer.refresh();
				clearFields();
			} else {
				showAlert(Alert.AlertType.WARNING, "Belum dipilih", "Silahkan Pilih Customer yang Ingin Diperbarui.");
			}
		});

		viewCustomerButton.setOnAction(e -> {
			loadCustomerData();
		});

		tableCustomer.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				nameField.setText(newSelection.getNameCust());
				addressField.setText(newSelection.getAddressCust());
				phoneField.setText(newSelection.getPhoneCust());
				emailField.setText(newSelection.getEmailCust());
				membershipStatusField.setText(newSelection.getMembershipCust());
			}
		});

		// Tampilan UI JavaFX
		GridPane grid = new GridPane();
		grid.setVgap(10);
		grid.setHgap(10);
		grid.setPadding(new Insets(20));

		grid.add(labelNameCust, 0, 0);
		grid.add(nameField, 1, 0);

		grid.add(labelAddressCust, 0, 1);
		grid.add(addressField, 1, 1);

		grid.add(labelPhoneCust, 0, 2);
		grid.add(phoneField, 1, 2);

		grid.add(labelEmailCust, 0, 3);
		grid.add(emailField, 1, 3);

		grid.add(labelMembership, 0, 4);
		grid.add(membershipStatusField, 1, 4);

		grid.add(addCustomerButton, 0, 6);
		grid.add(deleteCustomerButton, 1, 6);
		grid.add(updateCustomerButton, 0, 7);
		grid.add(viewCustomerButton, 1, 7);

		VBox vbox = new VBox(10);
		vbox.getChildren().addAll(grid, tableCustomer);
		vbox.setPadding(new Insets(10));
		vbox.setStyle("-fx-background-color: #FFFFE0;");

		Scene scene = new Scene(vbox, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// Add ke Database
	public Integer addData(String name, String address, String phone, String email, String membership) {
		String query = "INSERT INTO Customer (Name, Address, Phone, Email, MembershipStatus) VALUES (?, ?, ?, ?, ?)";
		Integer customerID = null;

		try (Connection connection = Connect.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query,
						Statement.RETURN_GENERATED_KEYS)) {

			preparedStatement.setString(1, name);
			preparedStatement.setString(2, address);
			preparedStatement.setString(3, phone);
			preparedStatement.setString(4, email);
			preparedStatement.setString(5, membership);

			int rowsInserted = preparedStatement.executeUpdate();

			if (rowsInserted > 0) {
				ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
				if (generatedKeys.next()) {
					customerID = generatedKeys.getInt(1); // Dapatkan CustomerID yang baru
					System.out.println("Data customer baru berhasil ditambahkan dengan ID: " + customerID);
				}
			}

		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "GAGAL", "Kesalahan dalam input data.");
			e.printStackTrace();
		}

		return customerID;
	}

	// Delete dari database
	public void deleteData(Integer customerID) {
		String query = "DELETE FROM Customer WHERE CustomerID = ?";

		try (Connection connection = Connect.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setInt(1, customerID);

			int rowsDeleted = preparedStatement.executeUpdate();

			if (rowsDeleted > 0) {
				System.out.println("Customer dengan ID " + customerID + " berhasil dihapus.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Memperbarui Data ke Database
	public void updateData(Integer customerID, String name, String address, String phone, String email,
			String membership) {
		String query = "UPDATE Customer SET Name = ?, Address = ?, Phone = ?, Email = ?, MembershipStatus = ? WHERE CustomerID = ?";

		try (Connection connection = Connect.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setString(1, name);
			preparedStatement.setString(2, address);
			preparedStatement.setString(3, phone);
			preparedStatement.setString(4, email);
			preparedStatement.setString(5, membership);
			preparedStatement.setInt(6, customerID);

			int rowsUpdated = preparedStatement.executeUpdate();

			if (rowsUpdated > 0) {
				System.out.println("Customer dengan ID " + customerID + " berhasil diperbarui.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadCustomerData() {
		String query = "SELECT * FROM Customer ORDER BY CustomerID";

		try (Connection connection = Connect.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			customerList.clear();

			while (resultSet.next()) {
				Integer customerID = resultSet.getInt("CustomerID");
				String name = resultSet.getString("Name");
				String address = resultSet.getString("Address");
				String phone = resultSet.getString("Phone");
				String email = resultSet.getString("Email");
				String membership = resultSet.getString("MembershipStatus");

				CustomerManagement customer = new CustomerManagement(customerID, name, address, phone, email,
						membership);
				customerList.add(customer);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void clearFields() {
		nameField.clear();
		addressField.clear();
		phoneField.clear();
		emailField.clear();
		membershipStatusField.clear();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
