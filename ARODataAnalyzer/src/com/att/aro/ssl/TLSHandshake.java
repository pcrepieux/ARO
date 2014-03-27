/*
 *  Copyright 2012 AT&T
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.att.aro.ssl;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.att.aro.util.Util;

public class TLSHandshake {
	private static final Logger LOGGER = Logger.getLogger(TLS_SESSION_INFO.class.getName());
	private static final int MAX_ENC_PREMASTER_LEN = 2048;
	public static final int TLS_HANDSHAKE_CLIENT_HELLO = 1;
	public static final int TLS_HANDSHAKE_SERVER_HELLO = 2;
	public static final int TLS_HANDSHAKE_NEW_SESSION_TICKET = 4;
	public static final int TLS_HANDSHAKE_CERTIFICATE = 11;
	public static final int TLS_HANDSHAKE_SERVER_KEY_EXCHANGE = 12;
	public static final int TLS_HANDSHAKE_CERTIFICATE_REQUEST = 13;
	public static final int TLS_HANDSHAKE_SERVER_HELLO_DONE = 14;
	public static final int TLS_HANDSHAKE_CERTIFICATE_VERIFY = 15;
	public static final int TLS_HANDSHAKE_CLIENT_KEY_EXCHANGE = 16;
	public static final int TLS_HANDSHAKE_FINISHED = 20;
	public static final int TLS_HANDSHAKE_NEXT_PROTOCOL = 67;
	public static final int TLS_EXTENSION_SESSION_TICKET = 35;
	public static final int APP_PROTOCOL_SPDY_3	= 1;
	public static final int APP_PROTOCOL_SPDY_2	= 2;
	public static final int APP_PROTOCOL_HTTP_1_1 = 3;	
	private int type;
	
	//TLS_HANDSHAKE_CLIENT_HELLO
	private byte[] clientRandom = new byte[32];
	
	//TLS_HANDSHAKE_SERVER_HELLO
	private byte[] serverRandom = new byte[32];
	private int cipherSuite;
	private int compressionMethod;
	private int compressionCandidates;

	private int sessionIDLen;
	private byte[] sessionID = new byte[256];
	private int ticketLen;	//-1 means no session ticket extension present in CLIENT/SERVER HELLO
	private byte[] ticket = new byte[65536];
	
	//TLS_HANDSHAKE_CLIENT_KEY_EXCHANGE
	private int encPreMasterLen;
	private byte[] encPreMaster = new byte[MAX_ENC_PREMASTER_LEN];	

	//TLS_HANDSHAKE_NEXT_PROTOCOL
	private int nextProtocol;
		
	void clean() {
		type = -1;
		cipherSuite = -1;
		compressionMethod = -1;
		compressionCandidates = -1;
		encPreMasterLen = -1;
		sessionIDLen = -1;
		nextProtocol = -1;

		for(int i=0; i<32; i++) {
			clientRandom[i] = 0;
		}
		for(int i=0; i<32; i++) {
			serverRandom[i] = 0;
		}
		for(int i=0; i<encPreMaster.length; i++) {
			encPreMaster[i] = 0;
		}
		for(int i=0; i<256; i++) {
			sessionID[i] = 0;
		}
		
		ticketLen = -1;
	}
	
	public int getType() {
		return type;
	}
	
	public int getNextProtocol() {
		return nextProtocol;
	}
	
	public int getCompressionMethod() {
		return compressionMethod;
	}
	
	public int getCipherSuite() {
		return cipherSuite;
	}
	
	public byte[] getServerRandom() {
		return serverRandom;
	}
	
	public byte[] getClientRandom() {
		return clientRandom;
	}

	public int getSessionIDLen() {
		return sessionIDLen;
	}
	
	public byte[] getSessionID() {
		return sessionID;
	}
	
	public int getTicketLen() {
		return ticketLen;
	}

	public byte[] getTicket() {
		return ticket;
	}
	
	private int read24bitInteger(byte pData[], int i) {
		byte[] tmp = new byte[4];
		tmp[3] = pData[i+2];
		tmp[2] = pData[i+1];
		tmp[1] = pData[i];
		tmp[0] = 0;
		int h= ByteBuffer.wrap(tmp).getInt();
		return h;
	}
	
	private int checkTLSVersion(byte[] pData, int i) {
		if ((pData[i] != 0x03) || ((pData[i + 1]) != 0x01)) {
			return 0;
		} else {
			return 1;
		}
	}
	
	public int read(byte[] pDataEx, int len, Integer[] hsSize) {
		clean();

		ByteBuffer pData = ByteBuffer.wrap(pDataEx);
		if (len < 4) {
			return 0;
		}
		type = pData.get(0);
		int payloadSize = read24bitInteger(pData.array(), 1);
		if (payloadSize + 4 > len) {
			return 0;
		}

		hsSize[0] = payloadSize + 4;		
		len = hsSize[0];	
		
		//len is now the whole payload. e.g., for CLIENT_HELLO and SEERVER_HELLO, It includes:
		//Handshake Type (1B)		<------ pData points before this
		//Payload len (3B)
		//Version (2B)
		//Random (32B)
		//...

		switch (type) {
			case TLS_HANDSHAKE_CLIENT_HELLO:
				{
					if (len < 4 + 2 + 32 + 1) {
						return 0;	//4 is the "base"
					}
					if (checkTLSVersion(pData.array(), 4) == 0) {
						return 0;
					}
					
					for(int j=0; j<6; j++) {
						pData.get();
					}					
					pData.get(clientRandom, 0, 32);	//memcpy(clientRandom, pData + 4 + 2, 32);
					pData.position(0);
					
					sessionIDLen = pData.get(38); //(int)(*(pData + 4 + 2 + 32));
					if (sessionIDLen > 0) {
						if (len < 4 + 2 + 32 + 1 + sessionIDLen) {
							return 0;
						}
						
						for(int j=0; j<39; j++) {
							pData.get();
						}
						pData.get(sessionID, 0, sessionIDLen); //memcpy(sessionID, pData + 4 + 2 + 32 + 1, sessionIDLen);
						pData.position(0);
					}

					if (len < 41 + sessionIDLen) {
						return 0;					
					}
					long cipherSuitesLen =  pData.getShort(39 + sessionIDLen); //Reverse(*(WORD *)(pData + 39 + sessionIDLen));

					if (len < 42 + sessionIDLen + cipherSuitesLen) {
						return 0;
					}
					byte compressMethodLen = pData.get(41 + sessionIDLen + (int)cipherSuitesLen); //*(pData + 41 + sessionIDLen + cipherSuitesLen);				
					this.compressionCandidates = compressMethodLen;

					int vLen = sessionIDLen + (int)cipherSuitesLen + compressMethodLen;
					if (len < 42 + vLen) {
						return 0;
					}

					if (len == 42 + vLen) {
						//no extension
						readExtensions(null, 0);			
					} else {
						if (len < 44 + vLen) {
							return 0;
						}
						long extensionLen = pData.getShort(42 + vLen); //Reverse(*(WORD *)(pData + 42 + vLen));
						if (len != 44 + vLen + extensionLen) {
							return 0;
						}
						
						int offset = 44 + vLen; //pData + 44 + vLen;
						for(int j=0; j<offset; j++) {
							pData.get();
						}
						byte[] tempData = new byte[len - offset];
						pData.get(tempData, 0, len - offset);
						pData.position(0);
						if (readExtensions(tempData, (int)extensionLen) == 0) {
							return 0;
						}
					}
					break;
				}

			case TLS_HANDSHAKE_SERVER_HELLO:
				{
					if (len < 4 + 2 + 32 + 3 + 1) {
						return 0; //4 is the "base"
					}

					if (checkTLSVersion(pData.array(), 4) == 0) {
						return 0;
					}
					for(int j=0; j<6; j++) {
						pData.get();
					}					
					pData.get(serverRandom, 0, 32); //memcpy(serverRandom, pData + 4 + 2, 32);
					pData.position(0);

					sessionIDLen = pData.get(38); //(int)(*(pData + 4 + 2 + 32));
					if (sessionIDLen > 0) {
						if (len < 4 + 2 + 32 + 3 + 1 + sessionIDLen) {
							return 0;
						}
						
						for(int j=0; j<39; j++) {
							pData.get();
						}
						pData.get(sessionID, 0, sessionIDLen); //memcpy(sessionID, pData + 4 + 2 + 32 + 1, sessionIDLen);
						pData.position(0);
					}

					cipherSuite = pData.getShort(4 + 2 + 32 + 1 + sessionIDLen); //Reverse(*((WORD *)(pData + 4 + 2 + 32 + 1 + sessionIDLen)));
					compressionMethod = pData.get(39 + sessionIDLen + 2); //*(pData + 4 + 2 + 32 + 1 + sessionIDLen + 2);

					if (len == 42 + sessionIDLen) {
						//no extension
						readExtensions(null, 0);
					} else {
						if (len < 44 + sessionIDLen) {
							return 0;
						}
						long extensionLen = pData.getShort(42 + sessionIDLen); //Reverse(*(WORD *)(pData + 42 + sessionIDLen));
						if (len != 44 + sessionIDLen + extensionLen) {
							return 0;
						}
						
						int offset = 44 + sessionIDLen; //pData + 44 + vLen;
						for(int j=0; j<offset; j++) {
							pData.get();
						}
						byte[] tempData = new byte[len - offset];
						pData.get(tempData, 0, len - offset);
						pData.position(0);
						if (readExtensions(tempData, (int)extensionLen) == 0) {
							return 0;
						}
					}
					break;
				}

			case TLS_HANDSHAKE_CERTIFICATE:
				{
					if (len < 4 + 3) {
						return 0;
					}
					int certLen = read24bitInteger(pData.array(), 4);
					if (certLen + 3 != payloadSize) {
						return 0;
					}
				}
				break;

			case TLS_HANDSHAKE_SERVER_KEY_EXCHANGE:
			case TLS_HANDSHAKE_CERTIFICATE_REQUEST:
			case TLS_HANDSHAKE_CERTIFICATE_VERIFY:
				//TODO: not support now
				LOGGER.log(Level.FINE, "30017 - TLS_HANDSHAKE_SERVER_KEY_EXCHANGE/TLS_HANDSHAKE_CERTIFICATE_REQUEST/TLS_HANDSHAKE_CERTIFICATE_VERIFY not support now");
				break;

			case TLS_HANDSHAKE_SERVER_HELLO_DONE:
				if (payloadSize != 0) {
					return 0;
				}
				break;

			case TLS_HANDSHAKE_CLIENT_KEY_EXCHANGE:			
				{
					if (len < 4 + 2) {
						return 0;
					}
					encPreMasterLen = pData.getShort(4); //Reverse(*((WORD *)(pData + 4)));
					if (len < 4 + 2 + encPreMasterLen) {
						return 0;
					}
					
					for(int j=0; j<6; j++) {
						pData.get();
					}
					encPreMaster = new byte[encPreMasterLen];
					pData.get(encPreMaster, 0, encPreMasterLen); //memcpy(encPreMaster, pData + 4 + 2, encPreMasterLen);
				}
				break;

			case TLS_HANDSHAKE_FINISHED:
				if (payloadSize != 12) {
					return 0;
				}
				break;

			case TLS_HANDSHAKE_NEW_SESSION_TICKET:
				{
					if (len < 4 + 4 + 2) {
						return 0;
					}
					ticketLen = pData.getShort(4 + 4); //(int)(Reverse(*((WORD *)(pData + 4 + 4))));

					if (ticketLen == 0) {
						return 0;
					}
					if (len != 4 + 4 + 2 + ticketLen) {
						return 0;
					}

					for(int j=0; j<10; j++) {
						pData.get();
					}
					ticket = new byte[ticketLen];
					pData.get(ticket, 0, ticketLen); //memcpy(ticket, pData + 4 + 4 + 2, ticketLen);
					break;
				}

			case TLS_HANDSHAKE_NEXT_PROTOCOL:
				{
					if (len < 4 + 1) {
						return 0;
					}
					byte npLen = pData.get(4); //*(pData + 4);

					if (npLen == 0) {
						return 0;
					}
					if (len < 4 + 1 + npLen) {
						return 0;
					}

					byte[] np = new byte[256];
					for(int j=0; j<5; j++) {
						pData.get();
					}
					pData.get(np, 0, npLen); //memcpy(np, pData + 4 + 1, npLen);
					np[npLen] = 0;
					String protocol = new String(np);
					protocol = protocol.trim();

					if (protocol.compareToIgnoreCase("spdy/3") == 0) {
						nextProtocol = APP_PROTOCOL_SPDY_3;
					} else if (protocol.compareToIgnoreCase("spdy/2") == 0) {
						nextProtocol = APP_PROTOCOL_SPDY_2;
					} else if (protocol.compareToIgnoreCase("http/1.1") == 0) {
						nextProtocol = APP_PROTOCOL_HTTP_1_1;
					} else {
						LOGGER.fine(Util.RB.getString("tls.error.invalidprotocol"));
						return -1;
					}
				}
				break;

			default:
				LOGGER.fine(Util.RB.getString("tls.error.invalidHSType"));
				return -1;
		}

		return 1;
	}
	
	int readExtensions(byte[] pDataEx, int extLen) {
		//default settings of no extension
		this.ticketLen = -1;
		if (extLen == 0) {
			return 1;
		}
		
		ByteBuffer pData = ByteBuffer.wrap(pDataEx);

		int nRead = 0;
		while (true) {
			if (nRead + 4 > extLen) {
				return 0;
			}
			long type = pData.getShort(nRead); //Reverse(*(WORD *)(pData + nRead));
			long len  = pData.getShort(nRead + 2); //Reverse(*(WORD *)(pData + nRead + 2));
			
			nRead += 4;
			if (nRead + len > extLen) {
				return 0;
			}

			switch ((int)type) {
				case TLS_EXTENSION_SESSION_TICKET:
					{
						this.ticketLen = (int)len;
						
						for(int j=0; j<nRead; j++) {
							pData.get();
						}
						this.ticket = new byte[(int)len];
						pData.get(this.ticket, 0, (int)len); //memcpy(this.ticket, pData + nRead, len);
						break;
					}

				default:				
					break;
			}

			nRead += len;
			if (nRead == extLen) {
				break;
			}
		}
		
		return 1;
	}	
}
