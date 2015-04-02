package com.ibm.pdf.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ibm.pdf.bean.PDFBean;
import com.ibm.pdf.dao.connection.SQLDBConnection;

public class PDFDaoImpl implements PDFDao{
	
	public List<PDFBean> getUserDetails(){
		
		List<PDFBean> userDetails = new ArrayList<PDFBean>();
		try {
		Connection myConnection = SQLDBConnection
				.getSQLDBConnectionInstance().getConnection();
		
		Statement stmt = myConnection.createStatement();
		String selectSql = "SELECT * FROM PDFUSER";
		ResultSet rs = stmt.executeQuery(selectSql);

		// Process the result set
		while (rs.next()) {
			String userName = rs.getString(1);
			String userEmail = rs.getString(2);
			byte[] userFile = rs.getBytes(3);
			
			PDFBean pdfBean = new PDFBean();
			pdfBean.setUserName(userName);
			pdfBean.setUserEmail(userEmail);
			pdfBean.setFileContent(userFile);
			
			userDetails.add(pdfBean);
		}
		// Close the ResultSet
		rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			
		}
		return userDetails;
	}
	
public List<PDFBean> getUserDetailsByEmail( String userEmail){
		
		List<PDFBean> userDetails = new ArrayList<PDFBean>();
		try {
		Connection myConnection = SQLDBConnection
				.getSQLDBConnectionInstance().getConnection();
		
		Statement stmt = myConnection.createStatement();
		String selectSql = "SELECT * FROM PDFUSER WHERE USEREMAIL='"+userEmail+"'";
		ResultSet rs = stmt.executeQuery(selectSql);

		// Process the result set
		while (rs.next()) {
			String userName = rs.getString(1);
			String email = rs.getString(2);
			byte[] userFile = rs.getBytes(3);
			
			PDFBean pdfBean = new PDFBean();
			pdfBean.setUserName(userName);
			pdfBean.setUserEmail(email);
			pdfBean.setFileContent(userFile);
			
			userDetails.add(pdfBean);
		}
		// Close the ResultSet
		rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			
		}
		return userDetails;
	}
	
	public void insertUserDetails(PDFBean pdfBean) {

		try {
			Connection myConnection = SQLDBConnection
					.getSQLDBConnectionInstance().getConnection();

			String insertSQL = "INSERT INTO PDFUSER"
					+ "(USERNAME, USEREMAIL,USERFILE) VALUES" + "(?,?,?)";
			PreparedStatement preparedStatement = myConnection
					.prepareStatement(insertSQL);
			preparedStatement.setString(1, pdfBean.getUserName());
			preparedStatement.setString(2, pdfBean.getUserEmail());
			preparedStatement.setBytes(3, pdfBean.getFileContent());
			// execute insert SQL stetement
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
