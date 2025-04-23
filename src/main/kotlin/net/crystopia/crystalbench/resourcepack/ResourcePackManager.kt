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
import net.crystopia.crystalbench.utils.*
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.inventory.meta.*
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
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
        ignoreUnknownKeys = true
    }

    private val tools = arrayOf("PICKAXE", "SWORD", "HOE", "AXE", "SHOVEL")
    private val availableLanguageCodes = setOf(
        "af_za",
        "ar_sa",
        "ast_es",
        "az_az",
        "ba_ru",
        "bar",
        "be_by",
        "bg_bg",
        "br_fr",
        "brb",
        "bs_ba",
        "ca_es",
        "cs_cz",
        "cy_gb",
        "da_dk",
        "de_at",
        "de_ch",
        "de_de",
        "el_gr",
        "en_au",
        "en_ca",
        "en_gb",
        "en_nz",
        "en_pt",
        "en_ud",
        "en_us",
        "enp",
        "enws",
        "eo_uy",
        "es_ar",
        "es_cl",
        "es_ec",
        "es_es",
        "es_mx",
        "es_uy",
        "es_ve",
        "esan",
        "et_ee",
        "eu_es",
        "fa_ir",
        "fi_fi",
        "fil_ph",
        "fo_fo",
        "fr_ca",
        "fr_fr",
        "fra_de",
        "fur_it",
        "fy_nl",
        "ga_ie",
        "gd_gb",
        "gl_es",
        "haw_us",
        "he_il",
        "hi_in",
        "hr_hr",
        "hu_hu",
        "hy_am",
        "id_id",
        "ig_ng",
        "io_en",
        "is_is",
        "isv",
        "it_it",
        "ja_jp",
        "jbo_en",
        "ka_ge",
        "kk_kz",
        "kn_in",
        "ko_kr",
        "ksh",
        "kw_gb",
        "la_la",
        "lb_lu",
        "li_li",
        "lmo",
        "lol_us",
        "lt_lt",
        "lv_lv",
        "lzh",
        "mk_mk",
        "mn_mn",
        "ms_my",
        "mt_mt",
        "nah",
        "nds_de",
        "nl_be",
        "nl_nl",
        "nn_no",
        "no_no",
        "oc_fr",
        "ovd",
        "pl_pl",
        "pt_br",
        "pt_pt",
        "qya_aa",
        "ro_ro",
        "rpr",
        "ru_ru",
        "ry_ua",
        "se_no",
        "sk_sk",
        "sl_si",
        "so_so",
        "sq_al",
        "sr_sp",
        "sv_se",
        "sxu",
        "szl",
        "ta_in",
        "th_th",
        "tl_ph",
        "tlh_aa",
        "tok",
        "tr_tr",
        "tt_ru",
        "uk_ua",
        "val_es",
        "vec_it",
        "vi_vn",
        "yi_de",
        "yo_ng",
        "zh_cn",
        "zh_hk",
        "zh_tw",
        "zlm_arab"
    )

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

        // Generate font files
        generateFontFiles(packFiles)

        // Add custom item models from CrystalItems
        val texturedItems = mutableMapOf<Material, MutableMap<String, PackObject>>()
        CrystalItems.items().forEach { (id, itemObj) ->
            val pack: PackObject = itemObj.pack ?: return@forEach
            val namespace = pack.namespace ?: "minecraft"
            val itemName = id.lowercase()
            val itemMaterial = itemObj.material ?: return@forEach

            // Group items by material for model definitions
            texturedItems.computeIfAbsent(itemMaterial) { mutableMapOf() }[id] = pack

            // Generate main item model
            val modelPath = "assets/$namespace/models/item/$itemName.json"
            val modelJson = buildItemModelJson(itemMaterial, pack, namespace, itemName)
            packFiles[modelPath] = modelJson.toByteArray(Charsets.UTF_8)
            logger.info("Added custom item model: $modelPath")

            // Generate additional models if specified
            generateAdditionalModels(itemMaterial, pack, namespace, itemName, packFiles)
        }

        // Generate model definitions for 1.21.4+
        generateModelDefinitions(texturedItems, packFiles)

        // Generate atlas file
        generateAtlasFile(packFiles)

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

    private fun generateModelDefinitions(
        texturedItems: Map<Material, Map<String, PackObject>>, packFiles: MutableMap<String, ByteArray>
    ) {
        texturedItems.forEach { (material, items) ->
            items.forEach { (itemId, pack) ->
                val namespace = pack.namespace ?: "minecraft"
                val modelDefinition = buildJsonObject {
                    putJsonObject("model") {
                        put("type", JsonPrimitive("minecraft:model"))
                        put(
                            "model", JsonPrimitive(pack.model?.substringAfterLast('/')?.removeSuffix(".json") ?: itemId)
                        )

                        if (needsTinting(material)) {
                            putJsonArray("tints") {
                                addJsonObject {
                                    when {
                                        material.name.startsWith("LEATHER_") -> {
                                            put("type", JsonPrimitive("minecraft:dye"))
                                            put("default", JsonPrimitive(16578808)) // Default leather color
                                        }

                                        material == Material.POTION || material == Material.SPLASH_POTION || material == Material.LINGERING_POTION -> {
                                            put("type", JsonPrimitive("minecraft:potion"))
                                            put("default", JsonPrimitive(16253176)) // Default water color
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                val modelDefPath = "assets/$namespace/models/item/${itemId}_definition.json"
                packFiles[modelDefPath] = json.encodeToString(modelDefinition).toByteArray(Charsets.UTF_8)
                logger.fine("Generated model definition: $modelDefPath")
            }
        }
    }

    private fun generateFontFiles(packFiles: MutableMap<String, ByteArray>) {
        // Generate default font file
        val defaultFont = buildJsonObject {
            putJsonArray("providers") {
                // Add your font providers here
                // Example:
                addJsonObject {
                    put("type", JsonPrimitive("bitmap"))
                    put("file", JsonPrimitive("minecraft:font/ascii.png"))
                    put("ascent", JsonPrimitive(7))
                    put("height", JsonPrimitive(8))
                    putJsonArray("chars") {
                        add(JsonPrimitive("\u0000\u00ff"))
                    }
                }
            }
        }

        packFiles["assets/minecraft/font/default.json"] = json.encodeToString(defaultFont).toByteArray(Charsets.UTF_8)

    }

    private fun generateAtlasFile(packFiles: MutableMap<String, ByteArray>) {
        val atlas = buildJsonObject {
            putJsonArray("sources") {
                // Add texture sources here
                // Example for directory-based atlas:
                addJsonObject {
                    put("type", JsonPrimitive("directory"))
                    put("source", JsonPrimitive("items"))
                    put("prefix", JsonPrimitive("items/"))
                }

                // Example for single texture entries:
                packFiles.keys.filter { it.startsWith("assets/") && it.endsWith(".png") }.forEach { path ->
                    val namespace = path.substringAfter("assets/").substringBefore("/")
                    val texturePath = path.substringAfter("textures/").removeSuffix(".png")
                    addJsonObject {
                        put("type", JsonPrimitive("single"))
                        put("resource", JsonPrimitive("$namespace:$texturePath"))
                        put("sprite", JsonPrimitive("$namespace:$texturePath"))
                    }
                }
            }
        }

        packFiles["assets/minecraft/atlases/blocks.json"] = json.encodeToString(atlas).toByteArray(Charsets.UTF_8)
    }

    private fun buildItemModelJson(
        itemMaterial: Material, pack: PackObject, namespace: String, itemName: String
    ): String {
        val modelJsonElement = buildJsonObject {
            // Parent Model
            put("parent", JsonPrimitive(pack.parentModel ?: getParentModel(itemMaterial)))

            // Textures
            putJsonObject("textures") {
                when {
                    // Wenn Texturen explizit angegeben sind
                    !pack.textures.isNullOrEmpty() -> {
                        pack.textures!!.forEachIndexed { index, texturePath ->
                            val (texNamespace, texPath) = parseNamespacePath(texturePath, namespace)
                            put("layer$index", JsonPrimitive("$texNamespace:$texPath"))
                        }
                    }

                    // Wenn ein Modell angegeben ist, aber keine Texturen
                    pack.model != null -> {
                        // Versuche, die Textur aus dem Modellpfad abzuleiten
                        val modelPath = pack.model!!
                        val texturePath = if (modelPath.contains("item/") || modelPath.contains("block/")) {
                            modelPath.replace("models/", "textures/").replace(".json", ".png")
                        } else {
                            "item/${modelPath.substringAfterLast('/').removeSuffix(".json")}"
                        }
                        val (texNamespace, texPath) = parseNamespacePath(texturePath, namespace)
                        put("layer0", JsonPrimitive("$texNamespace:$texPath"))
                    }

                    // Fallback: Standard-Texturpfad
                    else -> {
                        val fallbackPath = "item/$itemName"
                        put("layer0", JsonPrimitive("$namespace:$fallbackPath"))
                    }
                }
            }

            // Overrides für Custom Model Data
            val overridesArray = buildOverrides(pack, namespace, itemName)
            if (!overridesArray.isEmpty()) {
                put("overrides", Json.encodeToString(overridesArray))
            }
        }

        return json.encodeToString(modelJsonElement)
    }

    private fun buildOverrides(pack: PackObject, namespace: String, itemName: String): JsonArray {
        val overrides = JsonArray()

        fun addOverride(modelPath: String, vararg predicates: Pair<String, Any>) {
            val (modelNamespace, modelPathParsed) = parseNamespacePath(modelPath, namespace)
            val modelLocation = if (modelPathParsed.startsWith("item/") || modelPathParsed.startsWith("block/")) {
                modelPathParsed
            } else {
                "item/$modelPathParsed"
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

            val override = JsonObject().apply {
                add("predicate", predicateObj)
                addProperty("model", "$modelNamespace:$modelLocation")
            }
            overrides.add(override)
        }

        // Custom Model Data Overrides
        pack.customModelData.floats?.forEach { floatValue ->
            // Hauptmodell mit Custom Model Data
            pack.model?.let { addOverride(it, "custom_model_data" to floatValue) }

            // Spezialmodelle mit Custom Model Data
            pack.blockingModel?.let { addOverride(it, "custom_model_data" to floatValue, "blocking" to 1.0f) }
            pack.pullingModel?.let { addOverride(it, "custom_model_data" to floatValue, "pulling" to 1.0f) }
            pack.chargedModel?.let { addOverride(it, "custom_model_data" to floatValue, "charged" to 1.0f) }
            pack.fireWorkModel?.let { addOverride(it, "custom_model_data" to floatValue, "firework" to 1.0f) }
            pack.castModel?.let { addOverride(it, "custom_model_data" to floatValue, "cast" to 1.0f) }
            pack.damagedModel?.let { addOverride(it, "custom_model_data" to floatValue, "damaged" to 1.0f) }
        }

        // String-basierte Custom Model Data
        pack.customModelData.strings?.forEach { stringValue ->
            pack.model?.let { addOverride(it, "custom_model_data" to stringValue) }
        }

        // Flags für Custom Model Data
        pack.customModelData.flags?.forEach { flagValue ->
            pack.model?.let { addOverride(it, "custom_model_data" to flagValue) }
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
            if (modelPath.isNullOrBlank()) return

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
                putJsonObject("textures") {
                    texturePath?.let {
                        val (texNamespace, texPath) = parseNamespacePath(it, namespace)
                        put("layer0", JsonPrimitive("$texNamespace:$texPath"))
                    } ?: pack.textures?.firstOrNull()?.let {
                        val (texNamespace, texPath) = parseNamespacePath(it, namespace)
                        put("layer0", JsonPrimitive("$texNamespace:$texPath"))
                    }
                }
            }

            val fullPath = "assets/$modelNamespace/models/$modelDir/$modelName.json"
            if (!packFiles.containsKey(fullPath)) {
                packFiles[fullPath] = json.encodeToString(modelJson).toByteArray(Charsets.UTF_8)
                logger.info("Generated model file: $fullPath")
            }
        }

        // Generiere alle zusätzlichen Modelle
        createModelFile(pack.model)
        createModelFile(pack.blockingModel, pack.blockingModel!!)
        createModelFile(pack.pullingModel, pack.pullingModel)
        createModelFile(pack.chargedModel, pack.chargedModel)
        createModelFile(pack.fireWorkModel, pack.fireWorkModel)
        createModelFile(pack.castModel, pack.castModel)
        createModelFile(pack.damagedModel, pack.damagedModel)
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

    private fun needsTinting(material: Material): Boolean {
        return material.name.startsWith("LEATHER_") || material == Material.POTION || material == Material.SPLASH_POTION || material == Material.LINGERING_POTION
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