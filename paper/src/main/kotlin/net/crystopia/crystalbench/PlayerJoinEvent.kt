package net.crystopia.crystalbench

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import io.netty.handler.codec.MessageToMessageDecoder
import net.crystopia.crystalshard.builder.EntityBuilder
import net.minecraft.Optionull
import net.minecraft.network.chat.RemoteChatSession
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket
import net.minecraft.network.protocol.game.ServerboundInteractPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import org.bukkit.Material
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*
import java.util.function.Function


object PlayerJoinEvent : Listener {

    private fun getEntry(npcPlayer: ServerPlayer, gameProfile: GameProfile): ClientboundPlayerInfoUpdatePacket.Entry {

        return ClientboundPlayerInfoUpdatePacket.Entry(
            npcPlayer.getUUID(),
            gameProfile,
            true,
            0,
            npcPlayer.gameMode.gameModeForPlayer,
            npcPlayer.tabListDisplayName,
            true,
            -1,
            Optionull.map<RemoteChatSession?, RemoteChatSession.Data?>(
                npcPlayer.chatSession, Function { obj: RemoteChatSession? -> obj!!.asData() })
        )
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        EntityBuilder.spawnNpc(CrystalBenchPluginPaper.instance.server.worlds.first()) {
            setRot(0.0F, 0.0F)
            setYHeadRot(0.0F)
            xRot = 0.0F
            yRot = 0.0F
            setPos(0.0, 0.0, 0.0)

            val actions = EnumSet.noneOf(ClientboundPlayerInfoUpdatePacket.Action::class.java)
            actions.add(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER)
            actions.add(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME)
            actions.add(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED)

            val updatedGameProfile = GameProfile(uuid, "")

            updatedGameProfile.properties.put(
                "textures",
                Property(
                    "textures",
                    "ewogICJ0aW1lc3RhbXAiIDogMTc1NDA3NDg0NDk0MiwKICAicHJvZmlsZUlkIiA6ICI5OGQxYTQyNmRlMmU0NjBkYjdjNWExMmY5MGNhODg0OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJLdWJpbm9TSyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMDc5NDZkNjJkMzNkYTg3YmVjOWRiNDNmNDRjMjRmMzM2MTFmZTMxZTE2OTllMDFkZTA1ZTI5YWZlYjhmMzcyIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                    "xblXSSWXrnkIy2R7OHWuwT+1Ky03cIkhfesnIrArAExN7iILwrDVHnVElF+l6N1wl+PjCx24yROXp+J5f2zTFC/LduuXB88lLv4Xw6TFMOU5FFp0kIgAXeYijOMQ8FhL4K50VUM30dIM1QUnfqEOHRf3s7DhUNr58PCxi01xUbEXJFNlGx5Nwjp8Q1yt87lwDwihasunKiCTEukgkUSptEa4xeZDRL/tZdZJUEML09757ErhSjzA1MdCX+YZOMrbov4PnA45z4x5Ms1lRu6BJ+tQqz0UMK2q4jKDdUYk6gm8nsXsBJUx4rTx2r4MgdBx0gqJ8EeV98ST9N2uyKV3yIfnTRrxGhi1DVKzT+atZZADf4GwMe1eU/5gkgjxL3I38Y9IfWG7X4w06p1bgV2muMj9zHl0rnXxfMLylgb6hUVqnJImvbrMU6fsBNGqbdD1v9hXhkMynldbfBnsVKDzf4gm5DHPs6IkYxWymw2tdjI2IY/nQ+C7ejiukMq46ABmazr0OK/CDnB5VnmCUNZo49cC4QJ9uxKQlrnpIm+ss0nKoOxmVESgo/vqj234jvUgJvSdOs9WSo6CPdW46dADt/rQUmWDASWAiN7sRm6iFT8X5QKFlPAiDMLvf+B1OdqUeGwXK1tUbjvYYs9/9b5JDOQrIByQ0z9fd7TdQKrB0sw="
                )
            )

            gameProfile = updatedGameProfile

            val playerInfoPacket = ClientboundPlayerInfoUpdatePacket(actions, getEntry(this, updatedGameProfile))
            CrystalBenchPluginPaper.instance.server.onlinePlayers.forEach { player ->
                val serverPlayer = (player as CraftPlayer).handle
                serverPlayer.connection.send(playerInfoPacket)
            }

            setPos(0.0, 0.0, 0.0)

            val addEntityPacket = ClientboundAddEntityPacket(
                id, uuid, 0.0, 0.0, 0.0, 0.0F, 0.0F, type, 0, Vec3.ZERO, 0.0
            )
            CrystalBenchPluginPaper.instance.server.onlinePlayers.forEach { player ->
                val serverPlayer = (player as CraftPlayer).handle
                serverPlayer.connection.send(addEntityPacket)
            }

            val equipmentList: MutableList<com.mojang.datafixers.util.Pair<EquipmentSlot?, ItemStack?>?> =
                ArrayList<com.mojang.datafixers.util.Pair<EquipmentSlot?, ItemStack?>?>()
            equipmentList.add(
                com.mojang.datafixers.util.Pair(
                    EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(
                        org.bukkit.inventory.ItemStack(Material.STONE_SHOVEL)
                    )
                )
            )

            val setEquipmentPacket = ClientboundSetEquipmentPacket(
                this.id, equipmentList
            )

            CrystalBenchPluginPaper.instance.server.onlinePlayers.forEach { player ->
                val serverPlayer = (player as CraftPlayer).handle
                serverPlayer.connection.send(setEquipmentPacket)
            }

            val serverPlayer = (event.player as CraftPlayer).handle

            val channel = serverPlayer.connection.connection.channel;

            if (channel.pipeline().get("PacketInjector") != null) {
            }

           // channel.pipeline().addAfter("decoder", "PacketInjector", MessageToMessageDecoder<ServerboundInteractPacket>() {
           // })
            
        }
    }

    @EventHandler
    fun onPlayerInteractEntityEvent(event: PlayerInteractEntityEvent) {
        if (event.rightClicked.type == EntityType.PLAYER) {
            event.player.sendMessage("sdf ds")
        }
    }

    @EventHandler
    fun onPlayerInteractAtEntityEvent(event: PlayerInteractAtEntityEvent) {
        if (event.rightClicked.type == EntityType.PLAYER) {
            event.player.sendMessage("sdf ds")
        }
    }
}