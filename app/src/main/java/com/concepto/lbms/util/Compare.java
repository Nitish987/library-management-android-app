package com.concepto.lbms.util;

import java.util.List;

public class Compare {

    public Compare(){}

    public static boolean asStringExistenceInList(String find, List<String> list) {
        for (String string: list) {
            if (string.equals(find)) {
                return true;
            }
        }
        return false;
    }
}
