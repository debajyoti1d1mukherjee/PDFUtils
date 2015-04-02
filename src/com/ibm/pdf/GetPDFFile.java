package com.ibm.pdf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ibm.pdf.bean.PDFBean;
import com.ibm.pdf.dao.PDFDao;
import com.ibm.pdf.dao.PDFDaoImpl;
import com.ibm.websphere.objectgrid.ClientClusterContext;
import com.ibm.websphere.objectgrid.ObjectGrid;
import com.ibm.websphere.objectgrid.ObjectGridManager;
import com.ibm.websphere.objectgrid.ObjectGridManagerFactory;
import com.ibm.websphere.objectgrid.ObjectMap;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.security.config.ClientSecurityConfiguration;
import com.ibm.websphere.objectgrid.security.config.ClientSecurityConfigurationFactory;
import com.ibm.websphere.objectgrid.security.plugins.builtins.UserPasswordCredentialGenerator;

public class GetPDFFile extends HttpServlet{
	
	private PDFDao pdfDao = new PDFDaoImpl();
	

	Session ogSession = null;

	String VCAP_SERVICES = null;

	public void init() throws ServletException {
		// Do required initialization
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String userEmail = request.getParameter("userEmail");
		System.out.println("User Email:" + userEmail); 
		
		byte[] writeBytes = new byte[] {};
		try {
			ObjectMap map = getCacheSession().getMap("sample.NONE.P");
			
			PDFBean pdfBean =  (PDFBean) map.get(userEmail);
			System.out.println("PDFBEAN"+pdfBean);
			if(pdfBean!=null){
				System.out.println("Getting from cache");
				writeBytes =pdfBean.getFileContent();
			} else{
				System.out.println("Getting from DB");
				List<PDFBean> userList = pdfDao.getUserDetailsByEmail(userEmail);
				Iterator<PDFBean> iterator = userList.iterator();
			
				while (iterator.hasNext()) {
					PDFBean retrievedPDFBean = iterator.next();
					writeBytes = retrievedPDFBean.getFileContent();
					map.upsert(userEmail, retrievedPDFBean);
				}
			}
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
        if (writeBytes != null) {
            String contentType = "application/pdf";
            String fileName = "pdffile.pdf";
            // set pdf content
            response.setContentType("application/pdf");
            // if you want to download instead of opening inline
            // response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            // write the content to the output stream
            BufferedOutputStream fos = new BufferedOutputStream(
                response.getOutputStream());
            fos.write(writeBytes);
            fos.flush();
            fos.close();
        }  
		
        
        /*String filePath ="C:\\Users\\IBM_ADMIN\\Documents\\Indus_Ind_CC_Redemption.pdf";
        
        File f = new File(filePath);

        try {
            //OutputStream os = response.getOutputStream();
            InputStream is = new FileInputStream(f);
            byte[] bytes = IOUtils.toByteArray(is);
            //os.write(bytes);
            //os.flush();
           
            // int c = 0;
            //while ((c = is.read(buf, 0, buf.length)) > 0) {
              //  os.write(buf, 0, c);
                //os.flush();
            //}
            //os.close();
            is.close();
            
            String contentType = "application/pdf";
            String fileName = "pdffile.pdf";
            // set pdf content
            response.setContentType("application/pdf");
            // if you want to download instead of opening inline
            // response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            // write the content to the output stream
            BufferedOutputStream fos = new BufferedOutputStream(
                response.getOutputStream());
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
		
		/*// fetch pdf
        byte[] pdf = new byte[] {}; // Load PDF byte[] into here
        if (pdf != null) {
            String contentType = "application/pdf";
            byte[] ba1 = new byte[1024];
            String fileName = "pdffile.pdf";
            // set pdf content
            response.setContentType("application/pdf");
            // if you want to download instead of opening inline
            // response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            // write the content to the output stream
            BufferedOutputStream fos1 = new BufferedOutputStream(
                response.getOutputStream());
            fos1.write(ba1);
            fos1.flush();
            fos1.close();
        }  
		*/
	}
	public void destroy() {
		// do nothing.
	}
	
	
	private Session getCacheSession() throws Exception {
		String username = "";
		String password = "";
		String endpoint="";
		String gridName="";		
		VCAP_SERVICES = System.getenv("VCAP_SERVICES");

		JSONObject obj = new JSONObject(VCAP_SERVICES);
		String[] names = JSONObject.getNames(obj);
		if (names != null) {
			for (String name : names) {
				if (name.startsWith("DataCache")) {
					JSONArray val = obj.getJSONArray(name);
					JSONObject serviceAttr = val.getJSONObject(0);
					JSONObject credentials = serviceAttr
							.getJSONObject("credentials");
					username = credentials.getString("username");
					password = credentials.getString("password");
					endpoint = credentials.getString("catalogEndPoint");
					gridName = credentials.getString("gridName");
					System.out
							.println("Found configured username: " + username);
					System.out
							.println("Found configured password: " + password);
					System.out
							.println("Found configured endpoint: " + endpoint);
					System.out
							.println("Found configured gridname: " + gridName);

					break;
				}
			}
		}
		ObjectGridManager ogm = ObjectGridManagerFactory.getObjectGridManager();
		ClientSecurityConfiguration csc = null;
		csc = ClientSecurityConfigurationFactory
				.getClientSecurityConfiguration();
		csc.setCredentialGenerator(new UserPasswordCredentialGenerator(
				username, password));
		csc.setSecurityEnabled(true);

		ClientClusterContext ccc = ogm.connect(endpoint, csc, null);

		ObjectGrid clientGrid = ogm.getObjectGrid(ccc, gridName);
		ogSession = clientGrid.getSession();
		return ogSession;
	}
}
