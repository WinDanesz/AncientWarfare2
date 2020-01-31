package net.shadowmage.ancientwarfare.structure.worldgen;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLong;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Territory implements INBTSerializable<NBTTagCompound> {
	private String territoryId;
	private String territoryName;
	private int totalClusterValue = 0;
	private Set<Long> chunkPositions = new HashSet<>();

	Territory() {
	}

	Territory(String territoryId, String territoryName) {
		this.territoryId = territoryId;
		this.territoryName = territoryName;
	}

	public String getTerritoryId() {
		return territoryId;
	}

	public int getTotalClusterValue() {
		return totalClusterValue;
	}

	public void addClusterValue(int value) {
		totalClusterValue += value;
	}

	public void addChunk(long chunkPos) {
		chunkPositions.add(chunkPos);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Territory territory = (Territory) o;
		return territoryId.equals(territory.territoryId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(territoryId);
	}

	public int getNumberOfChunks() {
		return chunkPositions.size();
	}

	public String getTerritoryName() {
		return territoryName;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound ret = new NBTTagCompound();
		ret.setString("territoryId", territoryId);
		ret.setString("territoryName", territoryName);
		ret.setInteger("totalClusterValue", totalClusterValue);
		ret.setTag("chunkPositions", NBTHelper.getTagList(chunkPositions, NBTTagLong::new));
		return ret;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		territoryId = nbt.getString("territoryId");
		territoryName = nbt.getString("territoryName");
		totalClusterValue = nbt.getInteger("totalClusterValue");
		chunkPositions = NBTHelper.getSet(nbt.getTagList("chunkPositions", Constants.NBT.TAG_LONG), n -> ((NBTTagLong) n).getLong());
	}

	public Set<Long> getChunkPositions() {
		return chunkPositions;
	}
}
