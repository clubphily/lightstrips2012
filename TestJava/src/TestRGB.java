import javax.usb.*;
import java.util.List;
import java.lang.Math;
import java.awt.Color;

public class TestRGB
{
    private static void dump(UsbDevice device)
    {
        UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
        System.out.format("%04x:%04x%n", desc.idVendor() & 0xffff, desc.idProduct() & 0xffff);
        System.out.println("VENDOR: "+ desc.idVendor() +" PRODUCT: " + desc.idProduct());
        /*if(desc.idVendor() == 1027 && desc.idProduct() == 24577) {
        	testIO(device);
        	return;	
        }*/
        if (device.isUsbHub())
        {
            UsbHub hub = (UsbHub) device;
            for (UsbDevice child : (List<UsbDevice>) hub.getAttachedUsbDevices())
            {
                dump(child);
            }
        } else {
       		testIO(device); 
        }
    }
	
	public static void testIO(UsbDevice device) {
		try {
		   // Access to the active configuration of the USB device, obtain 
		   // all the interfaces available in that configuration.
		   UsbConfiguration config = device.getActiveUsbConfiguration();
		   List totalInterfaces = config.getUsbInterfaces();
				
		   // Traverse through all the interfaces, and access the endpoints 
		   // available to that interface for I/O.
		   for (int i=0; i<totalInterfaces.size(); i++) {
			  UsbInterface interf = (UsbInterface) totalInterfaces.get(i);
			  interf.claim();
			  List totalEndpoints = interf.getUsbEndpoints();
			  for (int j=0; j<totalEndpoints.size(); j++) {
				 // Access the particular endpoint, determine the direction
				 // of its data flow, and type of data transfer, and open the 
				 // data pipe for I/O.
				 UsbEndpoint ep = (UsbEndpoint) totalEndpoints.get(i);
				 int direction = ep.getDirection();
				 int type = ep.getType();
				 UsbPipe pipe = ep.getUsbPipe();
				 UsbIrp irp = pipe.createUsbIrp();
				 pipe.open();
				 for(int l = 0; l < 50; l++) {
					 int count = 0;
					 float red = (float)Math.random();
					 float green = (float)Math.random();
					 float blue = (float)Math.random();
					 byte[] bytes = new byte[72];
					 Color color = new Color(red, green, blue);
					 while (count < 72) {
						bytes[count++] = (byte)color.getRed();
						bytes[count++] = (byte)color.getBlue();
						bytes[count++] = (byte)color.getGreen();
					 }
					 irp.setData(bytes);
					 pipe.syncSubmit(irp); 
					 irp.waitUntilComplete();
					 try {
						Thread.sleep(800);  
					 } catch (InterruptedException ie) {
						System.out.println(ie.getMessage());
					 }
				 }
				 // Perform I/O through the USB pipe here.
				 pipe.close();
			  }
			  interf.release();
		   }
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
	}

    public static void main(String[] args) throws UsbException
    {
        UsbServices services = UsbHostManager.getUsbServices();
        UsbHub rootHub = services.getRootUsbHub();
        dump(rootHub);
        //testIO(rootHub);
    }
}