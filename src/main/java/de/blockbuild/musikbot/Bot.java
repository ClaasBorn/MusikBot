package de.blockbuild.musikbot;

import javax.security.auth.login.LoginException;

import de.blockbuild.musikbot.Listener.MessageListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;

public class Bot {
	
	private JDA jda;
	
	public Bot() {
		start();
		initListener();
	}
	
	public boolean start() {
		try {
			jda = new JDABuilder(AccountType.BOT)
					.setToken("NTIzOTI3MzY3NDY3NjYzNDAx.Dvh6cg.r6rrETJfOYRBJp2Xc3l-zPn_BuY")
					.setGame(Game.of(GameType.DEFAULT, "Loding"))
					.setAudioEnabled(true)
					.setStatus(OnlineStatus.DO_NOT_DISTURB)
					.build();
			jda.awaitReady();
			
			System.out.println("Finished Building JDA");
			return true;
		} catch (LoginException e) {
			System.out.println(e);
			System.out.println("Invaild bot Token");
			return false;
		}
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return false;
            }

	}
	
	public boolean stop() {
		jda.shutdown();
    	System.out.println("Finished JDA shutdown");
		return true;
	}
	
	public void initListener() {
		jda.addEventListener(new MessageListener());
	}
}