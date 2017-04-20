package edu.szebo.ppke.survival.brain;

import edu.szebo.ppke.survival.proto.Communication;

public interface Brain {
    public Communication.Answer decideBasedOn(Communication.Message message);
}
