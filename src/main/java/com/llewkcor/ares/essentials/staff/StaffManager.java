package com.llewkcor.ares.essentials.staff;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.llewkcor.ares.essentials.Essentials;
import com.llewkcor.ares.essentials.staff.data.StaffAccount;
import com.llewkcor.ares.essentials.staff.listener.StaffListener;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class StaffManager {
    @Getter public final Essentials plugin;
    @Getter public final StaffHandler handler;
    @Getter public final Set<StaffAccount> staffRepository;

    public StaffManager(Essentials plugin) {
        this.plugin = plugin;
        this.handler = new StaffHandler(this);
        this.staffRepository = Sets.newConcurrentHashSet();

        Bukkit.getPluginManager().registerEvents(new StaffListener(this), plugin);
    }

    public StaffAccount getAccountByID(UUID uniqueId) {
        return staffRepository.stream().filter(staff -> staff.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    public ImmutableSet<StaffAccount> getAccountByPermission(StaffAccount.StaffSetting setting, boolean value) {
        return ImmutableSet.copyOf(staffRepository.stream().filter(staff -> staff.getSettings().getSettings().getOrDefault(setting, setting.defaultSetting) == value).collect(Collectors.toSet()));
    }
}