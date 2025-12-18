package com.gmail.inayakitorikhurram.fdmc.mixininterfaces;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.TravelCriterion;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;

public interface DistancePredicateI {



    static DistancePredicate create(NumberRange.DoubleRange x, NumberRange.DoubleRange y, NumberRange.DoubleRange z, NumberRange.DoubleRange w, NumberRange.DoubleRange horizontal, NumberRange.DoubleRange absolute){
        DistancePredicate distancePredicate = new DistancePredicate(x,y,z,horizontal,absolute);
        ((DistancePredicateI)(Object)distancePredicate).setW(w);
        return distancePredicate;
    }

    static DistancePredicate w(NumberRange.DoubleRange w){
        DistancePredicate distancePredicate = DistancePredicate.absolute(NumberRange.DoubleRange.ANY);
        ((DistancePredicateI)(Object)distancePredicate).setW(w);
        return distancePredicate;
    }

    NumberRange.DoubleRange getW();

    void setW(NumberRange.DoubleRange w);

}
