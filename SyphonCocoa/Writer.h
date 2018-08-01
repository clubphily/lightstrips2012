//
//  SerialExample.h
//  Arduino Serial Example
//
//  Created by Gabe Ghearing on 6/30/09.
//

#import <Cocoa/Cocoa.h>

// import IOKit headers
#include <IOKit/IOKitLib.h>
#include <IOKit/serial/IOSerialKeys.h>
#include <IOKit/IOBSD.h>
#include <IOKit/serial/ioss.h>
#include <sys/ioctl.h>
#include <stdlib.h>


@interface Writer : NSObject {
	int serialFileDescriptor; // file handle to the serial port
	struct termios gOriginalTTYAttrs; // Hold the original termios attributes so we can reset them on quit ( best practice )
}
- (NSString *) openSerialPort: (NSString *)serialPortFile baud: (speed_t)baudRate;
-(void) writeColorArray: (uint8_t []) bytes: (uint16) size; 
-(void) initalize; 
-(void) flush;

@end
