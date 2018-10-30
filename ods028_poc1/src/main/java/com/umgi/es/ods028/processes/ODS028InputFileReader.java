package com.umgi.es.ods028.processes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transport.PropertyScope;

import com.umgi.es.ods028.constants.ODS028Constants;

import com.umgi.es.ods028.vo.OdsComponentInventor;


public class ODS028InputFileReader implements Callable{

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		// TODO Auto-generated method stub
		MuleMessage message = eventContext.getMessage();
		readFile(message);
		return message;
	}
	public void readFile(MuleMessage message) throws IOException {

		//String fileName = message.getProperty(ODS028Constants.VAR_INPUTFILE,
		//		PropertyScope.INVOCATION);

		  String directory = message.getProperty("moveToDirectory", PropertyScope.INVOCATION);
		  String filename1 = message.getProperty("filename", PropertyScope.INBOUND);
		  
		  String fileName =directory+"\\"+filename1 ;
		String countryCodeFromFileName = fileName.substring(fileName.lastIndexOf(File.separator)).split("\\.")[1];
		message.setProperty("countryCodeFromFileName",
				countryCodeFromFileName, PropertyScope.INVOCATION);
		System.out.println(countryCodeFromFileName);
		String line = null;
		List<OdsComponentInventor> totalList = new ArrayList<OdsComponentInventor>();
		String query = "";
		Integer auditLineCount = null;

		BufferedReader reader = null;
		int lineNumber = 0;
		try {

			InputStream inputStream = new FileInputStream(new File(fileName));
			reader = new BufferedReader(new InputStreamReader(inputStream,
					"UTF8"));
			line = reader.readLine();
			// Remove BOM from first line if exists
			if (line != null && line.startsWith(ODS028Constants.UTF8_BOM))
				line = line.substring(1);
			while (line != null) {

				String[] lineItems = line.trim().split(
						ODS028Constants.FIELD_DELIMITER_SEMICOLON, -1);
				// RecordType recordId = RecordType.toRecordType(lineItems[0]);
				switch (lineItems.length) {

				case 11:
					// The field length for the lines is 23
					lineNumber++;
					OdsComponentInventor sbVo = buildLineItemVO(lineItems, countryCodeFromFileName);					
					totalList.add(sbVo);
					break;

				case 1:
					// The field length for the header is 4 that isn't needed to
					// consider
					break;

				case 4:
					// The field length for the TAudit is 1
					String noOfTAudit = lineItems[3];
					auditLineCount = Integer.parseInt(noOfTAudit.trim());
					break;

				default:
					String errorMessage = String
							.format("Invalid File - Invalid Record Type at Line Number: %d, Line is: %s ",
									lineNumber + 1, line);
					
				}
				line = reader.readLine();
			}			 
			message.setProperty(ODS028Constants.VAR_TAUDIT_LINES_COUNT,
					auditLineCount, PropertyScope.INVOCATION);
			message.setProperty(ODS028Constants.VAR_TOTAL_LINE_COUNT,
					lineNumber, PropertyScope.INVOCATION);
			message.setProperty(ODS028Constants.VAR_TOTAL_LIST, totalList,
					PropertyScope.INVOCATION);
			message.setProperty(ODS028Constants.VAR_QUERY_BUILDER, query,
					PropertyScope.INVOCATION);
			// Check whether Header Audit and Trailer Audit records are present
		
		} catch (Exception e) {}
		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {

				}
			}

		}

	}

	// buildLineItemVO is used to read the fields from the lines
	public OdsComponentInventor buildLineItemVO(String[] fieldList, String countryCodeFromFileName)
			throws Exception {

		OdsComponentInventor purcVo = new OdsComponentInventor();

		String plant = fieldList[1];
		String countryCode = fieldList[2];
		String manufacturerId = countryCodeFromFileName+fieldList[3];
		String componentId = fieldList[4];
		String stockOnHandQt = fieldList[5];
		String reservedQt = fieldList[6];
		String qualityCheckQt = fieldList[7];
		String qtOnDispatchNote = fieldList[8];
		String stockAvailableQt = fieldList[9];
		String orderQt = fieldList[10];

		purcVo.setPlant(plant);
		purcVo.setCountryCode(countryCode);
		purcVo.setManufacturerId(manufacturerId);
		purcVo.setComponentId(componentId);
		purcVo.setStockOnHandQt(stockOnHandQt);
		purcVo.setReservedQt(reservedQt);
		purcVo.setQualityCheckQt(qualityCheckQt);
		purcVo.setQtOnDispatchNote(qtOnDispatchNote);
		purcVo.setStockAvailableQt(stockAvailableQt);
		purcVo.setOrderQt(orderQt);
		

		return purcVo;
	}
}
