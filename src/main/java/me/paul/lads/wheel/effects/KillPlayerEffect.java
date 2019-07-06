package me.paul.lads.wheel.effects;

import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import me.paul.lads.Main;
import me.paul.lads.wheel.GenerateEffect;
import me.paul.lads.wheel.Wheel;
import me.paul.lads.wheel.WheelEffect;
import net.md_5.bungee.api.ChatColor;

@GenerateEffect(description = "An effect that lets the spinner kill anyone on the server", key = "effect_kill", name = "Wheel Kill")
public class KillPlayerEffect extends WheelEffect {

	private ConversationFactory cf;
	
	public KillPlayerEffect() {
		this.cf = new ConversationFactory(Main.getInstance());
	}
	
	@Override
	public void play(Player spinner, Wheel spun) {
		cf.withFirstPrompt(new KillPrompt()).withLocalEcho(true).buildConversation(spinner).begin();
	}

	class KillPrompt extends StringPrompt {

		@Override
		public Prompt acceptInput(ConversationContext context, String answer) {
			Player target = Bukkit.getPlayer(answer);
			if(target == null || !target.isOnline())  {
				context.getForWhom().sendRawMessage(ChatColor.RED + "The player " + answer + " is either not online right now or doesn't exist!");
				return new KillPrompt();
			}
			
			context.getForWhom().sendRawMessage(ChatColor.GREEN + "Very well... " + target.getName() + " has been dealt with accordingly ;)");
			target.setHealth(0.0D);
			return null;
		}

		@Override
		public String getPromptText(ConversationContext context) {
			return ChatColor.GOLD + "If you could kill anyone on the server... who would it be? :3";
		}
		
	}
	
}
