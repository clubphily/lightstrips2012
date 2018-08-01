# Lightstrips 2012
A cooperation with [Giordano Canova](http://godan-visuals.net) to add an additional twist to his VJ shows.

## Idea
Add an [LED lightstrip](https://en.wikipedia.org/wiki/LED_strip_light) to the overall VJ set-up and integrate the controlling of the pixels on the lightstrip into the VJ software.

## Approach
VJ software uses the graphic card of the computer to calculate visuals and send them to the used display media. With an additional small tool, [Syphon Server](http://syphon.v002.info), it is possible to [grab the frames on the graphic card memory](SyphonCocoa/) and use them for other means.

The frame was then [massively scaled down and copied from a matrix into an array](SyphonCocoa/SimpleClientGLView.m). The array had the exact size of the amount of pixels on the lightstrip.

The array was then [sent over USB](SyphonCocoa/Writer.m) to an Arduino device with the attached lightstrip. The received array was slightly [transformed and then displayed](Arduino/binaryColorListener/binaryColorListener.ino) on the lightstrip.

For testing the lightstrip without the VJ software running, a [small Java program](TestJava) was written, which simulated the sending of the RGB array.
