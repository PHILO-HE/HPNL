package com.intel.hpnl.pingpong;

import java.nio.ByteBuffer;

import com.intel.hpnl.core.Handler;
import com.intel.hpnl.core.Buffer;
import com.intel.hpnl.core.Connection;
import com.intel.hpnl.core.EqService;

public class ReadCallback implements Handler {
  public ReadCallback(boolean is_server, EqService eqService) {
    this.is_server = is_server;
    this.eqService = eqService;
    byteBufferTmp = ByteBuffer.allocate(4096);
    byteBufferTmp.putChar('a');
    byteBufferTmp.flip();
  }
  public synchronized void handle(Connection con, int rdmaBufferId, int blockBufferSize) {
    if (!is_server) {
      if (count == 0) {
        startTime = System.currentTimeMillis();
      }
      if (++count >= 1000000) {
        endTime = System.currentTimeMillis();
        totally_time = (float)(endTime-startTime)/1000;
        System.out.println("finished, total time is " + totally_time + " s");
        eqService.shutdown();
        return;
      }
    }
    Buffer sendBuffer = con.getSendBuffer();
    Buffer recvBuffer = con.getRecvBuffer(rdmaBufferId);

    ByteBuffer recvByteBuffer = recvBuffer.get(blockBufferSize);

    sendBuffer.put(recvByteBuffer, 1, 10);
    con.send(sendBuffer.getByteBuffer().remaining(), sendBuffer.getRdmaBufferId());
  }
  private int count = 0;
  private long startTime;
  private long endTime;
  private float totally_time = 0;
  private ByteBuffer byteBufferTmp;

  boolean is_server = false;
  private EqService eqService;
}
