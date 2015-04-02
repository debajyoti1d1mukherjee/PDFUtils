package com.ibm.pdf;



//Import required java libraries
import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.ibm.pdf.bean.PDFBean;
import com.ibm.pdf.dao.PDFDao;
import com.ibm.pdf.dao.PDFDaoImpl;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;


//Extend HttpServlet class
public class SubmitPDFForm extends HttpServlet {

	private String message;
	
	private PDFDao pdfDao = new PDFDaoImpl();

	public void init() throws ServletException {
		// Do required initialization
		message = "Pdf uploader";
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Set response content type
		response.setContentType("text/html");

		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println("<h1> User Details</h1>");
		
		//ServletContext servletContext = getServletContext();
		//String contextPath = servletContext.getRealPath(File.separator);
		//out.println("<br/>File system context path (in TestServlet): " + contextPath);

		out.println("<table border='1'>");
		out.println("<tr>");
		out.println("<td>Name</td>");
		out.println("<td>Email</td>");
		out.println("<td>Pdf File</td>");
		out.println("</tr>");
		
		
		List<PDFBean> userList = pdfDao.getUserDetails();
		if(userList.isEmpty()){
			out.println("<tr>");
			out.println("<td colspan='3'>No Records are present</td>");
			out.println("</tr>");
		}	
		Iterator<PDFBean> iterator = userList.iterator();
		while (iterator.hasNext()) {
			PDFBean pdfBean = iterator.next();
			
			out.println("<tr>");
			out.println("<td>"+pdfBean.getUserName()+"</td>");
			out.println("<td>"+ pdfBean.getUserEmail()+"</td>");
			out.println("<td><a href='getpdf?userEmail="+pdfBean.getUserEmail()+"' target='_blank'>View PDF</a></td>");
			out.println("</tr>");
			
		/*	byte[] writeBytes = pdfBean.getFileContent();
			 
	        //below is the different part
	        File someFile = new File(contextPath+pdfBean.getUserEmail()+".pdf");
	        FileOutputStream fos = new FileOutputStream(someFile);
	        fos.write(writeBytes);
	        fos.flush();
	        fos.close();*/
		}	
		out.println("</table>");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = null;
		String email = null;
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		  try {
			if (isMultipart) {
				//Create PDF Bean
				PDFBean pdfBean = new PDFBean();
				
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> formItems = upload.parseRequest(request);
				// Process the uploaded items
				Iterator<FileItem> iter = formItems.iterator();
				while (iter.hasNext()) {
					FileItem item = iter.next();

					if (item.isFormField()) {
						String fieldName = item.getFieldName();
						if (fieldName.equalsIgnoreCase("name")) {
							name = item.getString();
							pdfBean.setUserName(name);
						} else if (fieldName.equalsIgnoreCase("email")) {
							email = item.getString();
							pdfBean.setUserEmail(email);
						}
					} else {
						 InputStream uploadedStream = item.getInputStream();
						 byte[] bytes = IOUtils.toByteArray(uploadedStream);
						 System.out.println(Arrays.toString(bytes));
						//byte[] data = item.get();
						//System.out.println(Arrays.toString(data));
						 pdfBean.setFileContent(bytes);
					}
				}
				pdfDao.insertUserDetails(pdfBean);
				sendEmail(pdfBean.getUserEmail());
				
			}
	        
	    } catch (FileUploadException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

		response.setContentType("text/html");

		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println("<h1> User : " + email + " has uploaded a pdf file</h1>");
		out.println("<br>");
		out.println("<a href='index.jsp'>Home</a>");
		//out.println("<h1> File Content " + bytes + "</h1>");
	}

	private void sendEmail(String emailId) {
		SendGrid sendgrid = new SendGrid("c1xZLbFMkx", "TfLcqaKKdH");// username,
																		// password

		SendGrid.Email email = new SendGrid.Email();
		email.addTo(emailId);
		email.setFrom("bluemix@in.ibm.com");
		email.setSubject("PDF uploaded");
		email.setText("Thanks for using Bluemix to upload a PDF!!!");

		try {
			SendGrid.Response response = sendgrid.send(email);
			System.out.println(response.getMessage());
		} catch (SendGridException e) {
			System.err.println(e);
		}
	}

	public void destroy() {
		// do nothing.
	}
}
