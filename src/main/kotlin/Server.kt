package org.example

import java.net.ServerSocket

fun main() {
    val serverSocket = ServerSocket(4221)
    serverSocket.reuseAddress = true
    serverSocket.reuseAddress = true

    val socket = serverSocket.accept()
    println("New connection has been accepted")

    socket.use {
        val writer = socket.getOutputStream().bufferedWriter()
        writer.write("HTTP/1.1 200 OK\r\n\r\n")
        writer.flush()
    }
}
