package org.example

import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

fun main() {
    val serverSocket = ServerSocket(4221)
    serverSocket.reuseAddress = true
    val threadPool = Executors.newFixedThreadPool(10)

    while (true) {
        val socket = serverSocket.accept()
        threadPool.submit {
            handle(socket)
        }
    }
}

fun handle(socket: Socket) {
    socket.use {
        val writer = socket.getOutputStream().bufferedWriter()
        val reader = socket.getInputStream().bufferedReader()
        val request = reader.readLine()
        val headers = mutableMapOf<String, String>()
        var read = reader.readLine()
        while (read != null && read.isNotEmpty()) {
            read.split(":").also { strings ->
                headers[strings[0]] = strings[1].trim()
            }
            read = reader.readLine()
        }
        println("request: $request")
        println("headers: $headers")
        val requestArgs = request.split(Regex("\\s+"))
        val requestTarget = requestArgs[1]
        if (requestTarget == "/") {
            writer.write("HTTP/1.1 200 OK\r\n\r\n")
        } else if (requestTarget.startsWith("/echo/")) {
            val str = requestTarget.substringAfter("/echo/")
            writer.write(
                "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: ${
                    str.encodeToByteArray().size
                }\r\n\r\n$str"
            )
        } else if (requestTarget == "/user-agent") {
            val str = headers["User-Agent"]!!
            writer.write(
                "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: ${
                    str.encodeToByteArray().size
                }\r\n\r\n$str"
            )
        } else {
            writer.write("HTTP/1.1 404 Not Found\r\n\r\n")
        }
        writer.flush()
    }
}
