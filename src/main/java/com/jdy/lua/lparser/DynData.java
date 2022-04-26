package com.jdy.lua.lparser;

import java.util.ArrayList;
import java.util.List;

public class DynData {

    /* list of all active local variables */
    List<Vardesc> arr = new ArrayList<>();
    int n;
    int size;

    LabelList gt;  /* list of pending gotos */
    LabelList label;   /* list of active labels */
}
