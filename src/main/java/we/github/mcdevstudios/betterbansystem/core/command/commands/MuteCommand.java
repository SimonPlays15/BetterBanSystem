/*
 * Copyright (c) MCDevStudios 2024. All Rights Reserved
 */

package we.github.mcdevstudios.betterbansystem.core.command.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import we.github.mcdevstudios.betterbansystem.api.exceptions.CommandException;
import we.github.mcdevstudios.betterbansystem.api.uuid.UUIDFetcher;
import we.github.mcdevstudios.betterbansystem.core.BetterBanSystem;
import we.github.mcdevstudios.betterbansystem.core.command.BaseCommand;
import we.github.mcdevstudios.betterbansystem.core.mute.MuteHandler;
import we.github.mcdevstudios.betterbansystem.core.player.BaseCommandSender;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MuteCommand extends BaseCommand {

    public MuteCommand() {
        super("mute");
    }

    private @Nullable Date futureDate(Date now, String timeString) {

        Pattern pattern = Pattern.compile("(\\d+y)?(\\d+m)?(\\d+d)?(\\d+h)?(\\d+min)?(\\d+s)?");
        Matcher matcher = pattern.matcher(timeString);
        if (matcher.matches()) {
            String yearMatch = matcher.group(1);
            String monthMatch = matcher.group(2);
            String dayMatch = matcher.group(3);
            String hourMatch = matcher.group(4);
            String minuteMatch = matcher.group(5);
            String secondMatch = matcher.group(6);

            int years = yearMatch != null ? Integer.parseInt(yearMatch.substring(0, yearMatch.length() - 1)) : 0;
            int months = monthMatch != null ? Integer.parseInt(monthMatch.substring(0, monthMatch.length() - 1)) : 0;
            int days = dayMatch != null ? Integer.parseInt(dayMatch.substring(0, dayMatch.length() - 1)) : 0;
            int hours = hourMatch != null ? Integer.parseInt(hourMatch.substring(0, hourMatch.length() - 1)) : 0;
            int minutes = minuteMatch != null ? Integer.parseInt(minuteMatch.substring(0, minuteMatch.length() - 3)) : 0;
            int seconds = secondMatch != null ? Integer.parseInt(secondMatch.substring(0, secondMatch.length() - 1)) : 0;

            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.YEAR, years);
            cal.add(Calendar.MONTH, months);
            cal.add(Calendar.DAY_OF_MONTH, days);
            cal.add(Calendar.HOUR_OF_DAY, hours);
            cal.add(Calendar.MINUTE, minutes);
            cal.add(Calendar.SECOND, seconds);

            return cal.getTime();
        }

        return null;
    }

    @Override
    public boolean runCommand(BaseCommandSender sender, String @NotNull [] args) throws CommandException {
        if (args.length < 2) {
            return false;
        }

        String target = args[0];
        if (MuteHandler.findMuteEntry(target) != null) {
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("mute.alreadyMuted", Map.of("target", target)));
            return true;
        }

        String reason = args.length < 3 ? BetterBanSystem.getInstance().getLanguageFile().getMessage("mute.reason") : Arrays.stream(args).skip(2).collect(Collectors.joining(" "));

        if (sender.isPlayer() && (this.getPermManager().hasPermission(target, "betterbansystem.exempt.mute") || BetterBanSystem.getInstance().getConfig().getStringList("exempted-mute-players").contains(target))) {
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("defaults.exemptMessage", Map.of("targetType", "player", "target", target, "type", "mute")));
            return true;
        }

        String time = args[1];
        Date parsed = this.futureDate(new Date(), time);
        if (parsed == null) {
            sender.sendMessage("§cWrong time pattern. Example Timepattern: 1h3min (for 1 Hour and 3 minutes)");
            return true;
        }

        Object targetPlayer = BetterBanSystem.getPlayer(target);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        if (targetPlayer != null) {
            BetterBanSystem.sendMessage(targetPlayer, BetterBanSystem.getInstance().getLanguageFile().getMessage("mute.playerMessage", Map.of("date", format.format(parsed))));
        }

        Object offlinePlayer = BetterBanSystem.getOfflinePlayer(UUIDFetcher.getUUIDOrOfflineUUID(target));
        if (offlinePlayer != null && !BetterBanSystem.hasPlayedBefore(offlinePlayer)) {
            sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("defaults.warning", Map.of("target", target)));
        }

        MuteHandler.addMute(sender, target, reason, parsed);
        sender.sendMessage(BetterBanSystem.getInstance().getLanguageFile().getMessage("mute.success", Map.of("target", target, "date", format.format(parsed))));

        return true;
    }
}
