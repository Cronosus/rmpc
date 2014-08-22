package sk.yweb.gnox.bukkit.resmodprotect;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;

public class ResidenceProtectionField
{

	public Location getResidenceCenter(ClaimedResidence res)
	{
		CuboidArea area = res.getArea(res.getName());
		Location highLoc = area.getHighLoc();
		Location lowLoc = area.getLowLoc();

		double middleX = (highLoc.getX() + lowLoc.getX()) / 2;
		double middleY = (highLoc.getY() + lowLoc.getY()) / 2;
		double middleZ = (highLoc.getZ() + lowLoc.getZ()) / 2;

		return new Location(Bukkit.getWorld(res.getWorld()), middleX, middleY,
				middleZ);

	}

	public boolean watchProtectionField(ClaimedResidence res)
	{
		CuboidArea area = res.getArea(res.getName());
		int highX = (int) area.getHighLoc().getX();
		int lowX = (int) area.getLowLoc().getX();
		int highY = (int) area.getHighLoc().getY();
		int lowY = (int) area.getLowLoc().getY();
		int highZ = (int) area.getHighLoc().getZ();
		int lowZ = (int) area.getLowLoc().getZ();

		List<Block> firstXWall = new ArrayList();
		List<Block> secondXWall = new ArrayList();
		List<Block> firstYWall = new ArrayList();
		List<Block> secondYWall = new ArrayList();
		List<Block> firstZWall = new ArrayList();
		List<Block> secondZWall = new ArrayList();

		for(int y = lowY; y < highY + 1; y++)
		{
			for(int x = highX > lowX ? lowX : highX; x < (highX > lowX ? highX
					: lowX); x++)
			{
				firstXWall.add(Bukkit.getWorld(res.getWorld()).getBlockAt(x, y,
						highZ));
			}

			for(int x = highX > lowX ? lowX : highX; x < (highX > lowX ? highX
					: lowX); x++)
			{
				secondXWall.add(Bukkit.getWorld(res.getWorld()).getBlockAt(x,
						y, highZ + area.getZSize()));
			}

			for(int z = highZ > lowZ ? lowZ : highZ; z < (highZ > lowZ ? highZ
					: lowZ); z++)
			{
				firstZWall.add(Bukkit.getWorld(res.getWorld()).getBlockAt(
						highX, y, z));
			}

			for(int z = highZ > lowZ ? lowZ : highZ; z < (highZ > lowZ ? highZ
					: lowZ); z++)
			{
				secondZWall.add(Bukkit.getWorld(res.getWorld()).getBlockAt(
						highX + area.getXSize(), y, z));
			}
		}

		for(int x = highX > lowX ? lowX : highX; x < (highX > lowX ? highX
				: lowX); x++)
		{
			for(int z = highZ > lowZ ? lowZ : highZ; z < (highZ > lowZ ? highZ
					: lowZ); z++)
			{
				firstYWall.add(Bukkit.getWorld(res.getWorld()).getBlockAt(x,
						lowY, z));
			}
			for(int z = highZ > lowZ ? lowZ : highZ; z < (highZ > lowZ ? highZ
					: lowZ); z++)
			{
				secondYWall.add(Bukkit.getWorld(res.getWorld()).getBlockAt(x,
						highY, z));
			}
		}

		List<List> hollowCube = new ArrayList();
		hollowCube.add(firstXWall);
		hollowCube.add(secondXWall);
		hollowCube.add(firstYWall);
		hollowCube.add(secondYWall);
		hollowCube.add(firstZWall);
		hollowCube.add(secondZWall);

		return true;
	}

}
