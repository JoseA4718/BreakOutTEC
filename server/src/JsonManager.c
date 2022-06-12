#include <stdio.h>
#include <stdlib.h>
#include "../lib/json-maker.h"
#include "../lib/tiny-json.h"
#include "GameStructure.c"
#include "Constants.h"


/** jsonGame(): convertir el struct game_t en un string en formato json
 * char* dest: donde se guardará el string
 * game_t* src: struct game actual
 */
void jsonGame( char* p){
    //char* p = dest;                                         // p always points to the null character
    //Abrir el JSON
    p = json_objOpen( p, NULL );                            // --> {\0

    //Insertar la matriz en formato json
    p = json_arrOpen( p, "matrix" );            //Abrir un arreglo para la matriz
    for( int i = 0; i < MAXROWS ; i++){
        p = json_arrOpen(p, NULL);              //Abrir un subarreglo
        for (int j = 0; j < MAXCOLS; j++){
            p = json_int( p, NULL, mainGame->matrix[i][j] );    //Colocar los elementos
        }
        p = json_arrClose( p );                 //Cerrar subarreglo
    }
    p = json_arrClose( p );                     //Cerrar arreglo de la matriz

    p = json_int( p, "lives", mainGame->lives );                 // --> {"lives":3,\0
    p = json_int( p, "ballQuantity", mainGame->ballQuantity );   // --> {"lives":3,"ballQuantity":1,\0
    p = json_double( p, "ballSpeed", mainGame->ballSpeed );

    p = json_arrOpen( p, "ballPosition" );
    for (int k = 0; k < GAMEDIMENSION; k++) {
        p = json_int( p, NULL, mainGame->ballPosition[k]);       //Agregar las coordenadas como un arreglo en foramto json
    }
    p = json_arrClose( p );

    p = json_double( p, "racketLenght", mainGame->racketLenght );
    p = json_int( p, "racketPosition", mainGame->racketPosition );

    p = json_int( p, "level", mainGame->level );
    p = json_int( p, "score", mainGame->score );

    p = json_int( p, "greenBrickValue", mainGame->greenBrickValue );
    p = json_int( p, "yellowBrickValue", mainGame->yellowBrickValue );
    p = json_int( p, "orangeBrickValue", mainGame->orangeBrickValue );
    p = json_int( p, "redBrickValue", mainGame->redBrickValue );

    p = json_objClose( p );                                 // --> {"lives":3,"ballQuantity":1}\0
    p = json_end( p );                                      // --> {"lives":3,"ballQuantity":1}\0

    //return p ;
}



//============================== READER ==================================//

/**
 * jsonParse(): leer un archivo json
 *
 */
int jsonParse(){

    FILE *fp;
    char buff[MAX_READ_JSON];       //Buff es el texto del archivo
    if ((fp = fopen("./levels/level1.json", "r")) == NULL) {
        printf("Error! opening file");
        return -1;
    }
    fscanf(fp, "%s", buff);
    printf("Texto del JSON recibido: %s\n", buff );

    fclose(fp);

}



/**
 * useJsonFile: ingresar el string json en un archivo .json
 * char* newJson: string en formato json
 */
void useJsonFile(char* newJson){
    FILE *fp;
    fp = fopen("game.json", "w+");
    fputs(newJson, fp);
    fclose(fp);
}

/**
 * Actualizar el puntaje del jugador
 * brickID: número del ladrillo en el json
 */
void updateScore(int brickID){
    switch (brickID){
        case 1:
            mainGame->score += mainGame->greenBrickValue;
            break;
        case 2:
            mainGame->score += mainGame->yellowBrickValue;
            break;
        case 3:
            mainGame->score += mainGame->orangeBrickValue;
            break;
        case 4:
            mainGame->score += mainGame->redBrickValue;
            break;
        default:
            break;
    }
}

/**
 * Actualizar el puntaje del jugador
 * brickID: número del ladrillo en el json
 */

void updateGameStats(int brickID){
    switch (brickID){
        case BRICK_LIFE :
            mainGame->lives+=1;
            break;
        case BRICK_BALL:
            //Si ya hay 3 bolas no aumente
            if(mainGame->ballQuantity < 3){
                mainGame->ballQuantity+=1;
            }
            break;
        case BRICK_SPEED_UP:
            //Si la velocidad es mayor que 10 no la aumente
            if(mainGame->ballSpeed < 10){
                mainGame->ballSpeed = 2 * mainGame->ballSpeed;
            }
            break;

        case BRICK_SPEED_DOWN:
            //Si la velocidad es menor que 1 no la disminuya
            if(mainGame->ballSpeed >= 1){
                mainGame->ballSpeed = mainGame->ballSpeed/2;
            }
            break;
        case BRICK_RACKET_UP:
            if(mainGame->racketLenght < 500){
                mainGame->racketLenght = 2 * mainGame->racketLenght;
            }
            break;
        case BRICK_RACKET_DOWN:
            if(mainGame->racketLenght >= 50){
                mainGame->racketLenght = mainGame->racketLenght/2;
            }
            break;
        default:
            break;
    }
}


/**
 * receiveClientMessage
 * Recibe, analiza y actualiza el juego según msg
 * @param msg
 */
void receiveClientMessage(char* msg){
    short cont = 0;
    char tmp[DEFAULT_BUFLEN];
    strcpy(tmp, msg);       //Copiar msg en tmp

    char* splitMsg = strtok(tmp, " ");      // splitMsg equivalente msg[0]
    //printf("Mensaje del cliente recibido: %s ", splitMsg);

    //=======================    Actualizar si se rompió un bloque    =======================//

    if (strcmp(splitMsg, "Broke") == 0){

        splitMsg=strtok(NULL," ");          // splitMsg equivalente msg[1]
        int row = atoi(splitMsg);
        //printf("%i ", row);

        splitMsg=strtok(NULL," ");          // splitMsg equivalente msg[2]
        int col = atoi(splitMsg);
        //printf("%i \n", col);

        //Actualizar el puntaje del juego
        updateScore(mainGame->matrix[row][col]);

        //Actualizar las variables del juego
        updateGameStats(mainGame->matrix[row][col]);

        //Poner en cero el bloque --> roto
        mainGame->matrix[row][col] = 0;
    }


        //====================    Actualizar la cantidad de vidas    ====================//

    else if (strcmp(splitMsg, "Lost") == 0){
        mainGame->lives--;
    }


        //====================    Actualizar la cantidad de bolas    =======================//

    else if (strcmp(splitMsg, "BallQuantity") == 0){
        mainGame->ballQuantity+=1;
    }


        //====================    Actualizar la velocidad de la bola    =======================//

    else if (strcmp(splitMsg, "BallSpeed") == 0){
        splitMsg=strtok(NULL," ");          // splitMsg equivalente msg[1]
        printf("%s \n", splitMsg);
        if (strcmp(splitMsg, "Up") == 0){
            mainGame->ballSpeed = (1.3 * mainGame->ballSpeed);
        }
        else if (strcmp(splitMsg, "Down") == 0){
            mainGame->ballSpeed = (0.7 * mainGame->ballSpeed);
        }
    }


        //====================    Actualizar la posición de la bola    =======================//

    else if (strcmp(splitMsg, "BallPosition") == 0){
        splitMsg=strtok(NULL," ");          // splitMsg equivalente msg[1]
        int xPos = atoi(splitMsg);
        printf("%i ", xPos);

        splitMsg=strtok(NULL," ");          // splitMsg equivalente msg[2]
        int yPos = atoi(splitMsg);
        printf("%i \n", yPos);

        mainGame->ballPosition[0] = xPos;
        mainGame->ballPosition[1] = yPos;
    }


        //====================    Actualizar la longitud de la raqueta    =======================//

    else if (strcmp(splitMsg, "RacketLenght") == 0){
        splitMsg=strtok(NULL," ");          // splitMsg equivalente msg[1]
        printf("%s \n", splitMsg);
        if (strcmp(splitMsg, "Up") == 0){
            mainGame->racketLenght = (2 * mainGame->racketLenght);
        }
        else if (strcmp(splitMsg, "Down") == 0){
            mainGame->racketLenght = (0.5 * mainGame->racketLenght);
        }
    }


        //====================    Actualizar la posición de la raqueta    =======================//

    else if (strcmp(splitMsg, "RacketPosition") == 0){
        splitMsg=strtok(NULL," ");          // splitMsg equivalente msg[1]
        int pos = atoi(splitMsg);
        //printf("%i \n", pos);

        mainGame->racketPosition = pos;
    }
    else{
        //printf("\n");
    }
}



//================================================================//

/**
 * receiveUserMessage
 * Recibe, analiza y actualiza el juego según msg
 * @param msg
 */
void receiveUserMessage(char* msg){
    short cont = 0;
    char tmp[256];
    strcpy(tmp, msg);       //Copiar msg en tmp

    char* splitMsg = strtok(tmp, " ");      // splitMsg equivalente msg[0]
    printf("Mensaje del usuario recibido: %s ", splitMsg);

    //=======================    Actualizar el valor de los bloques    =======================//

    if (strlen(splitMsg) == 0){
        printf("Mensaje vacío");
    }
    if (strcmp(splitMsg, "BrickValues") == 0){

        splitMsg=strtok(NULL," ");          // splitMsg equivalente msg[1]
        int green = atoi(splitMsg);
        printf("%i ", green);

        splitMsg=strtok(NULL," ");          // splitMsg equivalente msg[2]
        int yellow = atoi(splitMsg);
        printf("%i ", yellow);

        splitMsg=strtok(NULL," ");          // splitMsg equivalente msg[3]
        int orange = atoi(splitMsg);
        printf("%i ", orange);

        splitMsg=strtok(NULL," ");          // splitMsg equivalente msg[4]
        int red = atoi(splitMsg);
        printf("%i ", red);

        mainGame->greenBrickValue = green;
        mainGame->yellowBrickValue = yellow;
        mainGame->orangeBrickValue = orange;
        mainGame->redBrickValue = red;
    }


        //====================    Actualizar la cantidad de vidas    ====================//

    else if (strcmp(splitMsg, "Life") == 0){
        splitMsg = strtok(NULL," ");          // splitMsg equivalente msg[1]
        int row = atoi(splitMsg);
        printf("%i ", row);

        splitMsg = strtok(NULL," ");          // splitMsg equivalente msg[2]
        int col = atoi(splitMsg);
        printf("%i \n", col);

        mainGame->matrix[row][col] = BRICK_LIFE;
        printf("Entendido!\n");

    }


        //====================    Actualizar la cantidad de bolas    =======================//

    else if (strcmp(splitMsg, "BallQuantity") == 0){
        splitMsg = strtok(NULL," ");          // splitMsg equivalente msg[1]
        int row = atoi(splitMsg);
        printf("%i ", row);

        splitMsg = strtok(NULL," ");          // splitMsg equivalente msg[2]
        int col = atoi(splitMsg);
        printf("%i \n", col);

        mainGame->matrix[row][col] = BRICK_BALL;
        printf("Entendido!\n");


    }


        //====================    Actualizar la velocidad de la bola    =======================//

    else if (strcmp(splitMsg, "BallSpeed") == 0){
        splitMsg=strtok(NULL," ");          // splitMsg equivalente msg[1]
        printf("%s ", splitMsg);
        if (strcmp(splitMsg, "Up") == 0){
            splitMsg = strtok(NULL," ");          // splitMsg equivalente msg[1]
            int row = atoi(splitMsg);
            printf("%i ", row);

            splitMsg = strtok(NULL," ");          // splitMsg equivalente msg[2]
            int col = atoi(splitMsg);
            printf("%i \n", col);
            mainGame->matrix[row][col] = BRICK_SPEED_UP;

            printf("Entendido!\n");

        }
        else if (strcmp(splitMsg, "Down") == 0){
            splitMsg = strtok(NULL," ");          // splitMsg equivalente msg[1]
            int row = atoi(splitMsg);
            printf("%i ", row);

            splitMsg = strtok(NULL," ");          // splitMsg equivalente msg[2]
            int col = atoi(splitMsg);
            printf("%i \n", col);

            mainGame->matrix[row][col] = BRICK_SPEED_DOWN;

            printf("Entendido!\n");

        }
    }


        //====================    Actualizar la longitud de la raqueta    =======================//

    else if (strcmp(splitMsg, "RacketLenght") == 0){
        splitMsg=strtok(NULL," ");          // splitMsg equivalente msg[1]
        printf("%s ", splitMsg);

        if (strcmp(splitMsg, "Up") == 0){
            splitMsg = strtok(NULL," ");          // splitMsg equivalente msg[2]
            int row = atoi(splitMsg);
            printf("%i ", row);

            splitMsg = strtok(NULL," ");          // splitMsg equivalente msg[3]
            int col = atoi(splitMsg);
            printf("%i \n", col);
            mainGame->matrix[row][col] = BRICK_RACKET_UP;
            printf("Entendido!\n");

        }
        else if (strcmp(splitMsg, "Down") == 0){
            splitMsg = strtok(NULL," ");          // splitMsg equivalente msg[2]
            int row = atoi(splitMsg);
            printf("%i ", row);

            splitMsg = strtok(NULL," ");          // splitMsg equivalente msg[3]
            int col = atoi(splitMsg);
            printf("%i \n", col);

            mainGame->matrix[row][col] = BRICK_RACKET_DOWN;
            printf("Entendido!\n");

        }
    }
    else{
        //printf("\n");
    }
}
