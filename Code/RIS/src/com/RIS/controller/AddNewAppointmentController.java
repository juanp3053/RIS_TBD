package com.RIS.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import com.RIS.model.Appointment;

import application.RISDbConfig;
import javafx.scene.text.Text;


public class AddNewAppointmentController {

    @FXML private DatePicker txtDate; 
    @FXML private TextField txtId;
    @FXML private TextArea txtNotes;
    @FXML private ComboBox<Integer> comboModality;
    @FXML private ComboBox<Integer> comboHour, comboMinute;
    @FXML private Text txtSuccess;
    private String PatientID, Notes, UserID, modality;
	LocalDate date;
	private int startTime, stopTime;
	private int ModalityId;
	private int appID;
    
    public void initialize() {
    	
    	txtId.setText(PatientID);
    	txtNotes.setText(Notes);

    	comboHour.getItems().removeAll(comboHour.getItems());
    	comboMinute.getItems().removeAll(comboMinute.getItems());
    	
    	for(int hour=1; hour<= 24; hour++)
    		comboHour.getItems().add(hour);
    	
    	comboMinute.getItems().add(00);
    	comboMinute.getItems().add(15);
    	comboMinute.getItems().add(30);
    	comboMinute.getItems().add(45);
    	
    	//For event Edits
    	if(date != null) {
    		txtId.setText(PatientID);
    		txtNotes.setText(Notes);
    		comboHour.getItems().add(startTime/100);
    		comboHour.setValue(startTime/100);
    		comboHour.getItems().add((startTime - ((startTime))/60)*100);
    		comboMinute.setValue((startTime - ((startTime))/60)*100);
    		txtDate.setValue(date);
    		comboModality.getItems().add(ModalityId);
    		comboModality.setValue(ModalityId);
    	}
    }
    public void setComboModality() {
    	if(ModalityId == 0 && date == null) {
	        comboModality.getItems().removeAll(comboModality.getItems());
	    	/*String query = "SELECT modID FROM modality, appointment WHERE modality.modID != appointment.modalityID "
	    			+ "AND name = ? AND date = ? "
	    			+ "AND startTime BETWEEN ? AND (?+duration);";*/
	        String query = "SELECT * FROM modality WHERE name = ? AND NOT EXISTS(SELECT * FROM appointment WHERE modality.modID = appointment.modalityID AND modality.name = ? AND date = ? AND startTime BETWEEN ? AND (? + modality.duration));";
	    	ResultSet rs = null;
	    	
	    	startTime = ((comboHour.getValue()) + (comboMinute.getValue()/60))*100;
	    	
	    	try (Connection conn = RISDbConfig.getConnection();
	    			PreparedStatement st = conn.prepareStatement(query);) {
	    		st.setString(1, modality);
	    		st.setString(2, modality);
	    		st.setString(3, txtDate.getValue().toString());
	    		st.setInt(4, startTime);
	    		st.setInt(5, startTime);
	    		
	    		//System.out.println(st);
	    		rs = st.executeQuery(); 		
	    	    
	            while(rs.next()) {
	            	comboModality.getItems().add(rs.getInt("modID"));
	            }
	    		} catch (Exception e) {
	    			System.out.println("Status: operation failed due to "+e);
	    			}
    	}
    }
 
    public void createAppointment(ActionEvent event){
    	ResultSet rs = null;
    	int duration = 0;
    	
    	startTime = (comboHour.getValue()*100) + (comboMinute.getValue()/60)*100; 
    	
    	//Gets modality ID and duration based on the modality selected in the comboBox
    	String query = "SELECT duration FROM modality WHERE modID = ?;";
    	
    	try (Connection conn = RISDbConfig.getConnection();
    		PreparedStatement dur = conn.prepareStatement(query);) {
    		
    		dur.setInt(1, comboModality.getValue());
    		rs = dur.executeQuery();
    		if(rs.next())
    			duration = rs.getInt("duration");
    		
    		} catch (Exception e) {
    			System.out.println("Status: operation failed due to "+e);
    			}  	
    	
    	stopTime = ((startTime + (duration/60)*100));
    	
    	// creates appointment object. 
    	Appointment newApp = new Appointment(
    			UserID,
    			PatientID,
    			comboModality.getValue(),
    			txtDate.getValue().toString(),
    			startTime,
    			stopTime,
    			Notes
    			);		
    	
   		//parameters-->	Appointment(String userId, String patientId, int modalityId, String startTime, String stopTime)
    		/// insert appointment into database
    		String query2 = "INSERT INTO appointment (userID, patientID, modalityID, date, startTime, stopTime, notes, status) " + "VALUES(?,?,?,?,?,?,?,?)";
    		String query3 = "UPDATE appointment SET (userID = ?, patientID = ?, modalityID = ?, date = ?, startTime = ?, stopTime = ?, notes = ?, statues = ?) WHERE appID = ?";
    		String query4 = "SELECT * FROM appointment WHERE appID = ?;";	
    		
    			try (Connection conn = RISDbConfig.getConnection();
    					PreparedStatement searchapp = conn.prepareStatement(query4);) {
    				
    				searchapp.setInt(1, appID);
    				rs = searchapp.executeQuery();
    				if(rs.next()) {
    					PreparedStatement updateapp = conn.prepareStatement(query3);
    					
    					updateapp.setString(1, newApp.getUserId());
    					updateapp.setString(2, newApp.getPatientId());
    					updateapp.setInt(3, newApp.getModalityId());
    					updateapp.setString(4, newApp.getDate());
    					updateapp.setInt(5, newApp.getStartTime());
    					updateapp.setInt(6, newApp.getStopTime());
    					updateapp.setString(7, newApp.getNotes());
    					updateapp.setString(8, newApp.getStatus());
    					updateapp.setInt(9, appID);
    				}
    				else {
    					PreparedStatement insertprofile = conn.prepareStatement(query2);
    					insertprofile.setString(1, newApp.getUserId());
    					insertprofile.setString(2, newApp.getPatientId());
    					insertprofile.setInt(3, newApp.getModalityId());
    					insertprofile.setString(4, newApp.getDate());
    					insertprofile.setInt(5, newApp.getStartTime());
    					insertprofile.setInt(6, newApp.getStopTime());
    					insertprofile.setString(7, newApp.getNotes());
    					insertprofile.setString(8, newApp.getStatus());

    					insertprofile.execute();
    				}
    					txtSuccess.setText("Success! Appointment has been created.");
    					
    				} catch (Exception e) {
    					System.out.println("Status: operation failed due to "+e);
    					}    
    }

	public void setID(String id) {
		this.PatientID = id;
    	txtId.setText(id);
	}

	public void setNotes(String notes) {
		this.Notes = notes;
    	txtNotes.setText(notes);
	}

	public void setUserID(String userId) {
		this.UserID = userId;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}
	
	public void setDate(String date) {
		LocalDate ndate = LocalDate.parse(date);
		this.date = ndate;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public void setStopTime(int stopTime) {
		this.stopTime = stopTime;
	}
	public void setModalityId(int modalityId) {
		this.ModalityId = modalityId;
	}
	public void setAppID(int appointmentId) {
		this.appID = appointmentId;
		
	}	
}