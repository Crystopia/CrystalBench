package net.crystopia.crystalbench.config

interface Configurable {
    fun save()
    fun load() {}
    fun reset() {}
}