import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Set;


public class ro2021send 
{

    //RTO
    boolean primerRTT= true;
    double sRTT;
    double dRTT;
    long RTO(long rtt)
    {
        
        if(primerRTT==true)
        {
            sRTT=rtt;
            primerRTT=false;
            dRTT=rtt/2;
        }else
        {
            sRTT=(1-(1/8)*sRTT)+(1/8)*rtt;
            dRTT=(1-(1/4)*dRTT)+(1/4)*Math.abs(sRTT-rtt);
        }
        return sRTT+4*dRTT; 
    }
    public static void main(String[] args) 
    {
        if(args.length!=5)
        {
            System.out.println("ro2021send input_file dest_IP dest_port emulator_IP emulator_port");
            System.exit(1);    
        } 

        try
        {
            InetAddress ipDestino=InetAddress.getByName(args[1]);
            InetAddress ipEmulador=InetAddress.getByName(args[3]);
            int puerto =Integer.parseInt(args[2]);
            int numeroSecuencia=0;
            int numSecuenciaEsperado=0;
            int iniVentana=0;
            int tamanhoVentana=20;
            int finVentana=20;
            boolean fin=false;
            byte[] datosArchivo= Files.readAllBytes(new File(args[0]).toPath());
            SocketAddress elSocket2= new InetSocketAddress(ipEmulador, Integer.parseInt(args[4]));
            DatagramChannel elCanal1= DatagramChannel.open();
            elCanal1.configureBlocking(false);
            elCanal1.connect(elSocket2);
            Selector elDeLectura = SelectorProvider.provider().openSelector();
            Selector elDeEscritura = SelectorProvider.provider().openSelector();
            elCanal1.register(elDeLectura, SelectionKey.OP_READ);
            elCanal1.register(elDeEscritura, SelectionKey.OP_WRITE);

        

            ByteBuffer primero = ByteBuffer.allocate(1472);
            ByteBuffer segundo= ByteBuffer.allocate(10);


            while(fin==false)
            {
                System.out.println(numeroSecuencia+" "+finVentana);
                if(numeroSecuencia<=finVentana && (numeroSecuencia)*1462<=datosArchivo.length)
                {
                    System.out.println("dos");
                    elDeEscritura.selectNow();
                    Set readkeys = elDeEscritura.selectedKeys();
                    Iterator iterator = readkeys.iterator();
                    while(iterator.hasNext())
                    {
                        SelectionKey llave =(SelectionKey)iterator.next();
                        iterator.remove();
                        primero.clear();
                        primero.put(ipDestino.getAddress());
                        primero.put((byte)(puerto>>8));
                        primero.put((byte)puerto);
                        primero.putInt(numeroSecuencia);

                        if((numeroSecuencia+1)*1462>datosArchivo.length)
                        {
                            primero.put(datosArchivo,numeroSecuencia,datosArchivo.length-numeroSecuencia);
                            System.out.println("enviadoElUltimo");

                        }else
                        {
                            primero.put(datosArchivo,numeroSecuencia,1462);

                        }

                        primero.flip();
                        elCanal1.write(primero);
                        System.out.println(numeroSecuencia);
                        numeroSecuencia++;

                    }
                }

                
                elDeLectura.selectNow();
                Set readkeysDos = elDeLectura.selectedKeys();
                Iterator iteratorDos = readkeysDos.iterator();
                while(iteratorDos.hasNext())
                {
                    SelectionKey llave =(SelectionKey)iteratorDos.next();
                    iteratorDos.remove();
                    elCanal1.read(segundo);
                    segundo.flip();
                    numSecuenciaEsperado=((segundo.get(6)&0xFF)<<24)+((segundo.get(7)&0xFF)<<16)+((segundo.get(8)&0xFF)<<8)+(segundo.get(9)&0xFF);
                   
                    System.out.println(numSecuenciaEsperado+" " +(numSecuenciaEsperado+1)*1462 + " "+datosArchivo.length);
                    if((numSecuenciaEsperado+1)*1462>=datosArchivo.length)
                    {
                        fin=true;
                    }

                    if(iniVentana==numSecuenciaEsperado)
                    {
                        iniVentana++;
                        finVentana++;
                    }
               
                }

            }
            primero.clear();
            primero.put(ipDestino.getAddress());
            primero.put((byte)(puerto>>8));
            primero.put((byte)puerto);
            primero.flip();
            elCanal1.write(primero);

        }catch(Exception e)
            {
                System.out.println("Error:"+e.getMessage());
            }



    }
}
