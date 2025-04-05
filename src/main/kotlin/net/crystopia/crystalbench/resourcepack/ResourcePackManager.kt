package net.crystopia.crystalbench.resourcepack

import net.crystopia.crystalbench.CrystalBenchPlugin
import net.crystopia.crystalbench.config.ConfigManager
import net.crystopia.crystalbench.utils.ResourcePackVersion
import team.unnamed.creative.ResourcePack
import team.unnamed.creative.base.Writable
import team.unnamed.creative.metadata.pack.PackMeta
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

object ResourcePackManager {


    var resourcePack: ResourcePack = ResourcePack.resourcePack()
    private val packPNG = File("plugins/CrystalBench/pack/pack.png")
    private val packVersion = ResourcePackVersion.getPackFormat(CrystalBenchPlugin.instance.server.minecraftVersion)
    private val packDescription = ConfigManager.settings.Pack.descrption

    fun buildPack() {
        resourcePack.packMeta(
            packVersion.toInt(), packDescription
        )
        if (packPNG.exists()) {
            resourcePack.icon(Writable.file(packPNG))
        }

        MinecraftResourcePackWriter.minecraft().writeToZipFile(
            File("plugins/CrystalBench/out/pack.zip"),
            resourcePack
        );

    }

}