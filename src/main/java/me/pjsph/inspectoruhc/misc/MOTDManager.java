package me.pjsph.inspectoruhc.misc;

import lombok.*;
import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.teams.Team;

@RequiredArgsConstructor
public class MOTDManager {

    private final InspectorUHC plugin;

    @Getter private String currentMOTD;


    public void updateMOTDBeforeStart() {
        currentMOTD = "§cEn attente de joueurs...";
    }

    public void updateMOTDDuringStart() {
        currentMOTD = "§cEn cours de démarrage...";
    }

    public void updateMOTDDuringGame() {
        currentMOTD = "§cEn cours ! §7"+plugin.getGameManager().getAlivePlayers().size()+" joueur(s) en vie et "+ Team.values().length+" équipes.";
    }

    public void updateMOTDAfterGame(Team winner) {
        currentMOTD = "§cPartie terminée§7, l'équipe des "+winner.getColor()+winner.getName()+" §7gagne !\nLe serveur va redémarrer.";
    }

}
