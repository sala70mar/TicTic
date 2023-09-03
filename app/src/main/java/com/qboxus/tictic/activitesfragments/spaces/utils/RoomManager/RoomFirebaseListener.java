package com.qboxus.tictic.activitesfragments.spaces.utils.RoomManager;

import android.os.Bundle;

public interface RoomFirebaseListener {
    void createRoom(Bundle bundle);
    void JoinedRoom(Bundle bundle);
    void onRoomLeave(Bundle bundle);
    void onRoomDelete(Bundle bundle);
    void onRoomUpdate(Bundle bundle);
    void onRoomUsersUpdate(Bundle bundle);
    void onMyUserUpdate(Bundle bundle);
    void onSpeakInvitationReceived(Bundle bundle);
    void onWaveUserUpdate(Bundle bundle);
}
