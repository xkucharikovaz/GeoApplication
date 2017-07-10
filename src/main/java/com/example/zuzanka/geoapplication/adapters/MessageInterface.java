package com.example.zuzanka.geoapplication.adapters;

import android.content.Context;

/**
 * Created by zuzanka on 16. 4. 2017.
 */

public interface MessageInterface {
    void setTitle(int titleId);
    void setTitle(String title);
    void setMessage(int messageId);
    void setMessage(String message);
    void setContext(Context context);
    void show();
}
