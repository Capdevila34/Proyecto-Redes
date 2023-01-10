import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class ro2021recv 
{
    public static void main(String[] args) 
    {
        if(args.length!=2)
        {
            System.out.println("ro2021recv output_file listen_port");
            System.exit(1);    
        }  
        try 
        {

            BufferedOutputStream ropero= new BufferedOutputStream(new FileOutputStream(new File (args[0])));
            byte[] paqueteRecibido= new byte[1472];
            DatagramSocket elSocket1 = new DatagramSocket(Integer.parseInt(args[1]));
            DatagramPacket elPacket1= new DatagramPacket(paqueteRecibido, paqueteRecibido.length);
            int numSecuencia=0;
            int numSecuenciaEsperado=0;
            
            while(true)
            {
                elSocket1.receive(elPacket1);
                
                if(elPacket1.getLength()==6)
                {
                    ropero.close();
                    break;
                }

                numSecuencia=((paqueteRecibido[6]&0xFF)<<24)+((paqueteRecibido[7]&0xFF)<<16)+((paqueteRecibido[8]&0xFF)<<8)+(paqueteRecibido[9]&0xFF);
                System.out.println(numSecuencia);

                if(numSecuenciaEsperado==numSecuencia)
                {
                    ropero.write(paqueteRecibido , 10 ,elPacket1.getLength()-10);
                    numSecuenciaEsperado++;
                }
                byte[] paqueteEnviado = new byte[10];
                for(int i=0;i<10;i++)
                {
                    paqueteEnviado[i]=paqueteRecibido[i];
                }
                DatagramPacket elEnviador = new DatagramPacket(paqueteEnviado,paqueteEnviado.length,elPacket1.getAddress(),elPacket1.getPort());
                elSocket1.send(elEnviador);

            }
        
        } catch (Exception e) 
        {
            System.out.println("Error:"+e.getMessage());    
        }

       

    }
}
