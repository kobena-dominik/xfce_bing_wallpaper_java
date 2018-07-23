import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.regex.Pattern;
import java.util.regex.Matcher ;


/**
 * Ce programme permet de définir l'image du jour de bing comme le fond d'ecran du bureau de xfce pour le 1er workspace.
 * À l'avenir, il faudra prendre en compte le nombre de workspace et definir le fond sur tous les workspaces
 * @author KOBENA Dominique
 * @version 1.0
 */
public final class Xfce {
	
	/**
	 * Cette propriété représente l'url pour le téléchargement de l'image Bing
	 */
	private final static   String url = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US";
	
	/**
	 * La date d'aujourd'hui
	 */
	private static String today = Xfce.tdate(); ;
	
	/**
	 * La date d'aujourd'hui, cette méthode sert à la propriété static today
	 * @return
	 */
	private static String tdate () {
		Calendar today = Calendar.getInstance();
		int day = today.get(Calendar.DAY_OF_MONTH);
		String Day ;
		if (day < 10)
			Day = 0 + String.valueOf(day);
		else
			Day = String.valueOf(day);
		int month = today.get(Calendar.MONTH)+1;
		String Month ;
		if (month < 10)
			Month = 0 + String.valueOf(month);
		else
			Month = String.valueOf(day);
		//Permet de formater la date au format dd/MM/YYYY
		String todayDate = today.get(Calendar.YEAR)+Month+Day ;
		Xfce.today = todayDate ;
		return todayDate ;
	}
	/**
	 * Cette Méthode détermine si l'image du jour a été téléchargée.
	 * @return uri of downloaded image or null 
	 */
	private static String bgDownload() {
		String bgDownloaded = null ;
		//Maintenant je cherche un fichier au format 'dd/MM/YYYY-nomdufichier.jpg'
		File dossierImages = new File("/usr/share/images/desktop-base/") ;
		//je dois parcourir le dossier
		String [] listeDesFichiers ;
		listeDesFichiers = dossierImages.list();
		for (String nomfichier : listeDesFichiers) {
			if (nomfichier.matches(Xfce.today+".*")) {
				bgDownloaded = nomfichier ;
				break ;
			}
		}
		return bgDownloaded ;
	}
	
	/**
	 * Cette methode permet de telecharger l'image du jour Bing
	 * @return true or false
	 * @throws Exception 
	 */
	private static boolean downloadImage () {
		URL urlBing = null ;
		InputStream inputStream = null ;
		try {
			urlBing = new URL(url) ;
			inputStream = urlBing.openStream() ;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			return false ; //throw new Exception("L'url de base n'est pas correctement ecrite");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false ; //throw new Exception("Impossible d'ouvrir l'url ! Vérifiez votre connexion a internet.");
		}
		
		BufferedReader buff = new BufferedReader(new InputStreamReader(inputStream));
        String currentLine = null;
        String queryResult = null ;
        try {
        	 while((currentLine = buff.readLine()) != null) {
             	queryResult = currentLine ;
             }
        }catch(IOException e) {
        	return false ; //throw new Exception("Probleme de lecture de l'url de base. Veuillez réessayer plus tard !");
        }
        //System.out.println(queryResult);
        String pattern = "\"url\":\"(.+)?\",\"urlbase\":\".*\"";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(queryResult);
        String urlImageATelecharger = null ;
        if (m.find()) {
        	urlImageATelecharger = "http://bing.com/" + m.group(1).substring(1);
        }else {
        	return false ; //throw new Exception("Impossible d'extraire l'url de l'image du jour pour la telecharger");
        }
        String nomDuFichierImage = null ;
    	String pattern1 = "^.*/(.*)$";
        Pattern p1 = Pattern.compile(pattern1);
        Matcher m1 = p1.matcher(urlImageATelecharger);
    	if (m1.find()) {
    		nomDuFichierImage = m1.group(1) ;
    	}
    	InputStream downloadLink = null ;
    	try {
    		downloadLink = new URL(urlImageATelecharger).openStream() ;
    	}catch(IOException e) {
    		return false ; //throw new Exception("Impossible d'ouvrir l'url ! Vérifiez votre connexion a internet.");
    	}
    	FileOutputStream imagedujour = null ;
    	try {
    		//System.out.println("/usr/share/images/desktop-base/"+Xfce.today+"-"+nomDuFichierImage);
    		imagedujour = new FileOutputStream(new File("/usr/share/images/desktop-base/"+Xfce.today+"-"+nomDuFichierImage)) ;
    		
    	}catch (FileNotFoundException e) {
    		return false ; //throw new Exception("Impossible de creer l'image. Veuillez verifier les droits d'acces en ecriture");
    	}
        int c ;
        try {
        	while( (c = downloadLink.read()) != -1 ) {
            	imagedujour.write(c);
            }
            imagedujour.flush();
            imagedujour.close();	
        }catch (IOException e) {
			// TODO: handle exception
        	return false ; //throw new Exception("Impossible de telecharger l'image. Veuillez verifier la connexion a Internet");
		}
        System.out.println("Fond d'ecran du jour téléchargé avec success");
		return true; 
	}
	/**
	 * Retourne le fond d'ecran actuel
	 * @return String last image 
	 */
	private static String getLastImage() {
		String ligne = null ;
		try {
			Process p = Runtime.getRuntime().exec("xfconf-query -c xfce4-desktop -p /backdrop/screen0/monitor0/workspace0/last-image");
			BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream())) ;
			String sortie = "" ;
			while((sortie = output.readLine()) != null) {
				ligne = sortie ;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ligne ;
	}
	
	/**
	 * Point d'entrée du programme
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Verifie si l'image du jour existe deja
		if(bgDownload() == null) {
			//Sinon telecharge l'image du jour, et reessaie 15 min apres 
			//en cas d'echec
			while (downloadImage() != true) {
				try {
					System.out.println("Downloading...");
					Thread.sleep(1000*60*15); //15 minutes
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}else {
			System.out.println("Vous avez deja l'image du jour");
			//System.exit(0);
		}
		//Maintenant il faut savoir si l'image du jour est celle qui est définie avec xconf
		//Si l'image definie est différente de l'image du jour
		if( ! ( getLastImage().equals("/usr/share/images/desktop-base/"+bgDownload()) )) {
			//je dois definir l'image du jour et quitter le programme
			try {
				Runtime.getRuntime().exec("xfconf-query -c xfce4-desktop -p /backdrop/screen0/monitor0/workspace0/last-image -s "+
						"/usr/share/images/desktop-base/"+bgDownload());
				System.out.println("Fond d'écran du jour défini avec succes");
				System.exit(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Impossible de definir le fond d'écran, veuillez contactez deressources@gmail.com pour signaler le bug");
				System.exit(1);
			}
		}else {
			System.out.println("Le fond d'écran est déjà défini le programme se termine !");
			System.exit(0);
		}
	}
}
