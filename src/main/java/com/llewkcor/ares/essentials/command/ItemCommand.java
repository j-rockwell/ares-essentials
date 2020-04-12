package com.llewkcor.ares.essentials.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.llewkcor.ares.commons.item.ItemBuilder;
import com.llewkcor.ares.commons.logger.Logger;
import com.llewkcor.ares.commons.remap.RemappedEnchantment;
import com.llewkcor.ares.essentials.Essentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@AllArgsConstructor
public final class ItemCommand extends BaseCommand {
    @Getter public Essentials plugin;

    @CommandAlias("enchant")
    @Syntax("<hand/armor> <enchantment> <level>")
    @Description("Enchant an item")
    @CommandPermission("essentials.enchant")
    public void onEnchant(Player player, @Values("hand|armor") String type, String enchantmentName, int enchantmentLevel) {
        if (type == null || !(type.equalsIgnoreCase("hand") || type.equalsIgnoreCase("armor"))) {
            player.sendMessage(ChatColor.RED + "Invalid enchantment type");
            return;
        }

        final Enchantment enchantment = RemappedEnchantment.getEnchantmentByName(enchantmentName);

        if (enchantmentLevel <= 0) {
            enchantmentLevel = 1;
        }

        if (enchantment == null) {
            player.sendMessage(ChatColor.RED + "Enchantment not found");
            return;
        }

        if (type.equalsIgnoreCase("hand")) {
            final ItemStack hand = player.getItemInHand();

            if (hand == null || hand.getType().equals(Material.AIR)) {
                player.sendMessage(ChatColor.RED + "You are not holding an item");
                return;
            }

            hand.addUnsafeEnchantment(enchantment, enchantmentLevel);
            player.sendMessage(Essentials.PRIMARY + "Enchanted item in-hand with " + Essentials.SECONDARY + enchantment.getName().toLowerCase().replace("_", " ") + " " + enchantmentLevel);
            Logger.print(player.getName() + " enchanted item in-hand with " + enchantment.getName() + " " + enchantmentLevel);

            return;
        }

        int enchanted = 0;

        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor == null || armor.getType().equals(Material.AIR)) {
                continue;
            }

            armor.addUnsafeEnchantment(enchantment, enchantmentLevel);
            enchanted += 1;
        }

        player.sendMessage(Essentials.PRIMARY + "Enchanted " + Essentials.SECONDARY + enchanted + " " + Essentials.PRIMARY + " pieces of armor with " + Essentials.SECONDARY + enchantment.getName().toLowerCase().replace("_", " ") + " " + enchantmentLevel);
        Logger.print(player.getName() + " enchanted their armor with " + enchantment.getName() + " " + enchantmentLevel);
    }

    @CommandAlias("rename")
    @Syntax("[name]")
    @Description("Rename the item in your hand")
    @CommandPermission("essentials.rename")
    public void onRename(Player player, String name) {
        final String styled = ChatColor.translateAlternateColorCodes('&', name);
        final ItemStack hand = player.getItemInHand();

        if (hand == null || hand.getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "You are not holding anything");
            return;
        }

        final ItemMeta meta = hand.getItemMeta();

        meta.setDisplayName(styled);
        hand.setItemMeta(meta);

        player.updateInventory();
        player.sendMessage(Essentials.PRIMARY + "Renamed item in-hand to " + styled);
    }

    @CommandAlias("repair")
    @Syntax("<hand/armor>")
    @Description("Repair an item")
    @CommandPermission("essentials.repair")
    public void onRepair(Player player, @Values("hand|armor") String type) {
        if (type == null || !(type.equalsIgnoreCase("hand") || type.equalsIgnoreCase("armor"))) {
            player.sendMessage(ChatColor.RED + "Invalid repair type");
            return;
        }

        if (type.equalsIgnoreCase("hand")) {
            final ItemStack hand = player.getItemInHand();

            if (hand == null || hand.getType().equals(Material.AIR)) {
                player.sendMessage(ChatColor.RED + "You are not holding anything");
                return;
            }

            hand.setDurability((short)0);
            player.sendMessage(Essentials.PRIMARY + "Repaired hand");
            return;
        }

        int repaired = 0;

        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor == null || armor.getType().equals(Material.AIR)) {
                continue;
            }

            armor.setDurability((short)0);
            repaired += 1;
        }

        player.sendMessage(Essentials.PRIMARY + "Repaired " + Essentials.SECONDARY + repaired + " " + Essentials.PRIMARY + "pieces of armor");
    }

    @CommandAlias("item|i|give")
    @CommandPermission("essentials.item")
    @Description("Give yourself an item")
    @Syntax("<item name> [amount]")
    public void onItem(Player player, String itemName, @Optional String amountName) {
        int amount = 1;
        final Material material = Material.getMaterial(itemName.toUpperCase());
        short data = 0;

        try {
            amount = Integer.parseInt(amountName);
        } catch (NumberFormatException ex) {
            player.sendMessage(ChatColor.RED + "Invalid amount");
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "Your inventory is full");
            return;
        }

        if (material == null) {
            player.sendMessage(ChatColor.RED + "Item not found");
            return;
        }

        if (itemName.contains(":")) {
            final String dataSplit = itemName.split(":")[1];

            if (dataSplit != null) {
                try {
                    data = Short.parseShort(dataSplit);
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "Bad data value");
                    return;
                }
            }
        }

        final ItemBuilder builder = new ItemBuilder().setMaterial(material).setData(data);

        if (amount > 0) {
            builder.setAmount(amount);
        }

        final ItemStack item = builder.build();

        player.getInventory().addItem(item);
        player.sendMessage(Essentials.PRIMARY + "Added " + Essentials.SECONDARY + "x" + item.getAmount() + " " + StringUtils.capitalize(item.getType().name().toLowerCase().replace("_", " ")) + Essentials.PRIMARY + " to your inventory");
        Logger.print(player.getName() + "(" + player.getUniqueId().toString() + ") gave themselves " + item.getAmount() + " " + item.getType().name());
    }

    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
        sender.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.GOLD + "/" + help.getCommandName() + " help " + (help.getPage() + 1) + ChatColor.YELLOW + " to see the next page");
    }
}