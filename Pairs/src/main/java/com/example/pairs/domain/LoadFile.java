package com.example.pairs.domain;

import com.opencsv.bean.CsvBindByPosition;

public class LoadFile {
	
	@CsvBindByPosition(position = 0)
	public int empId;
	@CsvBindByPosition(position = 1)
	public int projectId;
	@CsvBindByPosition(position = 2)
	public String dateFrom;
	@CsvBindByPosition(position = 3)
	public String dateTo;
	
	public int getEmpId() {
		return empId;
	}
	public void setEmpId(int empId) {
		this.empId = empId;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public String getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}
	public String getDateTo() {
		return dateTo;
	}
	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}
	

	

	
	
	

}
