#include <stdio.h>
#include <winsock2.h>
#include <string.h>
#include "Constants.h"
int iResult, iSendResult;
SOCKET sAcceptSocket;
char recvbuf[DEFAULT_BUFLEN];
char final[3]="\n";

/**
 * run
 * Socket Server
 * @return
 */
int run(){
    printf("\t\t------SERVER------\t\n");
    //VARIABLES LOCALES
    WSADATA Winsockdata;
    int iWasaStartup;
    SOCKET TCPServerSocket;
    struct sockaddr_in TCPServerAdd;
    struct sockaddr_in TCPClientAdd;
    int iTCPClientAdd=sizeof(TCPClientAdd);
    int iBind;
    int iListen;

    // WSAStartup
    iWasaStartup=WSAStartup(MAKEWORD(2,2),&Winsockdata);
    if(iWasaStartup!=0){
        printf("Fallo el WasaStartup\n");
    }

    //Estructura
    TCPServerAdd.sin_family=AF_INET;
    TCPServerAdd.sin_addr.s_addr=inet_addr("127.0.0.1");
    TCPServerAdd.sin_port=htons(PORT);

    //Creacion
    TCPServerSocket=socket(AF_INET,SOCK_STREAM,IPPROTO_TCP);
    if(TCPServerSocket == SOCKET_ERROR){
        printf("Fallo en el TCPServerSocket");
    }
    //Bind
    iBind=bind(TCPServerSocket,(SOCKADDR*)&TCPServerAdd,sizeof(TCPServerAdd));
    if(iBind==SOCKET_ERROR){
        printf("Fallo en bind\n");
    }
    //Listen
    iListen=listen(TCPServerSocket,2);
    if(iListen==SOCKET_ERROR){
        printf("Fallo en listen\n");
    }
    //Aceptar conexiones
    printf("\t\tEsperando Cliente.....\n");
    sAcceptSocket= accept(TCPServerSocket,(SOCKADDR*)&iTCPClientAdd,&iTCPClientAdd);
    if(sAcceptSocket==INVALID_SOCKET){
        printf("Fallo en aceptar\n");
    }
    printf("Cliente Conectado.....\n");
    int recvbuflen = DEFAULT_BUFLEN;


// Recibir hasta que la conexion sea cerrada
    do {
        char buff[DEFAULT_BUFLEN];
        nextLevel();
        //Verifica si el jugador perdió
        if(lost()){
            printf("You lost");
        }
        memset(recvbuf,0,recvbuflen);
        iResult = recv(sAcceptSocket, recvbuf, recvbuflen, 0);

        if (iResult > 0) {
            receiveClientMessage(recvbuf);
            jsonGame(buff);
            char* token=strtok(buff,"{ }");
            strcat(token,final);
            //printf("JSON: %s\n", token);
            iSendResult = send(sAcceptSocket, token, strlen(token)+1, 0);
            //memset(buff,0,strlen(buff));
            //memset(token,0,strlen(token));

            if (iSendResult == SOCKET_ERROR) {
                printf("Envio fallido: %d\n", WSAGetLastError());
                closesocket(sAcceptSocket);
                WSACleanup();
                return 1;
            }
        } else if (iResult == 0)
            printf("Cerrando conexion...\n");
        else {
            printf("recv failed: %d\n", WSAGetLastError());
            closesocket(sAcceptSocket);
            WSACleanup();
            return 1;
        }
    } while (iResult > 0);
}
