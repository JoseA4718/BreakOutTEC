#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>
#include <stdbool.h>

#include "Constants.h"

/**
 * game: estructura principal del juego
 * Almacena todas las variables del juego
 */
typedef struct game{
    int matrix[MAXROWS][MAXCOLS];
    int lives;
    int ballQuantity;
    double ballSpeed;

    int ballPosition[GAMEDIMENSION];
    double racketLenght;
    int racketPosition;

    int level;
    int score;

    int greenBrickValue;
    int yellowBrickValue;
    int orangeBrickValue;
    int redBrickValue;
} game_t;

static game_t* mainGame;

/** createGame: inicializar por defecto todos los miembros del struct game
 * game_t* newGame: game por inicializar
 * int greenVal: valor para los ladrillos verdes
 * int yellowVal: valor para los ladrillos amarillos
 * int orangeVal: valor para los ladrillos anaranjados
 * int redVal: valor para los ladrillos rojos
 */

void createGame(int greenVal,int yellowVal, int orangeVal, int redVal, double newBallSpeed, int levelUp, int newScore){

    //Inicializar la matriz de juego
    for (int i = 0; i < MAXROWS; i++){
        for (int j = 0; j < MAXCOLS; j++){
            if (i == 0 || i == 1){
                //mainGame->matrix[i][j] = rand() % 10;
                mainGame->matrix[i][j] = 4;
                continue;
            }
            else if(i == 2 || i == 3){
                mainGame->matrix[i][j] = 3;
                continue;
            }
            else if(i == 4 || i == 5){
                mainGame->matrix[i][j] = 2;
                continue;
            }
            else if(i == 6 || i == 7){
                mainGame->matrix[i][j] = 1;
                continue;
            }
        }
    }

    //Inicializar vidas, y demás miembros
    mainGame->lives = INIT_LIVES;
    mainGame->ballQuantity = 1;
    mainGame->ballSpeed = newBallSpeed;
    mainGame->ballPosition[0] = 0;
    mainGame->ballPosition[1] = 0;
    mainGame->racketLenght = INIT_RACKET_LEN;
    mainGame->racketPosition = 0;

    mainGame->level = levelUp;
    mainGame->score = newScore;

    mainGame->greenBrickValue = greenVal;
    mainGame->yellowBrickValue = yellowVal;
    mainGame->orangeBrickValue = orangeVal;
    mainGame->redBrickValue = redVal;

    printf("Valores de ladrillos: G = %i, Y = %i, O = %i, R = %i \n",
           mainGame->greenBrickValue,
           mainGame->yellowBrickValue,
           mainGame->orangeBrickValue,
           mainGame->redBrickValue);
}

/**
 * levelFinished: revisa la matriz para verificar si está vacía
 * @return
 */
bool levelFinished(){
    for (int i = 0; i < MAXROWS; i++){
        for (int j = 0; j < MAXCOLS; j++){
            if (mainGame->matrix[i][j] != 0){
                return false;
            }
        }
    }
    return true;
}

/**
 * nextLevel: revisa si el nivel ha terminado,
 * si es así, vuelve a inicializar el juego con los nuevos valores correspondientes
 */
void nextLevel(){
    if(levelFinished(mainGame) == true){
        //Reiniciar el juego con los mismos valores de ladrillos, pero con el doble de la velocidad de la bola
        createGame(mainGame->greenBrickValue,
                   mainGame->yellowBrickValue,
                   mainGame->orangeBrickValue,
                   mainGame->redBrickValue,
                   mainGame->ballSpeed*2,
                   mainGame->level++,
                   mainGame->score);
    }else{
        //printf("No se ha terminado el nivel");
    }
}

/**
 * lost: verifica si las vidas se han terminado
 * @return
 */
bool lost(){
    return mainGame->lives == 0;
}
