import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//Muhammad Afdi - 2502037782 - BAD SP

public class search extends Application {

	private TextField locationField, minTransactionField;
	private TableView<CustomerManagement> customerTable;
	private ObservableList<CustomerManagement> customerData;

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
		primaryStage.setTitle("Customer Search");

		locationField = new TextField();
		minTransactionField = new TextField();

		Label locationLabel = new Label("Location:");
		Label minTransactionLabel = new Label("Min Transaction Amount:");

		Button searchButton = new Button("Search");
		searchButton.setOnAction(e -> searchCustomers());

		customerTable = new TableView<>();
		customerData = FXCollections.observableArrayList();

		TableColumn<CustomerManagement, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("nameCust"));

		TableColumn<CustomerManagement, String> addressCol = new TableColumn<>("Address");
		addressCol.setCellValueFactory(new PropertyValueFactory<>("addressCust"));

		TableColumn<CustomerManagement, String> phoneCol = new TableColumn<>("Phone");
		phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneCust"));

		TableColumn<CustomerManagement, String> emailCol = new TableColumn<>("Email");
		emailCol.setCellValueFactory(new PropertyValueFactory<>("emailCust"));

		TableColumn<CustomerManagement, String> membershipCol = new TableColumn<>("Membership");
		membershipCol.setCellValueFactory(new PropertyValueFactory<>("membershipCust"));

		customerTable.getColumns().addAll(nameCol, addressCol, phoneCol, emailCol, membershipCol);
		customerTable.setItems(customerData);

		// Tampilan UI JavaFX
		GridPane inputGrid = new GridPane();
		inputGrid.setPadding(new Insets(10));
		inputGrid.setHgap(10);
		inputGrid.setVgap(10);

		inputGrid.add(locationLabel, 0, 0);
		inputGrid.add(locationField, 1, 0);
		inputGrid.add(minTransactionLabel, 0, 1);
		inputGrid.add(minTransactionField, 1, 1);
		inputGrid.add(searchButton, 1, 2);

		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));
		vbox.getChildren().addAll(inputGrid, customerTable);
		vbox.setStyle("-fx-background-color: #FFFFE0;");

		Scene scene = new Scene(vbox, 600, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void searchCustomers() {
		String location = locationField.getText().trim();
		String minTransaction = minTransactionField.getText().trim();

		if (location.isEmpty() || minTransaction.isEmpty()) {
			showAlert(Alert.AlertType.WARNING, "GAGAL", "Isi semua field.");
			return;
		}

		double minAmount;
		try {
			minAmount = Double.parseDouble(minTransaction);
		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.WARNING, "Gagal", "Invalid jumlah transaksi.");
			return;
		}

		customerData.clear();

		String query = "SELECT c.CustomerID, c.Name, c.Address, c.Phone, c.Email, c.MembershipStatus "
				+ "FROM Customer c " + "JOIN Sales s ON c.CustomerID = s.CustomerID "
				+ "WHERE c.Address = ? AND s.TotalAmount >= ?";

		try (Connection connection = Connect.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setString(1, location);
			preparedStatement.setDouble(2, minAmount);

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				CustomerManagement customer = new CustomerManagement(resultSet.getInt("CustomerID"),
						resultSet.getString("Name"), resultSet.getString("Address"), resultSet.getString("Phone"),
						resultSet.getString("Email"), resultSet.getString("MembershipStatus"));
				customerData.add(customer);
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
