package com.terransky.TestingBot.slashSystem.commands;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.terransky.TestingBot.Commons;
import com.terransky.TestingBot.slashSystem.ISlash;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class spam implements ISlash {
    private final Logger log = LoggerFactory.getLogger(spam.class);
    private final Commons cmn = new Commons();
    private final Dotenv config = Dotenv.configure().load();

    @Override
    public String getName() {
        return "spam";
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(this.getName(), "Make the bot send spam to someone! - Frost Request")
                .addSubcommands(
                        new SubcommandData("send", "Send some spam!")
                                .addOptions(
                                        new OptionData(OptionType.USER, "target", "Who to send the spam to.", true),
                                        new OptionData(OptionType.STRING, "message", "A small message you want to send as well.")
                                ),
                        new SubcommandData("upload", "Got spam? Want to add it? Send it in through this! NOTE: Not immediate!")
                                .addOptions(new OptionData(OptionType.STRING, "link", "The link to upload.", true))
                )
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
    }

    @Override
    public void slashExecute(@NotNull SlashCommandInteractionEvent event) {
        String subCommand = event.getSubcommandName() != null ? event.getSubcommandName() : "fish";
        EmbedBuilder callReply = new EmbedBuilder().setFooter("Requested by " + event.getUser().getAsTag()).setColor(cmn.defaultEmbedColor),
                toTarget = new EmbedBuilder().setFooter("From :: " + event.getUser().getAsTag()).setColor(cmn.defaultEmbedColor);

        switch (subCommand) {
            case "send" -> {
                String defaultMessage = "Totally not a spam bot!";
                User target = event.getOption("target", event.getUser(), OptionMapping::getAsUser);
                String message = event.getOption("message", defaultMessage, OptionMapping::getAsString);
                String[] theSpam = {
                        "https://post.healthline.com/wp-content/uploads/2020/08/is-spam-healthy-1200x628-facebook-1200x628.jpg",
                        "https://static.independent.co.uk/s3fs-public/thumbnails/image/2017/07/05/09/istock-180816321.jpg?quality=75&width=982&height=726&auto=webp",
                        "https://www.hormelfoods.com/wp-content/uploads/Inspired_20200111_recipe_LONO-spring-roll-musubi_musubi-Madness.jpg",
                        "https://vegnews.com/media/W1siZiIsIjI1MTM2L1ZlZ05ld3MuU1BBTUhvcm1lbC5jb20iXSxbInAiLCJjcm9wX3Jlc2l6ZWQiLCIxMDQ2eDYxOCsyNSswIiwiMTYwMHg5NDZeIix7ImZvcm1hdCI6ImpwZyJ9XSxbInAiLCJvcHRpbWl6ZSJdXQ/VegNews.SPAMHormel.com?sha=f04e38a024cb66d3",
                        "https://s3.crackedcdn.com/phpimages/article/1/9/6/525196.jpg?v=1",
                        "https://c8.alamy.com/comp/G2E6GX/spam-sandwich-G2E6GX.jpg",
                        "https://image.made-in-china.com/2f0j00WMilqmuPEvro/340g-Classic-Flavor-Canned-Meat-Food-Wholesale-Luncheon-Meat.jpg",
                        "https://media.nedigital.sg/fairprice/images/59278fd6-8e61-4741-9f3d-543e64207efb/L3_CannedMeat_281121.jpg",
                        "https://cdn.vox-cdn.com/thumbor/UO1hhAGb7ea5G-MuC43l1Sxx9Rw=/0x0:2282x1712/1200x675/filters:focal(0x0:2282x1712)/cdn.vox-cdn.com/uploads/chorus_image/image/50821489/spam-wall.0.0.jpg"
                };

                if (!message.equals(defaultMessage)) {
                    if (message.length() > MessageEmbed.TITLE_MAX_LENGTH) {
                        callReply.setTitle("Oops!")
                                .setDescription("Your spam could not be sent because the message cannot be more than " + MessageEmbed.TITLE_MAX_LENGTH + " characters!\n\n" +
                                        "Your message: ```" + message + "```")
                                .setColor(cmn.defaultEmbedColor);
                        event.replyEmbeds(callReply.build()).setEphemeral(true).queue();
                        return;
                    } else toTarget.setTitle(message);
                }

                String link = theSpam[(int) (Math.random() * theSpam.length)];
                log.debug("Linking being sent :: " + link);

                toTarget.setImage(link);
                callReply.setTitle("DONE!")
                        .setDescription("Your spam has been sent.");

                target.openPrivateChannel()
                        .flatMap(privateChannel -> privateChannel.sendMessageEmbeds(toTarget.build()))
                        .queue();
                event.replyEmbeds(callReply.build()).setEphemeral(true).queue();
            }
            case "upload" -> {
                event.deferReply(true).queue();
                String url = event.getOption("link", null, OptionMapping::getAsString);
                URL link;
                try {
                    link = new URL(url);
                } catch (MalformedURLException e) {
                    callReply.setTitle("Not a link")
                            .setDescription("Did you make sure it's an actual link?")
                            .addField("Provided", url, false);
                    event.getHook().sendMessageEmbeds(callReply.build()).setEphemeral(true).queue();
                    return;
                }

                try {
                    HttpURLConnection urlConnection = (HttpURLConnection) link.openConnection();
                    if (urlConnection.getResponseCode() == 200) {
                        WebhookClientBuilder builder = new WebhookClientBuilder(config.get("REQUEST_WEBHOOK"));
                        builder.setThreadFactory(job -> {
                            Thread thread = new Thread(job);
                            thread.setName("Request");
                            thread.setDaemon(true);
                            return thread;
                        });
                        builder.setWait(true);

                        try (WebhookClient client = builder.build()) {
                            WebhookEmbed request = new WebhookEmbedBuilder()
                                    .setColor(cmn.getIntFromColor(102, 52, 102))
                                    .setTitle(new WebhookEmbed.EmbedTitle("Spam request by: " + event.getUser().getAsTag(), null))
                                    .addField(new WebhookEmbed.EmbedField(false, "Link", url))
                                    .setImageUrl(url)
                                    .build();

                            client.send(request);
                        }

                        callReply.setTitle("Request sent!")
                                .setDescription("Your request was sent successfully!")
                                .addField("Link", url, false)
                                .setImage(url);
                    } else callReply.setTitle("Link is valid but got no return on checking.")
                            .setDescription("Either I require authentication or the there is nothing there.\nTry again?");

                    event.getHook().sendMessageEmbeds(callReply.build()).queue();
                } catch (IOException e) {
                    callReply.setTitle("Something happened!")
                            .setDescription("Either the link provided was invalid or en error occurred during checking.\nTry again in a few minutes!")
                            .addField("Link provided", url, false);
                    event.getHook().sendMessageEmbeds(callReply.build()).queue();
                }

            }
            default -> callReply.setTitle("How did you get here?")
                    .setDescription("No seriously how did you get here?\nThat's impossible.")
                    .setColor(cmn.defaultEmbedColor);
        }
    }
}
