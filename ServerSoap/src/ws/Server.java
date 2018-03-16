package ws;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

public class Server {

	String lien = "C:\\Users\\Cheikhouna\\eclipse-workspace\\ServerSoap\\message";
	@WebMethod
    public boolean subscribe(String pseudo) {	
	    String message = pseudo +" connected";
	    this.ajout(this.lien, message);
	    System.err.println(pseudo +" connected");
	    return true;
    }

	@WebMethod
    public boolean unsubscribe(String pseudo) {
        this.ajout(this.lien, pseudo+" disconected");
        return true;
    }

	@WebMethod
    public void postMessage(String pseudo, String message) {
        String mes = pseudo+"--->"+message ;
        System.out.println(mes);
        this.ajout(this.lien, mes);
    }

	@WebMethod
    public String getMessage(){
        return read(this.lien);
    }
    
    public void ajout(String filename, String contenu){
		Path fichier = Paths.get(filename);
		Charset charset = Charset.forName("US-ASCII");
		
		try (BufferedWriter writer = Files.newBufferedWriter(fichier, charset)) {
			synchronized (writer) {
				writer.write(contenu);
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}
	
	public String read(String fileName){
		String retour = "";
		try {
			List<String>lines = Files.readAllLines(
					FileSystems.getDefault().getPath(fileName), StandardCharsets.UTF_8);
			for(String ligne : lines){
				retour += ligne;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return retour;
	}
}