/*
 * @(#)SimpleWrite.java	1.12 98/06/25 SMI
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license
 * to use, modify and redistribute this software in source and binary
 * code form, provided that i) this copyright notice and license appear
 * on all copies of the software; and ii) Licensee does not utilize the
 * software in a manner which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THE
 * SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS
 * BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 * HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING
 * OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control
 * of aircraft, air traffic, aircraft navigation or aircraft
 * communications; or in the design, construction, operation or
 * maintenance of any nuclear facility. Licensee represents and
 * warrants that it will not use or redistribute the Software for such
 * purposes.
 */
import java.io.*;
import java.util.*;
import gnu.io.*;
import java.awt.Color;

/**
 * Class declaration
 *
 *
 * @author
 * @version 1.10, 08/04/00
 */
public class SimpleWrite {
static Enumeration<CommPortIdentifier>  portList;
static CommPortIdentifier portId;
static SerialPort serialPort;
static OutputStream outputStream;
static boolean outputBufferEmptyFlag = false;
/**
 * Method declaration
 *
 *
 * @param args
 *
 * @see
 */
public static void main(String[] args) {
        boolean portFound = false;
        String defaultPort = "/dev/tty.usbserial-A8008Imq";
        if (args.length > 0) {
                defaultPort = args[0];
        }

        portList = CommPortIdentifier.getPortIdentifiers();


        while (portList.hasMoreElements()) {
                portId = portList.nextElement();

                if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                        if (portId.getName().equals(defaultPort)) {
                                System.out.println("Found port " + defaultPort);

                                portFound = true;

                                try {
                                        serialPort = (SerialPort) portId.open("serial madness", 100000);
                                } catch (PortInUseException e) {
                                        System.out.println("Port in use.");

                                        continue;
                                }

                                try {
                                        outputStream = serialPort.getOutputStream();
                                } catch (IOException e) {}

                                try {
                                        serialPort.setSerialPortParams(28800,
                                                                       SerialPort.DATABITS_8,
                                                                       SerialPort.STOPBITS_1,
                                                                       SerialPort.PARITY_NONE);
                                } catch (UnsupportedCommOperationException e) {}


                                try {
                                        serialPort.notifyOnOutputEmpty(true);
                                } catch (Exception e) {
                                        System.out.println("Error setting event notification");
                                        System.out.println(e.toString());
                                        System.exit(-1);
                                }

                                for(int l = 0; l < 200; l++) {
                                        int count = 0;
                                        float red = (float)Math.random();
                                        float green = (float)Math.random();
                                        float blue = (float)Math.random();
                                        //byte[] bytes = new byte[72];
                                        Color color = new Color(red, green, blue);
                                        try {
                                                while (count < 24) {
                                                        outputStream.write(color.getRed());
                                                        outputStream.flush();
                                                        //bytes[count++] = (byte)color.getRed();
                                                        outputStream.write(color.getBlue());
                                                        outputStream.flush();
                                                        //bytes[count++] = (byte)color.getBlue();
                                                        outputStream.write(color.getGreen());
                                                        outputStream.flush();
                                                        //bytes[count++] = (byte)color.getGreen();
                                                        count++;
                                                }
                                                outputStream.write('\0');
                                                //outputStream.write(bytes);
                                                outputStream.flush();
                                        } catch (IOException e) {}
                                        try {
                                                Thread.sleep(34);
                                        } catch (InterruptedException ie) {
                                                System.out.println(ie.getMessage());
                                        }
                                }
                                serialPort.close();
                                System.exit(1);
                        }
                }
        }

        if (!portFound) {
                System.out.println("port " + defaultPort + " not found.");
        }
}


}
