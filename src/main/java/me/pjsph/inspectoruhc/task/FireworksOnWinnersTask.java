package me.pjsph.inspectoruhc.task;

import lombok.RequiredArgsConstructor;
import me.pjsph.inspectoruhc.game.IUPlayer;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

public class FireworksOnWinnersTask extends BukkitRunnable {

    private final int FIREWORK_DURATION = 10;

    private final Set<IUPlayer> winners;

    private Random random;
    private long startTime;

    public FireworksOnWinnersTask(Set<IUPlayer> winners) {
        this.winners = winners;
        this.random = new Random();
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        for(final IUPlayer winner : winners) {
            if(winner.isOnline()) {
                Location fireworkLocation = winner.getPlayer().getLocation();

                fireworkLocation.add(random.nextDouble() * 6 - 3, 2, random.nextDouble() * 6 - 3);

                generateRandomFirework(fireworkLocation.add(0.2, 0d, 0.2), 5, 15);
                generateRandomFirework(fireworkLocation.add(-0.2, 0d, 0.2), 5, 15);
            }
        }

        if((System.currentTimeMillis() - startTime) / 1000 > FIREWORK_DURATION)
            cancel();
    }

    public Firework generateRandomFirework(Location location, int minHeight, int maxHeight) {
        final Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        final FireworkMeta meta = firework.getFireworkMeta();

        IntStream.range(0, random.nextInt(3)+1).mapToObj(i -> generateRandomFireworkEffect()).forEach(meta::addEffect);

        meta.setPower((int) Math.min(Math.floor(minHeight / 5) + random.nextInt(maxHeight / 5), 128D));

        firework.setFireworkMeta(meta);

        return firework;
    }

    public FireworkEffect generateRandomFireworkEffect() {
        FireworkEffect.Builder fireworkBuilder = FireworkEffect.builder();

        fireworkBuilder.flicker(random.nextInt(3) == 1);
        fireworkBuilder.trail(random.nextInt(3) == 1);

        IntStream.range(0, random.nextInt(3)+1)
                .mapToObj(i -> Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .forEach(fireworkBuilder::withColor);
        IntStream.range(0, random.nextInt(3)+1)
                .mapToObj(i -> Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256)))
                .forEach(fireworkBuilder::withFade);

        FireworkEffect.Type[] types = FireworkEffect.Type.values();
        fireworkBuilder.with(types[random.nextInt(types.length)]);

        return fireworkBuilder.build();
    }
}
