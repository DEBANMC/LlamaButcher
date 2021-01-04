package net.lldv.llamabutcher.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.ConfigSection;
import net.lldv.llamabutcher.LlamaButcher;
import net.lldv.llamabutcher.components.language.Language;

/**
 * @author LlamaDevelopment
 * @project LlamaButcher
 * @website http://llamadevelopment.net/
 */
public class ClearLagCommand extends PluginCommand<LlamaButcher> {

    public ClearLagCommand(final LlamaButcher plugin, final ConfigSection section) {
        super(section.getString("Name"), plugin);
        setDescription(section.getString("Description"));
        setPermission(section.getString("Permission"));
        setUsage(section.getString("Usage"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender.hasPermission(getPermission())) {
            final int killed = this.getPlugin().clearAll();
            final String entString = killed == 1 ? Language.getNP("entity") : Language.getNP("entities");
            sender.sendMessage(Language.get("clearlag.command", killed, entString));
        }
        return false;
    }
}
