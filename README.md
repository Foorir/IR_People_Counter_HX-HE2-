# Infrared People Counter HE2 series product communication protocol

## 1. Brief description of the protocol

- The protocol is currently only for infrared people counter HE-2 series products. Other products will support this protocol in the future. Please pay attention to this in time for subsequent update records.
- When designing this protocol, low power consumption, low computing power and other devices were also taken into consideration, so the protocol is directly based on TCP and uses hexadecimal + ANSII code for transmission.
- The protocol takes into account special situations such as sticky packets and escaping.

##2. Detailed explanation of the protocol

### 2.1 Protocol structure

The protocol consists of 8 parts, fixed header, fixed trailer, device ID, instructions, parameters, data length, data, CRC, frame trailer

The fixed header and fixed trailer are both fixed. The device ID is currently a reserved bit. The default is a 32-bit integer currently fixed at 0xFF 0xFF 0xFF 0xFF. The CRC uses ModbusCRC16, and the data part uses commas for field separation. In order to prevent fixed header and fixed trailer data from appearing in the data part and CRC part, the escape bit: 0x7C is introduced.

| Fixed frame header | Fixed device ID | Command | Parameter | Data length |      Data       | ModbusCRC16 low eight bits | ModbusCRC16 high eight bits | Fixed frame tail |
| :----------------: | :-------------: | :-----: | :-------: | :---------: | :-------------: | :------------------------: | :-------------------------: | :--------------: |
|       1 byte       |   four bytes    | 1 byte  |  1 byte   | double byte | variable length |           1 byte           |           1 byte            |      1 byte      |

- Fixed frame header: 0x7E
- Fixed device ID: 0xFF 0xFF 0xFF 0xFF
- Command: See the command description chapter for details
- Parameters: See the instruction description chapter for details
- Data length: The length of the data part is int16 type, transmitted in double words.
- Data: ANSII string
- ModbusCRC16 lower eight bits: ModbusCRC16 lower eight bits
- ModbusCRC16 high eight bits: ModbusCRC16 high eight bits
- Fixed frame end: 0x7D
- Escape character: 0x7C

#### 2.1.1 Fixed header and trailer

The header is 7E and ends at 7D.

7E ............ 7D Such data is a complete packet of data

#### 2.1.2 Instructions and parameters

Instructions and parameters are used to distinguish different data types, including **heartbeat data**, **data reporting**, and **historical data reporting**

#### 2.1.3 Data length and data

The data length is used to calculate the length of the data part and to facilitate developers to intercept the data part through the data length. The data in the data part is composed of **field bits** and **comma delimiters**. It will be explained in detail in the field definition chapter.

Example:

Client initiated data: 1902201010001,20220101090004,10,20,1,V4.3,E20 Note: The length is 45

Actual data initiated by the client: 00 2D 31 39 30 32 32 30 31 30 31 30 30 30 31 2C 32 30 32 32 30 31 30 31 30 39 30 30 30 34 2C 31 30 2C 32 30 2C 31 2C 56 34 2E 33 2C 45 32 30

Note: The first two bytes are the data length: 0x00 0x2D. The high position is in front and the low position is in the back. What follows is the ASCII code corresponding to the string

#### 2.1.4 ModbusCRC16 checksum

The ModbusCRC16 check range does not include fixed frame headers and fixed frame tails. The calculation range is: fixed device ID, instructions, parameters, data length, and data. These locations must participate in ModbusCRC16 calculations. The purpose of ModbusCRC16 checksum calculation is to ensure the accuracy of data. The C language implementation code is as follows:

```c
#include "h_crc.h"

ushort const TAB_CRC16_ITU[256] =
    {
        0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
        0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
        0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
        0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
        0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
        0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
        0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
        0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
        0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
        0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
        0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
        0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
        0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
        0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
        0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
        0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
        0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
        0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
        0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
        0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
        0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
        0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
        0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
        0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
        0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
        0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
        0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
        0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
        0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
        0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
        0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
        0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040};

Bool Is_CRC16_Good(uchar *pData, uint nLength)
{
    ushort Fcs = 0xFFFF;

    while (nLength > 0)
    {
        Fcs = (Fcs >> 8) ^ TAB_CRC16_ITU[(Fcs ^ *pData) & 0xFF];
        nLength--;
        pData++;
    }

    if (Fcs == 0)
        return True;
    else
        return False;
}

ushort CRC16_ITU_Generate(uchar *pData, int nLength)
{
    ushort Fcs;

    Fcs = 0xFFFF;
    while (nLength > 0)
    {
        Fcs = (Fcs >> 8) ^ TAB_CRC16_ITU[(Fcs ^ *pData) & 0xFF];
        nLength--;
        pData++;
    }

    return Fcs;
}

```

When verifying data verification, you need to perform verification calculation again on the fixed device ID, command, parameter, data length, data and ModbusCRC16 two bytes. If the result is equal to 0, then the verification is successful, otherwise it fails.

#### 2.1.5 Escape

The purpose of the escape character is to prevent the sender from appearing in non-packet header and tail positions when sending a message, which makes it difficult for the receiving method to parse the data. The implementation method is as follows:

0x7D: Change to 0x7C 0x5D and send

0x7E: Change to 0x7C 0x5D and send

It is worth noting here what to do if 7C itself appears. same:

70xC: Change to 0x7C 0x5C and send

The specific implementation code is as follows:

sender:

```c
case 0x7D: 
	rsbus_frame_data[rsbus_frame_data_index++] = 0x7C;
	rsbus_frame_data[rsbus_frame_data_index++] = rsbus_send.send_buf[i] - 0x20;
	break;
case 0x7E: 
	rsbus_frame_data[rsbus_frame_data_index++] = 0x7C;
	rsbus_frame_data[rsbus_frame_data_index++] = rsbus_send.send_buf[i] - 0x20;
	break;
case 0x7C: 
	rsbus_frame_data[rsbus_frame_data_index++] = 0x7C;
	rsbus_frame_data[rsbus_frame_data_index++] = rsbus_send.send_buf[i] - 0x20;
	break;
```

When escaping, the sender first sends 0x7C, and then subtracts 0x20.

receiver:

```c#
if (recv == 0x7C) 
{
	if (rec_head_flag == true) 
	{
		rec_specialData_flag = true; 
        return;
	}
}

if (rec_specialData_flag == true) 
{
    recbuf[recbuf_index++] = (byte)(recv ^ 0x20);
    rec_specialData_flag = false;

    return;
}
```

When the receiver receives 7C, it needs to perform restoration processing. The specific method is to perform the ^ 0x20 operation on the data after 0x7C to restore it.



### 2.2 Protocol instruction description

When transmitting data bits, they are sent in ANSII hexadecimal format. For the convenience of demonstration, strings are used here.

#### 2.2.1 Heartbeat command 0xD1

1. When the device is powered on, the time is synchronized and a heartbeat is automatically uploaded (the platform can display that the device is online at this time)

2. If the device fails to synchronize the time when it is powered on, a heartbeat will also be sent when the user manually binds the code for the device to synchronize the time.

3. The device uploads a heartbeat every half hour during business hours, which is used by the platform to determine the online and offline status. The device does not report heartbeats and data outside business hours

- Sender heartbeat data command format

Command: 0xD1

Parameters: Default 0x02

Data part: [SN], [timestamp], [receiving power], [sending power], [coding status], [version], [product model]

,：Comma, delimiter, used to separate data

SN: 13-digit serial number, string, may not have a fixed length

Timestamp: current 14-digit timestamp, example: 20220101090004

Receiver power: Percentage of receiver power, updated every 4 hours

Transmitting power: percentage of transmitting end power, updated once every hour

Coding status: The transmitter is working normally. If the code is coupled normally, this field is 1. If the code is not coupled successfully, this field is 0.

Version: device software version number, not fixed length, such as: V4.1 V1.0.1

Product model: Equipment model, not fixed length, such as: E20 E2

- The receiver's heartbeat data replies to the sender's instructions

Command: 0xD1

Parameters: Default 0x02

Data part: [Status code], [Time stamp], [Business start time], [Business end time], [Recording period], [Detection speed], [Statistical direction], [Upgrade flag], [Upgrade link], [Reserved]

,: comma, delimiter, used to split data

Status code: 0 success 1 failure

Timestamp: The device synchronizes the time in the receiving direction. After the device receives it, it will be consistent with the receiver's time and start counting again from the receiver's time. Example: 20220101090004

Business start time: the start time of the equipment every day, the length must be fixed, and the time is 24 hours. The unit is only hours and minutes, for example: 0800, 0830

End of business time: The end of working time of the equipment every day, the length must be fixed, and the time is 24 hours. The unit is only hours and minutes, for example: 2230, 2359

Recording cycle: the upload cycle, currently measured in minutes. If it is 0, it is real-time mode. In real-time mode, USB needs to be plugged in. The maximum setting cannot exceed one day. By default, it is recommended to report every 5 minutes.

Detection speed: The new version of the device has canceled the detection speed, the default is 0

Statistical direction: the statistical calculation direction of the device, the default is 0 bidirectional, 1 incoming only, 2 outgoing only

Upgrade flag: 0 means no upgrade, 1 means upgrade device firmware, 2 means upgrade WIFI firmware. If the device needs to be upgraded,

Upgrade link: If there is an upgrade, the upgrade link is the actual firmware download link, such as: http://dddddd.com/file.bin Note: The download link does not support https

Reserved: reserved items, write 0

#### 2.2.2 Statistics command 0xD2

1. Device data reporting is based on the "recording period" returned by the heartbeat corresponding to each reporting interval of device data.

2. If there is data during business hours, it will be reported according to the reporting interval. If there is no data, the device will return heartbeat data every half an hour. The platform can determine whether it is online based on the heartbeat.

- Sender's heart statistics command format

Command: 0xD2

Parameters: Default 0x02

Data part: [SN], [timestamp], [number of people entering], [number of people leaving], [receiving power], [sending power], [coding status], [version], [product model]

,：Comma, delimiter, used to separate data

SN: 13-digit serial number, string, may not have a fixed length

Timestamp: current 14-digit timestamp, example: 20220101090004

Number of people entering: the total number of people entering during the recording period

Number of people leaving: the total number of people leaving during the recording period

Receiver power: Percentage of receiver power, updated every 4 hours

Transmitting power: percentage of transmitting end power, updated once every hour

Coding status: The transmitter is working normally. If the code is coupled normally, this field is 1. If the code is not coupled successfully, this field is 0.

Version: device software version number, not fixed length, such as: V4.1 V1.0.1

Product model: Equipment model, not fixed length, such as: E20 E2

- The receiver's heartbeat data replies to the sender's instructions

Command: 0xD2

Parameters: Default 0x02

Data part: [Status code], [Time stamp], [Upgrade flag], [Upgrade link]

Status code: 0 success 1 failure After the receiver receives the failure, it will treat this piece of data as failure data and wait until the next reporting cycle to report it.

Timestamp: current 14-digit timestamp, example: 20220101090004

Upgrade flag: 0 means no upgrade, 1 means upgrade device firmware, 2 means upgrade WIFI firmware. If the device needs to be upgraded,

Upgrade link: If there is an upgrade, the upgrade link is the actual firmware download link, such as: http://dddddd.com/file.bin Note: The download link does not support https

#### 2.2.3 Failed statistics command 0xD3

Command: 0xD3

Parameters: Default 0x02

Data part: same as 0xD2 command

Note: The format of sending and receiving data is the same as 0xD2, and the command changes to 0xD3

## 3. Examples of sending and receiving interactions

**Device power on**

&dArr;

**Device initiates heartbeat**

HEX: 7E FF FF FF FF D1 02 00 33 31 39 35 32 33 30 37 32 38 30 32 36 39 2C 32 30 32 33 30 31 30 31 30 39 30 30 30 30 2C 34 39 2C 34 37 2C 30 2C 56 35 2E 33 38 2D 45 4E 2C 48 58 2D 48 36 7D

Data part: 1952307280269,20230101090000,49,47,0,V5.38-EN,HX-H6

Platform reply: 7E FF FF FF FF D1 D1 00 26 30 2C 32 30 32 34 30 31 32 34 31 38 34 31 30 31 2C 30 30 30 30 2C 32 33 35 39 2C 31 2C 30 2C 30 2C 30 2C 30 2C 30 4E B6 7D

Data part: 0,20240124184115,0000,2359,1,0,0,0,0,0

​		 	&dArr;

**Device Initiation Statistics**

HEX: 7E FF FF FF FF D2 02 00 37 31 39 35 32 33 30 37 32 38 30 32 36 39 2C 32 30 32 34 30 31 31 35 31 35 31 38 30 37 2C 30 2C 31 2C 34 39 2C 34 37 2C 31 2C 56 35 2E 33 38 2D 45 4E 2C 48 58 2D 48 36 00 7F 7D

Data part: 1952307280269,20240115151807,0,1,49,47,1,V5.38-EN,HX-H6

Platform reply: 7E FF FF FF FF D2 D2 00 18 30 2C 32 30 32 34 30 31 32 34 31 37 34 35 32 37 2C 30 2C 30 97 E5 7D

Data part: 0,20240124174551,0,0



​	
