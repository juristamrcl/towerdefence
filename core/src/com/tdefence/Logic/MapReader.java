package com.tdefence.Logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by Marcel Jurišta on 14.10.2017.
 */

public class MapReader {
    private int [][][] map;

    public int [][][] read (String mission){
        map = new int [2][10][20];
        Scanner sc;
        try {
            sc = new Scanner(Gdx.files.internal(mission).readString());

            while(sc.hasNext()){
                for (int i = 0; i < 10; i++){
                    for (int j = 0; j < 20; j++){
                        map[0][i][j] = sc.nextInt();
                    }
                }
                for (int i = 0; i < 10; i++){
                    for (int j = 0; j < 20; j++){
                        map[1][i][j] = sc.nextInt();
                    }
                }
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error During reading");
            e.printStackTrace();
        }

        return map;

    }
}
