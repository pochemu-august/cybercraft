package com.cybercraft.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.cybercraft.item.component.CyberdeckSpec;
import com.cybercraft.registry.ModComponents;
import com.cybercraft.registry.ModItems;

/**
 * Нейровизор (Кибердека) — носимый головной интерфейс нетраннера.
 * Экипируется исключительно в слот шлема (EquipmentSlot.HEAD).
 * Надевание происходит автоматически через компонент Equippable (ПКМ в руке или через меню инвентаря).
 * Механики подзорной трубы из рук полностью удалены: всё сканирование и взаимодействие
 * происходит только при ношении на голове и удержании клавиши сканера.
 */
public class CyberdeckItem extends Item {

	public CyberdeckItem(Properties properties) {
		super(properties);
	}

	/**
	 * Проверяет, надет ли нейровизор на голове сущности.
	 */
	public static boolean isEquipped(LivingEntity entity) {
		return entity.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.CYBERDECK);
	}

	/**
	 * Получить стек надетого на голову визора или EMPTY, если визор не надет.
	 */
	public static ItemStack getEquippedCyberdeck(LivingEntity entity) {
		ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
		return head.is(ModItems.CYBERDECK) ? head : ItemStack.EMPTY;
	}

	/**
	 * Получить аппаратные характеристики данной деки из Data Component.
	 */
	public static CyberdeckSpec getSpec(ItemStack stack) {
		CyberdeckSpec spec = stack.get(ModComponents.CYBERDECK_SPEC);
		return spec != null ? spec : CyberdeckSpec.MK1;
	}
}
