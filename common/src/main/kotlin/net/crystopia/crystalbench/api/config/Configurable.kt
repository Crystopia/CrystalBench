package net.crystopia.crystalbench.api.config

interface Configurable {
    fun save()
    fun load() {}
    fun reset() {}
}