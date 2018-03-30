package com.RIS.model;
import javafx.beans.property.SimpleStringProperty;

public class Bill {

	private SimpleStringProperty userId, patientId;
	private int billId, appointmentId, modalityId;
	private double cost;
	
	public Bill(int billId, double cost, int appointmentId, String userId, String patientId, int modalityId) {
		this.billId  = billId;
		this.cost = cost;
		this.appointmentId = appointmentId;
		this.userId  = new SimpleStringProperty(userId);
		this.patientId  = new SimpleStringProperty(patientId);
		this.modalityId  = modalityId;	
	}
	
	/* Start of GETTERS AND SETTERS */
	public int getBillId() {
		return billId;
	}

	public void setBillId(int billId) {
		this.billId = billId;
	}

	public double getCost() {
		return cost;
	}
	
	public int getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(int appointmentId) {
		this.appointmentId = appointmentId;
	}
	
	public String getUserId() {
		return userId.get();
	}
	
	public void setUserId(String userId) {
		this.userId = new SimpleStringProperty(userId);
	}

	public String getPatientId() {
		return patientId.get();
	}

	public void setPatientId(String patientId) {
		this.patientId = new SimpleStringProperty(patientId);
	}
	
	public int getModalityId() {
		return modalityId;
	}

	public void setModalityId(int modalityId) {
		this.modalityId = modalityId;
	}

	@Override
	public String toString() {
		return "Bill [userId=" + userId + ", patientId=" + patientId + ", billId=" + billId + ", appointmentId="
				+ appointmentId + ", modalityId=" + modalityId + ", cost=" + cost + "]";
	}
	
}
