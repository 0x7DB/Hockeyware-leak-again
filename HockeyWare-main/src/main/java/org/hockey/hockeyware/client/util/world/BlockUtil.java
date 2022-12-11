package org.hockey.hockeyware.client.util.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.hockey.hockeyware.client.util.Globals;
import org.hockey.hockeyware.client.util.player.PlayerUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockUtil implements Globals {

    public static final List<Block> resistantBlocks = Arrays.asList(
            Blocks.OBSIDIAN,
            Blocks.ANVIL,
            Blocks.ENCHANTING_TABLE,
            Blocks.ENDER_CHEST,
            Blocks.BEACON
    );

    public static final List<Block> unbreakableBlocks = Arrays.asList(
            Blocks.BEDROCK,
            Blocks.COMMAND_BLOCK,
            Blocks.CHAIN_COMMAND_BLOCK,
            Blocks.END_PORTAL_FRAME,
            Blocks.BARRIER,
            Blocks.PORTAL
    );

    public static BlockPos extrudeBlock(BlockPos pos, EnumFacing direction) {

        switch (direction) {
            case WEST: return new BlockPos(pos.x - 1.0, pos.y, pos.z);

            case EAST: return new BlockPos(pos.x + 1.0, pos.y, pos.z);

            case NORTH: return new BlockPos(pos.x, pos.y, pos.z - 1.0);

            case SOUTH: return new BlockPos(pos.x, pos.y, pos.z + 1.0);

            case UP: return new BlockPos(pos.x, pos.y + 1.0, pos.z);

            case DOWN: return new BlockPos(pos.x, pos.y - 1.0, pos.z);
        }

        return pos;
    }


    public static Block getBlock(BlockPos pos) {
        IBlockState ibs = mc.world.getBlockState(pos);
        Block block = ibs.getBlock();
        return block;
    }

    public static boolean isNotVisible(BlockPos position, double offset) {
        if (offset > 50 || offset < -50) {
            return false;
        }

        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(position.getX() + 0.5, position.getY() + offset, position.getZ() + 0.5), false, true, false) != null;
    }

    public static List<BlockPos> getBlocksInArea(EntityPlayer player, AxisAlignedBB area) {
        if (player != null) {

            List<BlockPos> blocks = new ArrayList<>();

            for (double x = StrictMath.floor(area.minX); x <= StrictMath.ceil(area.maxX); x++) {
                for (double y = StrictMath.floor(area.minY); y <= StrictMath.ceil(area.maxY); y++) {
                    for (double z = StrictMath.floor(area.minZ); z <= StrictMath.ceil(area.maxZ); z++) {

                        BlockPos position = player.getPosition().add(x, y, z);

                        if (PlayerUtil.getDistanceToCenter(player, position) >= area.maxX) {
                            continue;
                        }

                        blocks.add(position);
                    }
                }
            }

            return blocks;
        }

        return new ArrayList<>();
    }

    public static boolean canPlaceBlock(BlockPos pos) {
        BlockPos bottom = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
        BlockPos top = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
        BlockPos front = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
        BlockPos back = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
        BlockPos right = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
        BlockPos left = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
        return (!getBlock(bottom).equals(Blocks.AIR) || !getBlock(top).equals(Blocks.AIR) || !getBlock(front).equals(Blocks.AIR) || !getBlock(back).equals(Blocks.AIR) || !getBlock(right).equals(Blocks.AIR) || !getBlock(left).equals(Blocks.AIR));
    }

    public static Color getRenderColor(BlockPos pos) {
        return BlockUtil.getBlock(pos).equals(Blocks.AIR) ? new Color(210, 24, 33, 100) : new Color(39, 252, 50, 100);
    }

    public static boolean canBreak(final BlockPos pos) {
        final IBlockState blockState = mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, mc.world, pos) != -1.0f;
    }

    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet) {
        EnumFacing side = getFirstFacing(pos);

        if (side == null) return false;

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));

        if (rotate) {
            faceVector(hitVec, true);
        }

        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4; //?
        return true;
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float) (vec.x - (double) pos.getX());
            float f1 = (float) (vec.y - (double) pos.getY());
            float f2 = (float) (vec.z - (double) pos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
        }
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        for (EnumFacing facing : getPossibleSides(pos)) {
            return facing;
        }
        return null;
    }

    public static void faceVector(Vec3d vec, boolean normalizeAngle) {
        float[] rotations = getLegitRotations(vec);
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? MathHelper.normalizeAngle((int) rotations[1], 360) : rotations[1], mc.player.onGround));
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{
                mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
                mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)
        };
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        List<EnumFacing> facings = new ArrayList<>();
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            if (mc.world.getBlockState(neighbour) == null) return facings;
            if (mc.world.getBlockState(neighbour).getBlock() == null) return facings;
            if (mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
                IBlockState blockState = mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable()) {
                    facings.add(side);
                }
            }
        }
        return facings;
    }

    public static BlockUtil.BlockResistance getBlockResistance(BlockPos block) {
        return BlockUtil.mc.world.isAirBlock(block) ? BlockUtil.BlockResistance.BLANK : (BlockUtil.mc.world.getBlockState(block).getBlock().getBlockHardness(BlockUtil.mc.world.getBlockState(block), BlockUtil.mc.world, block) != -1.0F && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.OBSIDIAN) && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.ANVIL) && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.ENCHANTING_TABLE) && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.ENDER_CHEST) ? BlockUtil.BlockResistance.BREAKABLE : (!BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.OBSIDIAN) && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.ANVIL) && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.ENCHANTING_TABLE) && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.ENDER_CHEST) ? (!BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.BEDROCK) && !BlockUtil.mc.world.getBlockState(block).getBlock().equals(Blocks.BARRIER) ? null : BlockUtil.BlockResistance.UNBREAKABLE) : BlockUtil.BlockResistance.RESISTANT));
    }

    public enum BlockResistance {
        BLANK, BREAKABLE, RESISTANT, UNBREAKABLE
    }


    /**
     * Finds the if a given position is breakable
     * @param position The position to check
     * @return Whether or not the given position is breakable
     */
    public static boolean isBreakable(BlockPos position) {
        return !getResistance(position).equals(Resistance.UNBREAKABLE);
    }

    /**
     * Checks if a block is replaceable
     * @param pos the position to check
     * @return if this block pos can be placed at
     */
    public static boolean isReplaceable(BlockPos pos) {
        return mc.world.getBlockState(pos).getMaterial().isReplaceable();
    }

    /**
     * Finds the resistance of a given position
     * @param position The position to find the resistance for
     * @return The {@link Resistance} resistance of the given position
     */
    public static Resistance getResistance(BlockPos position) {

        // the block at the given position
        Block block = mc.world.getBlockState(position).getBlock();

        // idk why this would be null but it throws errors
        if (block != null) {

            // find resistance
            if (resistantBlocks.contains(block)) {
                return Resistance.RESISTANT;
            }

            else if (unbreakableBlocks.contains(block)) {
                return Resistance.UNBREAKABLE;
            }

            else if (block.getDefaultState().getMaterial().isReplaceable()) {
                return Resistance.REPLACEABLE;
            }

            else {
                return Resistance.BREAKABLE;
            }
        }

        return Resistance.NONE;
    }

    /**
     * Gets the distance to the center of the block
     * @param in The block to get the distance to
     * @return The distance to the center of the block
     */
    public static double getDistanceToCenter(EntityPlayer player, BlockPos in) {

        // distances
        double dX = in.getX() + 0.5 - player.posX;
        double dY = in.getY() + 0.5 - player.posY;
        double dZ = in.getZ() + 0.5 - player.posZ;

        // distance to center
        return StrictMath.sqrt((dX * dX) + (dY * dY) + (dZ * dZ));
    }

    // the resistance level of the block
    public enum Resistance {

        /**
         * Blocks that are able to be replaced by other blocks
         */
        REPLACEABLE,

        /**
         * Blocks that are able to be broken with tools in survival mode
         */
        BREAKABLE,

        /**
         * Blocks that are resistant to explosions
         */
        RESISTANT,

        /**
         * Blocks that are unbreakable with tools in survival mode
         */
        UNBREAKABLE,

        /**
         * Null equivalent
         */
        NONE
    }
}
