/*
 * Part of core.
 * Made on 30/07/2017
 */

package com.maulss.core.text;

import java.io.Serializable;

public interface Scroller extends Serializable {

    String next();

    String getOriginal();
}