package ws;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.rpc.*;
import javax.xml.rpc.encoding.XMLType;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;


public class ChatRoom {

	private String title = "Logiciel de discussion en ligne";
    private String pseudo = null;
    
    private JFrame window = new JFrame(this.title);
    private JTextArea txtOutput = new JTextArea();
    private JTextField txtMessage = new JTextField();
    private JButton btnSend = new JButton("Envoyer");
    Call call;
    public ChatRoom(){
        try {
        	String endpoint = "http://localhost:8080/ServerSoap/services/Server";
    		Service service = new Service();
    		this.call = (Call)service.createCall();
    		this.call.setTargetEndpointAddress(new java.net.URL(endpoint));
		} catch (Exception e) {
			System.out.println("Impossible de joindre la salle de discussion");
			System.exit(0);
			e.printStackTrace();
		}
        this.createIHM();
        this.requestPseudo();
    }

    public void createIHM() {
        // Assemblage des composants
        JPanel panel = (JPanel)this.window.getContentPane();
        JScrollPane sclPane = new JScrollPane(txtOutput);
        panel.add(sclPane, BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(this.txtMessage, BorderLayout.CENTER);
        southPanel.add(this.btnSend, BorderLayout.EAST);
        panel.add(southPanel, BorderLayout.SOUTH);

        // Gestion des évènements
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
					window_windowClosing(e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
            }
        });
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnSend_actionPerformed(e);
            }
        });
	txtMessage.addKeyListener(new KeyAdapter() {
		public void keyReleased(KeyEvent event) {
			if (event.getKeyChar() == '\n')
			    btnSend_actionPerformed(null);
		    }
	});

        // Initialisation des attributs
        this.txtOutput.setBackground(new Color(220,220,220));
        this.txtOutput.setEditable(false);
		this.window.setSize(500,400);
        this.window.setVisible(true);
        this.txtMessage.requestFocus();
    }
    
    //inscription
    public void requestPseudo(){
		boolean retour = false;
		String retourString = null;
        do {            
            this.pseudo = JOptionPane.showInputDialog(
                this.window, "Entrez votre pseudo : ",
                this.title,  JOptionPane.OK_OPTION
        ); 
        if (this.pseudo == null) System.exit(0);
        
        try {
        	call.setOperationName("subscribe");
    		call.addParameter("op", XMLType.XSD_STRING, ParameterMode.IN);
    		call.setReturnType(XMLType.XSD_BOOLEAN);
    		retour = (Boolean)call.invoke( new Object [] { this.pseudo });
    		call.clearOperation();
            if(retour == false){
                JOptionPane.showMessageDialog(this.window, pseudo+" existe deja");
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (retour == false);
    }
    
    //desinscription
    public void window_windowClosing(WindowEvent e) throws Exception {
    	call.setOperationName("unsubscribe");
 		call.addParameter("ope", XMLType.XSD_STRING, ParameterMode.IN);
 		call.setReturnType(XMLType.XSD_BOOLEAN);
 		call.invoke( new Object [] { this.pseudo });
    	call.clearOperation();
 		System.exit(-1);
    }
    //envoi message
    public void btnSend_actionPerformed(ActionEvent e) {
    	try {
			call.setOperationName("postMessage");
			call.addParameter("op1", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("op2", XMLType.XSD_STRING, ParameterMode.IN);
			call.setReturnType(XMLType.XSD_STRING);
			call.invoke( new Object [] { this.pseudo, this.txtMessage.getText() });
			call.clearOperation();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Impossible d envoyer le message");
		}
    	this.txtMessage.setText("");
        this.txtMessage.requestFocus();
    }

    public static void main(String[] args) {
    	ChatRoom  chat = new ChatRoom();
    	 boolean received = false;
         String mes = null;
         String nouv = null;
         boolean first = true;
         while(true){
             try {
                 chat.call.setOperationName("getMessage");
                 chat.call.setReturnType(XMLType.XSD_STRING);
                 mes = (String)chat.call.invoke( new Object[]{});
                 chat.call.clearOperation();
                 received = true;
                 if(mes.equals(nouv)){
                     first = false;
                 }else{
                     first = true;
                 }
             } catch (Exception e) {
                 received = false;
             }finally{
                 if(received ){
                     if(first){
                         chat.txtOutput.append(mes+"\n");
                         nouv = mes;
                         first = false;
                     }
                     
                 }
             }
         }
		
	}
}