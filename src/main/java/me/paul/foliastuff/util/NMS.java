//package me.paul.foliastuff.util;
//
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import me.paul.foliastuff.other.FoliaStuff;
//import me.paul.foliastuff.util.entity.CustomEntityRegistry;
//import me.paul.foliastuff.util.entity.CustomPanda;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.*;
//import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
//import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
//import net.minecraft.world.entity.ambient.Bat;
//import net.minecraft.world.entity.animal.*;
//import net.minecraft.world.entity.animal.allay.Allay;
//import net.minecraft.world.entity.animal.axolotl.Axolotl;
//import net.minecraft.world.entity.animal.camel.Camel;
//import net.minecraft.world.entity.animal.frog.Frog;
//import net.minecraft.world.entity.animal.frog.Tadpole;
//import net.minecraft.world.entity.animal.goat.Goat;
//import net.minecraft.world.entity.animal.horse.*;
//import net.minecraft.world.entity.animal.sniffer.Sniffer;
//import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
//import net.minecraft.world.entity.boss.wither.WitherBoss;
//import net.minecraft.world.entity.monster.*;
//import net.minecraft.world.entity.monster.hoglin.Hoglin;
//import net.minecraft.world.entity.monster.piglin.Piglin;
//import net.minecraft.world.entity.monster.piglin.PiglinBrute;
//import net.minecraft.world.entity.monster.warden.Warden;
//import net.minecraft.world.entity.npc.Villager;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.Level;
//import org.bukkit.Location;
//import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
//
//import java.lang.invoke.MethodHandle;
//import java.lang.invoke.MethodHandles;
//import java.lang.reflect.*;
//import java.util.List;
//import java.util.Map;
//
//public class NMS {
//
//  private static CustomEntityRegistry ENTITY_REGISTRY;
//  private static MethodHandle ENTITY_REGISTRY_SETTER;
//  private static final Map<Class<?>, EntityType<?>> CUSTOM_ENTITIES = Maps
//    .newHashMap();
//  private static Method ADD_OPENS;
//  private static Method GET_MODULE;
//  private static MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
//  private static Field MODIFIERS_FIELD;
//  private static Object UNSAFE;
//  private static MethodHandle UNSAFE_FIELD_OFFSET;
//  private static MethodHandle UNSAFE_PUT_BOOLEAN;
//  private static MethodHandle UNSAFE_PUT_DOUBLE;
//  private static MethodHandle UNSAFE_PUT_FLOAT;
//  private static MethodHandle UNSAFE_PUT_INT;
//  private static MethodHandle UNSAFE_PUT_LONG;
//  private static MethodHandle UNSAFE_PUT_OBJECT;
//  private static MethodHandle UNSAFE_STATIC_FIELD_OFFSET;
//
//  private static MethodHandle ATTRIBUTE_MAP_SETTER;
//  static {
//    giveReflectiveAccess(Field.class, NMS.class);
//    MODIFIERS_FIELD = NMS.getField(Field.class, "modifiers", false);
//
//    try {
//      ENTITY_REGISTRY = new CustomEntityRegistry(BuiltInRegistries.ENTITY_TYPE);
//      ENTITY_REGISTRY_SETTER = getFinalSetter(BuiltInRegistries.class, "h");
//      ENTITY_REGISTRY_SETTER.invoke(ENTITY_REGISTRY);
//    } catch (Throwable e) {
//      e.printStackTrace();
//    }
//  }
//
//  public static void init(String rev) throws ClassNotFoundException {
//    Class<?> entity = null;
//    try {
//      entity = Class.forName("net.minecraft.server.v" + rev + ".Entity");
//    } catch (ClassNotFoundException ex) {
//      entity = Class.forName("net.minecraft.world.entity.Entity");
//    }
//
//    giveReflectiveAccess(entity, NMS.class);
//  }
//
//  public static MethodHandle getFirstGetter(Class<?> clazz, Class<?> type) {
//    try {
//      List<Field> found = getFieldsMatchingType(clazz, type, false);
//      if (found.isEmpty())
//        return null;
//      return LOOKUP.unreflectGetter(found.get(0));
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    return null;
//  }
//
//  public static void giveReflectiveAccess(Class<?> from, Class<?> to) {
//    try {
//      if (GET_MODULE == null) {
//        Class<?> module = Class.forName("java.lang.Module");
//        GET_MODULE = Class.class.getMethod("getModule");
//        ADD_OPENS = module.getMethod("addOpens", String.class, module);
//      }
//      ADD_OPENS.invoke(GET_MODULE.invoke(from), from.getPackage().getName(), GET_MODULE.invoke(to));
//    } catch (Exception e) {
//    }
//  }
//
//
//  private static List<Field> getFieldsMatchingType(Class<?> clazz, Class<?> type, boolean allowStatic) {
//    List<Field> found = Lists.newArrayList();
//    for (Field field : clazz.getDeclaredFields()) {
//      if (allowStatic ^ Modifier.isStatic(field.getModifiers()))
//        continue;
//      if (field.getType() == type) {
//        found.add(field);
//        field.setAccessible(true);
//      }
//    }
//    return found;
//  }
//
//  public static MethodHandle getFinalSetter(Class<?> clazz, String field) {
//    return getFinalSetter(clazz, field, true);
//  }
//
//  public static MethodHandle getFinalSetter(Class<?> clazz, String field, boolean log) {
//    return getFinalSetter(NMS.getField(clazz, field, log), log);
//  }
//
//  public static Field getField(Class<?> clazz, String field) {
//    return getField(clazz, field, true);
//  }
//
//  public static Field getField(Class<?> clazz, String field, boolean log) {
//    if (clazz == null)
//      return null;
//    Field f = null;
//    try {
//      f = clazz.getDeclaredField(field);
//      f.setAccessible(true);
//      return f;
//    } catch (Exception e) {
//      return null;
//    }
//  }
//
//  public static MethodHandle getMethodHandle(Class<?> clazz, String method, boolean log, Class<?>... params) {
//    if (clazz == null)
//      return null;
//    try {
//      return LOOKUP.unreflect(getMethod(clazz, method, log, params));
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    return null;
//  }
//
//  public static Method getMethod(Class<?> clazz, String method, boolean log, Class<?>... params) {
//    if (clazz == null)
//      return null;
//    Method f = null;
//    try {
//      f = clazz.getDeclaredMethod(method, params);
//      f.setAccessible(true);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    return f;
//  }
//
//  public static Map<EntityType<? extends LivingEntity>, AttributeSupplier> getSupplierMap() {
//    Field f = getField(DefaultAttributes.class, "b", false);
//    f.setAccessible(true);
//    try {
//      return (Map<EntityType<? extends LivingEntity>, AttributeSupplier>) f.get(null);
//    } catch (IllegalAccessException e) {
//      e.printStackTrace();
//      return null;
//    }
//  }
//
//  public static void registerEntityClass(Class<?> clazz, AttributeSupplier.Builder attributes, String nameKey) {
//    if (ENTITY_REGISTRY == null)
//      return;
//    Class<?> search = clazz;
//    while ((search = search.getSuperclass()) != null && Entity.class.isAssignableFrom(search)) {
//
//      net.minecraft.world.entity.EntityType<?> type = ENTITY_REGISTRY.findType(search);
//      ResourceLocation key = new ResourceLocation("minecraft", nameKey);
//      EntityType oType = EntityType.Builder.of(CustomPanda::new, MobCategory.MISC).build(nameKey);
//
//      if (type == null)
//        continue;
//
//      final Map<EntityType<? extends LivingEntity>, AttributeSupplier> suppliers = ImmutableMap.<EntityType<? extends LivingEntity>, AttributeSupplier>builder().putAll(getSupplierMap()).put(oType, attributes.build()).build();
//      try {
//        ATTRIBUTE_MAP_SETTER = getFinalSetter(DefaultAttributes.class, "b");
//        ATTRIBUTE_MAP_SETTER.invoke(suppliers);
//      } catch (Throwable e) {
//        e.printStackTrace();
//      }
//
//      CUSTOM_ENTITIES.put(clazz, oType);
//      int code = ENTITY_REGISTRY.getId(type);
//      ENTITY_REGISTRY.put(code, key, oType);
//
//      FoliaStuff.getInstance().getLogger().info("Registered entity " + clazz.getName() + " as " + key + " with id " + code);
//      return;
//    }
//    throw new IllegalArgumentException("unable to find valid entity superclass for class " + clazz.toString());
//  }
//
//  public static <T extends Entity> net.minecraft.world.entity.EntityType<T> getEntityType(Class<?> clazz) {
//    return (net.minecraft.world.entity.EntityType<T>) CUSTOM_ENTITIES.get(clazz);
//  }
//
//  public static Entity createEntity(Class<?> clazz, Location at) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
//    Level level = ((CraftWorld) at.getWorld()).getHandle();
//    net.minecraft.world.entity.EntityType<?> type = getEntityType(clazz);
//
//    Constructor<?> constructor = clazz.getConstructor(EntityType.class, Level.class);
//    net.minecraft.world.entity.Entity ent = (net.minecraft.world.entity.Entity) constructor.newInstance(type, level);
//    ent.absMoveTo(at.getX(), at.getY(), at.getZ(), at.getYaw(), at.getPitch());
//
//    level.addFreshEntity(ent);
//
//    return ent;
//  }
//
//  public static MethodHandle getFinalSetter(Field field, boolean log) {
//    if (field == null)
//      return null;
//    if (MODIFIERS_FIELD == null) {
//      if (UNSAFE == null) {
//        try {
//          UNSAFE = NMS.getField(Class.forName("sun.misc.Unsafe"), "theUnsafe").get(null);
//        } catch (Exception e) {
//          e.printStackTrace();
////          if (log) {
////            Messaging.logTr(Messages.ERROR_GETTING_FIELD, field.getName(), e.getLocalizedMessage());
////          }
//          return null;
//        }
//        UNSAFE_STATIC_FIELD_OFFSET = getMethodHandle(UNSAFE.getClass(), "staticFieldOffset", true, Field.class)
//          .bindTo(UNSAFE);
//        UNSAFE_FIELD_OFFSET = getMethodHandle(UNSAFE.getClass(), "objectFieldOffset", true, Field.class)
//          .bindTo(UNSAFE);
//        UNSAFE_PUT_OBJECT = getMethodHandle(UNSAFE.getClass(), "putObject", true, Object.class, long.class,
//          Object.class).bindTo(UNSAFE);
//        UNSAFE_PUT_INT = getMethodHandle(UNSAFE.getClass(), "putInt", true, Object.class, long.class, int.class)
//          .bindTo(UNSAFE);
//        UNSAFE_PUT_FLOAT = getMethodHandle(UNSAFE.getClass(), "putFloat", true, Object.class, long.class,
//          float.class).bindTo(UNSAFE);
//        UNSAFE_PUT_DOUBLE = getMethodHandle(UNSAFE.getClass(), "putDouble", true, Object.class, long.class,
//          double.class).bindTo(UNSAFE);
//        UNSAFE_PUT_BOOLEAN = getMethodHandle(UNSAFE.getClass(), "putBoolean", true, Object.class, long.class,
//          boolean.class).bindTo(UNSAFE);
//        UNSAFE_PUT_LONG = getMethodHandle(UNSAFE.getClass(), "putLong", true, Object.class, long.class,
//          long.class).bindTo(UNSAFE);
//      }
//      try {
//        boolean isStatic = Modifier.isStatic(field.getModifiers());
//        long offset = (long) (isStatic ? UNSAFE_STATIC_FIELD_OFFSET.invoke(field)
//          : UNSAFE_FIELD_OFFSET.invoke(field));
//        MethodHandle mh = field.getType() == int.class ? UNSAFE_PUT_INT
//          : field.getType() == boolean.class ? UNSAFE_PUT_BOOLEAN
//          : field.getType() == double.class ? UNSAFE_PUT_DOUBLE
//          : field.getType() == float.class ? UNSAFE_PUT_FLOAT
//          : field.getType() == long.class ? UNSAFE_PUT_LONG : UNSAFE_PUT_OBJECT;
//        return isStatic ? MethodHandles.insertArguments(mh, 0, field.getDeclaringClass(), offset)
//          : MethodHandles.insertArguments(mh, 1, offset);
//      } catch (Throwable t) {
//        t.printStackTrace();
//        return null;
//      }
//    }
//    try {
//      MODIFIERS_FIELD.setInt(field, field.getModifiers() & ~Modifier.FINAL);
//    } catch (Exception e) {
//      e.printStackTrace();
//      return null;
//    }
//    try {
//      return LOOKUP.unreflectSetter(field);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    return null;
//  }
//}
