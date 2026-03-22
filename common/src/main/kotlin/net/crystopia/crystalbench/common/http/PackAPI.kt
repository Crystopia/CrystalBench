package net.crystopia.crystalbench.common.http

object PackAPI {

    fun init() {
        // Start Web API

        /**
         * TODO
         */
        /*
         embeddedServer(Netty, port = ConfigManager.settings.Pack.port ?: 8080) {
            routing {
                get("/") {
                    val zipFilePath = ConfigManager.settings.Pack.path
                    if (zipFilePath.isBlank()) {
                        call.respond(HttpStatusCode.InternalServerError, "Invalid zip file path in configuration.")
                        return@get
                    }

                    val file = File(zipFilePath)
                    if (!file.exists() || !file.isFile) {
                        call.respond(HttpStatusCode.NotFound, "404 - No file found!")
                        return@get
                    }

                    try {
                        call.response.header(HttpHeaders.ContentDisposition, "attachment; filename=\"pack.zip\"")
                        call.response.header(HttpHeaders.ContentType, ContentType.Application.Zip.toString())

                        file.inputStream().use { inputStream ->
                            call.respondOutputStream(ContentType.Application.Zip) { inputStream.copyTo(this) }
                        }
                    } catch (e: IOException) {
                        call.respond(HttpStatusCode.InternalServerError, "Error reading zip file: ${e.message}")
                    }
                }
            }
        }.start(wait = false)
         */
    }

}