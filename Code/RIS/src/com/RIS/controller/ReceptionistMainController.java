package com.RIS.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.RIS.model.Appointment;
import com.RIS.model.Bill;
import com.RIS.model.Order;
import com.RIS.model.Patient;

import application.RISDbConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ReceptionistMainController {

		@FXML private TableView<Order> tableViewOrder;
		@FXML private TableView<Appointment> tableViewAppointment;
		@FXML private TableView<Bill> tableViewBill;
		
	    @FXML private TableColumn<Order, Integer> colOrderId;
	    @FXML private TableColumn<Order, String> colEmergency, colUserId, colPatientId, colNotesOrder, colModalityorder;
	    @FXML private TableColumn<Appointment, String> colStartTime, colStopTime, colDate, colPatientidApp, colNotesApp;
	    @FXML private TableColumn<Appointment, String> colModality;
	    @FXML private TableColumn<Bill, Integer> billId, appIdBill, modalityIdBill;
	    @FXML private TableColumn<Bill, Double> billCost;
	    @FXML private TableColumn<Bill, String> userIdBill, patientIdBill;
	    private String ID;
	    
	    @FXML
	    void initialize() {  	
	
			//Set Items into all tables
	    	tableViewOrder.setItems(getOrderList());

	    }
	    @FXML
	    public void onSelectedApp() {
			tableViewAppointment.setItems(getAppointmentList()); 

	    }
	    @FXML
	    public void onSelectedBill() {
			tableViewBill.setItems(getBillList());
	    }
	    @FXML
	    public void onSelectedOrder() {
	    	tableViewOrder.setItems(getOrderList());
	    }
	    // ObservableList: A list that enables listeners to track changes when they occur
	    // The following  method will return an ObservableList of  object
	    public ObservableList<Order>  getOrderList(){
	    	
	        //set up the columns in the tables
	    	//Order Table
	    	//colOrderId.setCellValueFactory(new PropertyValueFactory<Order, Integer>("orderID"));
	    	colEmergency.setCellValueFactory(new PropertyValueFactory<Order, String>("emergencyLevel"));
	    	colUserId.setCellValueFactory(new PropertyValueFactory<Order, String>("userId"));
	    	colPatientId.setCellValueFactory(new PropertyValueFactory<Order, String>("patientId"));
	    	colModalityorder.setCellValueFactory(new PropertyValueFactory<Order, String>("modality"));
	    	colNotesOrder.setCellValueFactory(new PropertyValueFactory<Order, String>("notes"));
	    	
			tableViewOrder.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    	
	    	ObservableList<Order> order = FXCollections.observableArrayList();
	        String SQLQuery = "SELECT * FROM orders ORDER BY emergencyLevel;"; //ADD WHERE idPatient == ''
	       	ResultSet rs = null;

	       	try(
	       			Connection conn = RISDbConfig.getConnection();
	       			PreparedStatement displaybill = conn.prepareStatement(SQLQuery);
	       	){
	       		//displayprofile.setInt(1, cutomerId);
	       		rs = displaybill.executeQuery();
	       		// check to see if receiving any data
	       		while (rs.next()){
	       			order.add(new Order(rs.getString("emergencyLevel").toString(),rs.getString("userID").toString(),rs.getString("patientID").toString(), rs.getString("modality"), rs.getString("notes")));
	       		}
	       	}catch(SQLException ex){
	       		RISDbConfig.displayException(ex);
	       		return null;
	       	}finally{
	       		if(rs != null){
	       			//rs.close();
	       		}
	       	}
	        return order;
	    }
	    
	    // ObservableList: A list that enables listeners to track changes when they occur
	    // The following  method will return an ObservableList of  object
	    public ObservableList<Appointment>  getAppointmentList(){
	    	
	    	//Appointment Table
	    	colDate.setCellValueFactory(new PropertyValueFactory<Appointment, String>("date"));
	    	colStartTime.setCellValueFactory(new PropertyValueFactory<Appointment, String>("startTime"));
	    	colStopTime.setCellValueFactory(new PropertyValueFactory<Appointment, String>("stopTime"));
	    	colModality.setCellValueFactory(new PropertyValueFactory<Appointment, String>("modality"));
	    	colPatientidApp.setCellValueFactory(new PropertyValueFactory<Appointment, String>("patientId"));
	    	colNotesApp.setCellValueFactory(new PropertyValueFactory<Appointment, String>("notes"));
	    	
	    	tableViewAppointment.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    	
	    	ObservableList<Appointment> appointment = FXCollections.observableArrayList();

	        String SQLQuery = "SELECT * FROM appointment ORDER BY startTime ASC;"; //ADD WHERE DATE == ''
	       	ResultSet rs = null;

	       	try(
	       			Connection conn = RISDbConfig.getConnection();
	       			PreparedStatement displayappointment = conn.prepareStatement(SQLQuery);
	       	){
	       		rs = displayappointment.executeQuery();
	       		while (rs.next()){
	       			appointment.add(new Appointment(rs.getString("patientID").toString(),rs.getInt("modalityID"),rs.getString("date"), rs.getInt("startTime"), rs.getInt("stopTime"), rs.getString("notes").toString()));
	       		}
	       	}catch(SQLException ex){
	       		RISDbConfig.displayException(ex);
	       		return null;
	       	}finally{
	       		if(rs != null){
	       			//rs.close();
	       		}
	       	}
	        return appointment;
	    }
	    
	    // ObservableList: A list that enables listeners to track changes when they occur
	    // The following  method will return an ObservableList of  object
	    public ObservableList<Bill>  getBillList(){
	    	
	    	//Billing Table
	    	billCost.setCellValueFactory(new PropertyValueFactory<Bill, Double>("Cost"));
	    	appIdBill.setCellValueFactory(new PropertyValueFactory<Bill, Integer>("appId"));
	    	userIdBill.setCellValueFactory(new PropertyValueFactory<Bill, String>("userId"));
	    	patientIdBill.setCellValueFactory(new PropertyValueFactory<Bill, String>("patientId"));
	    	modalityIdBill.setCellValueFactory(new PropertyValueFactory<Bill, Integer>("ModalityId"));
	    	
	    	tableViewBill.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    	
	    	ObservableList<Bill> bill = FXCollections.observableArrayList();

	        String SQLQuery = "SELECT * FROM bills ORDER BY billID ASC;"; //ADD WHERE idPatient == ''
	       	ResultSet rs = null;

	       	try(
	       			Connection conn = RISDbConfig.getConnection();
	       			PreparedStatement displaybill = conn.prepareStatement(SQLQuery);
	       	){
	       		//displayprofile.setInt(1, cutomerId);
	       		rs = displaybill.executeQuery();
	       		// check to see if receiving any data
	       		while (rs.next()){
	       			bill.add(new Bill(rs.getDouble("Cost"),rs.getInt("appID"),rs.getString("userID").toString(),rs.getString("patientID").toString(),rs.getInt("modalityID")));
	       		}
	       	}catch(SQLException ex){
	       		RISDbConfig.displayException(ex);
	       		return null;
	       	}finally{
	       		if(rs != null){
	       			//rs.close();
	       		}
	       	}
	        return bill;
	    }

	    @FXML
	    void NewAppfromOrder(ActionEvent event) throws IOException {
	    	
	        ObservableList<Order> selectedRows;

	        //this gives us the rows that were selected
	        selectedRows = tableViewOrder.getSelectionModel().getSelectedItems();
	        
	    	FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../com/InsulinPump/view/AddNewAppointment.fxml"));
	    	Parent root = (Parent) loader.load();
	        AddNewAppointmentController controller = loader.getController();
	        controller.setID(selectedRows.get(0).getPatientId());
	        controller.setNotes(selectedRows.get(0).getNotes());
	        controller.setUserID(selectedRows.get(0).getUserId());
	        controller.setModality(selectedRows.get(0).getModality());
	        
	        
	        Stage stage = new Stage();
	        stage.setTitle("RIS");
	        stage.setScene(new Scene (root));
	        stage.show();
	       
	    }
		public void setID(String text) {
			// TODO Auto-generated method stub
			this.ID = text;
		}
}
