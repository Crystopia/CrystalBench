package net.crystopia.crystalbench.resourcepack

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import gg.flyte.twilight.gson.toJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import net.crystopia.crystalbench.CrystalBenchPlugin
import net.crystopia.crystalbench.api.CrystalItems
import net.crystopia.crystalbench.config.ConfigManager
import net.crystopia.crystalbench.config.models.PackObject
import net.crystopia.crystalbench.utils.ResourcePackVersion
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.inventory.meta.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.min

object ResourcePackManager {

    private val logger = CrystalBenchPlugin.instance.logger
    private val outputZip = Path.of("plugins/CrystalBench/out/pack.zip")
    private val externalPackFolder = File("plugins/CrystalBench/pack/external_packs")
    private val packPNG = File("plugins/CrystalBench/pack/pack.png")
    private val packVersion: Int =
        ResourcePackVersion.getPackFormat(CrystalBenchPlugin.instance.server.minecraftVersion)
    private val packDescription = ConfigManager.settings.Pack.descrption

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    private val tools = arrayOf("PICKAXE", "SWORD", "HOE", "AXE", "SHOVEL")

    fun buildPack() {
        logger.info("Building CrystalBench resource pack for Minecraft 1.21.4...")

        // Create output directory if it doesn't exist
        outputZip.parent.toFile().mkdirs()

        // Create a map to hold all pack files with their paths
        val packFiles = mutableMapOf<String, ByteArray>()

        // Add pack.mcmeta
        val packMeta = buildJsonObject {
            putJsonObject("pack") {
                put("pack_format", JsonPrimitive(22)) // Pack format for 1.21.4
                put("description", JsonPrimitive(packDescription))
            }
        }.toString()
        packFiles["pack.mcmeta"] = packMeta.toByteArray(Charsets.UTF_8)

        // Add pack.png if exists
        if (packPNG.exists()) {
            packFiles["pack.png"] = packPNG.readBytes()
        }

        // Process external packs
        if (externalPackFolder.exists()) {
            externalPackFolder.listFiles()?.filter { it.isDirectory }?.forEach { dir ->
                logger.info("Merging external pack: ${dir.name}")
                val externalPackPath = dir.toPath()

                Files.walk(externalPackPath).forEach { path ->
                    val file = path.toFile()
                    if (file.isFile) {
                        val relative = externalPackPath.relativize(path).toString().replace("\\", "/")
                        packFiles[relative] = file.readBytes()
                        logger.fine("Added external file: $relative")
                    }
                }
            }
        }

        // Add custom item models
        CrystalItems.items().forEach { (id, itemObj) ->
            val pack: PackObject = itemObj.pack ?: return@forEach
            val namespace = pack.namespace ?: "minecraft"
            val itemName = id.lowercase()
            val itemMaterial = itemObj.material

            // Generate main item model
            val modelPath = "assets/$namespace/models/item/$itemName.json"
            val modelJson = buildItemModelJson(itemMaterial!!, pack, namespace, itemName)
            packFiles[modelPath] = modelJson.toByteArray(Charsets.UTF_8)
            logger.info("Added custom item model: $modelPath")

            // Generate additional models if specified
            generateAdditionalModels(itemMaterial!!, pack, namespace, itemName, packFiles)
        }

        // Write all files to zip
        ZipOutputStream(Files.newOutputStream(outputZip)).use { zipOut ->
            packFiles.forEach { (path, bytes) ->
                val entry = ZipEntry(path)
                zipOut.putNextEntry(entry)
                zipOut.write(bytes)
                zipOut.closeEntry()
            }
        }

        logger.info("Resource pack written to: ${outputZip.toAbsolutePath()}")
    }

    private fun buildItemModelJson(
        itemMaterial: Material, pack: PackObject, namespace: String, itemName: String
    ): String {
        val modelJsonElement = buildJsonObject {
            put("parent", JsonPrimitive(pack.parentModel ?: getParentModel(itemMaterial)))

            // Handle textures
            pack.textures?.takeIf { it.isNotEmpty() }?.let { textures ->
                putJsonObject("textures") {
                    if (shouldUseLayers(itemMaterial, pack)) {
                        textures.forEachIndexed { index, texture ->
                            put("layer$index", JsonPrimitive(texture))
                        }
                    } else {
                        handleSpecialTextures(itemMaterial, pack, this)
                    }
                }
            }

            // Handle custom model data overrides
            put("overrides", buildJsonArray {
                // Function to add model with predicates
                fun addModel(path: String?, vararg predicates: Pair<String, Any>) {
                    if (!path.isNullOrBlank()) {
                        add(buildJsonObject {
                            putJsonObject("predicate") {
                                predicates.forEach { (k, v) ->
                                    when (v) {
                                        is Number -> put(k, JsonPrimitive(v))
                                        is Boolean -> put(k, JsonPrimitive(v))
                                        is String -> put(k, JsonPrimitive(v))
                                        is List<*> -> {
                                            val jsonArray = JsonArray()
                                            when {
                                                v.all { it is Float } -> (v as List<Float>).forEach { jsonArray.add(it) }
                                                v.all { it is Boolean } -> (v as List<Boolean>).forEach {
                                                    jsonArray.add(
                                                        it
                                                    )
                                                }

                                                v.all { it is String } -> (v as List<String>).forEach { jsonArray.add(it) }
                                                else -> error("Unsupported list type for predicate: $k")
                                            }
                                            put(k, jsonArray.toJson())
                                        }

                                        else -> error("Unsupported type for predicate: ${v::class}")
                                    }
                                }
                            }
                            put("model", JsonPrimitive(path))
                        })
                    }
                }

                // Handle float values
                pack.customModelData.floats?.forEach { floatValue ->
                    addModel(pack.model, "custom_model_data" to floatValue)
                    pack.blockingModel?.let { addModel(it, "custom_model_data" to floatValue, "blocking" to 1.0) }
                    pack.pullingModel?.let { addModel(it, "custom_model_data" to floatValue, "pulling" to 1.0) }
                    pack.chargedModel?.let { addModel(it, "custom_model_data" to floatValue, "charged" to 1.0) }
                    pack.fireWorkModel?.let { addModel(it, "custom_model_data" to floatValue, "firework" to 1.0) }
                    pack.castModel?.let { addModel(it, "custom_model_data" to floatValue, "cast" to 1.0) }
                    pack.damagedModel?.let { addModel(it, "custom_model_data" to floatValue, "damaged" to 1.0) }
                }

                // Handle string values
                pack.customModelData.strings?.forEach { stringValue ->
                    addModel(pack.model, "custom_model_data" to stringValue)
                }

                // Handle boolean values
                pack.customModelData.flags?.forEach { flagValue ->
                    addModel(pack.model, "custom_model_data" to flagValue)
                }

                // Fallback if no custom model data is specified
                if (pack.customModelData.floats.isNullOrEmpty() && pack.customModelData.strings.isNullOrEmpty() && pack.customModelData.flags.isNullOrEmpty()) {
                    addModel(pack.model)
                    pack.blockingModel?.let { addModel(it, "blocking" to 1.0) }
                    pack.pullingModel?.let { addModel(it, "pulling" to 1.0) }
                    pack.chargedModel?.let { addModel(it, "charged" to 1.0) }
                    pack.fireWorkModel?.let { addModel(it, "firework" to 1.0) }
                    pack.castModel?.let { addModel(it, "cast" to 1.0) }
                    pack.damagedModel?.let { addModel(it, "damaged" to 1.0) }
                }

                // Add item-specific model if specified
                pack.itemModel?.let { itemModel ->
                    val modelPath = "$namespace:item/$itemModel"
                    pack.customModelData.floats?.forEach { floatValue ->
                        addModel(modelPath, "custom_model_data" to floatValue)
                    }
                    pack.customModelData.strings?.forEach { stringValue ->
                        addModel(modelPath, "custom_model_data" to stringValue)
                    }
                    pack.customModelData.flags?.forEach { flagValue ->
                        addModel(modelPath, "custom_model_data" to flagValue)
                    }
                }
            })
        }

        return json.encodeToString(modelJsonElement)
    }

    private fun generateAdditionalModels(
        itemMaterial: Material,
        pack: PackObject,
        namespace: String,
        itemName: String,
        packFiles: MutableMap<String, ByteArray>
    ) {
        // Generate blocking model if specified
        pack.blockingModel?.let { modelPath ->
            val blockingModelJson = buildJsonObject {
                put("parent", JsonPrimitive(pack.parentModel ?: getParentModel(itemMaterial)))
                putJsonObject("textures") {
                    put("layer0", JsonPrimitive(pack.blockingModel))
                }
            }
            packFiles["assets/$namespace/models/item/${modelPath.split('/').last()}.json"] =
                json.encodeToString(blockingModelJson).toByteArray(Charsets.UTF_8)
        }

        // Generate pulling model if specified
        pack.pullingModel?.let { modelPath ->
            val pullingModelJson = buildJsonObject {
                put("parent", JsonPrimitive(pack.parentModel ?: getParentModel(itemMaterial)))
                putJsonObject("textures") {
                    put("layer0", JsonPrimitive(pack.pullingModel))
                }
            }
            packFiles["assets/$namespace/models/item/${modelPath.split('/').last()}.json"] =
                json.encodeToString(pullingModelJson).toByteArray(Charsets.UTF_8)
        }

        // Generate other special models similarly...
    }

    private fun shouldUseLayers(itemMaterial: Material, pack: PackObject): Boolean {
        val parent = pack.parentModel ?: getParentModel(itemMaterial)
        return !(parent.startsWith("block/") || parent == "builtin/entity" || itemMaterial?.let { isSpecialItemType(it) } == true)
    }

    private fun isSpecialItemType(material: Material): Boolean {
        return material == Material.TIPPED_ARROW || material == Material.FIREWORK_STAR || material == Material.POTION || material == Material.SPLASH_POTION || material == Material.LINGERING_POTION || material == Material.LEATHER_HORSE_ARMOR
    }

    private fun handleSpecialTextures(itemMaterial: Material, pack: PackObject, texturesObj: JsonObjectBuilder) {
        when (itemMaterial) {
            Material.TIPPED_ARROW -> {
                texturesObj.put("layer0", JsonPrimitive("item/tipped_arrow_head"))
                texturesObj.put("layer1", JsonPrimitive("item/tipped_arrow_base"))
            }

            Material.FIREWORK_STAR -> {
                texturesObj.put("layer0", JsonPrimitive(pack.textures?.get(0) ?: ""))
                texturesObj.put("layer1", JsonPrimitive("${pack.textures?.get(0)}_overlay"))
            }

            Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION -> {
                texturesObj.put("layer0", JsonPrimitive("item/potion_overlay"))
                texturesObj.put("layer1", JsonPrimitive(pack.textures?.get(0) ?: ""))
            }

            else -> {
                // Default case - use first texture as layer0
                pack.textures?.firstOrNull()?.let {
                    texturesObj.put("layer0", JsonPrimitive(it))
                }
            }
        }
    }

    private fun getParentModel(material: Material?): String {
        material ?: return "item/generated"

        val materialName = material.name.lowercase()
        return when {
            material == Material.SNOW -> "block/snow_height2"
            material == Material.FISHING_ROD || material == Material.WARPED_FUNGUS_ON_A_STICK || material == Material.CARROT_ON_A_STICK -> "item/handheld_rod"

            material == Material.SCAFFOLDING -> "block/scaffolding_stable"
            material == Material.RESPAWN_ANCHOR -> "block/respawn_anchor_0"
            material == Material.CONDUIT || material == Material.SHIELD || material == Material.CHEST || material == Material.TRAPPED_CHEST || material == Material.ENDER_CHEST -> "builtin/entity"

            material == Material.SMALL_DRIPLEAF -> "block/small_dripleaf_top"
            material == Material.SPORE_BLOSSOM || material == Material.BIG_DRIPLEAF || material == Material.AZALEA || material == Material.FLOWERING_AZALEA -> "block/$materialName"

            material == Material.CHORUS_FLOWER || material == Material.CHORUS_PLANT || material == Material.END_ROD -> "block/$materialName"

            material == Material.SMALL_AMETHYST_BUD || material == Material.MEDIUM_AMETHYST_BUD || material == Material.LARGE_AMETHYST_BUD -> "item/amethyst_bud"

            material == Material.AMETHYST_CLUSTER -> "item/generated"
            tools.any { material.name.contains(it) } -> "item/handheld"
            Tag.SHULKER_BOXES.isTagged(material) -> "item/template_shulker_box"
            Tag.BEDS.isTagged(material) -> "item/template_bed"
            Tag.BANNERS.isTagged(material) -> "item/template_banner"
            Tag.CARPETS.isTagged(material) || material == Material.MOSS_CARPET -> "block/$materialName"
            material.isBlock && material.isSolid -> "block/$materialName"
            else -> "item/generated"
        }
    }
}