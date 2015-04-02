package com.ibm.pdf.dao;

import java.util.List;

import com.ibm.pdf.bean.PDFBean;

public interface PDFDao {

	public List<PDFBean> getUserDetails();
	
	public void insertUserDetails(PDFBean pdfBean);
	
	public List<PDFBean> getUserDetailsByEmail( String userEmail);
}
