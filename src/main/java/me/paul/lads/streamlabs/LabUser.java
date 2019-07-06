package me.paul.lads.streamlabs;

import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @RequiredArgsConstructor @EqualsAndHashCode(of = {"mcUuid", "twitchUser"})
public class LabUser {

	private final String mcUser;
	private final UUID mcUuid;
	
	private final String twitchUser;
	private final String accessToken;
	
	@Setter
	private double wheelGoal = 50;
	@Setter
	private double moneyReceived = 0D;
	@Setter
	private int spins = 0;
}
