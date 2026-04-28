package com.darksune.althera.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public class MultiblockValidator {

    // Camada Y = 0 (nível do core)
    private static final String[][] LAYER_0 = {
            {" ", " ", "G", " ", " "},
            {" ", " ", " ", " ", " "},
            {"G", " ", "C", " ", "G"},
            {" ", " ", " ", " ", " "},
            {" ", " ", "G", " ", " "}
    };

    // Camada Y = +1 (netherrack)
    private static final String[][] LAYER_1 = {
            {" ", " ", "N", " ", " "},
            {" ", " ", " ", " ", " "},
            {"N", " ", " ", " ", "N"},
            {" ", " ", " ", " ", " "},
            {" ", " ", "N", " ", " "}
    };

    // Camada Y = -1 (diamante embaixo do core)
    private static final String[][] LAYER_NEG1 = {
            {".", ".", ".", ".", "."},
            {".", ".", ".", ".", "."},
            {".", ".", "D", ".", "."},
            {".", ".", ".", ".", "."},
            {".", ".", ".", ".", "."}
    };

    // Mapeamento de símbolos
    private static final Map<String, Block> KEY = Map.of(
            "G", Blocks.GOLD_BLOCK,
            "N", Blocks.NETHERRACK,
            "D", Blocks.DIAMOND_BLOCK
    );

    public static boolean isValid(Level level, BlockPos center) {
        return checkLayer(level, center, LAYER_0, 0)
                && checkLayer(level, center, LAYER_1, 1)
                && checkLayer(level, center, LAYER_NEG1, -1);
    }

    private static boolean checkLayer(Level level, BlockPos center, String[][] layer, int yOffset) {

        int size = layer.length;
        int half = size / 2;

        for (int z = 0; z < size; z++) {
            for (int x = 0; x < size; x++) {

                String symbol = layer[z][x];
                BlockPos pos = center.offset(x - half, yOffset, z - half);

                // ignora
                if (symbol.equals(".")) continue;

                // vazio obrigatório
                if (symbol.equals(" ")) {
                    if (!level.isEmptyBlock(pos)) {
                        return false;
                    }
                    continue;
                }

                // core
                if (symbol.equals("C")) continue;

                Block expected = KEY.get(symbol);

                if (expected == null) return false;

                if (!level.getBlockState(pos).is(expected)) {
                    return false;
                }
            }
        }

        return true;
    }
}