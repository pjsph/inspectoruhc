package me.pjsph.inspectoruhc.scenarios;

import lombok.RequiredArgsConstructor;
import lombok.Getter;

public abstract class Scenario {

    protected Scenarios scenario;

    public void activeScenario() {
        this.configure();
        this.scenario.toggle();
        if(this.scenario.isEnabled())
            this.activate();
        else
            this.disable();
    }

    public abstract void configure();

    public abstract void activate();

    private void disable() {
        this.configure();
    }

    @RequiredArgsConstructor
    public enum Scenarios {

        CUTCLEAN("CutClean", "Plus besoin de four.", CutClean.class),
        TRIPLE_ORES("TripleOres", "Les minerais droppent 3x plus.", TripleOres.class),
        TIMBER("Timber", "Casser une bûche d'un arbre cassera tout l'arbre.", Timber.class),
        BLOODDIAMONDS("BloodDiamonds", "Miner un diamant contre un demi-coeur.", BloodDiamond.class),
        TIMEBOMB("TimeBomb", "Après la mort d'un joueur, son stuff sera stocké dans un coffre qui explosera après 30 secondes.", TimeBomb.class);

        @Getter private final String name;
        @Getter private final String desc;
        @Getter private final Class<? extends Scenario> clazz;
        @Getter private boolean enabled;

        public void toggle() {
            this.enabled = !this.enabled;
        }

    }

}
