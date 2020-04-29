package com.playares.essentials.staff.menu;

import com.playares.essentials.EssentialsService;
import com.playares.essentials.staff.data.StaffAccount;
import com.playares.commons.item.ItemBuilder;
import com.playares.commons.menu.ClickableItem;
import com.playares.commons.menu.Menu;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public final class StaffSettingMenu extends Menu {
    @Getter public final EssentialsService essentials;
    @Getter public final StaffAccount account;

    public StaffSettingMenu(EssentialsService essentials, Player player, StaffAccount account) {
        super(essentials.getOwner(), player, "Staff Settings", 2);
        this.essentials = essentials;
        this.account = account;
    }

    @Override
    public void open() {
        super.open();
        update();
    }

    private void update() {
        clear();

        int pos = 0;

        for (StaffAccount.StaffSetting setting : StaffAccount.StaffSetting.values()) {
            final boolean enabled = account.isEnabled(setting);

            final ItemStack icon = new ItemBuilder()
                    .setMaterial(Material.SIGN)
                    .setName(ChatColor.GOLD + setting.getDisplayName())
                    .addLore(ChatColor.GRAY + setting.getDescription())
                    .addLore(ChatColor.RESET + " ")
                    .addLore((enabled ? ChatColor.GREEN + "This setting is enabled" : ChatColor.RED + "This setting is disabled"))
                    .build();

            addItem(new ClickableItem(icon, pos, click -> {
                if (enabled) {
                    account.getSettings().getSettings().put(setting, false);
                    update();
                    return;
                }

                account.getSettings().getSettings().put(setting, true);
                update();
            }));

            pos += 1;
        }
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        super.onInventoryClose(event);
        essentials.getStaffManager().setAccount(false, account);
    }
}