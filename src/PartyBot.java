import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import com.botticelli.bot.Bot;
import com.botticelli.bot.request.methods.MessageToSend;
import com.botticelli.bot.request.methods.types.CallbackQuery;
import com.botticelli.bot.request.methods.types.ChosenInlineResult;
import com.botticelli.bot.request.methods.types.InlineQuery;
import com.botticelli.bot.request.methods.types.KeyboardButton;
import com.botticelli.bot.request.methods.types.Message;
import com.botticelli.bot.request.methods.types.ParseMode;
import com.botticelli.bot.request.methods.types.PreCheckoutQuery;
import com.botticelli.bot.request.methods.types.ReplyKeyboardMarkupWithButtons;
import com.botticelli.bot.request.methods.types.ShippingQuery;


public class PartyBot extends Bot{

	private HashSet<Long> authorizedUsers;
	private ReplyKeyboardMarkupWithButtons mainMenu;
	
	public PartyBot(String token) throws FileNotFoundException {
		super(token);

		authorizedUsers = new HashSet<>();
		try (Scanner s = new Scanner(new File(Main.filePath + Constants.AUTHORIZEDUSERS)))
		{
			while (s.hasNext())
			{
				long l = s.nextLong();
				authorizedUsers.add(l);
			}
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
	public void audioMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callback_query(CallbackQuery arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void chose_inline_result(ChosenInlineResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contactMessage(Message arg0) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inLineQuery(InlineQuery arg0) {
		// TODO Auto-generated method stub
		
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
	public void photoMessage(Message arg0) {
		// TODO Auto-generated method stub
		
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

		if (!authorizedUsers.contains(m.getFrom().getId())) 
			return;
		
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
		// TODO Auto-generated method stub
		
	}

	public boolean aimpCommand(String command)
	{
		try {
			Runtime.getRuntime().exec("cmd /C \"\"C:\\Program Files (x86)\\AIMP\\AIMP.exe\"\"" + command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
}
