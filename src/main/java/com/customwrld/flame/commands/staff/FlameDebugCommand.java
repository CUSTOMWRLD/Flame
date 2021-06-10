//package com.customwrld.flame.commands.staff;
//
//import com.customwrld.flame.util.Command;
//import dev.jorel.commandapi.CommandAPICommand;
//
//import java.util.List;
//
//public class FlameDebugCommand extends Command {
//
//    public FlameDebugCommand() {
//        this.command = "flamedebug";
//        this.permission = "flame.debug";
//    }
//
//    @Override
//    public CommandAPICommand command() {
//        return new CommandAPICommand(this.command)
//                .withPermission(this.permission)
//                .withSubcommand(new CommandAPICommand("commands")
//                        .withPermission(this.permission)
//                        .executes((sender, args) -> {
//                            String[] headers = { "Command", "Permission" };
//
//                            List<Command> commands = Initialize.registeredCommands;
//
//                            String[][] data = new String[commands.size()][];
//
//                            for(int i = 0; i < commands.size(); i++) {
//                                Command command = commands.get(i);
//                                data[i] = new String[]{command.command, command.permission};
//                            }
//
////                            try {
////                                sender.sendMessage(CC.GREEN + "[DEBUG] Commands: " + Flame.getFlame().getHastebin().post(FlipTable.of(headers, data), true));
////                            } catch (IOException e) {
////                                e.printStackTrace();
////                            }
//                        }));
//    }
//}
