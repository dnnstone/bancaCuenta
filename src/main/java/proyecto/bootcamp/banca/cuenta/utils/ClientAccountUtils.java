package proyecto.bootcamp.banca.cuenta.utils;

import java.util.Random;

public class ClientAccountUtils {
    public static String createNumber()
    {
        Random random = new Random();
        random.nextInt();
            Long number= random.nextLong();
            number = number<0?number*-1:number;

        System.out.println("mumero aleatorio"+number);
            return  correlativoHelper(number.toString());
    }
    public static String correlativoHelper(String flag){
        int aux=flag.length();
        String retorno="";
        if(aux>20){
            retorno= "00002";
        }
        else{
            while (aux<20){
                retorno=(retorno+"0");
                aux++;
            }
            retorno= retorno+flag;
        }
        return retorno;
    }
}
