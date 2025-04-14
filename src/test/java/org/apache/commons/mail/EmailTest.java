//triggering CI with a comment.
package org.apache.commons.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;



import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import static org.easymock.EasyMock.*;

import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

//import org.apache.commons.mocks.MockSmtpServer;
//import org.apache.commons.configs.MockSmtpServer;

public class EmailTest {


		//test email list
		private static final String[] TEST_EMAILS = { "ab@bc.com", "a.b@c.org", "abcdefghijklmnopqrst@abcdefghijklmnopqrst.com.bd"};
		//test valid char list
		private String[] testValidChars = { " ", "a", "A", "\uc5ec", "0123456789", "0123456780123456790", "\n" };
		//test email string
		private static final String testEmail = "sabreen@gmail.com";
		//concrete email class for testing
		private EmailConcrete email;
		//private MockSmtpServer mockSmtpServer;
		//the session to mail with
	    private Session mySession;
	    //date to test with
		private Date testDate;
		//subject to test with
		//private MimeMessage mimeMessageMock;
		 /** socket connection timeout value in milliseconds. */
		private static final int SOCKET_TIMEOUT_MS = 60000;
	
		@Before
		public void setUpEmailTest() throws Exception {
			
			email = new EmailConcrete();
			
			//mimeMessageMock = createMock(MimeMessage.class);
		
			//set up session
			//mySession = Session.getInstance(new Properties());
		    //email.setMailSession(mySession);
	   

			
			//mockSmtpServer = new MockSmtpServer(EmailConfiguration.MOCK_SMTP_PORT);
			
			//set up calender for set date function
			Calendar calendar = Calendar.getInstance();
	        calendar.set(2025, Calendar.MARCH, 8);
	        testDate = calendar.getTime();
		}
		
		@After
		public void tearDownEmailTest() throws Exception
		{
			//mockSmtpServer.stopSmtpMailServer();
		}

		//Test addBcc(String... emails) function
		@Test
		public void testAddBcc() throws Exception
		{
			
			email.addBcc(TEST_EMAILS);
			assertEquals(3, email.getBccAddresses().size());
			
		}
		//Test addCc(String email) function
		@Test
		public void testAddCc() throws Exception
		{
			 email.addCc("ab@bc.com");
			 email.addCc("sabreen@gmail.com");
			 email.addCc("sabreen@yahoo.com");
			 email.addCc("sabreen@outlook.com");
			 assertEquals(4, email.getCcAddresses().size());
		}
		
		//Test addHeader(String name, String value) function
		@Test
		public void testAddHeader() throws Exception
		{
			 // valid headers
			 email.addHeader("From", testEmail);
			 email.addHeader("To", "professor@umich.edu");
			// test valid header "From"
			 assertEquals(testEmail, email.getHeaders().get("From"));
			// test valid header "To"
			 assertEquals("professor@umich.edu", email.getHeaders().get("To"));
			
		}
		
		//testing header with an empty name
		@Test(expected = IllegalArgumentException.class)
	    public void testAddHeaderNullName() throws Exception {
	        email.addHeader("", testEmail);
	    }

		//testing header with an empty value
	    @Test(expected = IllegalArgumentException.class)
	    public void testAddHeaderNullValue() throws Exception {
	    
	        email.addHeader("To", "");
	    }
	    
	  //Test addReplyTo() function
	    @Test
		 public void testAddReplyTo() throws Exception {
		        
		        email.addReplyTo("sabreen@gmail.com", "sabreen");
		        List<InternetAddress> replyToAddresses = email.getReplyToAddresses();

		        assertNotNull(replyToAddresses);
		        assertEquals(1, replyToAddresses.size());

		        //get first address in list
		        InternetAddress firstAddress = replyToAddresses.get(0);
		        
		    
		        assertEquals("sabreen@gmail.com", firstAddress.getAddress()); 
		        assertEquals("sabreen", firstAddress.getPersonal());      
		    }
	    
	    
	    //Test buildMimeMessage() function
	    @Test (expected = RuntimeException.class)
	    public void testBuildMimeMessage() throws Exception {
	    	try {
	    	//set appropriate values, don't leave any null.
	       email.setHostName("localhost");
	       email.setSmtpPort(1234);
	       email.setFrom("a@gmail.com");
	       email.addTo("hi@gmail.com");
	       email.setSubject("test mail");
	       email.setCharset("ISO-8859-1");
	       email.setContent("test content", "test/plain");;
	       email.buildMimeMessage();
	       //throw exception because can't run buildMimeMessage twice!
	       email.buildMimeMessage();
	    	}
	       catch (RuntimeException re)
	       {
	    	   String message = "The MimeMessage is already built.";
	    	   assertEquals(message, re);
	    	   throw re;
	       }
	    }
	    
	    @Test
	    public void testSuccessfulBuildMimeMessage() throws Exception {
	    	//set appropriate values, don't leave any null.
	    	email.setHostName("localhost");
	        email.setSmtpPort(1234);
	        email.setFrom("a@gmail.com");
	        email.addTo("hi@gmail.com");
	        email.setSubject("Test Email");
	        email.setContent("Hello, this is a test.", "text/plain");
	        
	        //build it using the values set
	        email.buildMimeMessage();
	        
	        //expect mimemessage isn't null
	        assertNotNull(email.getMimeMessage());
	    }
	    
	    //testing buildmime without setFrom (sender)
	    @Test(expected = EmailException.class)
	    public void testBuildMimeMessageWithoutSender() throws Exception {
	    	email.setHostName("localhost");
	        email.setSmtpPort(1234);
	        //email.setFrom(null);
	        email.addTo("professor@gmail.com");
	        email.setSubject("Test Email");
	        email.setContent("This email has no sender.", "text/plain");

	        //email exception thrown
	        email.buildMimeMessage();
	    }
	  
	    //testing buildmime without addTo(...)
	    @Test(expected = EmailException.class)
	    public void testBuildMimeMessageWithoutRecipients() throws Exception {
	    	email.setHostName("localhost");
	        email.setSmtpPort(1234);
	        email.setFrom("sab@gmail.com");
	        //email.addTo("professor@gmail.com");
	        email.setSubject("Test Email");
	        email.setContent("This email has no recipients.", "text/plain");

	        email.buildMimeMessage();
	    }

	    //testing buildmime with headers
	    @Test
	    public void testBuildMimeMessageWithHeaders() throws Exception {
	    	email.setHostName("localhost");
	        email.setSmtpPort(1234);
	        email.setFrom("sab@gmail.com");
	        email.addTo("professor@gmail.com");
	        email.setSubject("Header Test");
	        //set header here (exmaple)
	        email.addHeader("Subject", "Test Email");
	        //build the mimemessage object
	        email.buildMimeMessage();

	        //expect the header returned
	        assertEquals("Test Email", email.getMimeMessage().getHeader("Subject")[0]);
	    }
	    
	    
	    
	    
	    //Test getHostName() function
	    //test gethostname with a session not null
	    @Test
	    public void testGetHostNameFromSession() throws Exception {
	        
	    	//create a new email object since the global one already has a hostname set
	        Email e = new EmailConcrete();
	   
	        //host name is null
	        e.setHostName(null);

	        // Set up the session with a property that specifies the SMTP host
	        Properties properties = new Properties();
	        properties.setProperty("mail.host", "smtp.example.com");
	        Session session = Session.getInstance(properties);

	        //set the session in the email object
	        e.setMailSession(session);

	        //assert that the getHostName() method returns the host from the session's properties
	        assertEquals("smtp.example.com", e.getHostName());
	    }
	    
	    @Test
	    public void testGetHostName() throws Exception {
	        
	        Email e = new EmailConcrete();
	        	e.setHostName("localhost");

	        	//assert that hostname set in email object is localhost
	        assertEquals("localhost", e.getHostName());
	    }
	    @Test
	    public void testGetHostNameFromHostNameField() throws Exception {
	       
	        Email email = new EmailConcrete();
	        
	       
	        //no session is set
	        email.setMailSession(null);
	        email.setHostName("fallback.example.com");

	        //assert that the hostname set directly on the email object
	        assertEquals("fallback.example.com", email.getHostName());
	    }

	    @Test
	    public void testGetHostNameReturnsNull() throws Exception {
	        
	        Email email = new EmailConcrete(); 
	        
	        //no session set
	        email.setMailSession(null);
	        //no hostname
	        email.setHostName(null);

	       //assert that getHostName returns null when no session/hostname
	        assertNull(email.getHostName());
	    }

		 

		//Test getMailSession() function
		 @Test
		 public void testGetMailSessionCreateSession() throws Exception 
		 {
			 Session aSession = email.getMailSession();
		
			 //assert that session is created successfully when when called
			 assertNotNull(aSession);
			
			 
		 }
		
		    
		 	 
		//Test getSentDate() function
		 @Test
		 public void testEmptySentDate() throws Exception 
		 {
		       email.setSentDate(null);
		       Date currentDate = new Date();
		       Date sentDate = email.getSentDate();
		       // assert that the sent date is close to the current date, within 1 second
		       assertTrue(Math.abs(currentDate.getTime() - sentDate.getTime()) < 1000);
		  }
		 
		  @Test
		  public void testGetSentDate() throws Exception
		  {
			  //setting the sent date
			  email.setSentDate(testDate);
			// assert that the sent date matches the test date
		      assertEquals(testDate, email.getSentDate());
		  }
		  
		//Test getSocketConnectionTimeout() function
		  @Test
		  public void testGetSocketConnectionTimeoutDefaultValue() 
		  {
			//assert that the default socket connection timeout matches the expected constant
		    assertEquals(EmailConstants.SOCKET_TIMEOUT_MS, email.getSocketConnectionTimeout());
		  }



	//Test setFrom() function
	 @Test
	 public void testSetFrom() throws Exception
	 {
	    
		 //setting the 'from' address to the test email
		 email.setFrom(testEmail);
		 // assert that the 'from' address is set correctly
		 assertEquals(testEmail, email.getFromAddress());
	 }
}