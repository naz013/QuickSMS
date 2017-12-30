package com.hexrain.design.quicksms.helpers;

import android.content.Context;

import com.hexrain.design.quicksms.R;


public class ColorSetter {

    private Context cContext;
    private SharedPrefs sPrefs;

    public ColorSetter(Context context){
        this.cContext = context;
    }

    public int getColor(int color){
        return cContext.getResources().getColor(color);
    }

    public int getFullscreenStyle(){
        int id;
        sPrefs = new SharedPrefs(cContext);
        if (sPrefs.loadBoolean(Constants.PREFERENCES_USE_DARK_THEME)){
            id = R.style.HomeDarkFullscreen;
        } else id = R.style.HomeWhiteFullscreen;
        return id;
    }

    public int colorSetter(){
        sPrefs = new SharedPrefs(cContext);
        String loadedColor = sPrefs.loadPrefs(Constants.PREFERENCES_THEME);
        int color;
        switch (loadedColor) {
            case "1":
                color = getColor(R.color.colorRed);
                break;
            case "2":
                color = getColor(R.color.colorViolet);
                break;
            case "3":
                color = getColor(R.color.colorLightCreen);
                break;
            case "4":
                color = getColor(R.color.colorGreen);
                break;
            case "5":
                color = getColor(R.color.colorLightBlue);
                break;
            case "6":
                color = getColor(R.color.colorBlue);
                break;
            case "7":
                color = getColor(R.color.colorYellow);
                break;
            case "8":
                color = getColor(R.color.colorOrange);
                break;
            case "9":
                color = getColor(R.color.colorGrey);
                break;
            case "10":
                color = getColor(R.color.colorPink);
                break;
            case "11":
                color = getColor(R.color.colorSand);
                break;
            case "12":
                color = getColor(R.color.colorBrown);
                break;
            case "13":
                color = getColor(R.color.colorDeepPurple);
                break;
            case "14":
                color = getColor(R.color.colorDeepOrange);
                break;
            case "15":
                color = getColor(R.color.colorLime);
                break;
            case "16":
                color = getColor(R.color.colorIndigo);
                break;
            default:
                color = getColor(R.color.colorGreen);
                break;
        }
        return color;
    }

    public int colorStatus(){
        sPrefs = new SharedPrefs(cContext);
        String loadedColor = sPrefs.loadPrefs(Constants.PREFERENCES_THEME);
        int color;
        switch (loadedColor) {
            case "1":
                color = getColor(R.color.colorRedDark);
                break;
            case "2":
                color = getColor(R.color.colorVioletDark);
                break;
            case "3":
                color = getColor(R.color.colorLightCreenDark);
                break;
            case "4":
                color = getColor(R.color.colorGreenDark);
                break;
            case "5":
                color = getColor(R.color.colorLightBlueDark);
                break;
            case "6":
                color = getColor(R.color.colorBlueDark);
                break;
            case "7":
                color = getColor(R.color.colorYellowDark);
                break;
            case "8":
                color = getColor(R.color.colorOrangeDark);
                break;
            case "9":
                color = getColor(R.color.colorGreyDark);
                break;
            case "10":
                color = getColor(R.color.colorPinkDark);
                break;
            case "11":
                color = getColor(R.color.colorSandDark);
                break;
            case "12":
                color = getColor(R.color.colorBrownDark);
                break;
            case "13":
                color = getColor(R.color.colorDeepPurpleDark);
                break;
            case "14":
                color = getColor(R.color.colorDeepOrangeDark);
                break;
            case "15":
                color = getColor(R.color.colorLimeDark);
                break;
            case "16":
                color = getColor(R.color.colorIndigoDark);
                break;
            default:
                color = getColor(R.color.colorBlueDark);
                break;
        }
        return color;
    }

    public int getStyle(){
        int id;
        sPrefs = new SharedPrefs(cContext);
        String loadedColor = sPrefs.loadPrefs(Constants.PREFERENCES_THEME);
        boolean isDark = sPrefs.loadBoolean(Constants.PREFERENCES_USE_DARK_THEME);
        if (isDark) {
            switch (loadedColor) {
                case "1":
                    id = R.style.HomeDark_Red;
                    break;
                case "2":
                    id = R.style.HomeDark_Violet;
                    break;
                case "3":
                    id = R.style.HomeDark_LightGreen;
                    break;
                case "4":
                    id = R.style.HomeDark_Green;
                    break;
                case "5":
                    id = R.style.HomeDark_LightBlue;
                    break;
                case "6":
                    id = R.style.HomeDark_Blue;
                    break;
                case "7":
                    id = R.style.HomeDark_Yellow;
                    break;
                case "8":
                    id = R.style.HomeDark_Orange;
                    break;
                case "9":
                    id = R.style.HomeDark_Grey;
                    break;
                case "10":
                    id = R.style.HomeDark_Pink;
                    break;
                case "11":
                    id = R.style.HomeDark_Sand;
                    break;
                case "12":
                    id = R.style.HomeDark_Brown;
                    break;
                case "13":
                    id = R.style.HomeDark_DeepPurple;
                    break;
                case "14":
                    id = R.style.HomeDark_DeepOrange;
                    break;
                case "15":
                    id = R.style.HomeDark_Lime;
                    break;
                case "16":
                    id = R.style.HomeDark_Indigo;
                    break;
                default:
                    id = R.style.HomeDark_Blue;
                    break;
            }
        } else {
            switch (loadedColor) {
                case "1":
                    id = R.style.HomeWhite_Red;
                    break;
                case "2":
                    id = R.style.HomeWhite_Violet;
                    break;
                case "3":
                    id = R.style.HomeWhite_LightGreen;
                    break;
                case "4":
                    id = R.style.HomeWhite_Green;
                    break;
                case "5":
                    id = R.style.HomeWhite_LightBlue;
                    break;
                case "6":
                    id = R.style.HomeWhite_Blue;
                    break;
                case "7":
                    id = R.style.HomeWhite_Yellow;
                    break;
                case "8":
                    id = R.style.HomeWhite_Orange;
                    break;
                case "9":
                    id = R.style.HomeWhite_Grey;
                    break;
                case "10":
                    id = R.style.HomeWhite_Pink;
                    break;
                case "11":
                    id = R.style.HomeWhite_Sand;
                    break;
                case "12":
                    id = R.style.HomeWhite_Brown;
                    break;
                case "13":
                    id = R.style.HomeWhite_DeepPurple;
                    break;
                case "14":
                    id = R.style.HomeWhite_DeepOrange;
                    break;
                case "15":
                    id = R.style.HomeWhite_Lime;
                    break;
                case "16":
                    id = R.style.HomeWhite_Indigo;
                    break;
                default:
                    id = R.style.HomeWhite_Blue;
                    break;
            }
        }
        return id;
    }

    public int getDialogStyle(){
        int id;
        sPrefs = new SharedPrefs(cContext);
        String loadedColor = sPrefs.loadPrefs(Constants.PREFERENCES_THEME);
        boolean isDark = sPrefs.loadBoolean(Constants.PREFERENCES_USE_DARK_THEME);
        if (isDark) {
            switch (loadedColor) {
                case "1":
                    id = R.style.HomeDarkDialog_Red;
                    break;
                case "2":
                    id = R.style.HomeDarkDialog_Violet;
                    break;
                case "3":
                    id = R.style.HomeDarkDialog_LightGreen;
                    break;
                case "4":
                    id = R.style.HomeDarkDialog_Green;
                    break;
                case "5":
                    id = R.style.HomeDarkDialog_LightBlue;
                    break;
                case "6":
                    id = R.style.HomeDarkDialog_Blue;
                    break;
                case "7":
                    id = R.style.HomeDarkDialog_Yellow;
                    break;
                case "8":
                    id = R.style.HomeDarkDialog_Orange;
                    break;
                case "9":
                    id = R.style.HomeDarkDialog_Grey;
                    break;
                case "10":
                    id = R.style.HomeDarkDialog_Pink;
                    break;
                case "11":
                    id = R.style.HomeDarkDialog_Sand;
                    break;
                case "12":
                    id = R.style.HomeDarkDialog_Brown;
                    break;
                case "13":
                    id = R.style.HomeDarkDialog_DeepPurple;
                    break;
                case "14":
                    id = R.style.HomeDarkDialog_DeepOrange;
                    break;
                case "15":
                    id = R.style.HomeDarkDialog_Lime;
                    break;
                case "16":
                    id = R.style.HomeDarkDialog_Indigo;
                    break;
                default:
                    id = R.style.HomeDarkDialog_Blue;
                    break;
            }
        } else {
            switch (loadedColor) {
                case "1":
                    id = R.style.HomeWhiteDialog_Red;
                    break;
                case "2":
                    id = R.style.HomeWhiteDialog_Violet;
                    break;
                case "3":
                    id = R.style.HomeWhiteDialog_LightGreen;
                    break;
                case "4":
                    id = R.style.HomeWhiteDialog_Green;
                    break;
                case "5":
                    id = R.style.HomeWhiteDialog_LightBlue;
                    break;
                case "6":
                    id = R.style.HomeWhiteDialog_Blue;
                    break;
                case "7":
                    id = R.style.HomeWhiteDialog_Yellow;
                    break;
                case "8":
                    id = R.style.HomeWhiteDialog_Orange;
                    break;
                case "9":
                    id = R.style.HomeWhiteDialog_Grey;
                    break;
                case "10":
                    id = R.style.HomeWhiteDialog_Pink;
                    break;
                case "11":
                    id = R.style.HomeWhiteDialog_Sand;
                    break;
                case "12":
                    id = R.style.HomeWhiteDialog_Brown;
                    break;
                case "13":
                    id = R.style.HomeWhiteDialog_DeepPurple;
                    break;
                case "14":
                    id = R.style.HomeWhiteDialog_DeepOrange;
                    break;
                case "15":
                    id = R.style.HomeWhiteDialog_Lime;
                    break;
                case "16":
                    id = R.style.HomeWhiteDialog_Indigo;
                    break;
                default:
                    id = R.style.HomeWhiteDialog_Blue;
                    break;
            }
        }
        return id;
    }

    public int getBackgroundStyle(){
        int id;
        sPrefs = new SharedPrefs(cContext);
        if (sPrefs.loadBoolean(Constants.PREFERENCES_USE_DARK_THEME)){
            id = getColor(R.color.grey_dark_x);
        } else id = getColor(R.color.grey_light_x);
        return id;
    }

    int getCardStyle(){
        int color;
        sPrefs = new SharedPrefs(cContext);
        if (sPrefs.loadBoolean(Constants.PREFERENCES_USE_DARK_THEME)){
            color = getColor(R.color.grey_x);
        } else color = getColor(R.color.colorWhite);
        return color;
    }
}
