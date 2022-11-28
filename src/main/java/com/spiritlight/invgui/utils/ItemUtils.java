package com.spiritlight.invgui.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemUtils {
    public static String getItemName(ItemStack item) {
        if(item.getTagCompound() != null) {
            return item.getTagCompound().getCompoundTag("display").getString("Name");
        } else {
            return item.getDisplayName();
        }
    }

    public static boolean itemEquals(Item i1, Item i2) {
        return (i1 == null) == (i2 == null);
    }
}
