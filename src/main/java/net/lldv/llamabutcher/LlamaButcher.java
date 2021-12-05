package net.lldv.llamabutcher;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.entity.item.EntityEndCrystal;
import cn.nukkit.entity.item.EntityPainting;
import cn.nukkit.entity.item.EntityVehicle;
import cn.nukkit.entity.mob.EntityWither;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.Setter;
import net.lldv.llamabutcher.commands.ClearLagCommand;
import net.lldv.llamabutcher.components.language.Language;
import net.lldv.llamabutcher.components.tasks.ClearLagTask;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LlamaDevelopment
 * @project LlamaButcher
 * @website http://llamadevelopment.net/
 */
public class LlamaButcher extends PluginBase {

    // slapper = petteri's plugin, npc = c1oky's plugin
    private boolean slapper = false, pets = false, nametag = false, holograms = false, npc = false;
    @Getter
    private Announcement announcement;

    @Getter
    private final Set<Integer> announcements = new HashSet<>();

    @Getter
    @Setter
    private int seconds, schedule;


    @Override
    public void onEnable() {
        Language.init(this);

        this.saveDefaultConfig();
        final Config c = this.getConfig();

        this.nametag = c.getBoolean("KillEntitiesWithNametags");
        this.schedule = c.getInt("Schedule");
        this.seconds = this.schedule;

        final String announcements = c.getString("Announcement").toLowerCase();

        this.announcements.addAll(c.getIntegerList("Announcements"));

        if (announcements.equalsIgnoreCase("chat")) announcement = Announcement.CHAT;
        else if (announcements.equalsIgnoreCase("title")) announcement = Announcement.TITLE;
        else if (announcements.equalsIgnoreCase("actionbar")) announcement = Announcement.ACTIONBAR;

        this.getServer().getPluginManager().getPlugins().forEach((s, p) -> {
            if (p.getName().equalsIgnoreCase("NPC")) this.slapper = true;
            else if (p.getName().equalsIgnoreCase("LlamaPets")) this.pets = true;
            else if (p.getName().equalsIgnoreCase("Holograms")) this.holograms = true;
            else if (p.getName().equalsIgnoreCase("PlaceholderAPI")) this.placeholder();
        });

        if (this.slapper) {
            final Plugin plugin = this.getServer().getPluginManager().getPlugin("NPC");
            if (plugin.getDescription().getAuthors().contains("C1oky")) {
                this.slapper = false;
                this.npc = true;
            }
        }

        this.getServer().getCommandMap().register("clearlag", new ClearLagCommand(this, c.getSection("Commands.Clearlag")));
        this.getServer().getScheduler().scheduleDelayedRepeatingTask(this, new ClearLagTask(this), 100, 20);
    }

    public void placeholder() {
        
    }

    public int clearAll() {
        final AtomicInteger killed = new AtomicInteger();

        this.getServer().getLevels().forEach((i, level) -> {
            for (Entity entity : level.getEntities()) {
                boolean kill = true;
                if (!this.nametag && entity.hasCustomName()) kill = false;
                else if (entity instanceof EntityHuman) kill = false;
                else if (entity instanceof EntityPainting) kill = false;
                else if (entity instanceof EntityEndCrystal) kill = false;
                else if (entity instanceof EntityBoat) kill = false;
                else if (entity instanceof EntityWither) kill = false;
                else if (entity instanceof EntityVehicle) kill = false; // minecarts
                else if (entity.getNetworkId() == 61) kill = false; // armor stand

                if (kill) {
                    entity.despawnFromAll();
                    entity.close();
                    killed.getAndIncrement();
                }
            }
            level.doChunkGarbageCollection();
        });

        System.gc();
        return killed.get();
    }

    public void broadcast(final int seconds) {

        final String secString = seconds == 1 ? Language.getNP("second") : Language.getNP("seconds");

        switch (announcement) {
            case CHAT:
                this.getServer().broadcastMessage(Language.get("clearlag.chat", seconds, secString));
                break;
            case TITLE:
                this.getServer().getOnlinePlayers().values().forEach((e) -> {
                    e.sendTitle(Language.getNP("clearlag.title"), Language.getNP("clearlag.subtitle", seconds, secString));
                });
                break;
            case ACTIONBAR:
                this.getServer().getOnlinePlayers().values().forEach((e) -> {
                    e.sendActionBar(Language.getNP("clearlag.actionbar", seconds, secString));
                });
                break;
        }
    }


    public enum Announcement {
        CHAT, TITLE, ACTIONBAR
    }

}
