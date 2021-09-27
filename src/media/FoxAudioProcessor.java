package media;

import fox.Out;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class FoxAudioProcessor {
	private static Map<String, File> musicMap = new LinkedHashMap<String, File>();
	private static Map<String, File> soundMap = new LinkedHashMap<String, File>();
	
//	private static Player musicPlayer;
	private static AdvancedPlayer musicPlayer;
	private static AdvancedPlayer soundPlayer;
	
	private static Boolean soundEnabled = false, musicEnabled = false;
	private static Float gVolume = 1f;
	
	
	public static void addSound(String name, File audioFile) {soundMap.put(name, audioFile);}
	
	public static void addMusic(String name, File audioFile) {musicMap.put(name, audioFile);}
	
	
	public static void playSound(String trackName, Double vol) {
		if (!soundEnabled) return;
		
		if (soundMap.containsKey(trackName)) {
			new Thread(() -> {
				try (InputStream fis = new FileInputStream(soundMap.get(trackName).toString())) {
					soundPlayer = new AdvancedPlayer(fis);
					soundPlayer.play();
				} catch (IOException | JavaLayerException e) {e.printStackTrace();}
			}).start();
		}
	}
	
	private static void playMusic(String trackName, Boolean rep) {
		if (!musicEnabled) {return;}
		
		if (musicMap.containsKey(trackName)) {
			stopMusic();
		    
	        new Thread(() -> {
				try (InputStream fis = new FileInputStream(musicMap.get(trackName).toString())) {
					musicPlayer = new AdvancedPlayer(fis);
					musicPlayer.play();
				} catch (IOException | JavaLayerException e) {e.printStackTrace();
				} finally {musicPlayer.close();}
			}).start();
		
			Out.Print("Media: music: the '" + trackName + "' exist into musicMap and play now...");
		} else {Out.Print("Media: music: music '" + trackName + "' is NOT exist in the musicMap");}
	}
	
	public static void nextMusic() {
		int playingNow = new Random().nextInt(musicMap.size() + 1);
		
		int tmp = 0;
		for (String musikName : musicMap.keySet()) {
			if (tmp == playingNow) {
				playMusic(musikName, true);
				break;
			}
			tmp++;
		}
	}
	
	public static void pauseMusic() {

	}
	
	public static void stopMusic() {
		try {
			musicPlayer.stop();
		} catch (Exception e) {/* IGNORE */}

		try {
			soundPlayer.close();
		} catch (Exception e) {/* IGNORE */}
	}
	
	public static void resumeMusic() {
		if (!musicEnabled) return;
		
	}
	
	
	public static void setVolume(Float vol) {gVolume = vol;}
	public static Float getVolume() {return gVolume;}
	
	public static void setSoundEnabled(Boolean _soundEnabled) {soundEnabled = _soundEnabled;}
	public static Boolean getSoundEnabled() {return soundEnabled;}

	public static void setMusicEnabled(Boolean _musicEnabled) {musicEnabled = _musicEnabled;}
	public static Boolean getMusicEnabled() {return musicEnabled;}
}
