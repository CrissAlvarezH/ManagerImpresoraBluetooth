package miercoles.dsl.bluetoothprintprueba.utilidades;

import android.graphics.Bitmap;

import zj.com.customize.sdk.Other;

/**
 * Created by usuario on 6/03/2018.
 */

public class PrintBitmap {

    public static byte[] POS_PrintBMP(Bitmap mBitmap, int nWidth, int nMode) {
        int width = ((nWidth + 7) / 8) * 8;
        int height = mBitmap.getHeight() * width / mBitmap.getWidth();
        height = ((height + 7) / 8) * 8;

        Bitmap rszBitmap = mBitmap;
        if (mBitmap.getWidth() != width){
            rszBitmap = Other.resizeImage(mBitmap, width, height);
        }

        Bitmap grayBitmap = Other.toGrayscale(rszBitmap);

        byte[] dithered = Other.thresholdToBWPic(grayBitmap);

        byte[] data = Other.eachLinePixToCmd(dithered, width, nMode);

        return data;
    }


    public static byte[] Print_1D2A(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        byte data[]=new byte[1024*100];
        data[0] = 0x1D;// caracter GS
        data[1] = 0x2A;// caracter *
        data[2] =(byte)( (width - 1)/ 8 + 1);// X
        data[3] =(byte)( (height - 1)/ 8 + 1);// Y
        byte k = 0;
        int position = 4;
        int i;// posicion en el eje x de los bytes de la imagen
        int j;// posicion en el eje y de los bytes de la imagen
        byte temp = 0;

        for(i = 0; i <width;  i++){// recorremos el ancho
            for(j = 0; j < height; j++){// recorremos el alto
                if(bmp.getPixel(i, j) != -1){// Si devuelve un color valido del pixel en ARGB
                    temp |= (0x80 >> k);
                }

                k++;

                if(k == 8){
                    data[position++] = temp;
                    temp = 0;
                    k = 0;
                }
            }// fin for j

            if(k % 8 != 0){
                data[position ++] = temp;
                temp = 0;
                k = 0;
            }
        }// fin for i

        if( width% 8 != 0){
            i =   height/ 8;
            if(height % 8 != 0) i++;
            j = 8 - (width % 8);
            for(k = 0; k < i*j; k++){
                data[position++] = 0;
            }
        }

        return data;
    }
}
