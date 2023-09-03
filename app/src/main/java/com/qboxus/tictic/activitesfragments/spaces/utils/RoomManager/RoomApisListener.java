package com.qboxus.tictic.activitesfragments.spaces.utils.RoomManager;

import android.os.Bundle;

public interface RoomApisListener {
    void roomCreated(Bundle bundle);

    //invite members into room
    void roomInvitationsSended(Bundle bundle);

    //    invite members into room
    void goAheadForRoomGenrate(Bundle bundle);

    //    invite members into room
    void goAheadForRoomJoin(Bundle bundle);

    void onRoomJoined(Bundle bundle);

    void onRoomReJoin(Bundle bundle);

    void onRoomMemberUpdate(Bundle bundle);
    //    leave room
    void doRoomLeave(Bundle bundle);

    //    delete room
    void doRoomDelete(Bundle bundle);


    void onRoomLeave(Bundle bundle);

    void onRoomDelete(Bundle bundle);

    //    open room with detail
    void showRoomDetailAfterJoin(Bundle bundle);
}
