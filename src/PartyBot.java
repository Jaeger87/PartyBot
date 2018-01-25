import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.botticelli.bot.Bot;
import com.botticelli.bot.request.methods.AudioReferenceToSend;
import com.botticelli.bot.request.methods.DocumentFileToSend;
import com.botticelli.bot.request.methods.DocumentReferenceToSend;
import com.botticelli.bot.request.methods.MessageToSend;
import com.botticelli.bot.request.methods.PhotoFileToSend;
import com.botticelli.bot.request.methods.PhotoReferenceToSend;
import com.botticelli.bot.request.methods.types.Audio;
import com.botticelli.bot.request.methods.types.CallbackQuery;
import com.botticelli.bot.request.methods.types.ChosenInlineResult;
import com.botticelli.bot.request.methods.types.InlineKeyboardButton;
import com.botticelli.bot.request.methods.types.InlineKeyboardMarkup;
import com.botticelli.bot.request.methods.types.InlineQuery;
import com.botticelli.bot.request.methods.types.KeyboardButton;
import com.botticelli.bot.request.methods.types.Message;
import com.botticelli.bot.request.methods.types.ParseMode;
import com.botticelli.bot.request.methods.types.PhotoSize;
import com.botticelli.bot.request.methods.types.PreCheckoutQuery;
import com.botticelli.bot.request.methods.types.ReplyKeyboardMarkupWithButtons;
import com.botticelli.bot.request.methods.types.ShippingQuery;
import com.botticelli.bot.request.methods.types.User;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class PartyBot extends Bot {

	private long boss;
	private ReplyKeyboardMarkupWithButtons mainMenu;
	private boolean active = true;
	private HashSet<Long> users;
	private HashMap<Long, Integer> banRegister;
	private HashSet<Long> banned;
	private final int banLimit = 5;

	public PartyBot(String token) throws FileNotFoundException {
		super(token);
		banRegister = new HashMap<>();
		banned = new HashSet<>();
		users = new HashSet<>();

		try (Scanner s = new Scanner(new File(Main.filePath + Constants.AUTHORIZEDUSERS))) {
			while (s.hasNext())
				boss = s.nextLong();
		}

		// creating the keyboard for the menu
		List<List<KeyboardButton>> keyboard = new ArrayList<List<KeyboardButton>>();
		List<KeyboardButton> line = new ArrayList<>();
		line.add(new KeyboardButton(Constants.PREVTRACK));
		line.add(new KeyboardButton(Constants.PLAY));
		line.add(new KeyboardButton(Constants.PAUSE));
		line.add(new KeyboardButton(Constants.STOP));
		line.add(new KeyboardButton(Constants.NEXTTRACK));
		keyboard.add(line);

		line = new ArrayList<>();
		line.add(new KeyboardButton(Constants.VOLDOWN));
		line.add(new KeyboardButton(Constants.VOLUP));
		keyboard.add(line);

		mainMenu = new ReplyKeyboardMarkupWithButtons(keyboard);
		mainMenu.setResizeKeyboard(true);

	}

	@Override
	public void audioMessage(Message m) {
		users.add(m.getFrom().getId());
		if (!control(m))
			return;

		if (m.getFrom().getId() == boss) {
			addTrack(m.getAudio());
			return;
		}

		addTrack(m.getAudio());

		/*
		 * List<List<InlineKeyboardButton>> inlKeyboard = new
		 * ArrayList<List<InlineKeyboardButton>>(); List<InlineKeyboardButton> line =
		 * new ArrayList<>(); InlineKeyboardButton button = new
		 * InlineKeyboardButton(Constants.YES);
		 * button.setCallback_data(CallBackCodes.MUSICYES.toString() +
		 * Constants.SEPARATOR); line.add(button);
		 * 
		 * button = new InlineKeyboardButton(Constants.NO);
		 * button.setCallback_data(CallBackCodes.MUSICNO.toString() +
		 * Constants.SEPARATOR + m.getFrom().getId() ); line.add(button);
		 * 
		 * inlKeyboard.add(line);
		 * 
		 * AudioReferenceToSend arts = new AudioReferenceToSend(boss,
		 * m.getAudio().getFileID()); arts.setReplyMarkup(new
		 * InlineKeyboardMarkup(inlKeyboard));
		 * arts.setCaption(captionFactory(m.getFrom()));
		 * 
		 * sendAudiobyReference(arts);
		 * 
		 */

	}

	@Override
	public void callback_query(CallbackQuery c) {

		if (!active)
			return;

		String[] values = c.getData().split(Constants.SEPARATOR);
		CallBackCodes cbc = CallBackCodes.fromString(values[0]);

		switch (cbc) {
		case PHOTOYES:
			downloadPhotos(c.getMessage().getPhoto());
			break;
		case PHOTONO:
			updateBanRegister(Long.parseLong(values[1]));
			break;
		case MUSICYES:
			addTrack(c.getMessage().getAudio());
			break;
		case MUSICNO:
			updateBanRegister(Long.parseLong(values[1]));
			break;
		}

	}

	@Override
	public void chose_inline_result(ChosenInlineResult arg0) {

	}

	@Override
	public void contactMessage(Message arg0) {

	}

	@Override
	public void documentMessage(Message arg0) {

	}

	@Override
	public void gameMessage(Message arg0) {

	}

	@Override
	public void groupChatCreatedMessage(Message arg0) {

	}

	@Override
	public void groupChatPhotoDeleteMessage(Message arg0) {

	}

	@Override
	public void inLineQuery(InlineQuery arg0) {

	}

	@Override
	public void invoiceMessage(Message arg0) {

	}

	@Override
	public void leftChatMemberMessage(Message arg0) {

	}

	@Override
	public void locationMessage(Message arg0) {

	}

	@Override
	public void newChatMemberMessage(Message arg0) {

	}

	@Override
	public void newChatMembersMessage(Message arg0) {

	}

	@Override
	public void newChatPhotoMessage(Message arg0) {

	}

	@Override
	public void newChatTitleMessage(Message arg0) {

	}

	@Override
	public void photoMessage(Message m) {

		users.add(m.getFrom().getId());
		if (!control(m))
			return;
		if (m.getFrom().getId() == boss) {
			downloadPhotos(m.getPhoto());
			return;
		}

		downloadPhotos(m.getPhoto());
		/*
		 * List<List<InlineKeyboardButton>> inlKeyboard = new
		 * ArrayList<List<InlineKeyboardButton>>(); List<InlineKeyboardButton> line =
		 * new ArrayList<>(); InlineKeyboardButton button = new
		 * InlineKeyboardButton(Constants.YES);
		 * button.setCallback_data(CallBackCodes.PHOTOYES.toString() +
		 * Constants.SEPARATOR); line.add(button);
		 * 
		 * button = new InlineKeyboardButton(Constants.NO);
		 * button.setCallback_data(CallBackCodes.PHOTONO.toString() +
		 * Constants.SEPARATOR + m.getFrom().getId() ); line.add(button);
		 * 
		 * inlKeyboard.add(line);
		 * 
		 * String bigPhotoID = m.getPhoto() .stream() .reduce(m.getPhoto().get(0),
		 * (p1,p2) -> {if(p1.getfileSize() > p2.getfileSize()) return p1; else return
		 * p2;}) .getFileID();
		 * 
		 * PhotoReferenceToSend prts = new PhotoReferenceToSend(boss, bigPhotoID);
		 * prts.setReplyMarkup(new InlineKeyboardMarkup(inlKeyboard));
		 * prts.setCaption(captionFactory(m.getFrom()));
		 * 
		 * sendPhotobyReference(prts);
		 * 
		 */
	}

	@Override
	public void pinnedMessage(Message arg0) {

	}

	@Override
	public void preCheckOutQueryMessage(PreCheckoutQuery arg0) {

	}

	@Override
	public void shippingQueryMessage(ShippingQuery arg0) {

	}

	@Override
	public void stickerMessage(Message arg0) {

	}

	@Override
	public void successfulPaymentMessage(Message arg0) {

	}

	@Override
	public void textMessage(Message m) {

		users.add(m.getFrom().getId());

		if (!control(m))
			return;

		if (boss != m.getFrom().getId())
			return;

		if (m.getText().equals(Constants.STOPPARTY)) {
			users.remove(boss);
			active = false;
			// vlcCommand("/STOP");
			zipPhotos();
			mosaic();
			return;
		}

		if (m.getText().equals(Constants.PREVTRACK)) {
			vlcCommand("/PREV");
			return;
		}

		if (m.getText().equals(Constants.PLAY)) {
			vlcCommand("/PLAY");
			return;
		}

		if (m.getText().equals(Constants.PAUSE)) {
			vlcCommand("/PAUSE");
			return;
		}

		if (m.getText().equals(Constants.STOP)) {
			vlcCommand("/STOP");
			return;
		}

		if (m.getText().equals(Constants.NEXTTRACK)) {
			vlcCommand("/NEXT");
			return;
		}

		if (m.getText().equals(Constants.VOLDOWN)) {
			vlcCommand("/VOLDWN");
			return;
		}

		if (m.getText().equals(Constants.VOLUP)) {
			vlcCommand("/VOLUP");
			return;
		}
		MessageToSend mts = new MessageToSend(m.getChat().getId(), "Ecco il tastierino padrone");
		mts.setParseMode(ParseMode.MARKDOWN);
		mts.setReplyMarkup(mainMenu);
		sendMessage(mts);

	}

	@Override
	public void venueMessage(Message arg0) {

	}

	@Override
	public void videoMessage(Message arg0) {

	}

	@Override
	public void videoNoteMessage(Message arg0) {

	}

	@Override
	public void voiceMessage(Message arg0) {

	}

	private boolean vlcCommand(String command) {
		Process process;
		try {
			process = Runtime.getRuntime().exec("vlc --one-instance \"+ command");
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return true;
	}

	private boolean control(Message m) {
		return active && (!banned.contains(m.getFrom().getId()));
	}

	private void updateBanRegister(long evil) {
		if (!banRegister.containsKey(evil)) {
			banRegister.put(evil, 1);
			return;
		}
		int newValue = banRegister.get(evil) + 1;

		if (newValue >= banLimit)
			banned.add(evil);

		banRegister.put(evil, newValue);
		return;
	}

	private void downloadPhotos(List<PhotoSize> photos) {
		String bigPhotoID = photos.stream().reduce(photos.get(0), (p1, p2) -> {
			if (p1.getfileSize() > p2.getfileSize())
				return p1;
			else
				return p2;
		}).getFileID();

		String smallPhotoID = photos.stream().reduce(photos.get(0), (p1, p2) -> {
			if (p1.getfileSize() < p2.getfileSize())
				return p1;
			else
				return p2;
		}).getFileID();

		downloadFileFromTelegramServer(bigPhotoID, Constants.PHOTOFOLDER + bigPhotoID + ".png");
		downloadFileFromTelegramServer(smallPhotoID, Constants.TILESFOLDER + smallPhotoID + ".png");
	}

	private void addTrack(Audio music) {
		File f = downloadFileFromTelegramServer(music, Constants.MUSICFOLDER + music.getFileID() + ".mp3");
		vlcCommand("--playlist-enqueue " + f.getAbsolutePath());
	}

	private void zipPhotos()
	{
		try
		{

			
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);

			Path source = Paths.get(Constants.PHOTOFOLDER);
			List<File> photosFile = new ArrayList<>();

			try 
			{
				photosFile = Files.walk(source).filter(Files::isRegularFile).map(p -> p.toFile())
						.collect(Collectors.toList());
			} catch (IOException e1) 
			{

				e1.printStackTrace();
			}
			int limit = 100;
			int zipFileNumber = 0;
			int i = 0;
			while ((limit - photosFile.size()) < 100) 
			{
				Files.deleteIfExists(new File("photos"+zipFileNumber+".zip").toPath());

				
				ZipFile zipFile = new ZipFile("photos"+zipFileNumber+".zip");
				
				for (; i < limit && i < photosFile.size(); i++) 
					zipFile.addFile(photosFile.get(i), parameters);
				Message m = null;
				while (m == null)
					m = sendDocumentFile(new DocumentFileToSend(boss, zipFile.getFile()));

				String fileId = m.getDocument().getFileID();

				try {
					TimeUnit.MILLISECONDS.sleep(800);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}

				for (Long user : users) {
					sendDocumentbyReference(new DocumentReferenceToSend(user, fileId));
				}
				limit += 100;
				zipFileNumber++;
			}
		} 
		
		catch (ZipException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e1) 
		{
			
			e1.printStackTrace();
		}
	}

	private void mosaic() {
		StringBuilder sb = new StringBuilder();

		try {
			Files.deleteIfExists(new File("mosaico.png").toPath());
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		Path source = Paths.get(Constants.TILESFOLDER);
		try {
			Files.walk(source).filter(Files::isRegularFile)
					.forEach(f -> sb.append(f.toAbsolutePath().toString() + " "));
		} catch (IOException e1) {

			e1.printStackTrace();
		}

		try {
			Process process = Runtime.getRuntime()
					.exec("montage " + sb.toString() + "-shadow  -geometry +1+1  -texture wall3.jpg  mosaico.png");
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			TimeUnit.MILLISECONDS.sleep(1500);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		Message m = sendPhotoFile(new PhotoFileToSend(boss, new File("mosaico.png")));
		String fileId = m.getDocument().getFileID();
		try {
			TimeUnit.MILLISECONDS.sleep(800);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		for (Long user : users) {
			sendPhotobyReference(new PhotoReferenceToSend(user, fileId));
		}

	}

	private String captionFactory(User u) {
		if (u.getUserName() != null)
			return u.getUserName();
		return u.getFirstName();
	}

}
