package com.turn.ttorrent.client.network;

import com.turn.ttorrent.client.peer.SharingPeer;
import com.turn.ttorrent.common.Peer;
import com.turn.ttorrent.common.PeersStorage;
import com.turn.ttorrent.common.PeersStorageProvider;

import java.io.IOException;
import java.nio.channels.ByteChannel;

public class ShutdownProcessor implements DataProcessor {

  private final String uid;
  private final PeersStorageProvider peersStorageProvider;

  public ShutdownProcessor(String uid, PeersStorageProvider peersStorageProvider) {
    this.uid = uid;
    this.peersStorageProvider = peersStorageProvider;
  }

  @Override
  public DataProcessor processAndGetNext(ByteChannel socketChannel) throws IOException {
    if (socketChannel.isOpen()) {
      try {
        socketChannel.close();
      } catch (IOException e) {
        //already closed?
      }
      removePeers();
    }
    return this;
  }

  private void removePeers() {
    PeersStorage peersStorage = peersStorageProvider.getPeersStorage();
    Peer peer = peersStorage.removePeer(uid);
    if (peer == null) {
      return;
    }
    SharingPeer sharingPeer = peersStorage.removeSharingPeer(peer);
    if (sharingPeer == null) {
      return;
    }
    sharingPeer.unbind(true);
  }
}
