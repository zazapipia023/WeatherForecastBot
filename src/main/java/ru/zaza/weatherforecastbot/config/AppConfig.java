package ru.zaza.weatherforecastbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import ru.zaza.weatherforecastbot.TelegramBot;
import ru.zaza.weatherforecastbot.TelegramFacade;

import java.util.Set;

@Configuration
public class AppConfig {
    private final BotConfig botConfig;

    public AppConfig(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(botConfig.getWebhookPath()).build();
    }

    @Bean
    public TelegramBot springWebhookBot(SetWebhook setWebhook, TelegramFacade telegramFacade) {
        TelegramBot bot = new TelegramBot(telegramFacade, setWebhook);

        bot.setBotToken(botConfig.getBotToken());
        bot.setBotName(botConfig.getBotName());
        bot.setBotPath(botConfig.getWebhookPath());

        return bot;
    }

}
