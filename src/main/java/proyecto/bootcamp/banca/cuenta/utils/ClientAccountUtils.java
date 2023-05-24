package proyecto.bootcamp.banca.cuenta.utils;

import java.util.Random;

public class ClientAccountUtils {
    public static String createNumber()
    {
        Random random = new Random();
            Long number= random.nextLong();
            return  correlativoHelper(number.toString());
    }
    public static String correlativoHelper(String flag){
        int aux=flag.length();
        String retorno="";
        if(aux>5){
            retorno= "00001";
        }
        else{
            while (aux<5){
                retorno=(retorno+"0");
                aux++;
            }
            retorno= retorno+flag;
        }
        return retorno;
    }
}
