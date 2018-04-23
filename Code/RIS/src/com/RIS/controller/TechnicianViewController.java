package com.RIS.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import java.awt.Label;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.RIS.model.Appointment;
import com.RIS.model.Bill;

import application.RISDbConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

public class TechnicianViewController {

    @FXML private TableView<Appointment> techTable;
    @FXML private TableColumn<Appointment, Integer> colPatientID, colUserID;
    @FXML private TableColumn<Appointment, String> colTime, colModality, colEmergencyLevel;
    @FXML private TextField textAreaTechNotes;
    @FXML private Label txtNotes;
    
    File stored;
    private String ID;
    @FXML
    void initialize() { 
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	LocalDate localDate = LocalDate.now();
    	
		techTable.setItems(getAppointmentList(dtf.format(localDate)));
    }

    public ObservableList<Appointment>  getAppointmentList(String date){
    	
    	//Appointment Table
    	colTime.setCellValueFactory(new PropertyValueFactory<Appointment, String>("startTimeToString"));
    	colPatientID.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("patientId"));
    	colUserID.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("userId"));
    	colModality.setCellValueFactory(new PropertyValueFactory<Appointment, String>("modality"));
    	colEmergencyLevel.setCellValueFactory(new PropertyValueFactory<Appointment, String>("emergencyLevel"));
 
    	
    	techTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    	
    	ObservableList<Appointment> appointment = FXCollections.observableArrayList();

        String SQLQuery = "SELECT * FROM appointment WHERE date LIKE %?% AND status='new' ORDER BY startTime ASC;";
       	ResultSet rs = null;

       	try(
       			Connection conn = RISDbConfig.getConnection();
       			PreparedStatement displayappointment = conn.prepareStatement(SQLQuery);
       	){
       		displayappointment.setString(1, date);
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
    
    @FXML
    void displayAppointments(ActionEvent event) {

    }

    @FXML
    void submit(ActionEvent event) {
    	
    	//load image into PACS
    	//
    	//
    	
    	
    	
    	// sets appointment = 'pending' and appends technician notes to physician notes	
    	ObservableList<Appointment> selectedRows;
    	selectedRows = techTable.getSelectionModel().getSelectedItems();
    	
    	String allNotes = "Physician Notes:\n" + selectedRows.get(0).getNotes()
    			+ "\nTechnician Notes:\n" + textAreaTechNotes.getText();
    	
    	String query = "UPDATE appointments SET status = 'pending', notes= ? WHERE appointmentID = ?;";
		try (Connection conn = RISDbConfig.getConnection();
		PreparedStatement updateApp = conn.prepareStatement(query);) {
			updateApp.setString(1, allNotes);
			updateApp.setInt(2, selectedRows.get(0).getAppointmentId());
			updateApp.execute();

		} catch (Exception e) {
			System.out.println("Status: operation failed due to "+e);
			
			}
		
		
    	createBill();
    }
    @FXML
    void browse(ActionEvent event) {
    	imageSelect(event);
    	
    	
    	//set status = "pending" in the database.

    }
    
    @FXML void imageSelect(ActionEvent event) {
    	
    	//Select Image
    	FileChooser fileChooser = new FileChooser();
    	File file = fileChooser.showOpenDialog(null);
    	stored = file;
    }
    
    @FXML void imageSave(ActionEvent event) {
    	//Save Image
    	if (stored != null) {
    		try {
    			saveImage(stored);
    		} catch (IOException ex) {
    			ex.printStackTrace();
    		}
    	}   	    	
    }
    
    public void saveImage(File file) throws IOException {
    	BufferedImage bufferedImage = new BufferedImage(100, 100, 1);
    	stored = new File("images/" + file.getName());
    	try {
    		if (!stored.exists()) {
    			ImageIO.write(bufferedImage, "png", stored);
    		}
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	}   	
    }
    
    
    // Loads physician's notes when row is selected
    void loadNotes(){
    	 ObservableList<Appointment> selectedRows;

         //this gives us the rows that were selected
         selectedRows = techTable.getSelectionModel().getSelectedItems();
         
         txtNotes.setText(selectedRows.get(0).getNotes());
    }
  
    //creates bill
    public Bill createBill(){
    	double cost = 0.0;
    	
    	ObservableList<Appointment> selectedRows;
    	
    	selectedRows = techTable.getSelectionModel().getSelectedItems();
    	
       
    	String query = "SELECT cost FROM modality WHERE modID = ?;";
    	try (Connection conn = RISDbConfig.getConnection();
    			PreparedStatement st = conn.prepareStatement(query);) {
    		st.setInt(1, selectedRows.get(0).getModalityId());
    		ResultSet rs = st.executeQuery();
		
			cost = rs.getDouble("cost");
		
		} catch (Exception e) {
		System.out.println("Status: operation failed due to "+e);
		}
	
        	
        	Bill newBill = new Bill(
        			cost,
        			selectedRows.get(0).getAppointmentId(),
        			selectedRows.get(0).getUserId(),
        			selectedRows.get(0).getPatientId(),
        			selectedRows.get(0).getModalityId()
        			);
        return newBill;
        

    }
    

	public void setID(String text) {
		// TODO Auto-generated method stub
		this.ID = text;
	}

}
