package net.tospay.auth.utils;

import android.content.Context;

import com.amulyakhare.textdrawable.TextDrawable;

import net.tospay.auth.R;

public class TextImages {
    public static TextDrawable textToImage(String name, Context context){
        String[] userInitials = name.split("");
        for (int i = 0; i < userInitials.length; i++) {
            if (userInitials[i].equals("") || userInitials[i].equals(" ")) {
                try{
                    userInitials[i] = userInitials[i + 1];
                    userInitials[i+1]= userInitials[i+2];
                }catch (Exception ex){
                    userInitials[i] = userInitials[i + 1];
                }
            }
        }
        name = userInitials[0].toUpperCase();
        return TextDrawable.builder().buildRound(name,context.getResources().getColor(R.color.shade));
    }
}
