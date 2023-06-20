package me.paul.foliastuff.util;

import com.destroystokyo.paper.Namespaced;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.*;
import org.bukkit.enchantments.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ItemBuilder {

	private final ItemStack is;
	/**
	 * Create a new ItemBuilder from scratch.
	 * @param m The material to create the ItemBuilder with.
	 */
	public ItemBuilder(Material m){
		this(m, 1);
	}
	/**
	 * Create a new ItemBuilder over an existing itemstack.
	 * @param is The itemstack to create the ItemBuilder over.
	 */
	public ItemBuilder(ItemStack is){
		this.is=is;
	}
	/**
	 * Create a new ItemBuilder from scratch.
	 * @param m The material of the item.
	 * @param amount The amount of the item.
	 */
	public ItemBuilder(Material m, int amount){
		is= new ItemStack(m, amount);
	}
	/**
	 * Create a new ItemBuilder from scratch.
	 * @param m The material of the item.
	 * @param amount The amount of the item.
	 * @param durability The durability of the item.
	 */
	public ItemBuilder(Material m, int amount, byte durability){
		is = new ItemStack(m, amount, durability);
	}
	/**
	 * Clone the ItemBuilder into a new one.
	 * @return The cloned instance.
	 */
	public ItemBuilder clone(){
		return new ItemBuilder(is);
	}
	public static ItemBuilder of(Material m) {
		return new ItemBuilder(m);
	}
	public static ItemBuilder of(ItemStack is) {
		return new ItemBuilder(is);
	}
	public static ItemBuilder of(Material m, int amount) {
		return new ItemBuilder(m, amount);
	}
	public static ItemBuilder of(Material m, int amount, byte durability) {
		return new ItemBuilder(m, amount, durability);
	}
	/**
	 * Change the durability of the item.
	 * @param dur The durability to set it to.
	 */
	public ItemBuilder setDurability(short dur){
		is.setDurability(dur);
		return this;
	}
	/**
	 * Set the displayname of the item.
	 * @param name The name to change it to.
	 */
	public ItemBuilder setName(String name){
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		is.setItemMeta(im);
		return this;
	}

  /**
   * Set the displayname of the item.
   * @param component The name to change it to.
   * @return The ItemBuilder instance.
   */
  public ItemBuilder name(Component component) {
    ItemMeta im = is.getItemMeta();
    im.displayName(component);
    is.setItemMeta(im);
    return this;
  }

	/**
	 * Set the material of the item.
	 * @param material The material to change it to.
	 */
	public ItemBuilder setType(Material material){
		is.setType(material);
		return this;
	}

	/**
	 * Set the amount of the item.
	 * @param amount Amount to make item
	 */
	public ItemBuilder setAmount(int amount) {
		is.setAmount(amount);
		return this;
	}
	public ItemBuilder addItemFlag(ItemFlag flag) {
		ItemMeta im = is.getItemMeta();
		im.addItemFlags(flag);
		is.setItemMeta(im);

		return this;
	}
	public ItemBuilder hideEnchant() {
		ItemMeta im = is.getItemMeta();
		im.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		is.setItemMeta(im);
		return this;
	}
	public ItemBuilder hideAll() {
		ItemMeta im = is.getItemMeta();
		im.addItemFlags(ItemFlag.values());
		is.setItemMeta(im);

		return this;
	}
	public ItemBuilder fakeEnchant() {
		hideEnchant();
		addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
		return this;
	}
	/**
	 * Add an unsafe enchantment.
	 * @param ench The enchantment to add.
	 * @param level The level to put the enchant on.
	 */
	public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level){
		is.addUnsafeEnchantment(ench, level);
		return this;
	}
	/**
	 * Remove a certain enchant from the item.
	 * @param ench The enchantment to remove
	 */
	public ItemBuilder removeEnchantment(Enchantment ench){
		is.removeEnchantment(ench);
		return this;
	}
	/**
	 * Set the skull owner for the item. Works on skulls only.
	 * @param owner The name of the skull's owner.
	 */
	public ItemBuilder setSkullOwner(String owner){
		try{
			SkullMeta im = (SkullMeta)is.getItemMeta();
			im.setOwner(owner);
			is.setItemMeta(im);
		}catch(ClassCastException expected){}
		return this;
	}
	/**
	 * Add an enchant to the item.
	 * @param ench The enchant to add
	 * @param level The level
	 */
	public ItemBuilder addEnchant(Enchantment ench, int level){
		ItemMeta im = is.getItemMeta();
		im.addEnchant(ench, level, true);
		is.setItemMeta(im);
		return this;
	}
	/**
	 * Add an enchant to the item.
	 * @param ench The enchant to add at level 1
	 */
	public ItemBuilder addEnchant(Enchantment ench){
		ItemMeta im = is.getItemMeta();
		im.addEnchant(ench, 1, true);
		is.setItemMeta(im);
		return this;
	}
	/**
	 * Add multiple enchants at once.
	 * @param enchantments The enchants to add.
	 */
	public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments){
		is.addEnchantments(enchantments);
		return this;
	}
	/**
	 * Sets infinity durability on the item by setting the durability to Short.MAX_VALUE.
	 */
	public ItemBuilder setInfinityDurability(){
		is.setDurability(Short.MAX_VALUE);
		return this;
	}
	/**
	 * Re-sets the lore.
	 * @param lore The lore to set it to.
	 */
	public ItemBuilder setLore(String... lore){
		ItemMeta im = is.getItemMeta();
		im.setLore(Arrays.asList(lore));
		is.setItemMeta(im);
		return this;
	}
	/**
	 * Re-sets the lore.
	 * @param lore The lore to set it to.
	 */
	public ItemBuilder setLore(List<String> lore) {
		ItemMeta im = is.getItemMeta();
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}
	/**
	 * Remove a lore line.
	 * @param line The lore to remove.
	 */
	public ItemBuilder removeLoreLine(String line){
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		if(!lore.contains(line))return this;
		lore.remove(line);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}
	/**
	 * Remove a lore line.
	 * @param index The index of the lore line to remove.
	 */
	public ItemBuilder removeLoreLine(int index){
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		if(index<0||index>lore.size())return this;
		lore.remove(index);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}
	/**
	 * Add a lore line.
	 * @param line The lore line to add.
	 */
	public ItemBuilder addLoreLine(String line){
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>();
		if(im.hasLore())lore = new ArrayList<>(im.getLore());
		lore.add(line);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}
	/**
	 * Add a lore line.
	 * @param line The lore line to add.
	 * @param pos The index of where to put it.
	 */
	public ItemBuilder addLoreLine(String line, int pos){
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		lore.set(pos, line);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}
	/**
	 * Sets the armor color of a leather armor piece. Works only on leather armor pieces.
	 * @param color The color to set it to.
	 */
	public ItemBuilder setLeatherArmorColor(Color color){
		try{
			LeatherArmorMeta im = (LeatherArmorMeta)is.getItemMeta();
			im.setColor(color);
			is.setItemMeta(im);
		}catch(ClassCastException expected){}
		return this;
	}

	/**
	 * Sets the potion color of a potion. Works only on potions.
	 * @param color The color to set it to.
	 */
	public ItemBuilder setPotionColor(Color color) {
		try{
			PotionMeta im = (PotionMeta)is.getItemMeta();
			im.setColor(color);
			is.setItemMeta(im);
		}catch(ClassCastException expected){}
		return this;
	}

	/**
	 * Sets the potion meta of a potion. Works only on potions and splash potions.
	 * @param type Type of potion to make it
	 * @param extended If the potion should be extended (note some potions cannot be extended and upgraded)
	 * @param upgraded If the potion should be upgraded (note some potions cannot be extended and upgraded)
	 */
	public ItemBuilder setPotionMeta(PotionType type, boolean extended, boolean upgraded) {
		try {
			PotionMeta meta = (PotionMeta) is.getItemMeta();
			meta.setBasePotionData(new PotionData(type, extended, upgraded));
			is.setItemMeta(meta);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}

		return this;
	}

	public ItemBuilder unbreaking() {
		ItemMeta meta = is.getItemMeta();
		meta.setUnbreakable(true);
		is.setItemMeta(meta);

		return this;
	}

	/**
	 * Add a stored enchantment to itemmeta. This is important to create enchant books.
	 * @param ench Enchantment to store
	 * @param level Level of the enchantment
	 * @param ignoreLevelRestriction allow unsafe enchantment books
	 */
	public ItemBuilder addStoredEnchant(Enchantment ench, int level, boolean ignoreLevelRestriction){
		try {
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) is.getItemMeta();
			meta.addStoredEnchant(ench, level, ignoreLevelRestriction);
			is.setItemMeta(meta);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Add a stored enchantment to itemmeta. This is important to create enchant books.
	 * @param ench Enchantment to store
	 * @param level Level of the enchantment
	 */
	public ItemBuilder addStoredEnchant(Enchantment ench, int level){
		return addStoredEnchant(ench, level, false);
	}

	/**
	 * Add a stored enchantment to itemmeta. This is important to create enchant books.
	 * @param ench Enchantment to store
	 */
	public ItemBuilder addStoredEnchant(Enchantment ench){
		return addStoredEnchant(ench, 1, false);
	}

	/**
	 * Remove a stored enchantment to itemmeta. This is important to create enchant books.
	 * @param ench Enchantment to store
	 */
	public ItemBuilder removeStoredEnchant(Enchantment ench){
		try {
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) is.getItemMeta();
			meta.removeStoredEnchant(ench);
			is.setItemMeta(meta);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Set title of the books
	 * @param title
	 */
	public ItemBuilder setTitle(@Nullable String title){
		try {
			BookMeta meta = (BookMeta) is.getItemMeta();
			meta.setTitle(title);
			is.setItemMeta(meta);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Set author of the book
	 * @param author
	 */
	public ItemBuilder setAuthor(@Nullable String author){
		try {
			BookMeta meta = (BookMeta) is.getItemMeta();
			meta.setAuthor(author);
			is.setItemMeta(meta);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Set specific page number data
	 * @param page Page number of which setting data
	 * @param content The pages content
	 */
	public ItemBuilder setPage(int page, String content){
		try {
			BookMeta meta = (BookMeta) is.getItemMeta();
			meta.setPage(page, content);
			is.setItemMeta(meta);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Set page list
	 * @param pages list of pages
	 */
	public ItemBuilder setPages(List<String> pages){
		try {
			BookMeta meta = (BookMeta) is.getItemMeta();
			meta.setPages(pages);
			is.setItemMeta(meta);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * Set page list
	 * @param pages list of pages or singleton
	 */
	public ItemBuilder setPages(String... pages){
		return setPages(Arrays.asList(pages));
	}

	/**
	 * Add pages to the book
	 * @param page list of pages or singleton
	 */
	public ItemBuilder addPage(String... page){
		try {
			BookMeta meta = (BookMeta) is.getItemMeta();
			meta.addPage(page);
			is.setItemMeta(meta);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 *
	 * @param attribute Attribute wished to be modified
	 * @param amount Value set for the attribute
	 * @param operation	How to implement attribute value
	 */
	public ItemBuilder addAttributeModifier(@NotNull Attribute attribute, double amount, @NotNull AttributeModifier.Operation operation){
		AttributeModifier attributeModifier = new AttributeModifier(UUID.randomUUID(), attribute.getKey().getKey(), amount, operation);
		ItemMeta meta = is.getItemMeta();

		assert meta != null;
		meta.addAttributeModifier(attribute, attributeModifier);

		is.setItemMeta(meta);
		return this;
	}


	/**
	 *
	 * @param materials	Materials that this item can be placed on.
	 */
	public ItemBuilder setPlaceable(Material... materials){
		Collection<Namespaced> keys = new ArrayList<>();
		for (Material material : materials) {
			keys.add(material.getKey());
		}
		return setPlaceable(keys);
	}


	/**
	 *
	 * @param materials	Materials that this item can be placed on.
	 */
	public ItemBuilder setPlaceable(List<Material> materials){
		Collection<Namespaced> keys = new ArrayList<>();
		for (Material material : materials) {
			keys.add(material.getKey());
		}
		return setPlaceable(keys);
	}


	/**
	 *
	 * @param materials Collection Materials that this item can be placed on.
	 */
	public ItemBuilder setPlaceable(Collection<Namespaced> materials){
		ItemMeta meta = is.getItemMeta();
		meta.setPlaceableKeys(materials);
		is.setItemMeta(meta);
		return this;
	}


	/**
	 *
	 * @param materials	Materials that this item can destroy
	 */
	public ItemBuilder setDestroyable(Material... materials){
		Collection<Namespaced> keys = new ArrayList<>();
		for (Material material : materials) {
			keys.add(material.getKey());
		}
		return setDestroyable(keys);
	}


	/**
	 *
	 * @param materials	Materials that this item can destroy
	 */
	public ItemBuilder setDestroyable(List<Material> materials){
		Collection<Namespaced> keys = new ArrayList<>();
		for (Material material : materials) {
			keys.add(material.getKey());
		}
		return setDestroyable(keys);
	}


	/**
	 *
	 * @param materials Collection Materials that this item can destroy
	 */
	public ItemBuilder setDestroyable(Collection<Namespaced> materials){
		ItemMeta meta = is.getItemMeta();
		meta.setDestroyableKeys(materials);
		is.setItemMeta(meta);
		return this;
	}

	/**
	 *
	 * @param itemStacks Items stored in a crossbow
	 */
	public ItemBuilder setChargedProjectiles(ItemStack... itemStacks) {
		CrossbowMeta crossbowMeta = (CrossbowMeta) is.getItemMeta();
		crossbowMeta.setChargedProjectiles(Arrays.asList(itemStacks));
		is.setItemMeta(crossbowMeta);
		return this;
	}

	public ItemBuilder setChargedProjectiles() {
		return setChargedProjectiles(new ItemStack(Material.ARROW));
	}

	/**
	 * Retrieves the itemstack from the ItemBuilder.
	 * @return The itemstack created/modified by the ItemBuilder instance.
	 */
	public ItemStack build(){
		return is;
	}
}
