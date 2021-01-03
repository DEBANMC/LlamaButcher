package net.lldv.llamabutcher.components.tasks;

import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.scheduler.Task;
import lombok.RequiredArgsConstructor;
import net.lldv.llamabutcher.LlamaButcher;
import net.lldv.llamabutcher.components.language.Language;

/**
 * @author LlamaDevelopment
 * @project LlamaButcher
 * @website http://llamadevelopment.net/
 */
@RequiredArgsConstructor
public class ClearLagTask extends Task {

    private final LlamaButcher plugin;

    @Override
    public void onRun(int i) {

        if (this.plugin.getAnnouncements().contains(this.plugin.getSeconds())) this.plugin.broadcast(this.plugin.getSeconds());

        if (this.plugin.getSeconds() == 0) {
            this.plugin.setSeconds(this.plugin.getSchedule());
            final int killed = this.plugin.clearAll();

            final String entString = killed == 1 ? Language.getNP("entity") : Language.getNP("entities");

            switch (this.plugin.getAnnouncement()) {
                case CHAT:
                    this.plugin.getServer().broadcastMessage(Language.get("clearlag.chat.end", killed, entString));
                    break;
                case TITLE:
                    this.plugin.getServer().getOnlinePlayers().values().forEach((e) -> {
                        e.sendTitle(Language.getNP("clearlag.title"), Language.getNP("clearlag.subtitle.end", killed, entString));
                    });
                    break;
                case ACTIONBAR:
                    this.plugin.getServer().getOnlinePlayers().values().forEach((e) -> {
                        e.sendActionBar(Language.getNP("clearlag.actionbar.end", killed, entString));
                    });
                    break;
            }

            return;
        }

        this.plugin.setSeconds(this.plugin.getSeconds() - 1);
    }

}
