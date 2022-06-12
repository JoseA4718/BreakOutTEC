#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>
#include <stdbool.h>
#include <Windows.h>

#include "JsonManager.c"
#include "Constants.h"
#include "../lib/json-maker.c"
#include "../lib/tiny-json.c"
#include "Server.c"

/**
 * myThread: función que se ejecuta en un thread aparte
 * Se encarga de escuchar al usuario desde consola
 */
DWORD WINAPI myThread(LPVOID lpParam){
    printf("Thread running! Escuchando sus sugerencias.. \n");
    char input[100];
    char buff[DEFAULT_BUFLEN];
    while(1){
        gets(input);
        printf("Ha ingresado: %s\n", input);

        //Analizar el input del usuario
        receiveUserMessage(input);
        Sleep(2 * 1000);
    }
    return 0;
}


/**
 * main Función principal para ejecutar el server
 * @param argc cantidad de argumentos
 * @param argv argumentos
 * @return
 */
int main(int argc,char* argv[]) {

//======================  MANEJO DE ARGUMENTOS  =======================//
    int counter;
    printf("Program Name Is: %s\n",argv[0]);

    //Si no se ingresó algún argumento
    if(argc==1)
        printf("No Extra Command Line Argument Passed Other Than Program Name \n");

    //Imprime los valores de los argumentos ingresados
    if(argc>=2) {
        printf("Number Of Arguments Passed: %d \n",argc);
        printf("----Following Are The Command Line Arguments Passed---- \n");
        for(counter=0;counter<argc;counter++)
            printf("argv[%d]: %s\n",counter,argv[counter]);
    }

//=======================================================================//

    //Crear juego
    mainGame = (game_t *) malloc(sizeof(game_t));
    createGame(atoi(argv[1]), atoi(argv[2]), atoi(argv[3]), atoi(argv[4]), INIT_BALL_SPEED, INIT_LVL, INIT_SCORE);

    //Thread
    HANDLE hThread;
    DWORD threadID;
    hThread = CreateThread(NULL, 0, myThread, NULL, 0, &threadID);
    if (hThread == NULL){
        printf("No se ha podido crear el thread \n");
    }
    printf("Thread creado, su id es: %d \n", threadID);


    /* Si el .exe se ejecuta con al menos 5 argumentos
        argv[0] = nombreArchivo
        argv[1] = valor ladrillo verde
        ...
    */

    if (argc = MINARGS){
        run();
    }
    else{
        printf("No se lograron asignar valores a los ladrillos \n");
    }

    //=======================================================================//
    //CloseHandle(hThread);
    //system("PAUSE");

    return 0;
} 
