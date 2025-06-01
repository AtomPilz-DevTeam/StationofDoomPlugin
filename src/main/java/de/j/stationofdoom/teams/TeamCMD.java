    @Override
    public void execute(CommandSourceStack stack, String[] args) {
+       if (!(stack.getSender() instanceof Player player)) {
+           return;
+       }
-       Player player = (Player) stack.getSender();
        Team team = HandleTeams.getTeamFromPlayerUUID(player.getUniqueId());
        if (!team.isMember(player.getUniqueId())) {
            player.sendMessage(Component.text(new TranslationFactory().getTranslation(player, "TeamCMDNoTeam"))
                                         .color(NamedTextColor.RED));
            return;
        }
        new TeamSettingsGUI(team).showPage(1, player);
    }
