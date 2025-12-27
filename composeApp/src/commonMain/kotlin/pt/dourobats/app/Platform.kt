package pt.dourobats.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform