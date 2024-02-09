package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.Metadata;
import com.terransky.stuffnthings.utilities.cyphers.Base64Cypher;
import com.terransky.stuffnthings.utilities.cyphers.HexCypher;
import com.terransky.stuffnthings.utilities.cyphers.ReverseCypher;
import com.terransky.stuffnthings.utilities.cyphers.Rot13Cypher;
import com.thedeanda.lorem.LoremIpsum;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * Command the encodes/decodes messages.
 */
public class CypherCmd implements ICommandSlash {
    @Override
    public String getName() {
        return "cypher";
    }

    @Override
    public Metadata getMetadata() {
        final List<SubcommandData> enDeCode = List.of(
            new SubcommandData("encode", "Encode a message")
                .addOptions(
                    new OptionData(OptionType.STRING, "message", "The message to encode", true)
                        .setRequiredLength(5, MessageEmbed.DESCRIPTION_MAX_LENGTH / 4)
                ),
            new SubcommandData("decode", "Decode a message")
                .addOptions(
                    new OptionData(OptionType.STRING, "message", "The message to decode", true)
                        .setRequiredLength(5, MessageEmbed.DESCRIPTION_MAX_LENGTH / 4)
                )
        );
        return new Metadata(getName(), "Encode/Decode messages using different cyphers.", """
            Encode/Decode messages using different cyphers.
            Current Cyphers:
            ```
            • Base64
            • Rot13
            • Reverse
            ```
            """, Mastermind.DEVELOPER, CommandCategory.FUN,
            Metadata.parseDate(2023, 2, 5, 14, 41),
            Metadata.parseDate(2024, 2, 9, 16, 11)
        )
            .addSubcommandGroups(
                new SubcommandGroupData("base64", "Base64 Cypher")
                    .addSubcommands(enDeCode),
                new SubcommandGroupData("rot13", "Rot13 Cypher")
                    .addSubcommands(enDeCode),
                new SubcommandGroupData("reverse", "Reverse Cypher")
                    .addSubcommands(enDeCode),
                new SubcommandGroupData("hexadecimal", "Hexadecimal Cypher")
                    .addSubcommands(enDeCode)
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        String[] fullCommandName = event.getFullCommandName().split(" ");
        String cypher = fullCommandName[1] != null ? fullCommandName[1] : "base64";
        boolean isEncode = fullCommandName[2] != null && "encode".equals(fullCommandName[2]);
        String message = event.getOption("message", LoremIpsum.getInstance().getWords(10), OptionMapping::getAsString);
        String enDecodedString;

        switch (cypher) {
            case "base64" -> {
                Base64Cypher base64 = new Base64Cypher();
                enDecodedString = isEncode ? base64.encode(message) : base64.decode(message);
            }
            case "rot13" -> enDecodedString = new Rot13Cypher().encode(message);
            case "reverse" -> enDecodedString = new ReverseCypher().encode(message);
            case "hexadecimal" -> {
                HexCypher hexadecimal = new HexCypher();
                enDecodedString = isEncode ? hexadecimal.encode(message) : hexadecimal.decode(message);
            }
            default -> throw new IllegalStateException("Unexpected value: " + cypher);
        }

        event.replyEmbeds(
            blob.getStandardEmbed(String.format("%s - %s %s", getNameReadable(), WordUtils.capitalize(cypher), isEncode ? " Encode" : " Decode"))
                .addField("Original", String.format("```%s```", message), false)
                .addField(isEncode ? "Encoded Message" : "Decoded Message", String.format("```%s```", enDecodedString), false)
                .build()
        ).queue();
    }
}
