package com.playares.essentials.support.menu;

import com.google.common.collect.Lists;
import com.playares.essentials.EssentialsService;
import com.playares.essentials.support.data.ISupport;
import com.playares.essentials.support.data.Report;
import com.playares.essentials.support.data.Request;
import com.playares.commons.item.ItemBuilder;
import com.playares.commons.menu.ClickableItem;
import com.playares.commons.menu.Menu;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public final class TicketMenu extends Menu {
    @Getter public final EssentialsService essentials;
    @Getter public final List<ISupport> tickets;
    @Getter @Setter public int page;

    public TicketMenu(EssentialsService essentials, Player player, Collection<ISupport> tickets) {
        super(essentials.getOwner(), player, "Open Tickets", 6);
        this.essentials = essentials;
        this.tickets = Lists.newArrayList(tickets);
        this.page = 0;
    }

    @Override
    public void open() {
        super.open();
        update();
    }

    private void update() {
        clear();

        int cursor = 0;
        final int start = page * 52;
        final int end = start + 52;
        final boolean hasNextPage = tickets.size() > end;
        final boolean hasPrevPage = start > 0;

        tickets.sort(Comparator.comparingLong(ISupport::getCreateTime));

        for (int i = start; i < end; i++) {
            if (cursor >= 52 || tickets.size() <= i) {
                break;
            }

            final ISupport ticket = tickets.get(i);

            if (ticket == null) {
                continue;
            }

            final List<String> lore = Lists.newArrayList();
            lore.add(ChatColor.GRAY + ticket.getDescription());
            lore.add(ChatColor.RESET + " ");

            if (ticket instanceof Request) {
                final Request request = (Request)ticket;

                lore.add(ChatColor.DARK_PURPLE + "Click to teleport to " + ChatColor.LIGHT_PURPLE + ticket.getCreatorUsername());

                final ItemStack icon = new ItemBuilder()
                        .setMaterial(Material.STAINED_CLAY)
                        .setData((short)11)
                        .setName(ChatColor.BLUE + request.getCreatorUsername())
                        .addLore(lore)
                        .build();

                addItem(new ClickableItem(icon, cursor, click -> {
                    if (click.isRightClick()) {
                        essentials.getSupportManager().getTickets().remove(request);
                        tickets.remove(request);
                        update();
                        return;
                    }

                    final Player dest = Bukkit.getPlayer(ticket.getCreatorUniqueId());

                    if (dest == null || !dest.isOnline()) {
                        player.closeInventory();
                        player.sendMessage(ChatColor.RED + "Player is no longer online");
                        return;
                    }

                    player.closeInventory();
                    player.teleport(dest);
                    player.sendMessage(EssentialsService.PRIMARY + "Summoned to " + EssentialsService.SECONDARY + dest.getName());
                }));
            }

            if (ticket instanceof Report) {
                final Report report = (Report)ticket;

                lore.add(ChatColor.YELLOW + "Created By" + ChatColor.GRAY + ": " + report.getCreatorUsername());
                lore.add(ChatColor.DARK_PURPLE + "Click to teleport to " + ChatColor.LIGHT_PURPLE + report.getReportedUsername());

                final ItemStack icon = new ItemBuilder()
                        .setMaterial(Material.STAINED_CLAY)
                        .setData((short)14)
                        .setName(ChatColor.RED + report.getReportedUsername())
                        .addLore(lore)
                        .build();

                addItem(new ClickableItem(icon, cursor, click -> {
                    if (click.isRightClick()) {
                        essentials.getSupportManager().getTickets().remove(report);
                        tickets.remove(report);
                        update();
                        return;
                    }

                    final Player dest = Bukkit.getPlayer(report.getReportedUniqueId());

                    if (dest == null || !dest.isOnline()) {
                        player.closeInventory();
                        player.sendMessage(ChatColor.RED + "Player is no longer online");
                        return;
                    }

                    player.closeInventory();

                    if (!essentials.getVanishManager().isVanished(player)) {
                        player.sendMessage(ChatColor.DARK_AQUA + "Hiding you before sending to reported player");
                        essentials.getVanishManager().getHandler().hidePlayer(player);
                    }

                    player.teleport(dest);
                    player.sendMessage(EssentialsService.PRIMARY + "Summoned to " + EssentialsService.SECONDARY + dest.getName());
                }));
            }

            cursor += 1;
        }

        if (hasNextPage) {
            final ItemStack nextPageIcon = new ItemBuilder().setMaterial(Material.EMERALD_BLOCK).setName(ChatColor.GREEN + "Next Page").build();
            addItem(new ClickableItem(nextPageIcon, 53, click -> {
                setPage(page + 1);
                update();
            }));
        }

        if (hasPrevPage) {
            final ItemStack prevPageIcon = new ItemBuilder().setMaterial(Material.REDSTONE_BLOCK).setName(ChatColor.RED + "Previous Page").build();
            addItem(new ClickableItem(prevPageIcon, 52, click -> {
                setPage(page - 1);
                update();
            }));
        }
    }
}