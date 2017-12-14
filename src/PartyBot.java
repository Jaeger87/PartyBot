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


public class PartyBot extends Bot{

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
		
		try (Scanner s = new Scanner(new File(Main.filePath + Constants.AUTHORIZEDUSERS)))
		{
			while (s.hasNext())
				boss = s.nextLong();
		}
		
		//creating the keyboard for the menu
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
		if(!control(m))
			return;
		
		if(m.getFrom().getId() == boss)
		{
			addTrack(m.getAudio());
			return;
		}
		

		List<List<InlineKeyboardButton>> inlKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> line = new ArrayList<>();
		InlineKeyboardButton button = new InlineKeyboardButton(Constants.YES);
		button.setCallback_data(CallBackCodes.MUSICYES.toString() + Constants.SEPARATOR);
		line.add(button);
		
		button = new InlineKeyboardButton(Constants.NO);
		button.setCallback_data(CallBackCodes.MUSICNO.toString() + Constants.SEPARATOR +	m.getFrom().getId() );
		line.add(button);
		
		inlKeyboard.add(line);
		
		AudioReferenceToSend arts = new AudioReferenceToSend(boss, m.getAudio().getFileID());
		arts.setReplyMarkup(new InlineKeyboardMarkup(inlKeyboard));
		arts.setCaption(captionFactory(m.getFrom()));
		
		sendAudiobyReference(arts);
		
	}

	@Override
	public void callback_query(CallbackQuery c) {
		
		if(!active)
			return;

		String[] values = c.getData().split(Constants.SEPARATOR);
		CallBackCodes cbc = CallBackCodes.fromString(values[0]);
		
		switch(cbc)
		{
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void groupChatCreatedMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void groupChatPhotoDeleteMessage(Message arg0) {
		
	}

	@Override
	public void inLineQuery(InlineQuery arg0) {
		
	}

	@Override
	public void invoiceMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void leftChatMemberMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void locationMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newChatMemberMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newChatMembersMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newChatPhotoMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newChatTitleMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void photoMessage(Message m) {
		
		users.add(m.getFrom().getId());
		if(!control(m))
			return;
		if(m.getFrom().getId() == boss)
		{
			downloadPhotos(m.getPhoto());
			return;
		}
		
		List<List<InlineKeyboardButton>> inlKeyboard = new ArrayList<List<InlineKeyboardButton>>();
		List<InlineKeyboardButton> line = new ArrayList<>();
		InlineKeyboardButton button = new InlineKeyboardButton(Constants.YES);
		button.setCallback_data(CallBackCodes.PHOTOYES.toString() + Constants.SEPARATOR);
		line.add(button);
		
		button = new InlineKeyboardButton(Constants.NO);
		button.setCallback_data(CallBackCodes.PHOTONO.toString() + Constants.SEPARATOR +	m.getFrom().getId() );
		line.add(button);
		
		inlKeyboard.add(line);
		
		String bigPhotoID = m.getPhoto()
				.stream()
				.reduce(m.getPhoto().get(0), (p1,p2) -> {if(p1.getfileSize() > p2.getfileSize()) return p1; else return p2;})
				.getFileID();
		
		PhotoReferenceToSend prts = new PhotoReferenceToSend(boss, bigPhotoID);
		prts.setReplyMarkup(new InlineKeyboardMarkup(inlKeyboard));
		prts.setCaption(captionFactory(m.getFrom()));
		
		sendPhotobyReference(prts);
	}

	@Override
	public void pinnedMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preCheckOutQueryMessage(PreCheckoutQuery arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shippingQueryMessage(ShippingQuery arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stickerMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void successfulPaymentMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void textMessage(Message m) {

		users.add(m.getFrom().getId());
		
		if(!control(m))
			return;
		
		
		if (boss != m.getFrom().getId()) 
			return;
		
		
		if(m.getText().equals(Constants.STOPPARTY))
		{
			users.remove(boss);
			active = false;
			zipPhotos();
			mosaic();
			return;
		}
		
		if(m.getText().equals(Constants.PREVTRACK))
		{
			aimpCommand("/PREV");
			return;
		}
		
		if(m.getText().equals(Constants.PLAY))
		{
			aimpCommand("/PLAY");
			return;
		}
		
		
		if(m.getText().equals(Constants.PAUSE))
		{
			aimpCommand("/PAUSE");
			return;
		}
		
		if(m.getText().equals(Constants.STOP))
		{
			aimpCommand("/STOP");
			return;
		}
		
		if(m.getText().equals(Constants.NEXTTRACK))
		{
			aimpCommand("/NEXT");
			return;
		}
		
		if(m.getText().equals(Constants.VOLDOWN))
		{
			aimpCommand("/VOLDWN");
			return;
		}
		
		
		if(m.getText().equals(Constants.VOLUP))
		{
			aimpCommand("/VOLUP");
			return;
		}
		MessageToSend mts = new MessageToSend(m.getChat().getId(), "Ecco il tastierino padrone");
		mts.setParseMode(ParseMode.MARKDOWN);
		mts.setReplyMarkup(mainMenu);
		sendMessage(mts);

	}

	@Override
	public void venueMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void videoMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void videoNoteMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void voiceMessage(Message arg0) {
		
	}

	private boolean aimpCommand(String command)
	{
		try {
			Runtime.getRuntime().exec("cmd /C \"\"C:\\Program Files (x86)\\AIMP\\AIMP.exe\"\"" + command);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	
	private boolean control(Message m)
	{
		return active && (!banned.contains(m.getFrom().getId()));
	}
	
	
	private void updateBanRegister(long evil)
	{
		if(!banRegister.containsKey(evil))
		{
			banRegister.put(evil, 1);
			return;
		}
		int newValue = banRegister.get(evil) + 1;
		
		if(newValue >= banLimit)
			banned.add(evil);
	
		banRegister.put(evil, newValue);	
		return;
	}
	
	private void downloadPhotos(List<PhotoSize> photos)
	{
		String bigPhotoID = photos
				.stream()
				.reduce(photos.get(0), (p1,p2) -> {if(p1.getfileSize() > p2.getfileSize()) return p1; else return p2;})
				.getFileID();
		
		String smallPhotoID = photos
				.stream()
				.reduce(photos.get(0), (p1,p2) -> {if(p1.getfileSize() < p2.getfileSize()) return p1; else return p2;})
				.getFileID();
		
		downloadFileFromTelegramServer(bigPhotoID, Constants.PHOTOFOLDER + bigPhotoID + ".png");
		downloadFileFromTelegramServer(smallPhotoID, Constants.TILESFOLDER + smallPhotoID + ".png");
	}
	
	private void addTrack(Audio music)
	{
		File f = downloadFileFromTelegramServer(music, Constants.MUSICFOLDER + music.getFileID() + ".mp3");
		aimpCommand("/INSERT " + f.getAbsolutePath());
	}
	
	
	private void zipPhotos()
	{
		try {
			
			Files.deleteIfExists(new File("photos.zip").toPath());
			
			ZipFile zipFile = new ZipFile("photos.zip");			
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			
			zipFile.addFolder("photos", parameters);
			
			Message m = sendDocumentFile(new DocumentFileToSend(boss, zipFile.getFile()));
			
			String fileId = m.getDocument().getFileID();
			

			try {
				TimeUnit.MILLISECONDS.sleep(800);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			
			for(Long user : users)
			{
				sendDocumentbyReference(new DocumentReferenceToSend(user, fileId));
			}
			
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void mosaic()
	{
		StringBuilder sb = new StringBuilder();
		
		/*
		File dir = new File(Constants.TILESFOLDER);
		
		File[] directoryListing = dir.listFiles();
		if (directoryListing == null)
			return;

		for (File child : directoryListing)
			sb.append(child.getAbsolutePath() + " ");
			
		*/
		
		try {
			Files.deleteIfExists(new File("mosaico.png").toPath());
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		Path source = Paths.get(Constants.TILESFOLDER);
		try {
			Files.walk(source).filter(Files::isRegularFile).forEach(f -> sb.append(f.toAbsolutePath().toString() + " "));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		
		try 
		{
			Process process = Runtime.getRuntime()
					.exec("magick montage " + sb.toString() 
					+ "-shadow  -geometry +1+1  -texture wall3.jpg  mosaico.png");
			process.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
		
		for(Long user : users)
		{
			sendPhotobyReference(new PhotoReferenceToSend(user, fileId));
		}
		
	}
	
	private String captionFactory(User u)
	{
		if(u.getUserName() != null)
			return u.getUserName();
		return u.getFirstName();
	}
	
}
