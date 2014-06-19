package net.shadowmage.ancientwarfare.core.crafting;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

public class RecipeResearched extends ShapedRecipes
{

private Set<Integer> neededResearch = new HashSet<Integer>();

public RecipeResearched(int par1, int par2, ItemStack[] par3ArrayOfItemStack, ItemStack par4ItemStack)
  {
  super(par1, par2, par3ArrayOfItemStack, par4ItemStack);
  }

protected final RecipeResearched addResearch(String... names)
  {
  ResearchGoal g;  
  for(String name : names)
    {
    name = name.startsWith("research.") ? name : "research."+name;
    g = ResearchGoal.getGoal(name);
    if(g!=null)
      {
      neededResearch.add(g.getId());
      }
    else
      {
      throw new IllegalArgumentException("COULD NOT LOCATE RESEARCH GOAL FOR NAME: "+name);
      }
    }
  return this;
  }

protected final RecipeResearched addResearch(int... nums)
  {
  ResearchGoal g;
  for(int k : nums)
    {
    g = ResearchGoal.getGoal(k);
    if(g!=null)
      {
      neededResearch.add(k);
      }
    }
  return this;
  }

public final boolean canPlayerCraft(World world, String playerName)
  {
  boolean canCraft = true;
  for(Integer i : this.neededResearch)
    {
    if(!ResearchTracker.instance().hasPlayerCompleted(world, playerName, i))
      {
      canCraft = false;
      break;
      }
    }
  return canCraft;
  }

public Set<Integer> getNeededResearch(){return neededResearch;}

}
