//
//  based on SerialExample.m
//

#import "Writer.h"


@implementation Writer

// executes after everything in the xib/nib is initiallized
- (void)initalize {
	// we don't have a serial port open yet
	serialFileDescriptor = -1;
	NSLog(@"\nWHYYY\n");
}

- (void) flush {
    struct timespec interval = {0,20000000}, remainder;
    if(serialFileDescriptor!=-1) {
        ioctl(serialFileDescriptor, TIOCDRAIN);
        ioctl(serialFileDescriptor, TIOCFLUSH, FWRITE);
    }
}

- (void) dealloc {
    struct timespec interval = {0,100000000}, remainder;
	if(serialFileDescriptor!=-1) {
		ioctl(serialFileDescriptor, TIOCSDTR);
		nanosleep(&interval, &remainder); // wait 0.1 seconds
		ioctl(serialFileDescriptor, TIOCCDTR);
		close(serialFileDescriptor);
	}
    [super dealloc];
}

// open the serial port
//   - nil is returned on success
//   - an error message is returned otherwise
- (NSString *) openSerialPort: (NSString *)serialPortFile baud: (speed_t)baudRate {
    NSLog(@"\nHALLO\n");
	int success;

	// close the port if it is already open
	if (serialFileDescriptor != -1) {
		close(serialFileDescriptor);
		serialFileDescriptor = -1;

		// re-opening the same port REALLY fast will fail spectacularly... better to sleep a sec
		sleep(0.5);
	}

	// c-string path to serial-port file
	const char *bsdPath = [serialPortFile cStringUsingEncoding:NSUTF8StringEncoding];

	// Hold the original termios attributes we are setting
	struct termios options;

	// receive latency ( in microseconds )
	unsigned long mics = 3;

	// error message string
	NSMutableString *errorMessage = nil;

	// open the port
	//     O_NONBLOCK causes the port to open without any delay (we'll block with another call)
	serialFileDescriptor = open(bsdPath, O_RDWR | O_NOCTTY | O_NONBLOCK );

	if (serialFileDescriptor == -1) {
		// check if the port opened correctly
		[errorMessage appendString: @"Error: couldn't open serial port"];
	} else {
		// TIOCEXCL causes blocking of non-root processes on this serial-port
		success = ioctl(serialFileDescriptor, TIOCEXCL);
		if ( success == -1) {
			[errorMessage appendString:  @"Error: couldn't obtain lock on serial port"];
		} else {
			success = fcntl(serialFileDescriptor, F_SETFL, 0);
			if ( success == -1) {
				// clear the O_NONBLOCK flag; all calls from here on out are blocking for non-root processes
				[errorMessage appendString:  @"Error: couldn't obtain lock on serial port"];
			} else {
				// Get the current options and save them so we can restore the default settings later.
				success = tcgetattr(serialFileDescriptor, &gOriginalTTYAttrs);
				if ( success == -1) {
					[errorMessage appendString: @"Error: couldn't get serial attributes"];
				} else {
					// copy the old termios settings into the current
					//   you want to do this so that you get all the control characters assigned
					options = gOriginalTTYAttrs;

					/*
					 cfmakeraw(&options) is equivilent to:
					 options->c_iflag &= ~(IGNBRK | BRKINT | PARMRK | ISTRIP | INLCR | IGNCR | ICRNL | IXON);
					 options->c_oflag &= ~OPOST;
					 options->c_lflag &= ~(ECHO | ECHONL | ICANON | ISIG | IEXTEN);
					 options->c_cflag &= ~(CSIZE | PARENB);
					 options->c_cflag |= CS8;
					 */
					cfmakeraw(&options);

					// set tty attributes (raw-mode in this case)
					success = tcsetattr(serialFileDescriptor, TCSANOW, &options);
					if ( success == -1) {
						[errorMessage appendString: @"Error: coudln't set serial attributes"];
					} else {
						// Set baud rate (any arbitrary baud rate can be set this way)
						success = ioctl(serialFileDescriptor, IOSSIOSPEED, &baudRate);
						if ( success == -1) {
							[errorMessage appendString: @"Error: Baud Rate out of bounds"];
						} else {
							// Set the receive latency (a.k.a. don't wait to buffer data)
							success = ioctl(serialFileDescriptor, IOSSDATALAT, &mics);
							if ( success == -1) {
								[errorMessage appendString: @"Error: coudln't set serial latency"];
							}
						}
					}
				}
			}
		}
	}

	// make sure the port is closed if a problem happens
	if ((serialFileDescriptor != -1) && (errorMessage != nil)) {
		close(serialFileDescriptor);
		serialFileDescriptor = -1;
	}

	return errorMessage;
}

// send a string to the serial po
- (void) writeColorArray: (uint8_t []) bytes: (uint16) size {
	if(serialFileDescriptor!=-1) {
		write(serialFileDescriptor, bytes, size);
	} else {
		// make sure the user knows they should select a serial port
		NSLog(@"\n ERROR:  Select a Serial Port from the pull-down menu\n");
	}
}
- (void) writeByte: (uint8_t*) byte {
	if(serialFileDescriptor!=-1) {
		write(serialFileDescriptor, byte, 1);
	} else {
		// make sure the user knows they should select a serial port
		NSLog(@"\n ERROR:  Select a Serial Port from the pull-down menu\n");
	}
}


@end
