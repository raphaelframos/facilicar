package com.raar.facilicar

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform