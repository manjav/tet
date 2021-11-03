package com.farsitel.bazaar.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;

import com.farsitel.bazaar.game.callbacks.IConnectionCallback;
import com.farsitel.bazaar.game.callbacks.ITournamentMatchCallback;
import com.farsitel.bazaar.game.callbacks.ITournamentsCallback;
import com.farsitel.bazaar.game.utils.GHLogger;
import com.farsitel.bazaar.game.data.Result;
import com.farsitel.bazaar.game.data.Status;

public abstract class AbstractGameHub {

    static final int MINIMUM_BAZAAR_VERSION = 1400700;

    GHLogger logger;
    boolean isDispose = false;
    Result connectionState;

    public AbstractGameHub(GHLogger logger) {
        this.logger = logger;
    }

    public GHLogger getLogger() {
        return logger;
    }

    Result isCafebazaarInstalled(Context context, boolean showPrompts) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo("com.farsitel.bazaar", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (packageInfo == null) {
            if (showPrompts) {
            startActionViewIntent(context, "https://cafebazaar.ir/install", null);
            }
            return new Result(Status.INSTALL_CAFEBAZAAR, "Install cafebazaar to support GameHub!");
        }
        if (packageInfo.versionCode < MINIMUM_BAZAAR_VERSION) {
            if (showPrompts) {
            startActionViewIntent(context, "bazaar://details?id=com.farsitel.bazaar", "com.farsitel.bazaar");
        }
            return new Result(Status.UPDATE_CAFEBAZAAR, "Install new version of cafebazaar to support GameHub!");
        }
        return new Result(Status.SUCCESS, "");
    }

    abstract Result isLogin(Context context, boolean showPrompts);

    void startActionViewIntent(Context context, String uri, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));
        if (packageName != null) {
            intent.setPackage(packageName);
        }
        context.startActivity(intent);
    }

    abstract void connect(Context context, boolean showPrompts, IConnectionCallback callback);

    abstract void getTournaments(Activity activity, ITournamentsCallback callback);

    abstract void startTournamentMatch(Activity activity, ITournamentMatchCallback callback, String matchId, String metaData);

    abstract void endTournamentMatch(ITournamentMatchCallback callback, String sessionId, float score);

    abstract void showLastTournamentLeaderboard(Context context, IConnectionCallback callback);

    boolean disposed() {
        return isDispose;
    }

    void dispose() {
        connectionState.status = Status.DISCONNECTED;
        isDispose = true;
    }
}
