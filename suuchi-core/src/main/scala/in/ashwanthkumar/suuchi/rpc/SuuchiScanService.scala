package in.ashwanthkumar.suuchi.rpc

import com.google.protobuf.ByteString
import in.ashwanthkumar.suuchi.partitioner.SuuchiHash
import in.ashwanthkumar.suuchi.rpc.generated.SuuchiRPC.{ScanRequest, ScanResponse}
import in.ashwanthkumar.suuchi.rpc.generated.{SuuchiRPC, SuuchiScanGrpc}
import in.ashwanthkumar.suuchi.store.{KV, Store}
import in.ashwanthkumar.suuchi.utils.ByteArrayUtils
import io.grpc.stub.{ServerCallStreamObserver, StreamObserver}

class SuuchiScanService(store: Store) extends SuuchiScanGrpc.SuuchiScanImplBase {

  private def buildKV(kv: KV) = {
    SuuchiRPC.KV.newBuilder()
      .setKey(ByteString.copyFrom(kv.key))
      .setValue(ByteString.copyFrom(kv.value))
      .build()
  }

  private def buildResponse(response: KV): ScanResponse = {
    SuuchiRPC.ScanResponse.newBuilder()
      .setIterator(buildKV(response))
      .build()
  }

  override def scan(request: ScanRequest, responseObserver: StreamObserver[ScanResponse]): Unit = {
    val observer = responseObserver.asInstanceOf[ServerCallStreamObserver[ScanResponse]]
    val start = request.getStart
    val end = request.getEnd

    val iterator = store.scan()
    for(response <- iterator) {
//      ConnectionUtils.waitForReady(observer) // block until the channel is ready to send messages
      if (ByteArrayUtils.isHashKeyWithinRange(start, end, response.key, SuuchiHash))
        observer.onNext(buildResponse(response))
    }
    observer.onCompleted()
  }
}
