import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class creadorArchivos {

        public static void main (String args[]) throws IOException
        {
            Random r = new Random();
            FileOutputStream archivo= new FileOutputStream(new File("envio200mb"));
            byte[] datos= new byte[200 * 1000 * 1000];
            for (int i = 0; i < (200 * 1000 * 1000); i++) {
                datos[i] = (byte) (r.nextInt(125 - 48) + 48);
            }
            archivo.write(datos);
            archivo.close();
            archivo= new FileOutputStream(new File("envio100mb"));
            datos= new byte[100 * 1000 * 1000];
            for (int i = 0; i < (100 * 1000 * 1000); i++) {
                datos[i] = (byte) (r.nextInt(125 - 48) + 48);
            }           
            archivo.write(datos);
            archivo.close();
            archivo= new FileOutputStream(new File("envio10mb"));
            datos= new byte[10 * 1000 * 1000];
            for (int i = 0; i < (10 * 1000 * 1000); i++) {
                datos[i] = (byte) (r.nextInt(125 - 48) + 48);
            }           
            archivo.write(datos);
            archivo.close();
            archivo= new FileOutputStream(new File("envio5mb"));
            datos= new byte[5 * 1000 * 1000];
            for (int i = 0; i < (5 * 1000 * 1000); i++) {
                datos[i] = (byte) (r.nextInt(125 - 48) + 48);
            }           
            archivo.write(datos);
            archivo.close();
        }
}