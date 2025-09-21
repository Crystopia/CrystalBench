package net.crystopia.crystalbench.items

import net.crystopia.crystalbench.CrystalBenchPluginPaper
import org.bukkit.NamespacedKey

object CrystalKeys {
    val ID = NamespacedKey(CrystalBenchPluginPaper.instance, "id")
    val NAME = NamespacedKey(CrystalBenchPluginPaper.instance, "name")
    val PLAYSOUND = NamespacedKey(CrystalBenchPluginPaper.instance, "playSound")
    val TELEPORTRANDOMLY = NamespacedKey(CrystalBenchPluginPaper.instance, "teleportRandomly")
    val APPLYEFFECTS = NamespacedKey(CrystalBenchPluginPaper.instance, "applyEffects")
    val EFFECT = NamespacedKey(CrystalBenchPluginPaper.instance, "effect")
    val CONSUMABLE = NamespacedKey(CrystalBenchPluginPaper.instance, "consumable")
    val DISABLEENTCHANTING = NamespacedKey(CrystalBenchPluginPaper.instance, "disable_enchanting")
    val AMOUNT = NamespacedKey(CrystalBenchPluginPaper.instance, "amount")
    val CONSUMEPARTICLES = NamespacedKey(CrystalBenchPluginPaper.instance, "consumeParticles")
    val CONSUMESECONDS = NamespacedKey(CrystalBenchPluginPaper.instance, "consumeSeconds")
    val ANIMATION = NamespacedKey(CrystalBenchPluginPaper.instance, "animation")
    val REMOVEEFFECTS = NamespacedKey(CrystalBenchPluginPaper.instance, "removeEffects")
    val CLEARALLEFFECTS = NamespacedKey(CrystalBenchPluginPaper.instance, "clearAllEffects")
    val REPAIRABLE = NamespacedKey(
        CrystalBenchPluginPaper.instance,
        "repairable",
    )
}
