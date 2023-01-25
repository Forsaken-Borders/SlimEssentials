package net.forsaken_borders;

import eu.midnightdust.lib.config.MidnightConfig;

public class FabrissentialsConfig extends MidnightConfig {

	// Check out https://github.com/TeamMidnightDust/MidnightLib/wiki/Using-MidnightConfig for Info on how to use this Class
	// We need to wait for our translation branch to be done, unless we wanna push English-Only until then
	// FIXME: Wait for translation Branch

	@Comment(centered = true) public static Comment enableAliasComment;
	@Entry public static boolean enableUnbanAlias = true;


}
