package com.darksune.althera.common.item;

import com.darksune.althera.common.entity.AltheraEntities;
import com.darksune.althera.common.entity.TrainingDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.AABB;

public class TrainingDummyItem extends Item {

    public TrainingDummyItem(Properties properties) {
        super(properties.stacksTo(16));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());

        if (!context.getLevel().getBlockState(pos).canBeReplaced()
                || !context.getLevel().getBlockState(pos.above()).canBeReplaced()
                || !context.getLevel().getEntities(null, new AABB(pos).expandTowards(0, 1, 0)).isEmpty()) {
            return InteractionResult.FAIL;
        }

        if (context.getLevel() instanceof ServerLevel level) {

            TrainingDummyEntity dummy = AltheraEntities.TRAINING_DUMMY.get().create(level);

            if (dummy == null) {
                return InteractionResult.FAIL;
            }

            Player player = context.getPlayer();
            float yaw = player == null ? 0.0F : Mth.wrapDegrees(player.getYRot() + 180.0F);

            dummy.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, yaw, 0.0F);
            dummy.setYHeadRot(yaw);
            dummy.setYBodyRot(yaw);
            level.addFreshEntity(dummy);
            level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 0.9F);
            context.getItemInHand().consume(1, player);
        }

        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
}
