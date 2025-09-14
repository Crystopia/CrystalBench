package net.crystopia.crystalbench.utils

import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType

object StringListPersistentDataType : PersistentDataType<String, List<String>> {
    override fun getPrimitiveType(): Class<String> = String::class.java
    override fun getComplexType(): Class<List<String>> = List::class.java as Class<List<String>>

    override fun toPrimitive(complex: List<String>, context: PersistentDataAdapterContext): String {
        return complex.joinToString(";;") // z. B. mit `;;` trennen
    }

    override fun fromPrimitive(primitive: String, context: PersistentDataAdapterContext): List<String> {
        return if (primitive.isEmpty()) emptyList() else primitive.split(";;")
    }
}
