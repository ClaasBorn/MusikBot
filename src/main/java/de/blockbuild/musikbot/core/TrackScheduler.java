package de.blockbuild.musikbot.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.core.entities.Guild;

public class TrackScheduler extends AudioEventAdapter implements AudioEventListener {

	private final GuildMusicManager musicManager;
	private final AudioPlayer player;
	private final BlockingQueue<AudioTrack> queue;
	private final Guild guild;

	public TrackScheduler(Guild guild, GuildMusicManager musicManager) {
		this.musicManager = musicManager;
		this.player = musicManager.getAudioPlayer();
		this.queue = new LinkedBlockingQueue<AudioTrack>();
		this.guild = guild;
	}

	public void queue(AudioTrack track, CommandEvent event) {
		StringBuilder builder = new StringBuilder(event.getClient().getSuccess());

		if (!player.startTrack(track, true)) {
			queue.offer(track);
			builder.append(" Successfully added: `").append(track.getInfo().title).append("` on position: ")
					.append(queue.size());
		} else {
			builder.append(" Successfully added: `").append(track.getInfo().title).append("`");
		}

		if (!(event == null)) {
			event.reply(builder.toString());
		}
	}

	public void queue(AudioPlaylist playlist, CommandEvent event) {
		StringBuilder builder = new StringBuilder(event.getClient().getSuccess());
		builder.append(" Successfully added: \n");

		for (AudioTrack track : playlist.getTracks()) {
			if (player.getPlayingTrack() == null) {
				this.playTrack(track, event);
			} else {
				queue.offer(track);
				builder.append("`").append(track.getInfo().title).append("` on position: ").append(queue.size())
						.append("\n");
			}
		}

		if (!(event == null)) {
			event.reply(builder.toString());
		}
	}

	public void playTrack(AudioTrack track, CommandEvent event) {
		if (!(event == null)) {
			StringBuilder builder = new StringBuilder(event.getClient().getSuccess());
			builder.append(" Successfully added: `").append(track.getInfo().title).append("`");
			event.reply(builder.toString());
		}
		player.startTrack(track, false);
	}

	public void nextTrack(CommandEvent event) {
		StringBuilder builder = new StringBuilder();
		if (queue.isEmpty()) {
			builder.append(event.getClient().getWarning()).append(" Queue is empty.");
		} else {
			builder.append(event.getClient().getSuccess());
			builder.append(" Now Playing: `").append(queue.peek().getInfo().title).append("`.");
			player.startTrack(queue.poll(), false);
		}
		event.reply(builder.toString());
	}

	public void shuffle() {
		List<AudioTrack> q = new ArrayList<AudioTrack>();
		Iterator<AudioTrack> x = queue.iterator();
		while (x.hasNext()) {
			AudioTrack track = x.next();
			q.add(track);
		}
		Collections.shuffle((List<AudioTrack>) q);
		queue.clear();
		for (AudioTrack track : q) {
			queue.offer(track);
		}
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		System.out.println("onTrackEnd endReason " + endReason.toString());

		if (endReason.mayStartNext) {
			player.playTrack(queue.poll());
		}

		if (player.getPlayingTrack() == null && queue.isEmpty()
				&& musicManager.config.isDisconnectAfterLastTrackEnabled()) {
			guild.getAudioManager().closeAudioConnection();
			return;
		}
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		System.out.println("onTrackStart");
		System.out.println("textChannel " + guild.getSystemChannel().getName());
		if (player.isPaused()) {
			player.setPaused(false);
		}
	}

	public String getPlaylist() {
		StringBuilder builder = new StringBuilder();
		if (queue.isEmpty()) {
			builder.append("`Queue is empty.`");
		} else {
			int i = 0;
			Iterator<AudioTrack> x = queue.iterator();
			while (x.hasNext()) {
				AudioTrack track = x.next();
				builder.append("`").append(++i).append(". ").append(track.getInfo().title).append("`\n");
			}
		}
		return builder.toString();
	}

	public AudioTrack getNextTrack() {
		return queue.peek();
	}

	public void flushQueue() {
		queue.clear();
	}

	public void stopTrack() {
		player.playTrack(null);
	}

	public void flushQueue(int amount) {
		for (int i = 0; i < amount; i++) {
			queue.poll();
		}
	}
}