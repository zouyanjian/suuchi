package in.ashwanthkumar.suuchi.router

import in.ashwanthkumar.suuchi.membership.MemberAddress
import io.grpc.Metadata.AsciiMarshaller

/**
 * Send a string value using AsciiMarshaller
 */
case object StringMarshaller extends AsciiMarshaller[String] {
  override def toAsciiString(value: String): String = value
  override def parseAsciiString(serialized: String): String = serialized
}

/**
 * Converts a collection of [[MemberAddress]] to it's external form separated by `|`
 */
case object ListOfNodesMarshaller extends AsciiMarshaller[List[MemberAddress]] {
  override def parseAsciiString(serialized: String): List[MemberAddress] = serialized.split('|').map(MemberAddress.apply).toList
  override def toAsciiString(value: List[MemberAddress]): String = value.map(_.toExternalForm).mkString("|")
}
