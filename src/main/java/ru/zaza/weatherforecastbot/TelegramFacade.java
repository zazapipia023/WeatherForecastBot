package ru.zaza.weatherforecastbot;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.zaza.weatherforecastbot.enums.MethodState;
import ru.zaza.weatherforecastbot.utility.ForecastUtil;

import java.lang.reflect.Method;
import java.util.HashMap;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramFacade {
    private HashMap<Long, Boolean> userRequest = new HashMap<>(){
        @Override
        public Boolean get(Object key) {
            if(!containsKey(key)) {
                return false;
            }
            return super.get(key);
        }
    };
    private HashMap<Long, MethodState> userMethod = new HashMap<>();
    private ReplyKeyboardMarker replyKeyboardMarker = new ReplyKeyboardMarker();
    Method method;
    final ForecastUtil UTIL = new ForecastUtil();

    static final String HELP_TEXT = """
            There is description of my commands:\s

            /forecast - making weather forecast""";


    public BotApiMethod<?> handleUpdate(Update update) {

        if(update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return null;
        } else {

            Message message = update.getMessage();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(message.getChatId()));
            if(message.hasText()) {
//                sendMessage.setText("hello");
//                return sendMessage;
                return handleInputMessage(message);
            }

        }
        return null;
    }

    private SendMessage handleInputMessage(Message message) {
        String messageText = message.getText();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());

        switch (messageText) {
            case "/start" -> {
                sendMessage.setText("Type /help for guide");
                userRequest.put(message.getChatId(), false);
                return sendMessage;
            }
            case "/help" -> {
                sendMessage.setText(HELP_TEXT);
                userRequest.put(message.getChatId(), false);
                return sendMessage;
            }
            case "/forecast" -> {
                sendMessage.setText("Which forecast you want to see?");
                sendMessage.setReplyMarkup(replyKeyboardMarker.getMainKeyboard());
                userRequest.put(message.getChatId(), false);
                return sendMessage;
            }
            case "Current forecast" -> {
                sendMessage.setText("Enter city");
                userRequest.put(message.getChatId(), true);
                userMethod.put(message.getChatId(), MethodState.CURRENTFORECAST);
                return sendMessage;
            }
            case "Detailed today's forecast" -> {
                sendMessage.setText("Enter city");
                userRequest.put(message.getChatId(), true);
                userMethod.put(message.getChatId(), MethodState.TODAYSDETAILEDFORECAST);
                return sendMessage;
            }
            case "Tomorrow's forecast" -> {
                sendMessage.setText("Enter city");
                userRequest.put(message.getChatId(), true);
                userMethod.put(message.getChatId(), MethodState.TOMORROWSFORECAST);
                return sendMessage;
            }
            default -> {
                if(userRequest.get(message.getChatId())) {
                    String textToSend = UTIL.giveForecast(messageText, userMethod.get(message.getChatId()));
                    if(textToSend.equals("City not found, try again")) {
                        sendMessage.setText(textToSend);
                        return sendMessage;
                    } else {
                        sendMessage.setText(textToSend);
                        userRequest.put(message.getChatId(), false);
                        return sendMessage;
                    }
                } else {
                    sendMessage.setText("Sorry, i don't understand you");
                    return sendMessage;
                }
            }
        }
    }

}
