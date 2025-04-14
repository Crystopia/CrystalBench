package net.crystopia.crystalbench.resourcepack

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import gg.flyte.twilight.gson.toJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import net.crystopia.crystalbench.CrystalBenchPlugin
import net.crystopia.crystalbench.api.CrystalItems
import net.crystopia.crystalbench.config.ConfigManager
import net.crystopia.crystalbench.config.models.CustomModelData
import net.crystopia.crystalbench.config.models.PackObject
import org.bukkit.Material
import org.bukkit.Tag
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ResourcePackManager {

    private val logger = CrystalBenchPlugin.instance.logger
    private val outputZip = Path.of("plugins/CrystalBench/out/pack.zip")
    private val packFolder = File("plugins/CrystalBench/pack")
    private val assetsFolder = File(packFolder, "assets")
    private val externalPackFolder = File(packFolder, "external_packs")
    private val packPNG = File(packFolder, "pack.png")
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

        // Add pack.mcmeta (format 22 for 1.21.4)
        val packMeta = buildJsonObject {
            putJsonObject("pack") {
                put("pack_format", JsonPrimitive(22))
                put("description", JsonPrimitive(packDescription))
            }
        }.toString()
        packFiles["pack.mcmeta"] = packMeta.toByteArray(Charsets.UTF_8)

        // Add pack.png if exists
        if (packPNG.exists()) {
            packFiles["pack.png"] = packPNG.readBytes()
        }

        // Process main pack assets
        if (assetsFolder.exists()) {
            Files.walk(assetsFolder.toPath()).forEach { path ->
                val file = path.toFile()
                if (file.isFile) {
                    val relative = packFolder.toPath().relativize(path).toString().replace("\\", "/")
                    packFiles[relative] = file.readBytes()
                    logger.fine("Added pack file: $relative")
                }
            }
        }

        // Process external packs and merge them into assets
        if (externalPackFolder.exists()) {
            externalPackFolder.listFiles()?.filter { it.isDirectory }?.forEach { externalPack ->
                logger.info("Merging external pack: ${externalPack.name}")

                // Process assets folder of external pack
                val externalAssets = File(externalPack, "assets")
                if (externalAssets.exists()) {
                    Files.walk(externalAssets.toPath()).forEach { path ->
                        val file = path.toFile()
                        if (file.isFile) {
                            val relative =
                                "assets/" + externalAssets.toPath().relativize(path).toString().replace("\\", "/")
                            packFiles[relative] = file.readBytes()
                            logger.fine("Added external asset: $relative")
                        }
                    }
                }

                // Process other files from root of external pack
                externalPack.listFiles()?.filter { it.isFile && it.name != "assets" }?.forEach { file ->
                    if (file.name != "pack.mcmeta") {
                        packFiles[file.name] = file.readBytes()
                        logger.fine("Added external file: ${file.name}")
                    }
                }
            }
        }

        // Add custom item models from CrystalItems
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
            generateAdditionalModels(itemMaterial, pack, namespace, itemName, packFiles)
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

        logger.info("Successfully built 1.21.4 resource pack at: ${outputZip.toAbsolutePath()}")
    }

    private fun buildItemModelJson(
        itemMaterial: Material, pack: PackObject, namespace: String, itemName: String
    ): String {
        val modelJsonElement = buildJsonObject {
            // Parent Model
            put("parent", JsonPrimitive(pack.parentModel ?: getParentModel(itemMaterial)))

            // Textures
            pack.textures?.takeIf { it.isNotEmpty() }?.let { textures ->
                putJsonObject("textures") {
                    if (shouldUseLayers(itemMaterial, pack)) {
                        textures.forEachIndexed { index, texture ->
                            val (textureNamespace, texturePath) = parseNamespacePath(texture, namespace)
                            put("layer$index", JsonPrimitive("$textureNamespace:$texturePath"))
                        }
                    } else {
                        handleSpecialTextures(itemMaterial, pack, this, namespace)
                    }
                }
            }

            // Overrides - nur hinzufügen wenn nicht leer
            val overridesArray = buildOverrides(pack, namespace, itemName).toJson()
            if (overridesArray.length > 0) {
                put("overrides", overridesArray)
            }
        }

        return json.encodeToString(modelJsonElement)
    }

    private fun buildOverrides(pack: PackObject, namespace: String, itemName: String): JsonArray {
        val overrides = JsonArray()

        fun addOverride(modelPath: String?, vararg predicates: Pair<String, Any>) {
            if (!modelPath.isNullOrBlank()) {
                val (modelNamespace, modelPathParsed) = parseNamespacePath(modelPath, namespace)
                val modelLocation = if (modelPathParsed.startsWith("item/") || modelPathParsed.startsWith("block/")) {
                    modelPathParsed // Already a full model path
                } else {
                    "item/$modelPathParsed" // Assume it's an item model
                }

                val predicateObj = JsonObject().apply {
                    predicates.forEach { (key, value) ->
                        when (value) {
                            is Float -> addProperty(key, value)
                            is Int -> addProperty(key, value)
                            is Boolean -> addProperty(key, value)
                            is String -> addProperty(key, value)
                            else -> error("Unsupported predicate type: ${value::class}")
                        }
                    }
                }

                // Nur hinzufügen wenn mindestens ein Predicate existiert oder modelPath gültig ist
                if (predicates.isNotEmpty() || modelPath.isNotBlank()) {
                    val override = JsonObject().apply {
                        add("predicate", predicateObj)
                        addProperty("model", "$modelNamespace:$modelLocation")
                    }
                    overrides.add(override)
                }
            }
        }

        // Rest der Funktion bleibt gleich...
        pack.customModelData.floats?.forEach { floatValue ->
            pack.model?.let { addOverride(it, "custom_model_data" to floatValue) }
            pack.blockingModel?.let { addOverride(it, "custom_model_data" to floatValue, "blocking" to 1.0f) }
            pack.pullingModel?.let { addOverride(it, "custom_model_data" to floatValue, "pulling" to 1.0f) }
            pack.chargedModel?.let { addOverride(it, "custom_model_data" to floatValue, "charged" to 1.0f) }
            pack.fireWorkModel?.let { addOverride(it, "custom_model_data" to floatValue, "firework" to 1.0f) }
            pack.castModel?.let { addOverride(it, "custom_model_data" to floatValue, "cast" to 1.0f) }
            pack.damagedModel?.let { addOverride(it, "custom_model_data" to floatValue, "damaged" to 1.0f) }
        }

        pack.customModelData.strings?.forEach { stringValue ->
            pack.model?.let { addOverride(it, "custom_model_data" to stringValue) }
        }

        pack.customModelData.flags?.forEach { flagValue ->
            pack.model?.let { addOverride(it, "custom_model_data" to flagValue) }
        }

        // Fallback wenn keine custom model data aber models definiert sind
        if (pack.customModelData.floats.isNullOrEmpty() && pack.customModelData.strings.isNullOrEmpty() && pack.customModelData.flags.isNullOrEmpty()) {

            pack.model?.let { addOverride(it) }
            pack.blockingModel?.let { addOverride(it, "blocking" to 1.0f) }
            pack.pullingModel?.let { addOverride(it, "pulling" to 1.0f) }
            pack.chargedModel?.let { addOverride(it, "charged" to 1.0f) }
            pack.fireWorkModel?.let { addOverride(it, "firework" to 1.0f) }
            pack.castModel?.let { addOverride(it, "cast" to 1.0f) }
            pack.damagedModel?.let { addOverride(it, "damaged" to 1.0f) }
        }

        return overrides
    }

    private fun parseNamespacePath(path: String, defaultNamespace: String): Pair<String, String> {
        return if (path.contains(':')) {
            val parts = path.split(':')
            parts[0] to parts[1]
        } else {
            defaultNamespace to path
        }
    }

    private fun generateAdditionalModels(
        itemMaterial: Material,
        pack: PackObject,
        namespace: String,
        itemName: String,
        packFiles: MutableMap<String, ByteArray>
    ) {
        fun createModelFile(modelPath: String?, texturePath: String? = null) {
            if (modelPath != null) {
                val (modelNamespace, modelPathParsed) = parseNamespacePath(modelPath, namespace)
                val fullModelPath = if (modelPathParsed.endsWith(".json")) {
                    modelPathParsed
                } else {
                    "$modelPathParsed.json"
                }

                val modelName = fullModelPath.substringAfterLast('/').removeSuffix(".json")
                val modelDir = fullModelPath.substringBeforeLast('/').ifEmpty { "item" }

                val modelJson = buildJsonObject {
                    put("parent", JsonPrimitive(pack.parentModel ?: getParentModel(itemMaterial)))

                    // Only add textures if they're provided
                    texturePath?.let {
                        val (texNamespace, texPath) = parseNamespacePath(it, namespace)
                        putJsonObject("textures") {
                            put("layer0", JsonPrimitive("$texNamespace:$texPath"))
                        }
                    } ?: pack.textures?.firstOrNull()?.let {
                        val (texNamespace, texPath) = parseNamespacePath(it, namespace)
                        putJsonObject("textures") {
                            put("layer0", JsonPrimitive("$texNamespace:$texPath"))
                        }
                    }
                }

                val fullPath = "assets/$modelNamespace/models/$modelDir/$modelName.json"
                packFiles[fullPath] = json.encodeToString(modelJson).toByteArray(Charsets.UTF_8)
                logger.info("Generated additional model: $fullPath")
            }
        }

        // Generate models for all special states
        createModelFile(pack.model, pack.textures?.firstOrNull())
        createModelFile(pack.blockingModel)
        createModelFile(pack.pullingModel)
        createModelFile(pack.chargedModel)
        createModelFile(pack.fireWorkModel)
        createModelFile(pack.castModel)
        createModelFile(pack.damagedModel)
    }

    private fun shouldUseLayers(itemMaterial: Material, pack: PackObject): Boolean {
        // If a custom model is specified, don't use layers
        if (pack.model != null) return false

        val parent = pack.parentModel ?: getParentModel(itemMaterial)
        return !(parent.startsWith("block/") || parent == "builtin/entity" || isSpecialItemType(itemMaterial))
    }

    private fun isSpecialItemType(material: Material): Boolean {
        return material == Material.TIPPED_ARROW || material == Material.FIREWORK_STAR || material == Material.POTION || material == Material.SPLASH_POTION || material == Material.LINGERING_POTION || material == Material.LEATHER_HORSE_ARMOR
    }

    private fun handleSpecialTextures(
        itemMaterial: Material, pack: PackObject, texturesObj: JsonObjectBuilder, namespace: String
    ) {
        when (itemMaterial) {
            Material.TIPPED_ARROW -> {
                texturesObj.put("layer0", JsonPrimitive("item/tipped_arrow_head"))
                texturesObj.put("layer1", JsonPrimitive("item/tipped_arrow_base"))
            }

            Material.FIREWORK_STAR -> {
                pack.textures?.firstOrNull()?.let {
                    val (texNamespace, texPath) = parseNamespacePath(it, namespace)
                    texturesObj.put("layer0", JsonPrimitive("$texNamespace:$texPath"))
                    texturesObj.put("layer1", JsonPrimitive("$texNamespace:${texPath}_overlay"))
                }
            }

            Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION -> {
                texturesObj.put("layer0", JsonPrimitive("item/potion_overlay"))
                pack.textures?.firstOrNull()?.let {
                    val (texNamespace, texPath) = parseNamespacePath(it, namespace)
                    texturesObj.put("layer1", JsonPrimitive("$texNamespace:$texPath"))
                }
            }

            else -> {
                pack.textures?.firstOrNull()?.let {
                    val (texNamespace, texPath) = parseNamespacePath(it, namespace)
                    texturesObj.put("layer0", JsonPrimitive("$texNamespace:$texPath"))
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