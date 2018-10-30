package com.umgi.es.ods028.processes;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transport.PropertyScope;



public class Read_file implements Callable {
public Object onCall(MuleEventContext eventContext) throws Exception {
		
		// TODO Auto-generated method stub
		 MuleMessage message = eventContext.getMessage();
		
		 
		  String directory = message.getProperty("moveToDirectory", PropertyScope.INVOCATION);
		  String filename = message.getProperty("originalFilename", PropertyScope.INVOCATION);
		  
		  String resourceId =directory+"/"+filename ;
			 
			 
		
		  System.out.println("The directory name is"+directory);
		 System.out.println("The file name is"+filename);
		 System.out.println("The Resource id is"+(directory+"/"+filename));
		 System.out.println(resourceId);
		
		return message;
		 
	}
}


