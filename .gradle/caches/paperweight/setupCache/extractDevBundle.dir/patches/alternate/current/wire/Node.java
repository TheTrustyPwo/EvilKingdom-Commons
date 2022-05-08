package alternate.current.wire;

import java.util.Arrays;

import alternate.current.wire.WireHandler.Directions;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A Node represents a block in the world. It also holds a few other pieces of
 * information that speed up the calculations in the WireHandler class.
 * 
 * @author Space Walker
 */
public class Node {

    // flags that encode the Node type
    private static final int CONDUCTOR = 0b01;
    private static final int SOURCE    = 0b10;

    final ServerLevel level;
    final Node[] neighbors;

    BlockPos pos;
    BlockState state;
    boolean invalid;

    private int flags;

    Node(ServerLevel level) {
        this.level = level;
        this.neighbors = new Node[Directions.ALL.length];
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Node)) {
            return false;
        }

        Node node = (Node)obj;

        return level == node.level && pos.equals(node.pos);
    }

    @Override
    public int hashCode() {
        return pos.hashCode();
    }

    Node update(BlockPos pos, BlockState state, boolean clearNeighbors) {
        if (state.is(Blocks.REDSTONE_WIRE)) {
            throw new IllegalStateException("Cannot update a regular Node to a WireNode!");
        }

        if (clearNeighbors) {
            Arrays.fill(neighbors, null);
        }

        this.pos = pos.immutable();
        this.state = state;
        this.invalid = false;

        this.flags = 0;

        if (this.state.isRedstoneConductor(this.level, this.pos)) {
            this.flags |= CONDUCTOR;
        }
        if (this.state.isSignalSource()) {
            this.flags |= SOURCE;
        }

        return this;
    }

    public boolean isWire() {
        return false;
    }

    public boolean isConductor() {
        return (flags & CONDUCTOR) != 0;
    }

    public boolean isSignalSource() {
        return (flags & SOURCE) != 0;
    }

    public WireNode asWire() {
        throw new UnsupportedOperationException("Not a WireNode!");
    }
}
