package me.paul.lads.streamlabs;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.twitch4j.streamlabs4j.api.StreamlabsApi;
import com.github.twitch4j.streamlabs4j.api.StreamlabsApiBuilder;
import com.github.twitch4j.streamlabs4j.api.domain.StreamlabsDonationsData.StreamlabsDonation;
import com.github.twitch4j.streamlabs4j.api.domain.StreamlabsUser;

import lombok.Getter;
import me.paul.lads.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

@Getter
public class LabUtil {

	private LabUtil() {
//		new BukkitRunnable() {
//
//			@Override
//			public void run() {
//				Map<String, List<StreamlabsDonation>> donos = getAllDonations();
//
//				synchronized (this) {
//					donos.forEach((token, list) -> {
//						LabUser user = getUser(token);
//						
//						if(user != null) {
//							for(StreamlabsDonation d : list)
//								user.setMoneyReceived(user.getMoneyReceived() + d.getAmount());
//							
//							boolean spinGiven = false;
//							while(user.getMoneyReceived() >= user.getWheelGoal()) {
//								spinGiven = true;
//								user.setMoneyReceived(user.getMoneyReceived() - user.getWheelGoal());
//								user.setSpins(user.getSpins() + 1);
//							}
//							
//							if(user.getMoneyReceived() < 0D)
//								user.setMoneyReceived(0D);
//							
//							if(spinGiven) {
//								Player player = Bukkit.getPlayer(user.getMcUuid());
//								if(player != null) {
//									player.sendMessage(ChatColor.BOLD + "" + ChatColor.YELLOW + " >> " + ChatColor.RESET + ChatColor.GREEN + "You unlocked a spin for the wheel!");
//								}
//							}
//						}
//					});
//				}
//
//				System.out.println("Donations Updated");
//			}
//		}.runTaskTimerAsynchronously(Main.getInstance(), 0L, 20L * 30L);

//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				synchronized (this) {
//					for (Player player : Bukkit.getOnlinePlayers()) {
//						LabUser user = LabUtil.getInstance().getUser(player.getUniqueId());
//						if(user == null)
//							continue;
//						
//						String message = ChatColor.GOLD + "So far, " + ChatColor.DARK_GREEN + "$"
//								+ new DecimalFormat("##.##").format(user.getMoneyReceived()) + ChatColor.GOLD + " has been raised!";
//						player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
//					}
//				}
//			}
//		}.runTaskTimer(Main.getInstance(), 0L, 20L * 5L);

//		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
//			Map<String, StreamlabsUser> map = new HashMap<>();
//			for (String token : ACCESS_TOKENS) {
//				StreamlabsUser user = API.getUser(token).execute();
//				map.put(token, user);
//				System.out.println("Cached new Streamer: " + user.getTwitch().get().getDisplayName());
//			}
//
//			synchronized (this) {
//				cachedUsers.putAll(map);
//			}
//		});
	}

	public static LabUtil getInstance() {
		if (instance == null)
			instance = new LabUtil();

		return instance;
	}

	private static LabUtil instance;

	public static final List<String> ACCESS_TOKENS;

	public static final String CLIENT_ID = "8K8HmWp6dhJDcSFRvLaHErSwcQlEvwEuhmsml9Y8";
	public static final String CLIENT_SECRET = "8X1WzzPf4ulQ9nE9CwxV45fymFoc54MqnlPVB6a5";

	@Getter
	private static final StreamlabsApi API;

	/**
	 * Cache containing all of the donations currently being stored on the server
	 **/
	private Set<StreamlabsDonation> cachedDonations = new HashSet<>();

	/** Map of all the cached users. Access Token -> StreamlabsUser **/
	private Map<String, StreamlabsUser> cachedUsers = new HashMap<>();

	/**
	 * List of all the registered streamers that have a donation goal linked with
	 * the server
	 **/
	private List<LabUser> streamers = new ArrayList<>();

	static {
		ACCESS_TOKENS = new ArrayList<>();
		ACCESS_TOKENS.add("bjFE8xR5estui1L9T7xOyla5uxdnAoBcsTnP1ma8");
		ACCESS_TOKENS.add("zQNsYB4gxums6V2wKrbBVmU4qkGuFEXRF2iO4JT0");

		API = StreamlabsApiBuilder.builder().withClientSecret(CLIENT_SECRET).withClientId(CLIENT_ID).build();
	}

	/**
	 * Gets a fresh map of all the new donations
	 * 
	 * @return
	 */
	
	private static Integer smaller = null;
	private static Integer larger = null;
	
	public static Map<String, List<StreamlabsDonation>> getAllDonations() {
		Map<String, List<StreamlabsDonation>> toReturn = new HashMap<>();
		
		for (String t : ACCESS_TOKENS) {
			List<StreamlabsDonation> donos = API.getDonations(t, 10, smaller == null ? null : smaller.toString(), larger == null ? null : larger.toString(), null, null).execute().getDonations();
			boolean dateCheck = smaller == null;
			
			donos.forEach(d -> {
				if(smaller == null) {
					smaller = Integer.parseInt(d.getId());
				} else if(larger == null) {
					Integer i = Integer.parseInt(d.getId());
					
					if(i < smaller) {
						int temp = smaller;
						smaller = i;
						larger = temp;
					} else {
						larger = i;
					}
				} else {
					Integer i = Integer.parseInt(d.getId());
					
					if(i < smaller)
						smaller = i;
					else if(i > larger)
						larger = i;
				}
			});
			
			if(!dateCheck) {
				toReturn.put(t, donos);
			} else {
				toReturn.put(t, donos.stream().filter(don -> don.getCreationDate().getTime() >= Main.getInstance().getStartTime())
						.collect(Collectors.toList()));
			}
		}

		return toReturn;
	}
	
	public LabUser getUser(String token) {
		return getUser(token, false);
	}
	
	public LabUser getUser(String str, boolean twitch) {
		return streamers.stream().filter(s -> twitch ? s.getTwitchUser().equalsIgnoreCase(str) : s.getAccessToken().equals(str)).findFirst().orElse(null);
	}
	
	public LabUser getUser(UUID uuid) {
		return streamers.stream().filter(s -> s.getMcUuid().equals(uuid)).findAny().orElse(null);
	}
	
}
