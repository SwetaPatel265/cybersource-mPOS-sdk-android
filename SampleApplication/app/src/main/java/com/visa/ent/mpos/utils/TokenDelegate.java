package com.visa.ent.mpos.utils;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 8/24/2016.
 */
public interface TokenDelegate {
    public abstract void tokenGenerationFinished(String token);
}
