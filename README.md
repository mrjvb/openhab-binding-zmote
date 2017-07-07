# ZMote Binding

This binding allows you to control your [ZMote](http://www.zmote.io) device and send 
IR codes to IR enabled gadgets.

## Supported Things

ZMote devices with firmware version 2.

## Discovery

Auto discovery is supported by sending a UDP multicast to all ZMote devices on the 
local network. If your device is not on your local network, you have to configure it 
manually as specified in the Thing configuration section.

## IR Configuration

The IR code configuration is based on a configuration file that you can download from
the ZMote webapp, as described [here](https://community.openhab.org/t/openhab-zmote-binding/14226/23).
Just configure your IR remotes within the webapp and download the configuration in JSON
format. The full path to the configuration file has to be specified in the Thing
configuration.

To reference a button, use the value provided for the "key" field in the JSON file. By
default, this field cannot be edited within the ZMote webapp. If you do not like the
key values auto-generated by the ZMote webapp, you can simply use any text editor to
change them.

## Thing Configuration

Currently only the IR transmitter is supported. 

When configuring your device manually, be aware that some options have to be provided 
as numbers, i.e. without quotation marks.

```
zmote:zmt2:devicename [ uuid="CI00a1b2c4", configFile="/path/to/config.json", overrideUrl="http://10.10.10.10", retry=1, timeout=5 ]
```

- **uuid** (required): The unique ID of your ZMote device. You can get this id by checking 
  the auto-discovered things in your inbox where the uuid will be used as device name.
- **configFile** (optional): The configuration file which contains the IR configuration 
  downloaded from the ZMote webapp. This should be the full path to the file, e.g. 
  "C:\OpenHAB\userdata\config\remote.json" on Windows or "/opt/openhab/userdata/config/remote.json" 
  on Unix systems. If no configuration file is provided, only the "sendcode" channel can be used.
- **overrideUrl** (optional): The URL of the ZMote device. This can be used to override
  auto-discovery in case the device is in a different network segment and cannot be auto-discovered
  with UDP multicasts. If auto-discovery works, this option should not be set.
- **retry** (optional): The number of retries in case the device is busy (Default: 
  1).
- **timeout** (optional): The time we wait in seconds until we give up connecting to 
  the device (Default: 5).


## Item Configuration

## Channels

### Channel: online

This channel allows you to check if the ZMote transmitter is currently online and
if the configuration file provided is valid. Auto discovery will try to find the device 
in one minute intervals.

**demo.things**
```
zmote:zmt2:sony    "Sony Remote"    [ uuid="CI00a1b2c4", configFile="/opt/openhab/userdata/config/sony.json" ]
zmote:zmt2:samsung "Samsung Remote" [ uuid="CI00a1b2c4", configFile="c:\OpenHAB\userdata\config\samsung.json" ]
```

**demo.items**
```xtend
Switch zmote_sony    "Sony Remote Online"    { channel="zmote:zmt2:sony:online" }
Switch zmote_samsung "Samsung Remote Online" { channel="zmote:zmt2:samsung:online" }
```

### Channel: sendkey

Allows you to send the IR code for the given button name, as referenced by the "key" 
field in your configuration file.

**demo.things**
```
zmote:zmt2:sony    "Sony Remote"    [ uuid="CI00a1b2c4", configFile="/opt/openhab/userdata/config/sony.json" ]
zmote:zmt2:samsung "Samsung Remote" [ uuid="CI00a1b2c4", configFile="c:\OpenHAB\userdata\config\samsung.json" ]
```

**demo.items**
```xtend
String zmote_sony_sendkey    "Send Sony Button"    { channel="zmote:zmt2:sony:sendkey" }
String zmote_samsung_sendkey "Send Samsung Button" { channel="zmote:zmt2:samsung:sendkey" }
```

**demo.sitemap**
```xtend
Switch    item=zmote_sony_sendkey    label="Sony Power"              mappings=[ "KEY_POWER"="Power" ]
Switch    item=zmote_sony_sendkey    label="Sony Play/Stop"          mappings=[ "KEY_PLAY"="Play", "KEY_STOP"="Stop" ]
Selection item=zmote_samsung_sendkey label="Samsung Input Selection" mappings=[ "KEY_HDMI1"="TV", "KEY_HDMI2"="DVD", "KEY_HDMI3"="X-Box", "KEY_HDMI4"="Playstation" ]
```

### Channel: sendcode

Allows you to send raw IR code. As you have to provide the IR codes directly, the configuration
file is optional. You can use any existing Thing configuration which has a remote configuration
or define a new Thing for raw IR codes.

**demo.things**
```
zmote:zmt2:ir      "Raw Remote"     [ uuid="CI00a1b2c4" ]
zmote:zmt2:samsung "Samsung Remote" [ uuid="CI00a1b2c4", configFile="c:\OpenHAB\userdata\config\samsung.json" ]
```

**demo.items**
```xtend
String zmote_ir_sendcode      "Send IR Code" { channel="zmote:zmt2:ir:sendcode" }
String zmote_samsung_sendcode "Send IR Code" { channel="zmote:zmt2:samsung:sendcode" }
```

**demo.sitemap**
```xtend
Switch item=zmote_ir_sendcode      label="Sony Power" mappings=[ "36000,2,1,32,32,64,32,32,64,32,3264"="On" ]
Switch item=zmote_samsung_sendcode label="Sony Power" mappings=[ "36000,2,1,32,32,64,32,32,64,32,3264"="On" ]
```

## Additional Examples

### Amazon Alexa

You need the Hue Emulation service configured for the following to work.

**demo.things**
```
zmote:zmt2:samsung "Samsung Remote" [ uuid="CI00a1b2c4", configFile="c:\OpenHAB\userdata\config\samsung.json" ]
```

**demo.items**
```xtend
String zmote_samsung_sendkey "Send Samsung Button" { channel="zmote:zmt2:samsung:sendkey" }
Switch zmote_samsung_power   "TV"                  [ "Switchable" ]
```

**demo.rules**
```xtend
rule "Samsung Power"
when
    Item zmote_samsung_power received command
then
    sendCommand(zmote_samsung_sendkey, "KEY_POWER")
end
```

**Amazon Alexa**
```
Alexa, discover devices
Alexa, turn on TV
```

## Debugging

Debuging can be done by connecting to the OpenHAB console via SSH. The default port is **8101**
and the default password is **habopen**.

```
$ ssh -p 8101 localhost
```

### Logging

To enable debug logging you have to raise the log level from INFO to DEBUG.

```
openhab> log:set DEBUG org.openhab.binding.zmote
openhab> log:tail
```

Press Ctrl-C to abort log monitoring and set the log level back to INFO once you are done.

### Starting and Stopping

You can stop and start the plugin manually to fore reinitialization.

```
openhab> stop org.openhab.binding.zmote
openhab> start org.openhab.binding.zmote
```

