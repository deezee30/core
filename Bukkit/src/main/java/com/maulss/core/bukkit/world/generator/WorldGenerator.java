package com.maulss.core.bukkit.world.generator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WorldGenerator extends ChunkGenerator {

	private List<BlockPopulator> populators = new ArrayList<>();
	private World world;
	private Material[] layers;
	private Location spawnLocation;
	private Biome biome;

	/**
	 * Sets the settings used for this world while generating it. This generation code does not work with noise nor any other random terrain.
	 * However, you can use BlockPopulator to generate structures/trees/etc in this world.
	 *
	 * @param world         The world to generate.
	 * @param layers        The ordered list of layers this world will generate.
	 *                      EG: Material[] m = {Material.BEDROCK, Material.DIRT, Material.DIRT, Material.GRASS};
	 * @param spawnLocation The default spawn location for each player.
	 * @param biome         The type of biome this will be generated in. Available biomes: {@link Biome}
	 * @return WorldGenerator
	 */
	public WorldGenerator withSettings(World world, Material[] layers, Location spawnLocation, Biome biome) {
		this.world = world;
		this.layers = layers;
		this.spawnLocation = spawnLocation;
		this.biome = biome;

		return this;
	}

	/**
	 * Sets the settings used for this world while generating it. This generation code does not work with noise nor any other random terrain.
	 * However, you can use BlockPopulator to generate structures/trees/etc in this world.
	 *
	 * @param world         The world to generate.
	 * @param layers        The ordered list of layers this world will generate.
	 *                      EG: Material[] m = {Material.BEDROCK, Material.DIRT, Material.DIRT, Material.GRASS};
	 * @param spawnLocation The default spawn location for each player.
	 * @param biome         The type of biome this will be generated in. Available biomes: {@link Biome}
	 * @param populators    The populators you want this world to use. For example, ChunkPopulator.
	 * @return WorldGenerator
	 */
	public WorldGenerator withSettings(World world, Material[] layers, Location spawnLocation, Biome biome, BlockPopulator... populators) {
		this.world = world;
		this.layers = layers;
		this.spawnLocation = spawnLocation;
		this.biome = biome;
		this.populators = Arrays.asList(populators);

		return this;
	}


	@Override
	public byte[][] generateBlockSections(World world, Random random, int cx, int cz, BiomeGrid biomes) {
		byte[][] result = new byte[world.getMaxHeight() / 16][];

		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < layers.length; y++)
					setBlock(result, x, y, z, (byte) layers[y].getId());

				biomes.setBiome(x, z, biome);
			}

		return result;
	}

	/**
	 * Sets the current block at the result of the location x, y and z with the ID of the block.
	 *
	 * @param rslt The result of byte[][]. Honestly I bearly comprehend how this works.
	 * @param x    The x coordinate.
	 * @param y    The y coordinate.
	 * @param z    The z coordinate.
	 */
	private void setBlock(byte[][] rslt, int x, int y, int z, byte blkid) {
		if (rslt[y >> 4] == null)
			rslt[y >> 4] = new byte[4096];

		rslt[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
	}

	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		return new ArrayList<>();
	}

	@Override
	public Location getFixedSpawnLocation(World world, Random random) {
		Location l = spawnLocation == null ? new Location(world, 0D, 64D, 0D) : spawnLocation;
		World w = this.world == null ? world : this.world;
		return new Location(w, l.getX(), w.getHighestBlockYAt(l), l.getZ());
	}
}