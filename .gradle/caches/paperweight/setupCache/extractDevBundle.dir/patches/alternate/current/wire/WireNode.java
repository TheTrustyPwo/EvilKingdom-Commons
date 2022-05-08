package alternate.current.wire;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;

/**
 * A WireNode is a Node that represents a wire in the world. It stores
 * all the information about the wire that the WireHandler needs to
 * calculate power changes.
 * 
 * @author Space Walker
 */
public class WireNode extends Node {

    final WireConnectionManager connections;

    /** The power level this wire currently holds in the world. */
    int currentPower;
    /**
     * While calculating power changes for a network, this field is used to keep
     * track of the power level this wire should have.
     */
    int virtualPower;
    /** The power level received from non-wire components. */
    int externalPower;
    /**
     * A 4-bit number that keeps track of the power flow of the wires that give this
     * wire its power level.
     */
    int flowIn;
    /** The direction of power flow, based on the incoming flow. */
    int iFlowDir;
    boolean added;
    boolean removed;
    boolean shouldBreak;
    boolean prepared;
    boolean inNetwork;

    /** The power for which this wire was queued. */
    int power;
    /** The previous wire in the power queue. */
    WireNode prev;
    /** The next wire in the power queue. */
    WireNode next;

    WireNode(ServerLevel level, BlockPos pos, BlockState state) {
        super(level);

        this.pos = pos.immutable();
        this.state = state;

        this.connections = new WireConnectionManager(this);

        this.virtualPower = this.currentPower = this.state.getValue(RedStoneWireBlock.POWER);
    }

    @Override
    public Node update(BlockPos pos, BlockState state, boolean clearNeighbors) {
        throw new UnsupportedOperationException("Cannot update a WireNode!");
    }

    @Override
    public boolean isWire() {
        return true;
    }

    @Override
    public WireNode asWire() {
        return this;
    }

    int nextPower() {
        return Mth.clamp(virtualPower, Redstone.SIGNAL_MIN, Redstone.SIGNAL_MAX);
    }

    boolean offerPower(int power, int iDir) {
        if (removed || shouldBreak) {
            return false;
        }
        if (power == virtualPower) {
            flowIn |= (1 << iDir);
            return false;
        }
        if (power > virtualPower) {
            virtualPower = power;
            flowIn = (1 << iDir);

            return true;
        }

        return false;
    }

    boolean setPower() {
        if (removed) {
            return true;
        }

        state = level.getBlockState(pos);

        if (shouldBreak) {
            Block.dropResources(state, level, pos);
            return level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }

        currentPower = LevelHelper.doRedstoneEvent(level, pos, currentPower, power);
        state = state.setValue(RedStoneWireBlock.POWER, currentPower);

        return LevelHelper.setWireState(level, pos, state, added);
    }
}
