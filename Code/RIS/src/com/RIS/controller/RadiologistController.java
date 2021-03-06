package com.RIS.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import com.RIS.model.Appointment;
import com.RIS.model.PACS;
import com.RIS.model.Patient;
import com.RIS.model.Transcript;

import application.RISDbConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RadiologistController {

    @FXML private TableView<Appointment> tableViewApp;
    @FXML private TableView<PACS> tableViewPacs;

    @FXML private TableColumn<PACS, Integer> colImageId;
    @FXML private TableColumn<PACS, String> colImagePath;
    @FXML private TableColumn<Appointment, String> colPatientId;
    @FXML private TableColumn<Appointment, String> colModality;
    @FXML private TableColumn<Appointment, String> colDate;
    @FXML private TableColumn<Appointment, Integer> colAppId;
    @FXML private Label txtReady;
    @FXML private TextArea lblTranscript;
    @FXML private Text lblDOB, lblLastName, lblFirstName, lblPatientId, lblNotesPatient, lblGender, lblNotesApp, lblStopTime, lblStartTime, lblDate, lblModality;
    private String ID;
    
    @FXML
    public void initialize() {
    	tableViewApp.setItems(refreshApp());
    }
    @FXML
    void openImage(ActionEvent event) {
    	
    	Desktop desktop = Desktop.getDesktop();
    	ObservableList<PACS> selectedRows;
    	
    	selectedRows = tableViewPacs.getSelectionModel().getSelectedItems();
    	//URL urlToImage = this.getClass().getResource(selectedRows.get(0).getImage());
    	File file = new File(selectedRows.get(0).getImage());
    			
    	try {
    		desktop.open(file);
			//ImageIO.read(urlToImage);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML
    void submitReport(ActionEvent event) {
    	
        ObservableList<Appointment> selectedRows;
        
        //this gives us the rows that were selected
        selectedRows = tableViewApp.getSelectionModel().getSelectedItems();
    	
    	Transcript newTranscript = new Transcript(lblTranscript.getText(),
						selectedRows.get(0).getAppointmentId(),
						selectedRows.get(0).getPatientId(),
						selectedRows.get(0).getUserId(),
						selectedRows.get(0).getModalityId());
		
		String query = "INSERT INTO transcripts " + "(transcript, appointment_appID, appointment_userID, appointment_patientID, appointment_modalityID) " + "VALUES (?,?,?,?,?)";
		String queryapp = "UPDATE appointment SET status = 'completed' WHERE appID = ? ;";
		
		try (Connection conn = RISDbConfig.getConnection();
		PreparedStatement insertTranscript = conn.prepareStatement(query);PreparedStatement updateApp = conn.prepareStatement(queryapp);) {
			
			insertTranscript.setString(1, newTranscript.getTranscript());
			insertTranscript.setInt(2, newTranscript.getAppointmentId());
			insertTranscript.setString(3, newTranscript.getUserId());
			insertTranscript.setString(4, newTranscript.getPatientId());
			insertTranscript.setInt(5, newTranscript.getModalityId());
			
			updateApp.setInt(1, newTranscript.getAppointmentId());
			
			//Changes appointment Status to Completed 
			insertTranscript.execute();
			updateApp.execute();
			txtReady.setText("Report has been Submited");
			
			//Set everything to blank
	        lblModality.setText("");
	        lblDate.setText("");
	        lblStartTime.setText("");
	        lblStopTime.setText("");
	        lblNotesApp.setText("");
            lblPatientId.setText("");
            lblFirstName.setText("");
            lblLastName.setText("");
            lblDOB.setText("");
            lblGender.setText("");
            lblNotesPatient.setText("");
            refreshApp();
	        
	        
		} catch (Exception e) {
		System.out.println("Status: operation failed due to "+e);
		
		}
    }

    @FXML
    public ObservableList<Appointment> refreshApp() {
    	
    	 //set up the columns in the table
    	colAppId.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("appointmentId"));
    	colPatientId.setCellValueFactory(new PropertyValueFactory<Appointment, String>("patientId"));
    	colDate.setCellValueFactory(new PropertyValueFactory<Appointment, String>("date"));
    	colModality.setCellValueFactory(new PropertyValueFactory<Appointment, String>("modalityId"));
    	
    	tableViewPacs.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    	tableViewApp.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    	
    	ObservableList<Appointment> appointment = FXCollections.observableArrayList();
        String SQLQuery = "SELECT * FROM appointment WHERE status='pending' ORDER BY appID;";
       	ResultSet rs = null;

       	try(
       			Connection conn = RISDbConfig.getConnection();
       			PreparedStatement displayapp = conn.prepareStatement(SQLQuery);
       	){
       		rs = displayapp.executeQuery();
       		// check to see if receiving any data
       		while (rs.next()){
       			appointment.add(new Appointment(rs.getInt("appID"), rs.getString("userID"), rs.getString("patientID").toString(),rs.getInt("modalityID"),rs.getString("date").toString(), rs.getInt("startTime"), rs.getInt("stopTime"), rs.getString("notes").toString()));
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
    public void loadData() {
    	
        ObservableList<Appointment> selectedRows;

        //this gives us the rows that were selected
        selectedRows = tableViewApp.getSelectionModel().getSelectedItems();
        
        //Load Appointment Details
        lblModality.setText(selectedRows.get(0).getModalityId()+"");
        lblDate.setText(selectedRows.get(0).getDate());
        lblStartTime.setText(selectedRows.get(0).getStartTime()+"");
        lblStopTime.setText(selectedRows.get(0).getStopTime()+"");
        lblNotesApp.setText(selectedRows.get(0).getNotes());
        
        String SQLQuery = "SELECT * FROM patient WHERE PatientID = ?;";
       	ResultSet rs = null;

       	try(
       			Connection conn = RISDbConfig.getConnection();
       			PreparedStatement displaypatient = conn.prepareStatement(SQLQuery);
       	){
       		displaypatient.setString(1, selectedRows.get(0).getPatientId());
       		
       		rs = displaypatient.executeQuery();
       		if(rs.next()) {
       			Patient newPatient = new Patient(rs.getString("patientID").toString(), rs.getString("firstName").toString(),rs.getString("lastName").toString(),rs.getString("gender").toString(), rs.getString("DOB").toString(), rs.getString("insurance").toString(), rs.getString("address").toString(), rs.getString("phone").toString(), rs.getString("email").toString(), rs.getString("notes").toString());
       		
       		//Load Patient Info
            lblPatientId.setText(newPatient.getidPatient());
            lblFirstName.setText(newPatient.getFirstName());
            lblLastName.setText(newPatient.getLastName());
            lblDOB.setText(newPatient.getDOB());
            lblGender.setText(newPatient.getGender());
            lblNotesPatient.setText(newPatient.getNotes());
       		
            //Load Images
       		tableViewPacs.setItems(LoadImages(newPatient.getidPatient(), selectedRows.get(0).getAppointmentId()));
       		}
       	}catch(SQLException ex){
       		RISDbConfig.displayException(ex);
       	}
    	
    }
    public ObservableList<PACS> LoadImages(String patientId, int appointmentId) {
    	
    	colImageId.setCellValueFactory(new PropertyValueFactory<PACS, Integer>("imageId"));
    	tableViewPacs.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    	
    	ObservableList<PACS> pacs = FXCollections.observableArrayList();
        String SQLQuery = "SELECT * FROM pacs WHERE appointment_patientID = ? AND appointment_appID = ? ORDER BY imageID;";
       	ResultSet rs = null;

       	try(
       			Connection conn = RISDbConfig.getConnection();
       			PreparedStatement displayapp = conn.prepareStatement(SQLQuery);
       	){
       		displayapp.setString(1, patientId);
       		displayapp.setInt(2, appointmentId);
       		
       		rs = displayapp.executeQuery();
       		// check to see if receiving any data
       		while (rs.next()){
       			pacs.add(new PACS(rs.getInt("imageID"), rs.getInt("appointment_appID"),rs.getString("appointment_patientID").toString(),rs.getString("image").toString()));
       		}
       	}catch(SQLException ex){
       		RISDbConfig.displayException(ex);
       		return null;
       	}finally{
       		if(rs != null){
       			//rs.close();
       		}
       	}
        return pacs;
    }
    
    public void logout(ActionEvent event) throws IOException
    {
        Parent tableViewParent = FXMLLoader.load(getClass().getResource("../../../com/RIS/view/LoginPage.fxml"));
        Scene tableViewScene = new Scene(tableViewParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setTitle("RIS Home");
        window.setScene(tableViewScene);
        window.show();
    	
    }
    
	public void setID(String text) {
		this.ID = text;	
	}
	

}
