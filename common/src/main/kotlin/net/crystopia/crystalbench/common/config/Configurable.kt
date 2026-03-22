package net.crystopia.crystalbench.common.config

interface Configurable {
    fun save()
    fun load() {}
    fun reset() {}
}